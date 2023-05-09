package com.auth;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.net.InetAddress;

import jakarta.servlet.http.HttpServletRequest;

public class utils {
    public static void update_user_tokens(String username,String access_token,String refresh_token){
        try{
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ams", "postgres", "admin");
            if(username.equals("admin")){
                PreparedStatement p=c.prepareStatement("update admin set access_token=?,refresh_token=? where username=?;");
                p.setString(1, access_token);
                p.setString(2, refresh_token);
                p.setString(3, username);
                p.execute();
            }
            else{
                PreparedStatement p=c.prepareStatement("update teacher set access_token=?,refresh_token=? where tid=?;");
                p.setString(1, access_token);
                p.setString(2, refresh_token);
                p.setString(3, username);
                p.execute();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void update_access_token(String access_token,String username){
        try{
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ams", "postgres", "admin");
            if(username.equals("admin")){
              
                PreparedStatement p=c.prepareStatement("update admin set access_token=? where username=?;");
                p.setString(1, access_token);
                p.setString(2, username);
                p.executeUpdate();
            }
            else{
                PreparedStatement p=c.prepareStatement("update teacher set access_token=?where tid=?;");
                p.setString(1, access_token);
                p.setString(2, username);
                p.execute();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void delete_old_token(String username){
        try{
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ams", "postgres", "admin");
            if(username.equals("admin")){
              
                PreparedStatement p=c.prepareStatement("update admin set refresh_token=null,access_token=null;");
                p.executeUpdate();
            }
            else{
                PreparedStatement p=c.prepareStatement("update teacher set refresh_token=null,access_token=null;");
                p.execute();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
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

    public static boolean checkauth(String username, String password) {
        if (username.equals("admin")) {
            try {
                Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ams", "postgres", "admin");
                PreparedStatement p = c.prepareStatement("select * from admin where username=? AND password=?");
                p.setString(1, username);
                p.setString(2, password);
                ResultSet rs = p.executeQuery();
                if (rs.next()) {
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        } else {
            try {
                Class.forName("org.postgresql.Driver");
                Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ams", "postgres", "admin");
                PreparedStatement p = c.prepareStatement("select * from teacher where tid=? AND password=?");
                p.setString(1, username);
                p.setString(2, password);
                ResultSet rs = p.executeQuery();
                if (rs.next()) {
                    return true;
                }
            } catch (Exception e) {
                System.out.println(e);
                return false;
            }

        }
        return false;

    }

    
    public static String[] getdata(String token, String username) {
        String[] datas = new String[6];
        try {
            Class.forName("org.postgresql.Driver");
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ams", "postgres", "admin");
            PreparedStatement p = c.prepareStatement("select * from log_data where token=? and username=?");
            p.setString(1, token);
            p.setString(2, username);
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                datas[0] = rs.getString(1);
                datas[1] = rs.getString(2);
                datas[2] = rs.getString(3);
                datas[3] = rs.getString(4);
                datas[4] = rs.getString(5);
                datas[5] = rs.getString(7);
                return datas;
            }
            return null;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }

    }

    

    public static void send_otp(String username) {
        try {
            URL url = new URL("http://localhost:8080/ams/otpresponse?username=" + username);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int status = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            System.out.println(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get_ip(HttpServletRequest request){
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null){
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null){
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null){
            ip =  request.getHeader("HTTP_CLIENT_IP");
        }
        if(ip==null||"unknown".equals (ip)) {
            ip = request.getRemoteAddr();
            if("127.0.0.1".equals (ip) || "0:0:0:0:0:0:0:1".equals (ip)) {
                try {
                    InetAddress inetAddress = InetAddress.getLocalHost();
                ip = inetAddress.getHostAddress();
               
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
        

        return ip;
        


        
    }


    //Latest.........







}
