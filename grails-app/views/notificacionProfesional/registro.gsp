<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Expedición de Constancia</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>
        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Expedición de Constancias - Generar
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

        <div class="container my-3">
            <div class="row align-items-end">
                <div class="col-md-6">
                    <div class="card" >
                        <div class="card-header">
                            Datos del alumno
                            </div>
                        <div class="card-body py-2 px-3">
                            <div class="card-text">
                                <p class="m-0"> <b> Institución: </b>${notificacion?.alumno?.carrera?.institucion?.nombre} </p>
                                <p class="m-0"> <b> Carrera: </b> ${notificacion?.alumno?.carrera?.nombre}</p>
                                <p class="m-0"> <b> Matrícula: </b> ${notificacion?.alumno?.matricula}</p>
                                <p class="m-0"> <b> Alumno: </b> ${notificacion?.alumno?.persona?.nombre} ${notificacion?.alumno?.persona?.primerApellido} ${notificacion?.alumno?.persona?.segundoApellido}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="container">
        </div>

        <div class="container mb-5">
            <form action="/NotificacionProfesional/aceptar" class="needs-validation" novalidate>
                    <input id="uuid" name="uuid" type="text" value="${notificacion?.uuid}" hidden>
                    <div class="form-row">
                <div class="form-row justify-content-end">
                    <p>
                        <a onclick="mostrarProceso();" href="NotificacionProfesional/revision?uuid=${notificacion?.uuid}" class="btn btn-secondary">Cancelar</a>
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
