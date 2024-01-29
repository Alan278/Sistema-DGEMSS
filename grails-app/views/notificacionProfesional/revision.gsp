<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Detalle de Constanciass</title>
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

        <!-- Acciones -->
        <content tag="buttons">
            <a href="#" class="btn btn-primary col-md-12 my-2 disabled">
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Detalles de la Notificación
                </h3>
            </div>
        </div>
        
        <g:set var="numeroLetrasService" bean="numeroLetrasService"/>

        <div class="container">
            <div class="row mb-3">
                <div class="col-md-9">
                    <div class="row">
                        <div class="col-md-9">
                            <div class="card" style="height: 100%;" >
                                <div class="card-header">
                                    Datos de la constancia
                                </div>
                                <div class="card-body py-2 px-3">
                                    <div class="card-text">
                                        <div class="row ">
                                            <div class="col-md-6">
                                                <p class="my-1"> <b> Institución: </b>${notificacion?.alumno?.carrera?.institucion?.nombre} </p>
                                                <p class="my-1"> <b> Carrera: </b>${notificacion?.alumno?.carrera?.nombre}</p>
                                                <p class="my-1"> <b> Clave CCT: </b> ${notificacion?.alumno?.carrera?.institucion?.claveCt}</p>
                                                <p class="my-1"> <b> No.RVOE: </b> ${notificacion?.alumno?.carrera?.rvoe}</p>
                                                <p class="my-1"> <b> Fecha RVOE: </b> ${notificacion?.alumno?.carrera?.fechaRvoeFormato}</p>
                                            </div>
                                            <div class="col-md-6">
                                                <p class="my-1"> <b> CURP: </b> ${notificacion?.alumno?.persona?.curp}</p>
                                                <p class="my-1"> <b> Alumno: </b> ${notificacion?.alumno?.persona?.nombre} ${notificacion?.alumno?.persona?.primerApellido} ${notificacion?.alumno?.persona?.segundoApellido}</p>
                                                <p class="my-1"> <b> Opción de titulación: </b>${notificacion?.opctitulacion?.nombre}</p>
                                                <p class="my-1"> <b> Titulo del documento </b>${notificacion?.titulo}</p>
                                                <p class="my-1"> <b> Nombre del presidente: </b>${notificacion?.presidente}</p>
                                                <p class="my-1"> <b> Nombre del vocal: </b>${notificacion?.vocal}</p>
                                                <p class="my-1"> <b> Nombre del secretario/a: </b>${notificacion?.secretario}</p>
                                                <p class="my-1"> <b> Fecha expedición: </b> ${notificacion?.firmaAutenticadorDgemss?.fechaFirmaFormato} </p>
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
                                            <b>Firmante:</b> ${notificacion?.firmaDirectorEscuela?.firmaElectronica?.persona?.titulo} ${notificacion?.firmaDirectorEscuela?.firmaElectronica?.nombreCer} <br>
                                        </div>
                                        <div class="mb-3">
                                            <p><b>AUTORIDAD ESTATAL</b></p>
                                            <b>Firmante:</b> ${notificacion?.firmaAutenticadorDgemss?.firmaElectronica?.persona?.titulo} ${notificacion?.firmaAutenticadorDgemss?.firmaElectronica?.nombreCer} <br>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="sticky-top-b">
                        <form id="formulario" action="/NotificacionProfesional/cambiarEstatusRechazado" class="needs-validation" novalidate>
                            <div class="card sticky-top-b">
                                <div class="card-body">
                                    <h6 class="card-subtitle mb-2 text-muted">Comentarios</h6>
                                    <input id="uuid" name="uuid" type="text" value="${notificacion.uuid}" hidden>
                                    <textarea class="form-control" id="comentarios" name="comentarios" rows="5" required>${notificacion?.comentarios}</textarea>

                                    <div class="invalid-feedback">
                                        Por favor ingrese un comentario.
                                    </div>
                                    <button id="boton" type="submit" class="btn btn-primary" hidden>Guardar</button>
                                </div>
                            </div>
                            <div class="form-row justify-content-center mt-3">
                                <% urlVolver = ""%>
                                <sec:ifAnyGranted roles='ROLE_REVISOR, ROLE_REVISOR_PUBLICA'>
                                    <a onclick="mostrarProceso();" href="/NotificacionProfesional/registro?uuid=${notificacion.uuid}" class="btn btn-primary bck">Aceptar</a>
                                    <% urlVolver = "/NotificacionProfesional/listarNotificacionesRevisar" %>
                                </sec:ifAnyGranted>
                                <sec:ifAnyGranted roles='ROLE_DIRECTOR_ESCUELA, ROLE_AUTENTICADOR_DGEMSS'>
                                    <a onclick="mostrarProceso();" href="/NotificacionProfesional/firmar?uuid=${notificacion.uuid}" class="btn btn-primary bck">Firmar</a>
                                    <% urlVolver = "/NotificacionProfesional/listarFirmasNotificaciones" %>
                                </sec:ifAnyGranted>
                                <button type="submit" class="btn btn-danger bck" onclick="return confirm('¿Está seguro que desea realizar esta acción?')">Rechazar</button>
                                <a onclick="mostrarProceso();" href="${urlVolver}" class="btn btn-secondary bck">Volver</a>
                            </div>
                        </form>
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
