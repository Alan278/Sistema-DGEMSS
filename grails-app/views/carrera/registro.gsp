<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Nuevas Instituciones</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/carrera/registro" class="btn btn-primary col-md-12 my-2">
                Nueva carrera
            </a>
            <a href="/carrera/subirExcel" class="btn btn-primary col-md-12 my-2">
                Cargar carreras
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Nueva Carrera
                </h3>
            </div>
        </div>

        <div class="container">
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
            <form action="/carrera/registrar" class="needs-validation" novalidate>
                <div class="form-group">
                    <label for="institucionesMod">Institución <font color="red">*</font></label>
                    <g:select  class="form-control" id="institucionesMod" name="institucionesMod" from="${instituciones}" optionKey="id" optionValue='${{"CCT: ${it.claveCt ?: ""} :::: NOMBRE: ${it.nombre}"}}' value="${carrera?.institucionId}" required="true"/>
                    <input type="text" id="institucionId" name="institucionId" value="${carrera?.institucionId}" hidden>
                    <div class="invalid-feedback">
                        Por favor ingrese la institución.
                    </div>
                </div>
                <div class="form-group">
                    <label for="nombre">Nombre de la carrera <font color="red">*</font></label>
                    <input id="nombre" name="nombre" type="text" class="form-control"  value="${carrera?.nombre}" required>
                    <div class="invalid-feedback">
                        Por favor ingrese el nombre de la carrera.
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label for="claveSeem">Clave Seem</label>
                        <input id="claveSeem" name="claveSeem" type="text" class="form-control" value="${carrera?.claveSeem}">
                    </div>
                    <div class="form-group col-md-6">
                        <label for="claveDgp">Clave Dgp</label>
                        <input id="claveDgp" name="claveDgp" type="text" class="form-control"  value="${carrera?.claveDgp}">
                    </div>
                </div>
                <div class="form-group">
                    <label for="nivelId">Nivel <font color="red">*</font></label>
                    <g:select  class="form-control" id="nivelId" name="nivelId" from="${niveles}" optionKey="id" optionValue="nombre"value="${carrera?.nivelId}" noSelection="${['':'Seleccione...']}" required="true"/>
                    <div class="invalid-feedback">
                        Por favor ingrese el nivel.
                    </div>
                </div>
                <div class="form-group">
                    <label for="modalidadId">Modalidad <font color="red">*</font></label>
                    <g:select  class="form-control" id="modalidadId" name="modalidadId" from="${modalidades}" optionKey="id" value="${carrera?.modalidadId}" optionValue="nombre" noSelection="${['':'Seleccione...']}" required="true"/>
                    <div class="invalid-feedback">
                        Por favor ingrese la modalidad.
                    </div>
                </div>
                <div class="form-group">
                    <label for="areas">Área</label>
                    <g:select  class="form-control" id="areas" name="areas" from="${areas}" optionKey="id" optionValue="nombre" value="${carrera?.areaId}"/>
                    <input type="text" id="areaId" name="areaId" value="${carrera?.areaId}" hidden>
                </div>
                <div class="form-row justify-content-end">
                    <p>
                        <a onclick="mostrarProceso();" href="carrera/listar" class="btn btn-secondary">Cancelar</a>
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
            $('#institucionesMod').editableSelect()
                .editableSelect()
                    .on('select.editable-select', function (e, li) {
                        $('#institucionId').val(li.val());
                    });
            $('#areas').editableSelect()
                .editableSelect()
                    .on('select.editable-select', function (e, li) {
                        $('#areaId').val(li.val());
                    });
        </script>
    </body>
</html>

