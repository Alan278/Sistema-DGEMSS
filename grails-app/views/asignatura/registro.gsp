<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Nueva Asignatura</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/asignatura/registro" class="btn btn-primary col-md-12 my-2">
                Nueva asignatura
            </a>
            <a href="/asignatura/subirExcel" class="btn btn-primary col-md-12 my-2">
                Cargar asignaturas
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Nueva Asignatura
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
            <form action="/asignatura/registro" id="form-select" method="post">
                <input type="text" id="institucionIdAux" name="institucionId" value="${asignatura?.institucionId}" hidden>
                <input type="text" id="carreraIdAux" name="carreraId" value="${asignatura?.carreraId}" hidden>
                <input type="text" id="planEstudiosIdAux" name="planEstudiosId" value="${asignatura?.planEstudiosId}" hidden>
                <input type="text" id="nombreAux" name="nombre" value="${asignatura?.nombre}" hidden>
                <input type="text" id="claveAux" name="clave" value="${asignatura?.clave}" hidden>
                <input type="text" id="periodoAux" name="periodo" value="${asignatura?.periodo}" hidden>
                <input type="text" id="creditosAux" name="creditos" value="${asignatura?.creditos}" hidden>
            </form>
        </div>

        <div class="container mb-5">
            <form action="/asignatura/registrar" class="needs-validation" novalidate>
                <div class="form-group">
                    <label for="institucionesMod">Institución<font color="red">*</font></label>
                    <g:select  class="form-control" id="institucionesMod" name="institucionesMod" from="${instituciones}" optionKey="id" optionValue='${{"CCT: ${it.claveCt ?: ""} :::: NOMBRE: ${it.nombre}"}}' value="${asignatura?.institucionId}" required="true"/>
                    <input type="text" id="institucionId" name="institucionId" value="${asignatura?.institucionId}" hidden>
                    <div class="invalid-feedback">
                        Por favor ingrese la institución.
                    </div>
                </div>

                <label for="carrerasMod">Carrera<font color="red">*</font></label>
                <div class="form-group" id="div-carreras">
                    <g:if test="${asignatura?.institucionId}">
                        <g:select  class="form-control" id="carrerasMod" name="carrerasMod" from="${carreras}" optionKey="id" optionValue="nombre" value="${asignatura?.carreraId}" required="true"/>
                        <input type="text" id="carreraId" name="carreraId" value="${asignatura?.carreraId}" hidden>
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
                    <g:if test="${asignatura?.carreraId}">
                        <g:select  class="form-control" id="planesEstudiosMod" name="planesEstudiosMod" from="${planesEstudios}" optionKey="id" optionValue="nombre" value="${asignatura?.planEstudiosId}" required="true"/>
                        <input type="text" id="planEstudiosId" name="planEstudiosId" value="${asignatura?.planEstudiosId}" hidden>
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
                    <label for="clave">Clave</label>
                    <input id="clave" name="clave" type="text" class="form-control"  value="${asignatura?.clave}">
                </div>
                <div class="form-group">
                    <label for="creditos">Número de créditos</label>
                    <input id="creditos" name="creditos" type="text" class="form-control"  value="${asignatura?.creditos}" onkeypress="return esEnteroPositivo(event, this.id);">
                </div>
                <div class="form-group">
                    <label for="horas">Horas a la semana</label>
                    <input id="horas" name="horas" type="text" class="form-control"  value="${asignatura?.horas}" onkeypress="return esEnteroPositivo(event, this.id);">
                </div>
                <div class="form-group">
                    <label for="periodo">Periodo<font color="red">*</font></label>
                    <input id="periodo" name="periodo" type="text" class="form-control"  value="${asignatura?.periodo}" required>
                    <div class="invalid-feedback">
                        Por favor ingrese el periodo.
                    </div>
                </div>
                <div class="form-group">
                    <label for="orden">Orden <font color="red">*</font></label>
                    <input id="orden" name="orden" type="text" class="form-control"  value="${asignatura?.orden}" onkeypress="return esEnteroPositivo(event, this.id);" required>
                    <div class="invalid-feedback">
                        Por favor ingrese el orden.
                    </div>
                </div>
                <div class="form-group">
                    <label for="estatus">Formación <font color="red">*</font></label>
                    <g:select class="form-control" id="formacionId" name="formacionId" from="${formaciones}" optionKey="id" optionValue="nombre" value="${params.formacionId}" noSelection="${['':'Seleccione...']}" required="true"/>
                    <div class="invalid-feedback">
                        Por favor ingrese la formación.
                    </div>
                </div>
                <div class="form-row justify-content-end">
                    <p>
                        <a onclick="mostrarProceso();" href="asignatura/listar" class="btn btn-secondary">Cancelar</a>
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

                        mostrarProceso();
                        $('#nombreAux').val($('#nombre').val());
                        $('#claveAux').val($('#clave').val());
                        $('#periodoAux').val($('#periodo').val());
                        $('#creditosAux').val($('#creditos').val());
                        $('#institucionIdAux').val($('#institucionId').val());
                        $('#carreraIdAux').val($('#carreraId').val());
                        $('#planEstudiosId').val(li.val());
                        $('#planEstudiosIdAux').val(li.val());
                        $('#form-select').submit()
                        
                    });
        </script>
    </body>
</html>

