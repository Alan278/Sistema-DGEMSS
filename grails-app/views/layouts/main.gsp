<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="description" content="">
    <meta name="author" content="">
    <base href="${createLink(uri:"/")}">
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
    <title>
        <g:layoutTitle default="Sistema Estatal de Gestión Escolar"/>
    </title>
    <asset:stylesheet src="application.css"/>
    <asset:stylesheet src="jquery-editable-select.css"/>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.0/font/bootstrap-icons.css">
    <g:layoutHead/>
</head>
<body>
    <div class="container-fluid p-0">
        <!-- <div style="width: 100%; min-height: 20px; background: #fff3cd; border: solid 1px #ffeeba; color: #856404; text-align: center;">
            La presente p&aacute;gina web se encuentra en desarrollo, por lo que toda informaci&oacute;n capturada y mostrada aqu&iacute;, carece de validez oficial.
        </div> -->
        <g:render template="/layouts/header"/>
        <g:render template="/layouts/mainNav"/>
        <div class="row col-md-12 ">
            <g:render template="/layouts/sidebar"/>
            <main role="main" class="container col-md-10">
                <g:layoutBody/>
                <g:if test="${pageProperty(name: "page.footer")?.contains("a")}">
                    <g:render template="/layouts/footerA"/>
                </g:if>
            </main>
        </div>
        <g:if test="${pageProperty(name: "page.footer")?.contains("b")}">
            <g:render template="/layouts/footerB"/>
        </g:if>
        <g:if test="${pageProperty(name: "page.footer")?.contains("c")}">
            <g:render template="/layouts/footerC"/>
        </g:if>
        <asset:javascript src="application.js"/>
        <g:pageProperty name="page.assets" />
    </div>

    <div class="modal fade" id="procesando" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="exampleModalLongTitle">Procesando...</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="progress">
                    <div class="progress-bar progress-bar-striped progress-bar-animated" role="progressbar" aria-valuenow="75" aria-valuemin="0" aria-valuemax="100" style="width: 100%"></div>
                    </div>
                </div>
                <div class="modal-footer">
                </div>
            </div>
        </div>
    </div>

</body>
</html>
