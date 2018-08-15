/* global variable */
myStorage = window.localStorage;

/*
    pagination: curPage, page1, page2, page3
    resultTable/noRecordTable:  resultBasic, alertBasic
*/

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

    var search = "location=" + in_location + "&" +"radius=" + in_dist + "&" +"type=" + in_category + "&" +"keyword=" + in_keyword;
    //console.log(encodeURI(search));

    nearBySearch(encodeURI(search));
});

function alertBasic(){

    var text = '<div id="alertBox" class="alert alert-warning">';

    text += '<strong>No records</strong>';
    text += '</div>';

    myStorage.setItem("alertBasic",text);
}

function resultBasic(tableBody, ctrlBody){

    var text = '<div id="searchTable">';

/*
    text += '<div class="col-md-12 col-xs-12 col-sm-12">';
    text += '<hr align="center" class="col-md-12 col-xs-12 col-sm-12"/>';
    text += '</div>';
*/
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

function nearBySearch(param){

    $.ajax({
        url: "http://cs-server.usc.edu:16651/search.php",
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

                    tableBody += "<tr>";
                    tableBody += "<td>" + index + "</td>";
                    tableBody += "<td class='hIcon'><img src='"+ icon +"' alt='IconImg' width='40%' height='40%'></td>";
                    tableBody += "<td>" + name + "</td>";
                    tableBody += "<td>"+ addr + "</td>";
                    tableBody += "<td>" + "<button type='button' class='btn btn-default'><i class='fa fa-star-o'></i></button>"+ "</td>";
                    tableBody += "<td>" + "<button type='button' class='btn btn-default'><i class='fa fa-angle-right'></i></button>"+"</td>";
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
                url: "http://cs-server.usc.edu:16651/search.php",
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

                        //console.log(nextPage);   

                        //console.log($("#searchTableBody").html());    
 
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

                        //console.log($("#searchTableBody").html());

                        var pageNum = parseInt(myStorage.getItem("curPage"),10) + 1;

                        if( pageNum > 0){
                            text = "<button type='button' class='btn btn-default' onclick='showMePrevious()' id='preBtn'>Previous</button>";
                            $("#ctrlTable").html(text);
                        }

                        if(nextPage != "" && nextPage != null){

                            //console.log($("#ctrlTable").html());
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

function reset(){

    $( "#demo" ).html("");

    myStorage.removeItem("curPage");
    

    for(var i=0; i<3; i++){ // at most 3 pages
        var pageName = "page" + i;
        myStorage.removeItem(pageName);
    }

    myStorage.removeItem("alertBasic");
    myStorage.removeItem("resultBasic");

}
            
function initAutocomplete() {
    // Create the autocomplete object, restricting the search to geographical
    // location types.
    autocomplete = new google.maps.places.Autocomplete(
    /** @type {!HTMLInputElement} */(document.getElementById('locationTextField')),{types: ['geocode']});

}