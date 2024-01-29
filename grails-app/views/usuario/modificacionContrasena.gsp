<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Perfil del Usuario</title>
        <script src="jquery-3.5.1.min.js"></script>
    </head>
    <body>
        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Cambio de Contraseña
                </h3>
            </div>
        </div>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="usuario/modificacionContrasena" class="btn btn-primary col-md-12 my-2">
                Cambiar contraseña
            </a>
        </content>

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
            <form id="form" action="/usuario/modificarContrasena" method="post" class="needs-validation" novalidate>
                <div class="form-group">
                    <label for="contrasenaActual">Contraseña actual <font color="red">*</font></label>
                    <input id="contrasenaActual" name="contrasenaActual" type="password" class="form-control"  value="${params?.contrasenaActual}" required>
                    <div id="contrasenaActualInvalida" class="invalid-feedback">
                        Por favor ingrese el nombre del curso.
                    </div>
                </div>
                <div class="form-group">
                    <label for="nuevaContrasena">Nueva contraseña <font color="red">*</font></label>
                    <input id="contrasena" name="nuevaContrasena" type="password" class="form-control"  value="${params?.nuevaContrasena}" required>
                    <small class="form-text text-muted">Use 10 o más caracteres con una combinación de letras mayúsculas, minúsculas, números y símbolos</small>
                    <div id="contrasenaInvalida" class="invalid-feedback">
                        Por favor ingrese el nombre del curso.
                    </div>
                </div>
                <div class="form-group">
                    <label for="nuevaContrasena2">Vuelava a escribir la nueva contraseña <font color="red">*</font></label>
                    <input id="contrasena2" name="nuevaContrasena2" type="password" class="form-control"  value="${params?.nuevaContrasena2}" required>
                    <div id="contrasenaInvalida2" class="invalid-feedback">
                        Por favor ingrese el nombre del curso.
                    </div>
                </div>

                <div class="form-row justify-content-end">
                    <p>
                        <a onclick="mostrarProceso();" href="usuario/perfil" class="btn btn-secondary">Cancelar</a>
                        <a onclick="validatePassword()" class="btn btn-primary">Guardar</a>
                    </p>
                </div>

            </form>
        </div>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <asset:javascript src="passwordValidation.js"/>
    </body>
</html>