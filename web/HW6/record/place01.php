<?php if(isset($_REQUEST["q"])): ?>
    <?php 
        $q = $_REQUEST["q"];
        list($addr, $loc, $radius, $type, $keyword) = explode("$",$q);
        echo getSearch($addr, $loc, $radius, $type, $keyword);
    ?>
<?php else: ?>

<!DOCTYPE HTML>
<html>
<head>
<style>
    .placeForm{
        margin: auto;
        margin-top: 50px;
        width: 500px;
        background-color: #F3F3F3;
        border:1px solid #ddd;
    }
    h1{
        text-align: center;
        font-size: 32px;
        border-bottom: 1px solid #808080;
        margin-top: 0px;
    }  
    table{
        border-collapse: collapse;
    }
    #xmlTable th{
        border: 1px solid black;
        text-align: center;
        width: 450px;
    }
    #xmlTable td{        
        border: 1px solid black;
    }
    #xmlTable .hIcon{
        text-align: center;
        width: 20px;
    }     
</style>
<script type="text/javascript">
    function getProperty(){

        var param = "";               

        if( document.myForm.location.value == "Here"){
            param += "address=Here"+"$"+"location=" + document.myForm.lat.value + "," + document.myForm.lon.value;                
        }
        else{
            var address = encodeURI(document.myForm.in_loc.value);
            param += "address=" + address + "$" + "location="; 
        }

        param += "$";

        if( document.myForm.in_dist.value == ""){
            param += "radius=10";
        }
        else{
            param += "radius=" + document.myForm.in_dist.value;
        }    

        param += "$";

        param += "type=" + document.myForm.in_category.value;

        param += "$";

        var keyword = encodeURI(document.myForm.in_key.value);
        param += "keyword=" + keyword;  

        return param;          
    }
    function placeDetail(what){
        var text = "";
        text += "This is a test";
        console.log(what)
        document.getElementById("demo").innerHTML = text; 
    }   
    function getSearchResult(jsonObj){   

        var text = "";  

        if( jsonObj.status == "ZERO_RESULTS"){
            text = "<table align='center' id='xmlTable'>";
            text += "<tr><th>No Records has been found</th></tr>"
            text += "</table>";
        }   
        else{
            text = "<table align='center' id='xmlTable'>";
            text += "<tr><th class='hIcon'>Category</th><th>Name</th><th>Address</th></tr>";

            var c = jsonObj.results;

            for(var i=0; i<c.length; i++){
                var icon = c[i].icon;
                var name = c[i].name;
                var addr = c[i].vicinity;
                var place = c[i].place_id;
                var comp_key = name + "&" + place;

                text += "<tr>";
                text += "<td class='hIcon'><img src='"+ icon +"' alt='IconImg' width='40%' height='40%'></td>";
                text += "<td id='"+ comp_key +"' name='"+ place +"' onclick='placeDetail(this.id)'>" + name + "</td>";


                text += "<td>"+ addr + "</td>";
                text += "</tr>";

            }

            text += "</table>";            
        }

        return text;  
    }        
    function showMeResult(){   


        var param = getProperty();

        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function() {
            if (this.readyState == 4 && this.status == 200) {   
                //console.warn(this.responseText)                 
                var jsonObj= JSON.parse(this.responseText);
                var text = getSearchResult(jsonObj);
                //console.warn(text);
                document.getElementById("demo").innerHTML = text;              
            }
        };
        xhttp.open("GET", "place.php?q=" + param, true);
        xhttp.send(); 

    }         
    function getGeoLocInfo(jsonObj){

        document.myForm.lat.value = jsonObj.lat;
        document.myForm.lon.value = jsonObj.lon;            
    }            
    function getGeoLoc(){
        var url = "http://ip-api.com/json/";
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function() {
            if (this.readyState == 4 && this.status == 200) {
                //document.getElementById("demo").innerHTML = this.responseText;
                var jsonObj= JSON.parse(this.responseText);
                getGeoLocInfo(jsonObj);                  
            }
        };
        xhttp.open("GET", url, true);
        xhttp.send();             
    }
    function changeProperty(){

        if( document.myForm.location.value == "Here"){
            document.myForm.in_loc.disabled = true;  
            document.myForm.in_loc.required = false;
        } 
        else{
            document.myForm.in_loc.disabled = false; 
            document.myForm.in_loc.required = true;
        }

    }    
    function clearFields(){
        var blank = "";
        // input type = "text"
        if(document.myForm.in_key.value != blank) document.myForm.in_key.value = blank;
        if(document.myForm.in_dist.value != blank) document.myForm.in_dist.value = blank;
        if(document.myForm.in_loc.value != blank) document.myForm.in_loc.value = blank;
        // <select>
        if(document.myForm.in_category.selectedIndex != 0) document.myForm.in_category.selectedIndex = 0;
        // radio button
        if( document.myForm.location.value != "Here"){
            document.myForm.location.value = "Here";
            document.myForm.in_loc.disabled = true;   
            document.myForm.in_loc.required = false;
        } 
    }
