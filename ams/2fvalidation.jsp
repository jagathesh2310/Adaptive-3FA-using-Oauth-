<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta1/dist/css/bootstrap.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta1/dist/js/bootstrap.bundle.min.js"></script>
    <link rel="stylesheet" href="style.css">
    <style>
        .height-100 {
            height: 100vh
        }

        .card {
            width: 400px;
            border: none;
            height: 300px;
            background: #16222a; /* fallback for old browsers */
            background: -webkit-linear-gradient(to right, #16222a, #3a6073); 
            background: linear-gradient(to right, #16222a, #3a6073); 
            z-index: 1;
            display: flex;
            justify-content: center;
            align-items: center
        }

        .card h6 {
            color: #0B5ED7;
            font-size: 20px
        }

        .inputs input {
            width: 40px;
            height: 40px
        }

        input[type=number]::-webkit-inner-spin-button,
        input[type=number]::-webkit-outer-spin-button {
            -webkit-appearance: none;
            -moz-appearance: none;
            appearance: none;
            margin: 0
        }

        .card-2 {
           
            padding: 10px;
            width: 350px;
            height: 100px;
            bottom: -50px;
            left: 20px;
            position: absolute;
            border-radius: 5px
        }

        .card-2 .content {
            margin-top: 50px
        }

        .card-2 .content a {
            color: red
        }

        .form-control:focus {
            box-shadow: none;
            border: 2px solid red
        }

        .validate {
            border-radius: 20px;
            height: 40px;
            
            border: 1px solid red;
            width: 140px
        }
    </style>
</head>

<body>
    <form action="validation" method="get" id="otpform">
        <div class="container height-100 d-flex justify-content-center align-items-center">
            <div class="position-relative">
                <div class="card login-div p-2 text-center">
                    <h6>Please enter the one time password <br> to verify your account</h6>
                    <br>
                    <div> <span>A code has been sent to</span> <small>*******7058</small> </div>
                    <div id="otp" class="inputs d-flex flex-row justify-content-center mt-2" >
                         <input class="m-2 text-center form-control rounded otp_temp_input" type="text" id="first" maxlength="1" /> 
                         <input class="m-2 text-center form-control rounded otp_temp_input" type="text" id="second" maxlength="1"/> 
                         <input class="m-2 text-center form-control rounded otp_temp_input" type="text" id="third" maxlength="1" /> 
                         <input class="m-2 text-center form-control rounded otp_temp_input" type="text" id="fourth" maxlength="1"/> 
                         <input class="m-2 text-center form-control rounded otp_temp_input" type="text" id="fifth" maxlength="1" /> 
                         <input class="m-2 text-center form-control rounded otp_temp_input" type="text" id="sixth" maxlength="1" /> 
                         <input type="hidden" name="value" id="password">
                    </div>
                    <div class="message" id="message" style="color: red;"><%String message=request.getParameter("message");
                        if(message!=null){
                            out.print(message);
                        }%></div>
                    <div class="resend " style="cursor: pointer;color: green;" onclick="resend()">Resend Otp</div>
                    <div class="mt-4"> 
                        <button type="button" class="btn btn-primary px-4 validate" onclick="validate()">Validate</button> 
                    </div>
                </div>
            </div>
        </div>
    </form>
    <script>
        document.addEventListener("DOMContentLoaded", function (event) {

            function OTPInput() {
                const inputs = document.querySelectorAll('#otp > *[id]');
                for (let i = 0; i < inputs.length; i++) {
                    inputs[i].addEventListener('keydown', function (event) {
                        if (event.key === "Backspace") {
                            inputs[i].value = '';
                            if (i !== 0) inputs[i - 1].focus();
                        } else 
                        { 
                            if (i === inputs.length - 1 && inputs[i].value !== '') { 
                            return true; } 
                            else if (event.keyCode > 47 && event.keyCode < 58) {
                                 inputs[i].value = event.key;
                                  if (i !== inputs.length - 1) inputs[i + 1].focus(); event.preventDefault(); }
                                   
                                     }
                    });
                }
            } OTPInput();
        });
        function validate(){
            document.getElementById("message").innerHTML="";
            var inputs=document.getElementsByClassName("otp_temp_input");
            var raw_input="";
            for(let x=0;x<inputs.length;x++){
                if (typeof inputs[x].value !== "undefined"){
                raw_input+=inputs[x].value;
                }
            }
            if(raw_input.length==6){
                document.getElementById("password").value=raw_input;
                document.forms["otpform"].submit();

            }
            else{
                document.getElementById("message").innerHTML="Please fill the Input";
            }
        }
        
    </script>
     <script>
        
        function resend() {
            async function fetchText() {
                let response = await fetch('otpresend');
                let data = await response.text();
                return data;
            }
            fetchText().then((data) => {
                console.log(data);
            });

        }

        
    </script>
</body>

</html>