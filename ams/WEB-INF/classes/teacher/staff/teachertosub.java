package teacher.staff;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;

import org.json.simple.*;
import java.sql.*;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


public class teachertosub extends HttpServlet{
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws IOException{
        response.setContentType("text/html");
        PrintWriter out=response.getWriter();
        if((String)request.getSession().getAttribute("login")!=null){
        try {
            JSONArray ob = new JSONArray();
            Class.forName("org.postgresql.Driver");
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ams", "postgres", "admin");
            Statement s = c.createStatement();
            ResultSet r = s.executeQuery("select * from teacher;");
            c.close();
            Connection c1 = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ams", "postgres", "admin");
            HttpSession session=request.getSession();
            while (r.next()) {
                //ob.add((String)session.getAttribute("dept_id"));
                if(r.getString("tdid").equals((String)session.getAttribute("dept_id"))){
                    JSONArray temp = new JSONArray();
                    temp.add(r.getString(1));
                    temp.add(r.getString(2));
                    temp.add(r.getString(3));
                    temp.add(r.getString(4));
                    PreparedStatement p1=c1.prepareStatement("select dept_name from department where dept_id=?;");
                    p1.setString(1, r.getString(4));
                    ResultSet r1 = p1.executeQuery();
                    if(r1.next()){
                        temp.add(r1.getString(1));
                    }
                    temp.add(r.getString(5));

                    ob.add(temp);
                }
            }
            String d1=String.valueOf(ob);
            response.getWriter().write(d1);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    else{
        out.print("<script>");
        out.print("alert(\"Login To Access Data\");");
        out.print("window.location = \"../../index.html\";");
        out.print("</script>");

    }

    }
}

