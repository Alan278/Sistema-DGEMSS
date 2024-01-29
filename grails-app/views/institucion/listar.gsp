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
            <a href="/institucion/registro" class="btn btn-primary col-md-12 my-2">
                Nueva institución
            </a>
            <a href="/institucion/subirExcel" class="btn btn-primary col-md-12 my-2">
                Cargar instituciones
            </a>
        </content>

        <div class="row mb-4">
                <div class="col-md-12 pt-2 border-bottom">
                    <h3 class="page-title pl-3">
                        Instituciones
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
                <label for="nivel">Nivel</label>
                <div class="input-group mb-4">
                    <g:select id="nivelId" name='nivelId' value="${params.nivelId}"
                    noSelection="${['':'Todas las instituciones']}"
                    from='${niveles}'
                    class="form-control"
                    optionKey="id" optionValue="nombre"></g:select>
                </div>

                <div class="row">
                    <div class="col-md-6">
                    </div>
                    <div class="col-md-6">
                        <div class="input-group mb-3">
                            <input type="text" class="form-control" name="search" value="${params.search}" placeholder="Nombre / Clave CCT">
                            <div class="input-group-append">
                                <input id="buscar" type="submit" class="btn btn-primary" value="Buscar" />
                            </div>
                            <g:if test="${params.search}">
                                <div class="input-group-append">
                                    <g:link uri="/institucion/listar/" params="${[max: params.max, offset: params.offset, nivelId: params.nivelId]}" class="btn btn-outline-secondary">
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
                            <th>Clave CCT</th>
                            <sec:ifNotGranted roles='ROLE_SUPERVISOR_MEDIA_PUBLICA, ROLE_SUPERVISOR_TECNICA_PUBLICA'>
                                <th>Clave DGP</th>
                            </sec:ifNotGranted>
                            <th>Carreras</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tBody>
                        <g:each status="i" in="${instituciones}" var="elemento">
                            <tr>
                                <td class="align-middle">${i+1}</td>
                                <td class="align-middle">
                                    <a onclick="mostrarProceso();" href="/institucion/consultar/${elemento.id}?numeroCarreras=${numeroCarreras[i]}" class="alert-link" data-tooltip="tooltip" data-placement="top" title="Consultar institución">
                                        ${elemento.nombre}
                                    </a>
                                </td>
                                <sec:ifNotGranted roles='ROLE_SUPERVISOR_MEDIA_PUBLICA, ROLE_SUPERVISOR_TECNICA_PUBLICA'>
                                    <td class="align-middle">${elemento.claveDgp}</td>
                                </sec:ifNotGranted>
                                <td class="align-middle">${elemento.claveCt}</td>
                                <td class="align-middle" >
                                    <a onclick="mostrarProceso();" href="/carrera/listar?institucionId=${elemento.id}" class="alert-link" data-tooltip="tooltip" data-placement="top" title="Consultar carreras">
                                        ${elemento.numCarreras}
                                    <a>
                                </td>
                                <g:set var="nivelId" value="${params.nivelId ? params.nivelId.toInteger() : 0}"></g:set>
                                <g:if test="${(nivelId in nivelesUsuario) && elemento.numCertificados == 0}">
                                    <td class="align-middle" align="end">
                                            <a onclick="mostrarProceso();" href="/institucion/modificacion/${elemento.id}" class="btn btn-outline-primary btn-sm" data-tooltip="tooltip" data-placement="top" title="Editar registro">
                                                <i class="fa fa-pencil"></i>
                                            </a>
                                            <a type="button" class="btn btn-outline-danger btn-sm" data-id="${elemento.id}" data-toggle="modal" data-target="#modalEliminacion_${elemento.id}" data-tooltip="tooltip" data-placement="top" title="Eliminar registro">
                                            <i class= "fa fa-trash"></i>
                                            </a>
                                        <div class="modal fade" id="modalEliminacion_${elemento.id}" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
                                            <div class="modal-dialog modal-dialog-centered" role="document">
                                                <div class="modal-content">
                                                    <div class="modal-header">
                                                        <h5 class="modal-title" id="exampleModalLongTitle">Eliminar Institución</h5>
                                                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                                            <span aria-hidden="true">&times;</span>
                                                        </button>
                                                    </div>
                                                    <div class="modal-body">
                                                        ¿Desea eliminar la institución?
                                                    </div>
                                                    <div class="modal-footer">
                                                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                                                        <a onclick="mostrarProceso();" href="/institucion/eliminar/${elemento.id}" class="btn btn-primary">Aceptar</a>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                </g:if>
                                <g:else>
                                    <td></td>
                                </g:else>
                            </tr>
                        </g:each>
                    </tBody>
                </table>
            </div>
        <g:render template="/layouts/pagination" model="${[linkUri: "institucion/listar", linkParams: [search: params.search?:""], count: conteo, max: params.max, offset: params.offset]}"/>

        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <asset:javascript src="jquery-editable-select.js"/>
        <asset:javascript src="jquery-editable-select.min.js"/>
        <!-- <asset:javascript src="filter-buttons.js"/> -->
        <script>
            $( "#nivelId" ).change(function() {
                $("#form-busqueda").submit()
            });
            $(function () {
                $('[data-tooltip="tooltip"]').tooltip()
            })
        </script>
    </body>
</html>