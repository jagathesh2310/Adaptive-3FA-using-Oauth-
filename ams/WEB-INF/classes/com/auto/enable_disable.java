package com.auto;

import java.beans.Statement;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import RSA.RSAUtil;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class enable_disable extends HttpServlet {
    String public_key;
    String private_key;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            RSAUtil.initialize();
            private_key = RSAUtil.private_key;
            public_key = RSAUtil.public_key;

        } catch (Exception e) {
            e.printStackTrace();
        }

        response.getWriter().write(public_key);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {

            String username = request.getRemoteUser();
            String password = request.getParameter("password");
            if (request.getParameter("connection").equalsIgnoreCase("enable")) {
                String device_code = request.getParameter("device_code");
                String decrypted_code = RSAUtil.decrypt(device_code, private_key);
                device_code = decrypted_code;
                if (!already_exit(device_code)) {
                    if (enable_update_db(username, password, device_code)) {
                        System.out.println("enabled");
                        if (username.equals("admin")) {
                            response.sendRedirect("../admin/adminlogin");
                        } else {
                            response.sendRedirect("../teacher/teacherlogin");
                        }

                    } else {
                        System.out.println("not enabled");
                        if (username.equals("admin")) {
                            response.sendRedirect("../admin/adminlogin?auto_message=Not Enabled Due to Your password or something Wrong");
                        } else {
                            response.sendRedirect("../teacher/teacherlogin?auto_message=Not Enabled Due to Your password or something Wrong");
                        }

                    }
                }
                else{
                    System.out.println("Already exit");
                    if (username.equals("admin")) {
                        response.sendRedirect("../admin/adminlogin?auto_message=Device Already Exists");
                    } else {
                        response.sendRedirect("../teacher/teacherlogin?auto_message=Device Already Exists");
                    }
                }

            } else {
                if (disable_update_db(username, password)) {
                    System.out.println("Disabled");
                    if (username.equals("admin")) {
                        response.sendRedirect("../admin/adminlogin");
                    } else {
                        response.sendRedirect("../teacher/teacherlogin");
                    }

                } else {
                    System.out.println("not disabled");
                    if (username.equals("admin")) {
                        response.sendRedirect("../admin/adminlogin?auto_message=Not Disabled Due to Your password or something Wrong");
                    } else {
                        response.sendRedirect("../teacher/teacherlogin?auto_message=Not Disabled Due to Your password or something Wrong");
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean enable_update_db(String username, String password, String device_code) {
        if (username.equals("admin")) {
            try {
                Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ams", "postgres", "admin");
                PreparedStatement p = c.prepareStatement("select * from admin where username=? and password=?");
                p.setString(1, username);
                p.setString(2, password);
                ResultSet r = p.executeQuery();
                if (r.next()) {
                    PreparedStatement p1 = c
                            .prepareStatement("update admin set device_hash=?,auto_status=? where username=?;");
                    p1.setString(1, device_code);
                    p1.setString(2, "enable");
                    p1.setString(3, username);
                    if (p1.executeUpdate() == 1) {
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            try {
                Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ams", "postgres", "admin");
                PreparedStatement p = c.prepareStatement("select * from teacher where tid=? and password=?");
                p.setString(1, username);
                p.setString(2, password);
                ResultSet r = p.executeQuery();
                if (r.next()) {
                    PreparedStatement p1 = c
                            .prepareStatement("update teacher set device_hash=?,auto_status=? where tid=?;");
                    p1.setString(1, device_code);
                    p1.setString(2, "enable");
                    p1.setString(3, username);
                    if (p1.executeUpdate() == 1) {
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean already_exit(String device_code) {
        try {
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ams", "postgres", "admin");
            PreparedStatement p = c.prepareStatement("select * from admin where device_hash=?");
            p.setString(1, device_code);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                return true;
            } else {
                PreparedStatement p1 = c.prepareStatement("select * from teacher where device_hash=?");
                p1.setString(1, device_code);
                ResultSet r1 = p1.executeQuery();
                if (r1.next()) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean disable_update_db(String username, String password) {
        if (username.equals("admin")) {

            try {
                Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ams", "postgres", "admin");
                PreparedStatement p = c.prepareStatement("select * from admin where username=? and password=?");
                p.setString(1, username);
                p.setString(2, password);
                ResultSet r = p.executeQuery();
                if (r.next()) {

                    PreparedStatement p1 = c
                            .prepareStatement("update admin set device_hash=?,auto_status=? where username=?;");
                    p1.setString(1, null);
                    p1.setString(2, "disable");
                    p1.setString(3, username);
                    int p10 = p1.executeUpdate();

                    if (p10 == 1) {
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {

            try {
                Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ams", "postgres", "admin");
                PreparedStatement p = c.prepareStatement("select * from teacher where tid=? and password=?");
                p.setString(1, username);
                p.setString(2, password);
                ResultSet r = p.executeQuery();
                if (r.next()) {

                    PreparedStatement p1 = c
                            .prepareStatement("update teacher set device_hash=?,auto_status=? where tid=?;");
                    p1.setString(1, null);
                    p1.setString(2, "disable");
                    p1.setString(3, username);
                    int p10 = p1.executeUpdate();

                    if (p10 == 1) {
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

}
