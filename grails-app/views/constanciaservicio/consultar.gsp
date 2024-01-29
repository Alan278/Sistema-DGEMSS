<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Detalle de Constancias</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
        <style>
            #fotografias {
                display: flex;
                justify-content: end;
                align-items: center;
            }

            #contenedorFotografia img{
                width: 100%;
                height: auto;
            }

            textarea{
                overflow:hidden;
                display:block;
                min-height: 60px;
            }

            .bck{
                width: 100%;
                margin-bottom: 1rem;
            }
        </style>
    </head>
    <body>

        <% statusId = constancia?.estatusConstancia?.id %>

        <!-- Acciones -->
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
                    Detalles de la constancia
                </h3>
            </div>
        </div>
        
        <g:set var="numeroLetrasService" bean="numeroLetrasService"/>
        <div class="container mb-5">
            <div class="row mb-3">
                <g:if test="${statusId == 9}">
                    <div class="col-md-12">
                </g:if>
                <g:else>
                    <div class="col-md-9">
                </g:else>
                    <div class="row">
                        <div class="col-md-9">
                            <div class="card" style="height: 100%;" >
                                <div class="card-header">
                                    Datos del certificado
                                </div>
                                <div class="card-body py-2 px-3">
                                    <div class="card-text">
                                        <div class="row ">
                                            <div class="col-md-6">
                                                <p class="my-1"> <b> Institución: </b>${constancia?.alumno?.planEstudios?.carrera?.institucion?.nombre} </p>
                                                    <p class="my-1"> <b> Carrera: </b>${constancia?.alumno?.planEstudios?.carrera?.nombre}</p>
                                                    <p class="my-1"> <b> Clave CCT: </b> ${constancia?.alumno?.planEstudios?.carrera?.institucion?.claveCt}</p>
                                                    <g:if test="${constancia?.alumno?.planEstudios?.rvoe}">
                                                        <p class="my-1"> <b> No.RVOE: </b> ${constancia?.alumno?.planEstudios?.rvoe}</p>
                                                    </g:if>
                                                    <p class="my-1"> <b> Alumno: </b> ${constancia?.alumno?.persona?.nombreCompleto}</p>
                                                    <p class="my-1"> <b> CURP: </b> ${constancia?.alumno?.persona?.curp}</p>
                                            </div>
                                            <div class="col-md-6">
                                                <p class="my-1"> <b> Fecha de expedición: </b> ${constancia?.firmaAutenticadorDgemss?.fechaFirmaFormato ?: "-"} </p>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="card">
                                <div class="row align-items-center" style="height: 100%;">
                                    <div class="col">
                                        <div id="contenedorFotografia">
                                            <img id="fotografiaOriginal" src="data:image/png;base64,${bphoto}" />
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="row mt-3" id="firmas">
                        <div class="col-md-12">
                            <div class="card" style="height: 100%;" >
                                <div class="card-header">
                                    Datos de los firmantes
                                </div>
                                <div class="card-body py-2 px-3">
                                    <div class="card-text">
                                        <h6 class="pb-1">
                                            <b>AUTORIDAD DE LA INSTITUCIÓN EDUCATIVA:</b>
                                            <medium class="text-muted">${constancia?.firmaDirectorEscuela?.firmaElectronica?.persona?.titulo} ${constancia?.firmaDirectorEscuela?.firmaElectronica?.nombreCer} </medium>
                                        </h6>
                                        <!-- <div class="mb-3">
                                            <p><b>AUTORIDAD DE LA INSTITUCIÓN EDUCATIVA</b></p>
                                            <b>Firmante:</b> ${certificado?.firmaDirectorEscuela?.firmaElectronica?.persona?.titulo} ${certificado?.firmaDirectorEscuela?.firmaElectronica?.nombreCer} <br>
                                        </div>
                                        <div class="mb-3">
                                            <p><b>AUTORIDAD ESTATAL</b></p>
                                            <b>Firmante:</b> ${certificado?.firmaAutenticadorDgemss?.firmaElectronica?.persona?.titulo} ${certificado?.firmaAutenticadorDgemss?.firmaElectronica?.nombreCer} <br>
                                        </div> -->
                                        <b>AUTORIDAD ESTATAL:</b>
                                        <medium class="text-muted">${constancia?.firmaAutenticadorDgemss?.firmaElectronica?.persona?.titulo} ${constancia?.firmaAutenticadorDgemss?.firmaElectronica?.nombreCer} </medium>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="sticky-top-b">
                        <g:if test="${statusId != 9}">
                            <div class="row justify-content-center">
                                <div class="col">
                                    <sec:ifAnyGranted roles='ROLE_GESTOR_ESCUELA'>
                                        <g:if test="${statusId == 1 || statusId == 3 || statusId == 6}">
                                            <a onclick="mostrarProceso();" href="/constanciaservicio/cambiarEstatusFirmandoEscuela?uuid=${constancia.uuid}" class="btn btn-primary btn-sm bck">Enviar a firmar</a>
                                            <a onclick="mostrarProceso();" href="/constanciaservicio/subirFotoModificacion?uuid=${constancia.uuid}" class="btn btn-secondary btn-sm bck">Modificar fotografía</a>
                                        </g:if>
                                    </sec:ifAnyGranted>

                                    <sec:ifAnyGranted roles='ROLE_AUTENTICADOR_DGEMSS'>
                                        <g:if test="${statusId == 7}">
                                            <a onclick="mostrarProceso();" href="/constanciaservicio/listarFirmasConstancias" class="btn btn-secondary btn-sm bck">Volver</a>
                                        </g:if>
                                        <g:else>
                                            <a onclick="mostrarProceso();" href="/constanciaservicio/listarConstancias" class="btn btn-secondary btn-sm bck">Volver</a>
                                        </g:else>
                                    </sec:ifAnyGranted>

                                    <sec:ifAnyGranted roles='ROLE_DIRECTOR_ESCUELA'>
                                        <g:if test="${statusId == 2}">
                                            <a onclick="mostrarProceso();" href="/constanciaservicio/listarFirmasConstancias" class="btn btn-secondary btn-sm bck">Volver</a>
                                        </g:if>
                                        <g:else>
                                            <a onclick="mostrarProceso();" href="/constanciaservicio/listarConstancias" class="btn btn-secondary btn-sm bck">Volver</a>
                                        </g:else>
                                    </sec:ifAnyGranted>

                                    <sec:ifAnyGranted roles='ROLE_GESTOR_ESCUELA'>
                                        <a onclick="mostrarProceso();" href="/constanciaservicio/listarConstancias" class="btn btn-secondary btn-sm bck">Volver</a>
                                    </sec:ifAnyGranted>
                                </div>
                            </div>
                        </g:if>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <asset:javascript src="jquery-editable-select.js"/>
        <asset:javascript src="jquery-editable-select.min.js"/>
        <asset:javascript src="filter-buttons.js"/>
    </body>
</html>
