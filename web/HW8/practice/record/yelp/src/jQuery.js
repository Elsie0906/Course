/* global variable */
//myStorage = window.localStorage;
myStorage = window.sessionStorage;
myScript = "index.php";
//myScript = "http://yinhsiay-hw8-env.us-east-1.elasticbeanstalk.com/index.php";
/*
    loc: lat, lon
    pagination: curPage, page1, page2, page3
    button: List, place_id, curTable, favor
*/

$(  document ).ready(function() {    
    var script = document.createElement('script');
    script.type = 'text/javascript';
    script.src = 'https://maps.googleapis.com/maps/api/js?key=AIzaSyCM3nxvuypmX28IgkrJ841diJbHaNdZkmA&libraries=places&callback=init';

    document.body.appendChild(script);

    myStorage.removeItem("lat");
    myStorage.removeItem("lon");
    myStorage.removeItem("favor");
    myStorage.removeItem("place_id");

    $.get("http://ip-api.com/json", function(data, status){
        if( status == "success" && data != null){
            myStorage.setItem("lat",data.lat);
            myStorage.setItem("lon",data.lon);
            $("#submit").removeAttr( "disabled" );
        }
        else{ 
            myStorage.setItem("lat","34.0223519");
            myStorage.setItem("lon","-118.285117");            
            $("#submit").removeAttr( "disabled" );                      
        }
        //console.log(myStorage.getItem("lat"));
    });         



    $("#optionHere").click(function(){
        //document.myForm.location.value = "";
        $("#locationTextField").attr("disabled","true");
    });

    $("#optionOther").click(function(){
        $("#locationTextField").removeAttr("disabled");
    });  
                                             
});
 
$(document).ajaxStop(function(){

    $('#loader').hide();
    $('#bar').css('width',20+ '%');
});

$(document).submit(function(event){ 

    event.preventDefault();

    reset();

    $('#loader').show();
    $('#bar').css('width',50+ '%');
    
    var in_category = encodeURI($("#category").val());
    
    var in_dist = "";

    if($("#distance").val() == ""){
        in_dist = "10";
    }else{
        in_dist = $("#distance").val();
    }

    var in_keyword = encodeURI($("#keyword").val());

    var in_location = "";

    if( document.myForm.optradio.value == "Other"){


        var getAjaxPromise = function(option){
            return new Promise(function(resolve,reject){
                $.ajax({
                    url: option.url,
                    type: 'get',
                    data: { location: $("#locationTextField").val()},
                    success: function(data){
                        resolve(data);
                    },
                    error: function(err){
                        reject(err);
                    }
                });
            });
        };

        var p1 = getAjaxPromise({
            url: myScript
        });

        p1.then(function(data1){
            //console.log(data1);
            in_location = data1; 

            //var search = "location=" + in_location + "&" +"radius=" + in_dist + "&" +"type=" + in_category + "&" +"keyword=" + in_keyword;
            //nearBySearch(encodeURI(search));
            nearBySearch(in_location, in_dist, in_category, in_keyword); 
        });           
    }
    else{

        in_location = myStorage.getItem("lat") + "," + myStorage.getItem("lon");

        //var search = "location=" + in_location + "&" +"radius=" + in_dist + "&" +"type=" + in_category + "&" +"keyword=" + in_keyword;
        //nearBySearch(encodeURI(search));
        nearBySearch(in_location, in_dist, in_category, in_keyword);        
    }

});

function changeView(id){

    //console.log(marker.getPosition());
    var text = "";
    if(id == "routeView"){
        text += "<button type='button' class='btn btn-default' onclick='changeView(this.id)' id='streetView'><img src='http://cs-server.usc.edu:45678/hw/hw8/images/Map.png' width='20px'></button>";
           
        var panorama = new google.maps.StreetViewPanorama(
        
            document.getElementById('map'), {
                position: marker.getPosition(),
                pov: {
                    heading: 34,
                    pitch: 10
                }
            });
        map.setStreetView(panorama);

    }
    else{
        text += "<button type='button' class='btn btn-default' onclick='changeView(this.id)' id='routeView'><img src='http://cs-server.usc.edu:45678/hw/hw8/images/Pegman.png' width='20px'></button>";

        map = createMap();

        resetMarker(marker.getPosition());
    
        resetService(map);        

    }
    
    $("#view").html(text);
}

function alertBasic(){

    var text = '<div id="alertBox" class="alert alert-warning" style="margin-top: 10px">';

    text += '<strong>Fail to get search results</strong>';
    text += '</div>';

    return text;
}

