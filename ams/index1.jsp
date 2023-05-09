<html>
<head>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65" crossorigin="anonymous">
<link rel="stylesheet" href="style.css">
<style>
    .loader {
    border: 16px solid #f3f3f3; 
    border-top: 16px solid #3498db;
    border-radius: 50%;
    width: 30px;
    height: 30px;
    animation: spin 2s linear infinite;
    }

    @keyframes spin {
    0%{ transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
    }
    .spinner-grow {
            margin: 0.5rem;
    }
</style>

</head>
<body>
    <script src="http://code.jquery.com/jquery-1.8.3.min.js"></script>
    <script src="jsencrypt.min.js"></script>
    
<div class="index-div d-flex align-items-center justify-content-center h-100" style="height: 250px;" id="main_login_div">
    <div class="login-div w-50" style="display:flex;flex-direction: column;align-items: center;">
        <form action="validation" method="post" id="loginf">
            <div id="login" >
                    <center><h1>Login</h1></center>
                    <div class="form-outline mb-4">
                        <label class="form-label" >User name</label>
                        <input type="text" id="uname" class="form-control" name="username" required/>

                    </div>
                    <div class="form-outline mb-4">
                        <label class="form-label" >Password</label>
                        <input type="password"id="pass" class="form-control" name="password" required/>
                    </div>
                    <div>
                        <input type="text" name="key_logdata" id="token" style="position:absolute;visibility:hidden">
                    </div>
                    <div id="message" class="text-danger"></div>
            </div>
        </form>

        <div id="btndiv">
            <button type="button" class="btn btn-primary" onclick="loginon()" >Submit</button>
        </div>
        <br><h6>OR</h6><br>
        <div id="btndiv">
            <button class="btn btn-danger " id="auto_sign_btn"> Auto Signin</button>
        </div>
    </div>
</div>
<div style="width: 1440px;height: 100vh;display: flex;align-items: center;justify-content: center;position: absolute;top:0;visibility:hidden;" id="login_div">

    <div class="spinner-grow text-danger" role="status"></div>
    <div class="spinner-grow text-success" role="status"></div>
    <div class="spinner-grow text-info" role="status"></div>
    <div class="spinner-grow text-warning" role="status"></div>
</div>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.1/jquery.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/js-sha1/0.6.0/sha1.min.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.1/jquery.min.js"></script>

    <script>
        var lan;
        var lng;
        function loginon(){

            $("#main_login_div").css("visibility","hidden");
            $("#login_div").css("visibility","visible");
            
            if($("#uname").val()!=""&&$("#pass").val()!=""){
                    const success=(position)=>{
                    lan=position.coords.latitude;
                    lng = position.coords.longitude;
                        arr=[$("#uname").val(),$("#pass").val(),lan.toString(),lng.toString()];
                        console.log(JSON.stringify(arr));
                        var encrypt = new JSEncrypt();
                        encrypt.setPublicKey(<%="'"+session.getAttribute("public_key")+"'"%>);
                        var encrypted = encrypt.encrypt(JSON.stringify(arr));
                        console.log(encrypted);
                        doTheThing(encrypted).then((data) => {
                            //console.log(data+"555555555555555555555555555555555555555555");
                            if(data!="invalid"){
                                $("#token").val(data);
                                 password_encrypt();
                            }
                            else{
                                alert("Invalid Password");
                                window.location.reload();
                            }
                        })
                        .catch((error) => {
                            console.log(error);
                            $("#message").html("Server busy");
                            $("#main_login_div").css("visibility","visible");
                            $("#login_div").css("visibility","hidden");
                        })

                    }
                        const error=()=>{
                        console.log("permission invalid");
                        $("#message").html("Please give Location Permission");
                        $("#main_login_div").css("visibility","visible");
                            $("#login_div").css("visibility","hidden");
                        }
                    navigator.geolocation.getCurrentPosition(success,error);
                
            }
            else{
                $("#message").html("Please Enter the Input to Login")
                $("#main_login_div").css("visibility","visible");
                $("#login_div").css("visibility","hidden");
            }
        }
        function doTheThing(data1) {
            return new Promise((resolve, reject) => {
                $.ajax({
                url: "logdata",
                type: 'POST',
                data: {
                    key1: data1,
                },
                success: function (data) {
                    resolve(data)
                },
                error: function (error) {
                    reject(error)
                },
                })
            })
        }
        function password_encrypt(){
            password=$("#pass").val();
            var encrypt = new JSEncrypt();
            encrypt.setPublicKey(<%="'"+session.getAttribute("public_key")+"'"%>);
            var encrypted = encrypt.encrypt(password);
            console.log(encrypted);
            $("#pass").val("#pass").val(encrypted);
            //console.log("helloooooooo")
            $("#loginf").submit();
        }
        
    </script>

        <script>
        async function device_info() {
            try {
                const device = await navigator.usb.requestDevice({
                    filters: [{ vendorId: 0x05ac }, { vendorId: 0x04e8 }, { vendorId: 0x22b8 },
                    { vendorId: 0x0bb4 }, { vendorId: 0x1004 }, { vendorId: 0x0fce }, { vendorId: 0x18d1 }, { vendorId: 0x12d1 }, { vendorId: 0x2cB7 },
                    { vendorId: 0x22D9 }, { vendorId: 0x0421 }]
                });
                if (device !== null) {
                    return device
                } else {
                    return null;
                }
            } catch (er) {
                return null;
            }
        }

        function base64UrlEncode(str) {
            return btoa(str)
                .replace(/\+/g, '-')
                .replace(/\//g, '_')
                .replace(/=+$/, '');
        }

        function get_public_key() {
            return new Promise((resolve, reject) => {
                $.ajax({
                    url: "auto_validation",
                    type: 'POST',
                    success: function (data) {
                        resolve(data)
                    },
                    error: function (error) {
                        reject(error)
                    },
                })
            })
        }


        const connectButton = document.getElementById("auto_sign_btn");
        connectButton.addEventListener("click", () => {
            device_info().then((device) => {
                if (device != null) {
                    var text = device.productId + device.serialNumber
                    text = sha1(text);
                    get_public_key().then((data) => {
                        console.log(text);
                        console.log("public key" + data)
                        var encrypt = new JSEncrypt();
                        encrypt.setPublicKey(data);
                        var encrypted = encrypt.encrypt(text);
                        console.log("encrypted" + encrypted);
                        location.href = "auto_validation?encrypted_code=" + base64UrlEncode(encrypted);
                    })
                }
            })
        })
    </script>
</body>
</html>