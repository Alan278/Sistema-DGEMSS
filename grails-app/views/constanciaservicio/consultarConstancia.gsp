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
        </style>
    </head>
    <body>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Detalles de Constancia
                </h3>
            </div>
        </div>

        <div class="container">
            <g:if test="${flash.mensaje}">
                <g:if test="${!flash.estatus}">
                    <div class="alert alert-danger" role="alert">
                        ${flash.mensaje}
                    </div>
                </g:if>
            </g:if>
        </div>
        
        <g:if test="${flash.estatus}">
            <g:set var="numeroLetrasService" bean="numeroLetrasService"/>
            <div class="container">
                <div class="row mb-3">
                    <div class="col-md-12">
                        <div class="row">
                            <div class="col-md-10">
                                <div class="card" style="height: 100%;" >
                                    <div class="card-header">
                                        Datos del constancia
                                    </div>
                                    <div class="card-body py-2 px-3">
                                        <div class="card-text">
                                            <div class="row ">
                                                <div class="col-md-6">
                                                    <p class="my-1"> <b> Institución: </b>${constancia?.alumno?.planEstudios?.carrera?.institucion?.nombre} </p>
                                                    <p class="my-1"> <b> Carrera: </b>${constancia?.alumno?.planEstudios?.carrera?.nombre}</p>
                                                    <p class="my-1"> <b> Clave CCT: </b> ${constancia?.alumno?.planEstudios?.carrera?.institucion?.claveCt}</p>
                                                    <g:if test="${constancia?.alumno?.carrera?.rvoe}">
                                                        <p class="my-1"> <b> No.RVOE: </b> ${constancia?.alumno?.planEstudios?.carrera?.rvoe}</p>
                                                        <p class="my-1"> <b> Fecha RVOE: </b> ${constancia?.alumno?.planEstudios?.carrera?.fechaRvoeFormato}</p>
                                                    </g:if>
                                                    <p class="my-1"> <b> Alumno: </b> ${constancia?.alumno?.persona?.nombreCompleto}</p>
                                                    <p class="my-1"> <b> CURP: </b> ${constancia?.alumno?.persona?.curp}</p>
                                                </div>
                                                <div class="col-md-6">
                                                    <p class="my-1"> <b> Fecha de expedición: </b> ${constancia?.firmaAutenticadorDgemss?.fechaFirmaFormato} </p>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-2">
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
                        <div class="row mt-3">
                            <div class="col">
                                <div class="table-responsive">
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
                                            <div class="mb-3">
                                                <p><b>AUTORIDAD DE LA INSTITUCIÓN EDUCATIVA</b></p>
                                                <b>Firmante:</b> ${constancia?.firmaDirectorEscuela?.firmaElectronica?.persona?.titulo} ${constancia?.firmaDirectorEscuela?.firmaElectronica?.nombreCer} <br>
                                            </div>
                                            <div class="mb-3">
                                                <p><b>AUTORIDAD ESTATAL</b></p>
                                                <b>Firmante:</b> ${constancia?.firmaAutenticadorDgemss?.firmaElectronica?.persona?.titulo} ${constancia?.firmaAutenticadorDgemss?.firmaElectronica?.nombreCer} <br>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </g:if>

        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <asset:javascript src="jquery-editable-select.js"/>
        <asset:javascript src="jquery-editable-select.min.js"/>
        <asset:javascript src="filter-buttons.js"/>
    </body>
</html>
