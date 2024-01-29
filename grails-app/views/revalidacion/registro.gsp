<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Nuevas Revalidaciones</title>
</head>

<body>
    <div class="row mb-5">
        <div class="col-md-12 pt-2 border-bottom">
            <h3 class="page-title pl-3">
                Expedición de Revalidación de Estudios - Generar
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
        <form action="/revalidacion/registrar" class="needs-validation" novalidate>
                <div class="form-group">
                    <label for="alumno">Nombre del Alumno <font color="red">*</font></label>
                    <input id="alumno" name="alumno" type="text" class="form-control"  value="${revalidacion?.alumno}" required>
                    <div class="invalid-feedback">
                        Por favor ingrese el nombre del Alumno.
                    </div>
                </div>
                <div class="form-group">
                    <label for="institucion">Nombre de la Institución <font color="red">*</font></label>
                    <input id="institucion" name="institucion" type="text" class="form-control"  value="${revalidacion?.institucion}" required>
                    <div class="invalid-feedback">
                        Por favor ingrese el nombre de la Institución.
                    </div>
                </div>
                <div class="form-group">
                    <label for="cicloEscolar">Ciclo escolar cursado <font color="red">*</font></label>
                    <input id="cicloEscolar" name="cicloEscolar" type="text" class="form-control"  value="${revalidacion?.cicloEscolar}" required>
                    <div class="invalid-feedback">
                        Por favor ingrese el ciclo cursado.
                    </div>
                </div>
                <div class="form-group">
                    <label for="nivelExterno">Nivel de estudios cursado <font color="red">*</font></label>
                    <input id="nivelExterno" name="nivelExterno" type="text" class="form-control"  value="${revalidacion?.nivelExterno}" required>
                    <div class="invalid-feedback">
                        Por favor ingrese el nivel de estudios cursado.
                    </div>
                </div>
                <div class="form-group">
                    <label for="nivelInterno">Nivel de estudio equivalente <font color="red">*</font></label>
                    <input id="nivelInterno" name="nivelInterno" type="text" class="form-control"  value="${revalidacion?.nivelInterno}" required>
                    <div class="invalid-feedback">
                        Por favor ingrese el nivel de estudios equivalente.
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label for="estado">Estado <font color="red">*</font></label>
                        <input id="estado" name="estado" type="text" class="form-control"  value="${revalidacion?.estado}" required>
                        <div class="invalid-feedback">
                            Por favor ingrese el estado.
                        </div>
                    </div>
                    <div class="form-group col-md-6">
                        <label for="pais">País <font color="red">*</font></label>
                        <input id="pais" name="pais" type="text" class="form-control"  value="${revalidacion?.pais}" required>
                        <div class="invalid-feedback">
                            Por favor ingrese el país.
                        </div>
                    </div>
                    <div class="form-group col-md-6">
                        <label for="fechaTermino">Fecha de Término <font color="red">*</font></label>
                        <input id="fechaTermino" name="fechaTermino" type="date" class="form-control"  value="${revalidacion?.fechaTermino}" required>
                        <div class="invalid-feedback">
                            Por favor ingrese la fecha de término.
                        </div>
                    </div>
                    <div class="form-group col-md-6">
                        <label for="expediente">Número de expediente <font color="red">*</font></label>
                        <input id="expediente" name="expediente" type="text" class="form-control"  value="${revalidacion?.expediente}" required>
                        <div class="invalid-feedback">
                            Por favor ingrese el número de expediente.
                        </div>
                    </div>
                    <div class="form-group col-md-6">
                        <label for="lugarExpedicion">Lugar de Expedición <font color="red">*</font></label>
                        <input id="lugarExpedicion" name="lugarExpedicion" type="text" class="form-control"  value="${revalidacion?.lugarExpedicion}" required>
                        <div class="invalid-feedback">
                            Por favor ingrese el lugar de expedición.
                        </div>
                    </div>
                    <div class="form-group col-md-6">
                        <label for="fechaExpedicion">Fecha de Expedición <font color="red">*</font></label>
                        <input id="fechaExpedicion" name="fechaExpedicion" type="date" class="form-control"  value="${revalidacion?.fechaExpedicion}" required>
                        <div class="invalid-feedback">
                            Por favor ingrese la fecha de expedición.
                        </div>
                    </div>
                    <div class="form-group col-md-6">
                        <label for="folio">Folio <font color="red">*</font></label>
                        <input id="folio" name="folio" type="text" class="form-control"  value="${revalidacion?.folio}" required>
                        <div class="invalid-feedback">
                            Por favor ingrese el folio.
                        </div>
                    </div>
                </div>
                </br>
                <div class="form-row justify-content-end">
                    <p>
                        <a onclick="mostrarProceso();" href="/revalidacion/listar" class="btn btn-secondary">Cancelar</a>
                        <button type="submit" class="btn btn-primary">Guardar</button>
                    </p>
                </div>
                <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
                <asset:javascript src="jquery-editable-select.js"/>
                <asset:javascript src="jquery-editable-select.min.js"/>
                <asset:javascript src="filter-buttons.js"/>
        </form>
        
    </div>

    <script>
         $("#fechaTermino").change(function(){
            $("#fechaExpedicion").prop("min", $("#fechaTermino").val());
         });

        validate.addEventListener("click", () => {
            if (toValidate.value.match(/[0-9]{1,}\.[0-9]{1,}/)) {
                console.log("Válido");
            } else {
                console.log("No válido");
            }
        });
    </script>
</body>
</html>

