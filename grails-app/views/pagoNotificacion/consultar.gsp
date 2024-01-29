<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Detalles del Trámite</title>
        <script src="jquery-3.5.1.min.js"></script>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/pagoNotificacion/registro" class="btn btn-primary col-md-12 my-2">
                Nuevo trámite
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Detalles del tramite
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
                <b>Tipo de trámite:</b>
                <medium class="text-muted">${tramite?.tipoTramite?.nombre}</medium>
            </h6>
            <hr>
            <h6 class="pb-1">
                <b>Institución:</b>
                <medium class="text-muted">${tramite?.institucion?.nombre}</medium>
            </h6>
            <h6 class="pb-1">
                <b>Fecha de recepción:</b>
                <medium class="text-muted">${tramite?.pago?.fechaRecepcionFormato}</medium>
            </h6>
            <hr>
            <h6 class="pb-1">
                <b>Linea de captura:</b>
                <medium class="text-muted">${tramite?.pago?.lineaCaptura}</medium>
            </h6>
            <h6 class="pb-1">
                <b>Folio:</b>
                <medium class="text-muted">${tramite?.pago?.folio}</medium>
            </h6>
            <h6 class="pb-1"><b>Concepto de pago:</b></h6>
                <h6><medium class="text-muted">${tramite?.pago?.concepto}</medium>
            </h6>
            <h6 class="pb-1">
                <b>Fecha de pago:</b>
                <medium class="text-muted">${tramite?.pago?.fechaPagoFormato}</medium>
            </h6>
            <h6 class="pb-1">
                <b>Hora de pago:</b>
                <medium class="text-muted">${tramite?.pago?.horaPago}</medium>
            </h6>
            <h6 class="pb-1">
                <b>Importe:</b>
                <medium class="text-muted">${tramite?.pago?.importe}</medium>
            </h6>

            <hr>

            <h6 class="pb-1 text-center">
                <b>NOTIFICACIONES</b>
            </h6>
            <table id="table" class="table">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>CURP</th>
                        <th>Alumno</th>
                        <th>Carrera</th>
                    </tr>
                </thead>
                <tBody>
                    <g:each status="i" in="${tramite?.notificacion}" var="notificacion">
                            <tr id="tr${notificacion.id}">
                                <td>${i+1}</td>
                                <td>${notificacion?.alumno?.persona?.curp}</td>
                                <td>
                                    ${notificacion?.alumno?.persona?.nombre} ${notificacion?.alumno?.persona?.primerApellido} ${notificacion?.alumno?.persona?.segundoApellido}
                                </td>
                                <td>${notificacion?.alumno?.planEstudios?.carrera?.nombre}</td>
                            </tr>
                    </g:each>
                </tBody>
            </table>



              <div class="d-flex justify-content-end mb-5">
                <p>
                    <!-- <a onclick="mostrarProceso();" href="certificacion/listar" class="btn btn-outline-primary">
                        Regresar
                    </a>
                    <a onclick="mostrarProceso();" href="tramite/modificacion/${tramite?.id}" class="btn btn-outline-primary">
                        Modificar
                    </a> -->
                    <!-- <button type="button" class="btn btn-outline-danger" data-toggle="modal" data-target="#modalEliminacion">
                        Eliminar
                    </button> -->
                </p>
              </div>
              <!-- <div class="modal fade" id="modalEliminacion" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="exampleModalLongTitle">Eliminar Trámite</h5>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div class="modal-body">
                            ¿Desea eliminar el trámite?
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                            <a onclick="mostrarProceso();" href="pago/eliminar/${tramite?.id}" class="btn btn-primary">Aceptar</a>
                        </div>
                    </div>
                </div>
              </div> -->
              <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
              <asset:javascript src="jquery-editable-select.js"/>
              <asset:javascript src="jquery-editable-select.min.js"/>
              <asset:javascript src="filter-buttons.js"/>
        </div>
    </body>
</html>