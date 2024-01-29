<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Lista de Ciclos Escolares</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/cicloEscolar/registro" class="btn btn-primary col-md-12 my-2">
                Nuevo ciclo escolar
            </a>
            <a href="/cicloEscolar/subirExcel" class="btn btn-primary col-md-12 my-2">
                Cargar ciclos escolares
            </a>
        </content>


        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Ciclos escolares
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
            <form action="/cicloEscolar/listar" class="mt-4" id="form-busqueda">
                <label for="instituciones">Institución</label>
                <div class="input-group mb-2">
                    <g:select  class="form-control" id="instituciones" name="instituciones" from="${instituciones}" optionKey="id" optionValue="nombre" value="${parametros?.institucionId}"/>
                    <input id="institucionId" name="institucionId" value="${params.institucionId}" hidden>
                    <g:if test="${params.institucionId}">
                        <div class="input-group-prepend">
                            <button id="limpiar-institucion" class="btn btn-outline-secondary">
                                <i class="fa fa-close"></i>
                            </button>
                        </div>
                    </g:if>
                </div>

                <g:if test="${parametros?.institucionId}">
                    <label for="carreras">Carrera</label>
                    <div class="input-group mb-3">
                        <g:select  class="form-control" id="carreras" name="carreras" from="${carreras}" optionKey="id" optionValue="nombre" value="${parametros?.carreraId}"/>
                        <input id="carreraId" name="carreraId" value="${params.carreraId}" hidden>
                        <g:if test="${params.carreraId}">
                            <div class="input-group-prepend">
                                <button id="limpiar-carrera" class="btn btn-outline-secondary">
                                    <i class="fa fa-close"></i>
                                </button>
                            </div>
                        </g:if>
                    </div>
                </g:if>
                <g:if test="${parametros?.carreraId}">
                    <label for="planesEstudios">Plan de estudios</label>
                    <div class="input-group">
                        <g:select  class="form-control" id="planesEstudios" name="planesEstudios" from="${planesEstudios}" optionKey="id" optionValue='${{"NOMBRE: ${it.nombre} :::: RVOE: ${it.rvoe ?: ""} - ${it.fechaRvoeFormato ?: ""}"}}' value="${parametros?.planEstudiosId}"/>
                        <input id="planEstudiosId" name="planEstudiosId" value="${params.planEstudiosId}" hidden>
                        <g:if test="${params.planEstudiosId}">
                            <div class="input-group-prepend">
                                <button id="limpiar-planEstudios" class="btn btn-outline-secondary">
                                    <i class="fa fa-close"></i>
                                </button>
                            </div>
                        </g:if>
                    </div>
                </g:if>

                <div class="row mt-4">
                    <div class="col-md-6">
                    </div>
                    <div class="col-md-6">
                        <div class="input-group mb-3">
                            <input type="text" class="form-control" id="search" name="search" value="${parametros?.search}" placeholder="Nombre del ciclo escolar">
                            <div class="input-group-prepend">
                                <input id='buscar' type="button" class="btn btn-primary" value="Buscar"/>
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
            <table class="table table-striped ">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Nombre</th>
                        <th>Periodo</th>
                        <th>Fecha de Inicio</th>
                        <th>Fecha de Fin</th>
                        <th>Plan de estudios</th>
                        <th></th>
                    </tr>
                </thead>
                <tBody>
                    <g:each status="i" in="${ciclosEscolares}" var="cicloEscolar">
                            <tr>
                                <td class="align-middle">${i+1}</td>
                                <td class="align-middle"><a onclick="mostrarProceso();" href="/cicloEscolar/consultar/${cicloEscolar.id}" class="alert-link" data-tooltip="tooltip" data-placement="top" title="Consultar Ciclo Escolar">${cicloEscolar.nombre}</a></td>
                                <td class="align-middle">${cicloEscolar.periodo}</td>
                                <td class="align-middle">${cicloEscolar.inicio}</td>
                                <td class="align-middle">${cicloEscolar.fin}</td>
                                <td class="align-middle">${cicloEscolar.planEstudios.nombre}</td>
                                <td class="align-middle" align="end">
                                    <a onclick="mostrarProceso();" href="/cicloEscolar/listarAlumnos/${cicloEscolar.id}" class="btn btn-outline-primary btn-sm" >
                                        <g:if test="${cicloEscolar.numAlumnos < 10}">
                                            Alumnos | 0${cicloEscolar.numAlumnos}
                                        </g:if>
                                        <g:else>
                                            Alumnos | ${cicloEscolar.numAlumnos}
                                        </g:else>
                                    </a>
                                    <g:if test="${cicloEscolar.estatusRegistro.id != 1}">
                                        <button type="button" class="btn btn-outline-secondary btn-sm" disabled>
                                            <i class="fa fa-pencil"></i>
                                        </button>
                                        <button type="button" class="btn btn-outline-secondary btn-sm" disabled>
                                            <i class= "fa fa-trash"></i>
                                        </button>
                                    </g:if>
                                    <g:if test="${cicloEscolar.estatusRegistro.id == 1}">
                                            <a onclick="mostrarProceso();" href="/cicloEscolar/modificacion/${cicloEscolar.id}" class="btn btn-outline-primary btn-sm" data-tooltip="tooltip" data-placement="top" title="Editar Registro">
                                                <i class="fa fa-pencil"></i>
                                            </a>
                                            <button type="button" class="btn btn-outline-danger btn-sm" data-id="${cicloEscolar.id}" data-toggle="modal" data-target="#modalEliminacion_${cicloEscolar.id}" data-tooltip="tooltip" data-placement="top" title="Eliminar Registro">
                                                <i class= "fa fa-trash"></i>
                                            </button>
                                        <div class="modal fade" id="modalEliminacion_${cicloEscolar.id}" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
                                            <div class="modal-dialog modal-dialog-centered" role="document">
                                                <div class="modal-content">
                                                    <div class="modal-header">
                                                        <h5 class="modal-title" id="exampleModalLongTitle">Eliminar Ciclo Escolar</h5>
                                                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                                            <span aria-hidden="true">&times;</span>
                                                        </button>
                                                    </div>
                                                    <div class="modal-body">
                                                        ¿Desea eliminar el ciclo escolar seleccionado?<br><br>
                                                        Tenga en cuenta que al eliminar un ciclo escolar eliminara todas las evaluaciones registradas que a el correspondan
                                                    </div>
                                                    <div class="modal-footer">
                                                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                                                        <a onclick="mostrarProceso();" href="/cicloEscolar/eliminar/${cicloEscolar.id}" class="btn btn-primary">Aceptar</a>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </g:if>
                                </td>
                            </tr>
                    </g:each>
                </tBody>
            </table>
        </div>

        <g:render template="/layouts/pagination" model="${[linkUri: "cicloEscolar/listar", linkParams: [search: params.search?:"", institucionId: params.institucionId?:"", carreraId: params.carreraId?:""], count: conteo, max: params.max, offset: params.offset]}"/>

        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <asset:javascript src="jquery-editable-select.js"/>
        <asset:javascript src="jquery-editable-select.min.js"/>
        <asset:javascript src="filter-buttons.js"/>

        <script>
            $(function () {
                $('[data-tooltip="tooltip"]').tooltip()
            })
        </script>

    </body>
</html>