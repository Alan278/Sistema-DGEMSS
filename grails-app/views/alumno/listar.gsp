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
            <a href="/alumno/registro" class="btn btn-primary col-md-12 my-2">
                Nuevo alumno
            </a>
            <a href="/alumno/subirExcel" class="btn btn-primary col-md-12 my-2">
                Cargar alumnos
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Matrícula de alumnos
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

        <div class="container">
            <form id="form-busqueda" action="/alumno/listar" class="mt-4">

                <label for="instituciones">Institución</label>
                <div class="input-group mb-2">
                    <g:select  class="form-control" id="instituciones" name="instituciones" from="${instituciones}" optionKey="id" optionValue="nombre" value="${parametros?.institucionId}"/>
                    <input id="institucionId" name="institucionId" value="${params.institucionId}" hidden>
                    <g:if test="${params.institucionId}">
                        <div class="input-group-prepend">
                            <button id="limpiar-institucion" class="btn btn-outline-secondary">
                                <i class="fa fa-close"></i>
                            </button>
                        </div>
                    </g:if>
                </div>

                <g:if test="${parametros?.institucionId}">
                    <label for="carreras">Carrera</label>
                    <div class="input-group mb-2">
                        <g:select  class="form-control" id="carreras" name="carreras" from="${carreras}" optionKey="id" optionValue="nombre" value="${parametros?.carreraId}"/>
                        <input id="carreraId" name="carreraId" value="${params.carreraId}" hidden>
                        <g:if test="${params.carreraId}">
                            <div class="input-group-prepend">
                                <button id="limpiar-carrera" class="btn btn-outline-secondary">
                                    <i class="fa fa-close"></i>
                                </button>
                            </div>
                        </g:if>
                    </div>
                </g:if>

                <g:if test="${parametros?.carreraId}">
                    <label for="planesEstudios">Plan de estudios</label>
                    <div class="input-group">
                        <g:select  class="form-control" id="planesEstudios" name="planesEstudios" from="${planesEstudios}" optionKey="id" optionValue='${{"NOMBRE: ${it.nombre} :::: RVOE: ${it.rvoe ?: ""} - ${it.fechaRvoeFormato ?: ""}"}}' value="${parametros?.planEstudiosId}"/>
                        <input id="planEstudiosId" name="planEstudiosId" value="${params.planEstudiosId}" hidden>
                        <g:if test="${params.planEstudiosId}">
                            <div class="input-group-prepend">
                                <button id="limpiar-planEstudios" class="btn btn-outline-secondary">
                                    <i class="fa fa-close"></i>
                                </button>
                            </div>
                        </g:if>
                    </div>
                </g:if>

                <div class="row mt-4">
                    <div class="col-md-6">
                    </div>
                    <div class="col-md-6">
                        <div class="input-group mb-3">
                            <input id="search" type="text" class="form-control" name="search" value="${parametros?.search}" placeholder="Nombre / CURP / Matrícula del alumno">
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
                        <th>CURP</th>
                        <th></th>
                    </tr>
                </thead>
                <tBody>
                    <g:each status="i" in="${alumnos}" var="alumno">
                            <tr>
                                <td class="align-middle">${i+1}</td>
                                <td class="align-middle">${alumno?.matricula}</td>
                                <td class="align-middle">
                                    <a onclick="mostrarProceso();" href="/alumno/consultar/${alumno?.id}" class="alert-link" data-tooltip="tooltip" data-placement="top" title="Consultar Alumno">
                                        ${alumno?.persona?.nombreCompleto}
                                    </a>
                                </td>
                                <td class="align-middle">${alumno?.persona?.curp}</td>
                                <td class="align-middle" align="end">
                                    <a onclick="mostrarProceso();" href="/alumno/listarCiclosEscolares/${alumno?.id}" class="btn btn-outline-primary btn-sm" >
                                        <g:if test="${alumno?.numCiclosEscolares < 10}">
                                            Ciclos escolares | 0${alumno?.numCiclosEscolares}
                                        </g:if>
                                        <g:else>
                                            Ciclos escolares | ${alumno?.numCiclosEscolares}
                                        </g:else>
                                    </a>
                                    <a onclick="mostrarProceso();" href="/evaluacion/listar/${alumno?.id}" class="btn btn-outline-primary btn-sm" >
                                        <g:if test="${alumno?.numEvaluaciones < 10}">
                                            Evaluaciones | 0${alumno?.numEvaluaciones}
                                        </g:if>
                                        <g:else>
                                            Evaluaciones | ${alumno?.numEvaluaciones}
                                        </g:else>
                                    </a>
                                    <a onclick="mostrarProceso();" href="/alumno/modificacion/${alumno?.id}" class="btn btn-outline-primary btn-sm" data-tooltip="tooltip" data-placement="top" title="Editar Registro">
                                        <i class="fa fa-pencil"></i>
                                    </a>
                                    <g:if test="${alumno.estatusRegistro.id == 1}">
                                        <button type="button" class="btn btn-outline-danger btn-sm" data-id="${alumno?.id}" data-toggle="modal" data-target="#modalEliminacion_${alumno?.id}" data-tooltip="tooltip" data-placement="top" title="Eliminar Registro">
                                            <i class= "fa fa-trash"></i>
                                        </button>
                                    </g:if>
                                    <g:if test="${alumno.estatusRegistro.id != 1}">
                                        <button type="button" class="btn btn-outline-secondary btn-sm" disabled>
                                            <i class= "fa fa-trash"></i>
                                        </button>
                                    </g:if>
                                    <div class="modal fade" id="modalEliminacion_${alumno?.id}" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
                                        <div class="modal-dialog modal-dialog-centered" role="document">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <h5 class="modal-title" id="exampleModalLongTitle">Eliminar Alumno</h5>
                                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                                        <span aria-hidden="true">&times;</span>
                                                    </button>
                                                </div>
                                                <div class="modal-body">
                                                    ¿Desea eliminar al Alumno?
                                                </div>
                                                <div class="modal-footer">
                                                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                                                    <a onclick="mostrarProceso();"  href="/alumno/eliminar/${alumno?.id}" class="btn btn-primary">Aceptar</a>
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

        <g:render template="/layouts/pagination" model="${[linkUri: "alumno/listar", linkParams: [search: params.search?:"", institucionId: params.institucionId?:"", carreraId: params.carreraId?:"", cicloEscolarId: params.cicloEscolarId?:""], count: conteo, max: params.max, offset: params.offset]}"/>

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