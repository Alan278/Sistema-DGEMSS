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
                    Carga de Asignaturas
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
            <form action="/asignatura/subirExcel" class="mt-4" id="form-busqueda" method="post">
                <input id="institucionId" name="institucionId" value="${params.institucionId}" hidden>
                <input id="carreraId" name="carreraId" value="${params.carreraId}" hidden>
            </form>
        </div>

        <div class="container mb-5">

            <div class="d-flex justify-content-end mb-2">
                <a onclick="mostrarProceso(1000);" href="/asignatura/descargarPlantilla" class="btn btn-outline-success">
                    <i class="bi bi-cloud-arrow-down-fill mr-2"></i>Descargar plantilla
                </a>
            </div>

            <form action="/asignatura/cargarPorExcel" enctype="multipart/form-data" method="POST" class="needs-validation" novalidate>

                <label for="instituciones">Institución</label>
                <div class="input-group mb-3">
                    <g:select  class="form-control" id="instituciones" name="instituciones" from="${instituciones}" optionKey="id" optionValue="nombre" value="${params?.institucionId}" required="true"/>
                    <input id="institucionIdAux" name="institucionId" value="${params.institucionId}" hidden>
                </div>
                <label for="carreras">Carrera</label>
                <g:if test="${params?.institucionId}">
                    <div class="input-group">
                        <g:select  class="form-control" id="carreras" name="carreras" from="${carreras}" optionKey="id" optionValue="nombre" value="${params?.carreraId}" required="true"/>
                        <input id="carreraIdAux" name="carreraId" value="${params.carreraId}" hidden>
                    </div>
                </g:if>
                <g:else>
                    <input type="text" class="form-control" readonly>
                </g:else>

                <label class="mt-3" for="planesEstudios">Plan de estudios</label>
                <g:if test="${params?.carreraId}">
                    <div class="input-group">
                        <g:select  class="form-control" id="planesEstudios" name="planesEstudios" from="${planesEstudios}" optionKey="id" optionValue="nombre" value="${params?.planEstudiosId}" required="true"/>
                        <input id="planEstudiosId" name="planEstudiosId" value="${params.planEstudiosId}" hidden>
                        <div class="invalid-feedback">
                            Por favor elija un plan de estudios.
                        </div>
                    </div>
                </g:if>
                <g:else>
                    <div class="input-group">
                        <input type="text" class="form-control" required readonly>
                        <div class="invalid-feedback">
                            Por favor elija el archivo a subir.
                        </div>
                    </div>
                </g:else>

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
                        <a onclick="mostrarProceso();" href="asignatura/listar" class="btn btn-secondary">Cancelar</a>
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
