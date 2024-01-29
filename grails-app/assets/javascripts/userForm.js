$('#institucionesMod').editableSelect()
    .editableSelect()
        .on('select.editable-select', function (e, li) {
            $('#institucionId').val(li.val());
        });

var instituciones = []

function mostrarTabla(){

    var institucionesJson = JSON.stringify(instituciones);
    console.log(institucionesJson)
    var institucionesInput = document.getElementById("listaInstituciones")
    institucionesInput.value = institucionesJson

    $("#tablaInstituciones tr:gt(0)").remove()

    instituciones.forEach(function(institucion, indice) {
        var institucion_node = document.createTextNode(institucion.nombre);

        var td_institucion_node = document.createElement("td");
        td_institucion_node.appendChild(institucion_node);

        var ele_a = document.createElement("a");
        ele_a.setAttribute("href","javascript:void(0);");
        ele_a.setAttribute("onclick","del_tr(" + indice + ");");
        ele_a.setAttribute("class","btn btn-danger");
        var del_node = document.createElement("i");
        del_node.setAttribute("class","fa fa-trash");

        ele_a.appendChild(del_node);
        var td_del_node = document.createElement("td");
        td_del_node.setAttribute("align", "right")
        td_del_node.appendChild(ele_a);
        
        var tr_node = document.createElement("tr");
        tr_node.appendChild(td_institucion_node);
        tr_node.appendChild(td_del_node);

        var table_node = document.getElementById("tablaInstituciones");
        table_node.appendChild(tr_node);
    });
}


function agregarInstitucion(){
    var id =  document.getElementById('institucionId');
    var nombre =  document.getElementById('institucionesMod');

    if(nombre.value == "" || id.value == ""){
        return
    }

    var institucion = {
        "id": id.value,
        "nombre": nombre.value,
    }

    nombre.value = ""
    id.value = ""

    if(existe(institucion)){
        return
    }

    instituciones.push(institucion)

    mostrarTabla()

}

del_tr = function (indice) {
    instituciones.splice(indice, 1)
    console.log(instituciones)

    mostrarTabla()
}

function existe(institucionAux){
    var existe = false
    instituciones.forEach(function(institucion){
        if(institucion.id == institucionAux.id){
            existe = true
        }
    });
    return existe
}


$('#roles').editableSelect()
    .editableSelect()
        .on('select.editable-select', function (e, li) {
            $('#rolId').val(li.val());
        });

var roles = []

function mostrarTablaRoles(){

    var rolesJson = JSON.stringify(roles);
    console.log(rolesJson)
    var rolesInput = document.getElementById("listaRoles")
    rolesInput.value = rolesJson

    $("#tablaRoles tr:gt(0)").remove()

    roles.forEach(function(rol, indice) {
        var institucion_node = document.createTextNode(rol.nombre);

        var td_rol_node = document.createElement("td");
        td_rol_node.appendChild(institucion_node);

        var ele_a = document.createElement("a");
        ele_a.setAttribute("href","javascript:void(0);");
        ele_a.setAttribute("onclick","del_trRol(" + indice + ");");
        ele_a.setAttribute("class","btn btn-danger");
        var del_node = document.createElement("i");
        del_node.setAttribute("class","fa fa-trash");

        ele_a.appendChild(del_node);
        var td_del_node = document.createElement("td");
        td_del_node.setAttribute("align", "right")
        td_del_node.appendChild(ele_a);
        
        var tr_node = document.createElement("tr");
        tr_node.appendChild(td_rol_node);
        tr_node.appendChild(td_del_node);

        var table_node = document.getElementById("tablaRoles");
        table_node.appendChild(tr_node);
    });
}


function agregarRol(){
    var id =  document.getElementById('rolId');
    var nombre =  document.getElementById('roles');

    if(nombre.value == "" || id.value == ""){
        return
    }

    var rol = {
        "id": id.value,
        "nombre": nombre.value,
    }

    nombre.value = ""
    id.value = ""

    if(existeRol(rol)){
        return
    }

    roles.push(rol)

    mostrarTablaRoles()

}

del_trRol = function (indice) {
    roles.splice(indice, 1)
    console.log(roles)

    mostrarTablaRoles()
}

function existeRol(rol){
    var existe = false
    roles.forEach(function(rolAux) {
        if(rol.id == rolAux.id){
            existe = true
        }
    });
    return existe
}

$(document).ready(function(){
    $("input[name='curp']").keyup(function() {
        this.value = this.value.toLocaleUpperCase();
    });

    $("input[name='validarCurp']").on("click", function(){
        var curp = $("input[name='curp']").val();
        validarCurp(curp.trim().toUpperCase());
    });
});

function validarCurp(curp){
    $("#msgError").html("");
    $("#msgOk").html("Validando CURP...");
    $("#resultado").html("");
    $.ajax({
        url: "https://wscurp.morelos.gob.mx/restful/curp.json",
        data: {
            "curp": curp,
            "token": "5dc77af0-d56e-42b3-9c60-3fb66f564f03"
        },
        type: 'POST',
        dataType: 'json',
        traditional: true
    }).done(function(respuesta){
        $("#resultado").html(JSON.stringify(respuesta, null, 2));
        if (respuesta.curp != null && respuesta.curp != undefined) {
            $("#msgOk").html("");
            $("#msgError").html("");
            $("#curp").val(respuesta.curp);
            $("input[name='nombre']").val(respuesta.nombre);
            $("input[name='primerApellido']").val(respuesta.apellidoPaterno);
            $("input[name='segundoApellido']").val(respuesta.apellidoMaterno);
            $("input[name='entidadNacimiento']").val(respuesta.claveEntidadNacimiento);
            $("input[name='fechaNacimiento']").val(respuesta.fechaNacimiento);
            $("input[name='sexo']").val(respuesta.sexo);
        } else {
            $("#msgError").html(respuesta.mensaje);
            $("#msgOk").html("");
            $("input[name='nombre']").val("");
            $("input[name='primerApellido']").val("");
            $("input[name='segundoApellido']").val("");
            $("input[name='entidadNacimiento']").val("");
            $("input[name='fechaNacimiento']").val("");
            $("input[name='sexo']").val("");
        }
    }).fail(function(error){
        $("#mensaje").html("No se pudo validar la CURP");
    }).always(function () {

    });
}