function noRecordBasic(){

    var text = '<div id="alertBox" class="alert alert-warning" style="margin-top: 10px">';

    text += '<strong>No records</strong>';
    text += '</div>';

    return text;
}

function resultBasic(tableBody, ctrlBody){

    var text = '<div class="text-right" style="margin-top: 10px">';

    text += "<button type='button' class='btn btn-default' disabled id='detailBtn' onclick='showDetail()'>Details <i class='fa fa-chevron-right'></i></button>";
    text += '</div>';

    text += '<div id="searchTable">';       
    text += '<div class="container-fluid">';
    text += '<table class="table table-condensed" align="center" style="border-top: 0.5px solid #d3d3d3;margin-top: 5px">';
    text += '<thead>';
    text += '<tr>';
    text += '<th>#</th>';
    text += '<th>Category</th>';
    text += '<th>Name</th>';
    text += '<th>Address</th>';
    text += '<th>Favorite</th>';
    text += '<th>Details</th>';
    text += '</tr>';
    text += '</thead>';
    text += '<tbody id="searchTableBody">' + tableBody + '</tbody>';  
    text += '</table>';        
    text += '<div id="ctrlTable" align="center">'+ ctrlBody + '</div>';   
    text += '</div>';                
    text += '</div>';                
 
    return text;             
}

function infoTabBasic(){
    var text = "";

    text += '<div id="Info" class="tab-pane fade in active">';  // Info Tab
    text += '<div class="container-fluid">';
    text += '<table class="table table-condensed table-striped" align="center" style="border-top: 0.5px solid #d3d3d3;margin-top: 10px">';
    text += '<tbody>';    
    text += '<tr id="Addr"><th>Address</th>' + '<td id="addr"></td>' +'</tr>';
    text += '<tr id="Phone"><th>Phone Number</th>' + '<td id="phone"></td>' +'</tr>';
    text += '<tr id="Price><th>Price Level</th>' + '<td id="price"></td>' +'</tr>';
    text += '<tr id="Rate"><th>Rating</th>' + '<td id="rate"></td>' +'</tr>';
    text += '<tr id="GooPage"><th>Google Page</th>' + '<td id="gooPage"></td>' +'</tr>';
    text += '<tr id="Web"><th>Website</th>' + '<td id="web"></td>' +'</tr>';
    text += '<tr id="Hour"><th>Hours</th>' + '<td id="hour"></td>' +'</tr>';
    text += '</tbody>';
    text += '</table>';
    text += '</div></div>';

    return text;    
}

function photoTabBasic(){
    var text = "";
    text += '<div id="Photos" class="tab-pane fade">';  // Photos Tab
    text += '<div class="container-fluid">';
    text += '<table class="table table-condensed" align="center" style="border-top: 0.5px solid #d3d3d3;margin-top: 10px">';
    text += '<tbody id="photoCtrlBody">'; 
    text += '</tbody>';
    text += '</table>';
    text += '</div></div>'; 
    return text;
}

function mapTabBasic(){
    var text = "";
    text += '<div id="Map" class="tab-pane fade">';     // Map Tab
    text += '<div class="container-fluid">';
    text += '<table class="table table-condensed" align="center" style="border-top: 0.5px solid #d3d3d3;margin-top: 10px">';
    text += '<tbody>';          
    text += '<tr>';        
    text += '<th><div class="col-xs-4 col-sm-4 col-md-4 col-lg-4">From</div>';
    text += '<div class="col-xs-4 col-sm-4 col-md-4 col-lg-4">To</div>';
    text += '<div class="col-xs-4 col-sm-4 col-md-4 col-lg-4">Travel Modes</div></th>';
    text += '</tr>';
    text += '<tr><td>';
    text += '<div class="col-xs-4 col-sm-4 col-md-4 col-lg-4 container-fluid"><input type="text" placeholder="Your location" id="directionFrom"></div>';
    text += '<div class="col-xs-4 col-sm-4 col-md-4 col-lg-4 container-fluid"><input type="text" id="directionTo"></div>';
    text += '<div class="col-xs-12 col-sm-2 col-md-2 col-lg-2">';
    text += '<select id="mode">';
    text += "<option class='dropbtn' value='DRIVING'>Driving</option>";
    text += "<option class='dropbtn' value='BICYCLING'>Bicycling</option>";
    text += "<option class='dropbtn' value='TRANSIT'>Transit</option>";
    text += "<option class='dropbtn' value='WALKING'>Walking</option>";
    text += "<option class='dropbtn' value='WALKING'>Walking</option>";
    text += '</select>';
    text += '</div>';
    text += '<div class="col-xs-12 col-sm-2 col-md-2 col-lg-2">';
    text += '<button type="submit" class="btn btn-primary" id="getDirect">Get Direction</button>';
    text += '</div>';
    text += '</td></tr>';    
    text += '<tr><td>';
    text += '<div class="col-xs-12 col-sm-2 col-md-2 col-lg-2" id="view">';
    text += "<button type='button' class='btn btn-default' onclick='changeView(this.id)' id='routeView'><img src='http://cs-server.usc.edu:45678/hw/hw8/images/Pegman.png' width='20px'></button>";
    text += '</div>';
    text += '</td></tr>';
 
    text += '<tr><td>' + '<div id="map" style="width:100%;height:400px;"></div>' + '</td></tr>'; 
    text += '<tr><td>' + '<div id="directionStep" style="width:100%;height: 100%"></div>' + '</td></tr>'; 
  
    text += '</tbody>';
    text += '</table>';   
    text += '</div></div>';
    return text;
    
}

