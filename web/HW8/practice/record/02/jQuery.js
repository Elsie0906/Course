$(  document ).ready(function() {    
    var script = document.createElement('script');
    script.type = 'text/javascript';
    script.src = 'https://maps.googleapis.com/maps/api/js?key=AIzaSyCM3nxvuypmX28IgkrJ841diJbHaNdZkmA&libraries=places&callback=initAutocomplete';

    document.body.appendChild(script);

    $.get("http://ip-api.com/json", function(data, status){
        if( status == "success" && data != null){
            $("#lat").val(data.lat);
            $("#lon").val(data.lon);
            $("#submit").removeAttr( "disabled" );
        }
        else{
            $("#lat").val("34.0223519");
            $("#lon").val("-118.285117"); 
            $("#submit").removeAttr( "disabled" );                      
        }
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

    // get geolocation

    var in_location = "";

    if( document.myForm.optradio.value == "Other"){

        $.ajax({
            async: false,
            url: "http://cs-server.usc.edu:16651/search.php",
            data: { location: $("#locationTextField").val()},
            success: function(response){
                $("#geoLoc").val(response);
            }
        });

        in_location = $("#geoLoc").val();
             
    }
    else{
        in_location = $("#lat").val() + "," + $("#lon").val();
    }

    var in_category = encodeURI($("#category").val());
    
    var in_dist = "";

    if($("#distance").val() == ""){
        in_dist = "10";
    }else{
        in_dist = $("#distance").val();
    }

    var in_keyword = encodeURI($("#keyword").val());

    var search = "location=" + in_location + "$" + "radius=" + in_dist + "$" + "type=" + in_category + "$" + "keyword=" + in_keyword;
    //console.log(search);

    nearBySearch(search);
});

function nearBySearch(param){

    $.ajax({
        url: "http://cs-server.usc.edu:16651/search.php",
        data: { nearBy: param},
        type: 'GET',
        success: function(response, status){

            var obj = $.parseJSON(response);
            //console.log(obj.status);

            reset();
            
            if(obj.status != "ZERO_RESULTS"){



                $( "#searchTable" ).show();
            }
            else{
                $( "#alertBox" ).show();
            }
            
        }
    });    
}

function reset(){
    $( "#alertBox" ).hide();
    $( "#searchTable" ).hide();
}
            
function initAutocomplete() {
    // Create the autocomplete object, restricting the search to geographical
    // location types.
    autocomplete = new google.maps.places.Autocomplete(
    /** @type {!HTMLInputElement} */(document.getElementById('locationTextField')),{types: ['geocode']});

}