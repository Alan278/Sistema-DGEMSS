var marker;
var map;

//Carga el mapa
function initMap() {
    var latitud = document.getElementById('latitud').value;
    var longitud = document.getElementById('longitud').value;

    if(isNaN(latitud) || isNaN(longitud) || latitud == '' || longitud == ''){
        latitud = 18.614586;
        longitud =  -99.070597;
    }else{
        latitud = parseFloat(latitud);
        longitud = parseFloat(longitud);
    }

    map = new google.maps.Map(document.getElementById('map'), {
        zoom: 8,
        center: {lat: latitud, lng: longitud}
    });

    marker = new google.maps.Marker({
        map: map,
        draggable: true,
        animation: google.maps.Animation.DROP,
        position: {lat: latitud, lng: longitud}
    });

    marker.addListener( 'dragend', function (event){
        var lng = this.getPosition().lng();
        var lat = this.getPosition().lat();
        document.getElementById("longitud").value = lng;
        document.getElementById("latitud").value = lat;
    });

    google.maps.event.addListener(map, "click", function(ele) {
        marker.setPosition(ele.latLng);
        var lng = marker.getPosition().lng();
        var lat = marker.getPosition().lat();
        document.getElementById("longitud").value = lng;
        document.getElementById("latitud").value = lat;
    })
}