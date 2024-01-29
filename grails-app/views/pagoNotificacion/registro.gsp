<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Nuevos Trámites</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
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
                    Nuevo Trámite / Notificación
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
            <div class="alert alert-light" role="alert">
              Los campos marcados con * son obligatorios.
            </div>
        </div>
        <div class="container mb-5">

            <ul class="nav nav-tabs" id="myTab" role="tablist">
                <li class="nav-item" role="presentation">
                  <button class="nav-link ${!params.serie ? 'active' : ''}" id="line-captura-tab" data-toggle="tab" data-target="#line-captura" type="button" role="tab" aria-controls="home" aria-selected="true">Linea de captura</button>
                </li>
                <li class="nav-item" role="presentation">
                  <button class="nav-link ${params.serie ? 'active' : ''}" id="folio-tab" data-toggle="tab" data-target="#folio" type="button" role="tab" aria-controls="profile" aria-selected="false">Folio</button>
                </li>
            </ul>
            <div class="tab-content mt-4" id="myTabContent">
                <div class="tab-pane fade ${!params.serie ? 'show active' : ''}" id="line-captura" role="tabpanel" aria-labelledby="home-tab">
                    <form id="lineCapturaForm" action="/pagoNotificacion/registro" class="needs-validation" method="post" novalidate>
                        <div class="form-row">
                            <div class="form-group col-md-5">
                                <label for="lineaCaptura">Linea de captura <font color="red">*</font></label>
                                <input id="lineaCapturaAux" name="lineaCaptura" type="text" class="form-control" value="${params?.lineaCaptura}" required>
                                <div class="invalid-feedback">
                                    Por favor ingrese la linea de captura.
                                </div>
                            </div>
                            <div class="form-group col-md-2 align-self-end ">
                                <input type="submit" class="btn btn-primary" value="Validar">
                            </div>
                        </div>
                    </form>
                    <div class="form-group">
                        <label for="fechaPago">Folio</label>
                        <input id="fechaPago" type="text" class="form-control" value="${params.datosPago ? params.datosPago.folio : ''}" readonly>
                        <div class="invalid-feedback">
                            Por favor ingrese la fecha de pago.
                        </div>
                    </div>
                </div>
                <div class="tab-pane fade  ${params.serie ? 'show active' : ''}" id="folio" role="tabpanel" aria-labelledby="profile-tab">
                    <form id="folioForm" action="/pagoNotificacion/registro" class="needs-validation" method="post" novalidate>
                        <div class="form-row">
                            <div class="form-group col-md-5">
                                <label for="serie">Serie <font color="red">*</font></label>
                                <input id="serieAux" name="serie" type="text" class="form-control" value="${params?.serie}" required>
                                <div class="invalid-feedback">
                                    Por favor ingrese el número de serie.
                                </div>
                            </div>
                            <div class="form-group col-md-5">
                                <label for="folio">Folio <font color="red">*</font></label>
                                <input id="folioAux" name="folio" type="text" class="form-control" value="${params?.folio}" required>
                                <div class="invalid-feedback">
                                    Por favor ingrese el número de folio.
                                </div>
                            </div>
                            <div class="form-group col-md-2 align-self-end ">
                                <input type="submit" class="btn btn-primary" value="Validar">
                            </div>
                        </div>
                    </form>
                    <div class="form-group">
                        <label for="fechaPago">Linea de captura</label>
                        <input id="fechaPago" type="text" class="form-control" value="${params.datosPago ? params.datosPago.lineaCaptura : ''}" readonly>
                        <div class="invalid-feedback">
                            Por favor ingrese la fecha de pago.
                        </div>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <label for="fechaPago">RFC</label>
                <input id="fechaPago" type="text" class="form-control" value="${params.datosPago ? params.datosPago.rfc : ''}" readonly>
                <div class="invalid-feedback">
                    Por favor ingrese la fecha de pago.
                </div>
            </div>
            <div class="form-group">
                <label for="fechaPago">Nombre</label>
                <input id="fechaPago" type="text" class="form-control" value="${params.datosPago ? params.datosPago.nombre : ''}" readonly>
                <div class="invalid-feedback">
                    Por favor ingrese la fecha de pago.
                </div>
            </div>
            <div class="form-group">
                <label for="fechaPago">Razon Social</label>
                <input id="fechaPago" type="text" class="form-control" value="${params.datosPago ? params.datosPago.razonSocial : ''}" readonly>
                <div class="invalid-feedback">
                    Por favor ingrese la fecha de pago.
                </div>
            </div>
            <div class="form-row">
                <div class="form-group col-md-6">
                    <label for="fechaPago">Fecha de Pago</label>
                    <input id="fechaPago" type="text" class="form-control" value="${params.datosPago ? params.datosPago.fechaPago : ''}" readonly>
                    <div class="invalid-feedback">
                        Por favor ingrese la fecha de pago.
                    </div>
                </div>
                <div class="form-group col-md-6">
                    <label for="horaPago">Hora de Pago</label>
                    <input id="horaPago" type="text" class="form-control"  value="${params.datosPago ? params.datosPago.horaPago : ''}" readonly>
                     <div class="invalid-feedback">
                        Por favor ingrese la hora de pago.
                     </div>
                </div>
            </div>
            <table class="table table-striped table-bordered">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Concepto</th>
                        <th>Monto</th>
                    </tr>
                </thead>
                <tbody>
                    <g:each in="${params.datosPago?.conceptos}" var="concepto">
                        <tr>
                            <td>${concepto.idConcepto}</td>
                            <td>${concepto.descripcion}</td>
                            <td>$${concepto.monto}</td>
                        </tr>
                    </g:each>
                </tbody>
            </table>

            <form action="/pagoNotificacion/seleccionNotificaciones" class="needs-validation" novalidate>
                <input id="serie" name="serie" type="text" value="${params?.serie}" hidden>
                <input id="folio" name="folio" type="text" value="${params?.folio}" hidden>
                <input id="lineaCaptura" name="lineaCaptura" type="text" value="${params?.lineaCaptura}" hidden>
                <input id="importe" name="importe" type="text" value="${params.datosPago ? params.datosPago.conceptoValido.monto : ''}" hidden>
                <div class="form-group">
                    <label for="institucionesMod">Institución <font color="red">*</font></label>
                    <g:select  class="form-control" id="institucionesMod" name="institucionesMod" from="${instituciones}" optionKey="id" optionValue="nombre" value="${params?.institucionId}" required="true"/>
                    <input type="text" id="institucionId" name="institucionId" value="${params?.institucionId}" hidden>
                    <div class="invalid-feedback">
                        Por favor ingrese la institución.
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label for="fechaRecepcion">Fecha de Recepción <font color="red">*</font></label>
                        <input id="fechaRecepcion" name="fechaRecepcion" type="date" class="form-control" value="${params?.fechaRecepcion}" required>
                        <div class="invalid-feedback">
                            Por favor ingrese la fecha de recepción.
                        </div>
                    </div>
                    <div class="form-group col-md-6">
                        <label for="numeroTramite">No. Tramite<font color="red">*</font></label>
                        <input id="numeroTramite" name="numeroTramite" type="text" class="form-control" value="${params?.numeroTramite}" required>
                        <div class="invalid-feedback">
                            Por favor ingrese la fecha de recepción.
                        </div>
                    </div>
                </div>

                <div class="form-row justify-content-end">
                    <p>
                        <a onclick="mostrarProceso();" href="pagoNotificacion/listar" class="btn btn-secondary">Cancelar</a>
                        <button type="submit" class="btn btn-primary">Continuar</button>
                    </p>
                </div>
            </form>
        </div>
         <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <asset:javascript src="jquery-editable-select.js"/>
        <asset:javascript src="jquery-editable-select.min.js"/>
        <asset:javascript src="filter-buttons.js"/>
        <script>

            $(document).ready(function(){
                $("#lineaCapturaAux").focus()
            });

            $("#line-captura-tab").click(() => {
                $("#serieAux").val("")
                $('#serie').val("")
                $("#folioAux").val("")
                $('#folio').val("")
                setTimeout(function(){
                    $("#lineaCaptura").focus()
                }, 300);
            })

            $("#folio-tab").click(() => {
                $("#lineaCapturaAux").val("")
                $('#lineaCaptura').val("")
                setTimeout(function(){
                    $("#serieAux").focus()
                }, 300);
            })

            $("#lineaCapturaAux").on("input", function(){
                $('#lineaCaptura').val($('#lineaCapturaAux').val())
                if($("#lineaCapturaAux").val().length == 20){
                    $("#lineCapturaForm").submit()
                    mostrarProceso(5000)
                }
            });

            function asignarFechaActual(){
                var now = new Date();
                var date = '0' + now.getDate();
                date = date.substr(-2);
                var month = '0' + (now.getMonth() + 1);
                month = month.substr(-2);
                var year = now.getFullYear();
                now = year+'-'+month+'-'+date;

                $('#fechaRecepcion').val(now);
            }

            asignarFechaActual();

            $('#serieAux').keyup(function() {
                $('#serie').val($('#serieAux').val())
            });

            $('#folioAux').keyup(function() {
                $('#folio').val($('#folioAux').val())
            });

            $('#institucionesMod').editableSelect()
                .editableSelect()
                    .on('select.editable-select', function (e, li) {
                        $('#institucionId').val(li.val());
                    });
        </script>
    </body>
</html>