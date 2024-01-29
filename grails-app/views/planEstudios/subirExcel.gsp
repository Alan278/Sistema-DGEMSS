<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Nuevas Evaluaciones</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/planEstudios/registro" class="btn btn-primary col-md-12 my-2">
                Nuevo plan de estudios
            </a>
            <a href="/planEstudios/subirExcel" class="btn btn-primary col-md-12 my-2">
                Cargar planes
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Carga de planes de estudios
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

        <div class="container mb-5">

            <div class="d-flex justify-content-end mb-2">
                <a onclick="mostrarProceso(1000);" href="/planEstudios/descargarPlantilla" class="btn btn-outline-success">
                    <i class="bi bi-cloud-arrow-down-fill mr-2"></i>Descargar plantilla
                </a>
            </div>

            <form action="/planEstudios/cargarPorExcel" enctype="multipart/form-data" method="POST" class="needs-validation" novalidate>

                <label for="instituciones">Institución</label>
                <div class="input-group mb-3">
                    <g:select  class="form-control" id="instituciones" name="instituciones" from="${instituciones}" optionKey="id" optionValue="nombre" value="${params?.institucionId}" required="true"/>
                    <input id="institucionId" name="institucionId" value="${params.institucionId}" hidden>
                </div>

                <div class="form-group mt-3">
                    <label>Excel <font color="red">*</font></label>
                    <div class="custom-file">
                      <label class="custom-file-label" for="customFile">Elija un archivo</label>
                      <input type="file" class="custom-file-input" id="customFile" name="excel" required>
                      <div class="invalid-feedback">
                          Por favor elija el archivo a subir.
                      </div>
                    </div>
                </div>

                <div class="form-row justify-content-end">
                    <p>
                        <a onclick="mostrarProceso();" href="planEstudios/listar" class="btn btn-secondary">Cancelar</a>
                        <button type="submit" class="btn btn-primary">Guardar</button>
                    </p>
                </div>
            </form>
        </div>

        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <asset:javascript src="jquery-editable-select.js"/>
        <asset:javascript src="jquery-editable-select.min.js"/>
        <script>
            $('#instituciones').editableSelect()
                .on('select.editable-select', function (e, li) {
                    $('#institucionId').val(li.val());
                }
            );
        </script>
    </body>
</html>
