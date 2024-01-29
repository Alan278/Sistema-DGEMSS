<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Expedición de Certificado</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
        <asset:stylesheet src="cropper-1.5.9.css" />
        <asset:javascript src="jquery-3.5.1.js"/>
        <asset:javascript src="cropper-1.5.9.js"/>
        <asset:javascript src="recortar-foto.js"/>
        <asset:javascript src="photo-style.js"/>
        <style>
            .cropper-view-box, .cropper-face {
                border-radius: 0%;
            }

            #fotografias {
                display: flex;
                justify-content: center;
                align-items: center;
            }
    
            #contenedorFotografia img, #contenedorFotografiaRecortada img{
                width: 316px;
                height: 380px;
            }
        </style>
    </head>
    <body>
        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Expedición de Certificados - Subir foto
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
            <div class="card" >
                <div class="card-body py-3 px-3">
                    <div class="card-text">
                        <div class="progress ">
                            <div class="progress-bar" role="progressbar" style="width: 50%" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="container mt-3 mb-5">
            <div class="row align-items-end">
                <div class="col-md-6">
                    <div class="card" >
                        <div class="card-header">
                            Datos del alumno
                            </div>
                        <div class="card-body py-2 px-3">
                            <div class="card-text">
                                <p class="m-0"> <b> Institución: </b>${persona?.alumnos[0]?.cicloEscolar?.carrera?.institucion?.nombre} </p>
                                <p class="m-0"> <b> Carrera: </b> ${persona?.alumnos[0]?.cicloEscolar?.carrera?.nombre}</p>
                                <p class="m-0"> <b> Matrícula: </b> ${persona?.alumnos[0]?.matricula}</p>
                                <p class="m-0"> <b> Alumno: </b> ${persona?.nombre} ${persona?.primerApellido} ${persona?.segundoApellido}</p>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <p class="m-0"> <b> Fecha de expedición: </b> ${params.fechaExpedicion ? params.fechaExpedicion : (persona?.constancias[0]?.fechaExpedicion)}</p>
                </div>
            </div>
        </div>

        <div class="container mb-5 ">
            <div class="row justify-content-center">
                <div class="col-md-6">
                    <form id="formulario" action="/certificado/registrar/${persona?.id}" method="POST" class="needs-validation" novalidate>
                        <div class="row mb-3">
                            <input type="text" id="foto" name="foto" value="${params.logo}" hidden>
                            <div class="custom-file mx-3">
                                <label class="custom-file-label" for="customFile">Elija un foto</label>
                                <input type="file" class="custom-file-input" name="seleccionar" accept="image/png, image/jpg, image/jpeg" required>
                                <div class="invalid-feedback">
                                    Por favor elija el archivo a subir.
                                </div>
                            </div>
                        </div>
                        <div class="row justify-content-center my-1">
                            <button type="button" class="btn btn-secondary" onclick="javascript:moverArriba()">
                                <i class="bi bi-caret-up-fill"></i>
                            </button>
                        </div>
                        <div class="row justify-content-center my-2">
                            <button type="button" class="btn btn-secondary mx-1" onclick="javascript:moverIzquierda()">
                                <i class="bi bi-caret-left-fill"></i>
                            </button>
                            <button type="button" class="btn btn-outline-secondary mx-1" onclick="javascript:moverAbajo()" disabled>
                                <i class="bi bi-dot"></i>
                            </button>
                            <button type="button" class="btn btn-secondary mx-1" onclick="javascript:moverDerecha()">
                                <i class="bi bi-caret-right-fill"></i>
                            </button>
                        </div>
                        <div class="row justify-content-center my-1">
                            <button type="button" class="btn btn-secondary" onclick="javascript:moverAbajo()">
                                <i class="bi bi-caret-down-fill"></i>
                            </button>
                        </div>
                        <div class="row justify-content-center mt-5">
                            <button type="button" class="btn btn-secondary mx-1" onclick="javascript:alejar()">
                                <i class="bi bi-zoom-out"></i>
                            </button>
                            <button type="button" class="btn btn-secondary mx-1" onclick="javascript:acercar()">
                                <i class="bi bi-zoom-in"></i>
                            </button>
                        </div>
                        <div class="row justify-content-center mt-5">
                            <p>
                                <a onclick="mostrarProceso();" href="constanciaservicio/listarAlumnos" class="btn btn-secondary">Cancelar</a>
                                <a onclick="mostrarProceso();" href="constanciaservicio/registro/${persona?.id}?fechaExpedicion=${params.fechaExpedicion}" class="btn btn-secondary">Volver</a>
                                <button  class="btn btn-primary" onclick="javascript:enviar()">Continuar</button>
                            </p>
                        </div>
                    </form>
                </div>
                <div id="fotografias" class="col-md-6">
                    <div id="contenedorFotografia">
                        <img id="fotografiaOriginal" >
                    </div>
                </div>
            </div>
        
        </div>

    </body>
</html>
