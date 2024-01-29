<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Constancias Expedidos</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>

        <content tag="buttons">
            <sec:ifAnyGranted roles='ROLE_GESTOR_ESCUELA'>
                <a href="/constanciaservicio/listarAlumnos" class="btn btn-primary col-md-12 my-2">
                    Nueva constancia
                </a>
            </sec:ifAnyGranted>
            <sec:ifNotGranted roles='ROLE_GESTOR_ESCUELA'>
                <a href="#" class="btn btn-primary col-md-12 my-2 disabled">
                </a>
            </sec:ifNotGranted>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Constancias
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
            <form id="form-busqueda" action="/constanciaservicio/listarConstancias" class="mt-4">
                <label for="estatusConstancia">Estatus</label>
                <div class="input-group mb-2">
                    <g:select  class="form-control" id="estatusConstancia" name="estatusConstancia" from="${estatusConstancia}" optionKey="id" optionValue="nombre" value="${params?.estatusCertificadoId}"/>
                    <input id="estatusConstancia" name="estatusConstanciaId" value="${params.estatusConstanciaId}" hidden>
                    <g:if test="${params.estatusConstanciaId}">
                        <div class="input-group-prepend">
                            <button id="limpiar-estatusConstancia" class="btn btn-outline-secondary">
                                <i class="fa fa-close"></i>
                            </button>
                        </div>
                    </g:if>
                </div>


                <label for="instituciones">Institución</label>
                <div class="input-group mb-2">
                    <g:select  class="form-control" id="instituciones" name="instituciones" from="${instituciones}" optionKey="id" optionValue="nombre" value="${params?.institucionId}"/>
                    <input id="institucionId" name="institucionId" value="${params.institucionId}" hidden>
                    <g:if test="${params.institucionId}">
                        <div class="input-group-prepend">
                            <button id="limpiar-institucion" class="btn btn-outline-secondary">
                                <i class="fa fa-close"></i>
                            </button>
                        </div>
                    </g:if>
                </div>

                <g:if test="${params?.institucionId}">
                    <label for="carreras">Carrera</label>
                    <div class="input-group mb-2">
                        <g:select  class="form-control" id="carreras" name="carreras" from="${carreras}" optionKey="id" optionValue="nombre" value="${params?.carreraId}"/>
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

                <g:if test="${params?.carreraId}">
                    <label for="planesEstudios">Plan de estudios</label>
                    <div class="input-group">
                        <g:select  class="form-control" id="planesEstudios" name="planesEstudios" from="${planesEstudios}" optionKey="id" optionValue="nombre" value="${params?.planEstudiosId}"/>
                        <input id="planEstudiosId" name="planEstudiosId" value="${params.planEstudiosId}" hidden>
                        <g:if test="${params.planEstudiosId}">
                            <div class="input-group-prepend">
                                <button id="limpiar-planEstudios" class="btn btn-outline-secondary">
                                    <i class="fa fa-close"></i>
                                </button>
                            </div>
                        </g:if>
                    </div>
                </g:if>

                <div class="row mt-4">
                    <div class="col-md-6">
                        <sec:ifAnyGranted roles='ROLE_AUTENTICADOR_DGEMSS'>
                        </sec:ifAnyGranted>
                    </div>
                    <div class="col-md-6">
                        <div class="input-group mb-3">
                            <input id="search" type="text" class="form-control" name="search" value="${params?.search}" placeholder="Nombre / CURP / Matrícula del alumno">
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
            <div class="table-responsive">

                <table class="table table-striped ta">
                    <thead>
                        <tr>
                            <th>#</th>
                            <g:if test="${params.isPublic == 'false' || !instituciones[0].publica}">
                                <th>No. Trámite</th>
                            </g:if>
                            <th>Alumno</th>
                            <th>Fecha expedición</th>
                            <sec:ifAnyGranted roles='ROLE_RECEPTOR, ROLE_REVISOR, ROLE_REVISOR_PUBLICA, ROLE_AUTENTICADOR_DGEMSS'>
                                <th>Institución</th>
                            </sec:ifAnyGranted>
                           
                            <th>Estatus</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tBody>
                        <g:each status="i" in="${constancias}" var="constancia">
                                <tr>
                                    <td class="align-middle">${i+1}</td>
                                    <g:if test="${params.isPublic == 'false' || !instituciones[0].publica}">
                                        <td class="align-middle">
                                            ${constancia?.tramite?.numeroTramite}
                                        </td>
                                    </g:if>
                                    <td class="align-middle">
                                        <a onclick="mostrarProceso();" href="/constanciaservicio/consultar?uuid=${constancia.uuid}" class="alert-link" data-tooltip="tooltip" data-placement="top" title="Consultar Constancia">
                                            ${constancia?.alumno?.persona?.nombre} ${constancia?.alumno?.persona?.primerApellido} ${constancia?.alumno?.persona?.segundoApellido}
                                        </a>
                                    </td>
                                    <td class="align-middle">
                                        ${constancia?.firmaAutenticadorDgemss?.fechaFirmaFormato}
                                    </td>
                                    <sec:ifAnyGranted roles='ROLE_RECEPTOR, ROLE_REVISOR, ROLE_REVISOR_PUBLICA, ROLE_AUTENTICADOR_DGEMSS'>
                                        <td class="align-middle">
                                            ${constancia?.alumno?.planEstudios?.carrera?.institucion?.nombre}
                                        </td>
                                    </sec:ifAnyGranted>
                                    <td class="align-middle">
                                        ${constancia?.estatusConstancia?.nombre}
                                    </td>
                                    <td align="end">
                                        <% statusId = constancia?.estatusConstancia?.id %>
                                        <g:if test="${statusId == 9}">
                                            <a onclick="mostrarProceso();" href="/constanciaservicio/listarConstanciasAlumno/${constancia.alumno.id}" class="btn btn-outline-primary btn-sm" data-tooltip="tooltip" data-placement="top" title="Consultar certificado">
                                                <i class="bi bi-eye-fill"></i>
                                            </a>
                                        </g:if>
                                        <sec:ifAnyGranted roles='ROLE_GESTOR_ESCUELA'>
                                            <g:if test="${statusId == 1}">
                                                <a onclick="mostrarProceso();" href="/constanciaservicio/subirFotoModificacion?uuid=${constancia.uuid}" class="btn btn-outline-primary btn-sm" data-tooltip="tooltip" data-placement="top" title="Modificar">
                                                    <i class="fa fa-pencil"></i>
                                                </a>
                                            </g:if>
                                            <g:if test="${statusId == 1 || statusId == 3 || statusId == 6}">
                                                <a onclick="mostrarProceso();" href="/constanciaservicio/consultar?uuid=${constancia.uuid}" class="btn btn-outline-primary btn-sm" data-tooltip="tooltip" data-placement="top" title="Validar">
                                                    <i class="bi bi-check-lg"></i>
                                                </a>
                                            </g:if>
                                            <g:if test="${statusId == 1}">
                                                <button type="button" class="btn btn-outline-danger btn-sm" data-id="${constancia.id}" data-toggle="modal" data-target="#modalEliminacion_${constancia.id}" data-tooltip="tooltip" data-placement="top" title="Eliminar Registro">
                                                    <i class= "fa fa-trash"></i>
                                                </button>
                                                <div class="modal fade" id="modalEliminacion_${constancia.id}" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
                                                    <div class="modal-dialog modal-dialog-centered" role="document">
                                                        <div class="modal-content">
                                                            <div class="modal-header">
                                                                <h5 class="modal-title" id="exampleModalLongTitle">Eliminar constancia</h5>
                                                                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                                                    <span aria-hidden="true">&times;</span>
                                                                </button>
                                                            </div>
                                                            <div class="modal-body">
                                                                ¿Desea eliminar el constancia?
                                                            </div>
                                                            <div class="modal-footer">
                                                                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                                                                <a onclick="mostrarProceso();"  href="/constanciaservicio/eliminar?uuid=${constancia.uuid}" class="btn btn-primary">Aceptar</a>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </g:if>
                                        </sec:ifAnyGranted>
                                    </td>
                                </tr>
                        </g:each>
                    </tBody>
                </table>
            </div>
        </div>

        <g:render template="/layouts/pagination" model="${[linkUri: "constanciaservicio/listarConstancias", linkParams: [search: params.search?:"", institucionId: params.institucionId?:"", estatusCertificadoId: params.estatusCertificadoId?:"", isPublic: params.isPublic?:""], count: conteo, max: params.max, offset: params.offset]}"/>


        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <asset:javascript src="jquery-editable-select.js"/>
        <asset:javascript src="jquery-editable-select.min.js"/>
        <asset:javascript src="filter-buttons.js"/>
        <script>
            $(function () {
                $('[data-toggle="tooltip"]').tooltip()
            })
            function ventanaSecundaria (URL){
                window.open(URL,"ventana1","width=600,height=600, top=30, left=380")
            }
            $(function () {
                $('[data-tooltip="tooltip"]').tooltip()
            })

            $("#privadas").change(function() {
                $("#form-busqueda").submit()
            });

            $("#publicas").change(function() {
                $("#form-busqueda").submit()
            });
        </script>
    </body>
</html>