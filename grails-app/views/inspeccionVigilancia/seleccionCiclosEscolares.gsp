<!doctype html>
<html>
     <head>
        <meta name="layout" content="main"/>
        <title> Listado de certificados</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/inspeccionVigilancia/registro" class="btn btn-primary col-md-12 my-2">
                Nuevo trámite
            </a>
        </content>

        <!-- Título -->
        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Listado de ciclos escolares
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
                                    <b>Valor por ciclo escolar: </b> $${(tipoTramite?.costoUmas * uma.valor).round(0)}
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
                                    <b>Total ciclos: </b>${ciclosEscolares.size()}
                                </h6>
                            </div>
                            <div class="mb-1">
                                <h6 class="d-inline">
                                    <b>Ciclos pagados: </b><span id="nPagados"></span>
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
                        <th>Nombre</th>
                        <th>Periodo</th>
                        <th>Fecha de Inicio</th>
                        <th>Fecha de Fin</th>
                        <th>Carrera</th>
                        <th>Plan de estudios</th>
                    </tr>
                </thead>
                <tBody>
                    <g:each status="i" in="${ciclosEscolares}" var="certificado">
                            <tr id="tr${certificado.id}">
                                <td>
                                    <input type="checkbox" name="" id="${certificado.id}" onclick="seleccionar(this.id)">
                                </td>
                                <td>${i+1}</td>
                                <td>${certificado?.nombre}</td>
                                <td>
                                    ${certificado?.periodo}
                                </td>
                                <td>${certificado?.inicio}</td>
                                <td>${certificado?.fin}</td>
                                <td>${certificado?.planEstudios?.carrera.nombre}</td>
                                <td>${certificado?.planEstudios?.nombre}</td>
                            </tr>
                    </g:each>
                </tBody>
            </table>
            <div class="form-row justify-content-end my-5">
                <a onclick="mostrarProceso();" href="inspeccionVigilancia/listar" class="btn btn-secondary">Cancelar</a>
                <a onclick="mostrarProceso();" href="inspeccionVigilancia/registro" class="btn btn-secondary ml-1">Volver</a>
                <form id="form" action="/inspeccionVigilancia/registrar" class="ml-1">
                    <input id="serie" name="serie" type="text" value="${params?.serie}" hidden>
                    <input id="folio" name="folio" type="text" value="${params?.folio}" hidden>
                    <input id="lineaCaptura" name="lineaCaptura" type="text" value="${params?.lineaCaptura}" hidden>
                    <input id="fechaRecepcion" name="fechaRecepcion" type="text" value="${params?.fechaRecepcion}" hidden>
                    <input id="numeroTramite" name="numeroTramite" type="text" value="${params?.numeroTramite}" hidden>
                    <input id="institucionId" name="institucionId" type="text" value="${params?.institucionId}" hidden>
                    <input id="ciclosEscolares" name="ciclosEscolares" type="text" hidden>
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
                if(ciclosId.length > nPagados){
                    alert("Seleccione " + nPagados + " ciclos");
                    return
                }
                mostrarProceso()
                $('#form').submit();
                return
            });


            var ciclosId = []
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
                    if(ciclosId.length < nPagados){
                        trElement.addClass("table-active");
                        ciclosId.push(id)
                    }else{
                        checkboxElement.prop('checked', false)
                    }
                }else{
                    trElement.removeClass("table-active");
                    var i = ciclosId.indexOf(id);
                    if ( i !== -1 ) {
                        ciclosId.splice( i, 1 );
                    }
                }

                if(ciclosId.length < nPagados){
                    $("#seleccionarTodo").prop('checked', false)
                }else{
                    $("#seleccionarTodo").prop('checked', true)
                }

                $("#nSeleccionados").html(ciclosId.length)
                var ciclos = JSON.stringify(ciclosId);
                $("#ciclosEscolares").val(ciclos)
            }

            function seleccionarTodo(id){
                var checkboxSeleccionarTodo = $("#"+id)
                if(checkboxSeleccionarTodo.prop('checked')){
                    ciclosId = []
                    $("#table").find(':input:checkbox').each(function() {
                        var elemento = this;
                        var checkboxElement = $("#"+elemento.id)
                        var trElement = $("#tr"+elemento.id)

                        if(ciclosId.length < nPagados){
                            trElement.addClass("table-active");
                            ciclosId.push(elemento.id)
                            checkboxElement.prop('checked', true)
                        }else{
                            trElement.removeClass("table-active");
                            checkboxElement.prop('checked', false)
                        }
                    });
                }else{
                    ciclosId = []
                    $("#table").find(':input:checkbox').each(function() {
                        var elemento = this;
                        var checkboxElement = $("#"+elemento.id)
                        var trElement = $("#tr"+elemento.id)

                        trElement.removeClass("table-active");
                        checkboxElement.prop('checked', false)
                    });
                }
                $("#nSeleccionados").html(ciclosId.length)
                var ciclos = JSON.stringify(ciclosId);
                $("#ciclosEscolares").val(ciclos)
            }
        </script>
    </body>
</html>