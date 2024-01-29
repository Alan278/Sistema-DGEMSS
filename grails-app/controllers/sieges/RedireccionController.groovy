package sieges

/**
 * Controlador que permite la redirección de usuarios dependiendo el rol
 * @ De Alan Jamir
 * @ 2022-2023
 */
class RedireccionController {

    /**
     * Permite redireccionar a un usuario a su vista principal dependiendo el rol
     */
    def redireccionar(){
        if (!isLoggedIn()) {
            redirect(uri:"/")
            return
        }

        def roles = []
        def authorities = authenticatedUser.getAuthorities()
        authorities.each{ authority ->
            roles << authority.getAuthority()
        }

        if(roles.contains('ROLE_ADMIN')){
            redirect(controller:'usuario', action:'listar')
            return
        }

        // Roles escuela
        if(roles.contains('ROLE_GESTOR_ESCUELA')){
            redirect(controller:'cicloEscolar', action:'listar')
            return
        }

        if(roles.contains('ROLE_DIRECTOR_ESCUELA')){
            redirect(controller:'certificado', action:'listarFirmasCertificados')
            return
        }

        if(roles.contains('ROLE_VOCAL_ESCUELA')){
            redirect(controller:'actaprofesional', action:'listarActas')
            return
        }

        if(roles.contains('ROLE_SECRETARIO_ESCUELA')){
            redirect(controller:'actaprofesional', action:'listarActas')
            return
        }

        if(roles.contains('ROLE_PRESIDENTE_ESCUELA')){
            redirect(controller:'actaprofesional', action:'listarActas')
            return
        }

        // Roles Dgemss
        if(roles.contains('ROLE_SUPERVISOR_POSGRADO')){
            redirect(controller:'institucion', action:'listar')
            return
        }

        if(roles.contains('ROLE_SUPERVISOR_SUPERIOR')){
            redirect(controller:'institucion', action:'listar')
            return
        }

        if(roles.contains('ROLE_SUPERVISOR_MEDIA')){
            redirect(controller:'institucion', action:'listar')
            return
        }

        if(roles.contains('ROLE_SUPERVISOR_MEDIA_PUBLICA')){
            redirect(controller:'institucion', action:'listar')
            return
        }

        if(roles.contains('ROLE_SUPERVISOR_TECNICA')){
            redirect(controller:'institucion', action:'listar')
            return
        }
        if(roles.contains('ROLE_SUPERVISOR_TECNICA_PUBLICA')){
            redirect(controller:'institucion', action:'listar')
            return
        }

        if(roles.contains('ROLE_SUPERVISOR_CONTINUA')){
            redirect(controller:'institucion', action:'listar')
            return
        }

        if(roles.contains('ROLE_AUTENTICADOR_DGEMSS')){
            redirect(controller:'dashBoard', action:'index')
            return
        }
        if(roles.contains('ROLE_REVISOR')){
            redirect(controller:'certificado', action:'listarCertificadosRevisar')
            return
        }
        if(roles.contains('ROLE_REVISOR_PUBLICA')){
            redirect(controller:'certificado', action:'listarCertificadosRevisar')
            return
        }
        if(roles.contains('ROLE_RECEPTOR')){
            redirect(controller:'certificacion', action:'listar')
            return
        }
    }
}