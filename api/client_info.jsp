<!DOCTYPE html>
<html>

<head>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css">
    <style>
        .h-100 {
            height: 100vh !important;
        }

        .w-100 {
            width: 100%;
        }
        .s_div{
            box-shadow: rgba(255, 255, 255, 0.1) 0px 1px 1px 0px inset, rgba(50, 50, 93, 0.25) 0px 50px 100px -20px, rgba(0, 0, 0, 0.3) 0px 30px 60px -30px;
            padding: 2rem;
        }
        td{
            background-color:black !important;
            color:white !important;
        }
    </style>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>
</head>

<body>
    <div class="container h-100 d-flex align-items-center">
        <div class="row w-100 s_div">
            <div class="col-lg-6 mx-auto">
                <h2 class="text-center mb-4">Client Info</h2>
                <table class="table">
                    <tbody>
                        <tr class="table-info">
                        <th>Organization Name</th>
                        <td><%=request.getParameter("organization")%></td>
                        </tr>
                        <tr class="table-info">
                        <th>Client ID</th>
                        <td><%=request.getParameter("client_id")%></td>
                        </tr>
                        <tr class="table-info">
                        <th>Client_Secret</th>
                        <td><%=request.getParameter("client_secret")%></td>
                        </tr>
                        <tr class="table-info">
                        <th>Access Token API</th>
                        <td><%=request.getParameter("api_host")%></td>

                        </tr>
                        <tr class="table-info">
                        <th>Adaptive Validation URI</th>
                         <td><%=request.getParameter("adaptive_uri")%></td>
                        </tr>

                        
                        <tr class="table-info">
                        
                        <th>Otp Response URI</th>
                         <td><%=request.getParameter("otpresponse_uri")%></td>
                        </tr>

                        
                        <tr class="table-info">
                        <th>Otp Validation API</th>
                         <td><%=request.getParameter("otpvalidation_uri")%></td>
                        </tr>

                        
                        <tr class="table-info">
                        <th>Duo auth API</th>
                         <td><%=request.getParameter("duo_auth")%></td>
                        </tr>

                        <tr class="table-info">
                        <th>Load Data API</th>
                         <td><%=request.getParameter("load_data")%></td>
                        </tr>
                    </tbody>
                    </table>
            </div>
        </div>
    </div>
</body>

</html>