function reviewTabBasic(){
    var text = "";

    text += '<div id="Reviews" class="tab-pane fade">';  // Photos Tab
    text += '<div class="container-fluid">';
    text += '<table class="table table-condensed" align="center" style="border-top: 0.5px solid #d3d3d3;margin-top: 10px">';
    text += '<tbody>'; 
    text += reviewCtrlBasic();
    text += '<tr id="reviewCtrlBody"></tr>';   
    text += '</tbody>';
    text += '</table>';
    text += '</div></div>'; 

    return text;
}

function reviewCtrlBasic(){
    var text = "";

    text += '<tr>';
    text += '<td>';
    text += '<span class="dropdown">';
    text += '<button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">Google Reviews';
    text += '<span class="caret"></span></button>';
    text += '<ul class="dropdown-menu">';
    text += '<li><a href="#">Google Reviews</a></li>';
    text += '<li><a href="#">Yelp Reviews</a></li>';
    text += '</ul>';
    text += '</span>';   
    text += '<span class="dropdown" style="padding-left: 5px">';
    text += '<button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">Default Order';
    text += '<span class="caret"></span></button>';
    text += '<ul class="dropdown-menu">';
    text += '<li onclick="sortArray(this.id)" id="Default Order"><a href="#">Default Order</a></li>';
    text += '<li onclick="sortArray(this.id)" id="Highest Rating"><a href="#">Highest Rating</a></li>';
    text += '<li onclick="sortArray(this.id)" id="Lowest Rating"><a href="#">Lowest Rating</a></li>';
    text += '<li onclick="sortArray(this.id)" id="Most Recent"><a href="#">Most Recent</a></li>';  
    text += '<li onclick="sortArray(this.id)" id="Least Recent"><a href="#">Least Recent</a></li>';   
    text += '</ul>';
    text += '</span>';   
    text += '</td>';   
    text += '</tr>';

    return text;
}

