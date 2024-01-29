<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Nuevas Instituciones</title>
</head>

<body>

    <!-- Acciones -->
    <content tag="buttons">
        <a href="/institucion/registro" class="btn btn-primary col-md-12 my-2">
            Nueva institución
        </a>
        <a href="/institucion/subirExcel" class="btn btn-primary col-md-12 my-2">
            Cargar instituciones
        </a>
    </content>

    <div class="row mb-5">
        <div class="col-md-12 pt-2 border-bottom">
            <h3 class="page-title pl-3">
                Nueva institución
            </h3>
        </div>
    </div>
    <div class="container">
        <g:if test="${flash.estatus}">
            <div class="alert alert-success" role="alert">
                ${flash.mensaje}
            </div>
        </g:if>
        <g:else>
            <g:if test="${flash.mensaje}">
                <div class="alert alert-danger" role="alert">
                    ${flash.mensaje}
                </div>
            </g:if>
        </g:else>
    </div>
    <div class="container">
        <div class="alert alert-light" role="alert">
          Los campos marcados con * son obligatorios.
        </div>
    </div>

    <div class="container mb-5">
        <form id="form-valid-claveCt" action="/institucion/registro" method="post">
            <input id="claveCtAux" name="claveCt" value="${institucion.claveCt}" hidden>
        </form>
        <form action="/institucion/subirLogo" class="needs-validation" method="post" novalidate>
                <div class="form-row">
                    <div class="form-group col-md-6">
                        Clave CCT <font color="red">*</font>
                        <br/>
                        <input id="claveCt" name="claveCt" class="form-control" value="${institucion.claveCt}" required>
                    </div>
                    <div class="form-group col-md-6">
                        Clave DGP
                        <br/>
                        <input name="claveDgp" class="form-control" value="${institucion.claveDgp}" ${params.bloquear ? 'readonly' : ''}>
                    </div>
                </div>
                <div class="form-group" >
                    Nombre <font color="red">*</font>
                    <br/>
                    <input name="nombre" class="form-control" value="${institucion.nombre}" ${params.bloquear ? 'readonly' : ''} required>
                    <div class="invalid-feedback">
                        Por favor ingrese su nombre.
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group col-md-6">
                        Nombre Comercial <font color="red">*</font>
                        <br/>
                        <input name="nombreComercial" class="form-control" value="${institucion.nombreComercial}" ${params.bloquear ? 'readonly' : ''} required>
                        <div class="invalid-feedback">
                            Por favor ingrese el Nombre Comercial
                        </div>
                    </div>
                    <div class="form-group col-md-6">
                        Raz&oacute;n Social <font color="red">*</font>
                        <br/>
                        <input name="razonSocial" class="form-control" value="${institucion.razonSocial}" ${params.bloquear ? 'readonly' : ''} required>
                        <div class="invalid-feedback">
                            Por favor ingrese la Raz&oacute;n Social.
                        </div>
                    </div>
                    <div class="form-group col-md-6">
                        Correo <font color="red">*</font>
                        <br/>
                        <input name="correoElectronico" class="form-control" value="${institucion.correoElectronico}" ${params.bloquear ? 'readonly' : ''} required>
                        <div class="invalid-feedback">
                            Por favor ingrese el Correo.
                        </div>
                    </div>
                    <div class="form-group col-md-6">
                        Teléfono <font color="red">*</font>
                        <br/>
                        <input name="telefono" class="form-control" value="${institucion.telefono}" onkeypress="return esNumeroTelefonico(event, this.id);" ${params.bloquear ? 'readonly' : ''}  required>
                        <div class="invalid-feedback">
                            Por favor ingrese el Telefono.
                        </div>
                    </div>
                </div>
                <div class="form-group ">
                    Calle <font color="red">*</font>
                    <br/>
                    <input name="calle" class="form-control" value="${institucion.calle}" ${params.bloquear ? 'readonly' : ''} required>
                    <div class="invalid-feedback">
                        Por favor ingrese la Calle.
                    </div>
                </div>
                 <div class="form-row">
                    <div class="form-group col-md-6">
                        N&uacute;mero Interior:
                        <br/>
                        <input name="numeroInterior" class="form-control" value="${institucion.numeroInterior}"${params.bloquear ? 'readonly' : ''} >
                    </div>
                    <div class="form-group col-md-6">
                        Número exterior
                        <br/>
                        <input name="numeroExterior" class="form-control" value="${institucion.numeroExterior}"${params.bloquear ? 'readonly' : ''} >
                    </div>
                    <div class="form-group col-md-6">
                        Colonia o Asentamiento <font color="red">*</font>
                        <br/>
                        <input name="asentamiento" class="form-control" value="${institucion.asentamiento}" ${params.bloquear ? 'readonly' : ''} required>
                        <div class="invalid-feedback">
                            Por favor ingrese la Colonia o Asentamieto.
                        </div>
                    </div>
                    <div class="form-group col-md-6">
                        Localidad <font color="red">*</font>
                        <br/>
                        <input name="localidad" class="form-control" value="${institucion.localidad}" ${params.bloquear ? 'readonly' : ''} required>
                        <div class="invalid-feedback">
                            Por favor ingrese la Localidad.
                        </div>
                    </div>
                     <div class="form-group col-md-6">
                        Municipio <font color="red">*</font>
                        <br/>
                        <input name="municipio" class="form-control" value="${institucion.municipio}" ${params.bloquear ? 'readonly' : ''} required>
                        <div class="invalid-feedback">
                            Por favor ingrese el Municipio.
                        </div>
                     </div>
                     <div class="form-group col-md-6">
                        Estado <font color="red">*</font>
                        <br/>
                        <input name="estado" class="form-control" value="${institucion.estado}" ${params.bloquear ? 'readonly' : ''} required>
                        <div class="invalid-feedback">
                            Por favor ingrese el Estado.
                        </div>
                     </div>
                     <div class="form-group col-md-6">
                        C&oacute;digo Postal <font color="red">*</font>
                        <br/>
                        <input id="codigoPostal" name="codigoPostal" class="form-control" value="${institucion.codigoPostal}" onkeypress="return esCodigoPostal(event, this.id);" ${params.bloquear ? 'readonly' : ''} required>
                        <div class="invalid-feedback">
                            Por favor ingrese el Codigo Postal.
                        </div>
                     </div>
                 </div>
                 <div class="form-group">
                    Referencias:
                    <br/>
                    <textarea name='referencias' class="form-control" rows="3" value="${institucion.referencias}" ${params.bloquear ? 'readonly' : ''}></textarea>
                 </div>
                 <div class="form-row">
                    <div class="form-group col-md-6">
                        Latitud:
                        <br/>
                        <input id="latitud" name="latitud" class="form-control" value="${institucion.latitud}" ${params.bloquear ? 'readonly' : ''} onkeypress="return esCoordenada(event, this.id);">
                    </div>
                    <div class="form-group col-md-6">
                        Longitud:
                        <br/>
                        <input id="longitud" name="longitud" class="form-control" value="${institucion.longitud}" ${params.bloquear ? 'readonly' : ''} onkeypress="return esCoordenada(event, this.id);">
                    </div>
                 </div>
                <div id="map" class="map"></div>
                </br>
                <div class="form-row justify-content-end">
                    <p>
                        <a onclick="mostrarProceso();" href="/institucion/listar" class="btn btn-secondary">Cancelar</a>
                        <g:if test="${!params.bloquear}">
                            <button type="submit" class="btn btn-primary">Continuar</button>
                        </g:if>
                    </p>
                </div>
                <asset:javascript src="map-select-location.js"/>
				<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBhfc1fX6WWG08xM_eBtX1m7NaGsIz1ACU&callback=initMap&libraries=&v=weekly" async></script>
                <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
                <asset:javascript src="jquery-editable-select.js"/>
                <asset:javascript src="jquery-editable-select.min.js"/>
                <asset:javascript src="filter-buttons.js"/>
                <asset:javascript src="validations.js"/>

                <script>
                    $("#claveCt").on("input", ()=>{
                        $("#claveCtAux").val($("#claveCt").val())
                    })
                    $("#claveCt").blur(() => {
                        $("#form-valid-claveCt").submit()
                    })
                </script>
        </form>
    </div>
</body>
</html>

