package com.resource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Random;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;

import io.jsonwebtoken.Claims;

public class otpresponse extends HttpServlet {
    String rand = "";
    String token = "";

    public static StringBuffer sendOTP(String message, String number, String apiKey) {

        StringBuffer response = new StringBuffer();

        try {
            String sendId = "FastSM";
            String language = "english";
            String route = "p";
            message = URLEncoder.encode(message, "UTF-8");

            String myUrl = "https://www.fast2sms.com/dev/bulkV2?authorization=" + apiKey + "&sender_id=" + sendId
                    + "&message=" + message + "&language=" + language + "&route=" + route + "&numbers=" + number;
            URL url = new URL(myUrl);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("cache-control", "no-cache");
            
            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                response.append(line);
            }
            return response;
        } catch (Exception e) {
        }
        return response;

    }

    public static int random_num() {
        int random;
        while (true) {
            random = (int) (Math.random() * 1000000);
            String rand_length = String.valueOf(random);
            if (rand_length.length() == 6) {
                break;
            }
        }
        return random;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Claims claims=(Claims) request.getAttribute("token");
        String username = (String)claims.get("CLIENT_ID")+claims.get("username");
        rand = String.valueOf(random_num());
        token = random();
        String apiKey = "NTnozJR4dYhqk0gb6Q9EOPjBLWmVvrMIlXA1F3Uu2eC5Ki7xGDB2gCLF6413iqSGdyYXbZ0eMNl5j9Rc";
        String message = "Hello User,Your OTP is " + rand;
        String number = "9344997058";
        try {
            Class.forName("org.postgresql.Driver");
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/api", "postgres", "admin");
            PreparedStatement p = c.prepareStatement("truncate table otpdata;insert into otpdata values(?,?,?);");
            p.setString(1, rand);
            p.setString(2, token);
            p.setString(3, username);
            p.execute();
            c.close();

        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println(rand);
        response.getWriter().write(String.valueOf(sendOTP(message, number, apiKey)));

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String temp = request.getParameter("value");
        if (temp.equals(rand)) {
            response.getWriter().write(token);
            response.getWriter().close();
        } else {
            response.getWriter().write("deny");
            response.getWriter().close();
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

}
