<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Perfil del Usuario</title>
        <script src="jquery-3.5.1.min.js"></script>
        <style>
            h6{
                padding-bottom: 5px;
            }
        </style>
    </head>
    <body>
        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Perfil del Usuario
                </h3>
            </div>
        </div>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="usuario/modificacionContrasena" class="btn btn-primary col-md-12 my-2">
                Cambiar contraseña
            </a>
        </content>

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

        <div class="container">
            <div class="row">
                <div class="col-6">
                    <h6>
                        <b> Curp:</b>
                        <medium class="text-muted">${usuario?.persona?.curp}</medium>
                    </h6>
                    <h6>
                        <b> Nombre:</b>
                        <medium class="text-muted">${usuario?.persona?.nombreCompleto}</medium>
                    </h6>
                    <h6>
                        <b> Entidad de nacimiento:</b>
                        <medium class="text-muted">${usuario?.persona?.entidadNacimiento}</medium>
                    </h6>
                    <h6>
                        <b> Fecha de nacimiento:</b>
                        <medium class="text-muted">${usuario?.persona?.fechaNacimiento}</medium>
                    </h6>
                    <h6>
                        <b> Sexo:</b>
                        <medium class="text-muted">${usuario?.persona?.sexo}</medium>
                    </h6>
                    <h6>
                        <b> Correo electrónico:</b>
                        <medium class="text-muted">${usuario?.persona?.correoElectronico}</medium>
                    </h6>
                    <h6>
                        <b> Número de teléfono:</b>
                        <medium class="text-muted">${usuario?.persona?.telefono}</medium>
                    </h6>
                </div>
                <div class="col-6">
                    <h6>
                        <b> Cargo:</b>
                        <medium class="text-muted">${usuario?.cargo}</medium>
                    </h6>
                    <h6>
                        <b> Nombre de usuario:</b>
                        <medium class="text-muted">${usuario?.username}</medium>
                    </h6>
                    <h6> <b> Función de la plataforma </b> </h6>
                    <medium class="text-muted">
                        <dd>
                            <g:each in="${usuario?.roles}" var="rol">
                                <li>
                                    ${rol.rol.nombre}
                                </li>
                            </g:each>
                        </dd>
                    </medium>
                    <g:if test="${usuario?.instituciones}">
                        <h6> <b> Instituciones </b> </h6>
                        <medium class="text-muted">
                            <dd>
                                <g:each in="${usuario?.instituciones}" var="registro"> 
                                   <li>
                                       ${registro.institucion.nombre}
                                   </li>
                                </g:each>
                            </dd>
                        </medium>
                    </g:if>
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