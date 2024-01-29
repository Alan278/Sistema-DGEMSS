<!doctype html>
<html>
     <head>
        <meta name="layout" content="main"/>
        <title>Lista de formaciones</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/formacion/registro?planEstudiosId=${params.planEstudiosId}" class="btn btn-primary col-md-12 my-2">
                Agregar formación
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Lista de formaciones
                </h3>
            </div>
        </div>

        <g:render
            template="/mensaje"
            model="[
                'estatus': flash.estatus,
                'mensaje': flash.mensaje
            ]"
        />

        <div class="container mb-4">
            <div class="jumbotron p-3">
                <h6 class="m-0 pb-1">
                    <b>Institución:</b>
                    <medium class="text-muted">${planEstudios?.carrera?.institucion?.nombre} </medium>
                </h6>
                <h6 class="m-0 pb-1" >
                    <b>Carrera:</b>
                    <medium class="text-muted">${planEstudios?.carrera?.nombre}</medium>
                </h6>
                <h6 class="m-0 pb-1" >
                    <b>Plan de estudios:</b>
                    <medium class="text-muted">${planEstudios?.nombre}</medium>
                </h6>
                <h6 class="m-0 pb-1" >
                    <b>RVOE:</b>
                    <medium class="text-muted">${planEstudios?.rvoe}-${planEstudios?.fechaRvoeFormato}</medium>
                </h6>
            </div>
        </div>

        <div class="container mb-5">
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Nombre</th>
                        <th>Requerida</th>
                        <th>General</th>
                        <th>Tipo de formación</th>
                        <th></th>
                    </tr>
                </thead>
                <tBody>
                    <g:each status="i" in="${formaciones}" var="formacion">
                            <tr>
                                <td class="align-middle">${i+1}</td>
                                <td class="align-middle">${formacion?.nombre}</td>
                                <td class="align-middle">${formacion?.requerida ? "REQUERIDA" : ""}</td>
                                <td class="align-middle">${formacion?.general ? "GENERAL" : ""}</td>
                                <td class="align-middle">${formacion?.tipoFormacion?.nombre}</td>
                                <td class="align-middle" align="end">
                                    <a onclick="mostrarProceso();" href="/formacion/modificacion/${formacion.id}" class="btn btn-outline-primary btn-sm" data-tooltip="tooltip" data-placement="top" title="Editar registro">
                                        <i class="fa fa-pencil"></i>
                                    </a>
                                    <g:render template="/deleteModal" model="[
                                        'registroId': formacion?.id,
                                        'titulo':'Eliminar formación',
                                        'descripcion':'¿Desea eliminar la formacion?',
                                        'url':'formacion/eliminar',
                                    ]"/>
                                </td>
                            </tr>
                    </g:each>
                </tBody>
            </table>
        </div>
    </body>
</html>