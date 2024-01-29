package sieges

import org.apache.commons.lang.RandomStringUtils

/**
 * Controlador que permite el enlace entre el modelo y las vistas de usuarios
 * @author Luis Dominguez, Leslie Navez
 * @since 2022
 */
class UsuarioController {
    /**
     * Inyección de InstitucionService que contiene la lógica de administración de instituciones
     */
    def institucionService
    /**
     * Inyección del rolService que contiene métodos para la gestión de roles
     */
    def rolService
    /**
     * Inyección de CargoInstitucionalService que contiene la lógica de administración de cargos
     */
    def cargoInstitucionalService
    /**
     * Inyección de UsuarioService que contiene la lógica de administración de usuarios
     */
    def usuarioService
    def consultaPagoService
	def springSecurityService

    // Estatus proceso confirmacion
    def INICIO = "1" // Inicio del proceso de confirmación
    def LINK_CADUCADO = "2" // Token no encontrado
    def ERROR_PARAMETROS = "3" // Error en parametros
    def EXITOSO = "4" // Proceso exitoso


    /**
     * Obtiene los catálogos necesarios para la vista 'registro'
     */
    def registro(){
        def instituciones = institucionService.obtenerActivos(params)
        def roles = rolService.obtenerActivos()
        def cargos = cargoInstitucionalService.obtenerActivos(params)

        [
            instituciones: instituciones.datos,
            roles: roles.datos,
            cargos: cargos.datos
        ]
    }

    /**
     * Registra un nuevo usuario
     * @param rolId (Requerido)
     * @param institucionId (Requerido)
     * @param cargoId (Requerido)
     * @param nombreCargo (Requerido)
     * @param nombre (Requerido)
     * @param primerApellido (Requerido)
     * @param segundoApellido (Opcional)
     * @param curp (Requerido)
     * @param telefono (Requerido)
     * @param correoElectronico (Requerido)
     */
    def registrar(){
        def resultado = usuarioService.registrar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje
        

        if(resultado.estatus){
            redirect action: "listar"
        }else{
            redirect(action: "registro", params: params)
        }
        return
    }

    /**
     * Obtiene los usuarios activos para mostrarlos en la vista 'listar'
     * @param institucionId (Opcional)
     * @param rolId (Opcional)
     * @param search (Opcional) Nombre del usuario
     */
    def listar(){
        def resultado = usuarioService.listar(params)
        def instituciones = institucionService.obtenerActivos(params)

        [
            instituciones: instituciones.datos,
            usuarios: resultado.datos.usuarios,
            conteo: resultado.datos.totalCount
        ]
    }

    /**
     * Obtiene un usuario para mostrarlo en la vista 'consultar'
     * @param id (Requerido) Identificador del usuario
     */
    def consultar(){
        def resultado = usuarioService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect action: "listar"
            return
        }

