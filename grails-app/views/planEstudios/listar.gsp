<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Lista de Planes de estudios</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/planEstudios/registro" class="btn btn-primary col-md-12 my-2">
                Nuevo plan de estudios
            </a>
            <a href="/planEstudios/subirExcel" class="btn btn-primary col-md-12 my-2">
                Cargar planes
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Planes de estudios
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
            <form action="/planEstudios/listar" class="mt-4" id="form-busqueda">
                <label for="instituciones">Institución</label>
                <div class="input-group mb-2">
                    <g:select  class="form-control" id="instituciones" name="instituciones" from="${instituciones}" optionKey="id" optionValue='${{"CCT: ${it.claveCt ?: ""} :::: NOMBRE: ${it.nombre}"}}' value="${parametros?.institucionId}"/>
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
                        <th>#</th>
                        <th>Nombre</th>
                        <th>Carrera</th>
                        <th>Institución</th>
                        <sec:ifNotGranted roles='ROLE_SUPERVISOR_MEDIA_PUBLICA, ROLE_SUPERVISOR_TECNICA_PUBLICA'>
                            <th>No. RVOE</th>
                        </sec:ifNotGranted>
                        <th>Asignaturas</th>
                        <th>Formaciones</th>
                        <th></th>
                    </tr>
                </thead>
                <tBody>
                    <g:each status="i" in="${planesEstudios}" var="planEstudios">
                            <tr>
                                <td class="align-middle">${i+1}</td>
                                <td class="align-middle">
                                    <a onclick="mostrarProceso();" href="/planEstudios/consultar/${planEstudios.id}" data-tooltip="tooltip" data-placement="top" title="Consultar Plan" class="alert-link" >
                                        ${planEstudios.nombre}
                                    </a>
                                </td>
                                <td class="align-middle">
                                    ${planEstudios?.carrera?.nombre}
                                </td>
                                <td class="align-middle">${planEstudios?.carrera?.institucion?.nombre}</td>
                                <sec:ifNotGranted roles='ROLE_SUPERVISOR_MEDIA_PUBLICA, ROLE_SUPERVISOR_TECNICA_PUBLICA'>
                                    <td class="align-middle">${planEstudios?.rvoe}</td>
                                </sec:ifNotGranted>
                                <td class="align-middle text-center">
                                    <a onclick="mostrarProceso();" href="/asignatura/listar?institucionId=${planEstudios.carrera.institucion.id}&carreraId=${planEstudios.carrera.id}&planEstudiosId=${planEstudios.id}" data-tooltip="tooltip" data-placement="top" title="Consultar asignaturas" class="alert-link" >
                                        ${planEstudios.numAsignaturas}
                                    </a>
                                </td>
                                <td class="align-middle text-center">
                                    <a onclick="mostrarProceso();" href="/formacion/listar?planEstudiosId=${planEstudios?.id}" class="alert-link">
                                        ${planEstudios.numFormaciones}
                                    </a>
                                </td>
                                <g:if test="${!planEstudios.numCertificados}">

                                    <td class="align-middle" align="end">
    
                                        <a onclick="mostrarProceso();" href="/planEstudios/modificacion/${planEstudios.id}" class="btn btn-outline-primary btn-sm" data-tooltip="tooltip" data-placement="top" title="Editar registro">
                                            <i class="fa fa-pencil"></i>
                                        </a>
                                        <button type="button" class="btn btn-outline-danger btn-sm" data-id="${planEstudios.id}" data-toggle="modal" data-target="#modalEliminacion_${planEstudios.id}" data-tooltip="tooltip" data-placement="top" title="Eliminar registro">
                                            <i class= "fa fa-trash"></i>
                                        </button>
    
                                        <div class="modal fade" id="modalEliminacion_${planEstudios.id}" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
                                            <div class="modal-dialog modal-dialog-centered" role="document">
                                                <div class="modal-content">
                                                    <div class="modal-header">
                                                        <h5 class="modal-title" id="exampleModalLongTitle">Eliminar Plan de estudios</h5>
                                                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                                            <span aria-hidden="true">&times;</span>
                                                        </button>
                                                    </div>
                                                    <div class="modal-body">
                                                        ¿Desea eliminar el plan de estudios seleccionado?
                                                    </div>
                                                    <div class="modal-footer">
                                                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                                                        <a onclick="mostrarProceso();" href="/planEstudios/eliminar/${planEstudios.id}" class="btn btn-primary">Aceptar</a>
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

        <g:render template="/layouts/pagination" model="${[linkUri: "planEstudios/listar", linkParams: [search: params.search?:"", institucionId: params.institucionId?:"", carreraId: params.carreraId?:""], count: conteo, max: params.max, offset: params.offset]}"/>

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