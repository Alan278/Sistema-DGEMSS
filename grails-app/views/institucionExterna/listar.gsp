<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Lista de Instituciones Foráneas / Externas</title>
        <script src="jquery-3.5.1.min.js"></script>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/institucionExterna/registro" class="btn btn-primary col-md-12 my-2">
                Nueva Institución Externa
            </a>
        </content>

        <div class="row mb-4">
                <div class="col-md-12 pt-2 border-bottom">
                    <h3 class="page-title pl-3">
                        Instituciones Foráneas / Externas
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
            <form id='form-busqueda' action="/institucionExterna/listar" class="mt-4">
                <div class="row">
                    <div class="col-md-6">
                    </div>
                    <div class="col-md-6">
                        <div class="input-group mb-3">
                            <input type="text" class="form-control" name="search" value="${params.search}" placeholder="Nombre de la institución">
                            <div class="input-group-append">
                                <input id="buscar" type="submit" class="btn btn-primary" value="Buscar"/>
                            </div>
                            <g:if test="${params.search}">
                                <div class="input-group-append">
                                    <g:link uri="/institucionExterna/modificacion/" params="${[max: params.max, offset: params.offset]}" class="btn btn-outline-secondary">
                                        <i class="fa fa-close"></i>
                                    </g:link>
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
                            <th>Clave DGP</th>
                            <th>Clave CCT</th>
                            <th>Nombre</th>
                            <th>Carreras activas</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tBody>

                        <g:each status="i" in="${instituciones}" var="elemento">
                                <tr>
                                    <td>${elemento.claveDgp}</td>
                                    <td>${elemento.claveCt}</td>
                                    <td>
                                        <a onclick="mostrarProceso();" href="/institucionExterna/consultar/${elemento.id}?numeroCarreras=${numeroCarreras[i]}" data-tooltip="tooltip" data-placement="top" title="Consultar institución" class="alert-link">
                                            ${elemento.nombre}
                                        </a>
                                    </td>
                                    <td>
                                        <a onclick="mostrarProceso();" href="/carreraExterna/listar?institucionId=${elemento.id}" data-tooltip="tooltip" data-placement="top" title="Consultar carreras" class="alert-link">
                                            ${numeroCarreras[i]}
                                        </a>
                                    </td>
                                    <td>
                                        <p align="right"><a onclick="mostrarProceso();" href="/institucionExterna/modificacion/${elemento.id}" class="btn btn-primary" data-tooltip="tooltip" data-placement="top" title="Editar registro">
                                            <i class="fa fa-pencil"></i>
                                        </a>
                                         <button type="button" class="btn btn-danger" data-id="${elemento.id}" data-toggle="modal" data-target="#modalEliminacion_${elemento.id}" data-tooltip="tooltip" data-placement="top" title="Eliminar registro">
                                            <i class= "fa fa-trash"></i>
                                         </button></p>
                                         <div class="modal fade" id="modalEliminacion_${elemento.id}" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
                                            <div class="modal-dialog modal-dialog-centered" role="document">
                                                <div class="modal-content">
                                                    <div class="modal-header">
                                                        <h5 class="modal-title" id="exampleModalLongTitle">Eliminar Institución Foránea / Externa</h5>
                                                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                                            <span aria-hidden="true">&times;</span>
                                                        </button>
                                                    </div>
                                                    <div class="modal-body">
                                                        ¿Desea eliminar la instituci&oacute;n seleccionada?
                                                    </div>
                                                    <div class="modal-footer">
                                                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                                                        <a onclick="mostrarProceso();" href="/institucionExterna/eliminar/${elemento.id}" class="btn btn-primary">Aceptar</a>
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
        <g:render template="/layouts/pagination" model="${[linkUri: "institucionExterna/listar", linkParams: [search: params.search?:""], count: conteo, max: params.max, offset: params.offset]}"/>

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