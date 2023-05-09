<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65" crossorigin="anonymous">
    <link rel="stylesheet" href="../style.css">
</head>
<body>
<div class="d-flex justify-content-end m-5">
        <button class="btn btn-info "onclick="window.location='../invalid'">Logout</button>
    </div>
    <div class="index-div d-flex align-items-center justify-content-center h-100" style="height: 250px;">
        <div class="login-div w-50">
            <h1>Update Teacher</h1><br><br>
            <form action="updateteacher" method="post">
                <div class="form-outline mb-4">
                    <label class="form-label" for="dname" >Teacher Name</label>
                    <input type="text" id="dname" class="form-control" name="tname" value=<%= request.getParameter("tname")%> required>
                </div>
                <div class="form-outline mb-4">
                    <label class="form-label" for="dname" >Teacher Designation</label>
                    <input type="text" id="dname" class="form-control" name="tdes" value=<%= request.getParameter("tdes")%> required>
                </div>
                <input type="text" id="tdid" class="form-control" name="tdid" value=<%= request.getParameter("tdid")%>required>

                <div id="deptdata"class="form-outline mb-4"></div>
                
                  <div class="form-outline mb-4" style="visibility:hidden;">
                    <input type="text" id="tid" class="form-control" name="tid" value=<%= request.getParameter("tid")%> required>
                  </div>
                  <div class="form-outline mb-4">
                    <label class="form-label" for="dname" >Password</label>
                    <input type="pass" id="did" class="form-control" name="pass" value=<%= request.getParameter("pass")%> required>
                  </div>
                  <center><button class="btn btn-primary">Update</button></center>
            </form>
        </div>
    </div>
    <script>
        async function fetchText() {
        let response = await fetch('viewdept');
        let data = await response.json();
        return data;
    }
    fetchText().then((data) => {
        tempdata=data;
        var s="<label for='dept'>Choose a Department:</label><select name='tdid'>"
            did=<%=request.getParameter("tdid")%>;
            dname=<%=request.getParameter("tdname")%>;
            
            s+="<option value='"+did+"'>"+dname+"</option>";
            for (let x=0;x<data.length;x++) {
            
            s+="<option value='"+data[x][0]+"'>"+data[x][1]+"</option>";
            console.log(data[0]);
        }
    
        document.getElementById("deptdata").innerHTML=s+"</select>";
    });
    </script>
</body>
</html>