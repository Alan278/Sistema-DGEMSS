<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Modificar Alumnos</title>
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
                    Modificacion del Alumno
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

        <div class="container mb-3">
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
            <h6 class="pb-1">
                <b>Alumno:</b>
                <medium class="text-muted">${alumno?.persona?.nombreCompleto} </medium>
            </h6>
            <h6 class="pb-1">
                <b>Curp:</b>
                <medium class="text-muted">${alumno?.persona?.curp} </medium>
            </h6>
        </div>

        <div class="container">
            <div class="alert alert-light" role="alert">
              Los campos marcados con * son obligatorios.
            </div>
        </div>

        <div class="container mb-5">
            <form action="/alumno/modificar/${alumno?.id}" class="needs-validation" novalidate>
                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label for="matricula">Matrícula <font color="red">*</font></label>
                        <input id="matricula" name="matricula" type="text" class="form-control" value="${params.tipoRegistro ? params.matricula : (alumno?.matricula)}" required ${alumno.estatusRegistro.id == 1 ? "" : "readonly"}>
                        <div class="invalid-feedback">
                            Por favor ingrese la matricula.
                        </div>
                    </div>
                    <div class="form-group col-md-6">
                        <label for="estatus">Estatus <font color="red">*</font></label>
                        <g:select  class="form-control" id="estatusAlumnoId" name="estatusAlumnoId" from="${estatus}" optionKey="id" optionValue="nombre" value="${params.tipoRegistro ? params.estatusAlumnoId : (alumno?.estatusAlumno?.id)}" required="true"/>
                        <div class="invalid-feedback">
                            Por favor ingrese el estatus.
                        </div>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label for="correoElectronico">Correo electrónico </label>
                        <input id="correoElectronico" name="correoElectronico" type="text" class="form-control" value="${params.tipoRegistro ? params.correoElectronico : (alumno?.persona?.correoElectronico)}">
                    </div>
                    <div class="form-group col-md-6">
                        <label for="telefono">Teléfono </label>
                        <input id="telefono" name="telefono" type="text" class="form-control"  value="${params.tipoRegistro ? params.telefono : (alumno?.persona?.telefono)}" onkeypress="return esNumeroTelefonico(event, this.id)" >
                    </div>
                </div>
                <div class="form-row justify-content-end">
                    <p>
                        <a onclick="mostrarProceso();" href="/alumno/listar" class="btn btn-secondary">Cancelar</a>
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

    </body>
</html>