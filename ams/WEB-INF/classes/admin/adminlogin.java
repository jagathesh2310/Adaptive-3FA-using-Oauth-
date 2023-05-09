package admin;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.*;

@WebServlet(value="/adminlogin")

public class adminlogin extends HttpServlet{
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws IOException{
        response.setContentType("text/html");
        PrintWriter out=response.getWriter();
        
            HttpSession session=request.getSession();
            session.setAttribute("login", "access");  
            session.setAttribute("uname",request.getRemoteUser()); 
            String status=auto_status(request.getRemoteUser());
            status="?auto_status="+status;
            if(request.getParameter("auto_message")!=null){
                status+="&auto_message="+request.getParameter("auto_message");
            }
            response.sendRedirect("adminindex.html"+status);  

    }


    public String auto_status(String username){
        try{
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ams", "postgres", "admin");
            PreparedStatement p=c.prepareStatement("select * from admin where username=?");
            p.setString(1, username);
            ResultSet r=p.executeQuery();
            if(r.next()){
                return r.getString("auto_status");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return "disable";

    }
    
}
