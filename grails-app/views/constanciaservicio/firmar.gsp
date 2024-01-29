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
                    Firma de constancia
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
            <div class="row align-items-end">
                <div class="col-md-6">
                    <div class="card" >
                        <div class="card-header">
                            Datos del alumno
                            </div>
                        <div class="card-body py-2 px-3">
                            <div class="card-text">
                                <p class="m-0"> <b> Institución: </b>${constancia?.alumno?.carrera?.institucion?.nombre} </p>
                                <p class="m-0"> <b> Carrera: </b> ${constancia?.alumno?.carrera?.nombre}</p>
                                <p class="m-0"> <b> Matrícula: </b> ${constancia?.alumno?.matricula}</p>
                                <p class="m-0"> <b> Alumno: </b> ${constancia?.alumno?.persona?.nombre} ${constancia?.alumno?.persona?.primerApellido} ${constancia?.alumno?.persona?.segundoApellido}</p>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <p class="m-0"> <b> Fecha de expedición: </b> ${constancia?.firmaAutenticadorDgemss?.fechaFirmaFormato}</p>
                </div>
            </div>
        </div>

        <% 
            def firmaElectronicaActiva = null
            usuario.persona.firmasElectronicas.any{ firma -> 
                if(!firma.expiro()){
                    firmaElectronicaActiva = firma
                    true
                }
            }

        %>

        <div class="container">
            <div class="alert alert-light" role="alert">
              Los campos marcados con * son obligatorios.
            </div>
        </div>

        <div class="container mb-5">
            <g:if test="${!firmaElectronicaActiva}">
                <div class="alert alert-warning" role="alert">
                    No cuenta con un certificado (.cer) activo. Por favor registre uno para seguir firmando. <a href="/firmaElectronica/registroCer">Registrar</a>
                </div>
            </g:if>
            <g:else>
                <form id="formu" action="/constanciaservicio/firmarConstancia" enctype="multipart/form-data" method="POST" class="needs-validation" novalidate>
                    
                    <input type="text" name="uuid" value="${constancia?.uuid}" hidden>
                    <input type="text" name="firmaId" value="${firmaElectronicaActiva.id}" hidden>
                    
                    <g:if test="${!firmaElectronicaActiva.archivoKey}">
                        <div class="form-group">
                            <label>Clave privada (.key)<font color="red">*</font></label>
                            <div class="custom-file">
                                <label class="custom-file-label" for="customFile">Elija un archivo</label>
                                <input type="file" class="custom-file-input" id="customFile" name="clavePrivada" accept=".key" required>
                                <div class="invalid-feedback">
                                    Por favor elija el archivo a subir.
                                </div>
                            </div>
                        </div>
                    </g:if>
    
                    <div class="form-group">
                        <label for="contraseña">Contraseña <font color="red">*</font></label>
                        <input type="password" class="form-control" id="contrasena" name="contrasena" placeholder="Password" required>
                        <div class="invalid-feedback">
                            Por favor ingrese la contraseña.
                        </div>
                    </div>
                    <div class="form-row justify-content-end">
                        <p>
                            <a onclick="mostrarProceso();" href="constanciaservicio/listarFirmasConstancias" class="btn btn-secondary">Cancelar</a>
                            <button type="submit" class="btn btn-primary" onclick="llenarProgreso()">Firmar</button>
                        </p>
                    </div>
                </form>
            </g:else>
        </div>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <asset:javascript src="jquery-editable-select.js"/>
        <asset:javascript src="jquery-editable-select.min.js"/>
        <asset:javascript src="filter-buttons.js"/>

    </body>
</html>
