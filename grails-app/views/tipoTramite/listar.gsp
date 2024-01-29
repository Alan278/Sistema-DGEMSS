<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Lista de tipos de trámite</title>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="#" class="btn btn-primary col-md-12 my-2 disabled">
            </a>
        </content>

        <!-- Título -->
        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Tipos de trámite
                </h3>
            </div>
        </div>

        <!-- Mensajes -->
        <div class="container">
            <g:if test="${flash.mensaje}">
                <g:if test="${flash.estatus}">
                    <div class="alert alert-success" role="alert">
                        ${flash.mensaje}
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                </g:if>
                <g:else>
                    <div class="alert alert-danger" role="alert">
                        ${flash.mensaje}
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                </g:else>
            </g:if>
        </div>

        <!-- Contenido -->
        <div class="container mt-2">

            <!-- UMAS -->
            <div class="row justify-content-end mb-4">
                <div class="col-md-2">
                    <div class="card">
                        <div class="card-body text-center ">
                            <div class="mb-2">
                                <h6 class="d-inline text-primary">
                                    <b>UMA</b>
                                </h6>
                                <h5 class="d-inline ">
                                    <b>$${uma?.valor}</b>
                                </h5>
                            </div>
                            <g:link
                                controller="uma"
                                action="modificacion"
                                id="${tipoTramite?.id}"
                                class="card-link"
                                onclick="mostrarProceso()">
                                Editar
                            </g:link>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Lista -->
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>Id</th>
                        <th>Nombre</th>
                        <th>Id concepto</th>
                        <th>Costo UMAS</th>
                        <th>Costo Pesos</th>
                        <th></th>
                    </tr>
                </thead>
                <tBody>
                    <g:render
                        template="listItem"
                        var="tipoTramite"
                        model="[uma: uma]"
                        collection="${tiposTramite}"/>
                </tBody>
            </table>
        </div>

        <asset:javascript src="show-process.js"/>
    </body>
</html>