function tabsBasic(){
    var text = "";

    text +="<div style='margin-top: 20px'>"
    text += "<div id='shopName' class='container-fluid' style='text-align: center'></div>";
    text += "<button type='button' class='btn btn-default' onclick='showMeList()'><i class='fa fa-chevron-left'></i> List</button>";
    text += "<button type='button' class='btn btn-default'><i class='fa fa-star-o'></i></button>";
    text += "<span onclick='twitterCtrl()'><a href='#' id='twitter'><img src='http://cs-server.usc.edu:45678/hw/hw8/images/Twitter.png' width='30'></a></span>";
    text += '<div class="right-tabs">';
    text += '<ul class="nav nav-tabs">';
    text += '<li class="active"><a data-toggle="tab" href="#Info">Info</a></li>';
    text += '<li><a data-toggle="tab" href="#Photos">Photos</a></li>';
    text += '<li><a data-toggle="tab" href="#Map">Map</a></li>';
    text += '<li><a data-toggle="tab" href="#Reviews">Reviews</a></li>';
    text += '</ul>';
    text += '<div class="tab-content">';
    text += infoTabBasic();  
    text += photoTabBasic(); 
    text += mapTabBasic();  
    text += reviewTabBasic(); 
    text += '</div></div>';
    text += '</div>';
    return text;
        
}
function favoriteBasic(tableBody){

    var text = '<div class="text-right" style="margin-top: 10px">';

    text += "<button type='button' class='btn btn-default' disabled id='detailBtn' onclick='showDetail()'>Details <i class='fa fa-chevron-right'></i></button>";
    text += '</div>';

    text += '<div id="searchTable">';       
    text += '<div class="container-fluid">';
    text += '<table class="table table-condensed" align="center" style="border-top: 0.5px solid #d3d3d3;margin-top: 5px">';
    text += '<thead>';
    text += '<tr>';
    text += '<th>#</th>';
    text += '<th>Category</th>';
    text += '<th>Name</th>';
    text += '<th>Address</th>';
    text += '<th>Favorite</th>';
    text += '<th>Details</th>';
    text += '</tr>';
    text += '</thead>';
    text += '<tbody id="favorTableBody">' + tableBody + '</tbody>';  
    text += '</table>';                        
    text += '</div>';                
 
    return text;             
}
//function nearBySearch(param){
function nearBySearch(in_location, in_dist, in_category, in_keyword){
    //console.log(param);

    $.ajax({     
        url: myScript,
        data: { 
            loc: in_location,
            dist: in_dist,
            category: in_category,
            keyword: in_keyword
        },
        type: 'GET',
        success: function(response, status){

            $('#bar').css('width',75+ '%');

            //console.log(response);

            var obj = $.parseJSON(response);
            

            //reset();
            
            if( obj.status != "ZERO_RESULTS" && obj.results != null){

                var c = obj.results;
                var number = Math.min(20, c.length);    // at most 20 entries

                var nextPage = obj.next_page_token;

                //console.log(nextPage);
                
                var tableBody = "";

                for(var i=0; i<number; i++){

                    var index = i+1;
                    var icon = c[i].icon;
                    var name = c[i].name;
                    var addr = c[i].vicinity;
                    var place = c[i].place_id;
                    var makeUp = place + "num" + i;

                    tableBody += "<tr class='clickable-row'>";
                    tableBody += "<td>" + index + "</td>";
                    tableBody += "<td class='hIcon'><img src='"+ icon +"' alt='IconImg' width='40%' height='40%'></td>";
                    tableBody += "<td>" + name + "</td>";
                    tableBody += "<td>"+ addr + "</td>";
                    tableBody += "<td>" + "<button type='button' class='btn btn-default' id='"+ makeUp + "' onclick='setFavorite(this.id)'>";
                    tableBody += "<i class='fa fa-star-o'></i></button>"+ "</td>";
                    tableBody += "<td>" + "<button type='button' class='btn btn-default' id='"+ place + "' onclick='getDetail(this.id)'>";
                    tableBody += "<i class='fa fa-chevron-right'></i></button>"+"</td>";
                    tableBody += "</tr>";
                }

                var ctrlBody = "";

                if( nextPage != "" && nextPage != null){
                    ctrlBody = "<button type='button' class='btn btn-default' onclick='showMeNext()'>Next</button>";
                    ctrlBody += "<input type='text' id='nextToken' hidden value='"+ nextPage +"'>";
                }

                var text = resultBasic(tableBody, ctrlBody);

                myStorage.setItem("curPage","0");
                //console.log(myStorage.getItem("curPage")); 
                
                $('#bar').css('width',100+ '%');      
                $( "#demo" ).html(text);

                
                var num = parseInt(myStorage.getItem("curPage"),10);
                var pageName = "page" + num;
                myStorage.setItem(pageName, $("#searchTable").html());

                myStorage.setItem("curTable",$("#demo").html());  
             
            }
            else{
                $("#demo").html(noRecordBasic());
            }
            
        },
        error: function(err){
            $("#demo").html(alertBasic());
        }        
    });    
}

function showMeResultTable(){

    if($("#favorTab").hasClass('btn-primary')){
        $("#favorTab").removeClass("btn-primary").css('color','#006dcc');
        $("#resultTab").addClass("btn-primary").css('color','white');
    }


    if(myStorage.getItem("curTable") != null){
        $("#demo").html(myStorage.getItem("curTable"));
    }

    if(myStorage.getItem("place_id") != null){
        highlight(myStorage.getItem("place_id"));
    }
}

function showMeFavorite(){

    if($("#resultTab").hasClass('btn-primary')){
        $("#resultTab").removeClass("btn-primary").css('color','#006dcc');
        $("#favorTab").addClass("btn-primary").css('color','white');
    }

    //console.log(myStorage.getItem("favor"));

    if(myStorage.getItem("favor") == null /*|| myStorage.getItem("favor") == ""*/){
        $("#demo").html(noRecordBasic());       
    }
    else{
        var favorBody = myStorage.getItem("favor");
        var text = favoriteBasic(favorBody);
        $("#demo").html(text);        

        $("i").removeClass("fa-star-o").addClass("fa-trash-o");
        $('button[onclick*="setFavorite"]').removeAttr('onclick').attr('onclick','removeFavorite(this.id)');        
        
    }

    if(myStorage.getItem("place_id") != null){
        highlight(myStorage.getItem("place_id"));
    }    
}

