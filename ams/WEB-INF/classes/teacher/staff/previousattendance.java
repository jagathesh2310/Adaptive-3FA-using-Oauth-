package teacher.staff;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


public class previousattendance extends HttpServlet{
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws IOException{
        response.setContentType("text/html");
        PrintWriter out=response.getWriter();
        
        try{
            JSONArray arr=new JSONArray();
            Class.forName("org.postgresql.Driver");
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ams", "postgres", "admin");
            String t=(request.getParameter("subject_id"));
            PreparedStatement p=c.prepareStatement("select * from attendance where sub_id=?");
            p.setString(1, t);
            ResultSet r=p.executeQuery();
            while(r.next()){
                JSONArray temp=new JSONArray();
                temp.add(r.getString(1));
                temp.add(r.getString(2));
                temp.add(r.getString(3));
                temp.add(r.getString(4));
                PreparedStatement p1=c.prepareStatement("select student_name from student where student_id=?;");
                p1.setString(1, r.getString(2));
                
                ResultSet r2=p1.executeQuery();
                if(r2.next()){
                    temp.add(r2.getString(1));
                }
                arr.add(temp);
            }
            response.getWriter().write(String.valueOf(arr));
            

	

        }
	    catch(Exception e){
            
            out.print(e);
            out.close();
        }

    }
    
}
