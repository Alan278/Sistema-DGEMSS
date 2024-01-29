<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Modificación Tipo de trámite</title>
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
                    Modificación de tipo de trámite
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
            <g:form method="post" id="${tipoTramite?.id}" class="needs-validation" novalidate="true">

                <div class="form-group">
                    <label for="nombre">Nombre <font color="red">*</font></label>
                    <g:textField
                        name="nombre"
                        value="${tipoTramite?.nombre}"
                        class="form-control"
                        required="true"
                    />
                    <div class="invalid-feedback">
                        Por favor ingrese el nombre.
                    </div>
                </div>

                <div class="form-group">
                    <label for="idConcepto">Id concepto<font color="red">*</font></label>
                    <g:textField
                        name="idConcepto"
                        value="${tipoTramite?.idConcepto}"
                        class="form-control"
                        required="true"
                    />
                    <div class="invalid-feedback">
                        Por favor ingrese el id del concepto.
                    </div>
                </div>

                <div class="form-group">
                    <label for="umas">Costo en UMAS <font color="red">*</font></label>
                    <g:textField
                        name="umas"
                        value="${tipoTramite?.costoUmas}"
                        class="form-control"
                        required="true"
                        onkeypress="return esNumeroPositivo(event, this.id)"
                    />
                    <div class="invalid-feedback">
                        Por favor ingrese el costo en UMAS.
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
                            class="btn btn-primary"
                        />
                    </p>
                </div>

            </g:form>
        </div>

        <asset:javascript src="validations.js"/>
        <asset:javascript src="show-process.js"/>
    </body>
</html>