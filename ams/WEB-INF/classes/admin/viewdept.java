package admin;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;

import org.json.simple.*;
import java.sql.*;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(value="/viewdept")
public class viewdept extends HttpServlet{
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws IOException{
        response.setContentType("text/html");
        PrintWriter out=response.getWriter();
        if((String)request.getSession().getAttribute("login")!=null){
        try {
            JSONArray ob = new JSONArray();
            Class.forName("org.postgresql.Driver");
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ams", "postgres", "admin");
            Statement s = c.createStatement();
            ResultSet r = s.executeQuery("select * from department;");
            while (r.next()) {
                JSONArray temp = new JSONArray();
                temp.add(r.getString(1));
                temp.add(r.getString(2));
                ob.add(temp);
            }
            String d1=String.valueOf(ob);
            response.getWriter().write(d1);
        }
        catch (Exception e) {
            response.getWriter().write("Error");
        }
    }
    else{
        out.print("<script>");
        out.print("alert(\"Login To Access Data\");");
        out.print("window.location = \"../index.html\";");
        out.print("</script>");

    }

    }
}

