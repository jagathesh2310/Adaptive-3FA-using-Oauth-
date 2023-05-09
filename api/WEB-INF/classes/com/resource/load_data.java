package com.resource;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import RSA.RSAUtil;


public class load_data extends HttpServlet {
    String private_key;
    public void doPost(HttpServletRequest request,HttpServletResponse response){
        
        try {
            RSAUtil.initialize();
            private_key = RSAUtil.private_key;
            
            response.getWriter().write(RSAUtil.public_key);
            response.getWriter().close();
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void doGet(HttpServletRequest request,HttpServletResponse response){
        try{
        Claims claims = (Claims) request.getAttribute("token");
        String username = (String) claims.get("username");
        String client_id=(String) claims.get("CLIENT_ID");
        String str_arr=request.getParameter("log_data");
        str_arr=RSAUtil.decrypt2(str_arr, private_key);
        JSONParser parser=new JSONParser();
        JSONArray arr=(JSONArray) parser.parse(str_arr);
        if(utils.loadUserDetails(client_id, username, (String)arr.get(0), (String)arr.get(1), (String)arr.get(2),(String)arr.get(3))){
            response.getWriter().write("completed");
        }
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
}
