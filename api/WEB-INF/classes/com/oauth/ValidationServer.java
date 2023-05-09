package com.oauth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;



import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import RSA.RSAUtil;

public class ValidationServer extends HttpServlet {
    String cu_ipaddress="";
    String cu_latitude="";
    String cu_longitude="";
    String cu_useragent="";
    String cu_username="";
    String client_id="";
    String state="";
    String REDIRECT_URI="";
    String private_key="";
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    
        try {
            RSAUtil.initialize();
            private_key = RSAUtil.private_key;
           
            
            response.getWriter().write(RSAUtil.public_key);
            response.getWriter().close();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(400);
        }

    }
    public void doGet(HttpServletRequest request, HttpServletResponse response){

        String grant_type=request.getParameter("grant_type");
  

        if(grant_type.equals("refresh_token")){
            try{
            String CLIENT_ID=request.getParameter("CLIENT_ID");
            String CLIENT_SECRET=RSAUtil.decrypt2(request.getParameter("CLIENT_SECRET"), private_key);
            String refresh_token=request.getParameter("refresh_token");
            Claims c=Jwts.parser().setSigningKey(CLIENT_SECRET).parseClaimsJws(refresh_token).getBody();
            HashMap<String,String> user_info=new HashMap<String,String>();
            user_info.put("username", (String) c.get("username"));
            user_info.put("scope", (String) c.get("scope"));
            user_info.put("CLIENT_ID", (String) c.get("CLIENT_ID"));
            user_info.put("aud","http://localhost:8080/api/resource/*");
            String access_token=generateAccessToken(user_info, CLIENT_SECRET);
            update_token(access_token, user_info.get("CLIENT_ID"), user_info.get("username") );
            response.getWriter().write(access_token);


            }
            catch(Exception e){
                e.printStackTrace();
                response.setStatus(400);
            }

        }
        else{

        
        try{
        String CLIENT_ID=request.getParameter("CLIENT_ID");
        String CLIENT_SECRET=RSAUtil.decrypt2(request.getParameter("CLIENT_SECRET"), private_key);
        String username=request.getParameter("username");

        String scope=request.getParameter("scope");


        if(grant_type.equalsIgnoreCase("client_credentials")&&utils.validate_client(CLIENT_ID,CLIENT_SECRET,scope)){
     
            HashMap<String,String> user_info=new HashMap<>();
            user_info.put("CLIENT_ID",CLIENT_ID);
            
            user_info.put("username", username);
            user_info.put("scope", scope);   
            user_info.put("aud","http://localhost:8080/api/resource/*");
            JSONObject json=new JSONObject();
            json.put("access_token",generateAccessToken(user_info, CLIENT_SECRET));
            json.put("refresh_token",generateRefreshToken(user_info, CLIENT_SECRET));
            update_token(json,user_info.get("CLIENT_ID"),user_info.get("username"));
            response.getWriter().write(String.valueOf(json));
        }
        else{
            
            response.setStatus(400);
        }

        }catch(Exception e){
            e.printStackTrace();
            response.setStatus(400);
            
        }
    }
    }
    public static void update_token(JSONObject json,String client_id,String username){
        try{
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/api", "postgres", "admin");
            PreparedStatement p = c.prepareStatement("update client_token set access_token=?,refresh_token=? where client_id=? and username=?");
            p.setString(1,(String) json.get("access_token"));
            p.setString(2,(String) json.get("refresh_token"));
            p.setString(3,client_id);
            p.setString(4,username);
            if(p.executeUpdate()==0){
                PreparedStatement p2=c.prepareStatement("insert into client_token values(?,?,?,?);");
                p2.setString(1,client_id);
                p2.setString(2, username);
                p2.setString(3,(String) json.get("access_token"));
                p2.setString(4,(String) json.get("refresh_token"));
                p2.execute();

            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void update_token(String access_token,String client_id,String username){
        
        try{
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/api", "postgres", "admin");
            PreparedStatement p = c.prepareStatement("update client_token set access_token=? where client_id=? and username=?");
            p.setString(1,access_token);
            p.setString(2,client_id);
            p.setString(3,username);
            System.out.println(p.executeUpdate());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private String generateAccessToken(HashMap<String,String> user_info,String client_secret) {
        
        long millitime=System.currentTimeMillis();
        long expiryTime=millitime+3600_000L;
        Date expiryAt=new Date(expiryTime);
        Date issuedAt = new Date(millitime);
        Claims claims = Jwts.claims ()
                .setIssuer ("Oauth AMS")
                .setIssuedAt (issuedAt)
                .setExpiration(expiryAt)
                .setAudience(user_info.get("aud"))
                .setIssuer("http://localhost:8080/api/ValidationServer");
                claims.put("username", user_info.get("username"));
                claims.put("scope",user_info.get("scope"));
                claims.put("CLIENT_ID",user_info.get("CLIENT_ID"));
                claims.put("token_type","access_token");
        String token=Jwts.builder().setClaims (claims).signWith(SignatureAlgorithm.HS512,client_secret).compact();
        update_token(user_info.get("CLIENT_ID"), token);
        
        return token;
    }

    private String generateRefreshToken(HashMap<String,String> user_info,String client_secret) {
        
        long millitime=System.currentTimeMillis();
        long expiryTime=millitime+2592000000L;
        Date expiryAt=new Date(expiryTime);
        Date issuedAt = new Date(millitime);
        Claims claims = Jwts.claims ()
                .setIssuer ("Oauth AMS")
                .setIssuedAt (issuedAt)
                .setExpiration(expiryAt)
                .setAudience(user_info.get("aud"))
                .setIssuer("http://localhost:8080/api/ValidationServer");
                claims.put("username", user_info.get("username"));
                claims.put("scope",user_info.get("scope"));
                claims.put("CLIENT_ID",user_info.get("CLIENT_ID"));
                claims.put("token_type","refresh_token");
        String token=Jwts.builder().setClaims (claims).signWith(SignatureAlgorithm.HS512,client_secret).compact();
        update_token(user_info.get("CLIENT_ID"), token);
        
        return token;
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
            e.printStackTrace();
        }

    }

    public void loaddb(String code,String username,String scope,String client_id,String MFA){
        try{
            Class.forName("org.postgresql.Driver");
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/api", "postgres", "admin");
            PreparedStatement p=c.prepareStatement("insert into oauth values(?,?,?,?,?,?,?);");
            p.setString(1,code);
            p.setString(2,"null");
            p.setString(3,username);
            p.setString(4,scope);
            LocalDateTime current_date = LocalDateTime.now();
            p.setString(5,current_date.toString());
            p.setString(6,client_id);
            p.setString(7,MFA);
            p.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    
    public String getClientSecret(String client_id){
        try{
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/api", "postgres", "admin");
            PreparedStatement p = c.prepareStatement("select client_password from client_info where client_id=?");
            p.setString(1, client_id);
            ResultSet r=p.executeQuery();
            if(r.next()){
                return r.getString(1);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return client_id;

    }
    public boolean validate_token(String client_secret,String token){
        try{
            Jwts.parser().setSigningKey(client_secret).parseClaimsJws(token);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }

    }
    public String[] get_users_old_info(String client_id,String username){
        String arr[]=new String[4];
        try{
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/api", "postgres", "admin");
            PreparedStatement p = c.prepareStatement("select * from client_user_info where client_id=? and username=?");
            p.setString(1, client_id);
            p.setString(2, username);
            ResultSet r=p.executeQuery();
            if(r.next()){
                arr[0]=r.getString("last_login");
                arr[1]=r.getString("user_location");
                arr[2]=r.getString("ip_address");
                arr[3]=r.getString("user_agent");
                return arr;

            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public boolean check_valid_ip(String cu_ipaddress,String ol_ip){
        try{
        JSONParser parser=new JSONParser();
        JSONArray arr=(JSONArray) parser.parse(ol_ip);
        for(int i=0;i<arr.size();i++){
            if(arr.get(i).equals(cu_ipaddress)){
                return true;
            }
        }
        }catch(Exception e){
            System.out.println(e);
        }
        return false;

    }
    public boolean check_valid_location(String cu_latitude,String cu_longitude,String ol_lo){
        try{
        JSONParser parser=new JSONParser();
        JSONArray arr=(JSONArray) parser.parse(ol_lo);
        for(int i=0;i<arr.size();i++){
            JSONObject json=(JSONObject) arr.get(i);
            if(distance(Double.parseDouble((String) json.get("latitude")),Double.parseDouble((String) json.get("longitude")), Double.parseDouble(cu_latitude),Double.parseDouble(cu_longitude))){
                return true;
            }
        }
        }catch(Exception e){
            System.out.println(e);
        }
        return false;

    }
    public boolean check_valid_user_agent(String cu_useragent,String ol_user_agent){
        try{
        JSONParser parser=new JSONParser();
        JSONArray arr=(JSONArray) parser.parse(ol_user_agent);
        for(int i=0;i<arr.size();i++){
            if(arr.get(i).equals(cu_useragent)){
                return true;
            }
        }
        }catch(Exception e){
            System.out.println(e);
        }
        return false;

    }
    public boolean check_valid_last_login(String ol_lastLogin){
        if(validatedate(ol_lastLogin)){
            return true;
        }
        return false;

    }
    public boolean check_valid_login_activity(JSONArray arr){
        if(arr.size()==1){
            return true;
        }
        return false;

    }




    public boolean distance(double lat1, double lon1, double lat2, double lon2) {

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));
        int dis = (int) (rad * c);
        if (dis < 10) {
            return true;
        } else {
            return false;
        }
    }
    public static boolean validatedate(String date) {
        LocalDateTime current_date = LocalDateTime.now();

        LocalDateTime pr_date = LocalDateTime.parse(date);

        long btwn = ChronoUnit.HOURS.between(pr_date, current_date);
        
        if (btwn < 24) {
        
            return true;
            
        } else {
            return false;
        }
    }

 
}

