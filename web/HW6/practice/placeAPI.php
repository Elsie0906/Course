<!DOCTYPE HTML>
<html>
<head>
<style>
    th, td{
        width: 500px;
        text-align: left;
    }
</style>
</head>
<body>
    <?php     
        function getIcon($json){

            return $json['results'][0]['icon'];

        }   
        function getName($json){
            return $json['results'][0]['name'];
        }
        function getAddr($json){
            return $json['results'][0]['vicinity'];
        }
        function getGeoCode(){
            $url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=34.0223519,-118.285117&radius=10&type=default&keyword=usc&key=AIzaSyBxe-zKVEPMYkWGPWKN7CcWax9KylcVpvM";
            $file = file_get_contents($url);
            $json = json_decode($file, true);

            return $json;

/*
            $category = $json['results'][0]['icon'];
            $name = $json['results'][0]['name'];
            $addr = $json['results'][0]['vicinity'];

            echo 'category: '.$category.'<br>';
            echo 'name: '.$name.'<br>';
            echo 'addr: '.$addr.'<br>';
*/          
        }
    ?>

    <div>
        <h>This is a test for PHP script</h>
        <p>The result is <br>
            <?php
                $json = getGeoCode(); 
            ?>
        </p>
        <table>
            <tr>
                <th>Name</th><th>Addr</th>
            </tr>
            <tr>
                <td><?php echo getName($json); ?></td>
                <td><?php echo getAddr($json); ?></td>
            </tr>
        </table>
    </div>

</body>
</html>