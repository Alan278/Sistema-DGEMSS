<%
    def metodo
    if(!request.getRequestURI().equals("/")){
        def uri = request.getRequestURI() + "/"
        uri = uri.substring(1);
        fisrtDiagonal = uri.indexOf("/")
        def secondDiagonal = uri.indexOf("/", fisrtDiagonal+1)
        metodo = uri.substring(fisrtDiagonal+1, secondDiagonal);
    }

    def certificadoFirmarLinks = [
        "listarFirmasCertificados",
        "revision",
        "firmar",
    ]

    def certificadoLinks = [
        "listarCertificados",
        "consultar",
        "listarCertificadosAlumno",
        "listarAlumnos",
        "solicitud",
        "subirFotoModificacion",
    ]

    def certificadoRevisar = [
        "listarCertificadosRevisar",
        "revision",
        "registro",
    ]

    def certificadoFirmarDgemssLinks = [
        "listarInstituciones",
        "listarCertificadosInstitucion",
        "firmaCertificados",
    ]
%>

<g:if test="${controllerName == 'certificado' || controllerName == 'dashBoard'}">
    <sec:ifAnyGranted roles='ROLE_AUTENTICADOR_DGEMSS'>
        <li class="nav-item mb-2">
            <a
                onclick = "mostrarProceso();"
                class = "nav-link-b ${controllerName == 'dashBoard' ?'active':''}"
                href = "${createLink(uri: 'dashBoard/index')}">
                Mesa de control
            </a>
        </li>
    </sec:ifAnyGranted>
    <li class="nav-item mb-2">
        <a
            onclick = "mostrarProceso();"
            class = "nav-link-b ${certificadoLinks.contains(metodo) ? 'active':''}"
            href = "${createLink(uri: 'certificado/listarCertificados')}">
            Certificados
        </a>
    </li>
    <sec:ifAnyGranted roles='ROLE_DIRECTOR_ESCUELA'>
        <li class="nav-item mb-2">
            <a
                onclick = "mostrarProceso();"
                class = "nav-link-b ${certificadoFirmarLinks.contains(metodo) ?'active':''}"
                href = "${createLink(uri: 'certificado/listarFirmasCertificados')}">
                Certificados a firmar
            </a>
        </li>
    </sec:ifAnyGranted>
    <sec:ifAnyGranted roles='ROLE_AUTENTICADOR_DGEMSS'>
        <li class="nav-item mb-2">
            <a
                onclick = "mostrarProceso();"
                class = "nav-link-b ${certificadoFirmarDgemssLinks.contains(metodo) ?'active':''}"
                href = "${createLink(uri: 'certificado/listarInstituciones')}">
                Certificados a firmar
            </a>
        </li>
    </sec:ifAnyGranted>
    <sec:ifAnyGranted roles='ROLE_REVISOR, ROLE_REVISOR_PUBLICA'>
        <li class="nav-item mb-2">
            <a
                onclick = "mostrarProceso();"
                class = "nav-link-b ${certificadoRevisar.contains(metodo) ?'active':''}"
                href = "${createLink(uri: 'certificado/listarCertificadosRevisar')}">
                Certificados a revisar
            </a>
        </li>
    </sec:ifAnyGranted>
</g:if>
