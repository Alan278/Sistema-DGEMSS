<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Registro de archivo cer</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>
        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Registro de certificado
                </h3>
            </div>
        </div>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/firmaElectronica/registroCer" class="btn btn-primary col-md-12 my-2">
                Registrar certificado
            </a>
        </content>

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
            <form id="formu" action="/firmaElectronica/registrarCer" enctype="multipart/form-data" method="POST" class="needs-validation" novalidate>
                
                <div class="form-group">
                    <label>Certificado (.cer)<font color="red">*</font></label>
                    <div class="custom-file">
                      <label class="custom-file-label" for="cer">Elija un archivo</label>
                      <input type="file" class="custom-file-input" id="cer" name="cer" accept=".cer" required>
                      <div class="invalid-feedback">
                          Por favor elija el archivo a subir.
                      </div>
                    </div>
                </div>
                
                <div class="form-row justify-content-end">
                    <p>
                        <a onclick="mostrarProceso();" href="firmaElectronica/consultar" class="btn btn-secondary">Cancelar</a>
                        <button type="submit" class="btn btn-primary" onclick="llenarProgreso()">Guardar</button>
                    </p>
                </div>
            </form>
        </div>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <asset:javascript src="jquery-editable-select.js"/>
        <asset:javascript src="jquery-editable-select.min.js"/>
        <asset:javascript src="filter-buttons.js"/>

    </body>
</html>
