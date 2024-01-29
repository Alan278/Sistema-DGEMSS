<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Detalles de la Asignatura</title>
        <script src="jquery-3.5.1.min.js"></script>
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
                    Detalles de la Asignatura
                </h3>
            </div>
        </div>

        <div class="container">
            <div>
                <h6 class="pb-3">
                    <medium class="text-muted">${asignatura?.nombre}</medium><br>
                </h6>
                <h6 class="pb-1">
                    <b> Institución:</b>
                    <medium class="text-muted">${asignatura?.planEstudios?.carrera?.institucion?.nombre}</medium>
                </h6>
                <h6 class="pb-1">
                    <b> Carrera:</b>
                    <medium class="text-muted">${asignatura?.planEstudios?.carrera?.nombre}</medium>
                </h6>
                <h6 class="pb-1">
                    <b> Plan de Estudios:</b>
                    <medium class="text-muted">${asignatura?.planEstudios?.nombre}</medium>
                </h6>
                <h6 class="pb-1">
                    <b> Horas a la semana:</b>
                    <medium class="text-muted">${asignatura?.horas}</medium>
                </h6>
                <h6 class="pb-1">
                    <b> Formación:</b>
                    <medium class="text-muted">${asignatura?.formacion?.nombre}</medium>
                </h6>
                <h6 class="pb-1">
                    <b> Periodo:</b>
                    <medium class="text-muted">${asignatura?.periodo}</medium>
                </h6>
                <h6 class="pb-1">
                    <b> Clave:</b>
                    <medium class="text-muted">${asignatura?.clave}</medium>
                </h6>
            </div>

            <div class="form-row justify-content-end">
                <p>
                    <a onclick="mostrarProceso();" href="asignatura/modificacion/${asignatura?.id}" class="btn btn-primary">
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
                        <h5 class="modal-title" id="exampleModalLongTitle">Eliminar Asignatura</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        ¿Desea eliminar la asignatura?
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                        <a onclick="mostrarProceso();" href="asignatura/eliminar/${asignatura?.id}" class="btn btn-primary">Aceptar</a>
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