
<?php
	header('Access-Control-Allow-Origin: *');
	//header("Content-Type:application/json");
	
	// Defaults for our simple example.
	$DEFAULT_TERM           = "Dinner";
	$DEFAULT_COUNTRY        = "United States";
	$DEFAULT_STATE			= "California";
	$DEFAULT_CITY			= "Los Angeles";
	$REVIEWS                = 0;

	// API constants, you shouldn't have to change these.
	$API_HOST = "https://api.yelp.com";
	$SEARCH_PATH = "/v3/businesses/matches/best";
	$BUSINESS_PATH = "/v3/businesses/";  // Business ID will come after slash.


	if(	isset($_REQUEST["name"]) && isset($_REQUEST["city"]) && isset($_REQUEST["state"]) && isset($_REQUEST["country"])){
		$DEFAULT_TERM = $_REQUEST['name'];
		$DEFAULT_CITY = $_REQUEST['city'];
		$DEFAULT_STATE = $_REQUEST['state'];
		$DEFAULT_COUNTRY = $_REQUEST['country'];

		query_api($DEFAULT_TERM, $DEFAULT_CITY, $DEFAULT_STATE, $DEFAULT_COUNTRY);
	}
		

		

	function query_api($term, $city, $state, $country){

		$response = bestMatch($term, $city, $state, $country);

		echo $response;

	}
	
	function bestMatch($term, $city, $state, $country){

		$url_params = array();

		$url_params['name'] = "";

		$url_params['name'] = $term;
		$url_params['city'] = $city;
		$url_params['state'] = $state;
		$url_params['country'] = $country;

		//return $GLOBALS['API_KEY'];

		return request($GLOBALS['API_HOST'], $GLOBALS['SEARCH_PATH'], $url_params);
	}

	function request($host, $path, $url_params = array()) {

	$API_KEY = 'scXl9Bo1v65vplRTRHDzqjc43BmwFjVW0xXmTE__kb-iBK1mEutT52-1EeDalVIwS4AjRUHls-YXANCrHsLfq8RrB8QorqUyNcrfzFEWVm1kIra_hgP5mHFILmLMWnYx';	
    		// Send Yelp API Call
    		try {
        		$curl = curl_init();
        		if (FALSE === $curl)
            		throw new Exception('Failed to initialize');
        		$url = $host . $path . "?" . http_build_query($url_params);

        		curl_setopt_array($curl, array(
            		CURLOPT_URL => $url,
            		CURLOPT_RETURNTRANSFER => true,  // Capture response.
            		CURLOPT_ENCODING => "",  // Accept gzip/deflate/whatever.
            		CURLOPT_MAXREDIRS => 10,
            		CURLOPT_TIMEOUT => 30,
            		CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
            		CURLOPT_CUSTOMREQUEST => "GET",
            		CURLOPT_HTTPHEADER => array(
                		"authorization: Bearer " . $API_KEY,
                		"cache-control: no-cache",
            		),
        		));
       			$response = curl_exec($curl);
        		if (FALSE === $response)
            		throw new Exception(curl_error($curl), curl_errno($curl));
        		$http_status = curl_getinfo($curl, CURLINFO_HTTP_CODE);
        		if (200 != $http_status)
            		throw new Exception($response, $http_status);
        		curl_close($curl);
    		} catch(Exception $e) {
        		trigger_error(sprintf(
            	'Curl failed with error #%d: %s',
            	$e->getCode(), $e->getMessage()),
            	E_USER_ERROR);
    		}
    		return $response;
	}		

		
?> 	
