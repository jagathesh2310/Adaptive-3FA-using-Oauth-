package teacher;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.*;



public class teacherlogin extends HttpServlet{
    public static String[] getdata(String tid){
        String[] arr=new String[3];
        try{
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ams", "postgres", "admin");
            Statement s = c.createStatement();
            ResultSet r = s.executeQuery("select * from teacher where tid='"+tid+"';");
            while(r.next()){
                arr[0]=r.getString("tdid");
                arr[1]=r.getString("tdes");
                arr[2]=r.getString("tdid");
                return arr;
            }
        }
        catch(Exception e){
        }
        return arr;

    }

    public void doGet(HttpServletRequest request,HttpServletResponse response)throws IOException{
        response.setContentType("text/html");
        PrintWriter out=response.getWriter();
        HttpSession session=request.getSession();
        session.setAttribute("login", "access");
        session.setAttribute("dept_id",teacherlogin.getdata(request.getRemoteUser())[2] );
        session.setAttribute("tid", request.getRemoteUser());
        if(teacherlogin.getdata(request.getRemoteUser())[1].equals("HOD")){
            out.print("allowed");
            String status=auto_status(request.getRemoteUser());
            status="?auto_status="+status;
            if(request.getParameter("auto_message")!=null){
                status+="&auto_message="+request.getParameter("auto_message");
            }
            response.sendRedirect("hod/hodindex.jsp"+status);

        }
        else{
            String status=auto_status(request.getRemoteUser());
            status="?auto_status="+status;
            if(request.getParameter("auto_message")!=null){
                status+="&auto_message="+request.getParameter("auto_message");
            }
            response.sendRedirect("staff/teacherindex.jsp"+status);
        }
        

    }
    
    public String auto_status(String username){
        try{
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ams", "postgres", "admin");
            PreparedStatement p=c.prepareStatement("select * from teacher where tid=?");
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
