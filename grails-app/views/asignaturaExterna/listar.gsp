<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Lista de Asignaturas Foráneas / Externas</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
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
                    Asignaturas Foráneas / Externas
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
            <form action="/asignaturaExterna/listar" class="mt-4" id="form-busqueda">
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
                        <g:select  class="form-control" id="planesEstudios" name="planesEstudios" from="${planesEstudios}" optionKey="id" optionValue="nombre" value="${parametros?.planEstudiosId}"/>
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
                            <input type="text" class="form-control" id="search" name="search" value="${parametros?.search}" placeholder="Nombre de la asignatura">
                            <div class="input-group-prepend">
                                <input id='buscar' type="button" class="btn btn-primary" value="Buscar"/>
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
                        <th>Clave</th>
                        <th>Nombre</th>
                        <th></th>
                    </tr>
                </thead>
                <tBody>
                    <g:each in="${asignaturas}" var="asignatura">
                            <tr>
                                <td>${asignatura?.clave}</td>
                                <td><a onclick="mostrarProceso();" href="/asignaturaExterna/consultar/${asignatura?.id}" class="alert-link" data-tooltip="tooltip" data-placement="top" title="Consultar Asignatura Externa">${asignatura?.nombre}</a></td>
                                <td>
                                    <p align="right">
                                        <a onclick="mostrarProceso();" href="/asignaturaExterna/modificacion/${asignatura?.id}" class="btn btn-primary" data-tooltip="tooltip" data-placement="top" title="Editar Registro">
                                            <i class="fa fa-pencil"></i>
                                        </a>
                                        <button type="button" class="btn btn-danger" data-id="${asignatura?.id}" data-toggle="modal" data-target="#modalEliminacion_${asignatura?.id}" data-tooltip="tooltip" data-placement="top" title="Eliminar Registro">
                                            <i class= "fa fa-trash"></i>
                                        </button>
                                    </p>
                                    <div class="modal fade" id="modalEliminacion_${asignatura?.id}" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
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
                                                    <a onclick="mostrarProceso();" href="/asignaturaExterna/eliminar/${asignatura?.id}" class="btn btn-primary">Aceptar</a>
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
        <g:render template="/layouts/pagination" model="${[linkUri: "asignaturaExterna/listar", linkParams: [search: params.search?:"", institucionId: params.institucionId?:"", carreraId: params.carreraId?:"", planEstudiosId: params.planEstudiosId?:""], count: conteo, max: params.max, offset: params.offset]}"/>
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