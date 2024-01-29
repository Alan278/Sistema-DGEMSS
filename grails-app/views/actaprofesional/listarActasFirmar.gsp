<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Actas a revisar</title>
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
                    Actas a revisar
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
            <form id="form-busqueda" action="/actaprofesional/listarActasFirmar" class="mt-4">

                <label for="instituciones">Institución</label>
                <div class="input-group mb-3">
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

                <div class="row mt-4">
                    <div class="col-md-6">
                    </div>
                    <div class="col-md-6">
                        <div class="input-group mb-3">
                            <input id="search" type="text" class="form-control" name="search" value="${params?.search}" placeholder="Nombre del alumno">
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
        
        <br>
            <div class="container mb-4">
                <div class="table-responsive">

                    <table class="table table-striped ta">
                        <thead>
                            <tr>
                                <th>No. trámite</th>
                                <th>Matrícula</th>
                                <th>Nombre</th>
                                <th>Fecha expedición</th>
                                <th>Estatus</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tBody>
                            <g:each status="i" in="${actas}" var="acta">
                                    <tr>
                                        <td class="align-middle">
                                            <a onclick="mostrarProceso();" href="/actaprofesional/consultar?uuid=${acta.uuid}" class="alert-link" data-tooltip="tooltip" data-placement="top" title="Consultar Constancia">
                                                ${notificacion?.tramite?.numeroTramite}
                                            </a>
                                        </td>
                                        <td class="align-middle">${acta?.alumno?.matricula}</td>
                                        <td class="align-middle">${acta?.alumno?.persona.nombre} ${acta?.alumno?.persona.primerApellido} ${acta?.alumno?.persona.segundoApellido}</td>
                                        <td class="align-middle">
                                            ${acta?.firmaAutenticadorDgemss?.fechaFirma}
                                        </td>
                                        <td class="align-middle">
                                            ${acta?.estatusActa?.nombre}
                                        </td>
                                        <td class="text-right">
                                            <a onclick="mostrarProceso();" href="/actaprofesional/revision?uuid=${acta.uuid}" class="btn btn-outline-primary btn-sm" data-tooltip="tooltip" data-placement="top" title="Validar">
                                                <i class="bi bi-check-lg"></i>
                                            </a>
                                        </td>
                                    </tr>
                            </g:each>
                        </tBody>
                    </table>
                </div>
            </div>


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
            </script>
    </body>
</html>