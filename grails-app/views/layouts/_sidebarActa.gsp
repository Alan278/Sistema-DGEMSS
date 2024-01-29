<%
    def metodo
    if(!request.getRequestURI().equals("/")){
        def uri = request.getRequestURI() + "/"
        uri = uri.substring(1);
        fisrtDiagonal = uri.indexOf("/")
        def secondDiagonal = uri.indexOf("/", fisrtDiagonal+1)
        metodo = uri.substring(fisrtDiagonal+1, secondDiagonal);
    }

    def actaFirmarLinks = [
        "listarFirmasActas",
        "revision",
        "firmar",
        

    ]

    def actaLinks = [
        "listarActas",
        "consultar",
        "listarActasAlumno",
        "listarAlumnos",
        "solicitud",
    ]

    def actaRevisar = [
        "listarActasRevisar",
        "revision",
        "registro",
    ]

    def actaFirmarDgemssLinks = [
        "listarInstituciones",
        "listarNotificacionesInstitucion",
        "firmaActas",
        "listarActasFirmar",
        "consultarActafecha"
    ]
%>

<g:if test="${controllerName == 'actaprofesional'}">
    <sec:ifAnyGranted roles='ROLE_AUTENTICADOR_DGEMSS'>
        <li class="nav-item mb-2">
            <a
                onclick = "mostrarProceso();"
                class = "nav-link-b ${actaFirmarLinks.contains(metodo) ?'active':''}"
                href = "${createLink(uri: 'dashBoard/index')}">
                Mesa de control
            </a>
        </li>
    </sec:ifAnyGranted>
    <li class="nav-item mb-2">
        <a
            onclick = "mostrarProceso();"
            class = "nav-link-b ${actaLinks.contains(metodo) ? 'active':''}"
            href = "${createLink(uri: 'actaprofesional/listarActas')}">
            Actas
        </a>
    </li>
    <sec:ifAnyGranted roles='ROLE_DIRECTOR_ESCUELA'>
        <li class="nav-item mb-2">
            <a
                onclick = "mostrarProceso();"
                class = "nav-link-b ${actaFirmarLinks.contains(metodo) ?'active':''}"
                href = "${createLink(uri: 'actaprofesional/listarFirmasActas')}">
                Actas a firmar
            </a>
        </li>
    </sec:ifAnyGranted>
    <sec:ifAnyGranted roles='ROLE_AUTENTICADOR_DGEMSS'>
        <li class="nav-item mb-2">
            <a
                onclick = "mostrarProceso();"
                class = "nav-link-b ${actaFirmarDgemssLinks.contains(metodo) ?'active':''}"
                href = "${createLink(uri: 'actaprofesional/listarActasFirmar')}">
                Actas a firmar
            </a>
        </li>
    </sec:ifAnyGranted>

    <sec:ifAnyGranted roles='ROLE_AUTENTICADOR_DGEMSS'>
        <li class="nav-item mb-2">
            <a
                onclick = "mostrarProceso();"
                class = "nav-link-b ${actaFirmarDgemssLinks.contains(metodo) ?'active':''}"
                href = "${createLink(uri: 'actaprofesional/consultarActasfecha')}">
                Actas por fecha
            </a>
        </li>
    </sec:ifAnyGranted>

    <sec:ifAnyGranted roles='ROLE_REVISOR, ROLE_REVISOR_PUBLICA'>
        <li class="nav-item mb-2">
            <a
                onclick = "mostrarProceso();"
                class = "nav-link-b ${actaRevisar.contains(metodo) ?'active':''}"
                href = "${createLink(uri: 'actaprofesional/listarActasRevisar')}">
                Actas a revisar
            </a>
        </li>
    </sec:ifAnyGranted>
</g:if>