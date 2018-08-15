
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

		$file = file_GET_contents($url); 
		$json = json_decode($file, true);

		$lat = $json['results'][0]['geometry']['location']['lat'];
		$lng = $json['results'][0]['geometry']['location']['lng'];

		$loc = $lat.','.$lng;
		return $loc;
	}

	if(	isset($_REQUEST["nearBy"])){
		$msg = $_REQUEST["nearBy"];
		//echo $msg;
		echo getNearBySearch($msg,$myKey);
	}

	function getNearBySearch($msg,$myKey){
		$prefix = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
		$url = $prefix.$msg."&".$myKey;
		$file = file_get_contents($url);
		return $file;
	}

	if(	isset($_REQUEST["nextToken"])){
		$msg = $_REQUEST["nextToken"];
		echo getNextPage($msg, $myKey);
	}	

	function getNextPage($msg, $myKey){
		$prefix = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?pagetoken=";
		$url = $prefix.$msg."&".$myKey;
		//echo $url;
		$file = file_get_contents($url);
		return $file;		
	}

?> 	
