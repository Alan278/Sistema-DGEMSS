<!doctype html>
<html>
     <head>
        <meta name="layout" content="main"/>
        <title> Listado de Firmas de Constancias</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/constanciaservicio/firmarConstancias/${institucion?.id}" class="btn btn-primary col-md-12 my-2">
                Firmar todos los constancias
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Listado de constancias a firmar
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

        <div class="container mt-4">
            <div class="card px-0 pb-0">
                <div class="card-body">
                    <h6>
                        <b> Institución:</b>
                        <medium class="text-muted">${institucion?.nombre}</medium>
                    </h6>
                    <h6>
                        <b>Clave CT:</b>
                        <medium class="text-muted">${institucion?.claveCt}</medium>
                    </h6>
                    <h6>
                        <b>Clave DGP:</b>
                        <medium class="text-muted">${institucion?.claveDgp}</medium>
                    </h6>
                    <h6>
                        <b>Número de constancias:</b>
                        <medium class="text-muted">${conteo}</medium>
                    </h6>
                </div>
            </div>
        </div>

        <div class="container">
            <form id="form-busqueda" action="/constanciaservicio/listarFirmasConstancias" class="mt-4">
                
                <div class="row mt-4">
                    <div class="col-md-6">
                    </div>
                    <div class="col-md-6">
                        <div class="input-group mb-3">
                            <input id="search" type="text" class="form-control" name="search" value="${params?.search}" placeholder="Nombre / CURP / Matrícula del alumno">
                            <div class="input-group-prepend">
                                <input id="buscar" type="submit" class="btn btn-primary" value="Buscar"/>
                            </div>
                            <g:if test="${params.search}">
                                <div class="input-group-append">
                                    <button id="limpiar-search" class="btn btn-outline-secondary">
                                        <i class="fa fa-close"></i>
                                    </button>
                                </div>
                            </g:if>
                        </div>
                    </div>
                </div>
            </form>
        </div>

        <div class="container">

            <div class="d-flex justify-content-between mb-3">
                <div class="d-flex align-items-center">
                    <input class="mr-1" id="seleccionarTodo" type="checkbox" onclick="seleccionarTodo(this.id)">
                    <label class="p-0 m-0" for="seleccionarTodo">Seleccionar todo</label>
                </div>
                <form id="formFirmar" action="/constanciaservicio/firmaConstancias" method="post" >
                    <input type="text" name="constancias" id="constancias" hidden>
                    <input type="text" name="institucionId" id="institucionId" value="${institucion?.id}" hidden>
                    <a id="btnFirmar"  class="btn btn-primary my-2" onclick="firmaConstancias()">
                        Firmar seleccionados (0)
                    </a>
                </form>
            </div>

            <table id="table" class="table">
                <thead>
                    <tr>
                        <th></th>
                        <th>Número tramite</th>
                        <th>CURP</th>
                        <th>Alumno</th>
                        <th>Institución</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tBody>
                    <g:each status="i" in="${constancias}" var="constancia">
                            <tr id="tr${constancia.id}">
                                <td>
                                    <input type="checkbox" name="" id="${constancia.id}" onclick="seleccionar(this.id)">
                                </td>
                                <td>${constancia?.tramite?.numeroTramite}</td>
                                <td>${constancia?.alumno?.persona?.curp}</td>
                                <td>
                                    <a onclick="mostrarProceso();" href="/constanciaservicio/consultar?uuid=${constancia.uuid}" class="alert-link" data-tooltip="tooltip" data-placement="top" title="Consultar constancia">
                                        ${constancia?.alumno?.persona?.nombre} ${constancia?.alumno?.persona?.primerApellido} ${constancia?.alumno?.persona?.segundoApellido}
                                    </a>
                                </td>
                                <td>
                                    ${constancia?.alumno?.carrera?.institucion?.nombre}
                                </td>
                                <td>
                                    <g:if test="${tieneFirmasActivas}">
                                        <a onclick="mostrarProceso();" href="/constanciaservicio/revision?uuid=${constancia.uuid}" class="alert-link">
                                            Firmar
                                        </a>
                                    </g:if>
                                </td>
                            </tr>
                    </g:each>
                </tBody>
            </table>
        </div>


        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <asset:javascript src="jquery-editable-select.js"/>
        <asset:javascript src="jquery-editable-select.min.js"/>
        <asset:javascript src="filter-buttons.js"/>

        <script>
            $(function () {
                $('[data-tooltip="tooltip"]').tooltip()
            })

            var constanciasId = []
            var totalConstancias = "${conteo}"

            function seleccionar(id){
                var checkboxElement = $("#"+id)
                var trElement = $("#tr"+id)

                if(checkboxElement.prop('checked')){
                    trElement.addClass("table-active");
                    constanciasId.push(id)
                }else{
                    trElement.removeClass("table-active");
                    var i = constanciasId.indexOf(id);
                    if ( i !== -1 ) {
                        constanciasId.splice( i, 1 );
                    }
                }
                
                if(constanciasId.length < totalConstancias){
                    $("#seleccionarTodo").prop('checked', false)
                }else{
                    $("#seleccionarTodo").prop('checked', true)
                }

                $("#btnFirmar").html("Firmar seleccionados ("+constanciasId.length+")")
                var certificados = JSON.stringify(constanciasId);
                $("#certificados").val(constancias)
            }

            function seleccionarTodo(id){
                var checkboxSeleccionarTodo = $("#"+id)
                if(checkboxSeleccionarTodo.prop('checked')){
                    constanciasId = []
                    $("#table").find(':input:checkbox').each(function() {
                        var elemento = this;
                        var checkboxElement = $("#"+elemento.id)
                        var trElement = $("#tr"+elemento.id)

                        trElement.addClass("table-active");
                        constanciasId.push(elemento.id)
                        checkboxElement.prop('checked', true)
                    });
                }else{
                    constanciasId = []
                    $("#table").find(':input:checkbox').each(function() {
                        var elemento = this;
                        var checkboxElement = $("#"+elemento.id)
                        var trElement = $("#tr"+elemento.id)
                        
                        trElement.removeClass("table-active");
                        checkboxElement.prop('checked', false)
                    });
                }
                $("#btnFirmar").html("Firmar seleccionados ("+constanciasId.length+")")
                var constancias = JSON.stringify(constanciasId);
                $("#constancias").val(constancias)
            }

            function firmaConstancias(){
                if(constanciasId.length > 0){
                    $("#formFirmar").submit()
                }
            }
        </script>
    </body>
</html>