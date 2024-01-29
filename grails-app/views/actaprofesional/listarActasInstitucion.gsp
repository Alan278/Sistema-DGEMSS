<!doctype html>
<html>
     <head>
        <meta name="layout" content="main"/>
        <title> Listado de Firmas de Actas</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/actaprofesional/firmarActas/${institucion?.id}" class="btn btn-primary col-md-12 my-2">
                Firmar todas las actas
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Listado de actas a firmar
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
                        <b>Número de actas:</b>
                        <medium class="text-muted">${conteo}</medium>
                    </h6>
                </div>
            </div>
        </div>

        <div class="container">
            <form id="form-busqueda" action="/actaprofesional/listarFirmasActas" class="mt-4">
                
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
                <form id="formFirmar" action="/actaprofesional/firmaActas" method="post" >
                    <input type="text" name="actas" id="actas" hidden>
                    <input type="text" name="institucionId" id="institucionId" value="${institucion?.id}" hidden>
                    <a id="btnFirmar"  class="btn btn-primary my-2" onclick="firmaActas()">
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
                    <g:each status="i" in="${actas}" var="acta">
                            <tr id="tr${acta.id}">
                                <td>
                                    <input type="checkbox" name="" id="${acta.id}" onclick="seleccionar(this.id)">
                                </td>
                                <td>${acta?.tramite?.numeroTramite}</td>
                                <td>${acta?.alumno?.persona?.curp}</td>
                                <td>
                                    <a onclick="mostrarProceso();" href="/actaprofesional/consultar?uuid=${acta.uuid}" class="alert-link" data-tooltip="tooltip" data-placement="top" title="Consultar notificacion">
                                        ${acta?.alumno?.persona?.nombre} ${acta?.alumno?.persona?.primerApellido} ${acta?.alumno?.persona?.segundoApellido}
                                    </a>
                                </td>
                                <td>
                                    ${acta?.alumno?.carrera?.institucion?.nombre}
                                </td>
                                <td>
                                    <g:if test="${tieneFirmasActivas}">
                                        <a onclick="mostrarProceso();" href="/actaprofesional/revision?uuid=${acta.uuid}" class="alert-link">
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

            var actasId = []
            var totalActas = "${conteo}"

            function seleccionar(id){
                var checkboxElement = $("#"+id)
                var trElement = $("#tr"+id)

                if(checkboxElement.prop('checked')){
                    trElement.addClass("table-active");
                    constanciasId.push(id)
                }else{
                    trElement.removeClass("table-active");
                    var i = actasId.indexOf(id);
                    if ( i !== -1 ) {
                        actasId.splice( i, 1 );
                    }
                }
                
                if(actasId.length < totalActas){
                    $("#seleccionarTodo").prop('checked', false)
                }else{
                    $("#seleccionarTodo").prop('checked', true)
                }

                $("#btnFirmar").html("Firmar seleccionados ("+actasId.length+")")
                var actas = JSON.stringify(actasId);
                $("#certificados").val(actas)
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
                        actasId.push(elemento.id)
                        checkboxElement.prop('checked', true)
                    });
                }else{
                    actasId = []
                    $("#table").find(':input:checkbox').each(function() {
                        var elemento = this;
                        var checkboxElement = $("#"+elemento.id)
                        var trElement = $("#tr"+elemento.id)
                        
                        trElement.removeClass("table-active");
                        checkboxElement.prop('checked', false)
                    });
                }
                $("#btnFirmar").html("Firmar seleccionados ("+actasId.length+")")
                var actas = JSON.stringify(actasId);
                $("#actas").val(actas)
            }

            function firmaActas(){
                if(actasId.length > 0){
                    $("#formFirmar").submit()
                }
            }
        </script>
    </body>
</html>