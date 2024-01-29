<!doctype html>
<html>
     <head>
        <meta name="layout" content="main"/>
        <title> Listado de notificaciones</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/pagoActa/registro" class="btn btn-primary col-md-12 my-2">
                Nuevo trámite
            </a>
        </content>

        <!-- Título -->
        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Listado de actas generadas
                </h3>
            </div>
        </div>

        <!-- Mensajes -->
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

        <!-- Contenido -->
        <div class="container mb-5">

            <!-- Total certificados -->
            <div class="row mb-4">
                <div class="col-md-6">
                    <div class="card h-100">
                        <div class="card-body  ">
                            <div class="mb-1">
                                <h6 class="d-inline">
                                    <b>Institución: </b>${institucion?.nombre}
                                </h6>
                            </div>
                            <hr>
                            <div class="mb-1">
                                <h6 class="d-inline">
                                    <b>Valor por acta: </b> $${(tipoTramite?.costoUmas * uma.valor).round(0)}
                                </h6>
                            </div>
                            <div class="mb-1">
                                <h6 class="d-inline">
                                    <b>Importe: </b> $${params.importe}
                                </h6>
                            </div>
                            <hr>
                            <div class="mb-1">
                                <h6 class="d-inline">
                                    <b>Total actas: </b>${actas.totalCount}
                                </h6>
                            </div>
                            <div class="mb-1">
                                <h6 class="d-inline">
                                    <b>Actas pagadas: </b><span id="nPagados"></span>
                                </h6>
                            </div>
                            <div class="mb-1">
                                <h6 class="d-inline">
                                    <b>Seleccionados: </b><span id="nSeleccionados">0</span>
                                </h6>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="card h-100">
                        <div class="card-body  ">
                            <div class="mb-1">
                                <h6 class="mb-2">
                                    <b>Ciclos pendientes de pago: </b>
                                </h6>
                                <h6 class="d-inline ">
                                    <span><ul class="list-unstyled mb-0 pb-0">
                                        <g:each in="${institucion.carreras}" var="carrera">
                                            <g:each in="${carrera.planesEstudio}" var="planEstudios">
                                                <g:each in="${planEstudios.ciclosEscolares}" var="ciclo">
                                                    <g:if test="${ciclo.activo}">
                                                        <g:if test="${ciclo.tramite == null}">
                                                            <li>${planEstudios.carrera.nombre}: ${ciclo.nombre}</li>
                                                        </g:if>
                                                    </g:if>
                                                </g:each>
                                            </g:each>
                                        </g:each>
                                    </ul></span>
                                </h6>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>

        <div class="container">
            <div class="mb-3 ml-1">
                <input class="mr-1" id="seleccionarTodo" type="checkbox" onclick="seleccionarTodo(this.id)">
                <label for="seleccionarTodo">Seleccionar todo</label>
            </div>
            <table id="table" class="table">
                <thead>
                    <tr>
                        <th></th>
                        <th>#</th>
                        <th>CURP</th>
                        <th>Alumno</th>
                        <th>Carrera</th>
                        <th>Ciclos pendientes por pagar</th>
                    </tr>
                </thead>
                <tBody>
                    <g:each status="i" in="${actas}" var="acta">
                            <g:set var="tienePagosPendientes" value="${false}"/>
                            <g:set var="ciclosPendientes" value="${[]}"/>
                            <g:each status="j" in="${acta.alumno.ciclosEscolares}" var="ciclo">
                                <g:if test="${ciclo.activo}">
                                    <g:if test="${ciclo.cicloEscolar.tramite == null}">
                                        <g:set var="tienePagosPendientes" value="${true}"/>
                                        <g:set var="ciclosPendientes" value="${ciclosPendientes + [ciclo.cicloEscolar.nombre]}"/>
                                    </g:if>
                                </g:if>
                            </g:each>
                            <tr id="tr${acta.id}">
                                <td>
                                    <g:if test="${!tienePagosPendientes}">
                                        <input type="checkbox" name="" id="${acta.id}" onclick="seleccionar(this.id)">
                                    </g:if>
                                </td>
                                <td>${i+1}</td>
                                <td>${acta?.alumno?.persona?.curp}</td>
                                <td>
                                    ${acta?.alumno?.persona?.nombre} ${acta?.alumno?.persona?.primerApellido} ${acta?.alumno?.persona?.segundoApellido}
                                </td>
                                <td>${acta?.alumno?.planEstudios?.carrera?.nombre}</td>
                                <td>
                                    <ul class="list-unstyled">
                                        <g:each in="${ciclosPendientes}" var="ciclo">
                                            <li>${ciclo}</li>
                                        </g:each>
                                    </ul>
                                </td>
                            </tr>
                    </g:each>
                </tBody>
            </table>
            <div class="form-row justify-content-end my-5">
                <a onclick="mostrarProceso();" href="pagoActa/listar" class="btn btn-secondary">Cancelar</a>
                <a onclick="mostrarProceso();" href="pagoActa/registro" class="btn btn-secondary ml-1">Volver</a>
                <form id="form" action="/pagoActa/registrar" class=" ml-1">
                    <input id="serie" name="serie" type="text" value="${params?.serie}" hidden>
                    <input id="folio" name="folio" type="text" value="${params?.folio}" hidden>
                    <input id="lineaCaptura" name="lineaCaptura" type="text" value="${params?.lineaCaptura}" hidden>
                    <input id="fechaRecepcion" name="fechaRecepcion" type="text" value="${params?.fechaRecepcion}" hidden>
                    <input id="numeroTramite" name="numeroTramite" type="text" value="${params?.numeroTramite}" hidden>
                    <input id="institucionId" name="institucionId" type="text" value="${params?.institucionId}" hidden>
                    <input id="actas" name="actas" type="text" hidden >
                    <button id="btnGuardar" type="button" class="btn btn-primary">Guardar</button>
                </form>
            </div>
        </div>


        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <asset:javascript src="jquery-editable-select.js"/>
        <asset:javascript src="jquery-editable-select.min.js"/>
        <asset:javascript src="filter-buttons.js"/>
        <script>

            $('#btnGuardar').on('click', function() {
                if(actasId.length > nPagados){
                    alert("Seleccione " + nPagados + " actas");
                    return
                }
                mostrarProceso()
                $('#form').submit();
                return
            });

            var actasId = []
            var nPagados = 0
            var costo = "${tipoTramite?.costoUmas}"
            var uma = "${uma?.valor}"

            function calcularPagados(){
                nPagados = Math.floor("${params.importe}" / (costo * uma))
            }
            calcularPagados()
            $('#nPagados').html(nPagados);

            function seleccionar(id){
                var checkboxElement = $("#"+id)
                var trElement = $("#tr"+id)

                if(checkboxElement.prop('checked')){
                    if(actasId.length < nPagados){
                        trElement.addClass("table-active");
                        actasId.push(id)
                    }else{
                        checkboxElement.prop('checked', false)
                    }
                }else{
                    trElement.removeClass("table-active");
                    var i = actasId.indexOf(id);
                    if ( i !== -1 ) {
                        actasId.splice( i, 1 );
                    }
                }

                if(actasId.length < nPagados){
                    $("#seleccionarTodo").prop('checked', false)
                }else{
                    $("#seleccionarTodo").prop('checked', true)
                }

                $("#nSeleccionados").html(actasId.length)
                var actas = JSON.stringify(actasId);
                $("#actas").val(actas)
            }

            function seleccionarTodo(id){
                var checkboxSeleccionarTodo = $("#"+id)
                if(checkboxSeleccionarTodo.prop('checked')){
                    actasId = []
                    $("#table").find(':input:checkbox').each(function() {
                        var elemento = this;
                        var checkboxElement = $("#"+elemento.id)
                        var trElement = $("#tr"+elemento.id)

                        if(actasId.length < nPagados){
                            trElement.addClass("table-active");
                            actasId.push(elemento.id)
                            checkboxElement.prop('checked', true)
                        }else{
                            trElement.removeClass("table-active");
                            checkboxElement.prop('checked', false)
                        }
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
                $("#nSeleccionados").html(actasId.length)
                var actas = JSON.stringify(actasId);
                $("#actas").val(actas)
            }
        </script>
    </body>
</html>