<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Nuevo Plan de Estudios</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/planEstudios/registro" class="btn btn-primary col-md-12 my-2">
                Nuevo plan de estudios
            </a>
            <a href="/planEstudios/subirExcel" class="btn btn-primary col-md-12 my-2">
                Cargar planes
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Nuevo Plan de Estudios
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
            <div class="alert alert-light" role="alert">
              Los campos marcados con * son obligatorios.
            </div>
        </div>

        <div class="container" >
            <form action="/planEstudios/registro" id="form-select" method="post">
                <input type="text" id="institucionIdAux" name="institucionId" value="${planEstudios?.institucionId}" hidden>
                <input id="nombreAux" name="nombre" type="text" class="form-control"  value="${planEstudios?.nombre}" hidden>
                <input type="text" id="periodoIdAux" name="periodoId" value="${planEstudios?.periodoId}" hidden>
                <input type="text" id="turnoIdAux" name="turnoId" value="${planEstudios?.turnoId}" hidden>
                <input id="horaInicioAux" name="horaInicio" type="text" class="form-control"  value="${planEstudios?.horaInicio}" hidden>
                <input id="horaFinAux" name="horaFin" type="text" class="form-control"  value="${planEstudios?.horaFin}" hidden>
                <input type="text" class="form-control" id="calificacionMinimaAux" name="calificacionMinima" value="${planEstudios?.calificacionMinima}" hidden>
                <input type="text" class="form-control" id="calificacionMinimaAprobatoriaAux" name="calificacionMinimaAprobatoria" value="${planEstudios?.calificacionMinimaAprobatoria}" hidden>
                <input type="text" class="form-control" id="calificacionMaximaAux" name="calificacionMaxima" value="${planEstudios?.calificacionMaxima}" hidden>
                <input type="text" class="form-control" id="rvoeAux" name="rvoe" value="${planEstudios?.rvoe}" hidden>
            </form>
        </div>

        <div class="container mb-5">
            <form action="/planEstudios/registrar" class="needs-validation" novalidate>
                <div class="form-group">
                    <label for="institucionesMod">Institución <font color="red">*</font></label>
                    <g:select  class="form-control" id="institucionesMod" name="institucionesMod" from="${instituciones}" optionKey="id" optionValue='${{"CCT: ${it.claveCt ?: ""} :::: NOMBRE: ${it.nombre}"}}' value="${planEstudios?.institucionId}" required="true"/>
                    <input type="text" id="institucionId" name="institucionId" value="${planEstudios?.institucionId}" hidden>
                    <div class="invalid-feedback">
                        Por favor ingrese la institución.
                    </div>
                </div>

                <label for="carrerasMod">Carrera <font color="red">*</font></label>
                <div class="form-group" id="div-carreras">
                    <g:if test="${planEstudios?.institucionId}">
                        <g:select  class="form-control" id="carrerasMod" name="carrerasMod" from="${carreras}" optionKey="id" optionValue="nombre" value="${planEstudios?.carreraId}" required="true"/>
                        <input type="text" id="carreraId" name="carreraId" value="${planEstudios?.carreraId}" hidden>
                        <div class="invalid-feedback">
                            Por favor ingrese la carrera.
                        </div>
                    </g:if>
                    <g:else>
                        <input class="form-control" type="text" readonly >
                    </g:else>
                </div>
                <div class="form-group">
                    <label for="nombre">Nombre <font color="red">*</font></label>
                    <input id="nombre" name="nombre" type="text" class="form-control"  value="${planEstudios?.nombre}" required>
                    <div class="invalid-feedback">
                        Por favor ingrese el nombre del plan de estudios.
                    </div>
                </div>
                <sec:ifNotGranted roles='ROLE_SUPERVISOR_MEDIA_PUBLICA, ROLE_SUPERVISOR_TECNICA_PUBLICA'>
                    <div class="form-row">
                        <div class="form-group col-md-6">
                            <label for="rvoe">RVOE <font color="red">*</font></label>
                            <input id="rvoe" name="rvoe" type="text" class="form-control"  value="${planEstudios?.rvoe}" required>
                            <div class="invalid-feedback">
                                Por favor ingrese el número de rvoe.
                            </div>
                        </div>
                        <div class="form-group col-md-6">
                            <label for="fechaRvoe">Fecha Rvoe <font color="red">*</font></label>
                            <input id="fechaRvoe" name="fechaRvoe" type="date" class="form-control"  value="${planEstudios?.fechaRvoe}" required>
                            <div class="invalid-feedback">
                                Por favor ingrese la fecha del otorgamiento del rvoe.
                            </div>
                        </div>
                    </div>
                </sec:ifNotGranted>
                <div class="form-group">
                    <label for="periodoId">Periodo <font color="red">*</font></label>
                    <g:select  class="form-control" id="periodoId" name="periodoId" from="${periodos}" optionKey="id" optionValue="nombre" value="${planEstudios?.periodoId}" noSelection="${['':'Seleccione...']}" required="true"/>
                    <div class="invalid-feedback">
                        Por favor ingrese el periodo.
                    </div>
                </div>
                <div class="form-group">
                    <label for="ciclos">Número de ciclos</label>
                    <input id="ciclos" name="ciclos" type="text" class="form-control"  value="${planEstudios?.ciclos}" onkeypress="return esEnteroPositivo(event, this.id);">
                </div>
                <div class="form-group">
                    <label for="turnoId">Turno <font color="red">*</font></label>
                    <g:select  class="form-control" id="turnoId" name="turnoId" from="${turnos}" optionKey="id" optionValue="nombre" value="${planEstudios?.turnoId}" noSelection="${['':'Seleccione...']}" required="true"/>
                    <div class="invalid-feedback">
                        Por favor ingrese el turno.
                    </div>
                </div>
                <div class="form-group align-items-start">
                    <label for="claveSeem">Horario</label>
                    <div class="input-group col-md-5 px-0 mb-2">
                        <div class="input-group-prepend col-md-6 px-0">
                            <div class="input-group-text col-md-12">Hora de Inicio</div>
                        </div>
                        <input id="horaInicio" name="horaInicio" type="time" class="form-control" value="${planEstudios?.horaInicio}">
                    </div>
                    <div class="input-group col-md-5 px-0 mb-2">
                        <div class="input-group-prepend col-md-6 px-0">
                            <div class="input-group-text col-md-12">Hora de Fin</div>
                        </div>
                        <input id="horaFin" name="horaFin" type="time" class="form-control"  value="${planEstudios?.horaFin}">
                        <div class="invalid-feedback">
                            La hora de fin debe de ser mayor a la hora de inicio
                        </div>
                    </div>
                </div>
                <div class="form-group align-items-start">
                        <label for="claveSeem">Calificaciones <font color="red">*</font></label>
                        <div class="input-group col-md-5 px-0 mb-2">
                            <div class="input-group-prepend col-md-6 px-0">
                                <div class="input-group-text col-md-12">Mínima</div>
                            </div>
                            <input id="calificacionMinima" type="number" step="0.01" class="form-control" id="calificacionMinima" name="calificacionMinima" value="${planEstudios?.calificacionMinima}"  onkeypress="return esCalificacion(event, this.id);" required>
                            <div class="invalid-feedback">
                                Por favor ingrese una calificación válida
                            </div>
                        </div>
                        <div class="input-group col-md-5 px-0 mb-2">
                            <div class="input-group-prepend col-md-6 px-0">
                                <div class="input-group-text col-md-12">Mínima aprobatoria </div>
                            </div>
                            <input type="number" step="0.01" class="form-control" id="calificacionMinimaAprobatoria" name="calificacionMinimaAprobatoria" value="${planEstudios?.calificacionMinimaAprobatoria}" onkeypress="return esCalificacion(event, this.id);" required>
                            <div class="invalid-feedback">
                                Por favor ingrese una calificación válida
                            </div>
                        </div>
                        <div class="input-group col-md-5 px-0">
                            <div class="input-group-prepend col-md-6 px-0">
                                <div class="input-group-text col-md-12">Máxima</div>
                            </div>
                            <input type="number" step="0.01" class="form-control" id="calificacionMaxima" name="calificacionMaxima" value="${planEstudios?.calificacionMaxima}" onkeypress="return esCalificacion(event, this.id);" required>
                            <div class="invalid-feedback">
                                Por favor ingrese una calificación válida
                            </div>
                        </div>
                </div>

                <div class="form-row justify-content-end">
                    <p>
                        <a onclick="mostrarProceso();" href="planEstudios/listar" class="btn btn-secondary">Cancelar</a>
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
            $("#horaInicio").change(function(){
                $("#horaFin").prop("min", $("#horaInicio").val());
            });

            $("#calificacionMinima").change(function(){
                $("#calificacionMinimaAprobatoria").prop("min", $("#calificacionMinima").val());
                $("#calificacionMaxima").prop("min", $("#calificacionMinima").val());
            });

            $("#calificacionMinimaAprobatoria").change(function(){
                if($("#calificacionMinimaAprobatoria").val() > $("#calificacionMinima").val()){
                    $("#calificacionMaxima").prop("min", $("#calificacionMinimaAprobatoria").val());
                }
            });

            $("#calificacionMaxima").change(function(){
                if($("#calificacionMaxima").val() > $("#calificacionMinima").val()){
                    $("#calificacionMinimaAprobatoria").prop("max", $("#calificacionMaxima").val());
                }
            });

            $('#institucionesMod').editableSelect()
                .editableSelect()
                    .on('select.editable-select', function (e, li) {
                        mostrarProceso();
                        $('#nombreAux').val($('#nombre').val());
                        $('#periodoIdAux').val($('#periodoId').val());
                        $('#turnoIdAux').val($('#turnoId').val());
                        $('#horaInicioAux').val($('#horaInicio').val());
                        $('#horaFinAux').val($('#horaFin').val());
                        $('#calificacionMinimaAux').val($('#calificacionMinima').val());
                        $('#calificacionMinimaAprobatoriaAux').val($('#calificacionMinimaAprobatoria').val());
                        $('#calificacionMaximaAux').val($('#calificacionMaxima').val());
                        $('#rvoeAux').val($('#rvoe').val());
                        $('#institucionIdAux').val(li.val());
                        $('#institucionId').val(li.val());
                        $('#form-select').submit()
                    });
            $('#carrerasMod').editableSelect()
                .editableSelect()
                    .on('select.editable-select', function (e, li) {
                        $('#carreraId').val(li.val());
                    });
        </script>
    </body>
</html>

