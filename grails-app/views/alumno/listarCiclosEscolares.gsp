<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Lista de Calificaciones</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/evaluacion/registro/${alumno?.id}" class="btn btn-primary col-md-12 my-2">
                Nueva evaluación
            </a>
            <a href="/alumno/agregarCicloEscolar/${alumno?.id}" class="btn btn-primary col-md-12 my-2">
                Nuevo ciclo escolar
            </a>
            <a href="/evaluacion/subirExcel/${alumno?.id}" class="btn btn-primary col-md-12 my-2">
                Cargar evaluaciones
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Detalles del alumno
                </h3>
            </div>
        </div>

        <div class="container">
            <g:if test="${flash.mensaje}">
                <g:if test="${flash.estatus}">
                    <div class="alert alert-success" role="alert">
                        ${flash.mensaje}
                    </div>
                </g:if>
                <g:else>
                    <div class="alert alert-danger" role="alert">
                        ${flash.mensaje}
                    </div>
                </g:else>
            </g:if>
        </div>

        <div class="container mb-4">
            <div class="jumbotron p-3">
                <h6 class="m-0 pb-1" >
                    <b> Alumno:</b>
                    <medium class="text-muted">${alumno?.persona?.nombreCompleto}</medium><br>
                </h6>
                <h6 class="m-0">
                    <b>Matrícula:</b>
                    <medium class="text-muted">${alumno?.matricula} </medium>
                </h6>
            </div>
            <ul class="nav nav-tabs mb-4">
                <li class="nav-item">
                    <a class="nav-link" href="/alumno/consultar/${params?.id}">Detalles</a>
                  </li>
                <li class="nav-item">
                  <a class="nav-link" href="/evaluacion/listar/${params?.id}">Evaluaciones (${alumno?.numEvaluaciones})</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link active" href="/alumno/listarCiclosEscolares/${params?.id}">Ciclos Escolares (${alumno?.numCiclosEscolares})</a>
                </li>
            </ul>
        </div>

        <div class="container pb-3 mb-5">
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>Ciclo Escolar</th>
                        <th class="text-center">Periodo</th>
                        <th>Fecha de inicio</th>
                        <th>Fecha de fin</th>
                        <!-- <th>Número de evaluaciones</th> -->
                        <th></th>
                    </tr>
                </thead>
                <tBody id="tb">
                    <g:each status="i" in="${ciclosEscolares}" var="registro">
                        <tr>
                            <td class="align-middle">${registro?.cicloEscolar?.nombre}</td>
                            <td class="align-middle text-center">${registro?.cicloEscolar?.periodo}</td>
                            <td class="align-middle">${registro?.cicloEscolar?.inicio}</td>
                            <td class="align-middle">${registro?.cicloEscolar?.fin}</td>
                            <!-- <td>${registro?.numEvaluaciones}</td> -->
                            <td class="align-middle" align="end">
                                <g:if test="${registro?.estatusRegistro?.id == 1}">
                                    <button type="button" class="btn btn-outline-danger btn-sm" data-id="${registro?.id}" data-toggle="modal" data-target="#modalEliminacion_${registro?.id}" data-tooltip="tooltip" data-placement="top" title="Eliminar alumno del ciclo">
                                        <i class= "fa fa-trash"></i>
                                    </button>
                                </g:if>
                                <div class="modal fade" id="modalEliminacion_${registro?.id}" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
                                    <div class="modal-dialog modal-dialog-centered" role="document">
                                        <div class="modal-content">
                                            <div class="modal-header">
                                                <h5 class="modal-title" id="exampleModalLongTitle">Eliminar alumno de ciclo escolar</h5>
                                                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                                    <span aria-hidden="true">&times;</span>
                                                </button>
                                            </div>
                                            <div class="modal-body align-items-start">
                                                ¿Desea eliminar al alumno del ciclo escolar? <br><br>
                                                Tenga en cuenta que al eliminar a un alumno de un ciclo escolar eliminara todas las evaluaciones registradas que a el correspondan
                                            </div>
                                            <div class="modal-footer">
                                                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                                                <a onclick="mostrarProceso();"  href="/alumno/eliminarInscripcion/${registro?.id}" class="btn btn-primary">Aceptar</a>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </g:each>
                </tBody>
            </table>
        </div>

        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <asset:javascript src="jquery-editable-select.js"/>
        <asset:javascript src="jquery-editable-select.min.js"/>

    </body>
</html>