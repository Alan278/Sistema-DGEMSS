<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Detalles de la carrera Foránea / Externa</title>
        <script src="jquery-3.5.1.min.js"></script>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/carreraExterna/registro" class="btn btn-primary col-md-12 my-2">
                Nueva Carrera Externa
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Detalles de la carrera Foránea / Externa
                </h3>
            </div>
        </div>

        <div class="container">
            <div class="form-group mb-4" >
                <h5> <b> Institución </b> </h5>
                <h6 class="ml-3">${carrera?.institucion?.nombre} </h6>
            </div>
            <div class="form-row mb-2">
                <div class="form-group col-md-6" >
                    <h5> <b> Nombre </b> </h5>
                    <h6 class="ml-3">${carrera?.nombre} </h6>
                </div>
                <div class="form-group col-md-6" >
                    <h5> <b> Número de planes de estudios </b> </h5>
                    <h6 class="ml-3"><a onclick="mostrarProceso();" href="/planEstudiosExterna/listar?institucionId=${carrera.institucion.id}&carreraId=${carrera.id}">${params?.numeroPlanes}<a></h6>
                </div>
            </div>
            <div class="form-row mb-2">
                <div class="form-group col-md-6" >
                    <h5> <b> Clave SEEM </b> </h5>
                    <h6 class="ml-3">${carrera?.claveSeem} </h6>
                </div>
                <div class="form-group col-md-6" >
                    <h5> <b> Clave DGP </b> </h5>
                    <h6 class="ml-3">${carrera?.claveDgp} </h6>
                </div>
            </div>
            <div class="form-row mb-2">
                <div class="form-group col-md-6" >
                    <h5> <b> Número de RVOE </b> </h5>
                    <h6 class="ml-3">${carrera?.rvoe} </h6>
                </div>
                <div class="form-group col-md-6" >
                    <h5> <b> Fecha de RVOE </b> </h5>
                    <h6 class="ml-3">${fechaRvoe} </h6>
                </div>
            </div>
            <div class="form-row mb-2">
                <div class="form-group col-md-6" >
                    <h5> <b> Nivel </b> </h5>
                    <h6 class="ml-3">${carrera?.nivel?.nombre} </h6>
                </div>
                <div class="form-group col-md-6" >
                    <h5> <b> Modalidad </b> </h5>
                    <h6 class="ml-3">${carrera?.modalidad?.nombre} </h6>
                </div>
            </div>
            <div class="form-group" >
                <h5> <b> Área </b> </h5>
                <h6 class="ml-3">${carrera?.area?.nombre} </h6>
            </div>

            <div class="form-row justify-content-end">
                <p>
                    <a onclick="mostrarProceso();" href="planEstudiosExterna/registro?institucionId=${carrera?.institucion?.id}&carreraId=${carrera?.id}" class="btn btn-primary">
                        Nuevo Plan
                    </a>
                    <a onclick="mostrarProceso();" href="carreraExterna/modificacion/${carrera?.id}" class="btn btn-primary">
                        Modificar
                    </a>
                    <button type="button" class="btn btn-danger" data-toggle="modal" data-target="#modalEliminacion">
                        Eliminar
                    </button>
                </p>
            </div>
        </div>

        <div class="modal fade" id="modalEliminacion" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="exampleModalLongTitle">Eliminar Carrera Foránea / Externa</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        ¿Desea eliminar la carrera?
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                        <a onclick="mostrarProceso();" href="carreraExterna/eliminar/${carrera?.id}" class="btn btn-primary">Aceptar</a>
                    </div>
                </div>
            </div>
        </div>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <asset:javascript src="jquery-editable-select.js"/>
        <asset:javascript src="jquery-editable-select.min.js"/>
        <asset:javascript src="filter-buttons.js"/>

    </body>
</html>