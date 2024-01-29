package sieges

import grails.gorm.transactions.Transactional
import org.hibernate.criterion.CriteriaSpecification
import groovy.json.JsonSlurper

/**
 * Servicio que permite la administración de usuarios
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */
@Transactional
class UsuarioService {
    /**
      * Inyección de messageSource que contiene mensajes de validaciones para los atributos de cada dominio
      * Los mensajes de validación se encuentran en: grails-app/i18n/messages_es.properties
      */
    def messageSource
    /**
     * Inyección de BitacoraService que contiene la lógica de administración de la bitacora
     */
    def bitacoraService
    def springSecurityService
    def passwordEncoder
    def mailService
    def grailsResourceLocator
    def efirmaService
    def emailService
    def grailsApplication
    def formatoService

    // Id de los niveles
    final SUPERIOR = 1
    final MEDIO_SUPERIOR = 2
    final TECNICO = 3
    final CONTINUA = 4

    // Id de los estatus de usuario
    final USUARIO_CREADO = 1
    final USUARIO_CONFIRMADO = 2
    final USUARIO_ACTIVO = 3
    final USUARIO_INACTIVO = 4
    final USUARIO_BLOQUEADO = 5

    /**
     * Registra un nuevo usuario
     * @param username (Requerido)
     * @param password (Requerido)
     * @param rolId (Requerido)
     * @param institucionId (Requerido)
     * @param cargo (Requerido)
     * @param nombre (Requerido)
     * @param primerApellido (Requerido)
     * @param segundoApellido (Opcional)
     * @param curp (Requerido)
     * @param telefono (Requerido)
     * @param correoElectronico (Requerido)
     */
    def registrar(params) {
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def datosBitacora = [
            clase: "UsuarioService",
            metodo: "registrar",
            nombre: "Registro de usuario",
            descripcion: "Se registró un usuario",
            estatus: "ERROR"
        ]

        def listaRoles = []
        def isGestorDirector = false
        def isFirmante = false

        if(!params.listaRoles){
            resultado.mensaje = 'Los roles son un dato requerido'
            return resultado
        }

        try{
            def jsonSlurper = new JsonSlurper()
            listaRoles = jsonSlurper.parseText(params.listaRoles)

            if(!listaRoles){
                resultado.mensaje = 'Los roles son un dato requerido'
                return resultado
            }

            for(rol in listaRoles){
                def rolAux = Rol.get(rol.id)
                if(!rolAux){
                    resultado.mensaje = "No se encontró el rol"
                    return resultado
                }
                if(rolAux.authority.equals('ROLE_GESTOR_ESCUELA') || rolAux.authority.equals('ROLE_DIRECTOR_ESCUELA') || rolAux.authority.equals('ROLE_PRESIDENTE_ESCUELA') || rolAux.authority.equals('ROLE_SECRETARIO_ESCUELA') || rolAux.authority.equals('ROLE_VOCAL_ESCUELA')){
                    isGestorDirector = true
                }
                if(rolAux.authority.equals('ROLE_DIRECTOR_ESCUELA') || rolAux.authority.equals('ROLE_AUTENTICADOR_DGEMSS') || rolAux.authority.equals('ROLE_PRESIDENTE_ESCUELA') || rolAux.authority.equals('ROLE_SECRETARIO_ESCUELA') || rolAux.authority.equals('ROLE_VOCAL_ESCUELA')){
                    isFirmante = true
                }
            }
        }catch(Exception ex){
            resultado.mensaje = 'Error en formato de roles'
            return resultado
        }

        if(isFirmante){
            if(!params.titulo){
                resultado.mensaje = 'El título es un dato requerido'
                return resultado
            }

            if(!params.cargo){
                resultado.mensaje = 'El cargo es un dato requerido'
                return resultado
            }
        }

        def listaInstituciones = []
        if(isGestorDirector){
            // Se validan las instituciones
            if(!params.listaInstituciones){
                resultado.mensaje = 'La institucion es un dato requerido'
                return resultado
            }

            try{
                def jsonSlurper = new JsonSlurper()
                listaInstituciones = jsonSlurper.parseText(params.listaInstituciones)

                if(!listaInstituciones){
                    resultado.mensaje = 'Las instituciones son un dato requerido'
                    return resultado
                }

                for(institucion in listaInstituciones){
                    def institucionAux = Institucion.get(institucion.id)
                    if(!institucionAux){
                        resultado.mensaje = "No se encontró la institución"
                        return resultado
                    }
                    if(!institucionAux.activo){
                        resultado.mensaje = "Institucion inactiva"
                        return resultado
                    }
                }
            }catch(Exception ex){
                resultado.mensaje = 'Error en formato de instituciones'
                return resultado
            }
        }

        //Se verifica si el curp ya se encuentra registrado
        def persona = null
        if(params.curp){
            persona = Persona.findByCurp(params.curp)
        }
        //En caso contrario se crea una nueva persona con los datos recibidos
        if(!persona) persona = new Persona(params)

        if(persona.usuario != null){
            resultado.mensaje = 'Ya existe un usuario registrado para esta persona'
            return resultado
        }

        def usuario = new Usuario(params)
        usuario.persona = persona
        usuario.username = generaNombreUsuario(params.nombre, params.primerApellido)
        usuario.password = usuario.username
        usuario.accountLocked = true
        usuario.confirmToken = efirmaService.generarUuid()
        usuario.estatusUsuario = EstatusUsuario.get(USUARIO_CREADO)

        if(!usuario.save(flush:true)){
            // Se recorren los errores de validación y se asignan a la propiedad resultado.mensaje
            usuario.errors.allErrors.each {
                resultado.mensaje = messageSource.getMessage(it, null)
            }
            bitacoraService.registrar(datosBitacora)
            return resultado
        }

        // Se asigna el rol
        listaRoles.each{ rol ->
            def rolAux = Rol.get(rol.id)
            if (!UsuarioRol.exists(usuario.id, rolAux.id)) {
                UsuarioRol.create(usuario, rolAux, true)
            }
        }

        // Se asigna la institución
        listaInstituciones.each{ institucion ->
            def institucionAux = Institucion.get(institucion.id)
            if (!UsuarioInstitucion.exists(usuario.id, institucionAux.id)) {
                UsuarioInstitucion.create(usuario, institucionAux, true)
            }
        }

        // Envio de correo electrónico para confirmación de cuenta
        def correoElectronico = usuario.persona.correoElectronico
        def asunto = "Confirma tu correo"
        def plantilla = "confirmacionUsuario"
        def parametros = [
            nombre: usuario.persona.nombre,
            username: usuario.username,
            token: usuario.confirmToken
        ]
        def adjuntos = []
        def cartaResponsiva = grailsApplication.mainContext.getResource("documentos/CartaResponsiva.pdf").file.getAbsoluteFile()
        def cartaResponsivaDatos = [
            fileName: "Carta responsiva.pdf",
            contentType: "application/octet-stream",
            bytes: cartaResponsiva.getBytes()
        ]
        adjuntos << cartaResponsivaDatos
        emailService.notificar(correoElectronico, asunto, plantilla, parametros, adjuntos)

        datosBitacora.estatus = "EXITOSO"
        bitacoraService.registrar(datosBitacora)

        resultado.estatus = true
        resultado.mensaje = 'Usuario creado exitosamente'
        resultado.datos = usuario

        return resultado
    }

