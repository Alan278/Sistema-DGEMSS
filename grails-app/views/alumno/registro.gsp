<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Nuevos Alumnos</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
        <link href="https://cdn.jsdelivr.net/gh/gitbrent/bootstrap4-toggle@3.6.1/css/bootstrap4-toggle.min.css" rel="stylesheet">
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/alumno/registro" class="btn btn-primary col-md-12 my-2">
                Nuevo alumno
            </a>
            <a href="/alumno/subirExcel" class="btn btn-primary col-md-12 my-2">
                Cargar alumnos
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Nuevo alumno
                </h3>
            </div>
        </div>
        <div class="container mb-3">
            <g:if test='${flash.mensaje}'>
                <g:if test="${!flash.estatus}">
                    <div class="alert alert-danger" role="alert">
                        ${flash.mensaje}
                    </div>
                </g:if>
            </g:if>
        </div>
        <div class="container">
            <div class="alert alert-light" role="alert">
              Los campos marcados con * son obligatorios.
            </div>
        </div>

        <div class="container" >
            <form action="/alumno/registro" id="form-select" method="post">
                <input type="text" id="institucionIdAux" name="institucionId" value="${params?.institucionId}" hidden>
                <input type="text" id="carreraIdAux" name="carreraId" value="${params?.institucionId}" hidden>
                <input type="text" id="planEstudiosIdAux" name="planEstudiosId" value="${params?.planEstudiosId}" hidden>
                <input type="text" id="curpAux" name="curp" value="${params?.curp}" hidden>
                <input type="text" id="matriculaAux" name="matricula" value="${params?.matricula}" hidden>
                <input type="text" id="estatusAux" name="estatus" value="${params?.estatusAlumnoId}" hidden>
                <input type="text" id="correoElectronicoAux" name="correoElectronico" value="${params?.correoElectronico}" hidden>
                <input type="text" id="telefonoAux" name="telefono" value="${params?.telefono}" hidden>

            </form>
        </div>
        <div class="container mb-5">

            <form action="/alumno/registrar" class="needs-validation" method="post" novalidate>
                <div class="form-group">
                    <label for="institucionesMod">Institución<font color="red">*</font></label>
                    <g:select  class="form-control" id="institucionesMod" name="institucionesMod" from="${instituciones}" optionKey="id" optionValue="nombre" value="${params?.institucionId}" required="true"/>
                    <input type="text" id="institucionId" name="institucionId" value="${params?.institucionId}" hidden>
                    <div class="invalid-feedback">
                        Por favor ingrese la institución.
                    </div>
                </div>
                <div id="ciclo-select" >
                    <label for="carrerasMod">Carrera<font color="red">*</font></label>
                    <div class="form-group" id="div-carreras">
                        <g:if test="${params?.institucionId}">
                            <g:select  class="form-control" id="carrerasMod" name="carrerasMod" from="${carreras}" optionKey="id" optionValue="nombre" value="${params?.carreraId}" required="true"/>
                            <input type="text" id="carreraId" name="carreraId" value="${params?.carreraId}" hidden>
                            <div class="invalid-feedback">
                                Por favor ingrese la carrera.
                            </div>
                        </g:if>
                        <g:else>
                            <input class="form-control" type="text" readonly >
                        </g:else>
                    </div>
                </div>
                <label for="planesEstudiosMod">Plan de estudios<font color="red">*</font></label>
                <div class="form-group">
                    <g:if test="${params?.carreraId}">
                        <g:select  class="form-control" id="planesEstudiosMod" name="planesEstudiosMod" from="${planesEstudios}" optionKey="id" optionValue='${{"NOMBRE: ${it.nombre} :::: RVOE: ${it.rvoe ?: ""} - ${it.fechaRvoeFormato ?: ""}"}}' value="${params?.planEstudiosId}" required="true"/>
                        <input type="text" id="planEstudiosId" name="planEstudiosId" value="${params?.planEstudiosId}" hidden>
                        <div class="invalid-feedback">
                            Por favor ingrese el plan de estudios.
                        </div>
                    </g:if>
                    <g:else>
                        <input class="form-control" type="text" readonly >
                    </g:else>
                </div>
                <g:if test="${formaciones}">
                    <div class="form-group">
                        <label for="estatus">Formación <font color="red">*</font></label>
                        <g:select class="form-control" id="formacionId" name="formacionId" from="${formaciones}" optionKey="id" optionValue="nombre" value="${params.formacionId}" noSelection="${['':'Seleccione...']}" required="true"/>
                        <div class="invalid-feedback">
                            Por favor ingrese la formación.
                        </div>
                    </div>
                </g:if>
                <div id="nuevo">
                    <div class="form-row">
                        <div class="col-md-6">
                          <label for="curp">Curp <font color="red">*</font></label>
                          <input id="curp" name="curp" type="text" class="form-control" value="${params?.curp}" required onkeypress="return esCurp(event, this.id)">
                        </div>
                        <div class="col-md-2 align-self-end">
                            <input class="btn btn-primary" name="validarCurp" type="button" value="Validar"/>
                        </div>
                    </div>
                    <span for="curp"><font color="blue" id="msgOk"></font></span>
                    <span for="curp"><font color="red" id="msgError"></font></span>
                    <div class="form-group mt-3">
                        <label for="nombre">Nombre <font color="red">*</font></label>
                        <input id="nombre" name="nombre" type="text" class="form-control"  value="${params?.nombre}" required readonly>
                        <div class="invalid-feedback">
                            Por favor ingrese el nombre.
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group col-md-6">
                            <label for="primerApellido">Primer Apellido <font color="red">*</font></label>
                            <input id="primerApellido" name="primerApellido" type="text" class="form-control" value="${params?.primerApellido}" required readonly>
                            <div class="invalid-feedback">
                                Por favor ingrese el primer apellido.
                            </div>
                        </div>
                        <div class="form-group col-md-6">
                            <label for="segundoApellido">Segundo Apellido</label>
                            <input id="segundoApellido" name="segundoApellido" type="text" class="form-control"  value="${params?.segundoApellido}" readonly>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group col-md-6">
                            <label for="entidadNacimiento">Entidad de Nacimiento <font color="red">*</font></label>
                            <input id="entidadNacimiento" name="entidadNacimiento" type="text" class="form-control" value="${params?.entidadNacimiento}" required readonly>
                            <div class="invalid-feedback">
                                Por favor ingrese la entidad de Nacimiento.
                            </div>
                        </div>
                        <div class="form-group col-md-6">
                            <label for="fechaNacimiento">Fecha de Nacimiento</label>
                            <input id="fechaNacimiento" name="fechaNacimiento" type="text" class="form-control"  value="${params?.fechaNacimiento}" readonly>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group col-md-6">
                            <label for="sexo">Sexo <font color="red">*</font></label>
                            <input id="sexo" name="sexo" type="text" class="form-control"  value="${params?.sexo}" readonly>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group col-md-6">
                            <label for="matricula">Matrícula <font color="red">*</font></label>
                            <input id="matricula" name="matricula" type="text" class="form-control" value="${params?.matricula}" required>
                            <div class="invalid-feedback">
                                Por favor ingrese la matricula.
                            </div>
                        </div>
                        <div class="form-group col-md-6">
                            <label for="estatus">Estatus <font color="red">*</font></label>
                            <g:select class="form-control" id="estatusAlumnoId" name="estatusAlumnoId" from="${estatus}" optionKey="id" optionValue="nombre" value="${params?.estatusAlumnoId}" noSelection="${['':'Seleccione...']}" required="true"/>
                            <div class="invalid-feedback">
                                Por favor ingrese el estatus.
                            </div>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group col-md-6">
                            <label for="correoElectronico">Correo electrónico </label>
                            <input id="correoElectronico" name="correoElectronico" type="text" class="form-control" value="${params?.correoElectronico}">
                        </div>
                        <div class="form-group col-md-6">
                            <label for="telefono">Teléfono </label>
                            <input id="telefono" name="telefono" type="text" class="form-control"  value="${params?.telefono}" onkeypress="return esNumeroTelefonico(event, this.id)">
                        </div>
                    </div>
                </div>
                <div class="form-row justify-content-end mt-3">
                    <p>
                        <a onclick="mostrarProceso();" href="/alumno/listar" class="btn btn-secondary">Cancelar</a>
                        <button type="submit" class="btn btn-primary">Guardar</button>
                    </p>
                </div>
            </form>
        </div>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <asset:javascript src="jquery-editable-select.js"/>
        <asset:javascript src="jquery-editable-select.min.js"/>
        <asset:javascript src="filter-buttons.js"/>
        <script src="https://cdn.jsdelivr.net/gh/gitbrent/bootstrap4-toggle@3.6.1/js/bootstrap4-toggle.min.js"></script>
        <asset:javascript src="validations.js"/>
        <script>

            $('#institucionesMod').editableSelect()
                .on('select.editable-select', function (e, li) {
                    mostrarProceso();
                    $('#institucionIdAux').val(li.val());
                    $('#institucionId').val(li.val());
                    $('#curpAux').val($('#curp').val());
                    $('#matriculaAux').val($('#matricula').val());
                    $('#estatusAux').val($('#estatus').val());
                    $('#correoElectronicoAux').val($('#correoElectronico').val());
                    $('#telefonoAux').val($('#telefono').val());
                    $('#form-select').submit()
                });

            $('#carrerasMod').editableSelect()
                .on('select.editable-select', function (e, li) {
                    mostrarProceso();
                    $('#carreraIdAux').val(li.val());
                    $('#carreraId').val(li.val());
                    $('#curpAux').val($('#curp').val());
                    $('#matriculaAux').val($('#matricula').val());
                    $('#estatusAux').val($('#estatus').val());
                    $('#correoElectronicoAux').val($('#correoElectronico').val());
                    $('#telefonoAux').val($('#telefono').val());
                    $('#form-select').submit()
                });

            $('#planesEstudiosMod').editableSelect()
                .on('select.editable-select', function (e, li) {
                    mostrarProceso();
                    $('#planEstudiosIdAux').val(li.val());
                    $('#planEstudiosId').val(li.val());
                    $('#curpAux').val($('#curp').val());
                    $('#matriculaAux').val($('#matricula').val());
                    $('#estatusAux').val($('#estatus').val());
                    $('#correoElectronicoAux').val($('#correoElectronico').val());
                    $('#telefonoAux').val($('#telefono').val());
                    $('#form-select').submit()
                });

            $(document).ready(function(){
                if($("#curp").val() != ""){
                    var curp = $("#curp").val();
                    var carreraId = $("#carreraId").val();
                    obtenerDatosCurp(curp.trim().toUpperCase(), carreraId);
                }

                $("#curp").keyup(function() {
                    this.value = this.value.toLocaleUpperCase();
                });

                $("input[name='validarCurp']").on("click", function(){
                    var curp = $("#curp").val();
                    var carreraId = $("#carreraId").val();
                    obtenerDatosCurp(curp.trim().toUpperCase());
                });
            });

            function obtenerDatosCurp(curp){
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
                    $("#msgError").html("No se pudo validar la CURP");
                    $("#msgOk").html("");;
                }).always(function () {

                });
            }

        </script>
    </body>
</html>