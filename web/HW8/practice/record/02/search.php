
<?php
	header('Access-Control-Allow-Origin: *');  

	$myKey = "key=AIzaSyCM3nxvuypmX28IgkrJ841diJbHaNdZkmA";
 
	if(	isset($_REQUEST["location"])){
		$msg = $_REQUEST["location"];
		echo getGeoCode($msg, $myKey);
	}

	function getGeoCode($str, $myKey){
		
		$addr = urlencode($str);
		$url = "https://maps.googleapis.com/maps/api/geocode/json?address=".$addr."&".$myKey;

		//echo 'make up addr: '.$url.'<br>';

		$file = file_GET_contents($url); 
		$json = json_decode($file, true);

		$lat = $json['results'][0]['geometry']['location']['lat'];
		$lng = $json['results'][0]['geometry']['location']['lng'];

		$loc = $lat.','.$lng;
		return $loc;
	}

	if(	isset($_REQUEST["nearBy"])){
		$msg = $_REQUEST["nearBy"];
		list($addr, $radius, $type, $keyword) = explode("$",$msg);
		echo getNearBySearch($addr, $radius, $type, $keyword, $myKey);
	}

	function getNearBySearch($addr, $radius, $type, $keyword, $myKey){
		$prefix = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
		$url = $prefix.$addr."&".$radius."&".$type."&".$keyword."&".$myKey;
		//echo $url.'<br>';
		$file = file_get_contents($url);
		//echo $file;
		return $file;
	}
?> 	
