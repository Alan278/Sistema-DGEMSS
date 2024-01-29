<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Detalle de Evaluaciones</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
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
                    Detalle de evaluación
                </h3>
            </div>
        </div>

        <div class="container mb-0">
            <div class="row align-items-end justify-content-between">
                <div class="col-md-6">
                    <div class="card" >
                        <div class="card-header">
                            Datos del alumno
                            </div>
                        <div class="card-body py-2 px-3">
                            <div class="card-text">
                                <p class="m-0"> <b> Institución: </b>${evaluacionAlumno?.alumno?.planEstudios?.carrera?.institucion?.nombre} </p>
                                <p class="m-0"> <b> Carrera: </b>  ${evaluacionAlumno?.alumno?.planEstudios?.carrera?.nombre}</p>
                                <p class="m-0"> <b> Matrícula: </b>  ${evaluacionAlumno?.alumno?.matricula}</p>
                                <p class="m-0"> <b> Alumno: </b>  ${evaluacionAlumno?.alumno?.persona?.nombre} ${evaluacionAlumno?.alumno?.persona?.primerApellido} ${evaluacionAlumno?.alumno?.persona?.segundoApellido}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class=" container mt-4 mb-4">
            <div class="form-group mb-4" >
                <h5> <b> Asignatura: </b> </h5>
                <h6 class="ml-3">${evaluacionAlumno?.asignatura?.nombre} </h6>
            </div>
            <div class="form-group mb-4" >
                <h5> <b> Calificación: </b> </h5>
                <h6 class="ml-3">${evaluacionAlumno.calificacion ? evaluacionAlumno.calificacion : (evaluacionAlumno.aprobada ? "A" : "NP")}</h6>
            </div>

            <div class="form-row justify-content-end">
                <p>
                    <a onclick="mostrarProceso();" href="/evaluacion/listar/${evaluacionAlumno?.alumno?.id}" class="btn btn-secondary">Volver</a>
                    <g:if test="${evaluacionAlumno.estatusRegistro.id == 1}">
                        <a onclick="mostrarProceso();" href="/evaluacion/modificacion/${evaluacionAlumno?.alumno?.id}?carreraId=${evaluacionAlumno?.alumno?.planEstudios?.carrera?.id}&evaluacionId=${evaluacionAlumno.id}" class="btn btn-primary">
                            Modificar
                        </a>
                        <button type="button" class="btn btn-danger" data-toggle="modal" data-target="#modalEliminacion">
                            Eliminar
                        </button>
                    </g:if>
                </p>
            </div>
        </div>
        <div class="modal fade" id="modalEliminacion" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="exampleModalLongTitle">Eliminar Calificación</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        ¿Desea eliminar la calificación?
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                        <a onclick="mostrarProceso();" href="evaluacion/eliminar/${evaluacionAlumnos?.id}" class="btn btn-primary">Aceptar</a>
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
