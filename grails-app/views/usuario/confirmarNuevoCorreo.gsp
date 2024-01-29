<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Lista de Usuarios</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Confirmar nuevo correo electrónico
                </h3>
            </div>
        </div>

        <div class="container">
            <g:if test="${resultado.estatus}">
                <div class="alert alert-success" role="alert">
                    ${resultado.mensaje}
                </div>
            </g:if>
            <g:else>
                <div class="alert alert-danger" role="alert">
                    ${resultado.mensaje}
                </div>
            </g:else>
        </div>
    </body>
</html>