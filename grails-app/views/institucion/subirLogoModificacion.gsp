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
        <asset:javascript src="logo-style.js"/>
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
                width: 354px;
                height: 402px;
                object-fit: contain;
            }
        </style>
    </head>
    <body>
        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Subir logo
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

        <div class="container my-5 ml-3">
            <div class="row align-items-center">
                <div class="col-md-6">
                    <form id="formulario" action="/institucion/modificar/${institucion?.id}" method="POST" class="needs-validation" novalidate>
                        <div class="row mb-3">
                            <input type="text" id="logo" name="logo" value="${params.logo}" hidden>
                            <div class="custom-file">
                                <label class="custom-file-label" for="customFile">Elija un foto</label>
                                <input type="file" class="custom-file-input" name="seleccionar" accept="image/png, image/jpg, image/jpeg" >
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
                                <a onclick="mostrarProceso();" href="institucion/listar" class="btn btn-secondary">Cancelar</a>
                                <a onclick="mostrarProceso();" href="institucion/modificacion/${institucion.id}" class="btn btn-secondary">Volver</a>
                                <button  class="btn btn-primary" onclick="javascript:enviar()">Continuar</button>

                            </p>
                        </div>
                    </form>
                </div>
                <div id="fotografias" class="col-md-6">
                    <div id="contenedorFotografia">
                        <img id="fotografiaOriginal" src="data:image/png;base64,${logo}">
                    </div>
                </div>
            </div>
        
        </div>

    </body>
</html>
