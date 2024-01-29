<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Detalle de Actas</title>
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
                    Detalles del Acta
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
                                        Datos del acta
                                    </div>
                                    <div class="card-body py-2 px-3">
                                        <div class="card-text">
                                            <div class="row ">
                                                <div class="col-md-6">
                                                    <p class="my-1"> <b> Institución: </b>${acta?.alumno?.planEstudios?.carrera?.institucion?.nombre} </p>
                                                    <p class="my-1"> <b> Carrera: </b>${acta?.alumno?.planEstudios?.carrera?.nombre}</p>
                                                    <p class="my-1"> <b> Clave CCT: </b> ${acta?.alumno?.planEstudios?.carrera?.institucion?.claveCt}</p>
                                                    <g:if test="${acta?.alumno?.carrera?.rvoe}">
                                                        <p class="my-1"> <b> No.RVOE: </b> ${acta?.alumno?.planEstudios?.carrera?.rvoe}</p>
                                                        <p class="my-1"> <b> Fecha RVOE: </b> ${acta?.alumno?.planEstudios?.carrera?.fechaRvoeFormato}</p>
                                                    </g:if>
                                                    <p class="my-1"> <b> Alumno: </b> ${acta?.alumno?.persona?.nombreCompleto}</p>
                                                    <p class="my-1"> <b> CURP: </b> ${acta?.alumno?.persona?.curp}</p>
                                                </div>
                                                <div class="col-md-6">
                                                    <p class="my-1"> <b> Fecha de expedición: </b> ${acta?.firmaAutenticadorDgemss?.fechaFirmaFormato} </p>
                                                    <p class="my-1"> <b> Lugar de expedición: </b> ${acta?.municipio.toUpperCase()} </p>
                                                    <p class="my-1"> <b> Libro: </b> ${acta?.libro} </p>
                                                    <p class="my-1"> <b> Foja: </b> ${acta?.foja}</p>
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
    
                                    <table class="table table-striped table-sm">
                                        <thead>
                                            <tr>
                                                <th>Clave</th>
                                                <th>Asignatura</th>
                                                <th>Ciclo</th>
                                                <th>Calificación</th>
                                                <th>Observaciones</th>
                                            </tr>
                                        </thead>
                                    </table>
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
                                                <b>Director:</b> ${acta?.firmaDirectorEscuela?.firmaElectronica?.persona?.titulo} ${acta?.firmaDirectorEscuela?.firmaElectronica?.nombreCer} <br>
                                                <b>Vocal:</b> ${acta?.firmaVocalEscuela?.firmaElectronica?.persona?.titulo} ${acta?.firmaVocalEscuela?.firmaElectronica?.nombreCer} <br>
                                                <b>Secretario:</b> ${acta?.firmaSecretarioEscuela?.firmaElectronica?.persona?.titulo} ${acta?.firmaSecretarioEscuela?.firmaElectronica?.nombreCer} <br>
                                                <b>Presidente:</b> ${acta?.firmaPresidenteEscuela?.firmaElectronica?.persona?.titulo} ${acta?.firmaPresidenteEscuela?.firmaElectronica?.nombreCer} <br>
                                            </div>
                                            <div class="mb-3">
                                                <p><b>AUTORIDAD ESTATAL</b></p>
                                                <b>Firmante:</b> ${acta?.firmaAutenticadorDgemss?.firmaElectronica?.persona?.titulo} ${acta?.firmaAutenticadorDgemss?.firmaElectronica?.nombreCer} <br>
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
