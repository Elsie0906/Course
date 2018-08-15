<?php

header('Access-Control-Allow-Origin: *');

function requestGeoCode($str){

	$myKey = "key=AIzaSyCM3nxvuypmX28IgkrJ841diJbHaNdZkmA";
		
	$addr = urlencode($str);
	$url = "https://maps.googleapis.com/maps/api/geocode/json?address=".$addr."&".$myKey;

	$file = file_GET_contents($url); 
	return $file;
}

function getNearBySearch($msg,$myKey){
	
	//$msg = urlencode($str);

	$prefix = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
	$myKey = "key=AIzaSyCM3nxvuypmX28IgkrJ841diJbHaNdZkmA";
	$url = $prefix.$msg."&".$myKey;
	$file = file_get_contents($url);
	return $file;
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

function getNextPage($msg, $myKey){
	$prefix = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?pagetoken=";
	$url = $prefix.$msg."&".$myKey;
	//echo $url;
	$file = file_get_contents($url);
	return $file;		
}
?>
