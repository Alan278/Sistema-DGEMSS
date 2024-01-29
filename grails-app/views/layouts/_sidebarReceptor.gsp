<%
    def metodo
    if(!request.getRequestURI().equals("/")){
        def uri = request.getRequestURI() + "/"
        uri = uri.substring(1);
        def fisrtDiagonal = uri.indexOf("/")
        def secondDiagonal = uri.indexOf("/", fisrtDiagonal+1)
        metodo = uri.substring(fisrtDiagonal+1, secondDiagonal);
    }

    def modulos = [
        "certificacion",
        "inspeccionVigilancia",
        "pagoNotificacion",
        "pagoConstancia",
        "pagoActa",
    ]
%>

<g:if test="${modulos.contains(controllerName)}">
    <li class="nav-item mb-2">
        <a
            onclick = "mostrarProceso();"
            class = "nav-link-b ${controllerName == 'certificacion'?'active':''}"
            href = "${createLink(uri: 'certificacion/listar')}">
            Certificación
        </a>
    </li>

    <li class="nav-item mb-2">
        <a
            onclick = "mostrarProceso();"
            class = "nav-link-b ${controllerName == 'inspeccionVigilancia'?'active':''}"
            href = "${createLink(uri: 'inspeccionVigilancia/listar')}">
            Inspección y vigilancia
        </a>
    </li>

    <li class="nav-item mb-2">
        <a
            onclick = "mostrarProceso();"
            class = "nav-link-b ${controllerName == 'pagoNotificacion'?'active':''}"
            href = "${createLink(uri: 'pagoNotificacion/listar')}">
            Notificaciones 
        </a>
    </li>

    <li class="nav-item mb-2">
        <a
            onclick = "mostrarProceso();"
            class = "nav-link-b ${controllerName == 'pagoActa'?'active':''}"
            href = "${createLink(uri: 'pagoActa/listar')}">
            Actas 
        </a>
    </li>

    <li class="nav-item mb-2">
        <a
            onclick = "mostrarProceso();"
            class = "nav-link-b ${controllerName == 'pagoConstancia'?'active':''}"
            href = "${createLink(uri: 'pagoConstancia/listar')}">
            Constancias 
        </a>
    </li>
</g:if>