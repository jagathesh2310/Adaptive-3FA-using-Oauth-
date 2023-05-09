package teacher.hod;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class addsubject extends HttpServlet{
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws IOException{
        response.setContentType("text/html");
        PrintWriter out=response.getWriter();
        if((String)request.getSession().getAttribute("login")!=null){
        try{
            Class.forName("org.postgresql.Driver");
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ams", "postgres", "admin");
            PreparedStatement p=c.prepareStatement("insert into subject values(?,?,?,?);");
            HttpSession session=request.getSession();
            p.setString(1,request.getParameter("sid"));
            p.setString(2,request.getParameter("sname"));
            p.setString(3,request.getParameter("incharge"));
            p.setString(4,(String)session.getAttribute("dept_id"));
            p.execute();
        
	        out.print("<script>");
	        out.print("alert(\"Inserted\");");
	        out.print("window.location = \"hodindex.jsp\";");
	        out.print("</script>");
	        out.close();
            c.close();
	

        }catch(SQLException e){
            
            
            	out.print("<script>");
		out.print("alert(\"Already Exists\");");
		out.print("window.location = \"adminindex.html\";");
		out.print("</script>");
		out.close();
        }
	    catch(Exception e){
            
            out.print(e);
            out.close();
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
