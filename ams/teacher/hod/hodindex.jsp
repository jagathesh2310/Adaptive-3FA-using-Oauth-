<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <link rel="stylesheet" href="../../style.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65" crossorigin="anonymous">
    <script src="//code.jquery.com/jquery-1.10.2.js"></script>
    <script src="../../jsencrypt.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/js-sha1/0.6.0/sha1.min.js"></script>
    
    
    <style>
        table,
        th,
        td {
            border: 1px solid;
            padding: 10px;
        }
    </style>
</head>

<body>
    <%=session.getAttribute("dept_id")%>
        <%String curl=String.valueOf(request.getRequestURL()); session.setAttribute("home",curl);
            session.setAttribute("homehod",curl);%>
            <div id="main_div">
                <div class="d-flex justify-content-end m-5">
                    <button class="btn btn-info " onclick="window.location='../../invalid'">Logout</button>
                </div>
                <div>
                    <button class="btn btn-warning" style="margin: 2rem;"
                        onclick="window.location='../passwordchange.html'">Change my Password</button>
                </div>
                <div style="display: flex;" class="p-2">
                    <h3 class="m-4">Auto Sign in : </h3>
                    <div class="toggle-switch m-4">

                        <input type="checkbox" class="toggle-switch-checkbox" id="switch">

                        <label class="toggle-switch-label" for="switch">
                            <span class="toggle-switch-inner"></span>
                            <span class="toggle-switch-switch"></span>
                        </label>
                    </div>
                </div>
                <center>
                    <div class="" style="height: 250px;">
                        <div class="login-div">
                            <a href="addstudent.html">
                                <button class="btn btn-primary">Add Student</button>
                            </a>
                            <a href="addsubject.html">
                                <button class="btn btn-primary">Add Subject</button>
                            </a>

                            <h1>Subject Should be Taken</h1>
                            <div class="form-outline mb-4">
                                <div id="subdata"></div>
                            </div>
                            <div id="subdata1"></div>


                        </div>
                    </div>
                </center>

            </div>
            <div id="sub_div" style="visibility: hidden;">
                <center>
                    <div class="index-div d-flex align-items-center justify-content-center h-100"
                        style="height: 250px;width:1519px;position: absolute;top: 0;" id="main_login_div">
        
                        <div class="login-div w-50" style="display:flex;flex-direction: column;align-items: center;">
                            <form action="../../auto/enable_disable" method="post">
                                <center>
                                    <h1>Confirm Your to Update Auto Sign in</h1>
                                </center><br><br>
        
                                <div class="form-outline mb-4">
                                    <label class="form-label">Password</label>
                                    <input type="password" id="pass" class="form-control" name="password" required />
                                </div>
                                <input type="hidden" name="device_code" id="device_code">
                                <input type="hidden" name="connection" id="connection">
        
                                <div id="btndiv">
                                    <button type="submit" class="btn btn-primary">Submit</button>
                                </div>
                            </form>
                        </div>
        
                    </div>
                </center>
            </div>
            <script>
                var s1 =<%="'" + (String)session.getAttribute("tid") + "'" %>
                    async function fetchText() {
                        let response = await fetch('../staff/subjectview?tid=' + s1);
                        let data = await response.json();
                        return data;
                    }
                fetchText().then((data) => {
                    var s = "<table><tr><th>Subject Id</th><th>Subject Name</th><th>Options</th></tr>";
                    console.log(data);
                    console.log(s1);
                    for (let x = 0; x < data.length; x++) {
                        s += "<tr><td>" + data[x][0] + "</td><td>" + data[x][1] + "</td><td><button class=\"btn btn-success\" onclick='location=\"../staff/attendance.jsp?sub_id=" + data[x][0] + "\"''>Attendance</button>&emsp;<button class=\"btn btn-info\" onclick='loadprevious(\"" + data[x][0] + "\")'>Previous Attendance</button></td></tr>";
                    }

                    document.getElementById("subdata").innerHTML = s + "</table>";
                });

                function loadprevious(subject_id) {
                    console.log(subject_id);
                    async function fetchText() {
                        let response = await fetch('../staff/previousattendance?subject_id=' + subject_id);
                        let data = await response.json();
                        return data;
                    }
                    fetchText().then((data) => {
                        var s = "<h2>Previous Attendance</h2><br><table><tr><th>Date</th><th>Student_id</th><th>Student Name</th><th>Status</th></tr>";
                        console.log(data);
                        console.log(s1);
                        let temp = data[0][0];
                        for (let x = 0; x < data.length; x++) {
                            if (temp != data[x][0]) {
                                s += "</table><br><br><table><tr><th>Date</th><th>Student_id</th><th>Student Name</th><th>Status</th></tr>"
                                temp = data[x][0];
                            }
                            s += "<tr><td>" + data[x][0] + "</td><td>" + data[x][1] + "</td><td>" + data[x][4] + "</td><td>" + data[x][3] + "</td></tr>";
                        }

                        document.getElementById("subdata1").innerHTML = s + "</table>";
                    });
                }
            </script>

            <script>
                window.onload = () => {
                    const params = new URLSearchParams(window.location.search)
                    let auto_status = params.get('auto_status');
                    if (auto_status == "enable") {
                        document.getElementById("switch").checked = true;
                    }
                    else {
                        document.getElementById("switch").checked = false;
                    }
                    let auto_message=params.get('auto_message');
                    if(auto_message!=null){
                    alert(auto_message)
            }
                }
                document.getElementById("switch").addEventListener("click", () => {
                    var check_box = document.getElementById("switch");
                    console.log(check_box.checked);
                    if (check_box.checked == true) {
                        device_info().then((device) => {
                            if (device != null) {
                                get_public_key().then((public_key) => {
                                    console.log(device)
                                    var text = device.productId + device.serialNumber
                                    text = sha1(text);
                                    var encrypt = new JSEncrypt();
                                    encrypt.setPublicKey(public_key);
                                    var encrypted = encrypt.encrypt(text);
                                    $("#device_code").val(encrypted);
                                    $("#connection").val("enable");
                                    $("#main_div").css("visibility", "hidden");
                                    $("#sub_div").css("visibility", "visible");
                                    console.log("connection")
                                })


                            }
                            else {
                                check_box.checked = false;
                            }

                        })

                    }
                    else {
                        $("#connection").val("disable");
                        $("#main_div").css("visibility", "hidden");
                        $("#sub_div").css("visibility", "visible");
                        console.log("connection")

                    }
                })


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
                        console.log(er.message);
                        return null;
                    }
                }


                function get_public_key() {
                    return new Promise((resolve, reject) => {
                        $.ajax({
                            url: "../../auto/enable_disable",
                            type: 'GET',
                            success: function (data) {
                                resolve(data)
                            },
                            error: function (error) {
                                reject(error)
                            },
                        })
                    })
                }
            </script>



</body>

</html>