<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Lista de Calificaciones</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/evaluacion/registro/${alumno?.id}" class="btn btn-primary col-md-12 my-2">
                Nueva evaluación
            </a>
            <a href="/alumno/agregarCicloEscolar/${alumno?.id}" class="btn btn-primary col-md-12 my-2">
                Nuevo ciclo escolar
            </a>
            <a href="/evaluacion/subirExcel/${alumno?.id}" class="btn btn-primary col-md-12 my-2">
                Cargar evaluaciones
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Detalles del alumno
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
            <div class="jumbotron p-3">
                <h6 class="m-0 pb-1" >
                    <b>Alumno:</b>
                    <medium class="text-muted">${alumno?.persona?.nombreCompleto}</medium><br>
                </h6>
                <h6 class="m-0">
                    <b>Matrícula:</b>
                    <medium class="text-muted">${alumno?.matricula} </medium>
                </h6>
            </div>
            <ul class="nav nav-tabs mb-4">
                <li class="nav-item">
                    <a class="nav-link" href="/alumno/consultar/${params?.id}">Detalles</a>
                  </li>
                <li class="nav-item">
                  <a class="nav-link active" href="/evaluacion/listar/${params?.id}">Evaluaciones (${alumno?.numEvaluaciones})</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" href="/alumno/listarCiclosEscolares/${params?.id}">Ciclos Escolares (${alumno?.numCiclosEscolares})</a>
                </li>
            </ul>
        </div>

        <div class="container">
            <form id="form-busqueda" action="/evaluacion/listar/${alumno?.id}">
                <div class="row">
                    <div class="col-md-6">
                        <h6 class="pt-2 pl-1">
                            <b>Promedio:</b>
                            <medium class="text-muted">${promedio} </medium>
                        </h6>
                    </div>
                    <div class="col-md-6">
                        <div class="input-group mb-3">
                            <input id="search" type="text" class="form-control" name="search" value="${params?.search}" placeholder="Nombre de la asignatura / Periodo">
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
                            <th class="text-center">Clave</th>
                            <th>Asignatura</th>
                            <th class="text-center">Calificación</th>
                            <th>Tipo</th>
                            <th>Fecha acreditación</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tBody id="tb">
                        <g:set var="periodo" value="0"></g:set>
                        <g:each status="i" in="${evaluaciones}" var="evaluacion">
                            <g:set var="periodoI" value="${evaluacion?.cicloEscolar?.periodo}"></g:set>
                            <g:if test="${periodoI != periodo}">
                                <g:set var="periodo" value="${periodoI}"></g:set>
                                <tr class="table-dark">
                                    <td colspan="6" class="align-middle" align="center">
                                        <b>CICLO ESCOLAR:</b> ${evaluacion.cicloEscolar?.nombre}
                                    </td>
                                </tr>
                            </g:if>
                            <tr>
                                <td class="align-middle text-center">${evaluacion?.asignatura?.clave}</td>
                                <td class="align-middle">${evaluacion?.asignatura?.nombre}</td>
                                <td class="align-middle text-center">
                                    ${evaluacion.calificacion ? evaluacion.calificacion : (evaluacion.aprobada ? "A" : "NA")}
                                </td>
                                <td class="align-middle">
                                    <g:if test="${evaluacion?.tipoEvaluacion?.id != 1}">
                                        ${evaluacion?.tipoEvaluacion?.abreviatura}
                                    </g:if>
                                </td>
                                <td class="align-middle">
                                    <g:if test="${evaluacion?.tipoEvaluacion?.id != 1}">
                                        ${evaluacion?.fechaAcreditacionFormato}
                                    </g:if>
                                </td>
                                <td class="align-middle" align="end">
                                    <g:if test="${evaluacion.estatusRegistro.id == 1}">
                                        <a onclick="mostrarProceso();" href="/evaluacion/modificacion/${alumno?.id}?evaluacionId=${evaluacion.id}" class="btn btn-outline-primary btn-sm" data-tooltip="tooltip" data-placement="top" title="Editar registro">
                                            <i class="fa fa-pencil"></i>
                                        </a>
                                        <button type="button" class="btn btn-outline-danger btn-sm" data-id="${evaluacion.id}" data-toggle="modal" data-target="#modalEliminacion_${evaluacion.id}" data-tooltip="tooltip" data-placement="top" title="Eliminar registro">
                                            <i class= "fa fa-trash"></i>
                                        </button>
                                    </g:if>
                                </td>
                                    <div class="modal fade" id="modalEliminacion_${evaluacion.id}" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
                                        <div class="modal-dialog modal-dialog-centered" role="document">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <h5 class="modal-title" id="exampleModalLongTitle">Eliminar Calificación</h5>
                                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                                        <span aria-hidden="true">&times;</span>
                                                    </button>
                                                </div>
                                                <div class="modal-body">
                                                    ¿Desea eliminar la calificación seleccionada?
                                                </div>
                                                <div class="modal-footer">
                                                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                                                    <a onclick="mostrarProceso();"  href="/evaluacion/eliminar?evaluacionId=${evaluacion.id}" class="btn btn-primary">Aceptar</a>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </td>
                            </tr>
                        </g:each>
                    </tBody>
                </table>
            </div>

            <g:render template="/layouts/pagination" model="${[linkUri: "evaluacion/listar", linkParams: [search: params.search?:"", id: params.id?:""], count: conteo, max: params.max, offset: params.offset]}"/>

            <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
            <asset:javascript src="jquery-editable-select.js"/>
            <asset:javascript src="jquery-editable-select.min.js"/>
            <script> 
                $(function () {
                    $('[data-tooltip="tooltip"]').tooltip()
                })

                function mostrarProceso(time){
                    time = time || 10000
                    $("#procesando").modal("show");
                    setTimeout(function(){
                        $("#procesando").modal('hide');
                    }, time);
                }

                $("#ciclosEscolares").change(function(){
                    mostrarProceso()
                    $('#periodo').val($(this).val());
                    $('#form-busqueda').submit()
                })

                $('#limpiar-search').click(function() {
                    mostrarProceso();
                    $('#search').val('');
                    $('#form-busqueda').submit();
                });
            </script>
    </body>
</html>