function removeFavorite(id){

    $("#"+id).closest('tr').remove();

    myStorage.setItem("favor",$("#favorTableBody").html());
    
    //console.log(myStorage.getItem("favor"));

    if($("#favorTableBody").html() == ""){
       myStorage.removeItem("favor");
       $("#demo").html("");  
    }

}

function setFavorite(id){
    var node = "<tr>";
        
    node += $("#"+id).parent().parent().html();

    node += '</tr>';

    var data = id.split("num");
    var place_id = data[0];

    myStorage.setItem("place_id",place_id);

    highlight(myStorage.getItem("place_id"));

    append(node);

    //console.log(node);
}

function append(data){

    var old = myStorage.getItem("favor");
    if(old == null){
       old = ""; 
    } 
    
    myStorage.setItem("favor", old + data);

}

function highlight(place_id){

    var node = $("#"+place_id).parent().parent();  

    node.addClass('bg-warning').siblings().removeClass('bg-warning');  
}

function createMap(){

    var lattitude = parseFloat(myStorage.getItem("lat"));
    var lngitude = parseFloat(myStorage.getItem("lon"));

    var pyrmont = {lat: lattitude, lng: lngitude}; 

    map = new google.maps.Map(document.getElementById('map'), { // create a map with current location
        center: pyrmont,
        zoom: 15
    });

    return map;    
}

function setService(map, place_id){

    service = new google.maps.places.PlacesService(map); 

    myStorage.removeItem("place_id");

    service.getDetails({
        placeId: place_id
        }, rDetails);

    myStorage.setItem("place_id", place_id);

    directionsDisplay = new google.maps.DirectionsRenderer;
    directionsService = new google.maps.DirectionsService;    
    
    directionsDisplay.setMap(map);  
    directionsDisplay.setPanel(document.getElementById('directionStep'));
}

function resetService(map){
    service = new google.maps.places.PlacesService(map); 

    directionsDisplay = new google.maps.DirectionsRenderer;
    directionsService = new google.maps.DirectionsService;    
    
    directionsDisplay.setMap(map);  
    directionsDisplay.setPanel(document.getElementById('directionStep'));
}

function setMarker(place){
    marker = new google.maps.Marker({   // add marker with placeId
        map: map,
        position: place.geometry.location
    });

    map.setCenter(marker.getPosition());    // recenter
}

function resetMarker(position){
    marker = new google.maps.Marker({   // add marker with placeId
        map: map,
        position: position
    });

    map.setCenter(marker.getPosition());    // recenter
}

function getDetail(place_id){

    highlight(place_id);


    $("#detailBtn").removeAttr( "disabled" );
    

    myStorage.setItem("List",$( "#demo" ).html()); 

    var text = tabsBasic();
    $( "#demo" ).html(text);

    map = createMap();

    setService(map, place_id);
              
}

function showDetail(){

    myStorage.setItem("List", $( "#demo" ).html());

    var text = tabsBasic();
    $( "#demo" ).html(text);

    map = createMap();

    setService(map, myStorage.getItem("place_id"));
           
}

function rDetails(place, status){

    if (status === google.maps.places.PlacesServiceStatus.OK) {
           
        //console.log(place);

        setMarker(place);

        /* Info Tabs */
        infoCtrl(place);

        /* Photo Tabs */
        photoCtrl(place);

        mapCtrl(place);

        reviewCtrl(place.reviews);
                 
      
    }
    else{
        window.alert('Details request failed due to ' + status);
    }
}

function twitterCtrl(){
    
    var name = "Check out " + $( "#shopName" ).text();
    var address = " located at "+ $( "#addr" ).text();
    var url = ' Website: ' + '&url=' + $( "#gooPage" ).text();
    var hashtag = '&hashtags=' + 'TravelAndEntertainmentSearch';

    $('#twitter').attr("href", "https://twitter.com/intent/tweet?text=" + name + address + url + hashtag);

}

function reviewCtrl(reviews){

    global_review = reviews;

    sortArray("Default Order");
}

