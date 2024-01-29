<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Modificación de Asignatura Foránea / Externa</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/asignaturaExterna/registro" class="btn btn-primary col-md-12 my-2">
                Nueva Asignatura Externa
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Modificación de Asignatura Foránea / Externa
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
            <form action="/asignaturaExterna/modificacion/${asignatura?.id}" id="form-select" method="post">
                <input type="text" id="institucionIdAux" name="institucionId" value="${asignatura?.planEstudios?.carrera?.institucion?.id}" hidden>
                <input type="text" id="carreraIdAux" name="carreraId" value="${asignatura?.planEstudios?.carrera?.id}" hidden>
                <input type="text" id="nombreAux" name="nombre" value="${asignatura?.nombre}" hidden>
                <input type="text" id="claveAux" name="clave" value="${asignatura?.clave}" hidden>
                <input type="text" id="periodoAux" name="periodo" value="${asignatura?.periodo}" hidden>
            </form>
        </div>

        <div class="container mb-5">
            <form action="/asignaturaExterna/modificar/${asignatura?.id}" class="needs-validation" novalidate>
                <div class="form-group">
                    <label for="institucionesMod">Institucion<font color="red">*</font></label>
                    <g:select  class="form-control" id="institucionesMod" name="institucionesMod" from="${instituciones}" optionKey="id" optionValue="nombre" value="${asignatura?.planEstudios?.carrera?.institucion?.id}" required="true"/>
                    <input type="text" id="institucionId" name="institucionId" value="${asignatura?.planEstudios?.carrera?.institucion?.id}" hidden>
                    <div class="invalid-feedback">
                        Por favor ingrese la institución.
                    </div>
                </div>

                <label for="carrerasMod">Carrera<font color="red">*</font></label>
                <div class="form-group" id="div-carreras">
                    <g:if test="${asignatura?.planEstudios?.carrera?.institucion?.id}">
                        <g:select  class="form-control" id="carrerasMod" name="carrerasMod" from="${carreras}" optionKey="id" optionValue="nombre" value="${asignatura?.planEstudios?.carrera?.id}" required="true"/>
                        <input type="text" id="carreraId" name="carreraId" value="${asignatura?.planEstudios?.carrera?.id}" hidden>
                        <div class="invalid-feedback">
                            Por favor ingrese la carrera.
                        </div>
                    </g:if>
                    <g:else>
                        <input class="form-control" type="text" readonly >
                    </g:else>
                </div>
                <label for="planesEstudiosMod">Plan de Estudios<font color="red">*</font></label>
                <div class="form-group">
                    <g:if test="${asignatura?.planEstudios?.carrera?.id}">
                        <g:select  class="form-control" id="planesEstudiosMod" name="planesEstudiosMod" from="${planesEstudios}" optionKey="id" optionValue="nombre" value="${asignatura?.planEstudios?.id}" required="true"/>
                        <input type="text" id="planEstudiosId" name="planEstudiosId" value="${asignatura?.planEstudios?.id}" hidden>
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
                    <input id="nombre" name="nombre" type="text" class="form-control"  value="${asignatura?.nombre}" required>
                    <div class="invalid-feedback">
                        Por favor ingrese el nombre de la asignatura.
                    </div>
                </div>
                <div class="form-group">
                    <label for="clave">Clave </label>
                    <input id="clave" name="clave" type="text" class="form-control"  value="${asignatura?.clave}">
                </div>
                <div class="form-group">
                    <label for="horas">Horas a la semana</label>
                    <input id="horas" name="horas" type="text" class="form-control"  value="${asignatura?.horas}" onkeypress="return esEnteroPositivo(event, this.id);">
                </div>
                <div class="form-group">
                    <label for="periodo">Periodo<font color="red">*</font></label>
                    <input id="periodo" name="periodo" type="text" class="form-control"  value="${asignatura?.periodo}" onkeypress="return esEnteroPositivo(event, this.id);" required>
                    <div class="invalid-feedback">
                        Por favor ingrese el periodo.
                    </div>
                </div>
                <div class="form-row justify-content-end">
                    <p>
                        <a onclick="mostrarProceso();" href="asignaturaExterna/listar" class="btn btn-secondary">Cancelar</a>
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
                        mostrarProceso();
                        $('#nombreAux').val($('#nombre').val());
                        $('#claveAux').val($('#clave').val());
                        $('#periodoAux').val($('#periodo').val());
                        $('#creditosAux').val($('#creditos').val());
                        $('#carreraIdAux').val('');
                        $('#institucionIdAux').val(li.val());
                        $('#institucionId').val(li.val());
                        $('#form-select').submit()
                    });
            $('#carrerasMod').editableSelect()
                .editableSelect()
                    .on('select.editable-select', function (e, li) {
                        mostrarProceso();
                        $('#nombreAux').val($('#nombre').val());
                        $('#claveAux').val($('#clave').val());
                        $('#periodoAux').val($('#periodo').val());
                        $('#creditosAux').val($('#creditos').val());
                        $('#institucionIdAux').val($('#institucionId').val());
                        $('#carreraIdAux').val(li.val());
                        $('#carreraId').val(li.val());
                        $('#form-select').submit()
                    });
            $('#planesEstudiosMod').editableSelect()
                .editableSelect()
                    .on('select.editable-select', function (e, li) {
                        $('#planEstudiosId').val(li.val());
                    });
        </script>
    </body>
</html>

