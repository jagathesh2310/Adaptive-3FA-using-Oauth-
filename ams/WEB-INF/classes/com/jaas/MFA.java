package com.jaas;


import java.io.FileReader;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.Properties;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import RSA.RSAUtil;

public class MFA implements LoginModule {
    private CallbackHandler handler;
    private Subject subject;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
            Map<String, ?> options) {
        handler = callbackHandler;
        this.subject = subject;

    }

    @Override
    public boolean login() throws LoginException {
        
        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback("login");
        callbacks[1] = new PasswordCallback("password", true);
        
        try {
            
            FileReader reader = new FileReader("C:\\Program Files\\Apache Software Foundation\\Tomcat 10.1\\webapps\\ams\\data\\db.properties");
            Properties p=new Properties();  
            p.load(reader);  
            String client_secret=p.getProperty("CLIENT_SECRET");
            handler.handle(callbacks);
            String name = ((NameCallback) callbacks[0]).getName();

            String password = String.valueOf(((PasswordCallback) callbacks[1]).getPassword());
            password=new String(Base64.getUrlDecoder().decode(password));
            JSONParser parser = new JSONParser();
            
            JSONArray arr = (JSONArray) parser.parse(password);
           
            
            String token = (String) arr.get(1);
            

            if (isValidAccessToken(token, client_secret,name)) {
                
                
                return true;
            }

            throw new LoginException("Authentication invalid");
        } catch (Exception e) {
            throw new LoginException(e.getMessage());
        }

    }

    @Override
    public boolean commit() throws LoginException {
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        return false;
    }

    @Override
    public boolean logout() throws LoginException {
        return false;
    }

    private boolean isValidAccessToken(String accessToken, String secret,String username) {

        try {
            Claims claims=Jwts.parser().setSigningKey(secret).parseClaimsJws(accessToken).getBody();
       
            
            String user=(String) claims.get("sub");
            
            if(user.equals(username)){
                
                if(claims.get("risk").equals("high_risk")&&claims.get("2fa").equals("allow")&&claims.get("duo_auth").equals("allow")){
                    
                    return true;
                }
                else if(claims.get("risk").equals("medium_risk")&&claims.get("2fa").equals("allow")){
                    return true;
                }
                else if(claims.get("risk").equals("low_risk")){
                    
                    return true;
                }
                
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        System.out.println("error occured...............");
        return false;

    }
}
