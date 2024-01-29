<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Consultar Instituciones Foráneas / Externas</title>
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
                    Detalles de la Institución Foráneas / Externas
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
            <div class="form-group" >
                <h5><b>Nombre:</b></h5>
                <h6>${institucion?.nombre}</h6>
            </div>
            <div class="form-row">
                <div class="form-group col-md-6">
                    <h5><b>Nombre Comercial:</b></h5>
                    <h6>${institucion?.nombreComercial}</h6>
                </div>
                <div class="form-group col-md-6">
                    <h5><b>Raz&oacute;n Social:</b></h5>
                    <h6>${institucion?.razonSocial}</h6>
                </div>
                <div class="form-group col-md-6">
                    <h5><b>Clave DGP:</b></h5>
                    <h6>${institucion?.claveDgp}</h6>
                </div>
                <div class="form-group col-md-6">
                    <h5><b>Clave CCT:</b></h5>
                    <h6>${institucion?.claveCt}</h6>
                </div>
                <div class="form-group col-md-6">
                    <h5><b>Correo:</b></h5>
                    <h6>${institucion?.correoElectronico}</h6>
                </div>
                <div class="form-group col-md-6">
                    <h5><b>Teléfono:</b></h5>
                    <h6>${institucion?.telefono}</h6>
                </div>
            </div>
            <div class="form-group ">
            <h5><b>Número de carreras:</b></h5>
                <a onclick="mostrarProceso();" href="/carreraExterna/listar?institucionId=${institucion?.id}">
                    <h6>${params.numeroCarreras}</h6>
                </a>
            </div>
            <br>
            <div class="form-row justify-content-end">
                <p>
                    <a onclick="mostrarProceso();" href="carreraExterna/registro?institucionId=${institucion?.id}" class="btn btn-primary">
                        Nueva Carrera
                    </a>
                    <a onclick="mostrarProceso();" href="institucionExterna/modificacion/${institucion?.id}" class="btn btn-primary">
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
                            <h5 class="modal-title" id="exampleModalLongTitle">Eliminar Institución Foránea / Externa</h5>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div class="modal-body">
                            ¿Desea eliminar la institución seleccionada?
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                            <a onclick="mostrarProceso();" href="institucionExterna/eliminar/${institucion?.id}" class="btn btn-primary">Aceptar</a>
                        </div>
                    </div>
                </div>
            </div>
            <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
            <asset:javascript src="jquery-editable-select.js"/>
            <asset:javascript src="jquery-editable-select.min.js"/>
            <asset:javascript src="filter-buttons.js"/>
        </div>
</body>
</html>

