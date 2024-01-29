<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Modificación de Carrera</title>
        <script src="jquery-3.5.1.min.js"></script>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/carrera/registro" class="btn btn-primary col-md-12 my-2">
                Nueva Carrera
            </a>
        </content>
        
        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Modificación certificado
                </h3>
            </div>
        </div>

        <div class="container">
            <g:if test="${flash.mensaje}">
                <g:if test="${!flash.estatus}">
                    <div class="alert alert-danger" role="alert">
                        ${flash.mensaje}
                    </div>
                </g:if>
            </g:if>
        </div>

        <div class="container ">
            <div class="card" >
                
                <div class="card-body py-3 px-3">
                    <div class="card-text">
                        <div class="progress">
                            <div class="progress-bar" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100"></div>
                        </div>
                    </div>
                </div>
            </div>
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
                                <p class="m-0"> <b> Institución: </b>${certificado.persona?.alumnos[0]?.cicloEscolar?.planEstudios?.carrera?.institucion?.nombre} </p>
                                <p class="m-0"> <b> Carrera: </b> ${certificado.persona?.alumnos[0]?.cicloEscolar?.planEstudios?.carrera?.nombre}</p>
                                <p class="m-0"> <b> Matrícula: </b> ${certificado.persona?.alumnos[0]?.matricula}</p>
                                <p class="m-0"> <b> Alumno: </b> ${certificado.persona?.nombre} ${certificado.persona?.primerApellido} ${certificado.persona?.segundoApellido}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="container">
            <div class="alert alert-light" role="alert">
              Los campos marcados con * son obligatorios.
            </div>
        </div>

        <div class="container mb-5">
            <form action="/certificado/subirFotoModificacion" class="needs-validation" novalidate>
                    <div class="form-row">
                        <input id="uuid" name="uuid" type="text" value="${certificado.uuid}" hidden>
                        <div class="form-group col-md-6">
                            <label for="libro">Libro <font color="red">*</font></label>
                            <input id="libro" name="libro" type="text" class="form-control"  value="${params.libro ? params.libro : certificado?.libro}" required onkeypress="return esEnteroPositivo(event, this.id)">
                            <div class="invalid-feedback">
                                Por favor ingrese el número de libro.
                                </div>
                            </div>
                        <div class="form-group col-md-6">
                            <label for="foja">Foja <font color="red">*</font></label>
                            <input id="foja" name="foja" type="text" class="form-control"  value="${params.foja ? params.foja : certificado?.foja}" required onkeypress="return esEnteroPositivo(event, this.id)">
                            <div class="invalid-feedback" >
                                Por favor ingrese el número de foja.
                            </div>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group col-md-6">
                            <label for="numero">Número </label>
                            <input id="numero" name="numero" type="text" class="form-control"  value="${params.numero ? params.numero : certificado?.numero}" onkeypress="return esEnteroPositivo(event, this.id)">
                        </div>
                        <div class="form-group col-md-6">
                            <label for="municipio">Municipio <font color="red">*</font></label>
                            <input id="municipio" name="municipio" type="text" class="form-control"  value="${params.municipio ? params.municipio : certificado?.municipio}" required>
                            <div class="invalid-feedback">
                                Por favor ingrese el municipio.
                            </div>
                        </div>
                    </div>
                <div class="row">
                    <div class="form-group col-md-6">
                        <label for="fechaExpedicion">Fecha <font color="red">*</font></label>
                        <input id="fechaExpedicion" name="fechaExpedicion" type="date" class="form-control"  value="${params.fechaExpedicion ? params.fechaExpedicion : fechaExpedicion}" required>
                        <div class="invalid-feedback">
                            Por favor ingrese la fecha de la expedición del certificado.
                        </div>
                    </div>
                </div>
                <div class="form-row justify-content-end">
                    <p>
                        <a onclick="mostrarProceso();" href="certificado/listarCertificados" class="btn btn-secondary">Cancelar</a>
                        <button type="submit" class="btn btn-primary">Continuar</button>
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
            $('#institucionesMod').editableSelect()
                .editableSelect()
                    .on('select.editable-select', function (e, li) {
                        $('#institucionId').val(li.val());
                    });
            $('#areas').editableSelect()
                .editableSelect()
                    .on('select.editable-select', function (e, li) {
                        $('#areaId').val(li.val());
                    });
            $("#areas").change(function(){
                if($('#areas').val() == ''){
                    $('#areaId').val('');
                }
            });
        </script>

    </body>
</html>