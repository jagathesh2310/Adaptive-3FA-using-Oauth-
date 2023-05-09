package com.auth;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import RSA.RSAUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.impl.DefaultJwtParser;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class validation extends HttpServlet {

    String password = "";
    String username = "";
    String access_token = "";
    String risk = "";
    String temp_token="";

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String value = request.getParameter("value");
        String code = request.getParameter("duo_code");
        String state = request.getParameter("state");
        if (state != null && code != null) {
            String temp = OAuth2Client.get_duo_auth_validation(access_token, code, state,temp_token);
            if (temp != null) {
                try {
                    JSONArray jsonarr = new JSONArray();
                    temp_token = temp;
                    byte[] en_password = RSAUtil.encrypt(password, RSAUtil.public_key);
                    System.out.println("private key: " + RSAUtil.private_key);
                    jsonarr.add(Base64.getEncoder().encodeToString(en_password));
                    jsonarr.add(temp_token);
                    jsonarr.add(access_token);
                    String str_json = String.valueOf(jsonarr);
                    request.getSession().setAttribute("temp", "allow");
                    response.sendRedirect("login.jsp?password="
                            + Base64.getUrlEncoder().encodeToString(str_json.getBytes()));
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else {
                response.sendRedirect("error.jsp");
            }
        }

        else {
            String mig_with_otp = OAuth2Client.otpvalidation(access_token, value,temp_token);
            if (mig_with_otp == null) {
             
                response.sendRedirect("2fvalidation.jsp?message=Invalid Otp");
            } else {
                
                temp_token = mig_with_otp;
               
                if (risk.equals("high_risk")) {
                    String duo_auth_uri = OAuth2Client.get_duo_auth_uri(access_token,String.valueOf(request.getRequestURL()));
                    
                    response.sendRedirect(duo_auth_uri);
                } else {
                    try {
                        JSONArray jsonarr = new JSONArray();

                        byte[] en_password = RSAUtil.encrypt(password, RSAUtil.public_key);
                        System.out.println("private key: " + RSAUtil.private_key);
                        jsonarr.add(Base64.getEncoder().encodeToString(en_password));
                        jsonarr.add(temp_token);
                        jsonarr.add(access_token);
                        String str_json = String.valueOf(jsonarr);
                        request.getSession().setAttribute("temp", "allow");
                        response.sendRedirect("login.jsp?password="+ Base64.getUrlEncoder().encodeToString(str_json.getBytes()));
                    } catch (Exception e) {
                        System.out.println(e);
                    }

                }
            }
        }

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println(request.getRequestURL());
        
        HttpSession session = request.getSession();
        session.setAttribute("username", request.getParameter("username"));

        if (request.getParameter("username") != null && request.getParameter("password") != null
                && request.getParameter("key_logdata") != null) {
            username = request.getParameter("username");

            try {
               
                password = RSAUtil.decrypt(request.getParameter("password"), RSAUtil.private_key);

            } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException
                    | NoSuchPaddingException e) {
                e.printStackTrace();
            }
            String key = request.getParameter("key_logdata");
            

            if (utils.checkauth(username, password)) {
                String[] arr = utils.getdata(key, username);

                if (arr == null) {
                    System.out.println("wrong token");
                    response.sendRedirect("error.jsp");
                } else {
                    String log_data = log_data(username, arr, key);
                    String temp2=OAuth2Client.getOldToken(username);

                    if(temp2!=null){
                        access_token=temp2;
                        System.out.println("old token");
                        
                    }
                    else{
                        access_token = OAuth2Client.getAuthorizationToken(username);
                        System.out.println("new access token");
                        
                    }

                    
                    
                    if (access_token.equals("bad_request")) {
                        
                        response.sendRedirect("error.jsp");
                    } else {
                        temp_token = OAuth2Client.Validate_Adaptive(log_data, access_token);
                        System.out.println(temp_token+"..................");
                        
                        
                        if (temp_token==null) {
                            utils.delete_old_token(username);
                            response.sendRedirect("error.jsp");
                        } else {
                            String[] splitToken = temp_token.split("\\.");
                            String unsignedToken = splitToken[0] + "." + splitToken[1] + ".";
                            DefaultJwtParser parser = new DefaultJwtParser();
                            Jwt<?, ?> jwt = parser.parse(unsignedToken);
                            Claims claims1 = (Claims) jwt.getBody();
                            risk = (String) claims1.get("risk");
                            if (risk.equals("high_risk") || risk.equals("medium_risk")) {
                                OAuth2Client.otprequest(access_token);
                              
                                response.sendRedirect("2fvalidation.jsp");
                            } else {
                                try {
                                    JSONArray jsonarr = new JSONArray();
                                    byte[] en_password = RSAUtil.encrypt(password, RSAUtil.public_key);
                    
                                    jsonarr.add(Base64.getEncoder().encodeToString(en_password));
                                    jsonarr.add(temp_token);
                                    jsonarr.add(access_token);
                                    String str_json = String.valueOf(jsonarr);
                                    request.getSession().setAttribute("temp", "allow");
                                    response.sendRedirect("login.jsp?password="
                                            + Base64.getUrlEncoder().encodeToString(str_json.getBytes()));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    }
                }
            } else {
                System.out.println("Invalid from validation");
                response.sendRedirect("index1.jsp");
            }
        }
        else{
            System.out.println("Invalid from validation");
            response.sendRedirect("index1.jsp");
        }
    }

    public String log_data(String username, String[] arr, String token) {
        JSONObject json = new JSONObject();
        try {
            Class.forName("org.postgresql.Driver");
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ams", "postgres", "admin");
            PreparedStatement p = c.prepareStatement("select * from log_data where username=?;");
            p.setString(1, username);

            // p.setString(2, arr[2]);
            ResultSet rs = p.executeQuery();
            int count = 0;
            boolean in_data = false;
            while (rs.next()) {
                if (rs.getString("token").equals(token)) {
                    json.put("username", rs.getString(1));
                    json.put("ipaddress", rs.getString("ipaddress"));
                    json.put("latitude", rs.getString("latitude"));
                    json.put("longtitude", rs.getString("longtitude"));
                    json.put("user_agent", rs.getString("user_agent"));
                    in_data = true;

                }
                count++;
            }
            if (in_data) {
                Properties p1 = new Properties();

                p1.put("ipaddress", json.get("ipaddress"));
                p1.put("latitude", json.get("latitude"));
                p1.put("longtitude", json.get("longtitude"));
                p1.put("user_agent", json.get("user_agent"));
                FileOutputStream outputStrem = new FileOutputStream(
                        "C:\\Program Files\\Apache Software Foundation\\Tomcat 10.1\\webapps\\ams\\data\\info.properties");
                p1.store(outputStrem, "enter");

                json.put("log_count", String.valueOf(count));

                return String.valueOf(json);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

}
