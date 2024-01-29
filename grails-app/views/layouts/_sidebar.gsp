<sec:ifLoggedIn>
    <g:if test="${controllerName}">
        <div class="container col-md-2 border-right">
            <ul class="navbar-nav ml-2 mt-2 mb-5 sticky-top-b">

                <div class="border-bottom pb-3 mb-3">
                    <h4> Acciones </h4>
                    <li class="nav-item">
                        <g:pageProperty name="page.buttons"/>
                    </li>
                </div>

                <sec:ifAnyGranted roles='ROLE_ADMIN'>
                    <g:render template="/layouts/sidebarAdmin"></g:render>
                </sec:ifAnyGranted>

                <sec:ifAnyGranted roles='ROLE_GESTOR_ESCUELA'>
                    <g:render template="/layouts/sidebarControlEscolar"></g:render>
                </sec:ifAnyGranted>

                <sec:ifAnyGranted roles='ROLE_RECEPTOR'>
                    <g:render template="/layouts/sidebarReceptor"></g:render>
                </sec:ifAnyGranted>

                <sec:ifAnyGranted roles='
                    ROLE_REVISOR,
                    ROLE_GESTOR_ESCUELA,
                    ROLE_REVISOR_PUBLICA,
                    ROLE_DIRECTOR_ESCUELA,
                    ROLE_AUTENTICADOR_DGEMSS'>
                        <g:render template="/layouts/sidebarCertificacion"></g:render>
                </sec:ifAnyGranted>

                <sec:ifAnyGranted roles='
                    ROLE_REVISOR,
                    ROLE_GESTOR_ESCUELA,
                    ROLE_REVISOR_PUBLICA,
                    ROLE_DIRECTOR_ESCUELA,
                    ROLE_AUTENTICADOR_DGEMSS'>
                        <g:render template="/layouts/sidebarConstancia"></g:render>
                </sec:ifAnyGranted>

                <sec:ifAnyGranted roles='
                    ROLE_REVISOR,
                    ROLE_GESTOR_ESCUELA,
                    ROLE_REVISOR_PUBLICA,
                    ROLE_DIRECTOR_ESCUELA,
                    ROLE_AUTENTICADOR_DGEMSS'>
                        <g:render template="/layouts/sidebarNotificacion"></g:render>
                </sec:ifAnyGranted>

                <sec:ifAnyGranted roles='
                    ROLE_REVISOR,
                    ROLE_GESTOR_ESCUELA,
                    ROLE_REVISOR_PUBLICA,
                    ROLE_DIRECTOR_ESCUELA,
                    ROLE_AUTENTICADOR_DGEMSS,
                    ROLE_VOCAL_ESCUELA,
                    ROLE_PRESIDENTE_ESCUELA,
                    ROLE_SECRETARIO_ESCUELA'>
                        <g:render template="/layouts/sidebarActa"></g:render>
                </sec:ifAnyGranted>


                <sec:ifAnyGranted roles='
                    ROLE_SUPERVISOR_POSGRADO,
                    ROLE_SUPERVISOR_SUPERIOR,
                    ROLE_SUPERVISOR_MEDIA,
                    ROLE_SUPERVISOR_MEDIA_PUBLICA,
                    ROLE_SUPERVISOR_TECNICA,
                    ROLE_SUPERVISOR_TECNICA_PUBLICA,
                    ROLE_SUPERVISOR_CONTINUA'>
                        <g:render template="/layouts/sidebarGestionEscolar"/>
                </sec:ifAnyGranted>

                <g:if test="${
                    request.getRequestURI().equals('/usuario/perfil') ||
                    request.getRequestURI().equals('/usuario/modificacionContrasena')
                }">
                    <li class="nav-item mb-2">
                        <a
                            onclick = "mostrarProceso();"
                            class = "nav-link-b active"
                            href = "${createLink(uri: 'usuario/perfil')}">
                            Perfil de usuario
                        </a>
                    </li>
                </g:if>
            </ul>
        </div>
    </g:if>
</sec:ifLoggedIn>