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

/* submit twice!!
    $('#searchForm').bootstrapValidator({
        message: 'This value is not valid',
            feedbackIcons: {
                valid: 'glyphicon glyphicon-ok',
                invalid: 'glyphicon glyphicon-remove',
                validating: 'glyphicon glyphicon-refresh'
            },        
            fields: {
                keyword: {
                    message: 'Please enter a keyword',
                    validators: {
                        notEmpty: {
                            message: 'Please enter a keyword'
                        }
                    }
                },
                location: {
                    message: 'Please enter a location',
                    validators: {
                        notEmpty: {
                            message: 'Please enter a location'
                        }
                    }    
                }
            }                   
    });
*/
    $("#optionHere").click(function(){
        $("#locationTextField").attr("disabled","true");
    });

    $("#optionOther").click(function(){
        $("#locationTextField").removeAttr("disabled");
    });  
                                             
});
 

$(document).submit(function(event){ 
       
    //console.log("<submit>" + $("#lat").val());

    event.preventDefault();

    $.get("http://cs-server.usc.edu:16651/angularTest.php?q=", function(data, status){
        alert(data);
    });
});
            
function initAutocomplete() {
    // Create the autocomplete object, restricting the search to geographical
    // location types.
    autocomplete = new google.maps.places.Autocomplete(
    /** @type {!HTMLInputElement} */(document.getElementById('locationTextField')),{types: ['geocode']});

}