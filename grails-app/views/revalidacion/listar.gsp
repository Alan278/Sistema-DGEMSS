<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Lista de Revalidaciones</title>
        <script src="jquery-3.5.1.min.js"></script>
    </head>
    <body>
        <div class="row mb-4">
                <div class="col-md-12 pt-2 border-bottom">
                    <h3 class="page-title pl-3">
                        Revalidaciones
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
            <form id='form-busqueda' action="/revalidacion/listar" class="mt-4">
                <div class="row">
                    <div class="col-md-6">
                    </div>
                    <div class="col-md-6">
                         <div class="input-group mb-3">
                            <input type="text" class="form-control" id="search" name="search" value="${params?.search}" placeholder="Expediente">
                            <div class="input-group-prepend">
                                <input id='buscar' type="submit" class="btn btn-primary" value="Buscar"/>
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
                            <th>Fecha de expedición</th>
                            <th>Expediente</th>
                            <th>Nombre del alumno</th>
                            <th>PDF</th>
                        </tr>
                    </thead>
                    <tBody>

                        <g:each status="i" in="${revalidaciones}" var="revalidacion">
                                <tr>
                                    <td class="align-middle">${revalidacion?.fechaExpedicionFormato}</td>
                                    <td class="align-middle"><a onclick="mostrarProceso();" href="/revalidacion/consultar?expediente=${revalidacion.expediente}" class="alert-link" data-toggle="tooltip" data-placement="top" title="Consultar Revalidación">${revalidacion?.expediente}</a></td>
                                    <td class="align-middle">${revalidacion?.alumno}</td>
                                    <td class="align-middle">
                                        <a onclick="mostrarProceso(1000);" href="/revalidacion/descargarPdf?expediente=${revalidacion.expediente}" class="btn btn-outline-danger" data-toggle="tooltip" data-placement="left" title="Descargar pdf">
                                            <i class="bi bi-cloud-arrow-down-fill"></i> 
                                        </a>
                                        <a onclick="mostrarProceso(1000);" href="javascript:ventanaSecundaria('/revalidacion/mostrarPdf?expediente=${revalidacion.expediente}')" class="btn btn-outline-danger" data-toggle="tooltip" data-placement="right" title="Vizualizar pdf">
                                            <i class="bi bi-eye-fill"></i> 
                                        </a>
                                    </td>
                                </tr>
                        </g:each>
                    </tBody>
                </table>
            </div>

            <g:render template="/layouts/pagination" model="${[linkUri: "revalidacion/listar", linkParams: [search: params.search?:""], count: conteo, max: params.max, offset: params.offset]}"/>


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
        </script>
    </body>
</html>