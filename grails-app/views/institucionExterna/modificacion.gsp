<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Modificar Instituciones Foráneas / Externas</title>
</head>
<body>

    <!-- Acciones -->
    <content tag="buttons">
        <a href="/institucionExterna/registro" class="btn btn-primary col-md-12 my-2">
            Nueva Institución Externa
        </a>
    </content>

    <div class="row mb-5">
        <div class="col-md-12 pt-2 border-bottom">
            <h3 class="page-title pl-3">
                Modificación de la Institución Foráneas / Externas
            </h3>
        </div>
    </div>
    <div class="container">
        <g:if test="${flash.mensaje}">
            <g:if test="${!flash.estatus}">
                <div class="alert alert-danger" role="alert">
                    ${flash.mensaje}
                </div>
            </g:if>
        </g:if>
    </div>

    <div class="container">
        <div class="alert alert-light" role="alert">
          Los campos marcados con * son obligatorios.
        </div>
    </div>

    <div class="container mb-5">
        <form action="/institucionExterna/modificar/${institucion?.id}" class="needs-validation" novalidate>
                <div class="form-group" >
                    Nombre <font color="red">*</font>
                    <br/>
                    <input name="nombre" class="form-control" value="${institucion?.nombre}" required>
                    <div class="invalid-feedback">
                        Por favor ingrese el nombre.
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group col-md-6">
                        Nombre Comercial
                        <br/>
                        <input name="nombreComercial" class="form-control" value="${institucion?.nombreComercial}">
                    </div>
                    <div class="form-group col-md-6">
                        Raz&oacute;n Social
                        <br/>
                        <input name="razonSocial" class="form-control" value="${institucion?.razonSocial}">
                    </div>
                    <div class="form-group col-md-6">
                        Clave DGP
                        <br/>
                        <input name="claveDgp" class="form-control" value="${institucion?.claveDgp}">
                    </div>
                    <div class="form-group col-md-6">
                        Clave CCT
                        <br/>
                        <input name="claveCt" class="form-control" value="${institucion?.claveCt}">
                    </div>
                    <div class="form-group col-md-6">
                        Correo
                        <br/>
                        <input name="correoElectronico" class="form-control" value="${institucion?.correoElectronico}">
                    </div>
                    <div class="form-group col-md-6">
                        Telefono
                        <br/>
                        <input name="telefono" class="form-control" value="${institucion?.telefono}" onkeypress="return esNumeroTelefonico(event, this.id)">
                    </div>
                </div>
                 </br>
                 <div class="form-row justify-content-end">
                    <p>
                        <a onclick="mostrarProceso();" href="institucionExterna/listar" class="btn btn-secondary">Cancelar</a>
                        <button type="submit" class="btn btn-primary">Guardar</button>
                    </p>
                </div>
                <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
                <asset:javascript src="jquery-editable-select.js"/>
                <asset:javascript src="jquery-editable-select.min.js"/>
                <asset:javascript src="filter-buttons.js"/>
                <asset:javascript src="validations.js"/>
        </form>
    </div>
</body>
</html>