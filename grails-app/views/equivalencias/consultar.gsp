<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Detalle de Equivalencias</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>
        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Detalle de Equivalencias
                </h3>
            </div>
        </div>
        <div class="container">
            <div class="form-group mb-4" >
                <h5> <b> Institución: </b> </h5>
                <h6 class="ml-3">${equivalencia?.institucion} </h6>
            </div>
            <div class="form-group mb-4" >
                <h5> <b> Nivel de procedencia: </b> </h5>
                <h6 class="ml-3">${equivalencia?.nivelExterno} </h6>
            </div>
            <div class="form-group mb-4" >
                <h5> <b> Nivel equiparable: </b> </h5>
                <h6 class="ml-3">${equivalencia?.nivelInterno} </h6>
            </div>
            <div class="form-group mb-4" >
                <h5> <b> Ciclo Escolar: </b> </h5>
                <h6 class="ml-3">${equivalencia?.cicloEscolar} </h6>
            </div>
            <div class="form-group mb-4" >
                <h5> <b> Folio: </b> </h5>
                <h6 class="ml-3">${equivalencia?.folio} </h6>
            </div>
            <div class="form-group mb-4" >
                <h5> <b> Número de expediente: </b> </h5>
                <h6 class="ml-3">${equivalencia?.expediente} </h6>
            </div>
            <div class="form-group mb-4" >
                <h5> <b> Lugar de expedición: </b> </h5>
                <h6 class="ml-3">${equivalencia?.lugarExpedicion} </h6>
            </div>
            <div class="form-group mb-4" >
                <h5> <b> Fecha de expedición: </b> </h5>
                <h6 class="ml-3">${equivalencia?.fechaExpedicionFormato} </h6>
            </div>
            <div class="form-group mb-4" >
                <h5> <b> C.C.T: </b> </h5>
                <h6 class="ml-3">${equivalencia?.cct} </h6>
            </div>
        </div>
         <div class="container">
            <table class="table table-striped" >
                <thead>
                    <tr>
                        <th>Asignatura cursada</th>
                        <th>Asignatura equivalente</th>
                        <th>Calificación</th>
                    </tr>
                </thead>
                <tbody>
                    <g:each status="i" in="${equivalencia?.asignaturas}" var="asignatura">
                        <tr>
                            <td>${asignatura.asignaturaCursada}</td>
                            <td>${asignatura.asignaturaEquivalente}</td>
                            <td>${asignatura.calificacion}</td>
                        </tr>
                    </g:each>
                </tbody>

            </table>


            <div class="form-row justify-content-end">
                <p>
                    <a onclick="mostrarProceso();" href="/equivalencias/listar" class="btn btn-secondary">Volver</a>

                </p>
            </div>
        </div>

        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <asset:javascript src="jquery-editable-select.js"/>
        <asset:javascript src="jquery-editable-select.min.js"/>
        <asset:javascript src="filter-buttons.js"/>
    </body>
</html>















