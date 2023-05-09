package com.oauth;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.Random;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class utils {
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

   

    
    public static void loadUserDetails(String client_id,String cu_username,String cu_ipaddress,String cu_latitude,String cu_longitude,String user_agent){
        JSONArray arr_ipaddress;
        JSONArray arr_location;
        JSONArray arr_user_agent;
        try{
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/api", "postgres", "admin");
            PreparedStatement p = c.prepareStatement("select * from client_user_info where username=? and client_id=?;");
            p.setString(1, cu_username);
            p.setString(2, client_id);
            ResultSet r=p.executeQuery();
            if(r.next()){
                JSONParser parser=new JSONParser();
                arr_ipaddress=(JSONArray) parser.parse(r.getString("ip_address"));
                arr_user_agent=(JSONArray) parser.parse(r.getString("user_agent"));
                arr_location=(JSONArray) parser.parse(r.getString("user_location"));
                if(!arr_ipaddress.contains(cu_ipaddress)){
                    arr_ipaddress.add(cu_ipaddress);
                }
                if(!arr_user_agent.contains(user_agent)){
                    arr_user_agent.add(user_agent);
                }
                JSONObject json=new JSONObject();
                json.put("latitude", cu_latitude);
                json.put("longitude", cu_longitude);
                if(!arr_location.contains(json)){
                    arr_location.add(json);
                }
                PreparedStatement p2=c.prepareStatement("update client_user_info set last_login=?,ip_address=?,user_agent=?,user_location=? where username=? and client_id=?;");
                p2.setString(1,String.valueOf(LocalDateTime.now()));
                p2.setString(2, String.valueOf(arr_ipaddress));
                p2.setString(3, String.valueOf(arr_user_agent));
                p2.setString(4, String.valueOf(arr_location));
                p2.setString(5, cu_username);
                p2.setString(6, client_id);
                p2.execute();
            }
            else{
                
                arr_ipaddress=new JSONArray();
                arr_location=new JSONArray();
                arr_user_agent=new JSONArray();
                arr_ipaddress.add(cu_ipaddress);
                arr_user_agent.add(user_agent);
                JSONObject json=new JSONObject();
                json.put("latitude", cu_latitude);
                json.put("longitude", cu_longitude);
                arr_location.add(json);
                PreparedStatement p2=c.prepareStatement("insert into client_user_info values(?,?,?,?,?,?);");
                p2.setString(1,client_id);
                p2.setString(2, cu_username);
                p2.setString(3, String.valueOf(LocalDateTime.now()));
                p2.setString(4,String.valueOf(arr_location));
                p2.setString(5,String.valueOf(arr_ipaddress));
                p2.setString(6,String.valueOf(arr_user_agent));
                p2.execute();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //Latest

    public static boolean validate_client(String client_id1,String client_secret,String scope){

        try{
            Class.forName("org.postgresql.Driver");
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/api", "postgres", "admin");
            PreparedStatement p=c.prepareStatement("select * from client_info where client_id=?");
            p.setString(1, client_id1);
            System.out.println(client_id1);
            ResultSet r=p.executeQuery();
            System.out.println("test11");
            while(r.next()){
                System.out.println("teat12");
                if(r.getString("client_password").equals(client_secret)&&r.getString("scope").equalsIgnoreCase(scope)){
                    System.out.println("tes14");
                    return true;
                }
            }
        }catch (Exception e){
            System.out.println(e);
        }
        System.out.println("test15");
        return false;

    }
    

    
    
}
