package com.auth;


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class fetchdata extends HttpServlet{
    

    public void doGet(HttpServletRequest request,HttpServletResponse response)throws IOException{
    
        
        if(request.getParameter("uname").equals("admin") ){
            try{
                Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ams", "postgres", "admin");
                PreparedStatement p=c.prepareStatement("select * from admin where username=? AND password=?");
                p.setString(1,request.getParameter("uname"));
                p.setString(2,request.getParameter("pass"));
                ResultSet rs=p.executeQuery();
                if(rs.next()){
                    response.getWriter().write("allow");
                    response.getWriter().close();
                }
                c.close();
                response.getWriter().write("deny");
                response.getWriter().close();
            }catch(Exception e){
            response.getWriter().write("allow");
            response.getWriter().close();
            }
        }
        else{
            try{
                Class.forName("org.postgresql.Driver");
                Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ams", "postgres", "admin");
                PreparedStatement p=c.prepareStatement("select * from teacher where tid=? AND password=?");
                p.setString(1,request.getParameter("uname"));
                p.setString(2,request.getParameter("pass"));
                ResultSet rs=p.executeQuery();
                if(rs.next()){
                    response.getWriter().write("allow");
                    response.getWriter().close();
                }
                c.close();
                response.getWriter().write("deny");
                response.getWriter().close();

        
    
            }catch(Exception e){
                System.out.println(e);
            }
            
        }
        
        

    }
    
}
