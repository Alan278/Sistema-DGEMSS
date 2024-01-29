<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Detalles del Plan de Estudios</title>
        <script src="jquery-3.5.1.min.js"></script>
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
                    Detalles del Plan de Estudios
                </h3>
            </div>
        </div>

        <div class="container">
            <h6 class="pb-3">
                <medium class="text-muted">${planEstudios?.nombre}</medium><br>
            </h6>
            <h6 class="pb-1">
                <b> Institución:</b>
                <medium class="text-muted">${planEstudios?.carrera?.institucion?.nombre}</medium>
            </h6>
            <h6 class="pb-1">
                <b> Carrera:</b>
                <medium class="text-muted">${planEstudios?.carrera?.nombre}</medium>
            </h6>
            <h6 class="pb-1">
                <b> Número de RVOE:</b>
                <medium class="text-muted">${planEstudios?.rvoe}</medium>
            </h6>
            <h6 class="pb-1">
                <b> Fecha de RVOE:</b>
                <medium class="text-muted">${planEstudios?.fechaRvoeFormato}</medium>
            </h6>
            <h6 class="pb-1">
                <b> Periodo:</b>
                <medium class="text-muted">${planEstudios?.periodo?.nombre}</medium>
            </h6>
            <h6 class="pb-1">
                <b> Turno:</b>
                <medium class="text-muted">${planEstudios?.turno?.nombre}</medium>
            </h6>
            <h6 class="pb-1">
                <b> Número de asignaturas:</b>
                <medium class="text-muted"><a onclick="mostrarProceso();" href="/asignatura/listar?institucionId=${planEstudios.carrera.institucion.id}&carreraId=${planEstudios.carrera.id}&planEstudiosId=${planEstudios.id}">${planEstudios.numAsignaturas}</a></medium>
            </h6>
            <h6 class="pb-1">
                <b>  Número de ciclos:</b>
                <medium class="text-muted">${planEstudios?.ciclos}</medium>
            </h6>
            <g:if test="${planEstudios?.horaInicio}">
                <div class="row col-md-3">
                    <h6> <b> Horario </b> </h6>
                    <table class="table table-bordered table-sm mt-1">
                        <tBody>
                            <tr>
                                <td class="table-active pl-2">
                                    Hora de Inicio
                                </td>
                                <td align="center">
                                    ${planEstudios?.horaInicio}
                                </td>
                            </tr>
                            <tr>
                                <td class="table-active pl-2">
                                    Hora de Fin
                                </td>
                                <td align="center">
                                    ${planEstudios?.horaFin}
                                </td>
                            </tr>
                        </tBody>
                    </table>
                </div>
            </g:if>
            <g:if test="${planEstudios?.calificacionMinima}">
                <div class="row col-md-3">
                    <h6> <b> Calificaciones </b> </h6>
                    <table class="table table-bordered table-sm mt-1">
                        <tBody>
                            <tr>
                                <td class="table-active pl-2">Mínima</td>
                                <td align="center">${planEstudios?.calificacionMinima}</td>
                            </tr>
                            <tr>
                                <td class="table-active pl-2">Mínima aprobatoria</td>
                                <td align="center">${planEstudios?.calificacionMinimaAprobatoria}</td>
                            </tr>
                            <tr>
                                <td class="table-active pl-2">Máxima</td>
                                <td align="center">${planEstudios?.calificacionMaxima}</td>
                            </tr>
                        </tBody>
                    </table>
                </div>
            </g:if>

            <div class="form-row justify-content-end">
                <p>
                    <a onclick="mostrarProceso();" href="asignatura/registro?institucionId=${planEstudios?.carrera?.institucion?.id}&carreraId=${planEstudios?.carrera?.id}&planEstudiosId=${planEstudios?.id}" class="btn btn-primary">
                        Nueva Asignatura
                    </a>
                    <a onclick="mostrarProceso();" href="planEstudios/modificacion/${planEstudios?.id}" class="btn btn-primary">
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
                        <h5 class="modal-title" id="exampleModalLongTitle">Eliminar Plan de Estudios</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        ¿Desea eliminar el plan de estudios?
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                        <a onclick="mostrarProceso();" href="planEstudios/eliminar/${planEstudios?.id}" class="btn btn-primary">Aceptar</a>
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