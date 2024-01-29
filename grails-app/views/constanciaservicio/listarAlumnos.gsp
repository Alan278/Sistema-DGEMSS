<!doctype html>
<html>
     <head>
        <meta name="layout" content="main"/>
        <title>Generar constancia de servicio social.</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/certificado/listarAlumnos" class="btn btn-primary col-md-12 my-2">
                Nueva constancia
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Nueva constancia - Seleccion de alumno
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
            <div class="alert alert-info" role="alert">
                Seleccione un alumno
            </div>

            <form id="form-busqueda" action="/constanciaservicio/listarAlumnos" class="mt-4">
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

                <g:if test="${params.institucionId}">
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

        <div class="container">
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>Matrícula</th>
                        <th>Nombre</th>
                        <th>CURP</th>
                        <th></th>
                    </tr>
                </thead>
                <tBody>
                    <g:each status="i" in="${alumnos}" var="alumno">
                            <tr>
                                <td>${alumno.matricula}</td>
                                <td>
                                    ${alumno.persona.nombreCompleto}
                                </td>
                                <td>
                                    ${alumno.persona.curp}
                                </td>
                                <td>
                                    <a onclick="mostrarProceso();" href="/constanciaservicio/solicitud/${alumno.id}" class="alert-link">
                                        Seleccionar
                                    </a>
                                </td>
                            </tr>
                    </g:each>
                </tBody>
            </table>
        </div>

        <g:render template="/layouts/pagination" model="${[linkUri: "constanciaservicio/listarAlumnos", linkParams: [search: params.search?:"", institucionId: params.institucionId?:"", carreraId: params.carreraId?:"", cicloEscolarId: params.cicloEscolarId?:""], count: conteo, max: params.max, offset: params.offset]}"/>

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