package teacher.staff;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


public class attendancedata extends HttpServlet{
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws IOException{
        response.setContentType("text/html");
        PrintWriter out=response.getWriter();
        
        try{
            
            Class.forName("org.postgresql.Driver");
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ams", "postgres", "admin");
            String t=(request.getParameter("data"));
            JSONParser parser = new JSONParser();
            JSONArray obj = (JSONArray) parser.parse(t);
            HttpSession session=request.getSession();
            for(int i=0;i<obj.size();i++){

                PreparedStatement p=c.prepareStatement("insert into attendance values(?,?,?,?);");
                JSONParser parser2 = new JSONParser();
                JSONArray obj2 = (JSONArray) parser2.parse(String.valueOf(obj.get(i)) );
                p.setString(1,request.getParameter("date"));
                p.setString(2,String.valueOf(obj2.get(0)) );
                p.setString(3,request.getParameter("su_id"));
                p.setString(4,request.getParameter(String.valueOf(obj2.get(0))));
                p.execute();
                

            }
            c.close();
            response.sendRedirect("attendance.jsp?sub_id="+request.getParameter("su_id"));
            

	

        }
	    catch(Exception e){
            
            out.print(e);
            out.close();
        }

    }
    
}
