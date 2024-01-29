<%
    def metodo
    if(!request.getRequestURI().equals("/")){
        def uri = request.getRequestURI() + "/"
        uri = uri.substring(1);
        def fisrtDiagonal = uri.indexOf("/")
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
    ]
%>

<g:if test="${controllerName == 'certificado'}">
    <li class="nav-item mb-2">
        <a
            onclick = "mostrarProceso();" 
            class = "nav-link-b ${certificadoFirmarLinks.contains(metodo) ?'active':''}" 
            href = "${createLink(uri: 'certificado/listarFirmasCertificados')}">
            Certificados a firmar
        </a>
    </li>
    <li class="nav-item mb-2">
        <a
            onclick = "mostrarProceso();" 
            class = "nav-link-b ${certificadoLinks.contains(metodo) ? 'active':''}" 
            href = "${createLink(uri: 'certificado/listarCertificados')}">
            Certificados
        </a>
    </li>
</g:if>

<g:if test="${controllerName == 'firmaElectronica'}">
    <li class="nav-item mb-2">
        <a
            onclick = "mostrarProceso();" 
            class = "nav-link-b active" 
            href = "${createLink(uri: 'firmaElectronica/consultar')}">
            Firma electrónica
        </a>
    </li>
</g:if>