        [usuario: resultado.datos]
    }

    /**
     * Obtiene los datos necesarios para la vista 'modificacion'
     * @param id (Requerido) Identificador del usuario
     */
    def modificacion(){
        def resultado = usuarioService.consultar(params)
        def instituciones = institucionService.obtenerActivos(params)
        def roles = rolService.obtenerActivos(params)
        def cargos = cargoInstitucionalService.obtenerActivos(params)

        if(!resultado.estatus){
            redirect action: "listar"
            return
        }

        [
            instituciones: instituciones.datos,
            roles: roles.datos,
            cargos: cargos.datos,
            usuario: resultado.datos
        ]
    }

    /**
     * Modifica los datos de un usuario
     * @param id (Requerido) Identificador del usuario
     * @param rolId (Requerido)
     * @param institucionId (Requerido)
     * @param cargoId (Requerido)
     * @param nombreCargo (Requerido)
     * @param nombre (Requerido)
     * @param primerApellido (Requerido)
     * @param segundoApellido (Opcional)
     * @param curp (Requerido)
     * @param telefono (Requerido)
     * @param correoElectronico (Requerido)
     */
    def modificar(){
        def resultado = usuarioService.modificar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            redirect action: "listar"
            return
        }
        redirect(action: "modificacion" , params: params)
    }

    /**
     * Realiza una baja lógica de un usuario
     * @param id (Requerido) Identificador del usuario
     */
    def eliminar(){
        def resultado = usuarioService.eliminar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect action: "listar"

        return
    }

    /**
     * Realiza una baja lógica de un usuario
     * @param id (Requerido) Identificador del usuario
     */
    def perfil(){
        def resultado = usuarioService.obtenerUsuarioLogueado()

        [
            usuario: resultado
        ]
    }

    def cadena(){
        String charset = (('A'..'Z') + ('0'..'9')).join()
        Integer length = 9
        String randomString = RandomStringUtils.random(length, charset.toCharArray())
        render randomString
    }

    def modificacionContrasena(){
        def resultado = usuarioService.obtenerUsuarioLogueado()

        [
            usuario: resultado
        ]
    }

    def modificarContrasena(){
        def resultado = usuarioService.modificarContrasena(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            redirect action: "perfil"
            return
        }
        redirect(action: "modificacionContrasena")
    }


    def cambioContrasena(){
    }

    def cambiarContrasena(){
        def resultado = usuarioService.cambiarContrasena(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            redirect action: "listar"
            return
        }
        redirect(action: "cambioContrasena" , params: params)
    }

    /**
     * Permite mostrar la vista para la confirmacion de un usuario
     * @param token (requerido)
     */
    def confirmacionUsuario(){
        def usuario = Usuario.findByConfirmToken(params.token?:"-1")
        def estatus = params.estatus?:INICIO

        if(!usuario){
            estatus = LINK_CADUCADO
            flash.mensaje = "Link caducado"
        }

        [usuario: usuario, estatus: estatus]
    }

    /**
     * Permite confirmar un usuario
     * @param token (requerido)
     * @param contrasena (requerido) contraseña a establecer
     * @param contrasena2 (requerido) confirmación de contraseña
     */
    def confirmarUsuario(){
        def resultado = usuarioService.confirmarUsuario(params)

        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect(
                action: "confirmarCuenta",
                params: [
                    token: params.token,
                    estatus: ERROR_PARAMETROS
                ]
            )
            return
        }

        render(view: "confirmacionUsuario", model: [estatus: EXITOSO])
        return
    }

    /**
     * Permite activar un usuario
     * @param id (requerido) id del usuario a activar
     */
    def activarUsuario(){
        def resultado = usuarioService.activarUsuario(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect action: "listar"
        return
    }

    /**
     * Permite mostrar la vista para la recuparación de una cuenta
     */
    def recuperacionCuenta(){}

    /**
     * Permite enviar un correo de recuperación de una cuenta
     * @param correo (requerido) correo de la cuenta a recuperar
     */
    def enviarCorreoRecuperacion(){
        def resultado = usuarioService.enviarCorreoRecuperacion(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect(action: "recuperacionCuenta")
    }

    /**
     * Permite mostrar la vista para restablecer una contraseña
     * @param token (requerido)
     */
    def restablecimientoContrasena(){
        def usuario = Usuario.findByConfirmToken(params.token?:"-1")
        def estatus = params.estatus?:INICIO

        if(!usuario){
            estatus = LINK_CADUCADO
            flash.mensaje = "Link caducado"
        }

        [usuario: usuario, estatus: estatus]
    }

    /**
     * Permite restablecer una contraseña
     * @param token (requerido)
     * @param contrasena (requerido) contraseña a establecer
     * @param contrasena2 (requerido) confirmación de contraseña
     */
    def restablecerContrasena(){
        def resultado = usuarioService.restablecerContrasena(params)

        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect(
                action: "restablecimientoContrasena",
                params: [
                    token: params.token,
                    estatus: ERROR_PARAMETROS
                ]
            )
            return
        }

        render(view: "restablecimientoContrasena", model: [estatus: EXITOSO])
        return
    }

    /**
     * Permite confirmar un nuevo correo electrónico
     * @param token (requerido)
     */
    def confirmarNuevoCorreo(){
        def resultado = usuarioService.confirmarNuevoCorreo(params)

        [
            resultado: resultado
        ]
    }
}
