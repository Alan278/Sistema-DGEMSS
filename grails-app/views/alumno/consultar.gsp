<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Detalles del Alumno</title>
        <script src="jquery-3.5.1.min.js"></script>
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

        <div class="container mb-3">
            <g:if test='${flash.mensaje}'>
                <g:if test="${!flash.estatus}">
                    <div class="alert alert-danger" role="alert">
                        ${flash.mensaje}
                    </div>
                </g:if>
            </g:if>
        </div>

        <div class="container">
            <div class="jumbotron p-3">
                <h6 class="m-0 pb-1" >
                    <b> Alumno:</b>
                    <medium class="text-muted">${alumno?.persona?.nombreCompleto}</medium><br>
                </h6>
                <h6 class="m-0">
                    <b>Matrícula:</b>
                    <medium class="text-muted">${alumno?.matricula} </medium>
                </h6>
            </div>
            <ul class="nav nav-tabs mb-4">
                <li class="nav-item">
                  <a class="nav-link active" href="/alumno/consultar/${params?.id}">Detalles</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" href="/evaluacion/listar/${params?.id}">Evaluaciones (${alumno?.numEvaluaciones})</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" href="/alumno/listarCiclosEscolares/${params?.id}">Ciclos Escolares (${alumno?.numCiclosEscolares})</a>
                </li>
            </ul>

            <div class="pl-1">
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
                    <medium class="text-muted">${alumno?.planEstudios?.nombre} - ${alumno?.planEstudios?.rvoe} - ${alumno?.planEstudios?.fechaRvoeFormato}</medium>
                </h6>
                <g:if test="${alumno.formacion}">
                    <h6 class="pb-1">
                        <b>Formacion:</b>
                        <medium class="text-muted">${alumno?.formacion?.nombre}</medium>
                    </h6>
                </g:if>
                <h6 class="pb-1">
                    <b>Matrícula:</b>
                    <medium class="text-muted">${alumno?.matricula} </medium>
                </h6>
                <h6 class="pb-0">
                    <b>Estatus:</b>
                    <medium class="text-muted">${alumno?.estatusAlumno?.nombre} </medium>
                </h6>
                <hr>
                <h6 class="pb-1">
                    <b>CURP:</b>
                    <medium class="text-muted">${alumno?.persona?.curp}</medium>
                </h6>
                <h6 class="pb-1">
                    <b>Entidad de nacimiento:</b>
                    <medium class="text-muted">${alumno?.persona?.entidadNacimiento}</medium>
                </h6>
                <h6 class="pb-0">
                    <b>Sexo:</b>
                    <medium class="text-muted">${alumno?.persona?.sexo}</medium>
                </h6>
                <hr>
                <h6 class="pb-1">
                    <b>Correo electrónico:</b>
                    <medium class="text-muted">${alumno?.persona?.correoElectronico} </medium>
                </h6>
                <h6 class="pb-5">
                    <b>Teléfono:</b>
                    <medium class="text-muted">${alumno?.persona?.telefono} </medium>
                </h6>
            </div>
        </div>

        <div class="container">

            <div class="form-row justify-content-end">
                <p>
                    <g:if test="${alumno.estatusRegistro.id == 1}">
                        <a onclick="mostrarProceso();" href="alumno/modificacion/${alumno?.id}" class="btn btn-secondary">
                            Modificar
                        </a>
                        <button type="button" class="btn btn-outline-danger" data-toggle="modal" data-target="#modalEliminacion">
                            Eliminar
                        </button>
                    </g:if>
                </p>
            </div>
            <div class="modal fade" id="modalEliminacion" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="exampleModalLongTitle">Eliminar Alumno</h5>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div class="modal-body">
                            ¿Desea eliminar al Alumno?
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                            <a onclick="mostrarProceso();" href="alumno/eliminar/${alumno?.id}" class="btn btn-primary">Aceptar</a>
                        </div>
                    </div>
                </div>
            </div>
            <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
            <asset:javascript src="jquery-editable-select.js"/>
            <asset:javascript src="jquery-editable-select.min.js"/>
            <asset:javascript src="filter-buttons.js"/>

        </div>
    </body>
</html>