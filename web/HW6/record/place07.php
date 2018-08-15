<?php if(isset($_REQUEST["q"])): ?>
    <?php 
        $q = $_REQUEST["q"];
        list($addr, $loc, $radius, $type, $keyword) = explode("$",$q);
        echo getSearch($addr, $loc, $radius, $type, $keyword);
    ?>
<?php elseif(isset($_REQUEST["x"])): ?>
    <?php
        $place_id = $_REQUEST["x"];
        //echo $place_id;
        echo getPlaceDetail($place_id);
    ?>   
<?php elseif(isset($_REQUEST["p"])): ?>
    <?php
        $photo = $_REQUEST["p"];
        //echo '<br>'.$photo.'<br>';
        list($name,$width,$reference) = explode("$",$photo);
        //echo 'fileName: '.$name.', size: '.$size.', reference'.$reference;
        getPhoto($name,$width,$reference);
    ?>     
<?php else: ?>

<!DOCTYPE HTML>
<html>
<head>
<style>
    body{
        text-align: center;
    }
    .placeForm{
        margin: auto;
        margin-top: 50px;
        width: 500px;
        background-color: #F3F3F3;
        border:1px solid #ddd;
        text-align: left;
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
        width: 450px;
    }
    #xmlTable td{   
        text-align: left;     
        border: 1px solid black;
    }
    #xmlTable .hIcon{
        width: 20px;
    }       
    #reviewTable th{
        border: 1px solid black;
        width: 500px;
    }  
    #reviewTable td{
        border: 1px solid black;
        width: 500px;
        min-height: 30px;
        text-align: left;
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

    function getReview(jsonObj){
        var text = "";
        var reviewNo = 5;

        if( jsonObj.status != "ZERO_RESULTS" && jsonObj.result.reviews != null){
            var c = jsonObj.result.reviews;

            //console.log(c);


            reviewNo = Math.min(reviewNo, c.length);

            text += "<table align='center' id='reviewTable'>";

            for(var i=0; i<reviewNo; i++){
                
                var review = c[i].text;
                var name = c[i].author_name;
                var icon = c[i].profile_photo_url;
                text += "<tr><th>";

                if(icon != null){
                    text += "<img src='"+ icon +"' alt='ProfileImg' width='20px' height='20px'> " + name;                    
                }
                else{ 
                    //text += "<img src='' alt='ProfileImg' width='20px' height='20px'> " + name; 
                    text += name;
                }
                
                text += "</th></tr>";
                text += "<tr><td>"+ review +"</td></tr>";
                
            }

            text += "</table>";
            
        }
        else{
            text += "<table align='center' id='reviewTable'>";
            text += "<tr><th>No Reviews Found</th></tr>"
            text += "</table>";

        }

        return text;

    } 
    function requestPhoto(num,reference,width,height){

        var param = "Img"+ num + "$"+"maxwidth="+width+"$"+"photoreference="+reference;

        //console.log(param);

        var xhttp = new XMLHttpRequest();

        xhttp.open("GET", "place.php?p=" + param, false); //synchronize
        xhttp.send();                

    }  
    function createPhotoTable(fileName){

        var xhttp = new XMLHttpRequest();

        xhttp.open("GET", fileName, true);
        xhttp.send();

        var text = "<a href='"+ fileName +"' target='_blank'>"; // open original img in a new tab
        text += "<img src='"+ fileName +"' alt='" + fileName +"' width='240px'>";
        text += "</a>";
        document.getElementById(fileName).innerHTML = text; 


    }
    function getPhoto(jsonObj){

        var photoNo = 5;
        var text ="";

        if( jsonObj.status != "ZERO_RESULTS" && jsonObj.result.photos != null){

            var c = jsonObj.result.photos;

            //console.log(c);

            photoNo = Math.min(photoNo, c.length);

            for(var i=0; i<photoNo; i++){
                var reference = c[i].photo_reference;
                var height = c[i].height;
                var width = c[i].width;

                requestPhoto(i,reference, width, height);

                var file = "Img" + i + ".png";
                text += "<p id='"+ file +"'></p>"; 
            }

            document.getElementById("photo").innerHTML = text;

            //text = "";

            for(var i=0; i<photoNo; i++){

                var file = "Img" + i + ".png";

                createPhotoTable(file);
            }
           
        }
        else{
            text += "<table align='center' id='reviewTable'>";
            text += "<tr><th>No Photos Found</th></tr>"
            text += "</table>";
            document.getElementById("photo").innerHTML = text;            
        }        

    }
    function showDetail(review, title){
        var text = "";
        var url = 'http://cs-server.usc.edu:45678/hw/hw6/images/arrow_down.png';

        text += "<h style='font-size:16px;'>"+ title +"</h>";

        text += "<div style='padding-top: 10px'>"
        text += "<p>click to show reviews</p>";
        text += "<p id='Reviews'><img src='"+ url +"' alt='DownArrow' width='20px'><p>";
        text += "<div id='review'>"+review+"</div>";
        text += "<p>click to show photos</p>";
        text += "<p id='Photos'><img src='"+ url +"' alt='DownArrow' width='20px'><p>";
        text += "<div id='photo'><div>";
        text += "</div>";

        return text;

    }
    function getTitle(jsonObj){
        //console.log(jsonObj.result.name);
        return jsonObj.result.name;
    }
    function getDetail(where){
        var param = "placeid="+where;

        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function() {
            if (this.readyState == 4 && this.status == 200) { 
                //console.log(this.responseText);  
                var jsonObj= JSON.parse(this.responseText);


                var review = getReview(jsonObj);

                var title = getTitle(jsonObj);

                var text = showDetail(review, title);
            
                document.getElementById("demo").innerHTML = text; 
                
                getPhoto(jsonObj);        


            }
        };
        xhttp.open("GET", "place.php?x=" + param, true);
        xhttp.send();        
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

                text += "<tr>";
                text += "<td class='hIcon'><img src='"+ icon +"' alt='IconImg' width='40%' height='40%'></td>";
                text += "<td id='"+ place +"' name='"+ place +"' onclick='getDetail(this.id)'>" + name + "</td>";


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

        document.getElementById("demo").innerHTML = ""; // clear Table
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

        function getPlaceDetail($place_id){
            $myKey = "AIzaSyBxe-zKVEPMYkWGPWKN7CcWax9KylcVpvM";
            $prefix = "https://maps.googleapis.com/maps/api/place/details/json?";
            $url = $prefix.$place_id."&key=".$myKey;

            //echo $url.'<br>';

            $file = file_get_contents($url);

            return $file;

        }   
        function getPhoto($name,$width,$reference){
            $myKey = "AIzaSyBxe-zKVEPMYkWGPWKN7CcWax9KylcVpvM";
            $prefix = "https://maps.googleapis.com/maps/api/place/photo?";
            $url = $prefix.$width.'&'.$reference.'&key='.$myKey;
            //echo $url;

            $file = file_get_contents($url);

            $name = $name.'.png';

            file_put_contents($name,$file);

        }  
    ?>


