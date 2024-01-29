<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Certificados Expedidos</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>

        <content tag="buttons">
            <sec:ifAnyGranted roles='ROLE_GESTOR_ESCUELA'>
                <a href="/certificado/listarAlumnos" class="btn btn-primary col-md-12 my-2">
                    Nuevo certificado
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
                    Certificados Expedidos por Alumno
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
        
        <div class="container mb-4">
            <h6 class="pb-1">
                <b>Institución:</b>
                <medium class="text-muted">${alumno?.planEstudios?.carrera?.institucion?.nombre} </medium>
            </h6>
            <h6 class="pb-1">
                <b>Carrera:</b>
                <medium class="text-muted">${alumno?.planEstudios?.carrera?.nombre} </medium>
            </h6>
            <h6 class="pb-1">
                <b>Plan de estudios:</b>
                <medium class="text-muted">${alumno?.planEstudios?.nombre} </medium>
            </h6>
            <h6 class="pb-1">
                <b>Alumno:</b>
                <medium class="text-muted">${alumno?.persona?.nombreCompleto} </medium>
            </h6>
            <h6 class="pb-1">
                <b>Curp:</b>
                <medium class="text-muted">${alumno?.persona?.curp} </medium>
            </h6>
            <h6 class="pb-1">
                <b>Matrícula:</b>
                <medium class="text-muted">${alumno?.matricula} </medium>
            </h6>
        </div>

            <div class="container">
                <div class="table-responsive">

                    <table class="table table-striped ta">
                        <thead>
                            <tr>
                                <th>#</th>
                                <g:if test="${!alumno?.planEstudios?.carrera?.institucion?.publica}">
                                    <th>No. trámite</th>
                                </g:if>
                                <th>Fecha de expedición</th>
                                <th>Tipo</th>
                                <th>Acciones</th>
                                <th>PDF</th>
                                <th>XML</th>
                            </tr>
                        </thead>
                        <tBody>
                            <g:each status="i" in="${certificados}" var="certificado">
                                    <tr>
                                        <td class="align-middle">${i+1}</td>
                                        <g:if test="${!alumno?.planEstudios?.carrera?.institucion?.publica}">
                                            <td class="align-middle">
                                                ${certificado?.tramite?.numeroTramite}
                                            </td>
                                        </g:if>
                                        <td class="align-middle">${certificado?.firmaAutenticadorDgemss?.fechaFirmaFormato}</td>
                                        <td class="align-middle">
                                            ${certificado.duplicado ? "DUPLICADO" : ""}
                                        </td>
                                        <td class="align-middle">
                                            <div class="row align-items-center justify-content-between px-3">
                                                <a onclick="mostrarProceso();" href="/certificado/consultar?uuid=${certificado.uuid}" data-toggle="tooltip" data-placement="top" title="Consultar certificado" class="alert-link">Consultar</a>
                                            </div>
                                        </td>
                                        <td>
                                            <a onclick="mostrarProceso(1000);" href="/certificado/descargarPdf?uuid=${certificado.uuid}&personaId=${persona?.id}" class="btn btn-outline-danger btn-sm" data-toggle="tooltip" data-placement="left" title="Descargar pdf">
                                                <i class="bi bi-cloud-arrow-down-fill"></i> 
                                            </a>
                                            <a onclick="mostrarProceso(1000);" href="javascript:ventanaSecundaria('/certificado/mostrarPdf?uuid=${certificado.uuid}')" class="btn btn-outline-danger btn-sm" data-toggle="tooltip" data-placement="right" title="Vizualizar pdf">
                                                <i class="bi bi-eye-fill"></i> 
                                            </a>
                                        </td>
                                        <td>
                                            <a onclick="mostrarProceso(1000);" href="/certificado/descargarXml?uuid=${certificado.uuid}&personaId=${persona?.id}" class="btn btn-outline-success btn-sm" data-toggle="tooltip" data-placement="left" title="Descargar xml">
                                                <i class="bi bi-cloud-arrow-down-fill"></i> 
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
            </script>
    </body>
</html>