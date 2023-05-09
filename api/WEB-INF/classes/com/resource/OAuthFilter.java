package com.resource;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultJwtParser;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class OAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
                
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String authorizationHeader = httpRequest.getHeader("Authorization");
        if (authorizationHeader != null) {
            String[] parts = authorizationHeader.split(" ");
            if (parts.length == 2 && "Bearer".equals(parts[0])) {
                String accessToken = parts[1];
                Claims valid=isValidAccessToken(accessToken);
                if (valid!=null) {
                    
                    request.setAttribute("token", valid);
                   
                    chain.doFilter(request, response);
                   
                   
                    return;
                }
            }
        }
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setContentType("application/json");
        httpResponse.setStatus(401);
        httpResponse.getWriter().write("{" +
                "\"error\": \"invalid_token\"" +
                "}");
    }

    private Claims isValidAccessToken(String accessToken) {
        

        try {
            String token = accessToken;
            String[] splitToken = token.split("\\.");
            String unsignedToken = splitToken[0] + "." + splitToken[1] + ".";
            DefaultJwtParser parser = new DefaultJwtParser();
            Jwt<?, ?> jwt = parser.parse(unsignedToken);
            Claims claims1 = (Claims) jwt.getBody();
            String client_id = (String) claims1.get("CLIENT_ID");
            String username = (String) claims1.get("username");
            String client_secret = utils.getClientSecret(client_id);
            if(!utils.validate_db_token(client_id,accessToken,username)){
                System.out.println("invalid access token from filter");
                return null;
            }
            
            if (!client_secret.equalsIgnoreCase("deny")) {
                
                Claims c=Jwts.parser().setSigningKey(client_secret).parseClaimsJws(accessToken).getBody();
                if(c.get("scope").equals("adaptivemfa")&&c.getAudience().equals("http://localhost:8080/api/resource/*")&&c.get("token_type").equals("access_token")){
                    
                    
                    return c;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
       
        return null;

    }

    
}


