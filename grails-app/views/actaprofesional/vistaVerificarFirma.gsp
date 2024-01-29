<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Expedición de Certificado</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>
        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Validar firma
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

        <div class="container mb-5">
            <form id="formu" action="/certificado/verificar" enctype="multipart/form-data" method="POST" class="needs-validation" novalidate>
                <div class="form-group">
                    <label>Certificado (.cer)<font color="red">*</font></label>
                    <div class="custom-file">
                    <label class="custom-file-label" for="customFile">Elija un archivo</label>
                    <input type="file" class="custom-file-input" id="customFile" name="cer" accept=".cer" required>
                    </div>
                </div>
                <div class="form-group">
                    <label for="contraseña">Cadena original <font color="red">*</font></label>
                    <input type="text" class="form-control" id="cadenaOriginal" name="cadenaOriginal" placeholder="Password" required>
                </div>

                <div class="form-group">
                    <label for="contraseña">Sello<font color="red">*</font></label>
                    <input type="text" class="form-control" id="sello" name="sello" placeholder="Password" required>
                </div>
                <input type="submit" value="ok">
            </form>
        </div>

    </body>
</html>
