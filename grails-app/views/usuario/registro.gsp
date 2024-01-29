<!doctype html>
<html>
    <head>
        <meta charset="utf-8">
        <meta name="layout" content="main"/>
        <title>Nuevos Usuarios</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
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
                    Nuevo Usuario
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
            <form action="/usuario/registrar" class="needs-validation" novalidate>
                <g:render template="form"/>
                <div class="form-row justify-content-end">
                    <p>
                        <a onclick="mostrarProceso();" href="usuario/listar" class="btn btn-secondary">Cancelar</a>
                        <button type="submit" class="btn btn-primary">Guardar</button>
                    </p>
                </div>
            </form>
        </div>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <asset:javascript src="jquery-editable-select.js"/>
        <asset:javascript src="jquery-editable-select.min.js"/>
        <asset:javascript src="filter-buttons.js"/>
        <asset:javascript src="validations.js"/>
        <asset:javascript src="userForm.js"/>
        <script>
            cargarInstituciones()
            cargarRoles()

            function cargarInstituciones(){
                var institucionesInput = document.getElementById("listaInstituciones")
                if(!institucionesInput.value == ""){
                    instituciones = JSON.parse(institucionesInput.value)
                }
                mostrarTabla()
            }


            function cargarRoles(){
                var rolesInput = document.getElementById("listaRoles")
                if(!rolesInput.value == ""){
                    roles = JSON.parse(rolesInput.value)
                }
                mostrarTablaRoles()
            }
        </script>
    </body>
</html>