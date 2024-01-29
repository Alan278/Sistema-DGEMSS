function esNumeroTelefonico(evt, elementId){
    var code = (evt.which) ? evt.which : evt.keyCode;

    if(code == 43) return true
    if(code >= 48 && code <= 57) return true
    if(code >= 40 && code <= 41) return true
    return false
}

function esCodigoPostal(evt, elementId){
    var code = (evt.which) ? evt.which : evt.keyCode;
    var cadena = $("#" + elementId).val()
    var cadenaLargo = ( cadena + String.fromCharCode(code) ).length

    if(code < 48 || code > 57) return false
    if(cadenaLargo > 5) return false

    return true
}

function esCoordenada(evt, elementId){
    var code = (evt.which) ? evt.which : evt.keyCode;
    var cadena = $("#" + elementId).val() + String.fromCharCode(code)

    if(cadena == '-') return true
    if(isNaN(cadena)) return false

    return true
}

function esEnteroPositivo(evt, elementId){
    var code = (evt.which) ? evt.which : evt.keyCode;

    if(code >= 48 && code <= 57) return true

    return false
}

function esNumeroPositivo(evt, elementId){
    var code = (evt.which) ? evt.which : evt.keyCode;
    var cadena = $("#" + elementId).val() + String.fromCharCode(code)

    if(isNaN(cadena)) return false

    return true
}

function esCalificacion(evt, elementId){
    var code = (evt.which) ? evt.which : evt.keyCode;
    var cadena = $("#" + elementId).val() + String.fromCharCode(code)

    if(isNaN(cadena)) return false

    return true
}

function esHora24(evt, elementId){
    var code = (evt.which) ? evt.which : evt.keyCode;
    var cadena = $("#" + elementId).val()
    var cadenaLargo = ( cadena + String.fromCharCode(code) ).length

    if(cadenaLargo == 1){
        if(code >= 48 && code <= 50) return true
        return false
    }

    if(cadenaLargo == 2){
        if(cadena == "2"){
            if(code >= 48 && code <= 51) return true
            return false
        }
        if(code >= 48 && code <= 57) return true
        return false
    }

    if(cadenaLargo == 3 && code == 58) return true

    if(cadenaLargo == 4){
        if(code >= 48 && code <= 53) return true
        return false
    }

    if(cadenaLargo == 5){
        if(code >= 48 && code <= 57) return true
        return false
    }

    return false
}

function esCurp(evt, elementId){
    var code = (evt.which) ? evt.which : evt.keyCode;
    var cadena = $("#" + elementId).val()
    var cadenaLargo = ( cadena + String.fromCharCode(code) ).length

    if(cadenaLargo > 18) return false
    if(code >= 48 && code <= 57) return true
    if(code >= 65 && code <= 90) return true
    if(code >= 97 && code <= 122) return true

    return false
}

function esRfc(evt, elementId){
    var code = (evt.which) ? evt.which : evt.keyCode;
    var cadena = $("#" + elementId).val()
    var cadenaLargo = ( cadena + String.fromCharCode(code) ).length

    if(cadenaLargo > 13) return false
    if(code >= 48 && code <= 57) return true
    if(code >= 65 && code <= 90) return true
    if(code >= 97 && code <= 122) return true

    return false
}
