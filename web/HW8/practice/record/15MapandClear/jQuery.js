/* global variable */
myStorage = window.localStorage;
myScript = "http://cs-server.usc.edu:16651/search.php";
//myScript = "http://http://localhost:8000/search.php";

/*
    loc: lat, lon
    pagination: curPage, page1, page2, page3
    resultTable/noRecordTable:  resultBasic, alertBasic
    button: List
*/

$(  document ).ready(function() {    
    var script = document.createElement('script');
    script.type = 'text/javascript';
    script.src = 'https://maps.googleapis.com/maps/api/js?key=AIzaSyCM3nxvuypmX28IgkrJ841diJbHaNdZkmA&libraries=places&callback=init';

    document.body.appendChild(script);

    myStorage.removeItem("lat");
    myStorage.removeItem("lon");

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
        $("#locationTextField").attr("disabled","true");
    });

    $("#optionOther").click(function(){
        $("#locationTextField").removeAttr("disabled");
    });  
                                             
});
 

$(document).submit(function(event){ 

    event.preventDefault();

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

            var search = "location=" + in_location + "&" +"radius=" + in_dist + "&" +"type=" + in_category + "&" +"keyword=" + in_keyword;
            nearBySearch(encodeURI(search));
        });           
    }
    else{

        in_location = myStorage.getItem("lat") + "," + myStorage.getItem("lon");

        var search = "location=" + in_location + "&" +"radius=" + in_dist + "&" +"type=" + in_category + "&" +"keyword=" + in_keyword;
        nearBySearch(encodeURI(search));        
    }

});

function changeView(id){
    console.log(id);
    //console.log($("#view").html());
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

        var pyrmont = marker.getPosition();

        map = new google.maps.Map(document.getElementById('map'), { // create a map with current location
            center: pyrmont,
            zoom: 15
        });

        marker = new google.maps.Marker({   // add marker with placeId
            map: map,
            position: pyrmont
        });

        service = new google.maps.places.PlacesService(map);
        directionsDisplay.setMap(map); 

    }
    
    $("#view").html(text);
}

function alertBasic(){

    var text = '<div id="alertBox" class="alert alert-warning" style="margin-top: 10px">';

    text += '<strong>No records</strong>';
    text += '</div>';

    myStorage.setItem("alertBasic",text);
}

function resultBasic(tableBody, ctrlBody){

    var text = '<div class="text-right" style="margin-top: 10px">';

    text += "<button type='button' class='btn btn-default' disabled>Details <i class='fa fa-chevron-right'></i></button>";
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
 
    myStorage.setItem("resultBasic",text);              
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

    text += '<div id="Reviews" class="tab-pane fade">';  
    text += '<div class="container-fluid">';
    text += '<table class="table table-condensed" align="center" style="border-top: 0.5px solid #d3d3d3;margin-top: 10px">';
    text += '<thead>';
    text += '<tr><td>';
    text += '<span class="dropdown">';
    text += '<button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">Google Reviews<span class="caret"></span></button>';
    text += '<ul class="dropdown-menu">';
    text += '<li>Google Reviews</li>';
    text += '<li>Yelp Reviews</li>';
    text += '</ul>';
    text += '</span>';
    text += '<span class="dropdown">';
    text += '<button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">Default Order<span class="caret"></span></button>';
    text += '<ul class="dropdown-menu">';
    text += '<li>Default Order</li>';
    text += '<li>Highest Rating</li>';
    text += '<li>Lowest Rating</li>';
    text += '<li>Most Recent</li>';
    text += '<li>Least Recent</li>';
    text += '</ul>';
    text += '</span>';    
    text += '</td></tr>'; 
    text += '</thead>';
    text += '<tbody id="reviewCtrlBody">';   
    text += '</tbody>';
    text += '</table>';
    text += '</div></div>'; 

    return text;
}

function tabsBasic(){
    var text = "";

    text +="<div style='margin-top: 20px'>"
    text += "<div id='shopName' class='container-fluid' style='text-align: center'></div>";
    text += "<button type='button' class='btn btn-default' onclick='showMeList()'><i class='fa fa-chevron-left'></i> List</button>";
    text += "<button type='button' class='btn btn-default'><i class='fa fa-star-o'></i></button>";
    text += "<button type='button' class='btn btn-default' id='twitterImg'><img src='http://cs-server.usc.edu:45678/hw/hw8/images/Twitter.png'></button>";
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

function nearBySearch(param){

    $.ajax({
        url: myScript,
        data: { nearBy: param},
        type: 'GET',
        success: function(response, status){

            //console.log(response);

            var obj = $.parseJSON(response);

            reset();
            
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

                    tableBody += "<tr>";
                    tableBody += "<td>" + index + "</td>";
                    tableBody += "<td class='hIcon'><img src='"+ icon +"' alt='IconImg' width='40%' height='40%'></td>";
                    tableBody += "<td>" + name + "</td>";
                    tableBody += "<td>"+ addr + "</td>";
                    tableBody += "<td>" + "<button type='button' class='btn btn-default'>";
                    tableBody += "<i class='fa fa-star-o'></i></button>"+ "</td>";
                    tableBody += "<td>" + "<button type='button' class='btn btn-default' id='"+ place + "' onclick='getDetail(this.id)'>";
                    tableBody += "<i class='fa fa-chevron-right'></i></button>"+"</td>";
                    tableBody += "</tr>";
                }

                var ctrlBody = "";

                if( nextPage != "" && nextPage != null){
                    ctrlBody = "<button type='button' class='btn btn-default' onclick='showMeNext()'>Next</button>";
                    ctrlBody += "<input type='text' id='nextToken' hidden>";
                }

                resultBasic(tableBody, ctrlBody);

                myStorage.setItem("curPage","0");
                //console.log(myStorage.getItem("curPage"));

                /* save current page */
                var num = parseInt(myStorage.getItem("curPage"),10);
                var pageName = "page" + num;
                myStorage.setItem(pageName, myStorage.getItem("resultBasic")); 
                     
                //$( "#searchTable" ).show();  
                $( "#demo" ).html(myStorage.getItem("resultBasic"));
                $("#nextToken").val(nextPage); 
               
            }
            else{
                //$( "#alertBox" ).show();
                alertBasic();
                $( "#demo" ).html(myStorage.getItem("alertBasic"));
            }
            
        }

    });    
}

