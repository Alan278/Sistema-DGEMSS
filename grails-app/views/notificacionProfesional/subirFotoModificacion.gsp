<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Expedición de Notificacion Profesional</title>
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
                                <p class="m-0"> <b> Institución: </b>${notificacion.alumno?.carrera?.institucion?.nombre} </p>
                                <p class="m-0"> <b> Carrera: </b> ${notificacion.alumno?.carrera?.nombre}</p>
                                <p class="m-0"> <b> Matrícula: </b> ${notificacion?.alumno?.matricula}</p>
                                <p class="m-0"> <b> Alumno: </b> ${notificacion.alumno?.persona?.nombre} ${notificacion.alumno?.persona?.primerApellido} ${notificacion.alumno?.persona?.segundoApellido}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="container mb-5">
            <div class="row justify-content-center">
                <div class="col-md-6">
                    <form id="formulario" action="/NotificacionProfesional/modificarFotografia" method="POST" class="needs-validation" novalidate>
                        <input id="uuid" name="uuid" type="text" value="${notificacion.uuid}" hidden>
                            <label class="d-block text-secondary">Opción de titulación</label>
                            <select name="opctitulacionId" id="opctitulacionId" required="required" class="form-control mb-2">
                                <g:each in="${opctitulaciones}" var= "opctitulacion">
                                    <option value="${opctitulacion.id}">${opctitulacion.nombre}</option>
                                </g:each>
                            </select>
                           
                            <label class="d-block text-secondary">Tipo de documento</label>
                            <select name="docId" id="docId" required="required" class="form-control mb-2">
                                <g:each in="${tipodocumentos}" var= "doc">
                                    <option value="${doc.id}">${doc.nombre}</option>
                                </g:each>
                            </select>
                            <label class="d-block text-secondary">Titulo del proyecto</label>
                            <input type="text" value="${notificacion.titulo}" name="titulo" id="titulo" required="required" class="form-control mb-2" placeholder="Titulo" />
                            <label class="d-block text-secondary">Nombre del presidente</label>
                            <input type="text" value="${notificacion.presidente}" name="presidente" id="presidente" required="required" class="form-control mb-2" placeholder="Presidente" />
                            <label class="d-block text-secondary">Nombre del secretario</label>
                            <input type="text" value="${notificacion.secretario}" name="secretario" id="secretario" required="required" class="form-control mb-2" placeholder="Secretario" />
                            <label class="d-block text-secondary">Nombre del vocal</label>
                            <input type="text" value="${notificacion.vocal}" name="vocal" id="vocal" required="required" class="form-control mb-2" placeholder="Vocal" />
                            <br>
                        <div class="row mb-3">
                            
                            <input type="text" id="foto" name="foto" value="${params.foto}" hidden>
                            <div class="custom-file">
                                <label class="custom-file-label" for="customFile">Elija un foto</label>
                                <input type="file" class="custom-file-input" name="seleccionar" accept="image/png, image/jpg, image/jpeg" >
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
                                <a onclick="mostrarProceso();" href="NotificacionProfesional/listarNotificaciones" class="btn btn-secondary">Cancelar</a>
                                <a onclick="mostrarProceso();" href="NotificacionProfesional/modificacion?uuid=${notificacion?.uuid}&fechaExpedicion=${params.fechaExpedicion}" class="btn btn-secondary">Volver</a>
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
