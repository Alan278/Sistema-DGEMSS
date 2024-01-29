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
%>

<a
    class = "py-3 px-4 text-muted border-right ${controllerName == 'usuario' ? 'active' : ''}"
    href = "${createLink(uri: 'usuario/listar')}">
    USUARIOS
</a>

<a
    class = "py-3 px-4 text-muted border-right ${controllerName == 'bitacora'?'active':''}" 
    href = "${createLink(uri: 'bitacora/listar')}">
    BITÁCORA
</a>

<a
    class = "py-3 px-4 text-muted border-right ${moduloCatalogo.contains(controllerName)?'active':''}"
    href = "${createLink(uri: 'tipoTramite/listar')}">
    CATÁLOGOS
</a>

<a
    class = "py-3 px-4 text-muted border-right ${controllerName == 'reporte' ? 'active' : ''}"
    href = "${createLink(uri: 'reporte/listar')}">
    REPORTES
</a>

<a
    class = "py-3 px-4 text-muted border-right ${controllerName == 'respaldo' ? 'active' : ''}"
    href = "${createLink(uri: 'respaldo/listar')}">
    RESPALDOS
</a>