var drawingManager;
var selectedShape;
var map;
var colors = ['#1E90FF', '#FF1493', '#32CD32', '#FF8C00', '#4B0082'];
var selectedColor;
var colorButtons = {};

function clearSelection() {
  if (selectedShape) {
    selectedShape.setEditable(false);
    selectedShape = null;
  }
}

function setSelection(shape) {
  clearSelection();
  selectedShape = shape;
  shape.setEditable(true);
  selectColor(shape.get('fillColor') || shape.get('strokeColor'));
  google.maps.event.addListener(shape.getPath(), 'set_at', calcar);
  google.maps.event.addListener(shape.getPath(), 'insert_at', calcar);
}

//the location of the center of polygon(polyline) 
function codeAddress(){
	var path = selectedShape.getPath();
	var bounds = new google.maps.LatLngBounds();
	for(var i=0;i<path.length;i++){
		var point = new google.maps.LatLng(path.getAt(i).lat(),path.getAt(i).lng());
		bounds.extend(point);
	}
	var latlng = bounds.getCenter();
	//var latlng = bounds.toSpan();
	//alert(latlng.lat());
	var geocoder = new google.maps.Geocoder;
	geocoder.geocode({'location': latlng}, function(results, status) {
        if (status === 'OK') {
          if (results[0]) {
        	  document.getElementById("address").innerHTML = "address =" + results[0].formatted_address;
          } else {
        	  document.getElementById("address").innerHTML = "address = No results found";
          }
        } else {
          document.getElementById("address").innerHTML = "address = Geocoder failed due to:"+status;
        }
      });
}
//build the object of XMLHttpRequest
function createXMLHttpRequest(){
	if(window.XMLHttpRequest){
		XMLHttpReq=new XMLHttpRequest();
	}
	else if(window.ActiveXObject){
		try{
			XMLHttpReq = new ActiveXObject("Msxm12.XMLHTTP");
		}
		catch(e){
			try{
				XMLHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
			}
			catch(e)
			{
			}
		}
	}
}

function processResponse(){
	if(XMLHttpReq.readyState == 4){
		if(XMLHttpReq.status == 200){
			document.getElementById("address").value=XMLHttpReq.responseText;
		}
		else{
			window.alert("您请求的页面有异常");
		}
	}
}
//send the function of request
 function sendRequest (place) {
	var placeName = place;
	createXMLHttpRequest();
	var url = "place.do";
	XMLHttpReq.open("POST",url,false);
	XMLHttpReq.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
	XMLHttpReq.onreadystatechange=processResponse;
	XMLHttpReq.send("placeName="+placeName);
}

//the location of the place contained in the polygon
function codeAddress1(){
	var path = selectedShape.getPath();
	var bounds = new google.maps.LatLngBounds();
	for(var i=0;i<path.length;i++){
		var point = new google.maps.LatLng(path.getAt(i).lat(),path.getAt(i).lng());
		bounds.extend(point);
	}
	var request = {
			bounds:bounds
			
	};
	var service = new google.maps.places.PlacesService(map);
	service.nearbySearch(request, callback);
}
function callback(results, status,pagination) {
	  if (status == google.maps.places.PlacesServiceStatus.OK) {
	    for (var i = 0; i < results.length; i++) {
	    	    if(results[i].types.includes("country") || results[i].types.includes("locality") )
	    	    	continue;
	    		sendRequest(results[i].name);
	    }
	    if(pagination.hasNextPage){
	    		pagination.nextPage();
	    }
	  }
	}




function calcar() {
	var area = google.maps.geometry.spherical.computeArea(selectedShape.getPath());
    document.getElementById("area").innerHTML = "Area =" + area;
   // codeAddress();
    codeAddress1();
}

function deleteSelectedShape() {
  if (selectedShape) {
    selectedShape.setMap(null);
  }
}