    /**
     * Obtiene los usuarios activos con parametros de paginación y filtrado
     * @param institucionId (Opcional) Identificador de la institución
     * @param search (Opcional) Nombre del usuario
     */
    def listar(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: [
                usuarios: null,
                totalCount: 0
            ]
        ]

        // Parametros de paginación
        if(!params.max) params.max = "50"
        if(!params.offset) params.offset = "0"

        // Parametros de filtrado (opcionales)
        def institucionId
        if(params.institucionId){
           institucionId = Integer.parseInt(params.institucionId)
        }

        def criteria = {
            createAlias("persona", "p", CriteriaSpecification.LEFT_JOIN)
            createAlias("instituciones", "is", CriteriaSpecification.LEFT_JOIN)
            createAlias("instituciones.institucion", "i", CriteriaSpecification.LEFT_JOIN)
            and{
                eq("activo", true)
                if(params.search){
                    or{
                        ilike("p.nombre", "%${params.search}%")
                        ilike("p.primerApellido", "%${params.search}%")
                        ilike("p.segundoApellido", "%${params.search}%")
                    }
                }
                if(params.institucionId){
                    eq("i.id", institucionId)
                }
            }
            order("p.nombre", "asc")
        }

        def usuarios = Usuario.createCriteria().listDistinct(criteria)
        def totalCount = usuarios.size

