<?php
//header("Content-Type:application/json");
header('Access-Control-Allow-Origin: *');

if(isset($_REQUEST["geoLocation"]))
{
	$msg = $_REQUEST["geoLocation"];

	//response($location);
	echo requestGeoCode($msg);
	
}

function response($response)
{
	header("HTTP/1.1");
	
	echo $response;
}

if(	isset($_REQUEST["location"])){
	$msg = $_REQUEST["location"];
	$myKey = "key=AIzaSyCM3nxvuypmX28IgkrJ841diJbHaNdZkmA";
	echo getGeoCode($msg, $myKey);
}

if(	isset($_REQUEST["loc"]) && isset($_REQUEST["dist"]) && isset($_REQUEST["category"]) && isset($_REQUEST["keyword"])){
	$loc = $_REQUEST["loc"];
	$dist = $_REQUEST["dist"];
	$category = $_REQUEST["category"];
	$keyword = $_REQUEST["keyword"];

    $msg = "location=".$loc."&"."radius=".$dist."&"."type=".$category."&"."keyword=".$keyword;
    $myKey = "key=AIzaSyCM3nxvuypmX28IgkrJ841diJbHaNdZkmA";
	//echo $msg;
	echo getNearBySearch($msg,$myKey);
}

if(	isset($_REQUEST["nextToken"])){
	$msg = $_REQUEST["nextToken"];
	$myKey = "key=AIzaSyCM3nxvuypmX28IgkrJ841diJbHaNdZkmA";
	echo getNextPage($msg, $myKey);
}

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