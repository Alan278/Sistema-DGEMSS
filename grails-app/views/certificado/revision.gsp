<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Detalle de Certificados</title>
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
                    Detalles de Certificado
                </h3>
            </div>
        </div>
        
        <g:set var="numeroLetrasService" bean="numeroLetrasService"/>

        <div class="container">
            <div class="row mb-3">
                <div class="col-md-9">
                    <div class="row">
                        <div class="col-md-9">
                            <div class="col-md-11">

                                <h6 class="pb-1">
                                    <b>Institución:</b>
                                    <medium class="text-muted">${certificado?.alumno?.planEstudios?.carrera?.institucion?.nombre} </medium>
                                </h6>
                                <h6 class="pb-1">
                                    <b>Clave CCT:</b>
                                    <medium class="text-muted">${certificado?.alumno?.planEstudios?.carrera?.institucion?.claveCt} </medium>
                                </h6>
                                <h6 class="pb-1">
                                    <b>Carrera:</b>
                                    <medium class="text-muted">${certificado?.alumno?.planEstudios?.carrera?.nombre} </medium>
                                </h6>
                                <h6 class="pb-1">
                                    <b>Plan de estudios:</b>
                                    <medium class="text-muted">${certificado?.alumno?.planEstudios?.nombre} </medium>
                                </h6>
                                <h6 class="pb-1">
                                    <b>RVOE:</b>
                                    <medium class="text-muted">${certificado?.alumno?.planEstudios?.rvoe} -  ${certificado?.alumno?.planEstudios?.fechaRvoeFormato}</medium>
                                </h6>
                                <g:if test="${certificado?.alumno.formacion}">
                                    <h6 class="pb-1">
                                        <b>Formación:</b>
                                        <medium class="text-muted">${certificado?.alumno?.formacion?.nombre} </medium>
                                    </h6>
                                </g:if>
                                <hr>
                                <h6 class="pb-1">
                                    <b>Alumno:</b>
                                    <medium class="text-muted">${certificado?.alumno?.persona?.nombreCompleto} </medium>
                                </h6>
                                <h6 class="pb-1">
                                    <b>Curp:</b>
                                    <medium class="text-muted">${certificado?.alumno?.persona?.curp} </medium>
                                </h6>
                                <h6 class="pb-1">
                                    <b>Matrícula:</b>
                                    <medium class="text-muted">${certificado?.alumno?.matricula} </medium>
                                </h6>
                                <hr>
                                <h6 class="pb-1">
                                    <b>Tipo de certificado:</b>
                                    <medium class="text-muted">${certificado.duplicado ? "DUPLICADO" : "NORMAL"}</medium>
                                </h6>
                                <h6 class="pb-1">
                                    <b>Promedio:</b>
                                    <medium class="text-muted">${promedio}</medium>
                                </h6>
                                <h6 class="pb-1">
                                    <b>Acreditación de la última materia:</b>
                                    <medium class="text-muted">${fechaUltimaAcreditacion}</medium>
                                </h6>
                                <h6 class="pb-1">
                                    <b>Lugar de expedición:</b>
                                    <medium class="text-muted">${certificado?.municipio.toUpperCase()} </medium>
                                </h6>
                                <h6 class="pb-1">
                                    <b>Fecha de expedición:</b>
                                    <medium class="text-muted">${certificado?.firmaAutenticadorDgemss?.fechaFirmaFormato ?: "-"} </medium>
                                </h6>
                                <hr>
                                <h6 class="pb-1">
                                    <b>Libro:</b>
                                    <medium class="text-muted">${certificado?.libro ?: "-"} </medium>
                                </h6>
                                <h6 class="pb-1">
                                    <b>Foja:</b>
                                    <medium class="text-muted">${certificado?.foja ?: "-"}</medium>
                                </h6>
                                <h6 class="pb-1">
                                    <b>Número:</b>
                                    <medium class="text-muted">${certificado?.numero ?: "-"}</medium>
                                </h6>
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

                                <table class="table table-striped table-sm">
                                    <thead>
                                        <tr>
                                            <th class="align-middle" rowspan="2">Clave</th>
                                            <th class="align-middle" rowspan="2">Asignatura</th>
                                            <th class="align-middle" rowspan="2">Ciclo</th>
                                            <th class="align-middle" colspan="2"><center>Calificación</center></th>
                                            <th class="align-middle" rowspan="2"><center>Tipo</center></th>
                                            <th class="align-middle" rowspan="2"><center>Fecha acreditación</center></th>
                                        </tr>
                                        <tr>
                                            <th class="align-middle"><center>Número</center></th>
                                            <th class="align-middle"><center>Letra</center></th>
                                        </tr>
                                    </thead>
                                    <tBody>
                                        <g:each status="i" in="${evaluaciones}" var="evaluacion">
                                            <tr>
                                                <td>${evaluacion?.asignatura?.clave}</td>
                                                <td>${evaluacion?.asignatura?.nombre}</td>
                                                <td>${evaluacion?.cicloEscolar?.nombre}</td>
                                                <td align="center">
                                                    ${evaluacion.calificacion ? (evaluacion.calificacion == 10 ? "10" : evaluacion.calificacion) : (evaluacion.aprobada ? "A" : "")}
                                                </td>
                                                <td align="center">
                                                    ${evaluacion.calificacion ? numeroLetrasService.convertir(String.valueOf(evaluacion.calificacion), true) : (evaluacion.aprobada ? "ACREDITADA" : "")}
                                                </td>
                                                <td align="center">
                                                    <g:if test="${evaluacion?.tipoEvaluacion?.id != 1}">
                                                        ${evaluacion?.tipoEvaluacion?.nombre}
                                                    </g:if>
                                                </td>
                                                <td align="center">
                                                    <g:if test="${evaluacion?.tipoEvaluacion?.id != 1}">
                                                        ${evaluacion?.fechaAcreditacionFormato}
                                                    </g:if>
                                                </td>
                                            </tr>
                                        </g:each>
                                    </tBody>
                                </table>
                            </div>
                        </div>
                    </div>
                    <g:set var="estatusValidos" value="${[5, 7, 8, 9]}"/>
                    <g:if test="${certificado.estatusCertificado?.id in estatusValidos}">
                        <div class="row mt-3" id="firmas">
                            <div class="col-md-12">
                                <div class="card" style="height: 100%;" >
                                    <div class="card-header text-center">
                                        FIRMANTES
                                    </div>
                                    <div class="card-body">
                                        <div class="card-text text-center">
                                            <g:if test="${certificado.firmaDirectorEscuela}">
                                                <h6 class="pb-1">
                                                    <b>AUTORIDAD DE LA INSTITUCIÓN EDUCATIVA:</b>
                                                </h6>
                                                <h6 class="pb-1">
                                                    <medium class="text-muted">${certificado?.firmaDirectorEscuela?.firmaElectronica?.persona?.titulo} ${certificado?.firmaDirectorEscuela?.firmaElectronica?.nombreCer}</medium>
                                                </h6>
                                            </g:if>
                                            <g:if test="${certificado.firmaAutenticadorDgemss}">
                                                <h6 class="pb-1">
                                                    <b>AUTORIDAD ESTATAL:</b>
                                                </h6>
                                                <h6 class="pb-1">
                                                    <medium class="text-muted">${certificado?.firmaAutenticadorDgemss?.firmaElectronica?.persona?.titulo} ${certificado?.firmaAutenticadorDgemss?.firmaElectronica?.nombreCer}</medium>
                                                </h6>
                                            </g:if>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </g:if>
                </div>
                <div class="col-md-3">
                    <div class="sticky-top-b">
                        <form id="formulario" action="/certificado/cambiarEstatusRechazado" class="needs-validation" novalidate>
                            <div class="card sticky-top-b">
                                <div class="card-body">
                                    <h6 class="card-subtitle mb-2 text-muted">Comentarios</h6>
                                    <input id="uuid" name="uuid" type="text" value="${certificado.uuid}" hidden>
                                    <textarea class="form-control" id="comentarios" name="comentarios" rows="5" required>${certificado?.comentarios}</textarea>

                                    <div class="invalid-feedback">
                                        Por favor ingrese un comentario.
                                    </div>
                                    <button id="boton" type="submit" class="btn btn-primary" hidden>Guardar</button>
                                </div>
                            </div>
                            <div class="form-row justify-content-center mt-3">
                                <% urlVolver = ""%>
                                <sec:ifAnyGranted roles='ROLE_REVISOR, ROLE_REVISOR_PUBLICA'>
                                    <a onclick="mostrarProceso();" href="/certificado/registro?uuid=${certificado.uuid}" class="btn btn-primary bck">Aceptar</a>
                                    <% urlVolver = "/certificado/listarCertificadosRevisar" %>
                                </sec:ifAnyGranted>
                                <sec:ifAnyGranted roles='ROLE_DIRECTOR_ESCUELA, ROLE_AUTENTICADOR_DGEMSS'>
                                    <a onclick="mostrarProceso();" href="/certificado/firmar?uuid=${certificado.uuid}" class="btn btn-primary bck">Firmar</a>
                                    <% urlVolver = "/certificado/listarFirmasCertificados" %>
                                </sec:ifAnyGranted>
                                <button type="submit" class="btn btn-secondary bck">Rechazar</button>
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
