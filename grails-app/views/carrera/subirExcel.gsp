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
                    Carga de Carreras
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
            <form action="/carrera/subirExcel" class="mt-4" id="form-busqueda" method="post">
                <input id="institucionId" name="institucionId" value="${params.institucionId}" hidden>
            </form>
        </div>

        <div class="container mb-5">

            <div class="d-flex justify-content-end mb-2">
                <a onclick="mostrarProceso(1000);" href="/carrera/descargarPlantilla" class="btn btn-outline-success">
                    <i class="bi bi-cloud-arrow-down-fill mr-2"></i>Descargar plantilla
                </a>
            </div>

            <form action="/carrera/cargarPorExcel" enctype="multipart/form-data" method="POST" class="needs-validation" novalidate>

                <label for="instituciones">Institución <font color="red">*</font></label>
                <div class="input-group mb-3">
                    <g:select  class="form-control" id="instituciones" name="instituciones" from="${instituciones}" optionKey="id" optionValue="nombre" value="${params?.institucionId}" required="true"/>
                    <input id="institucionIdAux" name="institucionId" value="${params.institucionId}" hidden>
                </div>

                <div class="form-group">
                    <label for="nivelId">Nivel <font color="red">*</font></label>
                    <g:select  class="form-control" id="nivelId" name="nivelId" from="${niveles}" optionKey="id" optionValue="nombre"value="${params?.nivelId}" noSelection="${['':'Seleccione...']}" required="true"/>
                    <div class="invalid-feedback">
                        Por favor ingrese el nivel.
                    </div>
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
                        <a onclick="mostrarProceso();" href="carrera/listar" class="btn btn-secondary">Cancelar</a>
                        <button type="submit" class="btn btn-primary">Guardar</button>
                    </p>
                </div>
            </form>
        </div>

        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <asset:javascript src="jquery-editable-select.js"/>
        <asset:javascript src="jquery-editable-select.min.js"/>
        <asset:javascript src="form-subirExcel.js"/>
    </body>
</html>
