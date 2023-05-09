import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Base64;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class client_registeration extends HttpServlet{
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws IOException{
        String organization=request.getParameter("organization");
        String email=request.getParameter("email");
        String password=request.getParameter("password");
        
        String client_id=client_id_generator();
        String client_secret=client_secret_generator();
        loaddb(client_id, client_secret, organization, email, password);
        String data="?client_id="+client_id+"&client_secret="+client_secret+"&organization="+organization+"&api_host="+"http://localhost:8080/api/ValidationServer"+"&adaptive_uri=http://localhost:8080/api/resource/adaptive&otpresponse_uri=http://localhost:8080/api/resource/otpresponse&otpvalidation_uri=http://localhost:8080/api/resource/otpvalidation&duo_auth=http://localhost:8080/api/resource/duo_auth&load_data=http://localhost:8080/api/resource/load_data";
        response.sendRedirect("client_info.jsp"+data);

    }
    public String client_id_generator(){
        String client_id;
        SecureRandom random1 = new SecureRandom();
        byte[] bytes1 = new byte[16];
        random1.nextBytes(bytes1);
        client_id=Base64.getUrlEncoder().encodeToString(bytes1);
        return client_id;
    }
    public String client_secret_generator(){
        SecureRandom random = new SecureRandom();
        byte[] secretBytes = new byte[32];
        random.nextBytes(secretBytes);
        return Base64.getUrlEncoder().encodeToString(secretBytes);
    }
    public void loaddb(String client_id,String client_secret,String organization,String email,String password){
        try{

                Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/api", "postgres", "admin");
                PreparedStatement p = c.prepareStatement("insert into client_info values(?,?,?,?,?,?)");
                p.setString(1, client_id);
                p.setString(2, client_secret);
                p.setString(3, organization);
                p.setString(4,email);
                p.setString(5, password);
                p.setString(6,"adaptivemfa");
                p.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}