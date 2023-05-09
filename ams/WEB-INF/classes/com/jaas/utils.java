package com.jaas;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Base64;
import java.util.Properties;

import org.json.simple.JSONArray;

import RSA.RSAUtil;

public class utils {
    static String ipaddress;
    static String username;
    static String user_agent;

    public static void erase_activity() {
        try {
            Class.forName("org.postgresql.Driver");
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ams", "postgres", "admin");
            PreparedStatement p = c.prepareStatement("delete from log_data where username=?;");
            p.setString(1, username);
            p.execute();
            c.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static String getPublickey(String url, String token) throws IOException {
        URL resourceUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) resourceUrl.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + token);

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return response.toString();

    }

    public static String load_data(String access_token, String public_key, String uri) {
        try {
            FileReader file = new FileReader(
                    "C:\\Program Files\\Apache Software Foundation\\Tomcat 10.0\\webapps\\ams\\data\\info.properties");
            Properties p = new Properties();
            p.load(file);
            JSONArray arr = new JSONArray();
            arr.add(p.getProperty("ipaddress"));
            arr.add(p.getProperty("latitude"));
            arr.add(p.getProperty("longtitude"));
            arr.add(p.getProperty("user_agent"));

            byte[] en_arr_by = RSAUtil.encrypt(String.valueOf(arr), public_key);
            String en_arr = Base64.getUrlEncoder().encodeToString(en_arr_by);

            URL resourceUrl = new URL(uri+"?log_data="+en_arr);
            HttpURLConnection connection = (HttpURLConnection) resourceUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + access_token);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();

        } catch (Exception e) {

            e.printStackTrace();
        }
        return null;

    }

}
