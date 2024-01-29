<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Expedición de Certificado</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>
        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Expedición de Certificados - Generar
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

        <div class="container mt-3 mb-3">
            <h6 class="pb-1">
                <b>Institución:</b>
                <medium class="text-muted">${certificado?.alumno?.planEstudios?.carrera?.institucion?.nombre} </medium>
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
        </div>

        <div class="container">
            <div class="alert alert-light" role="alert">
              Los campos marcados con * son obligatorios.
            </div>
        </div>

        <div class="container mb-5">
            <form action="/certificado/aceptar" class="needs-validation" novalidate>
                    <input id="uuid" name="uuid" type="text" value="${certificado?.uuid}" hidden>
                    <div class="form-row">
                        <div class="form-group col-md-6">
                            <label for="libro">Libro <font color="red">*</font></label>
                            <input id="libro" name="libro" type="text" class="form-control"  value="${params?.libro}" required onkeypress="return esEnteroPositivo(event, this.id)">
                            <div class="invalid-feedback">
                                Por favor ingrese el número de libro.
                                </div>
                            </div>
                        <div class="form-group col-md-6">
                            <label for="foja">Foja <font color="red">*</font></label>
                            <input id="foja" name="foja" type="text" class="form-control"  value="${params?.foja}" required onkeypress="return esEnteroPositivo(event, this.id)">
                            <div class="invalid-feedback" >
                                Por favor ingrese el número de foja.
                            </div>
                        </div>
                    </div>
                    <div class="form-group col-md-6">
                        <label for="numero">Número </label>
                        <input id="numero" name="numero" type="text" class="form-control"  value="${params?.numero}" onkeypress="return esEnteroPositivo(event, this.id)">
                    </div>

                <div class="form-row justify-content-end">
                    <p>
                        <a onclick="mostrarProceso();" href="certificado/revision?uuid=${certificado?.uuid}" class="btn btn-secondary">Cancelar</a>
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
