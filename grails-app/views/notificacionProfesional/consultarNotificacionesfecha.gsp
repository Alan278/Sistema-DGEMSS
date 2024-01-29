<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Constancias expedidas</title>
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
        <asset:stylesheet src="jquery-editable-select.css"/>
        <asset:stylesheet src="grafica.css"/>
    </head>
    <body>

        <content tag="buttons">
            <sec:ifNotGranted roles='ROLE_GESTOR_ESCUELA'>
                <a href="#" class="btn btn-primary col-md-12 my-2 disabled">
                </a>
            </sec:ifNotGranted>
        </content>

        <div class="row mb-4">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Actas expedidas
                </h3>
            </div>
        </div>

        <div class="container">
            <g:if test="${flash.mensaje}">
                <g:if test="${flash.estatus}">
                    <div class="alert alert-success" role="alert">
                        ${flash.mensaje}
                    </div>
                </g:if>
                <g:else>
                    <div class="alert alert-danger" role="alert">
                        ${flash.mensaje}
                    </div>
                </g:else>
            </g:if>
        </div>

        <div class="container">
            <form id="form-busqueda" action="/NotificacionProfesional/consultarNotificacionesfecha" class="mt-4">
                <label for="fechaInicio">Fecha de inicio:</label>
                <g:datePicker name="fechaInicio" value="${params.fechaInicio}" precision="day" noSelection="['':'- Seleccione -']"/>
                <label for="fechaFin">Fecha de Fin:</label>
                <g:datePicker name="fechaFin" value="${params.fechaFin}" precision="day" noSelection="['':'- Seleccione -']"/>
                
                <g:submitButton name="buscar" value="Buscar" class="btn btn-primary" />
            </form>

        </div>

        <div class="container">
            
            <div class="table-responsive">

                <table class="table table-striped ta">
                    <thead>
                        <tr>
                            <th>#</th>
                            <g:if test="${params.isPublic == 'false' || !instituciones[0].publica}">
                                <th>No. Trámite</th>
                            </g:if>
                            <th>Alumno</th>
                            <th>Fecha resgistro</th>
                            <sec:ifAnyGranted roles='ROLE_RECEPTOR, ROLE_REVISOR, ROLE_REVISOR_PUBLICA, ROLE_AUTENTICADOR_DGEMSS'>
                                <th>Institución</th>
                            </sec:ifAnyGranted>
                           
                            <th>Estatus</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tBody>
                        <g:each in="${registros}" var="registro">
                                <tr>
                                     <td class="align-middle">${registro.id+1}</td>
                                    <g:if test="${params.isPublic == 'false' || !instituciones[0].publica}">
                                        <td class="align-middle">
                                            ${registro?.tramite?.numeroTramite}
                                        </td>
                                    </g:if>
                                    <td class="align-middle">
                                            ${registro?.alumno?.persona?.nombre} ${registro?.alumno?.persona?.primerApellido} ${registro?.alumno?.persona?.segundoApellido}
                                        </a>
                                    </td>
                                    <td class="align-middle">
                                        ${registro?.fechaRegistro}
                                    </td>
                                    <sec:ifAnyGranted roles='ROLE_RECEPTOR, ROLE_REVISOR, ROLE_REVISOR_PUBLICA, ROLE_AUTENTICADOR_DGEMSS'>
                                        <td class="align-middle">
                                            ${registro?.alumno?.planEstudios?.carrera?.institucion?.nombre}
                                        </td>
                                    </sec:ifAnyGranted>
                                    <td class="align-middle">
                                        ${registro?.estatusNotificacion?.nombre}
                                    </td>
                                    
                                    </td>
                                </tr>
                        </g:each>
                    </tBody>
                </table>
            </div>
            <canvas id="grafica-actas"></canvas>
        </div>
       


        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <asset:javascript src="jquery-editable-select.js"/>
        <asset:javascript src="jquery-editable-select.min.js"/>
        <asset:javascript src="filter-buttons.js"/>

       <script type="text/javascript">
            
            var etiquetas = ["GENERADO","FIRMANDO_ESCUELA","RECHAZADO_DIRECTOR","EN_ESPERA","EN_REVISION","RECHAZADO_DGEMSS","FIRMANDO_DGEMSS","RECHAZADO_AUTENTICADOR","FINALIZADO"];
            var datos = ${datos as grails.converters.JSON};
            console.log()
            console.log(etiquetas)
            console.log(datos)
            
            var ctx = document.getElementById('grafica-actas').getContext('2d');
            var myChart = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: etiquetas,
                    datasets: [{
                        label: 'Cantidad',
                        data: datos,
                        backgroundColor: 'rgba(54, 162, 235, 0.2)',
                        borderColor: 'rgba(54, 162, 235, 1)',
                        borderWidth: 1
                    }]
                },
                options: {
                    scales: {
                        yAxes: [{
                            ticks: {
                                beginAtZero: true
                            }
                        }]
                    }
                }
            });
        </script>
        
        <script>
            $(function () {
                $('[data-toggle="tooltip"]').tooltip()
            })
            function ventanaSecundaria (URL){
                window.open(URL,"ventana1","width=600,height=600, top=30, left=380")
            }
            $(function () {
                $('[data-tooltip="tooltip"]').tooltip()
            })

            $("#privadas").change(function() {
                $("#form-busqueda").submit()
            });

            $("#publicas").change(function() {
                $("#form-busqueda").submit()
            });
        </script>
    </body>
</html>