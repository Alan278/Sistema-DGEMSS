<!doctype html>
<html>
     <head>
        <meta name="layout" content="main"/>
        <title> Listado de Firmas de Certificados</title>
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
                    Listado de certificados a firmar
                </h3>
            </div>
        </div>
        
        <% def tieneFirmasActivas = usuario.persona.firmasElectronicas.any{ firma -> !firma.expiro()}%>

        <div class="container">
            <g:if test="${!tieneFirmasActivas}">
                <div class="alert alert-warning" role="alert">
                    No cuenta con un certificado (.cer) activo. Por favor registre uno para seguir firmando. <a href="/firmaElectronica/registroCer">Registrar</a>
                </div>
            </g:if>

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
            <form id="form-busqueda" action="/certificado/listarFirmasCertificados" class="mt-4">
                
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
            <table class="table table-striped">
                <thead>
                    <tr>
                        <g:if test="${cartificados && !certificados[0]?.alumno?.planEstudios?.carrera?.institucion.publica}">
                            <th>Número tramite</th>
                        </g:if>
                        <th>CURP</th>
                        <th>Alumno</th>
                        <th>Institución</th>
                        <th>Tipo</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tBody>
                    <g:each status="i" in="${certificados}" var="certificado">
                            <tr>
                                <g:if test="${!certificados?.alumno?.planEstudios?.carrera?.institucion.publica}">
                                    <td>${certificado?.tramite?.numeroTramite}</td>
                                </g:if>
                                <td>${certificado?.alumno?.persona?.curp}</td>
                                <td>
                                    <a onclick="mostrarProceso();" href="/certificado/consultar?uuid=${certificado.uuid}" class="alert-link" data-tooltip="tooltip" data-placement="top" title="Consultar certificado">
                                        ${certificado?.alumno?.persona?.nombre} ${certificado?.alumno?.persona?.primerApellido} ${certificado?.alumno?.persona?.segundoApellido}
                                    </a>
                                </td>
                                <td>
                                    ${certificado?.alumno?.planEstudios?.carrera?.institucion?.nombre}
                                </td>
                                <td class="align-middle">
                                    ${certificado.duplicado ? "DUPLICADO" : ""}
                                </td>
                                <td>
                                    <g:if test="${tieneFirmasActivas}">
                                        <a onclick="mostrarProceso();" href="/certificado/revision?uuid=${certificado.uuid}" class="alert-link">
                                            Firmar
                                        </a>
                                    </g:if>
                                </td>
                            </tr>
                    </g:each>
                </tBody>
            </table>
        </div>

        <g:render template="/layouts/pagination" model="${[linkUri: "certificado/listarFirmasCertificados", linkParams: [search: params.search?:"", institucionId: params.institucionId?:""], count: conteo, max: params.max, offset: params.offset]}"/>


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