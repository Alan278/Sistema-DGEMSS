<div class="container mb-5">
    <form id="formu" action="/certificado/datosCer" enctype="multipart/form-data" method="POST" class="needs-validation" novalidate>
        
        <div class="form-group">
            <label>Clave privada (.key)<font color="red">*</font></label>
            <div class="custom-file">
              <label class="custom-file-label" for="customFile">Elija un archivo</label>
              <input type="file" class="custom-file-input" id="key" name="key" accept=".key" required>
              <div class="invalid-feedback">
                  Por favor elija el archivo a subir.
              </div>
            </div>
        </div>
        <div class="form-group">
            <label>Clave publica (.cer)<font color="red">*</font></label>
            <div class="custom-file">
              <label class="custom-file-label" for="customFile">Elija un archivo</label>
              <input type="file" class="custom-file-input" id="cer" name="cer" accept=".cer" required>
              <div class="invalid-feedback">
                  Por favor elija el archivo a subir.
              </div>
            </div>
        </div>
        <div class="form-group">
            <label for="contraseña">Contraseña <font color="red">*</font></label>
            <input type="password" class="form-control" id="contrasena" name="contrasena" placeholder="Password" required>
            <div class="invalid-feedback">
                Por favor ingrese la contraseña.
            </div>
        </div>
        <div class="form-row justify-content-end">
            <p>
                <a onclick="mostrarProceso();" href="certificado/listarFirmasCertificados" class="btn btn-secondary">Cancelar</a>
                <button type="submit" class="btn btn-primary" onclick="llenarProgreso()">Firmar</button>
            </p>
        </div>
    </form>
</div>