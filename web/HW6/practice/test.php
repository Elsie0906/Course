<html>
<head>
<script>
function showHint(str) {
    if (str.length == 0) { 
        document.getElementById("txtHint").innerHTML = "";
        return;
    } else {
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.onreadystatechange = function() {
            if (this.readyState == 4 && this.status == 200) {
                document.getElementById("txtHint").innerHTML = this.responseText;
            }
        };
        xmlhttp.open("GET", "test.php?q=" + str, true);
        xmlhttp.send();
    }
}
</script>
</head>
<body>

<?php if(isset($_REQUEST["q"])): ?>

<?php 
    $q = $_REQUEST["q"];
    echo $q;
?>    

<?php else: ?>

<p><b>Start typing a name in the input field below:</b></p>
<form> 
First name: <input type="text" onkeyup="showHint(this.value)">
</form>
<p>Suggestions: <span id="txtHint"></span></p>
<?php endif; ?> 
</body>
</html>
