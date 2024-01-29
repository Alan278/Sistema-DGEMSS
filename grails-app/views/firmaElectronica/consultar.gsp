<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Detalles del Plan de Estudios</title>
        <script src="jquery-3.5.1.min.js"></script>
    </head>
    <body>

       <!-- Acciones -->
        <content tag="buttons">
            <a href="/firmaElectronica/registroCer" class="btn btn-primary col-md-12 my-2">
                Registrar certificado
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Firma electrónica
                </h3>
            </div>
        </div>

        <% def tieneFirmasActivas = usuario.persona.firmasElectronicas.any{ firma -> !firma.expiro()}%>

        <div class="container">
            <g:if test="${!tieneFirmasActivas}">
                <div class="alert alert-warning" role="alert">
                    No cuenta con un certificado (.cer) activo. Por favor registre uno para seguir firmando. <a href="/firmaElectronica/registroCer">Registrar</a>
                </div>
            </g:if>
        </div>

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
        
        <div class="container mb-5">
            <div class="row">
                <g:each in="${usuario?.persona?.firmasElectronicas}" var="firma">
                    <div class="col-4 col-md-4 col-sm-6">
                        <div class="card" style="height: 100%;" >
                            <div class="card-header pt-3 pb-0">
                                <h6 class="card-title" align="center">
                                    <b>Datos del certificado</b>
                                    <g:if test="${usuario?.persona?.firmasElectronicas}">
                                        <a type="button" class="text-danger" data-toggle="modal" data-target="#modalEliminacionCer_${firma.id}" >
                                            <i class="bi bi-trash"></i>
                                        </a>
                                    </g:if>
                                </h4>
                            </div>
                            <div class="card-body pt-2 pb-0">
                                <div class="card-text" align="center">
                                        <p class="my-1" align="center"> 
                                            <b>Estatus:</b>
                                            <g:if test="${firma.expiro()}">
                                                <p class="text-danger my-0">EXPIRADO</p>
                                            </g:if>
                                            <g:else>
                                                <p class="text-success my-0">VIGENTE</p>
                                            </g:else>
                                        </p>
                                        <p class="my-1" align="center"> 
                                            <b>Número serie:</b><br> 
                                            ${firma?.numeroSerieCer}
                                        </p>
                                        <p class="my-1" align="center"> 
                                            <b>Curp: </b><br> 
                                            ${firma?.curpCer}
                                        </p>
                                        <p class="my-1" align="center"> 
                                            <b>Nombre: </b><br>
                                            ${firma?.nombreCer}
                                        </p>
                                        <p class="my-1" align="center">
                                            <b>Correo electronico: </b><br>
                                            ${firma?.correoElectronicoCer}
                                        </p>
                                        <p class="my-1" align="center">
                                            <b>RFC: </b><br>
                                            ${firma?.rfcCer}
                                        </p>
                                        <p class="my-1" align="center">
                                            <b>Valido desde: </b><br>
                                            ${firma?.validoDesdeCer}
                                        </p>
                                        <p class="my-1" align="center">
                                            <b>Valido hasta: </b><br>
                                            ${firma?.validoHastaCer}
                                        </p>
                                        <p class="my-1" align="center">
                                            <b>Archivo .key: </b><br>
                                            <g:if test="${firma?.archivoKey}">
                                                REGISTRADO.
                                                <a type="button" class="link" data-toggle="modal" data-target="#modalEliminacionKey_${firma.id}" >
                                                    Eliminar
                                                </a>
                                            </g:if>
                                            <g:else>
                                                NO REGISTRADO.
                                                <g:if test="${!firma?.expiro()}">
                                                    <a href="/firmaElectronica/registroKey/${firma?.id}">
                                                        Subir Key
                                                    </a>
                                                </g:if>
                                            </g:else>
                                        </p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="modal fade" id="modalEliminacionKey_${firma.id}" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
                        <div class="modal-dialog modal-dialog-centered" role="document">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title" id="exampleModalLongTitle">Eliminar clave privada</h5>
                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                        <span aria-hidden="true">&times;</span>
                                    </button>
                                </div>
                                <div class="modal-body">
                                    ¿Desea eliminar la clave privada?
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                                    <a onclick="mostrarProceso();"  href="/firmaElectronica/eliminarKey/${firma.id}" class="btn btn-primary">Aceptar</a>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="modal fade" id="modalEliminacionCer_${firma.id}" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
                        <div class="modal-dialog modal-dialog-centered" role="document">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title" id="exampleModalLongTitle">Eliminar certificado</h5>
                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                        <span aria-hidden="true">&times;</span>
                                    </button>
                                </div>
                                <div class="modal-body">
                                    ¿Desea eliminar el certificado?
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                                    <a onclick="mostrarProceso();"  href="/firmaElectronica/eliminarCer/${firma.id}" class="btn btn-primary">Aceptar</a>
                                </div>
                            </div>
                        </div>
                    </div>

                </g:each>
            </div>
        </div>

        

        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <asset:javascript src="jquery-editable-select.js"/>
        <asset:javascript src="jquery-editable-select.min.js"/>
        <asset:javascript src="filter-buttons.js"/>
        <script>
            $(function () {
                $('[data-tooltip="tooltip"]').tooltip()
            })
        </script>
    </body>
</html>