function sortArray(val){

    //console.log(val);

    var reviews = global_review.slice();    // copy an array

    if(val == "Highest Rating"){
       reviews.sort(function(a,b){ return b.rating - a.rating;}); 
    }
    else if(val == "Lowest Rating"){
        reviews.sort(function(a,b){ return a.rating - b.rating;});
    }
    else if(val == "Most Recent"){
        reviews.sort(function(a,b){ return b.time - a.time;});
    }
    else if(val == "Least Recent"){
        reviews.sort(function(a,b){ return a.time - b.time;});
    }

    var text = "";

    for(var i=0; i<reviews.length; i++){
        var icon = reviews[i].profile_photo_url;

        var timestring = "1970-01-01T00:00:00Z";
        var date = moment.utc(timestring).add('s',reviews[i].time).format('YYYY-MM-DD HH:mm:ss'); 
        var id = "gooReview" + i;
        var url = reviews[i].author_url;
        
        text += "<tr>";

        if(icon != null){
            text += "<td>"+ '<a href="'+ url + '" target="_blank">' +"<img src='"+ icon +"' alt='IconImg' width='40'></a></td>";
        }
        else{
            text += "<td></td>";
        }
        
        text += "<td style='padding-left: 10px'>";
        text += "<div>"+'<a href="'+ url + '" target="_blank">' + reviews[i].author_name +'</a>'+"</div>";
        //text += ratingInfo(date, reviews[i].rating, id);
        text += "<div>"+ "rating: " + reviews[i].rating + " " + date +"</div>";        
        text += "<div>"+ reviews[i].text +"</div>";
        text += "</td>";
        text += "</tr>";

    }

    //console.log($( "#reviewCtrlBody" ).html());
    $( "#reviewCtrlBody" ).html(text);     
}

function calculateAndDisplayRoute(directionsService, directionsDisplay){

    var start;

    //console.log($( "#directionFrom" ).val());

    if($( "#directionFrom" ).val() == null || $( "#directionFrom" ).val() == "" || $( "#directionFrom" ).val() == "My location" || $( "#directionFrom" ).val() == "Your location"){

        var lattitude = parseFloat(myStorage.getItem("lat"));
        var lngitude = parseFloat(myStorage.getItem("lon"));
        start = new google.maps.LatLng(lattitude, lngitude);    // current location

        getRoute(start);
    }
    else{
        var getAjaxPromise = function(option){
            return new Promise(function(resolve,reject){
                $.ajax({
                    url: option.url,
                    type: 'get',
                    data: { location: $("#directionFrom").val()},
                    success: function(data){
                        resolve(data);
                    },
                    error: function(err){
                        reject(err);
                    }
                });
            });
        };

        var p1 = getAjaxPromise({
            url: myScript
        });

        p1.then(function(data1){
            //console.log(data1);
            var num = data1.split(",");

            var lattitude = parseFloat(num[0]);
            var lngitude = parseFloat(num[1]);            

            start = new google.maps.LatLng(lattitude, lngitude);
            getRoute(start);
        }); 
    }

   
}

function getRoute(data){

    var start = data;    // current location
    
    var end = marker.getPosition();                             // fixed

    //console.log(document.getElementById('mode').value);
    var selectedMode = document.getElementById('mode').value;

    var request = {
        origin: start,
        destination: end,
        provideRouteAlternatives: true, // multiple routes
        travelMode: google.maps.TravelMode[selectedMode]
    };

    directionsService.route(request, function(result, status) {
        if (status == 'OK') {
            directionsDisplay.setDirections(result);
            //console.log(result);
        }
        else{
            window.alert('Directions request failed due to ' + status);
        }
    });     
}

function mapCtrl(place){

    var name = place.name + ", " + place.formatted_address;

    $( "#directionTo" ).val(name);

    var autocomplete = new google.maps.places.Autocomplete((document.getElementById('directionFrom')),{types: ['address']});

    document.getElementById("getDirect").addEventListener('click', function() {
        calculateAndDisplayRoute(directionsService, directionsDisplay);
    });
     
}

function photoCtrl(place){
    var text = "<tr><td>";

    text += '<div class="form-group">';


    var photos = place.photos;

    for(var i=0; i<photos.length; i++)
    {
        var file = photos[i].getUrl({'maxWidth': photos[i].width, 'maxHeight': photos[i].height});

        //console.log(file);
        text += '<div class="col-xs-12 col-sm-3 col-md-3 col-lg-3">';
        text += "<a href='"+ file +"' target='_blank'>"; // open original img in a new tab
        text += "<img src='"+ file +"' alt='image' width='240px'>";
        text += "</a>";
        text += '</div>';
    }

    text += '</div>';
    text += "</td></tr>";

    $( "#photoCtrlBody" ).html(text);
}

