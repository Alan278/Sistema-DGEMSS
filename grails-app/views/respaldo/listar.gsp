<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Lista de Respaldos</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/respaldo/generar" class="btn btn-primary col-md-12 my-2">
                Generar Respaldo
            </a>
            <a onclick="mostrarProceso();" href="/respaldo/cargarSql" class="btn btn-secondary col-md-12 my-2">
                Restaurar respaldo
            </a>
        </content>
        
        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Respaldos
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
            <form action="/respaldo/listar" class="mt-4" id="form-busqueda">
                <div class="row mt-4">
                    <div class="col-md-6">
                    </div>
                    <div class="col-md-6">
                        <div class="input-group mb-3">
                            <input type="text" class="form-control" id="search" name="search" value="${params?.search}" placeholder="Nombre del respaldo">
                            <div class="input-group-prepend">
                                <input id='buscar' type="submit" class="btn btn-primary" value="Buscar"/>
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
                        <th></th>
                    </tr>
                </thead>
                <tBody>
                    <g:each status="i" in="${respaldo}" var="archivo">
                        <tr>
                            <td>${archivo}</td>
                            <td>
                                <p align="right">
                                    <a onclick="mostrarProceso(1000);" href="/respaldo/descargar?nombre=${archivo}" class="btn btn-primary" data-tooltip="tooltip" data-placement="top" title="Descargar Respaldo">
                                        <i class="fa fa-download"></i>
                                    </a>
                                    <button type="button" class="btn btn-danger" data-id="${i}" data-toggle="modal" data-target="#modalEliminacion_${i}" data-tooltip="tooltip" data-placement="top" title="Eliminar Respaldo">
                                        <i class= "fa fa-trash"></i>
                                    </button>
                                </p>
                                <div class="modal fade" id="modalEliminacion_${i}" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
                                    <div class="modal-dialog modal-dialog-centered" role="document">
                                        <div class="modal-content">
                                            <div class="modal-header">
                                                <h5 class="modal-title" id="exampleModalLongTitle">Eliminar Respaldo</h5>
                                                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                                    <span aria-hidden="true">&times;</span>
                                                </button>
                                            </div>
                                            <div class="modal-body">
                                                ¿Desea eliminar el respaldo seleccionado?
                                            </div>
                                            <div class="modal-footer">
                                                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                                                <a onclick="mostrarProceso();" href="/respaldo/eliminar?nombre=${archivo}" class="btn btn-primary">Aceptar</a>
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