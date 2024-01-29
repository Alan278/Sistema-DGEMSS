<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Modificación de Diplomado</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/diplomado/registro" class="btn btn-primary col-md-12 my-2">
                Nuevo Diplomado
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Modificación de Diplomado
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
            <form action="/diplomado/modificar/${diplomado?.id}" class="needs-validation" method="post" novalidate>

                <div class="form-group">
                    <label for="institucionesMod">Institución<font color="red">*</font></label>
                    <g:select  class="form-control" id="institucionesMod" name="institucionesMod" from="${instituciones}" optionKey="id" optionValue="nombre" value="${params.institucionId?(params.institucionId):(diplomado?.institucion?.id)}" required="true"/>
                    <input type="text" id="institucionId" name="institucionId" value="${params.institucionId?(params.institucionId):(diplomado?.institucion?.id)}" hidden>
                    <div class="invalid-feedback">
                        Por favor ingrese la institución.
                    </div>
                </div>


                <div class="form-group">
                    <label for="nombre">Nombre <font color="red">*</font></label>
                    <input id="nombre" name="nombre" type="text" class="form-control"  value="${params.nombre?(params.nombre):(diplomado?.nombre)}" required>
                    <div class="invalid-feedback">
                        Por favor ingrese el nombre del diplomado.
                    </div>
                </div>

                <div class="form-row justify-content-end">
                    <p>
                        <a onclick="mostrarProceso();" href="diplomado/listar" class="btn btn-secondary">Cancelar</a>
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
                .on('select.editable-select', function (e, li) {
                        $('#institucionId').val(li.val());
                    });
        </script>
    </body>
</html>

