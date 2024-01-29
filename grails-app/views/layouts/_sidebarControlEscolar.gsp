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
        "cicloEscolar",
        "alumno",
        "evaluacion",
    ]
%>

<g:if test="${modulos.contains(controllerName)}">
    <li class="nav-item mb-2">
        <a
            onclick = "mostrarProceso();"
            class = "nav-link-b ${controllerName == 'cicloEscolar'?'active':''}"
            href = "${createLink(uri: 'cicloEscolar/listar')}">
            Ciclo Escolar
        </a>
    </li>
    <li class="nav-item mb-2">
        <a
            onclick = "mostrarProceso();"
            class = "nav-link-b ${controllerName == 'alumno' || controllerName == 'evaluacion' ?'active':''}"
            href = "${createLink(uri: 'alumno/listar')}">
            Alumnos
        </a>
    </li>
</g:if>