function getDetail(place_id){
    //console.log(place_id);

    myStorage.setItem("List",$( "#demo" ).html()); 

    var text = tabsBasic();
    $( "#demo" ).html(text);


    var lattitude = parseFloat(myStorage.getItem("lat"));
    var lngitude = parseFloat(myStorage.getItem("lon"));

    //console.log(lattitude);

    var pyrmont = {lat: lattitude, lng: lngitude}; 

    map = new google.maps.Map(document.getElementById('map'), { // create a map with current location
        center: pyrmont,
        zoom: 15
    });

    service = new google.maps.places.PlacesService(map); 

    service.getDetails({
        placeId: place_id
        }, rDetails);

    directionsDisplay = new google.maps.DirectionsRenderer;
    directionsService = new google.maps.DirectionsService;    
    
    directionsDisplay.setMap(map);  
    directionsDisplay.setPanel(document.getElementById('directionStep'));
              
}

function rDetails(place, status){

    if (status === google.maps.places.PlacesServiceStatus.OK) {
           
        console.log(place);

        marker = new google.maps.Marker({   // add marker with placeId
            map: map,
            position: place.geometry.location
        });

        map.setCenter(marker.getPosition());    // recenter

        /* Info Tabs */
        infoCtrl(place);
        /* Photo Tabs */
        photoCtrl(place);

        mapCtrl(place);

        reviewCtrl(place);
      
    }
}

function reviewCtrl(place){
    var reviews = place.reviews;

    var text = "";

    for(var i=0; i<reviews.length; i++){
        var icon = reviews[i].profile_photo_url;

        var timestring = "1970-01-01T00:00:00Z";
        var date = moment.utc(timestring).add('s',reviews[i].time).format('YYYY-MM-DD HH:mm:ss'); 
        var id = "gooReview" + i;
        
        text += "<tr>";
        text += "<td><img src='"+ icon +"' alt='IconImg' class='img-responsive'></td>";
        text += "<td>";
        text += "<div>"+ reviews[i].author_name +"</div>";
        text += ratingInfo(date, reviews[i].rating, id);
        //text += "<div>"+ date +"</div>";        
        text += "<div>"+ reviews[i].text +"</div>";
        text += "</td>";
        text += "</tr>";
    }

    

    $( "#reviewCtrlBody" ).html(text);  

}

function ratingInfo(label, rating, id){

    var text = "<div>";

    text += "<span style='display:inline-block;'>"+label + "</span>"+"<span id='"+id+"'></span>";
    text += "</div>";


    $(function () {
 
        $(id).rateYo({
            starWidth: "10px",
            rating    : rating
        });
 
    });
    //console.log(text);
    return text;
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

    console.log();
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

    var num = parseInt(myStorage.getItem("curPage"),10) + 1;

    var page = "page" + num;

    if($("#nextToken").val() != null){

        if( myStorage.getItem(page) != null){

            //console.log("cache");

            $("#searchTable").html(myStorage.getItem(page));

            myStorage.setItem("curPage",num);
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

                            text += "<tr>";
                            text += "<td>" + index + "</td>";
                            text += "<td class='hIcon'><img src='"+ icon +"' alt='IconImg' width='40%' height='40%'></td>";
                            text += "<td>" + name + "</td>";
                            text += "<td>"+ addr + "</td>";
                            text += "<td>" + "<button type='button' class='btn btn-default'><i class='fa fa-star-o'></i></button>"+ "</td>";
                            text += "<td>" + "<button type='button' class='btn btn-default'><i class='fa fa-angle-right'></i></button>"+"</td>";
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
                            text += "<input type='text' id='nextToken' hidden>";
                            $("#ctrlTable").html(text);
                            $("#nextToken").val(nextPage);
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
  
                    }

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
    

    for(var i=0; i<3; i++){ // at most 3 pages
        var pageName = "page" + i;
        myStorage.removeItem(pageName);
    }

    myStorage.removeItem("alertBasic");
    myStorage.removeItem("resultBasic");
    myStorage.removeItem("List");  

}
            
function init() {
    // Create the autocomplete object, restricting the search to geographical location types.
    var autocomplete = new google.maps.places.Autocomplete((document.getElementById('locationTextField')),{types: ['geocode']});
    
}
