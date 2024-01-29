<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Consultar Instituciones</title>
    <style>
        #contenedorFotografia img, #contenedorFotografiaRecortada img{
                width: 200px;
                height: auto;
            }
    </style>
</head>
<body>

    <!-- Acciones -->
    <content tag="buttons">
        <sec:ifNotGranted roles='ROLE_AUTENTICADOR_DGEMSS'>
            <a href="/institucion/registro" class="btn btn-primary col-md-12 my-2">
                Nueva institución
            </a>
            <a href="/institucion/subirExcel" class="btn btn-primary col-md-12 my-2">
                Cargar instituciones
            </a>
        </sec:ifNotGranted>
        <sec:ifAnyGranted roles='ROLE_AUTENTICADOR_DGEMSS'>
            <a href="#" class="btn btn-primary col-md-12 my-2 disabled">
            </a>
        </sec:ifAnyGranted>
    </content>

    <div class="row mb-4">
        <div class="col-md-12 pt-2 border-bottom">
            <h3 class="page-title pl-3">
                Detalles de la Institución
            </h3>
        </div>
    </div>
    <div class="row">
        <g:if test="${flash.mensaje}">
            <g:if test="${!flash.estatus}">
                <div class="alert alert-danger" role="alert">
                    ${flash.mensaje}
                </div>
            </g:if>
        </g:if>
    </div>

    <div class="container mb-5">
        <div class="row">
            <div class="col-md-8">
                <h6 class="pb-3">
                    <medium class="text-muted">${institucion?.nombre}</medium><br>
                </h6>
                <h6 class="pb-1">
                    <b> Nombre Comercial:</b>
                    <medium class="text-muted">${institucion?.nombreComercial}</medium>
                </h6>
                <h6 class="pb-1">
                    <b>Raz&oacute;n Social:</b>
                    <medium class="text-muted">${institucion?.razonSocial}</medium>
                </h6>
                <h6 class="pb-1">
                    <b>Clave DGP:</b>
                    <medium class="text-muted">${institucion?.claveDgp}</medium>
                </h6>
                <h6 class="pb-1">
                    <b>Clave CCT:</b>
                    <medium class="text-muted">${institucion?.claveCt}</medium>
                </h6>
                <h6 class="pb-1">
                    <b>Correo:</b>
                    <medium class="text-muted">${institucion?.correoElectronico}</medium>
                </h6>
                <h6 class="pb-1">
                    <b>Teléfono:</b>
                    <medium class="text-muted">${institucion?.telefono}</medium>
                </h6>
                <h6 class="pb-2">
                    <b>Número de carreras:</b>
                    <medium class="text-muted"><a onclick="mostrarProceso();" href="/carrera/listar?institucionId=${institucion?.id}">${params.numeroCarreras}</a></medium>
                </h6>
                <hr>
                <h6 class="pb-1 pt-2">
                    <b>Calle:</b>
                    <medium class="text-muted">${institucion?.domicilio?.calle}</medium>
                </h6>
                <h6 class="pb-1">
                    <b>N&uacute;mero Interior:</b>
                    <medium class="text-muted">${institucion?.domicilio?.numeroInterior}</medium>
                </h6>
                <h6 class="pb-1">
                    <b>N&uacute;mero Exterior:</b>
                    <medium class="text-muted">${institucion?.domicilio?.numeroExterior}</medium>
                </h6>
                <h6 class="pb-1">
                    <b>Colonia o Asentamiento:</b>
                    <medium class="text-muted">${institucion?.domicilio?.asentamiento}</medium>
                </h6>
                <h6 class="pb-1">
                    <b>Localidad:</b>
                    <medium class="text-muted">${institucion?.domicilio?.localidad}</medium>
                </h6>
                <h6 class="pb-1">
                    <b>Municipio:</b>
                    <medium class="text-muted">${institucion?.domicilio?.municipio}</medium>
                </h6>
                <h6 class="pb-1">
                    <b>Estado:</b>
                    <medium class="text-muted">${institucion?.domicilio?.estado}</medium>
                </h6>
                <h6 class="pb-1">
                    <b>C&oacute;digo Postal:</b>
                    <medium class="text-muted">${institucion?.domicilio?.codigoPostal}</medium>
                </h6>
                <h6 class="pb-1">
                    <b>Referencias:</b>
                    <medium class="text-muted">${institucion?.domicilio?.referencias}</medium>
                </h6>
                <h6 class="pb-1">
                    <b>Latitud:</b>
                    <medium class="text-muted">${institucion?.domicilio?.latitud}</medium>
                    <input id='latitud' type="text" value="${institucion?.domicilio?.latitud}" hidden>
                </h6>
                <h6 class="pb-3">
                    <b>Longitud:</b>
                    <medium class="text-muted">${institucion?.domicilio?.longitud}</medium>
                    <input id='longitud' type="text" value="${institucion?.domicilio?.longitud}" hidden>
                </h6>
            </div>
            <div class="col-md-4 d-flex justify-content-end">
                <g:if test="${logo}">
                    <div id="contenedorFotografia">
                        <img id="fotografiaOriginal" class="img-thumbnail" src="data:image/png;base64,${logo}" />
                    </div>
                </g:if>
            </div>
        </div>

        <div class="mb-3">
            <div class="map" id="map"></div>
        </div>

        <div class="form-row justify-content-end">
            <p>
                <a onclick="mostrarProceso();" href="carrera/registro?institucionId=${institucion?.id}" class="btn btn-primary">
                    Nueva Carrera
                </a>
                <a onclick="mostrarProceso();" href="institucion/modificacion/${institucion?.id}" class="btn btn-primary">
                    Modificar
                </a>
                <button type="button" class="btn btn-danger" data-toggle="modal" data-target="#modalEliminacion">
                    Eliminar
                </button>
            </p>
        </div>
        <div class="modal fade" id="modalEliminacion" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="exampleModalLongTitle">Eliminar Institución</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        ¿Desea eliminar la institución?
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                        <a onclick="mostrarProceso();" href="institucion/eliminar/${institucion?.id}" class="btn btn-primary">Aceptar</a>
                    </div>
                </div>
            </div>
        </div>
        <asset:javascript src="map-show-location.js"/>
        <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBhfc1fX6WWG08xM_eBtX1m7NaGsIz1ACU&callback=initMap&libraries=&v=weekly" async></script>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <asset:javascript src="jquery-editable-select.js"/>
        <asset:javascript src="jquery-editable-select.min.js"/>
        <asset:javascript src="filter-buttons.js"/>
    </div>
</body>
</html>

