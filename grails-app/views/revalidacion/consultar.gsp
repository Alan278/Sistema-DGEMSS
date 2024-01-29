<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Detalle de Revalidaciones</title>
        <asset:stylesheet src="jquery-editable-select.css"/>
    </head>
    <body>
        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Detalle de Revalidaciones
                </h3>
            </div>
        </div>

        <div class="container">
            <div class="form-group mb-4" >
                <h5> <b> Alumno: </b> </h5>
                <h6 class="ml-3">${revalidacion?.alumno} </h6>
            </div>
            <div class="form-group mb-4" >
                <h5> <b> Institución: </b> </h5>
                <h6 class="ml-3">${revalidacion?.institucion} </h6>
            </div>
            <div class="form-group mb-4" >
                <h5> <b> Ciclo escolar cursado: </b> </h5>
                <h6 class="ml-3">${revalidacion?.cicloEscolar} </h6>
            </div>
            <div class="form-group mb-4" >
                <h5> <b> Nivel de estudios cursado: </b> </h5>
                <h6 class="ml-3">${revalidacion?.nivelExterno} </h6>
            </div>
            <div class="form-group mb-4" >
                <h5> <b> Nivel de estudios equivalente: </b> </h5>
                <h6 class="ml-3">${revalidacion?.nivelInterno} </h6>
            </div>
            <div class="form-group mb-4" >
                <h5> <b> Estado: </b> </h5>
                <h6 class="ml-3">${revalidacion?.estado} </h6>
            </div>
            <div class="form-group mb-4" >
                <h5> <b> País: </b> </h5>
                <h6 class="ml-3">${revalidacion?.pais} </h6>
            </div>
            <div class="form-group mb-4" >
                <h5> <b> Fecha de Término: </b> </h5>
                <h6 class="ml-3">${revalidacion?.fechaTerminoFormato} </h6>
            </div>
            <div class="form-group mb-4" >
                <h5> <b> Número de expediente: </b> </h5>
                <h6 class="ml-3">${revalidacion?.expediente} </h6>
            </div>
            <div class="form-group mb-4" >
                <h5> <b> Lugar de expedición: </b> </h5>
                <h6 class="ml-3">${revalidacion?.lugarExpedicion} </h6>
            </div>
            <div class="form-group mb-4" >
                <h5> <b> Fecha de expedición: </b> </h5>
                <h6 class="ml-3">${revalidacion?.fechaExpedicionFormato} </h6>
            </div>
            <div class="form-group mb-4" >
                <h5> <b> Folio: </b> </h5>
                <h6 class="ml-3">${revalidacion?.folio} </h6>
            </div>
            <div class="form-row justify-content-end">
                <p>
                    <a onclick="mostrarProceso();" href="revalidacion/listar" class="btn btn-secondary">Volver</a>
                </p>
            </div>
        </div>



        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <asset:javascript src="jquery-editable-select.js"/>
        <asset:javascript src="jquery-editable-select.min.js"/>
        <asset:javascript src="filter-buttons.js"/>
    </body>
</html>
