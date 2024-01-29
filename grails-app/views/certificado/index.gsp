<html>
    <body>

    <a href="${createLink(action: "expedir")}" target="_blank">Expedir certificado</a>
    <hr/>

    <a href="${createLink(action: "leerCertificado")}" target="_blank">Leer certificado (.cer)</a>

    <hr/>

    <a href="${createLink(action: "firmarPrueba")}" target="_blank">Firmar prueba</a>

    <hr/>

    <a href="${createLink(action: "verificarFirma")}" target="_blank">Verificar firmar prueba</a>

    </body>

    <form action="/certificado/registrar" enctype="multipart/form-data" method="POST" class="needs-validation" novalidate >
        <input type="text" name="personaId" placeholder="personaId">
        <input type="text" name="libro" placeholder="libro">
        <input type="text" name="foja" placeholder="foja">
        <input type="text" name="municipio" placeholder="municipio">
        <input type="date" name="fechaExpedicion" placeholder="fechaExpedicion">
        <div class="input-group mb-3">
            <div class="input-group-prepend">
                <span class="input-group-text" id="inputGroupFileAddon01">Upload</span>
            </div>
            <div class="custom-file">
                <input type="file" class="custom-file-input" id="clavePrivada" name="clavePrivada" aria-describedby="inputGroupFileAddon01">
                <label class="custom-file-label" for="inputGroupFile01">Choose file</label>
            </div>
        </div>
        <input type="text" name="contrasena">
        <div class="form-row justify-content-end">
            <p>
                <a onclick="mostrarProceso();" href="carreraExterna/listar" class="btn btn-secondary">Cancelar</a>
                <button type="submit" class="btn btn-primary">Guardar</button>
            </p>
        </div>
    </form>
</html>