        def usuariosAux = []
        def max = Integer.parseInt(params.max)
        def offset = Integer.parseInt(params.offset)
        for(int i = offset; i < max + offset ; i++){
            if(!usuarios[i]) break
            usuariosAux << usuarios[i]
        }
        usuarios = usuariosAux

        if(usuarios.size <= 0){
            resultado.mensaje = 'No se encontraron usuarios'
            resultado.datos.usuarios = usuarios
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'Usaurios consultados exitosamente'
        resultado.datos.usuarios = usuarios
        resultado.datos.totalCount = totalCount

        return resultado
    }

    /**
     * Obtiene un usuario específico
     * @param id (Requerido) Identificador del personal
     */
    def consultar(params) {
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        if(!params.id){
            resultado.mensaje = 'El id es un dato requerido'
            return resultado
        }

        def usuario = Usuario.get(params.id)

        if(!usuario){
            resultado.mensaje = 'Usuario no encontrado'
            return resultado
        }

        if(!usuario.activo){
            resultado.mensaje = 'Usuario inactivo'
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'Usuario encontrado exitosamente'
        resultado.datos = usuario

        return resultado
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
    def modificar(params) {
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def datosBitacora = [
            clase: "UsuarioService",
            metodo: "modificar",
            nombre: "Modificación de usuario",
            descripcion: "Se modificó un usuario",
            estatus: "ERROR"
        ]

        if(!params.id){
            resultado.mensaje = 'El id es un dato requerido'
            return resultado
        }
        def usuario = Usuario.get(params.id)
        if(!usuario){
            resultado.mensaje = 'Usuario no encontrado'
            return resultado
        }
        if(!usuario.activo){
            resultado.mensaje = 'Usuario inactivo'
            return resultado
        }

        def listaRoles = []
        def isGestorDirector = false
        def isFirmante = false

        if(!params.listaRoles){
            resultado.mensaje = 'Los roles son un dato requerido'
            return resultado
        }

        try{
            def jsonSlurper = new JsonSlurper()
            listaRoles = jsonSlurper.parseText(params.listaRoles)

            if(!listaRoles){
                resultado.mensaje = 'Los roles son un dato requerido'
                return resultado
            }

            for(rol in listaRoles){
                def rolAux = Rol.get(rol.id)
                if(!rolAux){
                    resultado.mensaje = "No se encontró el rol"
                    return resultado
                }
                if(rolAux.authority.equals('ROLE_GESTOR_ESCUELA') || rolAux.authority.equals('ROLE_DIRECTOR_ESCUELA') || rolAux.authority.equals('ROLE_PRESIDENTE_ESCUELA') || rolAux.authority.equals('ROLE_SECRETARIO_ESCUELA') || rolAux.authority.equals('ROLE_VOCAL_ESCUELA')){
                    isGestorDirector = true
                }
                if(rolAux.authority.equals('ROLE_DIRECTOR_ESCUELA') || rolAux.authority.equals('ROLE_AUTENTICADOR_DGEMSS') || rolAux.authority.equals('ROLE_PRESIDENTE_ESCUELA') || rolAux.authority.equals('ROLE_SECRETARIO_ESCUELA') || rolAux.authority.equals('ROLE_VOCAL_ESCUELA')){
                    isFirmante = true
                }
            }
        }catch(Exception ex){
            resultado.mensaje = 'Error en formato de roles'
            return resultado
        }

        if(isFirmante){
            if(!params.titulo){
                resultado.mensaje = 'El título es un dato requerido'
                return resultado
            }

            if(!params.cargo){
                resultado.mensaje = 'El cargo es un dato requerido'
                return resultado
            }
        }

        def listaInstituciones = []
        if(isGestorDirector){
            // Se validan las instituciones
            if(!params.listaInstituciones){
                resultado.mensaje = 'La institucion es un dato requerido'
                return resultado
            }

            try{
                def jsonSlurper = new JsonSlurper()
                listaInstituciones = jsonSlurper.parseText(params.listaInstituciones)

                if(!listaInstituciones){
                    resultado.mensaje = 'Las instituciones son un dato requerido'
                    return resultado
                }

                for(institucion in listaInstituciones){
                    def institucionAux = Institucion.get(institucion.id)
                    if(!institucionAux){
                        resultado.mensaje = "No se encontró la institución"
                        return resultado
                    }
                    if(!institucionAux.activo){
                        resultado.mensaje = "Institucion inactiva"
                        return resultado
                    }
                }
            }catch(Exception ex){
                resultado.mensaje = 'Error en formato de instituciones'
                return resultado
            }
        }

        if(!usuario.persona.correoElectronico.equals(params.correoElectronico)){
            if(usuario.estatusUsuario.id == USUARIO_CREADO || usuario.estatusUsuario.id == USUARIO_CONFIRMADO){
                usuario.accountLocked = true
                usuario.confirmToken = efirmaService.generarUuid()
                usuario.estatusUsuario = EstatusUsuario.get(USUARIO_CREADO)

                // Envio de correo electrónico para confirmación de cuenta
                def correoElectronico = params.correoElectronico
                def asunto = "Confirma tu correo"
                def plantilla = "confirmacionUsuario"
                def parametros = [
                    nombre: usuario.persona.nombre,
                    username: usuario.username,
                    token: usuario.confirmToken
                ]
                def adjuntos = []
                def cartaResponsiva = grailsApplication.mainContext.getResource("documentos/CartaResponsiva.pdf").file.getAbsoluteFile()
                def cartaResponsivaDatos = [
                    fileName: "Carta responsiva.pdf",
                    contentType: "application/octet-stream",
                    bytes: cartaResponsiva.getBytes()
                ]
                adjuntos << cartaResponsivaDatos
                emailService.notificar(correoElectronico, asunto, plantilla, parametros, adjuntos)
            }

            if(usuario.estatusUsuario.id == USUARIO_ACTIVO){
                usuario.accountLocked = true
                usuario.confirmToken = efirmaService.generarUuid()
                usuario.estatusUsuario = EstatusUsuario.get(USUARIO_BLOQUEADO)
                // Envio de correo electrónico para confirmación de cuenta con el nuevo correo
                def correoElectronico = params.correoElectronico
                def asunto = "Actualización de correo"
                def plantilla = "actualizacionCorreo"
                def parametros = [
                    nombre: usuario.persona.nombre,
                    username: usuario.username,
                    token: usuario.confirmToken
                ]
                emailService.notificar(correoElectronico, asunto, plantilla, parametros)
            }
        }

        usuario.persona.correoElectronico = params.correoElectronico
        usuario.persona.telefono = params.telefono
        usuario.persona.titulo = params.titulo
        usuario.cargo = params.cargo

        if(!usuario.save(flush:true)){
            // Se recorren los errores de validación y se asignan a la propiedad resultado.mensaje
            usuario.errors.allErrors.each {
                resultado.mensaje = messageSource.getMessage(it, null)
            }
            bitacoraService.registrar(datosBitacora)
            return resultado
        }

        // Se asigna el rol
        UsuarioRol.removeAll(usuario)
        listaRoles.each{ rol ->
            def rolAux = Rol.get(rol.id)
            UsuarioRol.create(usuario, rolAux, true)
        }

        // Se asigna la institución
        UsuarioInstitucion.removeAll(usuario)
        listaInstituciones.each{ institucion ->
            def institucionAux = Institucion.get(institucion.id)
            UsuarioInstitucion.create(usuario, institucionAux, true)
        }

        datosBitacora.estatus = "EXITOSO"
        bitacoraService.registrar(datosBitacora)

        resultado.estatus = true
        resultado.mensaje = 'Usuario modificado exitosamente'
        resultado.datos = usuario

        return resultado
    }

    /**
     * Realiza una baja lógica de un usuario específico
     * @param id (Requerido) Identificador del personal
     */
    def eliminar(params) {
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def datosBitacora = [
            clase: "UsuarioService",
            metodo: "eliminar",
            nombre: "Eliminación de usuario",
            descripcion: "Se eliminó un usuario",
            estatus: "ERROR"
        ]

        if(!params.id){
            resultado.mensaje = 'El id es un dato requerido'
            return resultado
        }

        def usuario = Usuario.get(params.id)

        if(!usuario){
            resultado.mensaje = 'Usuario no encontrado'
            return resultado
        }

        usuario.activo = false
        usuario.enabled = false

        if(!usuario.save(flush:true)){
            // Se recorren los errores de validación y se asignan a la propiedad resultado.mensaje
            usuario.errors.allErrors.each {
                resultado.mensaje = messageSource.getMessage(it, null)
            }
            bitacoraService.registrar(datosBitacora)
            return resultado
        }

        datosBitacora.estatus = "EXITOSO"
        bitacoraService.registrar(datosBitacora)

        resultado.estatus = true
        resultado.mensaje = 'Usuario dado de baja exitosamente'
        resultado.datos = usuario

        return resultado
    }

    def obtenerRoles(){
        def usuario = Usuario.get(springSecurityService.principal.id)

        def roles = []
        def authorities = usuario.getAuthorities()
        authorities.each{ authority ->
            roles << authority.getAuthority()
        }
        return roles
    }

    def obtenerUsuarioLogueado(){
        if (!springSecurityService.isLoggedIn()) {
            return null
        }

        def usuario = springSecurityService.getCurrentUser()
        return usuario
    }

    def modificarContrasena(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        if (!params.contrasenaActual) {
            resultado.mensaje = 'La contraseña actual es un dato requerido'
            return resultado
        }
        if (!params.nuevaContrasena) {
            resultado.mensaje = 'La nueva contraseña es un dato requerido'
            return resultado
        }
        if (!params.nuevaContrasena2) {
            resultado.mensaje = 'La confirmación de la nueva contraseña es un dato requerido'
            return resultado
        }
        if (params.nuevaContrasena != params.nuevaContrasena2) {
            resultado.mensaje = 'La confirmación de la nueva contraseña no coincide'
            return resultado
        }

        def usuario = Usuario.get(springSecurityService.principal.id)
        if (!passwordEncoder.isPasswordValid(usuario.password, params.contrasenaActual, null)) {
            resultado.mensaje = 'La contraseña actual es incorrecta'
            return resultado
        }

        if (passwordEncoder.isPasswordValid(usuario.password, params.nuevaContrasena, null)) {
            resultado.mensaje = 'La nueva contraseña debe de ser diferente a la actual'
            return resultado
        }

        if(!formatoService.isValidPassword(params.nuevaContrasena)){
            resultado.mensaje = 'La nueva contraseña no cuenta con un formato válido'
            return resultado
        }

        usuario.password = params.nuevaContrasena
        usuario.save()

        resultado.estatus = true
        resultado.mensaje = 'Contraseña modificada exitosamente'
        resultado.datos = usuario

        return resultado
    }

    def cambiarContrasena(params) {
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def datosBitacora = [
            clase: "UsuarioService",
            metodo: "cambiarContrasena",
            nombre: "Cambio de contraseña",
            descripcion: "Se cambio una contraseña",
            estatus: "ERROR"
        ]

        if(!params.id){
            resultado.mensaje = 'El id es un dato requerido'
            return resultado
        }

        def usuario = Usuario.get(params.id)

        if(!usuario){
            resultado.mensaje = 'Usuario no encontrado'
            return resultado
        }
        if(!usuario.activo){
            resultado.mensaje = 'Usuario inactivo'
            return resultado
        }

        usuario.password = params.password

        if(!usuario.save(flush:true)){
            // Se recorren los errores de validación y se asignan a la propiedad resultado.mensaje
            usuario.errors.allErrors.each {
                resultado.mensaje = messageSource.getMessage(it, null)
            }
            bitacoraService.registrar(datosBitacora)
            return resultado
        }

        datosBitacora.estatus = "EXITOSO"
        bitacoraService.registrar(datosBitacora)

        resultado.estatus = true
        resultado.mensaje = 'Contraseña modificada exitosamente'
        resultado.datos = usuario

        return resultado
    }

    def perteneceANivel(nivelId){
        def roles = obtenerRoles()

        if(roles.contains('ROLE_SUPERVISOR_POSGRADO')){
            if(nivelId == SUPERIOR) return true
        }

        if(roles.contains('ROLE_SUPERVISOR_SUPERIOR')){
            if(nivelId == SUPERIOR) return true
        }

        if(roles.contains('ROLE_SUPERVISOR_MEDIA')){
            if(nivelId == MEDIO_SUPERIOR) return true
        }

        if(roles.contains('ROLE_SUPERVISOR_MEDIA_PUBLICA')){
            if(nivelId == MEDIO_SUPERIOR) return true
        }

        if(roles.contains('ROLE_SUPERVISOR_TECNICA')){
            if(nivelId == TECNICO) return true
        }
        if(roles.contains('ROLE_SUPERVISOR_TECNICA_PUBLICA')){
            if(nivelId == TECNICO) return true
        }

        if(roles.contains('ROLE_SUPERVISOR_CONTINUA')){
            if(nivelId == CONTINUA ) return true
        }

        return false
    }

    def perteneceAInstitucion(institucionId){
        def usuario = Usuario.get(springSecurityService.principal.id)

        def resultado = false
        usuario.instituciones.each{ registro ->
            if(registro.institucion.id == institucionId){
                resultado = true
            }
        }

        return resultado
    }

    def perteneceAInstitucionPublica(){
        def usuario = Usuario.get(springSecurityService.principal.id)

        def resultado = false
        usuario.instituciones.each{ registro ->
            if(registro.institucion.publica){
                resultado = true
            }
        }

        return resultado
    }

    def obtenerNivelesPorRol(){
        def roles = obtenerRoles()

        def niveles = []

        if(roles.contains('ROLE_SUPERVISOR_POSGRADO')){
            niveles << SUPERIOR
        }

        if(roles.contains('ROLE_SUPERVISOR_SUPERIOR')){
            niveles << SUPERIOR
        }

        if(roles.contains('ROLE_SUPERVISOR_MEDIA')){
            niveles << MEDIO_SUPERIOR
        }

        if(roles.contains('ROLE_SUPERVISOR_MEDIA_PUBLICA')){
            niveles << MEDIO_SUPERIOR
        }

        if(roles.contains('ROLE_SUPERVISOR_TECNICA')){
            niveles << TECNICO
        }

        if(roles.contains('ROLE_SUPERVISOR_TECNICA_PUBLICA')){
            niveles << TECNICO
        }

        if(roles.contains('ROLE_SUPERVISOR_CONTINUA')){
            niveles << CONTINUA
        }

        return niveles
    }

    /**
     * Permite validar si el usuario logueado puede actuar sobre la institución con base en
     * el rol del usuario y el tipo de la institución (publica o privada)
     * @param isPublic (requerido)
     * Tipo de la institución
     * @return Boolean
     */
    def validarPrivilegioSobreTipoInstitucion(isPublic){
        def roles = obtenerRoles()

        if(roles.contains('ROLE_SUPERVISOR_MEDIA_PUBLICA') && !isPublic){
			return false
		}
        if(roles.contains('ROLE_SUPERVISOR_TECNICA_PUBLICA') && !isPublic){
			return false
		}

        return true
    }

    /**
     * Permite validar si el usuario logueado puede actuar sobre la institución con base en
     * el rol del usuario y el tipo de la institución (publica o privada)
     * @param isPublic (requerido)
     * Tipo de la institución
     * @return Boolean
     */
    def esSupervisorPublico(){
        def roles = obtenerRoles()

        def rolesPublicos = ["ROLE_SUPERVISOR_MEDIA_PUBLICA", "ROLE_SUPERVISOR_TECNICA_PUBLICA"]

        def esSupervisorPublico = roles.any { rol ->
            rol in rolesPublicos
        }

        return esSupervisorPublico
    }

    def generaNombreUsuario(String nombre, String apellido){
        def nombreUsuario = ""

        def nombrePartes = nombre?.trim()?.split(" ")

        for (def x = 0; x < nombrePartes.length && x < 2; x++) {
            nombreUsuario = "${nombreUsuario}-${nombrePartes[x]}"
        }

        def apellidoPartes = apellido?.trim()?.split(" ")

        nombreUsuario = "${nombreUsuario}.${apellidoPartes[0]}"

        nombreUsuario = nombreUsuario.substring(1,nombreUsuario.length())

        nombreUsuario = nombreUsuario.toLowerCase()

        def existente = Usuario.findByUsername(nombreUsuario)

        if (existente) {
            nombreUsuario = "${nombreUsuario}.${generaIdentificadorConsecutivo()}"
        }

        return nombreUsuario
    }

    def generaIdentificadorConsecutivo() {
        def identificador = 0

        identificador = (Usuario.count()?:0) + 1

        return identificador
    }

    /**
     * Permite confirmar un usuario
     * @param token (requerido)
     * @param contrasena (requerido) contraseña a establecer
     * @param contrasena2 (requerido) confirmación de contraseña
     */
    def confirmarUsuario(params) {
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def datosBitacora = [
            clase: "UsuarioService",
            metodo: "confirmarUsuario",
            nombre: "Confirmación de correo y creación de contraseña",
            descripcion: "Confirmación de correo y creación de contraseña",
            estatus: "ERROR"
        ]

        if(!params.token){
            resultado.mensaje = "El token es un dato requerido"
        }

        if (!params.contrasena) {
            resultado.mensaje = 'La contraseña es un dato requerido'
            return resultado
        }
        if (!params.contrasena2) {
            resultado.mensaje = 'La confirmación de la nueva contraseña es un dato requerido'
            return resultado
        }

        if(!formatoService.isValidPassword(params.contrasena)){
            resultado.mensaje = 'La contraseña no cuenta con un formato válido'
            return resultado
        }

        if (params.contrasena != params.contrasena2) {
            resultado.mensaje = 'La contraseña y su confirmación no coinciden'
            return resultado
        }

        def usuario = Usuario.findByConfirmToken(params.token)
        if (!usuario) {
            resultado.mensaje = 'Usuario no encontrado'
            return resultado
        }

        usuario.password = params.contrasena
        usuario.confirmToken = null
        usuario.estatusUsuario = EstatusUsuario.get(USUARIO_CONFIRMADO)
        usuario.save()

        datosBitacora.estatus = "EXITOSO"
        bitacoraService.registrar(datosBitacora)

        resultado.estatus = true
        resultado.mensaje = 'Correo confirmado exitosamente'
        resultado.datos = usuario

        return resultado
    }

    /**
     * Permite activar un usuario
     * @param id (requerido) id del usuario a activar
     */
    def activarUsuario(params) {
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def datosBitacora = [
            clase: "UsuarioService",
            metodo: "activarUsuario",
            nombre: "Activación de usuario",
            descripcion: "Se habilitó un usuario",
            estatus: "ERROR"
        ]

        if (!params.id) {
            resultado.mensaje = 'El id es un dato requerido'
            return resultado
        }

        def usuario = Usuario.get(params.id)
        if (!usuario) {
            resultado.mensaje = 'Usuario no encontrado'
            return resultado
        }

        // Se valida que el usuario se encuentre en un estatus válido para la acción
        def estatusUsuarioId = usuario.estatusUsuario.id
        def estatusValidos = [USUARIO_CONFIRMADO]

        if(!(estatusUsuarioId in estatusValidos)){
            resultado.mensaje = 'No se puede cambiar el estatus'
            return resultado
        }

        usuario.accountLocked = false
        usuario.enabled = true
        usuario.estatusUsuario = EstatusUsuario.get(USUARIO_ACTIVO)
        usuario.save()

        // Envio de correo electrónico para confirmación de cuenta
        def correoElectronico = usuario.persona.correoElectronico
        def asunto = "Cuenta de usuario activada exitosamente"
        def plantilla = "usuarioActivado"
        def parametros = [
            username: usuario.username
        ]
        emailService.notificar(correoElectronico, asunto, plantilla, parametros)

        datosBitacora.estatus = "EXITOSO"
        bitacoraService.registrar(datosBitacora)

        resultado.estatus = true
        resultado.mensaje = 'Usuario activado exitosamente'
        resultado.datos = usuario

        return resultado
    }

    /**
     * Envia un correo de recuperación de una cuenta
     * @param correo (requerido) correo de la cuenta a recuperar
     */
    def enviarCorreoRecuperacion(params) {
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def datosBitacora = [
            clase: "UsuarioService",
            metodo: "enviarCorreoRecuperacion",
            nombre: "Restablecer contraseña",
            descripcion: "Se intenta restablecer la contraseña",
            estatus: "ERROR"
        ]

        if (!params.correo) {
            resultado.mensaje = 'El correo es un dato requerido'
            return resultado
        }

        def persona = Persona.findByCorreoElectronico(params.correo)
        if (!persona) {
            resultado.mensaje = 'Usuario no encontrado'
            return resultado
        }

        def usuario = Usuario.findByPersona(persona)
        if (!usuario) {
            resultado.mensaje = 'Usuario no encontrado'
            return resultado
        }

        if(!usuario.enabled || usuario.accountLocked){
            resultado.mensaje = 'El usuario se encuentra bloqueado o inactivo'
            return resultado
        }

        usuario.confirmToken = efirmaService.generarUuid()

        if(!usuario.save(flush:true)){
            // Se recorren los errores de validación y se asignan a la propiedad resultado.mensaje
            usuario.errors.allErrors.each {
                resultado.mensaje = messageSource.getMessage(it, null)
            }
            bitacoraService.registrar(datosBitacora)
            return resultado
        }

        // Envio de correo electrónico para confirmación de cuenta
        def correoElectronico = usuario.persona.correoElectronico
        def asunto = "Restablecer contraeña"
        def plantilla = "recuperacionCuenta"
        def parametros = [
            nombre: usuario.persona.nombre,
            username: usuario.username,
            token: usuario.confirmToken
        ]
        emailService.notificar(correoElectronico, asunto, plantilla, parametros)

        datosBitacora.estatus = "EXITOSO"
        bitacoraService.registrar(datosBitacora)

        resultado.estatus = true
        resultado.mensaje = "Se ha enviado un correo a ${params.correo}. Ingresa y sigue las instrucciones para continuar."
        resultado.datos = usuario

        return resultado
    }

    /**
     * Permite restablecer una contraseña
     * @param token (requerido)
     * @param contrasena (requerido) contraseña a establecer
     * @param contrasena2 (requerido) confirmación de contraseña
     */
    def restablecerContrasena(params) {
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def datosBitacora = [
            clase: "UsuarioService",
            metodo: "restablecerContrasena",
            nombre: "Restablecimiento de contraseña",
            descripcion: "Restablecimiento de contraseña",
            estatus: "ERROR"
        ]

        if(!params.token){
            resultado.mensaje = "El token es un dato requerido"
        }

        if (!params.contrasena) {
            resultado.mensaje = 'La contraseña es un dato requerido'
            return resultado
        }
        if (!params.contrasena2) {
            resultado.mensaje = 'La confirmación de la nueva contraseña es un dato requerido'
            return resultado
        }

        if(!formatoService.isValidPassword(params.contrasena)){
            resultado.mensaje = 'La contraseña no cuenta con un formato válido'
            return resultado
        }

        if (params.contrasena != params.contrasena2) {
            resultado.mensaje = 'La contraseña y su confirmación no coinciden'
            return resultado
        }

        def usuario = Usuario.findByConfirmToken(params.token)
        if (!usuario) {
            resultado.mensaje = 'Usuario no encontrado'
            return resultado
        }

        if(!usuario.enabled || usuario.accountLocked){
            resultado.mensaje = 'El usuario se encuentra bloqueado o inactivo'
            return resultado
        }

        usuario.password = params.contrasena
        usuario.confirmToken = null
        usuario.save()

        datosBitacora.estatus = "EXITOSO"
        bitacoraService.registrar(datosBitacora)

        resultado.estatus = true
        resultado.mensaje = 'Contraseña restablecida'
        resultado.datos = usuario

        return resultado
    }

    /**
     * Permite confirmar un nuevo correo electrónico
     * @param token (requerido)
     */
    def confirmarNuevoCorreo(params) {
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def datosBitacora = [
            clase: "UsuarioService",
            metodo: "confirmarNuevoCorreo",
            nombre: "Confirmación de nuevo correo",
            descripcion: "Confirmación de nuevo correo",
            estatus: "ERROR"
        ]

        if(!params.token){
            resultado.mensaje = "El token es un dato requerido"
        }

        def usuario = Usuario.findByConfirmToken(params.token)
        if (!usuario) {
            resultado.mensaje = 'Link caducado'
            return resultado
        }

        if(!usuario.enabled){
            resultado.mensaje = 'El usuario se encuentra inactivo'
            return resultado
        }

        usuario.accountLocked = false
        usuario.confirmToken = null
        usuario.estatusUsuario = EstatusUsuario.get(USUARIO_ACTIVO)
        usuario.save()

        datosBitacora.estatus = "EXITOSO"
        bitacoraService.registrar(datosBitacora)

        resultado.estatus = true
        resultado.mensaje = 'Correo confirmado exitosamente'
        resultado.datos = usuario

        return resultado
    }
}
