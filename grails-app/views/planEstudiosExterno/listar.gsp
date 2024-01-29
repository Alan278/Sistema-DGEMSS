<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Lista de Planes de estudios Foráneos / Externos</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
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
                    Planes de estudios Foráneos / Externos
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
            <form action="/planEstudiosExterno/listar" class="mt-4" id="form-busqueda">
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
                    <div class="input-group">
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

                <div class="row mt-4">
                    <div class="col-md-6">
                    </div>
                    <div class="col-md-6">
                        <div class="input-group mb-3">
                            <input type="text" class="form-control" id="search" name="search" value="${parametros?.search}" placeholder="Nombre del plan">
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
                        <th>Nombre</th>
                        <th>Asignaturas activas</th>
                        <th></th>
                    </tr>
                </thead>
                <tBody>
                    <g:each status="i" in="${planesEstudios}" var="planEstudios">
                            <tr>
                                <td>
                                    <a onclick="mostrarProceso();" href="/planEstudiosExterno/consultar/${planEstudios.id}?numeroAsignaturas=${numeroAsignaturas[i]}" class="alert-link" data-tooltip="tooltip" data-placement="top" title="Consultar plan">
                                        ${planEstudios.nombre}
                                    </a>
                                </td>
                                <td>
                                    <a onclick="mostrarProceso();" href="/asignaturaExterna/listar?institucionId=${planEstudios.carrera.institucion.id}&carreraId=${planEstudios.carrera.id}&planEstudiosId=${planEstudios.id}" class="alert-link" data-tooltip="tooltip" data-placement="top" title="Consultar asignaturas">
                                        ${numeroAsignaturas[i]}
                                    </a>
                                </td>
                                <td>
                                    <p align="right">
                                        <a onclick="mostrarProceso();" href="/planEstudiosExterno/modificacion/${planEstudios.id}" class="btn btn-primary" data-tooltip="tooltip" data-placement="top" title="Editar registro">
                                            <i class="fa fa-pencil"></i>
                                        </a>
                                        <button type="button" class="btn btn-danger" data-id="${planEstudios.id}" data-toggle="modal" data-target="#modalEliminacion_${planEstudios.id}" data-tooltip="tooltip" data-placement="top" title="Eliminar registro">
                                            <i class= "fa fa-trash"></i>
                                        </button>
                                    </p>
                                    <div class="modal fade" id="modalEliminacion_${planEstudios.id}" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
                                        <div class="modal-dialog modal-dialog-centered" role="document">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <h5 class="modal-title" id="exampleModalLongTitle">Eliminar Plan de Estudios Foráneo / Externo</h5>
                                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                                        <span aria-hidden="true">&times;</span>
                                                    </button>
                                                </div>
                                                <div class="modal-body">
                                                    ¿Desea eliminar el plan de estudios seleccionado?
                                                </div>
                                                <div class="modal-footer">
                                                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                                                    <a onclick="mostrarProceso();" href="/planEstudiosExterno/eliminar/${planEstudios.id}" class="btn btn-primary">Aceptar</a>
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

        <g:render template="/layouts/pagination" model="${[linkUri: "planEstudiosExterno/listar", linkParams: [search: params.search?:"", institucionId: params.institucionId?:"", carreraId: params.carreraId?:""], count: conteo, max: params.max, offset: params.offset]}"/>

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