package com.duosecurity.controller;

import com.duosecurity.Client;
import com.duosecurity.exception.DuoException;
import com.duosecurity.model.Token;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.simple.JSONArray;


public class LoginController extends HttpServlet {
    private String apiHost = "api-e2ad06e5.duosecurity.com";
    private String clientId = "DISCX7BICAX68EECXDPX";
    private String clientSecret = "2lWekB8c7VFFJKtPiBIZJkQu7ZaVKqSapUT9Yjgv";
    private Client duoClient;
    private Map<String, String> stateMap;
    JSONArray details_arr;

    public void init() {
        
        stateMap = new HashMap<>();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        Claims claims = (Claims) request.getAttribute("token");
        String username = (String) claims.get("CLIENT_ID") + claims.get("username");
        


        if (username != null) {
            stateMap = new HashMap<>();
            boolean duo_health = true;
            try {
                duoClient = new Client.Builder(clientId, clientSecret, apiHost, request.getParameter("redirect_uri_duo")).build();
                duoClient.healthCheck();
            } catch (DuoException exception) {
                System.out.println("Duo server UnAvailable ");
                response.setStatus(400);
                duo_health = false;

            }
            if (duo_health) {
                String state = duoClient.generateState();
                stateMap.put(state, username);
                try {
                    String authUrl = duoClient.createAuthUrl(username, state);
                    response.getWriter().write(authUrl);
                } catch (DuoException e) {
                    System.out.println(e);
                    response.setStatus(400);
                }
            }
        } else {
            System.out.println("username is null");
            response.setStatus(400);
        }
    }

    // After complete authentication duo call this method
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Claims claims = (Claims) request.getAttribute("token");
        Claims claims2=Jwts.parser().setSigningKey(getClientSecret((String) claims.get("CLIENT_ID"))).parseClaimsJws(request.getParameter("temp_token")).getBody();
        String state = request.getParameter("state");
        String duoCode = request.getParameter("duo_code");
    
        if (!stateMap.containsKey(state)) {
            System.out.println("Illegal access's...........");
            response.setStatus(400);
            
        } else {

            String username = stateMap.remove(state);
            try {
                Token token = duoClient.exchangeAuthorizationCodeFor2FAResult(duoCode, username);
                
                if (token != null && token.getAuth_result().getStatus().equalsIgnoreCase("ALLOW")) {
                    
                    claims2.put("duo_auth", token.getAuth_result().getResult());
                    String jwt=Jwts.builder().setClaims (claims2).signWith(SignatureAlgorithm.HS256,getClientSecret((String) claims.get("CLIENT_ID"))).compact();
                    //update_token((String) claims.get("CLIENT_ID"), jwt);
                    response.getWriter().write(jwt);
                }
                else{
                    response.setStatus(400);
                }

            } catch (Exception e) {
                System.out.println(e);
                response.setStatus(400);
            }
        }

    }

    public static String getClientSecret(String client_id) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/api", "postgres", "admin");
            PreparedStatement p = c.prepareStatement("select client_password from client_info where client_id=?;");
            p.setString(1, client_id);
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return "deny";

    }

    public static String random() {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 15; i++) {
            if (rand.nextBoolean()) {
                int num = rand.nextInt(10);
                sb.append(num);
            } else {
                char c = (char) (rand.nextInt(26) + 'a');
                sb.append(c);
            }
        }
        String randomString = sb.toString();
        return randomString;
    }
    public void update_token(String client_id,String access_token){
        try{
            Class.forName("org.postgresql.Driver");
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/api", "postgres", "admin");
            PreparedStatement p=c.prepareStatement("update client_info set cu_access_token=? where client_id=?");
            p.setString(1, access_token);
            p.setString(2, client_id);
            p.execute();
        }catch(Exception e){
            System.out.println(e+".................................................");
        }

    }

}
