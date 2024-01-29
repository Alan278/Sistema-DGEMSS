<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Detalles del Usuario</title>
        <script src="jquery-3.5.1.min.js"></script>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/usuario/registro" class="btn btn-primary col-md-12 my-2">
                Nuevo Usuario
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Detalles del Usuario
                </h3>
            </div>
        </div>

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
            <h6 class="pb-1">
                <b> Nombre:</b>
                <medium class="text-muted">${usuario?.persona?.nombreCompleto}</medium><br>
            </h6>
            <h6 class="pb-1">
                <b> Usuario:</b>
                <medium class="text-muted">${usuario?.username} </medium>
            </h6>
            <hr>
            <h6 class="pb-1">
                <b> CURP:</b>
                <medium class="text-muted">${usuario?.persona?.curp} </medium>
            </h6>
            <h6 class="pb-1">
                <b> Entidad de Nacimiento:</b>
                <medium class="text-muted">${usuario?.persona?.entidadNacimiento} </medium>
            </h6>
            <h6 class="pb-1">
                <b> Fecha de Nacimiento:</b>
                <medium class="text-muted">${usuario?.persona?.fechaNacimiento} </medium>
            </h6>
            <h6 class="pb-1">
                <b> Sexo:</b>
                <medium class="text-muted">${usuario?.persona?.sexo} </medium>
            </h6>
            <h6 class="pb-1">
                <b> Correo electrónico:</b>
                <medium class="text-muted">${usuario?.persona?.correoElectronico} </medium>
            </h6>
            <h6 class="pb-1">
                <b> Teléfono:</b>
                <medium class="text-muted">${usuario?.persona?.telefono} </medium>
            </h6>
            <hr>
            <h6 class="pb-1">
                <b>  Cargo:</b>
                <medium class="text-muted">${usuario?.cargo}</medium>
            </h6>
            <h6 class="pb-1">
                <b>  Título:</b>
                <medium class="text-muted">${usuario?.persona?.titulo}</medium>
            </h6>
            <h6 class="pb-1"> <b>  Función de la plataforma:</b> </h6>
            <medium class="text-muted">
                <dd>
                    <g:each in="${usuario?.roles}" var="rol">
                        <li>
                            ${rol.rol.nombre}
                        </li>
                    </g:each>
                </dd>
            </medium>
            <g:if test="${usuario.instituciones}">
                <h6 class="pb-1"> <b>  Institución:</b> </h6>
                <medium class="text-muted">
                    <dd>
                        <g:each in="${usuario?.instituciones}" var="registro">
                            <li>
                                ${registro?.institucion?.nombre}
                            </li>
                        </g:each>
                    </dd>
                </medium>
            </g:if>

              <div class="form-row justify-content-end mb-5">
                <p>
                    <a onclick="mostrarProceso();" href="usuario/modificacion/${usuario?.id}" class="btn btn-outline-primary">
                        Modificar
                    </a>
                    <button type="button" class="btn btn-outline-danger" data-toggle="modal" data-target="#modalEliminacion">
                        Eliminar
                    </button>
                </p>
              </div>
              <div class="modal fade" id="modalEliminacion" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="exampleModalLongTitle">Eliminar Usuario</h5>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div class="modal-body">
                            ¿Desea eliminar el usuario?
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                            <a onclick="mostrarProceso();" href="usuario/eliminar/${usuario?.id}" class="btn btn-primary">Aceptar</a>
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