<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Login</title>
    <link rel="stylesheet" href="style.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65" crossorigin="anonymous">
</head>
<body>
<%

    if(session.getAttribute("temp")==null){
        String curl= String.valueOf(request.getRequestURL());
        session.setAttribute("curl",curl);
        response.sendRedirect("/ams/logdata");
        
    }
    String submit="'allow'";
%>



<div class="index-div d-flex align-items-center justify-content-center h-100" style="height: 250px;" >
    <div class="login-div w-50">


        <div id="login" style="visibility:hidden;">
            

            <form action="j_security_check" method="post" id="loginform">
                <%String username= (String) session.getAttribute("username");
                String password;
                if(request.getParameter("password")==null){
                    password= (String) session.getAttribute("password");
                }
                else{
                    password= (String) request.getParameter("password");
                }
                %>

                <div >
                    <div class="form-outline mb-4">
                        <label class="form-label" >User name</label>
                        <input type="text" id="uname" class="form-control" value=<%=username%> name="j_username" required/>

                    </div>
                    <div class="form-outline mb-4">
                        <label class="form-label" >Password</label>
                        <input type="password"id="pass" class="form-control" value=<%=password%> name="j_password" required/>
                    </div>
                </div>
                <button >submit</button>
            </form>
        </div>
    </div>
</div>
<script>
    if(<%=submit%>=="allow"){
    document.forms["loginform"].submit();
    }
</script>
</body>