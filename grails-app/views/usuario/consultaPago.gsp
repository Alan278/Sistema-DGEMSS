<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Detalles del Usuario</title>
        <script src="jquery-3.5.1.min.js"></script>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/usuario/registro" class="btn btn-primary col-md-12 my-2">
                Nuevo Usuario
            </a>
        </content>
        
        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Detalles del pago
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
            <div class="form-group">
                <label for="nombre">Nombre <font color="red">*</font></label>
                <input id="nombre" name="nombre" type="text" class="form-control" >
                <div class="invalid-feedback">
                    Por favor ingrese el nombre.
                </div>
            </div>
            <input type="button" value="Enviar" onclick="soap()">
        </div>

        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <script type="text/javascript">
            function soap(){
                var xml = new XMLHttpRequest();
                xml.open('POST', 'https://www.ingresos.morelos.gob.mx/ws_myt/consultas.asmx?WSDL', true);
            }
        </script>
    </body>
</html>