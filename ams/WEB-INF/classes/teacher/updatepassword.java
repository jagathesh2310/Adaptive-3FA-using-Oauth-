package teacher;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
public class updatepassword extends HttpServlet{
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws IOException{
        String curl="";
        HttpSession session=request.getSession();
        if(session.getAttribute("homehod")==null){
            curl=(String) session.getAttribute("home");
        }
        else{
            curl=(String) session.getAttribute("homehod");
        }

        response.setContentType("text/html");
        PrintWriter out=response.getWriter();
        if(request.getParameter("new_password")!=null && !request.getParameter("new_password").equals("")){
            try{
                Class.forName("org.postgresql.Driver");
                Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ams", "postgres", "admin");
                PreparedStatement p=c.prepareStatement("update teacher set password=?,modify=? where tid=?;");
                p.setString(1,request.getParameter("new_password"));
                LocalDate date=LocalDate.now();
                p.setString(2,date.toString());
                p.setString(3,(String)request.getRemoteUser());
                p.execute();
                out.print("<script>");
                out.print("alert(\"Updated\");");
                out.print("window.location = \""+curl+"\";");
                out.print("</script>");
                out.print(request.getParameter("did"));
                out.close();
                c.close();
        

            }catch(SQLException e){
                
                    out.print("<script>");
                out.print("alert(\"Invalid"+e+"\");");
                out.print("window.location = \""+curl+"\";");
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
            out.print("alert(\"Invalid\");");
            out.print("window.location = \""+curl+"\";");
            out.print("</script>");

        }

    }
    
}
