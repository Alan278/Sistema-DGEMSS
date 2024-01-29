<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Lista de Respaldos</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/respaldo/generar" class="btn btn-primary col-md-12 my-2">
                Generar Respaldo
            </a>
            <a onclick="mostrarProceso();" href="/respaldo/cargarSql" class="btn btn-secondary col-md-12 my-2">
                Restaurar respaldo
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Restaurar respaldo
                </h3>
            </div>
        </div>

        <div class="container mb-5">
            <form id="formu" action="/respaldo/ejecutarScriptSql" enctype="multipart/form-data" method="POST" class="needs-validation" novalidate>
                <div class="form-group">
                    <label>Archivo SQL<font color="red">*</font></label>
                    <div class="custom-file">
                    <label class="custom-file-label" for="archivoSql">Elija un archivo</label>
                    <input type="file" class="custom-file-input" id="archivoSql" name="archivoSql" accept=".sql" required>
                    <div class="invalid-feedback">
                        Por favor elija el archivo a subir.
                    </div>
                    </div>
                </div>
                <div class="form-row justify-content-end">
                    <p>
                        <a onclick="mostrarProceso();" href="respaldo/listar" class="btn btn-secondary">Cancelar</a>
                        <button type="button" class="btn btn-primary" data-id="1" data-toggle="modal" data-target="#modalCarga">
                            Restaurar
                        </button>
                    </p>
                </div>
                <div class="modal fade" id="modalCarga" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered" role="document">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title" id="exampleModalLongTitle">Restaurar respaldo</h5>
                                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
                            <div class="modal-body">
                                <div class="row">
                                    <div class="col col-md-1 mr-3">
                                        <h1><i class="bi bi-exclamation-octagon"></i></h1>
                                    </div>
                                    <div class="col col-md-10">
                                        Los datos almacenados actualmente en la base de datos seran remplazados por los datos del respaldo seleccionado.
                                    </div>
                                </div>

                                
                                ¿Desea continuar con la restauración?
                                
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                                <button type="submit" class="btn btn-primary" onclick="cerrarModal()">Aceptar</button>
                            </div>
                        </div>
                    </div>
                </div>
            </form>
        </div>

        <asset:javascript src="filter-buttons.js"/>

        <script>
            function cerrarModal(){
                $('#modalCarga').modal('hide')
            }
        </script>

    </body>
</html>