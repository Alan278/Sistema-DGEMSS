<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Detalles de la carrera</title>
        <script src="jquery-3.5.1.min.js"></script>
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
                    Detalles de la carrera
                </h3>
            </div>
        </div>

        <div class="container">
            <div>
                <h6 class="pb-3">
                    <medium class="text-muted">${carrera?.nombre}</medium><br>
                </h6>
                <h6 class="pb-1">
                    <b> Institución:</b>
                    <medium class="text-muted">${carrera?.institucion?.nombre}</medium>
                </h6>
                <h6 class="pb-1">
                    <b> Número de planes de estudios:</b>
                    <medium class="text-muted">
                        <a onclick="mostrarProceso();" href="/planEstudios/listar?institucionId=${carrera.institucion.id}&carreraId=${carrera.id}">
                            ${carrera?.numPlanesEstudio}
                        </a>
                    </medium>
                </h6>
                <h6 class="pb-1">
                    <b> Clave SEEM:</b>
                    <medium class="text-muted">${carrera?.claveSeem}</medium>
                </h6>
                <h6 class="pb-1">
                    <b> Clave DGP:</b>
                    <medium class="text-muted">${carrera?.claveDgp}</medium>
                </h6>
                <h6 class="pb-1">
                    <b> Nivel:</b>
                    <medium class="text-muted">${carrera?.nivel?.nombre}</medium>
                </h6>
                <h6 class="pb-1">
                    <b> Modalidad:</b>
                    <medium class="text-muted">${carrera?.modalidad?.nombre}</medium>
                </h6>
                <h6 class="pb-1">
                    <b> Área:</b>
                    <medium class="text-muted">${carrera?.area?.nombre}</medium>
                </h6>
            </div>

            <div class="form-row justify-content-end">
                <p>
                    <a onclick="mostrarProceso();" href="planEstudios/registro?institucionId=${carrera?.institucion?.id}&carreraId=${carrera?.id}" class="btn btn-primary">
                        Nuevo Plan
                    </a>
                    <a onclick="mostrarProceso();" href="carrera/modificacion/${carrera?.id}" class="btn btn-primary">
                        Modificar
                    </a>
                    <button type="button" class="btn btn-danger" data-toggle="modal" data-target="#modalEliminacion">
                        Eliminar
                    </button>
                </p>
            </div>
        </div>

        <div class="modal fade" id="modalEliminacion" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="exampleModalLongTitle">Eliminar Carrera</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        ¿Desea eliminar la carrera?
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                        <a onclick="mostrarProceso();" href="carrera/eliminar/${carrera?.id}" class="btn btn-primary">Aceptar</a>
                    </div>
                </div>
            </div>
        </div>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <asset:javascript src="jquery-editable-select.js"/>
        <asset:javascript src="jquery-editable-select.min.js"/>
        <asset:javascript src="filter-buttons.js"/>

    </body>
</html>