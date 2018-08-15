<?php
//header("Content-Type:application/json");
header('Access-Control-Allow-Origin: *');

require "data.php";

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
?>