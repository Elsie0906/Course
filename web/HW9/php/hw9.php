<?php
//header("Content-Type:application/json");
header('Access-Control-Allow-Origin: *');

if(isset($_REQUEST["geoLocation"]))
{
	$msg = $_REQUEST["geoLocation"];

	//response($location);
	echo requestGeoCode($msg);
	
}

if(	isset($_REQUEST["loc"]) && isset($_REQUEST["dist"]) && isset($_REQUEST["category"]) && isset($_REQUEST["keyword"])){
	$loc = $_REQUEST["loc"];
	$dist = $_REQUEST["dist"];
	$category = $_REQUEST["category"];
	$category = urlencode($category);
	$keyword = $_REQUEST["keyword"];
	$keyword = urlencode($keyword);

    $msg = "location=".$loc."&"."radius=".$dist."&"."type=".$category."&"."keyword=".$keyword;
    $myKey = "key=AIzaSyBxe-zKVEPMYkWGPWKN7CcWax9KylcVpvM";
	//echo $msg;
	echo getNearBySearch($msg,$myKey);
}

if(isset($_REQUEST["x"])){
	$placeId = $_REQUEST["x"];
	$msg = "placeid=".$placeId;
	echo getPlaceDetail($msg);
}

function getPlaceDetail($place_id){
    $myKey = "AIzaSyBxe-zKVEPMYkWGPWKN7CcWax9KylcVpvM";
    $prefix = "https://maps.googleapis.com/maps/api/place/details/json?";
    $url = $prefix.$place_id."&key=".$myKey;

    //echo $url.'<br>';

    $file = file_get_contents($url);

    return $file;

} 

if(	isset($_REQUEST["nextToken"])){
	$msg = $_REQUEST["nextToken"];
	$myKey = "key=AIzaSyBxe-zKVEPMYkWGPWKN7CcWax9KylcVpvM";
	echo getNextPage($msg, $myKey);
}

function requestGeoCode($str){

	$myKey = "key=AIzaSyBxe-zKVEPMYkWGPWKN7CcWax9KylcVpvM";
		
	$addr = urlencode($str);
	$url = "https://maps.googleapis.com/maps/api/geocode/json?address=".$addr."&".$myKey;

	$file = file_GET_contents($url); 
	return $file;
}

function getNearBySearch($msg,$myKey){
	
	//$msg = urlencode($str);

	$prefix = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
	$url = $prefix.$msg."&".$myKey;
	
	$file = file_get_contents($url);
	return $file;
}


function getNextPage($msg, $myKey){
	$prefix = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?pagetoken=";
	$url = $prefix.$msg."&".$myKey;
	//echo $url;
	$file = file_get_contents($url);
	return $file;		
}
?>