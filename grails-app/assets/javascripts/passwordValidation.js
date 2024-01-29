function validatePassword(){
    var password = $("#contrasena").val()
    var password2 = $("#contrasena2").val()

    var regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[$@$!%*?&])([A-Za-z\d$@$!%*?&]|[^ ]){10,20}$/;

    $("#contrasena").removeClass("is-valid")
    $("#contrasena").removeClass("is-invalid")
    $("#contrasena2").removeClass("is-valid")
    $("#contrasena2").removeClass("is-invalid")

    if (document.getElementById("contrasenaActual") !== null) {
        $("#contrasenaActual").removeClass("is-valid")
        $("#contrasenaActual").removeClass("is-invalid")

        if($("#contrasenaActual").val().length == 0){
            $("#contrasenaActual").addClass("is-invalid")
            $("#contrasenaActualInvalida").html("La contraseña actual es un dato requerido")
        }
    }

    if(password.length < 8){
        $("#contrasena").addClass("is-invalid")
        $("#contrasenaInvalida").html("La contraseña debe contener mínimo 8 caracteres")
        return
    }

    if(!regex.test(password)){
        $("#contrasena").addClass("is-invalid")
        $("#contrasenaInvalida").html("Por favor ingrese una contraseña válida.")
        return
    }

    if(password2 == ""){
        $("#contrasena2").addClass("is-invalid")
        $("#contrasenaInvalida2").html("Por favor vuelva a escribir la contraseña")
        return
    }

    if(password != password2){
        $("#contrasena").addClass("is-invalid")
        $("#contrasena2").addClass("is-invalid")
        $("#contrasenaInvalida").html("")
        $("#contrasenaInvalida2").html("Las contraseñas no coinciden")
        return
    }

    mostrarProceso()
    $("#form").submit()
}

$("#contrasenaActual").on('input', function() {
    $("#contrasenaActual").removeClass("is-valid")
    $("#contrasenaActual").removeClass("is-invalid")
});

$("#contrasena").on('input', function() {
    $("#contrasena").removeClass("is-valid")
    $("#contrasena").removeClass("is-invalid")
    $("#contrasena2").removeClass("is-valid")
    $("#contrasena2").removeClass("is-invalid")
});

$("#contrasena2").on('input',function() {
    $("#contrasena").removeClass("is-valid")
    $("#contrasena").removeClass("is-invalid")
    $("#contrasena2").removeClass("is-valid")
    $("#contrasena2").removeClass("is-invalid")
});

$(document).keyup(function(event) {
    if (event.which === 13) {
        validatePassword()
    }
});

function mostrarProceso(time){
    time = time || 10000
    $("#procesando").modal("show");
    setTimeout(function(){
        $("#procesando").modal('hide');
    }, time);
}