function infoCtrl(place){

    var text = getName(place.name);
    //console.log(place.name);
    $( "#shopName" ).html(text);
    checkElement("#shopName", place.name);

    $( "#addr" ).text(place.formatted_address);
    checkElement("#Addr", place.formatted_address);

    $( "#phone" ).text(place.international_phone_number);
    checkElement("#Phone", place.international_phone_number);

    text = getPriceLevel(place.price_level);
    $( "#price" ).text(text);
    checkElement("#Price", place.price_level);

    text = getRating(place.rating);
    $( "#rate" ).html(text);
    checkElement("#Rate", place.rating);

    text = "<a href='" + place.url + "' target='_blank'>" + place.url + "</a>";
    $( "#gooPage" ).html(text);
    checkElement("#GooPage", place.url);

    text = "<a href='" + place.website + "' target='_blank'>" + place.website + "</a>";
    $( "#web" ).html(text);
    checkElement("#Web", place.website);

    text = getOpenHour(place.opening_hours, place.utc_offset);
    $( "#hour" ).html(text);        
    checkElement("#Hour", place.opening_hours);
    
}

function checkElement(id, value){
    if(value == "" || value == null){
        $(id).hide();
    }
    else{
        $(id).show();
    }
}

function getName(name){

    var text = "";

    text += "<h style='font-size: 16px'>" + name + "</h>"

    return text;
}
function getOpenHour(hour, offset){
    if( hour == null || hour == "")
        return null;

    var text = "";
    var open = hour.open_now;
    var localDay = moment.utc().add('m',offset).day();  
    //console.log(localDay);

    var time; // 0: Sun; 6: Sat

    if( localDay == 0){
        localDay = 6;
    }

    time = hour.weekday_text[localDay-1];
    //console.log(time);

    var idx = time.indexOf(" ");
    //console.log(time.substring(idx+1));
    var curTime = time.substring(idx+1); 

    var start = localDay - 1;
    var len = hour.weekday_text.length;
    var main = "";

    main += '<div class="container-fluid">';
    main += '<table class="table table-condensed">';
    main += '<thead>';
    main += '<tr><th style="font-size: 16px">Open hours</th>';
    main += '</thead>';
    main += '<tbody>';

    for(var i=0; i<len; i++){

        var index = (i + start)%len;
        var str = hour.weekday_text[index];
        var from = str.indexOf(" ");
        var head = str.substring(0,from-1);
        var tail = str.substring(from+1); 
        
        if( i==0){
            main += '<tr><th>' + head + '</th><th>'+ tail +'<th></tr>';           
        }
        else{
            main += '<tr><td>' + head + '</td><td>'+ tail + '</td></tr>';  
        }       
    } 

    main += '</tbody></table></div>';

    text += "<div>";

    if( open == true){       
        text += "<span style='display:inline-block;'>"+"Open now:" + curTime+ "</span>";   
    }
    else{
        text += "<span style='display:inline-block;'>"+"Closed" + "</span>";
    }

    text += '<span id="myBtn" onclick="pop()" style="display:inline-block;color: blue;"><u>Daily open hours</u></span>'; 
    text += "</div>";    
    text += '<div id="myModal" class="modal">';
    text += '<div class="modal-content">';
    text += '<span class="close">&times;</span>';
    //text += '<p>Some text in the Modal..</p>';
    text += main;
    text += '<button type="button" class="btn btn-default" id="myClose">Close</button>';
    text += '</div></div>';
    
    return text;
}

function pop(){
    var modal = document.getElementById('myModal');

    modal.style.display = "block";
}

$(document).click(function(event){
    var modal = document.getElementById('myModal');

    var span = document.getElementById("myClose");

    if( span != null){
        span.onclick = function() {
            modal.style.display = "none";
        }        
    }

});

function getRating(rating){

    var text = "<div>";

    text += "<span style='display:inline-block;'>"+rating + "</span>"+"<span id='rateYo' style='display:inline-block;'></span>";
    text += "</div>";

    $(function () {
 
        $("#rateYo").rateYo({
            starWidth: "10px",
            rating    : rating
        });
 
    });

    //console.log(text);
    return text;
}

function getPriceLevel(level){

    var text = "";
    for(var i=0; i<level; i++){
        text += "$";
    }

    return text;
}

function showMeList(){

    if( myStorage.getItem("List") != null){
        $( "#demo" ).html(myStorage.getItem("List"));
    }
}

