<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Mesa de control</title>
        <asset:javascript src="main.js"/>
        <asset:stylesheet src="grafica.css" />
    </head>
    <body>

        <!-- Acciones -->
        <content tag="buttons">
            <a href="#" class="btn btn-primary col-md-12 my-2 disabled">
            </a>
        </content>

        <div class="row mb-5">
            <div class="col-md-12 pt-2 border-bottom">
                <h3 class="page-title pl-3">
                    Mesa de control
                </h3>
            </div>
        </div>
        <div class="container">

            <div class="row">
                <g:render template="card" model="[
                    'title':'Certificados',
                    'icon':'bi bi-file-earmark-text',
                    'datos': datosCertificados,
                ]"/>

                <g:render template="card" model="[
                    'title':'Constancias',
                    'icon':'bi bi-file-earmark-text',
                    'datos': datosConstancias,
                ]"/>

                <g:render template="card" model="[
                    'title':'Notificaciones',
                    'icon':'bi bi-file-earmark-text',
                    'datos': datosNotificaciones,
                ]"/>

                <g:render template="card" model="[
                    'title':'Actas',
                    'icon':'bi bi-file-earmark-text',
                    'datos': datosActas,
                ]"/>

                <g:render template="card" model="[
                    'title':'Certificados Públicas',
                    'icon':'bi bi-file-earmark-text',
                    'datos': datosCertificadosPublicas,
                ]"/>

                <g:render template="card" model="[
                    'title':'Certificados Privadas',
                    'icon':'bi bi-file-earmark-text',
                    'datos': datosCertificadosPrivadas,
                ]"/>

                <g:render template="card" model="[
                    'title':'Instituciones',
                    'icon':'bi bi-building',
                    'datos': datosInstituciones,
                ]"/>

                <g:render template="card" model="[
                    'title':'Constancias Privadas',
                    'icon':'bi bi-file-earmark-text',
                    'datos': datosConstanciasPrivadas,
                ]"/>

                <g:render template="card" model="[
                    'title':'Notificaciones Privadas',
                    'icon':'bi bi-file-earmark-text',
                    'datos': datosNotificacionesPrivadas,
                ]"/>

                <g:render template="card" model="[
                    'title':'Actas Privadas',
                    'icon':'bi bi-file-earmark-text',
                    'datos': datosActasPrivadas,
                ]"/>

                <g:render template="card" model="[
                    'title':'Alumnos',
                    'icon':'bi bi-person',
                    'datos': datosAlumnos,
                ]"/>
            </div>
            <section class="grafica">
                <canvas id="myChart" heihgt="100" width="100"></canvas>
            </section>
        </div>
        
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

    </body>
</html>