<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Nuevas Evaluaciones</title>
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
                    Nueva evaluación
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

        <div class="container mb-4">
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
                <medium class="text-muted">${alumno?.planEstudios?.nombre} </medium>
            </h6>
            <g:if test="${alumno.formacion}">
                <h6 class="pb-1">
                    <b>Formación:</b>
                    <medium class="text-muted">${alumno?.formacion?.nombre} </medium>
                </h6>
            </g:if>
            <h6 class="pb-1">
                <b>Curp:</b>
                <medium class="text-muted">${alumno?.persona?.curp} </medium>
            </h6>
            <h6 class="pb-1">
                <b>Matrícula:</b>
                <medium class="text-muted">${alumno?.matricula} </medium>
            </h6>
            <h6 class="pb-1">
                <b>Alumno:</b>
                <medium class="text-muted">${alumno?.persona?.nombreCompleto} </medium>
            </h6>
        </div>

        <div class="container">
            <div class="alert alert-light" role="alert">
              Los campos marcados con * son obligatorios.
            </div>
        </div>

        <div class="container mb-5">

            <form id="formMostrarCampos" action="/evaluacion/registro/${alumno?.id}">
                <g:hiddenField id="asignaturaIdAux" name="asignaturaId" value="${params?.asignaturaId}" />
            </form>

            <form action="/evaluacion/registrar/${alumno?.id}" class="needs-validation" novalidate>
                <div class="form-group">
                    <label for="asignaturaId">Asignatura <font color="red">*</font></label>
                    <g:select  class="form-control" id="asignaturaId" name="asignaturaId" from="${asignaturas}" optionKey="id"
                    optionValue="${{'Periodo:' + it.periodo + ' | Clave:' + it.clave + ' | Nombre: ' + it.nombre}}"
                    value="${params?.asignaturaId}" noSelection="${['':'Seleccione...']}" required="true"/>
                    <div class="invalid-feedback">
                        Por favor ingrese la asignatura.
                    </div>
                </div>

                <g:if test="${asignatura}">
                    <g:if test="${asignatura.formacion.requerida}">
                        <div id="basica" class="mb-5">
                            <div class="form-group">
                                <label for="calificacion">Calificación (${alumno?.planEstudios?.calificacionMinima} - ${alumno?.planEstudios?.calificacionMaxima}) <font color="red">*</font></label>
                                <input id="calificacion" name="calificacion" type="text" class="form-control"  value="${params?.calificacion}" onkeypress="return esCalificacion(event, this.id)" required>
                                <div class="invalid-feedback">
                                    Por favor ingrese la calificación válida.
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="tipoEvaluacionId">Tipo de evaluación <font color="red">*</font></label>
                                <g:select  class="form-control" id="tipoEvaluacionId" name="tipoEvaluacionId" from="${tiposEvaluacion}" optionKey="id"
                                optionValue="nombre"
                                value="${params?.tipoEvaluacionId }" required="true"/>
                                <div class="invalid-feedback">
                                    Por favor ingrese la asignatura.
                                </div>
                            </div>
                            <div id="fecha-acreditacion-input" class="form-row">
                                <div class="form-group col-md-4">
                                    <label for="tipoEvaluacionId">Fecha de acreditación <font color="red">*</font></label>
                                    <input type="date" name="fechaAcreditacion" class="form-control" value="${params.fechaAcreditacion}">
                                    <div class="invalid-feedback">
                                        Por favor ingrese la fecha de acreditacion.
                                    </div>
                                </div>
                            </div>
                        </div>
                    </g:if>
                    <g:else>
                        <div id="paraescolares" class="mb-5">
                            <div class="form-group">
                                <label for="calificacion">Calificación <font color="red">*</font></label>
                                <select name="calificacion" id="calificacion" class="form-control">
                                    <option value="A">Acreditada</option>
                                    <option value="NA">No acreditada</option>
                                </select>
                            </div>
                        </div>
                    </g:else>
                </g:if>

                <div class="form-row justify-content-end mb-5" >
                    <p>
                        <a onclick="mostrarProceso();" href="evaluacion/listar/${alumno?.id}" class="btn btn-secondary">Cancelar</a>
                        <button type="submit" class="btn btn-primary">Guardar</button>
                    </p>
                </div>
            </form>
        </div>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <asset:javascript src="jquery-editable-select.js"/>
        <asset:javascript src="jquery-editable-select.min.js"/>
        <asset:javascript src="filter-buttons.js"/>
        <asset:javascript src="validations.js"/>
        <script>

            iniciarForm()
            function iniciarForm(){
                let tipoEvaluacionId = "${params.tipoEvaluacionId ?: 1}"

                $("#fecha-acreditacion-input").hide()
                $("input").prop('required',false);
                if(tipoEvaluacionId != "1"){
                    $("#fecha-acreditacion-input").show()
                    $("input").prop('required',true);
                }
            }


            $('#asignaturaId').change(function() {
                $('#asignaturaIdAux').val($('#asignaturaId').val())
                $('#formMostrarCampos').submit()
                mostrarProceso(500)
            });

            $("#tipoEvaluacionId").change(() => {
                if($("#tipoEvaluacionId").val() != 1){
                    $("#fecha-acreditacion-input").show()
                    $("input").prop('required',true);
                }else{
                    $("#fecha-acreditacion-input").hide()
                    $("input").prop('required',false);
                }
            })
        </script>
    </body>
</html>
