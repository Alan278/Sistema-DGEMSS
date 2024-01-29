<%
    def metodo
    if(!request.getRequestURI().equals("/")){
        def uri = request.getRequestURI() + "/"
        uri = uri.substring(1);
        fisrtDiagonal = uri.indexOf("/")
        def secondDiagonal = uri.indexOf("/", fisrtDiagonal+1)
        metodo = uri.substring(fisrtDiagonal+1, secondDiagonal);
    }

    def constanciaFirmarLinks = [
        "listarFirmasConstancias",
        "revision",
        "firmar",
    ]

    def constanciaLinks = [
        "listarConstancias",
        "consultar",
        "listarConstanciasAlumno",
        "listarAlumnos",
        "solicitud",
    ]

    def constanciaRevisar = [
        "listarConstanciasRevisar",
        "revision",
        "registro",
    ]

    def constanciaFirmarDgemssLinks = [
        "listarInstituciones",
        "listarConstanciasInstitucion",
        "firmaConstancias",
        "listarConstanciasFirmar",
        "consultarConstanciasfecha"
    ]
%>

<g:if test="${controllerName == 'constanciaservicio'}">
    <sec:ifAnyGranted roles='ROLE_AUTENTICADOR_DGEMSS'>
        <li class="nav-item mb-2">
            <a
                onclick = "mostrarProceso();"
                class = "nav-link-b ${constanciaFirmarLinks.contains(metodo) ?'active':''}"
                href = "${createLink(uri: 'dashBoard/index')}">
                Mesa de control
            </a>
        </li>
    </sec:ifAnyGranted>
    <li class="nav-item mb-2">
        <a
            onclick = "mostrarProceso();"
            class = "nav-link-b ${constanciaLinks.contains(metodo) ? 'active':''}"
            href = "${createLink(uri: 'constanciaservicio/listarConstancias')}">
            Constancias
        </a>
    </li>
    <sec:ifAnyGranted roles='ROLE_DIRECTOR_ESCUELA'>
        <li class="nav-item mb-2">
            <a
                onclick = "mostrarProceso();"
                class = "nav-link-b ${constanciaFirmarLinks.contains(metodo) ?'active':''}"
                href = "${createLink(uri: 'constanciaservicio/listarFirmasConstancias')}">
                Constancias a firmar
            </a>
        </li>
    </sec:ifAnyGranted>
    <sec:ifAnyGranted roles='ROLE_AUTENTICADOR_DGEMSS'>
        <li class="nav-item mb-2">
            <a
                onclick = "mostrarProceso();"
                class = "nav-link-b ${constanciaFirmarDgemssLinks.contains(metodo) ?'active':''}"
                href = "${createLink(uri: 'constanciaservicio/listarConstanciasFirmar')}">
                Constancias a firmar
            </a>
        </li>
    </sec:ifAnyGranted>

    <sec:ifAnyGranted roles='ROLE_AUTENTICADOR_DGEMSS'>
        <li class="nav-item mb-2">
            <a
                onclick = "mostrarProceso();"
                class = "nav-link-b ${constanciaFirmarDgemssLinks.contains(metodo) ?'active':''}"
                href = "${createLink(uri: 'constanciaservicio/consultarConstanciasfecha')}">
                Constancias por fecha
            </a>
        </li>
    </sec:ifAnyGranted>

    <sec:ifAnyGranted roles='ROLE_REVISOR, ROLE_REVISOR_PUBLICA'>
        <li class="nav-item mb-2">
            <a
                onclick = "mostrarProceso();"
                class = "nav-link-b ${constanciaRevisar.contains(metodo) ?'active':''}"
                href = "${createLink(uri: 'constanciaservicio/listarConstanciasRevisar')}">
                Constancias a revisar
            </a>
        </li>
    </sec:ifAnyGranted>
</g:if>