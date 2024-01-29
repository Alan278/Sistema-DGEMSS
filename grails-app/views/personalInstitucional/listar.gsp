<!doctype html>
<html>
     <head>
        <meta name="layout" content="main"/>
        <title>Lista de Personal</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/personalInstitucional/registro" class="btn btn-primary col-md-12 my-2">
                Nuevo Personal
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Personal Institucional
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
            <form id="form-busqueda" action="/personalInstitucional/listar" class="mt-4">
                <label for="instituciones">Institución</label>
                <div class="input-group mb-4">
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
                <div class="row">
                    <div class="col-md-6">
                    </div>
                    <div class="col-md-6">
                        <div class="input-group mb-3">
                            <input id="search" type="text" class="form-control" name="search" value="${parametros?.search}" placeholder="Nombre de la persona">
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
                        <th>Nombre</th>
                        <th>Cargo</th>
                        <th>Institución</th>
                        <th></th>
                    </tr>
                </thead>
                <tBody>
                    <g:each status="i" in="${personalInstitucional}" var="personal">
                            <tr>
                                <td class="align-middle">${i+1}</td>
                                <td class="align-middle">
                                    <a onclick="mostrarProceso();" href="/personalInstitucional/consultar/${personal.id}" data-tooltip="tooltip" data-placement="top" title="Consultar persona" class="alert-link" >
                                        ${personal.persona.nombre} ${personal.persona.primerApellido} ${personal.persona.segundoApellido}
                                    </a>
                                </td>
                                <td class="align-middle">${personal?.cargoInstitucional?.nombre}</td>
                                <td class="align-middle">${personal?.institucion?.nombre}</td>
                                <td class="align-middle" align="end">
                                        <a onclick="mostrarProceso();" href="/personalInstitucional/modificacion/${personal.id}" class="btn btn-outline-primary btn-sm" data-tooltip="tooltip" data-placement="top" title="Editar registro">
                                            <i class="fa fa-pencil"></i>
                                        </a>
                                        <button type="button" class="btn btn-outline-danger btn-sm" data-id="${personal.id}" data-toggle="modal" data-target="#modalEliminacion_${personal.id}" data-tooltip="tooltip" data-placement="top" title="Eliminar registro">
                                            <i class= "fa fa-trash"></i>
                                        </button>
                                    <div class="modal fade" id="modalEliminacion_${personal.id}" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
                                        <div class="modal-dialog modal-dialog-centered" role="document">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <h5 class="modal-title" id="exampleModalLongTitle">Eliminar Personal</h5>
                                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                                        <span aria-hidden="true">&times;</span>
                                                    </button>
                                                </div>
                                                <div class="modal-body">
                                                    ¿Desea eliminar al personal?
                                                </div>
                                                <div class="modal-footer">
                                                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                                                    <a onclick="mostrarProceso();"  href="/personalInstitucional/eliminar/${personal.id}" class="btn btn-primary">Aceptar</a>
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
        <g:render template="/layouts/pagination" model="${[linkUri: "personalInstitucional/listar", linkParams: [search: params.search?:"", institucionId: params.institucionId?:""], count: conteo, max: params.max, offset: params.offset]}"/>
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