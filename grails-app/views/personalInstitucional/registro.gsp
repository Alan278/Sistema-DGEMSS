<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Nuevos Pesonales</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/personalInstitucional/registro" class="btn btn-primary col-md-12 my-2">
                Nuevo Personal
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Nuevo Personal
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
        <div class="container mb-5">
            <form action="/personalInstitucional/registrar" class="needs-validation" novalidate>
                <h5> <b> Datos de la Institución </b> </h5>
                <div class="form-group">
                    <label for="institucionesMod">Institución<font color="red">*</font></label>
                    <g:select  class="form-control" id="institucionesMod" name="institucionesMod" from="${instituciones}" optionKey="id" optionValue="nombre" value="${personalInstitucional?.institucionId}" required="true" />
                    <input type="text" id="institucionId" name="institucionId" value="${personalInstitucional?.institucionId}" hidden>
                    <div class="invalid-feedback">
                        Por favor ingrese la institución.
                    </div>
                </div>
                <div class="from-group">
                    <label for="cargoId">Cargo <font color="red">*</font></label>
                    <g:select  class="form-control" id="cargoId" name="cargoId" from="${cargos}" optionKey="id" optionValue="nombre"value="${personalInstitucional?.cargoId}" noSelection="${['':'Seleccione...']}" required="true"/>
                    <div class="invalid-feedback">
                        Por favor ingrese el cargo.
                    </div>
                </div>
                <br/>
                <div class="form-group">
                    <label for="nombreCargo">Nombre del cargo <font color="red">*</font></label>
                    <input id="nombreCargo" name="nombreCargo" type="text" class="form-control"  value="${personalInstitucional?.nombreCargo}" required>
                    <div class="invalid-feedback">
                        Por favor ingrese el nombre del cargo.
                    </div>
                </div>
                <h5> <b> Datos personales </b> </h5>
                <div class="form-row mb-3">
                    <div class="col-md-10">
                      <label for="curp">Curp <font color="red">*</font></label>
                      <input id="curp" name="curp" type="text" class="form-control" value="${personalInstitucional?.curp}" required onkeypress="return esCurp(event, this.id)">
                    </div>
                    <div class="col-md-2 align-self-end">
                        <input class="btn btn-primary" name="validarCurp" type="button" value="Validar"/>
                    </div>
                </div>
                <div class="form-group">
                    <label for="nombre">Nombre <font color="red">*</font></label>
                    <input id="nombre" name="nombre" type="text" class="form-control"  value="${personalInstitucional?.nombre}" required readonly>
                    <div class="invalid-feedback">
                        Por favor ingrese el nombre.
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label for="primerApellido">Primer Apellido <font color="red">*</font></label>
                        <input id="primerApellido" name="primerApellido" type="text" class="form-control" value="${personalInstitucional?.primerApellido}" required readonly>
                        <div class="invalid-feedback">
                            Por favor ingrese el primer apellido.
                        </div>
                    </div>
                    <div class="form-group col-md-6">
                        <label for="segundoApellido">Segundo Apellido</label>
                        <input id="segundoApellido" name="segundoApellido" type="text" class="form-control"  value="${personalInstitucional?.segundoApellido}" readonly>
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label for="entidadNacimiento">Entidad de Nacimiento <font color="red">*</font></label>
                        <input id="entidadNacimiento" name="entidadNacimiento" type="text" class="form-control" value="${personalInstitucional?.entidadNacimiento}" required readonly>
                        <div class="invalid-feedback">
                            Por favor ingrese la entidad de nacimiento.
                        </div>
                    </div>
                    <div class="form-group col-md-6">
                        <label for="fechaNacimiento">Fecha de Nacimiento <font color="red">*</font></label>
                        <input id="fechaNacimiento" name="fechaNacimiento" type="text" class="form-control"  value="${personalInstitucional?.fechaNacimiento}" readonly>
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label for="sexo">Sexo <font color="red">*</font></label>
                        <input id="sexo" name="sexo" type="text" class="form-control"  value="${personalInstitucional?.sexo}" readonly>
                    </div>
                    <div class="form-group col-md-6">
                        <label for="rfc">RFC <font color="red">*</font></label>
                        <input id="rfc" name="rfc" type="text" class="form-control"  value="${personalInstitucional?.rfc}" onkeypress="return esRfc(event, this.id)">
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label for="correoElectronico">Correo electrónico <font color="red">*</font></label>
                        <input id="correoElectronico" name="correoElectronico" type="text" class="form-control" value="${personalInstitucional?.correoElectronico}" required>
                        <div class="invalid-feedback">
                            Por favor ingrese el correo electrónico.
                        </div>
                    </div>
                    <div class="form-group col-md-6">
                        <label for="telefono">Teléfono <font color="red">*</font></label>
                        <input id="telefono" name="telefono" type="text" class="form-control"  value="${personalInstitucional?.telefono}" required onkeypress="return esNumeroTelefonico(event, this.id)">
                        <div class="invalid-feedback">
                            Por favor ingrese el teléfono.
                        </div>
                    </div>
                </div>
                <div class="form-row justify-content-end">
                    <p>
                        <a onclick="mostrarProceso();" href="personalInstitucional/listar" class="btn btn-secondary">Cancelar</a>
                        <button type="submit" class="btn btn-primary">Guardar</button>
                    </p>
                </div>
            </form>
        </div>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <asset:javascript src="jquery-editable-select.js"/>
        <asset:javascript src="jquery-editable-select.min.js"/>
        <asset:javascript src="filter-buttons.js"/>
        <asset:javascript src="validations.js"/>
        <script>
            $('#institucionesMod').editableSelect()
                .editableSelect()
                    .on('select.editable-select', function (e, li) {
                        $('#institucionId').val(li.val());
                    });

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
                   $("#mensaje").html("Validando CURP...");
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
                           $("#mensaje").html("CURP valida...");
                           $("#curp").val(respuesta.curp);
                           $("input[name='nombre']").val(respuesta.nombre);
                           $("input[name='primerApellido']").val(respuesta.apellidoPaterno);
                           $("input[name='segundoApellido']").val(respuesta.apellidoMaterno);
                           $("input[name='entidadNacimiento']").val(respuesta.claveEntidadNacimiento);
                           $("input[name='fechaNacimiento']").val(respuesta.fechaNacimiento);
                           $("input[name='sexo']").val(respuesta.sexo);
                       } else {
                           $("#mensaje").html(respuesta.mensaje);
                       }
                   }).fail(function(error){
                       $("#mensaje").html("No se pudo validar la CURP");
                   }).always(function () {

                   });
             }
        </script>
    </body>
</html>