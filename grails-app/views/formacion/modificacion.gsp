<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Modificar formación</title>
</head>

<body>

    <!-- Acciones -->
    <content tag="buttons">
    </content>

    <div class="row mb-5">
        <div class="col-md-12 pt-2 border-bottom">
            <h3 class="page-title pl-3">
                Modificar formación
            </h3>
        </div>
    </div>

    <g:render
        template="/mensajeError"
        model="[
            'estatus': flash.estatus,
            'mensaje': flash.mensaje
        ]"
    />

    <div class="container">
        <div class="alert alert-light" role="alert">
          Los campos marcados con * son obligatorios.
        </div>
    </div>

    <div class="container mb-5">
        <g:form action="modificar" method="post" id="${params.id}">
            <g:render
                template="formulario"
                model="[
                    'btnMensaje': 'Guardar',
                ]"
            />
        </g:form>
    </div>
</body>
</html>

