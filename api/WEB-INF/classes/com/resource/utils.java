package com.resource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;



public class utils {
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

    public static boolean validate_db_token(String client_id,String access_token,String username) {
        try {
            
            Class.forName("org.postgresql.Driver");
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/api", "postgres", "admin");
            PreparedStatement p = c.prepareStatement("select * from client_token where client_id=? and username=? and access_token=?;");
            p.setString(1, client_id);
            p.setString(2, username);
            p.setString(3, access_token);
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("invalid filter token in");
        return false;

    }

    public static boolean loadUserDetails(String client_id,String cu_username,String cu_ipaddress,String cu_latitude,String cu_longitude,String user_agent){
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
                return true;
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
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }


    public static void update_token(String client_id,String access_token){
        try{
            Class.forName("org.postgresql.Driver");
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/api", "postgres", "admin");
            PreparedStatement p=c.prepareStatement("update client_info set cu_access_token=? where client_id=?");
            p.setString(1, access_token);
            p.setString(2, client_id);
            p.execute();
        }catch(Exception e){
            System.out.println(e);
        }

    }

    
}
