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
        "institucion",
        "personalInstitucional",
        "carrera",
        "planEstudios",
        "asignatura",
        "formacion",
    ]
%>

<g:if test="${modulos.contains(controllerName)}">
    <li class="nav-item mb-2">
        <a
            onclick = "mostrarProceso();"
            class = "nav-link-b ${controllerName == 'institucion'?'active':''}"
            href = "${createLink(uri: 'institucion/listar')}">
            Instituciones
        </a>
    </li>
    <!-- <li class="nav-item mb-2">
        <a
            onclick = "mostrarProceso();"
            class = "nav-link-b ${controllerName == 'personalInstitucional' ?'active':''}"
            href = "${createLink(uri: 'personalInstitucional/listar')}">
            Personal institucional
        </a>
    </li> -->
    <li class="nav-item mb-2">
        <a
            onclick = "mostrarProceso();"
            class = "nav-link-b ${controllerName == 'alumno' || controllerName == 'carrera' ?'active':''}"
            href = "${createLink(uri: 'carrera/listar')}">
            Carreras
        </a>
    </li>
    <li class="nav-item mb-2">
        <a
            onclick = "mostrarProceso();"
            class = "nav-link-b ${controllerName == 'planEstudios' ?'active':''}"
            href = "${createLink(uri: 'planEstudios/listar')}">
            Planes de estudios
        </a>
    </li>
    <g:if test="${controllerName == 'formacion'}">
        <li class="nav-item mb-2 ml-4">
            <strong>
                Formaciones
            </strong>
        </li>
    </g:if>
    <li class="nav-item mb-2">
        <a
            onclick = "mostrarProceso();"
            class = "nav-link-b ${controllerName == 'asignatura' ?'active':''}"
            href = "${createLink(uri: 'asignatura/listar')}">
            Asignaturas
        </a>
    </li>
    <sec:ifAnyGranted roles='ROLE_SUPERVISOR_CONTINUA'>
        <li class="nav-item mb-2">
            <a
                onclick = "mostrarProceso();"
                class = "nav-link-b ${controllerName == 'curso' ?'active':''}"
                href = "${createLink(uri: 'curso/listar')}">
                Cursos
            </a>
        </li>
        <li class="nav-item mb-2">
            <a
                onclick = "mostrarProceso();"
                class = "nav-link-b ${controllerName == 'diplomado' ?'active':''}"
                href = "${createLink(uri: 'diplomado/listar')}">
                Diplomados
            </a>
        </li>
    </sec:ifAnyGranted>
</g:if>
