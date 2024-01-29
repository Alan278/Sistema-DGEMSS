<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Lista de Instituciones</title>
        <script src="jquery-3.5.1.min.js"></script>
        <style>
            .logo-container{
                height: 80px;
                width: 100%;
                justify-content: center;
            }
            .logo{
                width: auto;
                height: 80px;
            }
        </style>
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
                        Certificados a firmar
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
            <form id='form-busqueda' action="/constanciaservicio/listarInstituciones" class="mt-4">
                <div class="row">
                    <div class="col-md-6">
                        <sec:ifAnyGranted roles='ROLE_AUTENTICADOR_DGEMSS'>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="isPublic" id="privadas" value="false" ${params.isPublic?(params.isPublic=="false"?"checked":""):"checked"}>
                                <label class="form-check-label" for="privadas">
                                    Privadas
                                </label>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="isPublic" id="publicas" value="true" ${params.isPublic?(params.isPublic=="true"?"checked":""):""}>
                                <label class="form-check-label" for="publicas">
                                    Públicas
                                </label>
                            </div>
                        </sec:ifAnyGranted>
                    </div>
                    <div class="col-md-6">
                        <div class="input-group mb-3">
                            <input type="text" class="form-control" name="search" value="${params.search}" placeholder="Nombre de la institución">
                            <div class="input-group-append">
                                <input id="buscar" type="submit" class="btn btn-primary" value="Buscar" />
                            </div>
                            <g:if test="${params.search}">
                                <div class="input-group-append">
                                    <g:link uri="/constanciaservicio/listarInstituciones/" params="${[max: params.max, offset: params.offset]}" class="btn btn-outline-secondary">
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
            <div class="table-responsive">

                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>#</th>
                            <g:if test="${ !params.isPublic || params.isPublic == 'false'}">
                                <th>Logo</th>
                            </g:if>
                            <th>Nombre</th>
                            <th>Número de constancias</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tBody>
                        <g:each status="i" in="${instituciones}" var="institucion">
                            <tr>
                                <td class="align-middle">${i+1}</td>
                                <g:if test="${!params.isPublic || params.isPublic == 'false'}">
                                    <td class="align-middle py-2">
                                        <div class="logo-container">
                                            <img class="logo" id="fotografiaOriginal" src="data:image/png;base64,${institucion.logo}" />
                                        </div>
                                    </td>
                                </g:if>
                                <td class="align-middle">
                                    <a onclick="mostrarProceso();" href="institucion/consultar/${institucion.id}" class="alert-link" data-tooltip="tooltip" data-placement="top" title="Consultar institución">
                                        ${institucion.nombre}
                                    </a>
                                </td>
                                <td class="align-middle" align="end">
                                    <a onclick="mostrarProceso();" href="constanciaservicio/listarConstanciasInstitucion/${institucion.id}" class="alert-link">
                                        Ver constancias
                                    </a>
                                </td>
                            </tr>
                        </g:each>
                    </tBody>
                </table>
            </div>
        </div>

        <g:render template="/layouts/pagination" model="${[linkUri: "constanciaservicio/listarInstituciones", linkParams: [search: params.search?:"", isPublic: params.isPublic?:""], count: conteo, max: params.max, offset: params.offset]}"/>


        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <asset:javascript src="jquery-editable-select.js"/>
        <asset:javascript src="jquery-editable-select.min.js"/>
        <asset:javascript src="filter-buttons.js"/>
        <script>
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