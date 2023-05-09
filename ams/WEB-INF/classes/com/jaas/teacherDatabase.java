package com.jaas;

import java.sql.*;

public class teacherDatabase {
    public static boolean getdata(String tid,String tpass){
        try{
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ams", "postgres", "admin");
            Statement s = c.createStatement();
            ResultSet r = s.executeQuery("select * from teacher;");
            while(r.next()){
                if(tid.equals(r.getString("tid"))&&tpass.equals(r.getString("password"))){
                    return true;
                }
            }
        }
        catch(Exception e){
            return false;
        }
        return false;

    }

    public static boolean getadmin(String id,String pass){
        try{
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ams", "postgres", "admin");
            PreparedStatement p=c.prepareStatement("select * from admin where username=? AND password=?");
            p.setString(1,id);
            p.setString(2,pass);
            ResultSet rs=p.executeQuery();
            if(rs.next()){
                return true;
            }
            c.close();
            return false;
        }catch(Exception e){
        System.out.println(e);
        return false;
        }

    }


    public static String getdatade(String tid){
        try{
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ams", "postgres", "admin");
            Statement s = c.createStatement();
            ResultSet r = s.executeQuery("select * from teacher where tid='"+tid+"';");
            while(r.next()){
                return r.getString("tdes");
            }
        }
        catch(Exception e){
            return "";
        }
        return "";

    }


}
