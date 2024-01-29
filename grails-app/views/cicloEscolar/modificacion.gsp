<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Modificar Ciclos Escolar</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>

    <body>

        <%@ page import="java.text.SimpleDateFormat" %>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="#" class="btn btn-primary col-md-12 my-2 disabled">
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Modificar Ciclo Escolar
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
            <form action="/cicloEscolar/modificacion/${cicloEscolar?.id}" id="form-select" method="post">
                <input type="text" id="institucionIdAux" name="institucionId" value="${params.institucionId ?: cicloEscolar?.planEstudios?.carrera?.institucion?.id}" hidden>
                <input type="text" id="carreraIdAux" name="carreraId" value="${params.carreraId ?:  cicloEscolar?.planEstudios?.carrera?.id}" hidden>
                <input id="nombreAux" name="nombre" type="text" class="form-control"  value="${params.nombre ?: cicloEscolar?.nombre}" hidden>
                <input id="periodoAux" name="periodo" type="text" class="form-control"  value="${params.periodo ?: cicloEscolar?.periodo}" hidden>
                <input id="fechaInicioAux" name="fechaInicio" type="text" class="form-control"  value="${params.fechaInicio ?: cicloEscolar?.fechaInicio}" hidden>
                <input id="fechaFinAux" name="fechaFin" type="text" class="form-control"  value="${params.fechaFin ?: cicloEscolar?.fechaFin}" hidden>
            </form>
        </div>

        <div class="container mb-5">
            <form action="/cicloEscolar/modificar/${cicloEscolar?.id}" class="needs-validation" novalidate>
                <div class="form-group">
                    <label for="institucionesMod">Institución <font color="red">*</font></label>
                    <g:select  class="form-control" id="institucionesMod" name="institucionesMod" from="${instituciones}" optionKey="id" optionValue="nombre" value="${params.institucionId ?: cicloEscolar?.planEstudios?.carrera?.institucion?.id}" required="true"/>
                    <input type="text" id="institucionId" name="institucionId" value="${params.institucionId ?: cicloEscolar?.planEstudios?.carrera?.institucion?.id}" hidden>
                    <div class="invalid-feedback">
                        Por favor ingrese la institución.
                    </div>
                </div>
                <div class="form-group" id="div-carreras">
                    <label for="carrerasMod">Carrera <font color="red">*</font></label>
                        <g:select  class="form-control" id="carrerasMod" name="carrerasMod" from="${carreras}" optionKey="id" optionValue="nombre" value="${params.carreraId ?: cicloEscolar?.planEstudios?.carrera?.id}" required="true"/>
                        <input type="text" id="carreraId" name="carreraId" value="${params.carreraId ?: cicloEscolar?.planEstudios?.carrera?.id}" hidden>
                        <div class="invalid-feedback">
                            Por favor ingrese la carrera.
                        </div>
                </div>
                <label for="planesEstudiosMod">Plan de estudios<font color="red">*</font></label>
                <div class="form-group">
                    <g:if test="${params.institucionId && !params.carreraId ? false : true}">
                        <g:select  class="form-control" id="planesEstudiosMod" name="planesEstudiosMod" from="${planesEstudios}" optionKey="id" optionValue="nombre" value="${params.planEstudiosId ?: cicloEscolar?.planEstudios?.id}" required="true"/>
                        <input type="text" id="planEstudiosId" name="planEstudiosId" value="${params.planEstudiosId ?: cicloEscolar?.planEstudios?.id}" hidden>
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
                    <input id="nombre" name="nombre" type="text" class="form-control"  value="${params.nombre ?: cicloEscolar?.nombre}" required>
                    <div class="invalid-feedback">
                        Por favor ingrese el nombre.
                    </div>
                </div>
                <div class="form-group">
                    <label for="periodo">Periodo <font color="red">*</font></label>
                    <g:if test="${cicloEscolar.numAlumnos}">
                        <input type="text" class="form-control" value="${cicloEscolar?.periodo}" disabled>
                        <input id="periodo" name="periodo" type="text" class="form-control" value="${cicloEscolar?.periodo}" hidden>
                    </g:if>
                    <g:else>
                        <input id="periodo" name="periodo" type="text" class="form-control" value="${cicloEscolar?.periodo}">
                    </g:else>
                </div>
                <div class="form-group align-items-start">
                    <label for="claveSeem">Fechas <font color="red">*</font></label>
                    <div class="input-group col-md-5 px-0 mb-2">
                        <g:set var="fechaInicio" value="${(new SimpleDateFormat('yyyy-MM-dd')).format(cicloEscolar?.fechaInicio)}"/>
                        <div class="input-group-prepend col-md-7 px-0">
                            <div class="input-group-text col-md-12">Fecha de Inicio</div>
                        </div>
                        <input id="fechaInicio" name="fechaInicio" type="date" class="form-control"  value="${fechaInicio}" required>
                        <div class="invalid-feedback">
                            Por favor ingrese la fecha de Inicio.
                        </div>
                    </div>
                    <div class="input-group col-md-5 px-0 mb-2">
                        <g:set var="fechaFin" value="${(new SimpleDateFormat('yyyy-MM-dd')).format(cicloEscolar?.fechaFin)}"/>
                        <div class="input-group-prepend col-md-7 px-0">
                            <div class="input-group-text col-md-12">Fecha de Fin</div>
                        </div>
                        <input id="fechaFin" name="fechaFin" type="date" class="form-control"  value="${fechaFin}" min="${fechaInicio}" required>
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
                        $('#carreraIdAux').val("");
                        $('#form-select').submit()
                    });

            $('#carrerasMod').editableSelect()
                .editableSelect()
                    .on('select.editable-select', function (e, li) {
                        mostrarProceso();
                        $('#carreraIdAux').val(li.val());
                        $('#planEstudiosIdAux').val("");
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
        </script>
    </body>
</html>
