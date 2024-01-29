<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Detalles del Personal</title>
        <script src="jquery-3.5.1.min.js"></script>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/personalInstitucional/registro" class="btn btn-primary col-md-12 my-2">
                Nuevo Personal
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Detalles del Personal
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
            <h5> <b> Datos de la Institución </b> </h5>
            <br/>
            <div class="form-group mb-4" >
                <h5> <b> Institución </b> </h5>
                <h6 class="ml-3">${personalInstitucional?.institucion?.nombre} </h6>
            </div>
            <div class="form-group" >
                <h5> <b> Cargo </b> </h5>
                <h6 class="ml-3">${personalInstitucional?.cargoInstitucional?.nombre} </h6>
            </div>
            <div class="form-group" >
                <h5> <b> Nombre del Cargo </b> </h5>
                <h6 class="ml-3">${personalInstitucional?.nombreCargo} </h6>
            </div>
            <hr>
            <h5> <b> Datos personales </b> </h5>
            <br/>
            <div class="form-group" >
                <h5> <b> Nombre </b> </h5>
                <h6 class="ml-3">${personalInstitucional?.persona?.nombre} ${personalInstitucional?.persona?.primerApellido} ${personalInstitucional?.persona?.segundoApellido}</h6>
            </div>
            <div class="form-row mb-2">
                <div class="form-group col-md-6" >
                    <h5> <b> CURP </b> </h5>
                    <h6 class="ml-3">${personalInstitucional?.persona?.curp} </h6>
                </div>
                <div class="form-group col-md-6" >
                    <h5> <b> RFC </b> </h5>
                    <h6 class="ml-3">${personalInstitucional?.persona?.rfc} </h6>
                </div>
            </div>
            <div class="form-row mb-2">
                <div class="form-group col-md-6" >
                    <h5> <b> Entidad de Nacimiento </b> </h5>
                    <h6 class="ml-3">${personalInstitucional?.persona?.entidadNacimiento} </h6>
                </div>
                <div class="form-group col-md-6" >
                    <h5> <b> Fecha de Nacimiento </b> </h5>
                    <h6 class="ml-3">${personalInstitucional?.persona?.fechaNacimiento} </h6>
                </div>
            </div>
            <div class="form-row mb-2">
                <div class="form-group col-md-6" >
                    <h5> <b> Correo electrónico </b> </h5>
                    <h6 class="ml-3">${personalInstitucional?.persona?.correoElectronico} </h6>
                </div>
                <div class="form-group col-md-6" >
                    <h5> <b> Teléfono </b> </h5>
                    <h6 class="ml-3">${personalInstitucional?.persona?.telefono} </h6>
                </div>
            </div>
             <div class="form-row mb-2">
                 <div class="form-group col-md-6" >
                    <h5> <b> Sexo </b> </h5>
                    <h6 class="ml-3">${personalInstitucional?.persona?.sexo} </h6>
                  </div>
             </div>
            <div class="form-row justify-content-end mb-5">
                <p>
                    <a onclick="mostrarProceso();" href="personalInstitucional/modificacion/${personalInstitucional?.id}" class="btn btn-primary">
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
                            <h5 class="modal-title" id="exampleModalLongTitle">Eliminar Personal</h5>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div class="modal-body">
                            ¿Desea eliminar el personal?
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                            <a onclick="mostrarProceso();" href="personalInstitucional/eliminar/${personalInstitucional?.id}" class="btn btn-primary">Aceptar</a>
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