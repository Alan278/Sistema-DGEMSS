<div class="main-nav mb-2 border-top sticky-top" >
    <div class="d-flex display-content-between">
        <nav class="nav container d-flex p-0">
            <sec:ifNotLoggedIn>
                <a class="py-3 px-4 text-muted border-left border-right ${controllerName ?'':'active'}" href="/">Inicio</a>
            </sec:ifNotLoggedIn>

            <sec:ifAnyGranted roles='ROLE_ADMIN'>
                <g:render template="/layouts/mainNavAdmin"></g:render>
            </sec:ifAnyGranted>

            <sec:ifAnyGranted roles='ROLE_GESTOR_ESCUELA'>
                <g:render template="/layouts/mainNavControlEscolar"></g:render>
            </sec:ifAnyGranted>

            <sec:ifAnyGranted roles='ROLE_RECEPTOR'>
                <g:render template="/layouts/mainNavReceptor"></g:render>
            </sec:ifAnyGranted>

            <sec:ifAnyGranted roles='
                ROLE_GESTOR_ESCUELA,
                ROLE_REVISOR,
                ROLE_REVISOR_PUBLICA,
                ROLE_DIRECTOR_ESCUELA,
                ROLE_AUTENTICADOR_DGEMSS'>
                <g:render template="/layouts/mainNavCertificacion"></g:render>
            </sec:ifAnyGranted>

            <sec:ifAnyGranted roles='
                ROLE_GESTOR_ESCUELA,
                ROLE_REVISOR,
                ROLE_REVISOR_PUBLICA,
                ROLE_DIRECTOR_ESCUELA,
                ROLE_AUTENTICADOR_DGEMSS'>
                <g:render template="/layouts/mainNavConstancia"></g:render>
            </sec:ifAnyGranted>

            <sec:ifAnyGranted roles='
                ROLE_GESTOR_ESCUELA,
                ROLE_REVISOR,
                ROLE_REVISOR_PUBLICA,
                ROLE_DIRECTOR_ESCUELA,
                ROLE_AUTENTICADOR_DGEMSS'>
                <g:render template="/layouts/mainNavNotification"></g:render>
            </sec:ifAnyGranted>

            <sec:ifAnyGranted roles='
                ROLE_GESTOR_ESCUELA,
                ROLE_REVISOR,
                ROLE_REVISOR_PUBLICA,
                ROLE_DIRECTOR_ESCUELA,
                ROLE_AUTENTICADOR_DGEMSS,
                ROLE_VOCAL_ESCUELA,
                ROLE_PRESIDENTE_ESCUELA,
                ROLE_SECRETARIO_ESCUELA'>
                <g:render template="/layouts/mainNavActa"></g:render>
            </sec:ifAnyGranted>

            <sec:ifAnyGranted roles='
                ROLE_SUPERVISOR_POSGRADO,
                ROLE_SUPERVISOR_SUPERIOR,
                ROLE_SUPERVISOR_MEDIA,
                ROLE_SUPERVISOR_MEDIA_PUBLICA,
                ROLE_SUPERVISOR_TECNICA,
                ROLE_SUPERVISOR_TECNICA_PUBLICA,
                ROLE_SUPERVISOR_CONTINUA'>
                <g:render template="/layouts/mainNavGestionEscolar"></g:render>
            </sec:ifAnyGranted>


        </nav>
        <sec:ifNotLoggedIn>
            <a class="py-3 px-4 text-muted border-left" href="redireccion/redireccionar">
                Iniciar sesión
            </a>
        </sec:ifNotLoggedIn>

        <sec:ifLoggedIn>
            <div class="dropdown">
                <a class="py-3 px-4 text-muted border-left dropdown-toggle" type="button" id="dropdownMenuButton" data-toggle="dropdown" aria-expanded="false">
                    <sec:loggedInUserInfo field='username'/>
                </a>
                <div class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenuButton">
                    <g:link class='dropdown-item' controller='usuario' action='perfil'>
                        Perfil de usuario
                    </g:link>
                    <sec:ifAnyGranted roles='ROLE_AUTENTICADOR_DGEMSS, ROLE_DIRECTOR_ESCUELA, ROLE_SECRETARIO_ESCUELA, ROLE_PRESIDENTE_ESCUELA, ROLE_VOCAL_ESCUELA'>
                        <g:link class='dropdown-item' controller='firmaElectronica' action='consultar'>
                            Firma electrónica
                        </g:link>
                    </sec:ifAnyGranted>
                    <div class="dropdown-divider"></div>
                    <g:link class='dropdown-item' controller='logout'>
                        Cerrar sesión
                    </g:link>
                </div>
              </div>
        </sec:ifLoggedIn>



    </div>
</div>