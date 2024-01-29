<%
    def metodo
    if(!request.getRequestURI().equals("/")){
        def uri = request.getRequestURI() + "/"
        uri = uri.substring(1);
        fisrtDiagonal = uri.indexOf("/")
        def secondDiagonal = uri.indexOf("/", fisrtDiagonal+1)
        metodo = uri.substring(fisrtDiagonal+1, secondDiagonal);
    }

    def notificacionFirmarLinks = [
        "listarFirmasNotificaciones",
        "revision",
        "firmar",
    ]

    def notificacionLinks = [
        "listarNotificaciones",
        "consultar",
        "listarNotificacionesAlumno",
        "listarAlumnos",
        "solicitud",
    ]

    def notificacionRevisar = [
        "listarNotificacionesRevisar",
        "revision",
        "registro",
    ]

    def notificacionFirmarDgemssLinks = [
        "listarInstituciones",
        "listarNotificacionesInstitucion",
        "firmaNotificaciones",
        "listarNotificacionesFirmar",
        "consultarNotificacionesfecha"
    ]
%>

<g:if test="${controllerName == 'notificacionProfesional'}">
    <sec:ifAnyGranted roles='ROLE_AUTENTICADOR_DGEMSS'>
        <li class="nav-item mb-2">
            <a
                onclick = "mostrarProceso();"
                class = "nav-link-b ${notificacionFirmarLinks.contains(metodo) ?'active':''}"
                href = "${createLink(uri: 'dashBoard/index')}">
                Mesa de control
            </a>
        </li>
    </sec:ifAnyGranted>
    <li class="nav-item mb-2">
        <a
            onclick = "mostrarProceso();"
            class = "nav-link-b ${notificacionLinks.contains(metodo) ? 'active':''}"
            href = "${createLink(uri: 'NotificacionProfesional/listarNotificaciones')}">
            Notificaciones
        </a>
    </li>
    <sec:ifAnyGranted roles='ROLE_DIRECTOR_ESCUELA'>
        <li class="nav-item mb-2">
            <a
                onclick = "mostrarProceso();"
                class = "nav-link-b ${notificacionFirmarLinks.contains(metodo) ?'active':''}"
                href = "${createLink(uri: 'NotificacionProfesional/listarFirmasNotificaciones')}">
                Notificaciones a firmar
            </a>
        </li>
    </sec:ifAnyGranted>
    <sec:ifAnyGranted roles='ROLE_AUTENTICADOR_DGEMSS'>
        <li class="nav-item mb-2">
            <a
                onclick = "mostrarProceso();"
                class = "nav-link-b ${notificacionFirmarDgemssLinks.contains(metodo) ?'active':''}"
                href = "${createLink(uri: 'NotificacionProfesional/listarNotificacionesFirmar')}">
                Notificaciones a firmar
            </a>
        </li>
    </sec:ifAnyGranted>

    <sec:ifAnyGranted roles='ROLE_AUTENTICADOR_DGEMSS'>
        <li class="nav-item mb-2">
            <a
                onclick = "mostrarProceso();"
                class = "nav-link-b ${notificacionFirmarDgemssLinks.contains(metodo) ?'active':''}"
                href = "${createLink(uri: 'NotificacionProfesional/consultarNotificacionesfecha')}">
                Notificaciones por fecha
            </a>
        </li>
    </sec:ifAnyGranted>

    <sec:ifAnyGranted roles='ROLE_REVISOR, ROLE_REVISOR_PUBLICA'>
        <li class="nav-item mb-2">
            <a
                onclick = "mostrarProceso();"
                class = "nav-link-b ${notificacionRevisar.contains(metodo) ?'active':''}"
                href = "${createLink(uri: 'NotificacionProfesional/listarNotificacionesRevisar')}">
                Notificaciones a revisar
            </a>
        </li>
    </sec:ifAnyGranted>
</g:if>