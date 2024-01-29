<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Establecer contraseña</title>
    </head>
    <body>
        <div class="container mb-4">
            <div class="col-md-12 pt-2 mb-3 border-bottom">
                <h3 class="page-title pl-3">
                    Confirmar cuenta
                </h3>
            </div>
        </div>


        <div class="container">
            <g:if test="${flash.mensaje}">
                <g:if test="${estatus == '1' || estatus == '4'}">
                    <div class="alert alert-success" role="alert">
                        ${flash.mensaje}
                    </div>
                </g:if>
                <g:else>
                    <div class="alert alert-danger" role="alert">
                        ${flash.mensaje}
                    </div>
                </g:else>
            </g:if>
        </div>

        <g:if test="${estatus == '1' || estatus == '3'}">

            <div class="container p-0 mb-5">
                <div class="col-md-6">
                    <div class="card" style="height: 100%;" >
                        <div class="card-body py-3 px-3">
                            <div class="card-text">
                                <div class="m-1 mb-4">
                                    <h6>
                                        <b><medium class="text-muted">${usuario?.persona?.nombreCompleto}</medium></b>
                                    </h6>
                                    <h6>
                                        <b> usuario:</b>
                                        <medium class="text-muted">${usuario?.username}</medium>
                                    </h6>
                                </div>

                                <g:if test="${estatus == '1' || estatus == '3'}">
                                    <div class="alert alert-primary" role="alert">
                                        Por favor establezca una contraseña para poder confirmar su cuenta.
                                    </div>
                                </g:if>
                                <form id="form" action="/usuario/confirmarUsuario"  method="post">

                                    <input type="text" name="token" value="${params.token}" hidden>

                                    <div class="form-group">
                                        <label for="contrasena">Contraseña <font color="red">*</font></label>
                                        <input id="contrasena" name="contrasena" type="password" class="form-control"  value="${params?.contrasena}" min="8" max="15" required>
                                        <small class="form-text text-muted">Use 10 o más caracteres con una combinación de letras mayúsculas, minúsculas, números y símbolos</small>
                                        <div id="contrasenaInvalida" class="invalid-feedback">
                                            Por favor ingrese una contraseña válida.
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label for="contrasena2">Vuelva a escribir la contraseña <font color="red">*</font></label>
                                        <input id="contrasena2" name="contrasena2" type="password" class="form-control"  value="${params?.contrasena2}" required>
                                        <div id="contrasenaInvalida2" class="invalid-feedback">
                                            Por favor ingrese la confirmación de la contraseña.
                                        </div>
                                    </div>

                                    <div class="row m-1 justify-content-end">
                                        <p>
                                            <a onclick="validatePassword()" class="btn btn-primary">Guardar</a>
                                        </p>
                                    </div>

                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </g:if>

        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <asset:javascript src="passwordValidation.js"/>
    </body>
</html>