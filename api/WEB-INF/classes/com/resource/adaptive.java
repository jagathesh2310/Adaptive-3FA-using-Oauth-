package com.resource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import RSA.RSAUtil;
import AES.AESUtil;

public class adaptive extends HttpServlet {
    String private_key;

    public void doPost(HttpServletRequest request,HttpServletResponse response){
        
        try {
            RSAUtil.initialize();
            private_key = RSAUtil.private_key;
            
            response.getWriter().write(RSAUtil.public_key);
            response.getWriter().close();
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    
    public void doGet(HttpServletRequest request,HttpServletResponse response){
  
        
        try {
            String symmetric_key=RSAUtil.decrypt2(request.getParameter("en_symmetric_key"), private_key);
            String log_data=AESUtil.decrypt(request.getParameter("log_data"), symmetric_key);
      

            JSONParser parser=new JSONParser();
            
            Claims c=(Claims) request.getAttribute("token");
            JSONObject json_log_data=(JSONObject) parser.parse(log_data);

            String user_info[]=get_users_old_info((String)c.get("CLIENT_ID"),(String)json_log_data.get("username"));
            String risk="";
            if(user_info!=null){
                int score=0;
                    if(check_valid_ip((String)json_log_data.get("ipaddress"),user_info[2])){
                        
                        score+=20;
                    }
                    if(check_valid_location((String)json_log_data.get("latitude"),(String)json_log_data.get("longtitude"),user_info[1])){
                        
                        score+=25;
                    }
                    if(check_valid_user_agent((String)json_log_data.get("user_agent"), user_info[3])){
                        
                        score+=15;
                    }
                    if(check_valid_last_login(user_info[0])){
                        
                        score+=15;
                    }
                    if(json_log_data.get("log_count").equals("1")){
                        
                        score+=25;
                    }
                    System.out.println("score : "+score);

                    if(score==100){
                        risk="low_risk";
                    }
                    else if(score>=80){
                        risk="medium_risk";
                    }
                    else {
                        risk="high_risk";
                    }
            }
            else{
                risk="high_risk";
            }
            Claims claims = Jwts.claims ()
                .setSubject((String)json_log_data.get("username"))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 3600000L));
                claims.put("risk",risk);
            String jwt=Jwts.builder().setClaims (claims).signWith(SignatureAlgorithm.HS256,utils.getClientSecret((String) c.get("CLIENT_ID"))).compact();
            //utils.update_token((String) c.get("CLIENT_ID"), jwt);
            response.getWriter().write(jwt);
            
        } catch (Exception e) {
            e.printStackTrace();
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
