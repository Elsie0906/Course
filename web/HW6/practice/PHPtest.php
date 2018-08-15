<!DOCTYPE HTML>
<html>
<head>
<style>
</style>
</head>
<body>
    <?php        
        function getGeoCode(){
            $myKey = "key=AIzaSyBxe-zKVEPMYkWGPWKN7CcWax9KylcVpvM";
            $addr = "address=University+of+Southern+California+CA";
            $url = "https://maps.googleapis.com/maps/api/geocode/json?".$addr."&".$myKey;
            $file = file_get_contents($url);
            $json = json_decode($file, true);
            //echo 'long_name: '.$json['results'][0]['address_components'][0]['long_name'].'<br>';
            //echo 'formatted_address: '.$json['results'][0]['formatted_address'].'<br>';
            //echo 'lat: '.$json['results'][0]['geometry']['location']['lat'].'<br>';

            $lat = $json['results'][0]['geometry']['location']['lat'];
            $lng = $json['results'][0]['geometry']['location']['lng'];

            $str = 'location='.$lat.','.$lng;

            echo 'make up addr: '.$str.'<br>';

            return $str;           
        }
    ?>

    <div>
        <h>This is a test for PHP script</h>
        <p>The result is <br>
            <?php
            getGeoCode(); 
            ?>
        </p>
    </div>

</body>
</html>