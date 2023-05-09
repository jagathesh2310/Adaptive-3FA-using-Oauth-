<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <link rel="stylesheet" href="../../style.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css"
        integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65" crossorigin="anonymous">
</head>

<body>
    <div class="d-flex justify-content-end m-5">
        <button class="btn btn-info " onclick="window.location='../../invalid'">Logout</button>
    </div>
    <button class="btn btn-primary" onclick="loadhome()">Home</button>
    <center>
        <div class="login-div w-50">
            <form action="attendancedata" method="post">
                <center><input type="date" name="date" required placeholder="Date"></center>
                <input type="text" id="data1" name="data" style="visibility:hidden;">
                <input type="text" id="su_id" name="su_id" style="visibility:hidden;">
                <div>
                    <table id="deptdata" class="w-100"></table>
                </div>
                <button class="btn btn-primary">Submit</button>
            </form>
        </div>
        
    </center>

</body>
<script>
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const sub_id = urlParams.get('sub_id');
    document.getElementById("su_id").value = sub_id;
    console.log(sub_id)
    var tempdata = null;
    async function fetchText() {
        let response = await fetch('attendance_res?sub_id=' + sub_id);
        let data = await response.json();
        return data;
    }
    fetchText().then((data) => {
        tempdata = data;
        document.getElementById("deptdata").innerHTML = "<tr><th>Student ID</th><th>Student Name</th><th colspan='2'>Options</th></tr>";
        for (let x = 0; x < data.length; x++) {
            document.getElementById("deptdata").innerHTML += "<tr><td>" + data[x][0] + "</td><td>" + data[x][1] + "</td>" + "<td><input type='radio' id='id01' name='" + data[x][0] + "' value='p' checked><label for='id01'>Present</label><input type='radio' id='id01' name='" + data[x][0] + "' value='a'><label for='id01'>Absent</label></td>";
            console.log(data[0]);
        }
        document.getElementById("data1").value = JSON.stringify(tempdata);


    });

    function loadhome() {
        let urlhome =<%="'" + (String)session.getAttribute("home") + "'" %>;
        let urlhomehod =<%="'" + (String)session.getAttribute("homehod") + "'" %>;
        if (urlhomehod != "null") {
            location = urlhomehod;
        }
        else {
            location = urlhome;
        }
    }
</script>

</html>