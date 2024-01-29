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

            .bck{
                width: 100%;
                margin-bottom: 1rem;
            }
        </style>
    </head>
    <body>

        <% statusId = acta?.estatusActa?.id %>

        <!-- Acciones -->
        <content tag="buttons">
            <sec:ifAnyGranted roles='ROLE_GESTOR_ESCUELA'>
                <a href="/actaprofesional/listarAlumnos" class="btn btn-primary col-md-12 my-2">
                    Nueva acta
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
                    Detalles de la acta
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
                                    Datos de la acta
                                </div>
                                <div class="card-body py-2 px-3">
                                    <div class="card-text">
                                        <div class="row ">
                                            <div class="col-md-6">
                                                <p class="my-1"> <b> Institución: </b>${acta?.alumno?.planEstudios?.carrera?.institucion?.nombre} </p>
                                                    <p class="my-1"> <b> Carrera: </b>${acta?.alumno?.planEstudios?.carrera?.nombre}</p>
                                                    <p class="my-1"> <b> Clave CCT: </b> ${acta?.alumno?.planEstudios?.carrera?.institucion?.claveCt}</p>
                                                    <g:if test="${acta?.alumno?.planEstudios?.rvoe}">
                                                        <p class="my-1"> <b> No.RVOE: </b> ${acta?.alumno?.planEstudios?.rvoe}</p>
                                                    </g:if>
                                                    <p class="my-1"> <b> Alumno: </b> ${acta?.alumno?.persona?.nombreCompleto}</p>
                                                    <p class="my-1"> <b> CURP: </b> ${acta?.alumno?.persona?.curp}</p>
                                            </div>
                                            <div class="col-md-6">
                                                <p class="my-1"> <b> Fecha de expedición: </b> ${acta?.firmaAutenticadorDgemss?.fechaFirmaFormato} </p>
                                                <p class="my-1"> <b> Opción de titulación: </b>${acta?.opctitulacion?.nombre}</p>
                                                <p class="my-1"> <b> Titulo del documento </b>${acta?.titulo}</p>
                                                <p class="my-1"> <b> Declaración del jurado: </b>${acta?.declaracion?.nombre}</p>
                                                <p class="my-1"> <b> Tipo de documento: </b>${acta?.doc?.nombre}</p>
                                                <p class="my-1"> <b> Nombre del presidente: </b>${acta?.presidente}</p>
                                                <p class="my-1"> <b> Nombre del vocal: </b>${acta?.vocal}</p>
                                                <p class="my-1"> <b> Nombre del secretario/a: </b>${acta?.secretario}</p>
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
                        <br>
                       <embed src="data:application/pdf;base64,${pdf}"  width="100%" height="600" frameborder="0"/>
                    </div>                   
                    <div class="row mt-3" id="firmas">
                        <div class="col-md-12">
                            <div class="card" style="height: 100%;" >
                                
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
                                            <b>Firmante:</b> ${acta?.firmaDirectorEscuela?.firmaElectronica?.persona?.titulo} ${acta?.firmaDirectorEscuela?.firmaElectronica?.nombreCer} <br>
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
                </div>
                <div class="col-md-3">
                    <div class="sticky-top-b">
                        <g:if test="${statusId != 9}">
                            <div class="card sticky-top-b mb-3">
                                <div class="card-body">
                                    <h6 class="card-subtitle mb-2 text-muted">Comentarios</h6>
                                    <input id="uuid" name="uuid" type="text" value="${acta.uuid}" hidden>
                                    <textarea class="form-control" id="comentarios" name="comentarios" rows="5" disabled>${acta?.comentarios}</textarea>
                                        
                                    <div class="invalid-feedback">
                                        Por favor ingrese un comentario.
                                    </div>
                                </div>
                            </div>
                            <div class="row justify-content-center">
                                <div class="col">
                                    <sec:ifAnyGranted roles='ROLE_GESTOR_ESCUELA'>
                                        <g:if test="${statusId == 1 || statusId == 3 || statusId == 6}">
                                            <a onclick="mostrarProceso();" href="/actaprofesional/cambiarEstatusFirmandoEscuela?uuid=${acta.uuid}" class="btn btn-primary btn-sm bck">Enviar a firmar</a>
                                            <a onclick="mostrarProceso();" href="/actaprofesional/subirFotoModificacion?uuid=${acta.uuid}" class="btn btn-secondary btn-sm bck">Modificar fotografía</a>
                                        </g:if>
                                    </sec:ifAnyGranted>

                                    <sec:ifAnyGranted roles='ROLE_AUTENTICADOR_DGEMSS'>
                                        <g:if test="${statusId == 7}">
                                            <a onclick="mostrarProceso();" href="/actaprofesional/listarFirmasActas" class="btn btn-secondary btn-sm bck">Volver</a>
                                        </g:if>
                                        <g:else>
                                            <a onclick="mostrarProceso();" href="/actaprofesional/listarActas" class="btn btn-secondary btn-sm bck">Volver</a>
                                        </g:else>
                                    </sec:ifAnyGranted>

                                    <sec:ifAnyGranted roles='ROLE_DIRECTOR_ESCUELA'>
                                        <g:if test="${statusId == 2}">
                                            <a onclick="mostrarProceso();" href="/actaprofesional/listarFirmasActas" class="btn btn-secondary btn-sm bck">Volver</a>
                                        </g:if>
                                        <g:else>
                                            <a onclick="mostrarProceso();" href="/actaprofesional/listarActas" class="btn btn-secondary btn-sm bck">Volver</a>
                                        </g:else>
                                    </sec:ifAnyGranted>

                                    <sec:ifAnyGranted roles='ROLE_GESTOR_ESCUELA'>
                                        <a onclick="mostrarProceso();" href="/actaprofesional/listarActas" class="btn btn-secondary btn-sm bck">Volver</a>
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
