<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Detalles del Ciclo Escolar</title>
        <script src="jquery-3.5.1.min.js"></script>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="/cicloEscolar/subirExcelAlumnos/${cicloEscolar?.id}" class="btn btn-primary col-md-12 my-2">
                Cargar alumnos
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Detalles del ciclo escolar
                </h3>
            </div>
        </div>

        <div class="container">
            <div class="jumbotron p-3 mb-4">
                <h6 class="m-0 ">
                    <b> Ciclo escolar:</b>
                    <medium class="text-muted">${cicloEscolar?.nombre}</medium><br>
                </h6>
            </div>
            <ul class="nav nav-tabs mb-4">
                <li class="nav-item">
                  <a class="nav-link active" href="/cicloEscolar/consultar/${params?.id}">Detalles</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/cicloEscolar/listarAlumnos/${params?.id}">Alumnos (${cicloEscolar?.numAlumnos})</a>
                  </li>
            </ul>
            <div class="pl-1">
                <h6 class="pb-1">
                    <b> Institución:</b>
                    <medium class="text-muted">${cicloEscolar?.planEstudios?.carrera?.institucion?.nombre}</medium>
                </h6>
                <h6 class="pb-1">
                    <b> Carrera:</b>
                    <medium class="text-muted">${cicloEscolar?.planEstudios?.carrera?.nombre}</medium>
                </h6>
                <h6 class="pb-1">
                    <b> Plan de estudios:</b>
                    <medium class="text-muted">${cicloEscolar?.planEstudios?.nombre}</medium>
                </h6>
                <h6 class="pb-1">
                    <b> Fechas:</b>
                    <medium class="text-muted">${cicloEscolar.inicio} - ${cicloEscolar.fin}</medium>
                </h6>
                <h6 class="pb-1">
                    <b> Periodo:</b>
                    <medium class="text-muted">${cicloEscolar?.periodo}</medium>
                </h6>
            </div>
        </div>

        <div class="container">
            <div class="form-row justify-content-end">
                <p>
                    <g:if test="${cicloEscolar.estatusRegistro.id == 1}">
                        <a onclick="mostrarProceso();" href="cicloEscolar/modificacion/${cicloEscolar?.id}" class="btn btn-secondary">
                            Modificar
                        </a>
                        <button type="button" class="btn btn-outline-danger" data-toggle="modal" data-target="#modalEliminacion">
                            Eliminar
                        </button>
                    </g:if>
                </p>
            </div>
        </div>

        <div class="modal fade" id="modalEliminacion" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="exampleModalLongTitle">Eliminar Ciclo Escolar</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        ¿Desea eliminar el ciclo escolar?
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                        <a onclick="mostrarProceso();" href="cicloEscolar/eliminar/${cicloEscolar?.id}" class="btn btn-primary">Aceptar</a>
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