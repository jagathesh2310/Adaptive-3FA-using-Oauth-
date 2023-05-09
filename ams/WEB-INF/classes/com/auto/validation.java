package com.auto;

import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;

import RSA.RSAUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.simple.JSONArray;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class validation extends HttpServlet {
    String public_key;
    String private_key;

    @Override
    public void init() {


    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            RSAUtil.initialize();
            private_key = RSAUtil.private_key;
            public_key = RSAUtil.public_key;

        } catch (Exception e) {
            e.printStackTrace();
        }

        response.getWriter().write(public_key);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String encrypted_code = request.getParameter("encrypted_code");

        byte[] encrypted_byte = Base64.getUrlDecoder().decode(encrypted_code);
        encrypted_code = new String(encrypted_byte);

        String decrypted_code = "";
        try {
            decrypted_code = RSAUtil.decrypt(encrypted_code, private_key);
            String userdetails[] = get_user_with_device_code(decrypted_code);
            if (userdetails == null) {
                System.out.println("Auto Sign in Disabled");
                response.sendRedirect("index.html?auto_message=Auto Sign is Disabled or wrong Device");
            } else {
                HttpSession session = request.getSession();
                session.setAttribute("username", userdetails[0]);
                JSONArray jsonarr = new JSONArray();

                byte[] en_password = RSAUtil.encrypt(userdetails[1], RSAUtil.public_key);
  
                jsonarr.add(Base64.getEncoder().encodeToString(en_password));
                jsonarr.add(temp_token(userdetails[0]));
                String str_json = String.valueOf(jsonarr);

                session.setAttribute("temp", "allow");
                response.sendRedirect("login.jsp?password="
                            + Base64.getUrlEncoder().encodeToString(str_json.getBytes()));
            }

        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException
                | NoSuchPaddingException e) {
            e.printStackTrace();
        }

    }

    public String[] get_user_with_device_code(String device_code) {
        String arr[] = new String[2];
        try {
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ams", "postgres", "admin");
            PreparedStatement p = c.prepareStatement("select * from admin where auto_status=? and device_hash=?;");
            p.setString(1, "enable");
            p.setString(2, device_code);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                arr[0] = r.getString("username");
                arr[1] = r.getString("password");
             
                return arr;
            } else {

                PreparedStatement p1 = c.prepareStatement("select * from teacher where auto_status=? and device_hash=?;");
                p1.setString(1, "enable");
                p1.setString(2, device_code);
                ResultSet r1 = p1.executeQuery();
                if (r1.next()) {
                    arr[0] = r1.getString("tid");
                    arr[1] = r1.getString("password");
                    return arr;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String temp_token(String username) {
        try {
            FileReader reader = new FileReader(
                    "C:\\Program Files\\Apache Software Foundation\\Tomcat 10.1\\webapps\\ams\\data\\db.properties");
            Properties p = new Properties();
            p.load(reader);
            String CLIENT_ID = p.getProperty("CLIENT_ID");
            String CLIENT_SECRET = p.getProperty("CLIENT_SECRET");
           

            Claims claims = Jwts.claims()
                    .setSubject(username)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 3600000L));
            claims.put("risk", "low_risk");
            String jwt = Jwts.builder().setClaims(claims)
                    .signWith(SignatureAlgorithm.HS256, CLIENT_SECRET).compact();

                    return jwt;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

}
