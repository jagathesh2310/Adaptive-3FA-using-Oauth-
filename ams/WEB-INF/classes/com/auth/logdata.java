package com.auth;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import RSA.RSAUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class logdata extends HttpServlet {
    String public_key;
    String private_key;

    @Override
    public void init() {
        System.out.println("11111111111111111");

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            RSAUtil.initialize();
            private_key = RSAUtil.private_key;
            System.out.println(private_key);
            public_key = RSAUtil.public_key;

        } catch (Exception e) {
            System.out.println(e);
        }
        HttpSession session = request.getSession();
        session.setAttribute("public_key", public_key);
        response.sendRedirect("/ams/index1.jsp");
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String key1 = request.getParameter("key1");
        String decryptedString = "";
        HttpSession session = request.getSession();
        try {
            decryptedString = RSAUtil.decrypt(key1, private_key);
           
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException
                | NoSuchPaddingException e) {
            System.out.println(e);
        }
        String user_agent = request.getHeader("User-Agent");
       

        try {
            
            if (key1 != null && decryptedString != null) {
                JSONParser parser = new JSONParser();
                JSONArray arr = (JSONArray) parser.parse(decryptedString);
                if (arr.size() == 4) {
                    String token = utils.random();
                    String ip = utils.get_ip(request);
                    loaddata(arr, token, user_agent, ip);
                    if (utils.checkauth((String) arr.get(0), (String) arr.get(1))) {
                        response.getWriter().write(token);
                    } else {
                        RSAUtil.initialize();
                        private_key = RSAUtil.private_key;
                        public_key = RSAUtil.public_key;
                        session.setAttribute("public_key", public_key);
                        response.getWriter().write("invalid");
                    }
                } else {
                    RSAUtil.initialize();
                    private_key = RSAUtil.private_key;
                    public_key = RSAUtil.public_key;
                    session.setAttribute("public_key", public_key);
                    response.getWriter().write("invalid");
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void loaddata(JSONArray arr, String token, String user_agent, String ip) {
        try {
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ams", "postgres", "admin");
            PreparedStatement p = c.prepareStatement("insert into log_data values(?,?,?,?,?,?,?)");
            p.setString(1, (String) arr.get(0));
            p.setString(2, (String) arr.get(1));
            p.setString(3, ip);
            p.setString(4, (String) arr.get(2));
            p.setString(5, (String) arr.get(3));
            p.setString(6, token);
            p.setString(7, user_agent);
            p.execute();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
