<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Detalles de la Acción</title>
        <script src="jquery-3.5.1.min.js"></script>
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="#" class="btn btn-primary col-md-12 my-2 disabled">
            </a>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Detalles de la Acción
                </h3>
            </div>
        </div>

        <div class="container">
            <div class="form-row mb-2">
                <div class="form-group col-md-6" >
                    <h5> <b> Nombre </b> </h5>
                    <h6 class="ml-3">${registro?.nombre} </h6>
                </div>
                <div class="form-group col-md-6" >
                    <h5> <b> Estatus </b> </h5>
                    <h6 class="ml-3">${registro?.estatus} </h6>
                </div>
            </div>

            <div class="form-group mb-4" >
                <h5> <b> Descripción </b> </h5>
                <h6 class="ml-3">${registro?.descripcion} </h6>
            </div>

            <div class="form-group mb-4" >
                <h5> <b> Usuario </b> </h5>
                <h6 class="ml-3">${registro?.usuario?.username} </h6>
            </div>

            <div class="form-row mb-2">
                <div class="form-group col-md-6" >
                    <h5> <b> Clase </b> </h5>
                    <h6 class="ml-3">${registro?.clase} </h6>
                </div>
                <div class="form-group col-md-6" >
                    <h5> <b> Método </b> </h5>
                    <h6 class="ml-3">${registro?.metodo} </h6>
                </div>
            </div>

            <div class="form-group mb-4" >
                <h5> <b> Fecha </b> </h5>
                <h6 class="ml-3">${registro?.fechaRegistro} </h6>
            </div>
        </div>

        <asset:javascript src="filter-buttons.js"/>

    </body>
</html>