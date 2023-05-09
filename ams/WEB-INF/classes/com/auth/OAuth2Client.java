package com.auth;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import AES.AESUtil;
import RSA.RSAUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class OAuth2Client {

    private static String AUTHORIZATION_SERVER_URL;
    private static String CLIENT_ID;
    public static String CLIENT_SECRET;

    private static String adaptive_uri;
    private static String otpresponse_uri = "";
    private static String otpvalidation_uri = "";
    private static String duo_auth = "";
    private static String temp_access_token = "";

    public static void initialize_info() {
        try {
            FileReader reader = new FileReader(
                    "C:\\Program Files\\Apache Software Foundation\\Tomcat 10.1\\webapps\\ams\\data\\db.properties");
            Properties p = new Properties();
            p.load(reader);
            CLIENT_ID = p.getProperty("CLIENT_ID");
            CLIENT_SECRET = p.getProperty("CLIENT_SECRET");

            AUTHORIZATION_SERVER_URL = p.getProperty("AUTHORIZATION_SERVER_URL");
            adaptive_uri = p.getProperty("adaptive_uri");
            otpresponse_uri = p.getProperty("otpresponse_uri");
            otpvalidation_uri = p.getProperty("otpvalidation_uri");
            duo_auth = p.getProperty("duo_auth");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getOldToken(String username) {
        initialize_info();

        try {
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ams", "postgres", "admin");
            if (username.equals("admin")) {
                Statement statement = c.createStatement();
                ResultSet resultSet = statement.executeQuery("select * from admin;");
                if (resultSet.next()) {
                    if (resultSet.getString("access_token") == null || resultSet.getString("refresh_token") == null) {
                        return null;
                    } else {
                        if (isValidToken(resultSet.getString("access_token"), CLIENT_SECRET)) {
                            return resultSet.getString("access_token");
                        } else if (isValidToken(resultSet.getString("refresh_token"), CLIENT_SECRET)) {
                            String access_token = getAccessToken(resultSet.getString("refresh_token"), CLIENT_ID,
                                    CLIENT_SECRET);
                            utils.update_access_token(access_token, username);
                            return access_token;
                        }
                    }
                }
            } else {

                PreparedStatement p = c.prepareStatement("select * from teacher where tid=?");
                p.setString(1, username);
                ResultSet resultSet = p.executeQuery();
                while (resultSet.next()) {
                    if (resultSet.getString("access_token") == null || resultSet.getString("refresh_token") == null) {
                        return null;
                    } else {
                        if (isValidToken(resultSet.getString("access_token"), CLIENT_SECRET)) {

                            return resultSet.getString("access_token");
                        } else if (isValidToken(resultSet.getString("refresh_token"), CLIENT_SECRET)) {

                            String access_token = getAccessToken(resultSet.getString("refresh_token"), CLIENT_ID,
                                    CLIENT_SECRET);
                            utils.update_access_token(access_token, username);
                            return access_token;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static boolean isValidToken(String access_token, String client_secret) {
        try {
            Claims s = Jwts.parser().setSigningKey(client_secret).parseClaimsJws(access_token).getBody();
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public static String getAccessToken(String refresh_token, String client_id, String client_secret) {
        initialize_info();
        try {
            String Public_key = getPublickey("http://localhost:8080/api/ValidationServer");
            if(Public_key==null){
                return null;
            }
            byte[] en = RSAUtil.encrypt(client_secret, Public_key);
            String encrypted_secret = Base64.getUrlEncoder().encodeToString(en);

            String authorizationUrl = AUTHORIZATION_SERVER_URL + "?" + "CLIENT_ID=" + client_id + "&CLIENT_SECRET="
                    + encrypted_secret + "&grant_type=" + "refresh_token" + "&refresh_token=" + refresh_token;
            URL url = new URL(authorizationUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                return content.toString();
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getAuthorizationToken(String username) throws IOException {
        initialize_info();
        try {
            String Public_key = getPublickey("http://localhost:8080/api/ValidationServer");
            byte[] en = RSAUtil.encrypt(CLIENT_SECRET, Public_key);
            String encrypted_secret = Base64.getUrlEncoder().encodeToString(en);

            String authorizationUrl = AUTHORIZATION_SERVER_URL + "?" + "CLIENT_ID=" + CLIENT_ID + "&CLIENT_SECRET="
                    + encrypted_secret + "&grant_type=" + "client_credentials" + "&scope=adaptivemfa" + "&username="
                    + username;

            URL url = new URL(authorizationUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            int responseCode = con.getResponseCode();

            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(content.toString());
                String access_token = (String) json.get("access_token");
                String refresh_token = (String) json.get("refresh_token");
                utils.update_user_tokens(username, access_token, refresh_token);

                return access_token;
            } else {
                return "bad_request";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "bad_request";
    }

    public static String Validate_Adaptive(String log_data, String token) {
        try {

            String Public_key = getPublickey("http://localhost:8080/api/resource/adaptive", token);
            if(Public_key==null){
                return null;
            }

            String symmetric_key = AESUtil.key_generator();
            String en_log_data = AESUtil.encrypt(log_data, symmetric_key);
            String en_symmetric_key = Base64.getUrlEncoder().encodeToString(RSAUtil.encrypt(symmetric_key, Public_key));
            URL url = new URL(adaptive_uri + "?log_data=" + en_log_data + "&en_symmetric_key=" + en_symmetric_key);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer " + token);
            con.setDoOutput(true);
            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                return content.toString();
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPublickey(String url, String token) throws IOException {
        URL resourceUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) resourceUrl.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + token);
        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();
        }
        return null;

    }

    public static String getPublickey(String url) throws IOException {
        URL resourceUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) resourceUrl.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Get_public_key");
        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();
        }
        return null;

    }

    public static String otprequest(String token) {
        initialize_info();
        temp_access_token = token;
        try {
            URL resourceUrl = new URL(otpresponse_uri);
            HttpURLConnection connection = (HttpURLConnection) resourceUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + token);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String otprequest() {
        String token = temp_access_token;
        initialize_info();
        try {
            URL resourceUrl = new URL(otpresponse_uri);
            HttpURLConnection connection = (HttpURLConnection) resourceUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + token);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String otpvalidation(String token, String value, String temp_token) {
        initialize_info();
        try {
            URL resourceUrl = new URL(otpvalidation_uri + "?value=" + value + "&temp_token=" + temp_token);
            HttpURLConnection connection = (HttpURLConnection) resourceUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            int status_code = connection.getResponseCode();
            if (status_code == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();

            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String get_duo_auth_uri(String token, String redirect_uri_duo) {
        initialize_info();
        try {
            URL resourceUrl = new URL(duo_auth + "?redirect_uri_duo=" + redirect_uri_duo);
            HttpURLConnection connection = (HttpURLConnection) resourceUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            int status_code = connection.getResponseCode();

            if (status_code == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();

            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String get_duo_auth_validation(String token, String code, String state, String temp_token) {
        initialize_info();
        try {
            URL resourceUrl = new URL(duo_auth + "?duo_code=" + code + "&state=" + state + "&temp_token=" + temp_token);
            HttpURLConnection connection = (HttpURLConnection) resourceUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            int status_code = connection.getResponseCode();

            if (status_code == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();

            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

}