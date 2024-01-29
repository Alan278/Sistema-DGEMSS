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
                    Restablece tu contraseña
                </h3>
            </div>
        </div>


        <div class="container">
            <g:if test="${flash.mensaje}">
                <g:if test="${flash.estatus}">
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


        <div class="container p-0">
            <div class="col-md-5">
                <div class="card" style="height: 100%;" >
                    <div class="card-body py-3 px-4">
                        <div class="card-text">

                            <form id="form" action="/usuario/enviarCorreoRecuperacion"  method="post">

                                <input type="text" name="token" value="${params.token}" hidden>

                                <div class="form-group">
                                    <label for="correo">
                                        Ingrese su correo electrónico. <font color="red">*</font></label>
                                    <input id="correo" name="correo" type="text" class="form-control" required>
                                </div>

                                <div class="row m-1 justify-content-end">
                                    <p>
                                        <button class="btn btn-primary">Continuar</button>
                                    </p>
                                </div>

                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <asset:javascript src="passwordValidation.js"/>
    </body>
</html>