</script>
</head>

<body onload="getGeoLoc()">
    <!-- stop reload page when onsubmit() return false-->
    <form class="placeForm" name="myForm" method="get" onsubmit="showMeResult();return false">
        <fieldset>
        <h1>Travel and Entertainment Search</h1>
        <table>
        <tr><td>Keyword <input type="text" name="in_key" id="in_key" required size="20"></td><td></td></tr>
        <tr><td>Category
        <select name="in_category">
            <option value="default">default</option>
            <option value="cafe">cafe</option>
            <option value="backery">backery</option>
            <option value="restaurant">restaurant</option>   
            <option value="beauty salon">beauty salon</option>
            <option value="casino">casino</option>
            <option value="movie theater">movie theater</option>  
            <option value="lodging">lodging</option>
            <option value="airport">airport</option>
            <option value="train station">train station</option>
            <option value="subway station">subway station</option>   
            <option value="bus station">bus station</option>                                
        </select></td><td></td></tr>
        <tr><td>Distance (miles) <input type="text" name="in_dist" placeholder="10" size="20"></td>
            <td><input type="radio" name="location" value="Here" onchange="changeProperty()" checked>Here</td></tr>
            <tr><td></td>
            <td><input type="radio" name="location" value="Other" onchange="changeProperty()"><input type="text" name="in_loc" placeholder="location" disabled size="20"></td></tr>
        </table>       
        <div>
        <input type="text" name="lat" id="lat" hidden>
        <input type="text" name="lon" id="lon" hidden>
        </div>
        <input type ="submit"  name="submit" value="Search">
        <input type ="button" name="clear" value ="Clear" onclick="clearFields()">     
        </fieldset>
    </form> 
    <p id="demo"></p>  
</body>
</html>         
<?php endif; ?> 

    <?php 
        function getGeoCode($str){
            
            $myKey = "key=AIzaSyBxe-zKVEPMYkWGPWKN7CcWax9KylcVpvM";
            //$str = $_GET["in_loc"];
            $addr = urlencode($str);
            $url = "https://maps.googleapis.com/maps/api/geocode/json?address=".$addr."&".$myKey;
            $file = file_GET_contents($url);         
            $json = json_decode($file, true);
            $lat = $json['results'][0]['geometry']['location']['lat'];
            $lng = $json['results'][0]['geometry']['location']['lng'];

            $str = $lat.','.$lng;

            //echo 'make up addr: '.$str.'<br>';

            //echo '<p>Get data from Google Maps Geocoding API, location: '.$str.'</p>';

            return 'location='.$str;            
             
        }    

        function nearbySearch($location,$radius,$type,$keyword){

            $myKey = "AIzaSyBxe-zKVEPMYkWGPWKN7CcWax9KylcVpvM";
            $prefix = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
            $url = $prefix.$location."&".$radius."&".$type."&".$keyword."&key=".$myKey;

            //echo $url.'<br>';
            $file = file_GET_contents($url); 
            //echo $file;           
            return $file;


        }

        function getSearch($addr, $loc, $radius, $type, $keyword){

            //echo $loc."<br>";
            if( $addr == "address=Here"){    // Here
                //echo "Here it is<br>";
                $location = $loc;
            }
            else{
                $location = getGeoCode($addr);
            }
                
            return nearbySearch($location,$radius,$type,$keyword);             
        }       
    ?>


