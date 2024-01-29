<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Lista de Trámites</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/pagoActa/registro" class="btn btn-primary col-md-12 my-2">
                Nuevo trámite
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Acta 
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
            <form id="form-busqueda" action="/pago/listar" class="mt-4">
                <div class="row">
                    <div class="col-md-6">
                    </div>
                    <div class="col-md-6">
                        <div class="input-group mb-3">
                            <input id="search" type="text" class="form-control" name="search" value="${parametros?.search}" placeholder=" número de folio">
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
                        <th>No. Trámite</th>
                        <th>Folio</th>
                        <th>Institución</th>
                        <th>Importe</th>
                        <!-- <th></th> -->
                    </tr>
                </thead>
                <tBody>
                    <g:each status="i" in="${tramites}" var="tramite">
                            <tr>
                                <td class="align-middle">
                                    <a onclick="mostrarProceso();" href="/pagoActa/consultar/${tramite?.id}" data-tooltip="tooltip" data-placement="top" title="Consultar tramite" class="alert-link" >
                                        ${tramite?.numeroTramite}
                                    </a>
                                </td>
                                <td class="align-middle">
                                    ${tramite?.pago?.folio}
                                </td>
                                <td class="align-middle">${tramite?.institucion?.nombre}</td>
                                <td class="align-middle">${tramite?.pago?.importe}</td>
                                <!-- <td class="align-middle" align="end">

                                    <button type="button" class="btn btn-outline-danger btn-sm" data-id="${tramite?.id}" data-toggle="modal" data-target="#modalEliminacion_${tramite?.pago?.id}" data-tooltip="tooltip" data-placement="top" title="Eliminar Pago">
                                        <i class= "fa fa-trash"></i>
                                    </button>

                                    <div class="modal fade" id="modalEliminacion_${tramite?.pago?.id}" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
                                        <div class="modal-dialog modal-dialog-centered" role="document">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <h5 class="modal-title" id="exampleModalLongTitle">Eliminar Trámite</h5>
                                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                                        <span aria-hidden="true">&times;</span>
                                                    </button>
                                                </div>
                                                <div class="modal-body">
                                                    ¿Desea eliminar el trámite seleccionado?
                                                </div>
                                                <div class="modal-footer">
                                                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                                                    <a onclick="mostrarProceso();"  href="/certificacion/eliminar/${tramite?.id}" class="btn btn-primary">Aceptar</a>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </td> -->
                            </tr>
                    </g:each>
                </tBody>
            </table>
        </div>
        <g:render template="/layouts/pagination" model="${[linkUri: "pago/listar", linkParams: [search: params.search?:""], count: conteo, max: params.max, offset: params.offset]}"/>

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