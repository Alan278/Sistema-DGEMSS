<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Lista de Instituciones</title>
        <script src="jquery-3.5.1.min.js"></script>
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
                        Firma de certificados - Instituciones privadas
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
            <form id='form-busqueda' action="/institucion/listar" class="mt-4">
                <div class="row">
                    <div class="col-md-6">
                    </div>
                    <div class="col-md-6">
                        <div class="input-group mb-3">
                            <input type="text" class="form-control" name="search" value="${params.search}" placeholder="Nombre de la institución">
                            <div class="input-group-append">
                                <input id="buscar" type="submit" class="btn btn-primary" value="Buscar" />
                            </div>
                            <g:if test="${params.search}">
                                <div class="input-group-append">
                                    <g:link uri="/institucion/modificacion/" params="${[max: params.max, offset: params.offset]}" class="btn btn-outline-secondary">
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
                            <th>#</th>
                            <th>Nombre</th>
                            <th>Número de certificados</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tBody>
                        <g:each status="i" in="${instituciones}" var="institucion">
                            <tr>
                                <td class="align-middle">${i+1}</td>
                                <td class="align-middle">
                                    <a onclick="mostrarProceso();" href="institucion/consultar/${institucion.id}" class="alert-link" data-tooltip="tooltip" data-placement="top" title="Consultar institución">
                                        ${institucion.nombre}
                                    </a>
                                </td>
                                <td>${institucion.numCertificados}</td>
                                <td align="end">
                                    <a onclick="mostrarProceso();" href="certificado/listarCertificadosInstitucion/${institucion.id}" class="alert-link">
                                        Ver certificados
                                    </a>
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