function selectColor(color) {
  selectedColor = color;
  for (var i = 0; i < colors.length; ++i) {
    var currColor = colors[i];
    colorButtons[currColor].style.border = currColor == color ? '2px solid #789' : '2px solid #fff';
  }

  // Retrieves the current options from the drawing manager and replaces the
  // stroke or fill color as appropriate.
  var polylineOptions = drawingManager.get('polylineOptions');
  polylineOptions.strokeColor = color;
  drawingManager.set('polylineOptions', polylineOptions);

  var rectangleOptions = drawingManager.get('rectangleOptions');
  rectangleOptions.fillColor = color;
  drawingManager.set('rectangleOptions', rectangleOptions);

  var circleOptions = drawingManager.get('circleOptions');
  circleOptions.fillColor = color;
  drawingManager.set('circleOptions', circleOptions);

  var polygonOptions = drawingManager.get('polygonOptions');
  polygonOptions.fillColor = color;
  drawingManager.set('polygonOptions', polygonOptions);
}

function setSelectedShapeColor(color) {
  if (selectedShape) {
    if (selectedShape.type == google.maps.drawing.OverlayType.POLYLINE) {
      selectedShape.set('strokeColor', color);
    } else {
      selectedShape.set('fillColor', color);
    }
  }
}

function makeColorButton(color) {
  var button = document.createElement('span');
  button.className = 'color-button';
  button.style.backgroundColor = color;
  google.maps.event.addDomListener(button, 'click', function() {
    selectColor(color);
    setSelectedShapeColor(color);
  });

  return button;
}

function buildColorPalette() {
  var colorPalette = document.getElementById('color-palette');
  for (var i = 0; i < colors.length; ++i) {
    var currColor = colors[i];
    var colorButton = makeColorButton(currColor);
    colorPalette.appendChild(colorButton);
    colorButtons[currColor] = colorButton;
  }
  selectColor(colors[0]);
}

function createMap(){
var map = new google.maps.Map(document.getElementById('map'), {
    zoom: 10,
    center: new google.maps.LatLng(-34.397, 150.644),
    mapTypeId: google.maps.MapTypeId.ROADMAP,
    disableDefaultUI: true,
    zoomControl: true
  });
var infoWindow = new google.maps.InfoWindow({map:map});
if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(function(position) {
      var pos = {
        lat: position.coords.latitude,
        lng: position.coords.longitude
      };

      infoWindow.setPosition(pos);
      infoWindow.setContent('Location found.');
      map.setCenter(pos);
    }, function() {
      handleLocationError(true, infoWindow, map.getCenter());
    });
  } else {
    // Browser doesn't support Geolocation
    handleLocationError(false, infoWindow, map.getCenter());
  }
return map;
}
function handleLocationError(browserHasGeolocation, infoWindow, pos) {
    infoWindow.setPosition(pos);
    infoWindow.setContent(browserHasGeolocation ?
                          'Error: The Geolocation service failed.' :
                          'Error: Your browser doesn\'t support geolocation.');
  }
function initialize() {
  map = createMap();

  var polyOptions = {
    strokeWeight: 0,
    fillOpacity: 0.45,
    editable: true
  };
  // Creates a drawing manager attached to the map that allows the user to draw
  // markers, lines, and shapes.
  drawingManager = new google.maps.drawing.DrawingManager({
    drawingMode: google.maps.drawing.OverlayType.POLYGON,
    markerOptions: {
        draggable: true
      },
      polylineOptions: {
        editable: true
      },
      rectangleOptions: polyOptions,
      circleOptions: polyOptions,
      polygonOptions: polyOptions,
    map: map
  });

  google.maps.event.addListener(drawingManager, 'overlaycomplete', function(e) {
    if (e.type != google.maps.drawing.OverlayType.MARKER) {
      // Switch back to non-drawing mode after drawing a shape.
      drawingManager.setDrawingMode(null);

      // Add an event listener that selects the newly-drawn shape when the user
      // mouses down on it.
      var newShape = e.overlay;
      newShape.type = e.type;
        setSelection(newShape);
    }
  });

  // Clear the current selection when the drawing mode is changed, or when the
  // map is clicked.
  google.maps.event.addListener(drawingManager, 'drawingmode_changed', clearSelection);
  google.maps.event.addListener(map, 'click', clearSelection);
  google.maps.event.addDomListener(document.getElementById('delete-button'), 'click', deleteSelectedShape);

  buildColorPalette();
}
 

   