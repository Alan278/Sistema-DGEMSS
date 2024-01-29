var marker;
var map;

/**
 * Carga un mapa en la vista con una marca en una latitud y longitud específica
 */
function initMap() {
    // Se obtienen la latitud y longitud de inputs precargados con datos del modelo
    var latitud = document.getElementById('latitud').value;
    var longitud = document.getElementById('longitud').value;

    // Se validan la latitud y longitud
    if(isNaN(latitud) || isNaN(longitud) || latitud == '' || longitud == ''){
        //Si no se encuentran o son invalidos se oculta el mapa
        document.getElementById('map').style.display ='none';
        latitud = 18.614586;
        longitud =  -99.070597;
    }else{
        latitud = parseFloat(latitud);
        longitud = parseFloat(longitud);
    }

    // Se carga el mapa
    map = new google.maps.Map(document.getElementById('map'), {
        zoom: 8,
        center: {lat: latitud, lng: longitud}
    });

    // Se crea una marca
    marker = new google.maps.Marker({
        map: map,
        draggable: false,
        animation: google.maps.Animation.DROP,
        position: {lat: latitud, lng: longitud}
    });
}