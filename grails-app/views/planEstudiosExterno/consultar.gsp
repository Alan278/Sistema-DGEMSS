<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Detalles del Plan de Estudios Foráneo / Externo</title>
        <script src="jquery-3.5.1.min.js"></script>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/planEstudiosExterno/registro" class="btn btn-primary col-md-12 my-2">
                Nuevo Plan de Estudios Externo
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Detalles del Plan de Estudios Foráneo / Externo
                </h3>
            </div>
        </div>

        <div class="container">
            <div class="form-group mb-4" >
                <h5> <b> Carrera </b> </h5>
                <h6 class="ml-3">${planEstudios?.carrera?.nombre} </h6>
            </div>
            <div class="form-group mb-4" >
                <h5> <b> Nombre </b> </h5>
                <h6 class="ml-3">${planEstudios?.nombre} </h6>
            </div>
            <div class="form-group mb-4" >
                <h5> <b> Periodo </b> </h5>
                <h6 class="ml-3">${planEstudios?.periodo?.nombre} </h6>
            </div>
            <div class="form-group mb-4" >
                <h5> <b> Turno </b> </h5>
                <h6 class="ml-3">${planEstudios?.turno?.nombre} </h6>
            </div>
            <div class="row col-md-4">
                <h5> <b> Horario </b> </h5>
                <table class="table table-bordered table-sm mt-2 ml-3">
                    <tBody>
                        <tr>
                            <td class="table-active"><h6 class='mb-0'> Hora de Inicio </h6></td>
                            <td>
                                <input type="time" value="${planEstudios?.horaInicio}"  disabled class="unstyle"></input>
                            </td>
                        </tr>
                        <tr>
                            <td class="table-active"><h6 class='mb-0'> Hora de Fin </h6></td>
                            <td>
                                <input type="time" value="${planEstudios?.horaFin}" class="unstyle" disabled></input>
                            </td>
                        </tr>
                    </tBody>
                </table>
            </div>
            <div class="form-group mb-4" >
                <h5> <b> Número de asignaturas</b> </h5>
                <h6 class="ml-3"><a onclick="mostrarProceso();" href="/asignaturaExterna/listar?institucionId=${planEstudios.carrera.institucion.id}&carreraId=${planEstudios.carrera.id}&planEstudiosId=${planEstudios.id}">${params.numeroAsignaturas} </a></h6>
            </div>
            <div class="form-group mb-4" >
                <h5> <b> Número de ciclos</b> </h5>
                <h6 class="ml-3">${planEstudios?.ciclos}</h6>
            </div>
            <div class="row col-md-4">
                <h5> <b> Calificaciones </b> </h5>
                <table class="table table-bordered table-sm mt-2 ml-3">
                    <tBody>
                        <tr>
                            <td class="table-active"><h6 class='mb-0'> Mínima </h6></td>
                            <td>${planEstudios?.calificacionMinima}</td>
                        </tr>
                        <tr>
                            <td class="table-active"><h6 class='mb-0'> Mínima aprobatoria </h6></td>
                            <td>${planEstudios?.calificacionMinimaAprobatoria}</td>
                        </tr>
                        <tr>
                            <td class="table-active"><h6 class='mb-0'> Máxima </h6></td>
                            <td>${planEstudios?.calificacionMaxima}</td>
                        </tr>
                    </tBody>
                </table>
            </div>

            <div class="form-row justify-content-end">
                <p>
                    <a onclick="mostrarProceso();" href="asignaturaExterna/registro?institucionId=${planEstudios?.carrera?.institucion?.id}&carreraId=${planEstudios?.carrera?.id}&planEstudiosId=${planEstudios?.id}" class="btn btn-primary">
                        Nueva Asignatura
                    </a>
                    <a onclick="mostrarProceso();" href="planEstudiosExterno/modificacion/${planEstudios?.id}" class="btn btn-primary">
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
                        <h5 class="modal-title" id="exampleModalLongTitle">Eliminar Plan de Estudios Foráneo / Externo</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        ¿Desea eliminar el plan de estudios foráneo / externo?
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                        <a onclick="mostrarProceso();" href="planEstudiosExterno/eliminar/${planEstudios?.id}" class="btn btn-primary">Aceptar</a>
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