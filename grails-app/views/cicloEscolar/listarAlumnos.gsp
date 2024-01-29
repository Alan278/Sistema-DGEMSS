<!doctype html>
<html>
     <head>
        <meta name="layout" content="main"/>
        <title>Lista de Alumnos</title>
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
                    Detalles del ciclo escolar
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
            <div class="jumbotron p-3 mb-4">
                <h6 class="m-0">
                    <b> Ciclo escolar:</b>
                    <medium class="text-muted">${cicloEscolar?.nombre}</medium><br>
                </h6>
            </div>
            <ul class="nav nav-tabs mb-4">
                <li class="nav-item">
                  <a class="nav-link" href="/cicloEscolar/consultar/${params?.id}">Detalles</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link active" href="/cicloEscolar/listarAlumnos/${params?.id}">Alumnos (${cicloEscolar?.numAlumnos})</a>
                  </li>
            </ul>
        </div>

        <div class="container">
            <form id="form-busqueda" action="/cicloEscolar/listarAlumnos/${cicloEscolar?.id}" class="mt-4">
                <div class="row mt-4">
                    <div class="col-md-6">
                    </div>
                    <div class="col-md-6">
                        <div class="input-group mb-3">
                            <input id="search" type="text" class="form-control" name="search" value="${params?.search}" placeholder="Nombre / CURP / Matrícula del alumno">
                            <div class="input-group-prepend">
                                <input id="buscar" type="submit" class="btn btn-primary" value="Buscar"/>
                            </div>
                            <g:if test="${params.search}">
                                <div class="input-group-append">
                                    <button id="limpiar-search" class="btn btn-outline-secondary">
                                        <i class="fa fa-close"></i>
                                    </button>
                                </div>
                            </g:if>
                        </div>
                    </div>
                </div>
            </form>
        </div>

        <div class="container">
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Matrícula</th>
                        <th>Nombre</th>
                        <th></th>
                    </tr>
                </thead>
                <tBody>
                    <g:each status="i" in="${inscripciones}" var="registro">
                            <tr>
                                <td class="align-middle">${i+1}</td>
                                <td class="align-middle">${registro?.alumno?.matricula}</td>
                                <td class="align-middle"><a onclick="mostrarProceso();" href="/alumno/consultar/${registro?.alumno?.id}" class="alert-link">${registro?.alumno?.persona?.nombreCompleto}</a></td>
                                <td align="end">
                                    <g:if test="${registro?.estatusRegistro?.id == 1}">
                                        <button type="button" class="btn btn-outline-danger btn-sm" data-id="${registro?.alumno?.id}" data-toggle="modal" data-target="#modalEliminacion_${registro?.alumno?.id}" data-tooltip="tooltip" data-placement="top" title="Eliminar alumno del ciclo">
                                            <i class= "fa fa-trash"></i>
                                        </button>
                                    </g:if>
                                    <div class="modal fade" id="modalEliminacion_${registro?.alumno?.id}" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
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
                                                    <a onclick="mostrarProceso();"  href="/cicloEscolar/eliminarInscripcion/${registro?.id}" class="btn btn-primary">Aceptar</a>
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
        <asset:javascript src="filter-buttons.js"/>

        <script>
            $(function () {
                $('[data-tooltip="tooltip"]').tooltip()
            })
        </script>

    </body>
</html>