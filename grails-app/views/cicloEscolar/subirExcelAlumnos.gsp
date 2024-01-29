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
            <a href="/cicloEscolar/subirExcelAlumnos/${cicloEscolar?.id}" class="btn btn-primary col-md-12 my-2">
                Cargar alumnos
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Carga de alumnos a ciclo escolar
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

        <div class="container mb-4">
            <h6 class="pb-1">
                <b> Ciclo escolar:</b>
                <medium class="text-muted">${cicloEscolar?.nombre}</medium><br>
            </h6>
            <h6 class="pb-1">
                <b> Institución:</b>
                <medium class="text-muted">${cicloEscolar?.planEstudios?.carrera?.institucion?.nombre}</medium>
            </h6>
            <h6 class="pb-1">
                <b> Carrera:</b>
                <medium class="text-muted">${cicloEscolar?.planEstudios?.carrera?.nombre}</medium>
            </h6>
            <h6 class="pb-1">
                <b> Plan de estudios:</b>
                <medium class="text-muted">${cicloEscolar?.planEstudios?.nombre}</medium>
            </h6>
            <h6 class="pb-1">
                <b> Periodo:</b>
                <medium class="text-muted">${cicloEscolar?.periodo}</medium>
            </h6>
        </div>

        <div class="container mb-5">

            <div class="d-flex justify-content-end mb-2">
                <a onclick="mostrarProceso(1000);" href="/cicloEscolar/descargarPlantillaAlumnos" class="btn btn-outline-success">
                    <i class="bi bi-cloud-arrow-down-fill mr-2"></i>Descargar plantilla
                </a>
            </div>

            <form action="/cicloEscolar/cargarPorExcelAlumnos/${cicloEscolar?.id}" enctype="multipart/form-data" method="POST" class="needs-validation" novalidate>
                <input type="text" name="cicloEscolarId" value="${cicloEscolar?.id}" hidden>
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
                        <a onclick="mostrarProceso();" href="cicloEscolar/listarAlumnos/${cicloEscolar?.id}" class="btn btn-secondary">Cancelar</a>
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
