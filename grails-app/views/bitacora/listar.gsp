<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Bitácora</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="#" class="btn btn-primary col-md-12 my-2 disabled">
            </a>
        </content>
        
        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Bitácora
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
            <form action="/bitacora/listar">
                <div class="row">
                    <div class="col-md-6">
                    </div>
                    <div class="col-md-6">
                        <div class="input-group mb-3">
                            <input id="search" type="text" class="form-control" name="search" value="${params?.search}" placeholder="Nombre de la acción">
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
                        <th>Fecha</th>
                        <th>Acción</th>
                        <th>Usuario</th>
                        <th>Estatus</th>
                    </tr>
                </thead>
                <tBody>
                    <g:each in="${bitacora}" var="registro">
                        <tr>
                            <td>${registro?.fechaRegistro}</a></td>
                            <td><a onclick="mostrarProceso();" href="/bitacora/consultar/${registro?.id}" class="alert-link" data-tooltip="tooltip" data-placement="top" title="Consultar Acción">${registro?.nombre}</a></td>
                            <td>${registro?.usuario?.username}</a></td>
                            <td>${registro?.estatus}</a></td>
                        </tr>
                    </g:each>
                </tBody>
            </table>
        </div>

        <div class="container">
            <g:render template="/layouts/pagination" model="${[linkUri: "bitacora/listar", linkParams: [search: params.search?:""], count: conteo, max: params.max, offset: params.offset]}"/>
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