<%
    def metodo
    if(!request.getRequestURI().equals("/")){
        def uri = request.getRequestURI() + "/"
        uri = uri.substring(1);
        def fisrtDiagonal = uri.indexOf("/")
        def secondDiagonal = uri.indexOf("/", fisrtDiagonal + 1)
        metodo = uri.substring(fisrtDiagonal + 1, secondDiagonal);
    }


    def moduloCatalogo = [
        "uma",
        "tipoTramite",
    ]

    def tipoTramiteLinks = [
        "uma",
        "tipoTramite",
    ]
%>


<g:if test="${controllerName  == 'bitacora'}">
    <li class="nav-item mb-2">
        <a
            onclick = "mostrarProceso();"
            class = "nav-link-b ${controllerName == 'bitacora'?'active':''}"
            href = "${createLink(uri: 'bitacora/listar')}">
            Bitácora
        </a>
    </li>
</g:if>
<g:if test="${controllerName  == 'respaldo'}">
    <li class="nav-item mb-2">
        <a
            onclick = "mostrarProceso();"
            class = "nav-link-b ${controllerName == 'respaldo'?'active':''}"
            href = "${createLink(uri: 'respaldo/listar')}">
            Respaldos
        </a>
    </li>
</g:if>

<g:if test="${controllerName  == 'usuario'}">
    <li class="nav-item mb-2">
        <a
            onclick = "mostrarProceso();"
            class = "nav-link-b ${controllerName == 'usuario'?'active':''}"
            href = "${createLink(uri: 'usuario/listar')}">
            Usuarios
        </a>
    </li>
</g:if>

<g:if test="${moduloCatalogo.contains(controllerName)}">
    <li class="nav-item mb-2">
        <a
            onclick = "mostrarProceso();"
            class = "nav-link-b ${moduloCatalogo.contains(controllerName)?'active':''}"
            href = "${createLink(uri: 'tipoTramite/listar')}">
            Tipo trámite
        </a>
    </li>
</g:if>