function showMeNext(){

    //$('#loader').show();    //progress bar
    //$('#bar').css('width',50+ '%');

    var num = parseInt(myStorage.getItem("curPage"),10) + 1;

    var page = "page" + num;

    if($("#nextToken").val() != null){

        if( myStorage.getItem(page) != null){

            //console.log("cache");

            $("#searchTable").html(myStorage.getItem(page));

            myStorage.setItem("curPage",num);
            myStorage.setItem("curTable",$("#demo").html()); 
            //console.log(myStorage.getItem("curPage"));

        }
        else{

            //console.log($("#nextToken").val());

            $.ajax({
                url: myScript,/*"http://cs-server.usc.edu:16651/search.php"*/
                data: { nextToken: $("#nextToken").val()},
                type: 'GET',
                success: function(response, status){
                    //console.log(response);

                    var obj = $.parseJSON(response);

                    var text = "";

                    if( obj.status != "ZERO_RESULTS" && obj.results != null){
                        var c = obj.results;
                        var number = Math.min(20, c.length);    // at most 20 entries

                        var nextPage = obj.next_page_token;    
 
                        for(var i=0; i<number; i++){

                            var index = i+1;
                            var icon = c[i].icon;
                            var name = c[i].name;
                            var addr = c[i].vicinity;
                            var place = c[i].place_id;

                            text += "<tr>";
                            text += "<td>" + index + "</td>";
                            text += "<td class='hIcon'><img src='"+ icon +"' alt='IconImg' width='40%' height='40%'></td>";
                            text += "<td>" + name + "</td>";
                            text += "<td>"+ addr + "</td>";
                            text += "<td>" + "<button type='button' class='btn btn-default'><i class='fa fa-star-o'></i></button>"+ "</td>";
                            //text += "<td>" + "<button type='button' class='btn btn-default'><i class='fa fa-angle-right'></i></button>"+"</td>";
                            text += "<td>" + "<button type='button' class='btn btn-default' id='"+ place + "' onclick='getDetail(this.id)'>";
                            text += "<i class='fa fa-chevron-right'></i></button>"+"</td>";
                            text += "</tr>";
                        }                                

                        $("#searchTableBody").html(text);  

                        var pageNum = parseInt(myStorage.getItem("curPage"),10) + 1;

                        if( pageNum > 0){
                            text = "<button type='button' class='btn btn-default' onclick='showMePrevious()' id='preBtn'>Previous</button>";
                            $("#ctrlTable").html(text);
                        }

                        if(nextPage != "" && nextPage != null){

                            text = $("#ctrlTable").html();
                            text += "<button type='button' class='btn btn-default' onclick='showMeNext()' id='nextBtn'>Next</button>";
                            text += "<input type='text' id='nextToken' hidden value='"+ nextPage +"'>";
                            $("#ctrlTable").html(text);
                            //$("#nextToken").val(nextPage);
                        }
                        else{
                            $("#nextToken").val(null);
                            $("#nextBtn").hide();
                        }     

                
                        myStorage.setItem("curPage",pageNum);
                        //console.log(myStorage.getItem("curPage"));
                    
                        /* save current page */
                        var num = parseInt(myStorage.getItem("curPage"),10);
                        var pageName = "page" + num;
                        myStorage.setItem(pageName, $("#searchTable").html());
                        myStorage.setItem("curTable",$("#demo").html());               
                    }
                    else{
                        $("#demo").html(noRecordBasic());
                    }
                },
                error: function(err){
                    $("#demo").html(alertBasic());
                }
            });             
        }
 
              
    }      
     
}

function showMePrevious(){

    var num = parseInt(myStorage.getItem("curPage"),10) - 1;

    var prePage = "page" + num;

    $("#searchTable").html(myStorage.getItem(prePage)); 

    myStorage.setItem("curPage",num);
    myStorage.setItem("curTable",$("#demo").html()); 
    //console.log(myStorage.getItem("curPage"));


}

function clearResult(){
    $( "#demo" ).html("");

    var blank = "";
    // input type = "text"
    if(document.myForm.keyword.value != blank) document.myForm.keyword.value = blank;
    if(document.myForm.distance.value != blank) document.myForm.distance.value = blank;
    if(document.myForm.location.value != blank) document.myForm.location.value = blank;
    
    // <select>
    if(document.myForm.category.selectedIndex != 0) document.myForm.category.selectedIndex = 0;

    // radio button
    if( document.myForm.optradio.value != "Here"){
        document.myForm.optradio.value = "Here";
        document.myForm.location.disabled = true;   
        document.myForm.location.required = false;
    }         
}

function reset(){

    $( "#demo" ).html("");

    myStorage.removeItem("curPage");
    myStorage.removeItem("curTable");
    

    for(var i=0; i<3; i++){ // at most 3 pages
        var pageName = "page" + i;
        myStorage.removeItem(pageName);
    }

    myStorage.removeItem("List");  

}
            
function init() {
    // Create the autocomplete object, restricting the search to geographical location types.
    var autocomplete = new google.maps.places.Autocomplete((document.getElementById('locationTextField')),{types: ['geocode']});
    
}
