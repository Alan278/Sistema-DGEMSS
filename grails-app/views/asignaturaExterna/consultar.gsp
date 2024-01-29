<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Detalles de la Asignatura Foránea / Externa</title>
        <script src="jquery-3.5.1.min.js"></script>
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
                    Detalles de la Asignatura Foránea / Externa
                </h3>
            </div>
        </div>

        <div class="container">
            <div class="form-group mb-4" >
                <h5> <b> Plan de Estudios </b> </h5>
                <h6 class="ml-3">${asignatura?.planEstudios?.nombre} </h6>
            </div>
            <div class="form-group mb-4" >
                <h5> <b> Nombre </b> </h5>
                <h6 class="ml-3">${asignatura?.nombre} </h6>
            </div>
            <div class="form-group mb-4" >
                <h5> <b> Clave </b> </h5>
                <h6 class="ml-3">${asignatura?.clave} </h6>
            </div>
            <div class="form-group mb-4" >
                <h5> <b> Horas a la semana </b> </h5>
                <h6 class="ml-3">${asignatura?.horas} </h6>
            </div>
            <div class="form-group mb-4" >
                <h5> <b> Periodo </b> </h5>
                <h6 class="ml-3">${asignatura?.periodo} </h6>
            </div>
            <div class="form-row justify-content-end">
                <p>
                    <a onclick="mostrarProceso();" href="asignaturaExterna/modificacion/${asignatura?.id}" class="btn btn-primary">
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
                        <h5 class="modal-title" id="exampleModalLongTitle">Eliminar Asignatura Foránea / Externa</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        ¿Desea eliminar la asignatura seleccionada?
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                        <a onclick="mostrarProceso();" href="asignaturaExterna/eliminar/${asignatura?.id}" class="btn btn-primary">Aceptar</a>
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