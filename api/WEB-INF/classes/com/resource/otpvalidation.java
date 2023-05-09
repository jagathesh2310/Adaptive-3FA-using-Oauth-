package com.resource;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class otpvalidation extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Claims claims = (Claims) request.getAttribute("token");
        String username = (String) claims.get("CLIENT_ID") + claims.get("username");
        Claims claims2=Jwts.parser().setSigningKey(utils.getClientSecret((String) claims.get("CLIENT_ID"))).parseClaimsJws(request.getParameter("temp_token")).getBody();

        try {
            Class.forName("org.postgresql.Driver");
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/api", "postgres", "admin");
            PreparedStatement p = c.prepareStatement("select * from otpdata where username=?");
            p.setString(1, username);
            ResultSet rs = p.executeQuery();
            c.close();
            if (rs.next()) {
                if (rs.getString(1).equals(request.getParameter("value"))) {
                    claims2.put("2fa", "allow");
                    String jwt = Jwts.builder().setClaims(claims2)
                            .signWith(SignatureAlgorithm.HS256, utils.getClientSecret((String) claims.get("CLIENT_ID")))
                            .compact();
                            //utils.update_token((String) claims.get("CLIENT_ID"), jwt);
                    response.getWriter().write(jwt);
                } else {
                    response.setStatus(400);
                }
            } else {
                response.setStatus(400);
            }

        } catch (Exception e) {
            System.out.println(e);
            response.setStatus(400);
        }

    }

}
