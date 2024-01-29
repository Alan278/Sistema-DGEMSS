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
        <div class="container">
            <div class="alert alert-light" role="alert">
              Los campos marcados con * son obligatorios.
            </div>
        </div>
        <div class="container">
            <div class="form-group mb-4" >
                <h6> <b> Institución: </b>${alumno?.cicloEscolar?.carrera?.institucion?.nombre} </h6>
                <h6 class="ml-3"> </h6>
            </div>
            <div class="form-group mb-4" >
                <h6> <b> Carrera: </b> ${alumno?.cicloEscolar?.carrera?.nombre}</h6>
            </div>
            <div class="form-group mb-4" >
                <h6> <b> Ciclo: </b> ${alumno?.cicloEscolar?.nombre}: ${alumno?.cicloEscolar?.inicio} al ${alumno?.cicloEscolar?.fin} </h6>
            </div>
            <div class="form-group mb-4" >
                <h6> <b> Matrícula: </b> ${alumno?.matricula}</h6>
            </div>
            <div class="form-group mb-4" >
                <h6> <b> Alumno: </b> ${alumno?.persona?.nombre} ${alumno?.persona?.primerApellido} ${alumno?.persona?.segundoApellido}</h6>
            </div>
            <div class="form-group mb-4" >
                <h6> <b> Libro: </b> ${certificado?.libro} </h6>
            </div>
            <div class="form-group mb-4" >
                <h6> <b> Foja: </b> ${certificado?.foja}</h6>
            </div>
            <div class="form-group mb-4" >
                <h6> <b> Número: </b> ${certificado?.numero}</h6>
            </div>
        </div>

        <div class="container mb-5">
            <form action="/certificado/registrar/${alumno?.id}" class="needs-validation" novalidate>
                 <div class="form-row">
                    <div class="form-group col-md-6">
                        <label for="fecha">Fecha <font color="red">*</font></label>
                        <input id="fecha" name="fecha" type="date" class="form-control"  value="${params?.fecha}" required>
                        <div class="invalid-feedback">
                            Por favor ingrese la fecha de la expedición del certificado.
                        </div>
                    </div>
                </div>
                <div class="form-row justify-content-end">
                    <p>
                        <a onclick="mostrarProceso();" href="certificado/listarAlumnos/${alumno?.id}" class="btn btn-secondary">Cancelar</a>
                         <a onclick="mostrarProceso();" href="certificado/firmar/${alumno?.id}" class="btn btn-primary">Siguiente</a>

                    </p>
                </div>
            </form>
        </div>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <asset:javascript src="jquery-editable-select.js"/>
        <asset:javascript src="jquery-editable-select.min.js"/>
        <asset:javascript src="filter-buttons.js"/>

    </body>
</html>
