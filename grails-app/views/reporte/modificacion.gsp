<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Modificación de Reporte</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/reporte/registro" class="btn btn-primary col-md-12 my-2">
                Nuevo Reporte
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Modificación de Reporte
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
            <form action="/reporte/modificar/${reporte?.id}" class="needs-validation" method="post" novalidate>
                <div class="form-group">
                    <label for="nombre">Nombre <font color="red">*</font></label>
                    <input id="nombre" name="nombre" type="text" class="form-control"  value="${params.nombre?(params.nombre):(reporte?.nombre)}" required>
                    <div class="invalid-feedback">
                        Por favor ingrese el nombre del reporte.
                    </div>
                </div>

                <div class="form-group">
                    <label for="consultaSql">Consulta <font color="red">*</font></label>
                    <input id="consultaSql" name="consultaSql" type="text" class="form-control"  value="${params.consultaSql?(params.consultaSql):(reporte?.consultaSql)}" required>
                    <div class="invalid-feedback">
                        Por favor ingrese la sentencia consultaSql del reporte.
                    </div>
                </div>

                <div class="form-row justify-content-end">
                    <p>
                        <a onclick="mostrarProceso();" href="reporte/listar" class="btn btn-secondary">Cancelar</a>
                        <button type="submit" class="btn btn-primary">Guardar</button>
                    </p>
                </div>
            </form>
        </div>

        <asset:javascript src="filter-buttons.js"/>

    </body>
</html>

