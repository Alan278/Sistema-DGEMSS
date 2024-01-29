<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Expedición de notificacion </title>
        <asset:stylesheet src="jquery-editable-select.css"/>
        <asset:stylesheet src="cropper-1.5.9.css" />
        <asset:javascript src="jquery-3.5.1.js"/>
        <asset:javascript src="cropper-1.5.9.js"/>
        <asset:javascript src="recortar-foto.js"/>
        <asset:javascript src="photo-style-rounded.js"/>
        <style>
            html {
                font-family: sans-serif;
            }
    
            body {
                padding: 10;
                margin: 10;
            }
            html, body {
                height: 100%;
                width: 100%;
            }
    
            .cropper-view-box, .cropper-face {
                border-radius: 50%;
            }
    
            .cropper-modal {
                opacity: 0.7!important;
            }
    
            .cropper-dashed {
                border-color: red;
            }
    
            #fotografias {
                display: flex;
                justify-content: center;
                align-items: center;
            }
    
            #contenedorFotografia img, #contenedorFotografiaRecortada img{
                width: 270px;
                height: 360px;
            }
        </style>
    </head>
    <body>
        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Expedición de Notificaciones - Subir logo
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

        <div class="container mt-3 mb-5">
            <div class="row align-items-end">
                <div class="col-md-6">
                    <div class="card" >
                        <div class="card-header">
                            Datos del alumno
                            </div>
                        <div class="card-body py-2 px-3">
                            <div class="card-text">
                                <p class="m-0"> <b> Institución: </b>${acta.alumno?.carrera?.institucion?.nombre} </p>
                                <p class="m-0"> <b> Carrera: </b> ${acta.alumno?.carrera?.nombre}</p>
                                <p class="m-0"> <b> Matrícula: </b> ${acta?.alumno?.matricula}</p>
                                <p class="m-0"> <b> Alumno: </b> ${acta.alumno?.persona?.nombre} ${acta.alumno?.persona?.primerApellido} ${acta.alumno?.persona?.segundoApellido}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="container mb-5">
            <div class="row justify-content-center">
                <div class="col-md-6">
                    <form id="formulario" action="/actaprofesional/modificarFotografia" method="POST" class="needs-validation" novalidate enctype="multipart/form-data">
                        <input id="uuid" name="uuid" type="text" value="${acta.uuid}" hidden>
                            <label class="d-block text-secondary">Opción de titulación</label>
                            <select name="opctitulacionId" id="opctitulacionId" required="required" class="form-control mb-2">
                                <g:each in="${opctitulaciones}" var= "opctitulacion">
                                    <option value="${opctitulacion.id}">${opctitulacion.nombre}</option>
                                </g:each>
                            </select>
                            <label class="d-block text-secondary">Titulo del proyecto</label>
                            <input type="text" value="${acta.titulo}" name="titulo" id="titulo" required="required" class="form-control mb-2" placeholder="Titulo" />
                             <input type="text" value="${acta.presidente}" name="presidente" id="presidente" required="required" class="form-control mb-2" placeholder="Presidente" />
                            <label class="d-block text-secondary">Nombre del secretario</label>
                            <input type="text" value="${acta.secretario}" name="secretario" id="secretario" required="required" class="form-control mb-2" placeholder="Secretario" />
                            <label class="d-block text-secondary">Nombre del vocal</label>
                            <input type="text" value="${acta.vocal}" name="vocal" id="vocal" required="required" class="form-control mb-2" placeholder="Vocal" />
                            <label class="d-block text-secondary">Tipo de documento</label>
                            <select name="docId" id="docId" required="required" class="form-control mb-2">
                                <g:each in="${tipodocumentos}" var= "doc">
                                    <option value="${doc.id}">${doc.nombre}</option>
                                </g:each>
                            </select>
                            <label class="d-block text-secondary">Declaración del jurado</label>
                            <select name="declaracionId" id="declaracionId" required="required" class="form-control mb-2">
                                <<g:each in="${declaraciones}" var= "declaracion">
                                    <option value="${declaracion.id}">${declaracion.nombre}</option>
                                </g:each>
                            </select>
                            <input type="text" name="alumnoId" value="${alumno?.id}" hidden>
                            <input type="file" id="archivopdf" name="archivopdf" accept=".pdf" required>
                            <br>
                            <input type="text" id="foto" name="foto" hidden>
                            <div class="custom-file mx-3">
                                <label class="custom-file-label" for="customFile">Elija un foto</label>
                                <input type="file" class="custom-file-input" name="seleccionar" accept="image/png, image/jpg, image/jpeg" required>
                                <div class="invalid-feedback">
                                    Por favor elija el archivo a subir.
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
                            <!-- <button class="btn btn-secondary" onclick="javascript:crop()">
                                <i class="bi bi-file-earmark-pdf"></i>
                            </button> --> 
                        </div>
                        <div class="row justify-content-center mt-5">
                            <p>
                                <a onclick="mostrarProceso();" href="actaprofesional/listarActas" class="btn btn-secondary">Cancelar</a>
                                <a onclick="mostrarProceso();" href="actaprofesional/modificacion?uuid=${acta?.uuid}&fechaExpedicion=${params.fechaExpedicion}" class="btn btn-secondary">Volver</a>
                                <button  class="btn btn-primary" onclick="javascript:enviar()">Continuar</button>

                            </p>
                        </div>
                    </form>
                </div>
                <div id="fotografias" class="col-md-6">
                    <div id="contenedorFotografia">
                        <img id="fotografiaOriginal" src="data:image/png;base64,${bphoto}" />
                    </div>
                    <!-- <div id="contenedorFotografiaRecortada">
                        <img id="fotografiaRecortada">
                    </div> -->
                </div>
            </div>
        
        </div>
        <!-- <asset:javascript src="filter-buttons.js"/> -->

    </body>
</html>
