<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Modificación de UMA</title>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/certificacion/registro" class="btn btn-primary col-md-12 my-2">
            </a>
        </content>

        <!-- Título -->
        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Modificación de UMA
                </h3>
            </div>
        </div>

        <!-- Mensajes -->
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

        <!-- Contenido -->
        <div class="container mt-2">
            <g:form method="post" class="needs-validation" novalidate="true">
                <div class="form-group">
                    <label for="valor">Valor <font color="red">*</font></label>
                    <g:textField
                        name="valor"
                        value="${uma.valor}"
                        class="form-control"
                        required="true"
                        onkeypress="return esNumeroPositivo(event, this.id)"/>
                    <div class="invalid-feedback">
                        Por favor ingrese el valor.
                    </div>
                </div>
                <div class="d-flex justify-content-end">
                    <p>
                        <g:link
                            controller="tipoTramite"
                            action="listar"
                            class="btn btn-outline-primary"
                            onclick="mostrarProceso()">
                            Cancelar
                        </g:link>
                        <g:actionSubmit
                            action="modificar"
                            value="Guardar"
                            class="btn btn-primary"/>
                    </p>
                </div>
            </g:form>
        </div>

        <asset:javascript src="validations.js"/>
        <asset:javascript src="show-process.js"/>
    </body>
</html>