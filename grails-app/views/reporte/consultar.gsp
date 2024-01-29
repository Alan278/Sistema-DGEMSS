<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Detalles del Reporte</title>
        <script src="jquery-3.5.1.min.js"></script>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/reporte/registro" class="btn btn-primary col-md-12 my-2">
                Nuevo Reporte
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Detalles del Reporte
                </h3>
            </div>
        </div>

        <div class="container">
            <div class="form-group mb-4" >
                <h5> <b> Nombre </b> </h5>
                <h6 class="ml-3">${reporte?.nombre} </h6>
            </div>

            <div class="form-group mb-4" >
                <h5> <b> Sentencia SQL </b> </h5>
                <h6 class="ml-3">${reporte?.consultaSql} </h6>
            </div>

            <h5> <b> Resultado del reporte </b> </h5>
            <g:if test="${consulta}">
                <div style="overflow-y: hidden; overflow-x: scroll">
                    <table class="table table-striped table-hover table-bordered">
                        <thead>
                        <tr>
                            <g:each in="${consulta?.get(0)?.keySet()}" var="nombreColumna">
                                <th>${nombreColumna}</th>
                            </g:each>
                        </tr>
                        </thead>
                        <tbody>
                        <g:each in="${consulta}" var="valoresFila">
                            <tr>
                                <g:each in="${valoresFila.entrySet()}" var="valorColumna">
                                    <td>
                                        ${valorColumna.value}
                                    </td>
                                </g:each>
                            </tr>
                        </g:each>
                        </tbody>
                    </table>
                </div>
            </g:if>
            <g:else>
                <div class="alert alert-info">
                    No se encontraron registros
                </div>
            </g:else>

            <div class="form-row justify-content-end mt-5">
                <p>
                    <a onclick="mostrarProceso();" href="reporte/modificacion/${reporte?.id}" class="btn btn-primary">
                        Modificar
                    </a>
                    <button type="button" class="btn btn-danger" data-toggle="modal" data-target="#modalEliminacion">
                        Eliminar
                    </button>
                </p>
            </div>
        </div>

        <div class="modal fade" id="modalEliminacion" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="exampleModalLongTitle">Eliminar Reporte</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        ¿Desea eliminar el reporte?
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                        <a onclick="mostrarProceso();" href="reporte/eliminar/${reporte?.id}" class="btn btn-primary">Aceptar</a>
                    </div>
                </div>
            </div>
        </div>
        <asset:javascript src="filter-buttons.js"/>

    </body>
</html>