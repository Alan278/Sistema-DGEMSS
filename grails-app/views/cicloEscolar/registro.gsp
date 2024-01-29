<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Nuevos Ciclos Escolares</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>

    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/cicloEscolar/registro" class="btn btn-primary col-md-12 my-2">
                Nuevo ciclo escolar
            </a>
            <a href="/cicloEscolar/subirExcel" class="btn btn-primary col-md-12 my-2">
                Cargar ciclos escolares
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Nuevo ciclo escolare
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
            <form action="/cicloEscolar/registro" id="form-select" method="post">
                <input type="text" id="institucionIdAux" name="institucionId" value="${cicloEscolar?.institucionId}" hidden>
                <input type="text" id="carreraIdAux" name="carreraId" value="${params?.institucionId}" hidden>
                <input id="nombreAux" name="nombre" type="text" class="form-control"  value="${cicloEscolar?.nombre}" hidden>
                <input id="periodoAux" name="periodo" type="text" class="form-control"  value="${cicloEscolar?.periodo}" hidden>
                <input id="fechaInicioAux" name="fechaInicio" type="text" class="form-control"  value="${cicloEscolar?.fechaInicio}" hidden>
                <input id="fechaFinAux" name="fechaFin" type="text" class="form-control"  value="${cicloEscolar?.fechaFin}" hidden>
            </form>
        </div>

        <div class="container mb-5">
            <form action="/cicloEscolar/registrar" class="needs-validation" novalidate>
                <div class="form-group">
                    <label for="institucionesMod">Institución<font color="red">*</font></label>
                    <g:select  class="form-control" id="institucionesMod" name="institucionesMod" from="${instituciones}" optionKey="id" optionValue="nombre" value="${cicloEscolar?.institucionId}" required="true"/>
                    <input type="text" id="institucionId" name="institucionId" value="${cicloEscolar?.institucionId}" hidden>
                    <div class="invalid-feedback">
                        Por favor ingrese la institución.
                    </div>
                </div>
                <label for="carrerasMod">Carrera <font color="red">*</font></label>
                <div class="form-group" id="div-carreras">
                    <g:if test="${cicloEscolar?.institucionId}">
                        <g:select  class="form-control" id="carrerasMod" name="carrerasMod" from="${carreras}" optionKey="id" optionValue="nombre" value="${cicloEscolar?.carreraId}" required="true"/>
                        <input type="text" id="carreraId" name="carreraId" value="${cicloEscolar?.carreraId}" hidden>
                        <div class="invalid-feedback">
                            Por favor ingrese la carrera.
                        </div>
                    </g:if>
                    <g:else>
                        <input class="form-control" type="text" readonly >
                    </g:else>
                </div>
                <label for="planesEstudiosMod">Plan de estudios<font color="red">*</font></label>
                <div class="form-group">
                    <g:if test="${cicloEscolar?.carreraId}">
                        <g:select  class="form-control" id="planesEstudiosMod" name="planesEstudiosMod" from="${planesEstudios}" optionKey="id" optionValue='${{"NOMBRE: ${it.nombre} :::: RVOE: ${it.rvoe ?: ""} - ${it.fechaRvoeFormato ?: ""}"}}' value="${cicloEscolar?.planEstudiosId}" required="true"/>
                        <input type="text" id="planEstudiosId" name="planEstudiosId" value="${cicloEscolar?.planEstudiosId}" hidden>
                        <div class="invalid-feedback">
                            Por favor ingrese el plan de estudios.
                        </div>
                    </g:if>
                    <g:else>
                        <input class="form-control" type="text" readonly >
                    </g:else>
                </div>
                <div class="form-group">
                    <label for="nombre">Nombre <font color="red">*</font></label>
                    <input id="nombre" name="nombre" type="text" class="form-control"  value="${cicloEscolar?.nombre}" required>
                    <div class="invalid-feedback">
                        Por favor ingrese el nombre.
                    </div>
                </div>
                <div class="form-group">
                    <label for="periodo">Periodo <font color="red">*</font></label>
                    <input id="periodo" name="periodo" type="text" class="form-control"  value="${cicloEscolar?.periodo}" onkeypress="return esEnteroPositivo(event, this.id)" required>
                    <div class="invalid-feedback">
                        Por favor ingrese el periodo.
                    </div>
                </div>
                <div class="form-group align-items-start">
                    <label for="claveSeem">Fechas <font color="red">*</font></label>
                    <div class="input-group col-md-5 px-0 mb-2">
                        <div class="input-group-prepend col-md-7 px-0">
                            <div class="input-group-text col-md-12">Fecha de Inicio</div>
                        </div>
                        <input id="fechaInicio" name="fechaInicio" type="date" class="form-control"  value="${cicloEscolar?.fechaInicio}" required>
                        <div class="invalid-feedback">
                            Por favor ingrese la fecha de Inicio.
                        </div>
                    </div>
                    <div class="input-group col-md-5 px-0 mb-2">
                        <div class="input-group-prepend col-md-7 px-0">
                            <div class="input-group-text col-md-12">Fecha de Fin</div>
                        </div>
                        <input id="fechaFin" name="fechaFin" type="date" class="form-control"  value="${cicloEscolar?.fechaFin}" required>
                        <div class="invalid-feedback">
                            Por favor ingrese la fecha de Fin mayor a la de inicio.
                        </div>
                    </div>
                </div>

                <div class="form-row justify-content-end">
                    <p>
                        <a onclick="mostrarProceso();" href="/cicloEscolar/listar" class="btn btn-secondary">Cancelar</a>
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
            $("#fechaInicio").change(function(){
                $("#fechaFin").prop("min", $("#fechaInicio").val());
            });

            $('#institucionesMod').editableSelect()
                .editableSelect()
                    .on('select.editable-select', function (e, li) {
                        mostrarProceso();
                        $('#nombreAux').val($('#nombre').val());
                        $('#periodoAux').val($('#periodo').val());
                        $('#fechaInicioAux').val($('#fechaInicio').val());
                        $('#fechaFinAux').val($('#fechaFin').val());
                        $('#institucionIdAux').val(li.val());
                        $('#institucionId').val(li.val());
                        $('#form-select').submit()
                    });

            $('#carrerasMod').editableSelect()
                .editableSelect()
                    .on('select.editable-select', function (e, li) {
                        mostrarProceso();
                        $('#carreraId').val(li.val());
                        $('#carreraIdAux').val(li.val());
                        $('#nombreAux').val($('#nombre').val());
                        $('#periodoAux').val($('#periodo').val());
                        $('#fechaInicioAux').val($('#fechaInicio').val());
                        $('#fechaFinAux').val($('#fechaFin').val());
                        $('#institucionIdAux').val($('#institucionId').val());
                        $('#form-select').submit()
                    });

            $('#planesEstudiosMod').editableSelect()
                .on('select.editable-select', function (e, li) {
                    $('#planEstudiosId').val(li.val());
                });

            $('#periodo').on("input", () => {
                let value  = parseInt($("#periodo").val())
                if(!value){
                    value = ""
                }
                $("#periodo").val(value)
            })
        </script>
    </body>
</html>
