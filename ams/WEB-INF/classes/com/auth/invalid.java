package com.auth;

import java.io.IOException;
import java.io.PrintWriter;


import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.servlet.annotation.*;

@WebServlet(value="/invalid")

public class invalid extends HttpServlet{
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws IOException{
        response.setContentType("text/html");
        PrintWriter out=response.getWriter();
        request.getSession().invalidate();
        System.out.println("Session invalidated");
        response.sendRedirect("index.html");
        
    }
    
}
