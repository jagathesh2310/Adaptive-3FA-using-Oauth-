package com.jaas;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
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

import RSA.RSAUtil;





public class jaasLoginModule implements LoginModule {
    private CallbackHandler handler;
    private Subject subject;
    private String login;
    private List<String> userGroups;
    private UserPrincipal userPrincipal;
    private RolePrincipal rolePrincipal;
    private JSONArray arr;
    private String access_token=null;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,Map<String, ?> options) {
        handler = callbackHandler;
        this.subject = subject;
        
    }

    @Override
    public boolean login() throws LoginException {
        
        System.out.println("JAAS");
        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback("login");
        callbacks[1] = new PasswordCallback("password", true);
        try{
            
            handler.handle(callbacks);
            String name = ((NameCallback) callbacks[0]).getName();
            String password = String.valueOf(((PasswordCallback) callbacks[1]).getPassword());
            JSONParser parser = new JSONParser();
            
            password=new String(Base64.getUrlDecoder().decode(password));
            
            arr = (JSONArray) parser.parse(password);
            
            if(arr.size()==3){
            access_token=(String) arr.get(2);
            }
           
            
            String de_password = RSAUtil.decrypt((String)arr.get(0), RSAUtil.private_key);
            
            

            if (name != null &&name.equals("admin") &&!de_password.equals(null) && teacherDatabase.getadmin(name,de_password)){
                 login = name;
                userGroups = new ArrayList<String>();
                userGroups.add("admin");  
                return true;
            }
            else if(!de_password.equals(null)&&teacherDatabase.getdata(name, de_password)){
                if(teacherDatabase.getdatade(name).equals("HOD")){
                    login = name;
                    userGroups = new ArrayList<String>();
                    userGroups.add("hod");
                    return true;
                }
                else{
                    login = name;
                    userGroups = new ArrayList<String>();
                    userGroups.add("staff"); 
                    return true;
                }
                
            }
            throw new LoginException("Authentication invalid");
        }catch(Exception e){
            e.printStackTrace();
            throw new LoginException(e.getMessage());
        }
        
    }

    @Override
    public boolean commit() throws LoginException {
    utils.username=login;
    utils.erase_activity();
    userPrincipal = new UserPrincipal(login);
    subject.getPrincipals().add(userPrincipal);

    if (userGroups != null && userGroups.size() > 0) {
      for (String groupName : userGroups) {
  
        rolePrincipal = new RolePrincipal(groupName);
        subject.getPrincipals().add(rolePrincipal);
      }
    }

    if(access_token!=null){
        try {
            FileReader file=new FileReader("C:\\Program Files\\Apache Software Foundation\\Tomcat 10.1\\webapps\\ams\\data\\db.properties");
            Properties p=new Properties();
            p.load(file);
            String load_data_uri=p.getProperty("load_data");
            
            String public_key=utils.getPublickey(load_data_uri, access_token);
            String res=utils.load_data(access_token, public_key, load_data_uri);
            
        } catch (Exception e) {
            
            e.printStackTrace();
        }
    }


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

    
}
