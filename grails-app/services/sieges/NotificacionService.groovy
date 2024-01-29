package sieges
import grails.gorm.transactions.Transactional
import groovy.json.JsonSlurper

import java.text.SimpleDateFormat
import org.hibernate.criterion.CriteriaSpecification
import groovy.sql.Sql
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import grails.gorm.transactions.Transactional

/**
 * @author Alan Guevarin
 * @since 2022
 */

@Transactional
class NotificacionService {
    def servletContext
    def efirmaService
    def alumnoService
    def documentoService
    def xmlService
    def zxingService
    def numeroLetrasService
    def bitacoraService
    def messageSource
    def usuarioService
    def springSecurityService
    def certificadoPublicoService
    def errorService
    def estatusRegistroService
    def dataSource

     // Id de los EstatusNotificacion
    final GENERADO = 1
    final FIRMANDO_ESCUELA = 2
    final RECHAZADO_DIRECTOR = 3
    final EN_ESPERA = 4
    final EN_REVISION = 5
    final RECHAZADO_DGEMSS = 6
    final FIRMANDO_DGEMSS = 7
    final RECHAZADO_AUTENTICADOR = 8
    final FINALIZADO = 9

     /**
     * Obtiene las notificaciones con parametros de paginación y filtrado
     *
     * @param instituciones Filtrar por instituciones a las que pertenece un usuario
     * @param estatusNotificaciones Filtrar por estatus de la constancia
     * @param institucionId Filtrar por una institución específica
     * @param estatusConstanciaId Filtrar por une status específico
     * @param tieneTramite Filtrar los que tienen o no tramites
     * @param esPublica Filtrar los que son o no de instituciones públicas
     * @param alumnoId Filtrar por alumno
     *
     * @return resultado con el estatus, mensaje y notificaciones
     */

    def listar(params){

        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        // Parametros de paginado
        if(!params.max) params.max = 50
        if(!params.offset) params.offset = 0

        // Parametros de filtrado requeridos
        def instituciones = params.instituciones
        def estatusNotificaciones = params.estatusNotificaciones
        def tieneTramite = params.tieneTramite
        def esPublica = params.esPublica

        // Parametros de filtrado opcionales
        def institucionId = params.institucionId ? params.institucionId.toInteger() : null
        def carreraId = params.carreraId ? params.carreraId.toInteger() : null
        def planEstudiosId = params.planEstudiosId ? params.planEstudiosId.toInteger() : null
        def estatusNotificacionId = params.estatusNotificacionId ? params.estatusNotificacionId.toInteger() : null
        def alumnoId = params.alumnoId ? params.alumnoId.toInteger() : null
        def search = params.search ? params.search : null


        def criteria = {
            createAlias("estatusNotificacion", "e", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno", "a", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno.persona", "p", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno.planEstudios", "plan", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno.planEstudios.carrera", "c", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno.planEstudios.carrera.institucion", "inst", CriteriaSpecification.LEFT_JOIN)
            and{
                eq("activo", true)

                or{
                    instituciones.each{ registro ->
                        eq("inst.id", registro.institucion.id)
                    }
                }

                or{
                    estatusNotificaciones.each{ registro ->
                        eq("e.id", registro)
                    }
                }

                if(institucionId != null){
 				    eq("inst.id", institucionId)
 				}

                if(estatusNotificacionId != null){
                    eq("e.id", estatusNotificacionId)
                }

                if(tieneTramite != null){
                    if(tieneTramite)
                        isNotNull("tramite")
                    else{
                        isNull("tramite")
                    }
                }

                if(esPublica != null){
                    eq("inst.publica", esPublica)
                }

                if(alumnoId != null){
                    eq("a.id", alumnoId)
                }

                if(search != null){
                    or{
                        ilike("p.nombre", "%${search}%")
                        ilike("p.primerApellido", "%${search}%")
                        ilike("p.segundoApellido", "%${search}%")
                        ilike("p.curp", "%${search}%")
                        ilike("a.matricula", "%${search}%")
                    }
                }
            }

            order("p.nombre", "asc")
        }

        def notificaciones = NotificacionProfesional.createCriteria().list(params, criteria)

        resultado.estatus = true
        resultado.mensaje = "Las notificaciones fueron consultadas exitosamenter ☻"
        resultado.datos = notificaciones

        return resultado
    }
     /**
     * Obtiene todas las notificaciones
     *
     * @param params con los parametros de pagínacion (opcional)
     *
     * @return resultado con el estatus, mensaje y notificaciones
     */
    def listarNotificaciones(params){
        def usuario = usuarioService.obtenerUsuarioLogueado()
        def roles = usuarioService.obtenerRoles()

        params.instituciones = null
        params.estatusNotificaciones = null
        params.tieneTramite = null
        params.esPublica = null

        if(roles.contains('ROLE_GESTOR_ESCUELA') || roles.contains('ROLE_DIRECTOR_ESCUELA')){
            params.instituciones = usuario.instituciones
        }
        if(roles.contains('ROLE_AUTENTICADOR_DGEMSS')){
            params.esPublica = params.isPublic ? (params.isPublic.equals("false") ? true : false) : false
            params.instituciones = []
        }

        def notificaciones = listar(params)

        return notificaciones
    }

    /**
     * Obtiene las notificaciones a revisar
     *
     * @param params con los parametros de pagínacion (opcional)
     *
     * @return resultado con el estatus, mensaje y notificaciones
     */
    def listarNotificacionesRevisar(params){
        def usuario = usuarioService.obtenerUsuarioLogueado()
        def roles = usuarioService.obtenerRoles()

        params.instituciones = null
        params.estatusNotificaciones = [EN_REVISION, RECHAZADO_AUTENTICADOR]
        params.tieneTramite = null
        params.esPublica = null

        if(roles.contains('ROLE_REVISOR')){
            params.esPublica = false
        }

        if(roles.contains('ROLE_REVISOR_PUBLICA')){
            params.esPublica = true
        }

        if(roles.contains('ROLE_REVISOR') && roles.contains('ROLE_REVISOR_PUBLICA')){
            params.esPublica = null
        }

        def notificaciones = listar(params)

        return notificaciones
    }


    def listarNotificacionFirmar(params){
        def usuario = usuarioService.obtenerUsuarioLogueado()
        def roles = usuarioService.obtenerRoles()

        params.instituciones = null
        params.estatusNotificaciones = [FIRMANDO_DGEMSS]
        params.tieneTramite = null
        params.esPublica = null

        if(roles.contains('ROLE_AUTENTICADOR_DGEMSS')){
            params.esPublica = false
        }

        if(roles.contains('ROLE_AUTENTICADOR_DGEMSS')){
            params.esPublica = true
        }

        if(roles.contains('ROLE_AUTENTICADOR_DGEMSS') ){
            params.esPublica = null
        }

        def notificaciones = listar(params)

        return notificaciones
    }

    def listarNotificacionesAFirmarPorInstitucion(params){

        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def criteria = {
            createAlias("estatusNotificacion", "e", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno", "a", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno.persona", "p", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno.planEstudios", "plan", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno.planEstudios.carrera", "c", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno.planEstudios.carrera.institucion", "inst", CriteriaSpecification.LEFT_JOIN)
            and{
                eq("activo", true)

                if(params.id)
                eq("inst.id", params.id.toInteger())

                or{
                    eq("e.id", FIRMANDO_DGEMSS)
                }

                if(params.search){
                    or{
                        ilike("p.nombre", "%${params.search}%")
                        ilike("p.primerApellido", "%${params.search}%")
                        ilike("p.segundoApellido", "%${params.search}%")
                        ilike("p.curp", "%${params.search}%")
                        ilike("a.matricula", "%${params.search}%")
                    }
                }
            }

            order("p.nombre", "asc")
        }

        def notificaciones = NotificacionProfesional.createCriteria().list(criteria)

        resultado.estatus = true
        resultado.mensaje = "Notificiaciones consultadas exitosamente"
        resultado.datos = notificaciones

        return resultado
    }

    
     /**
     * Permite realizar el registro de notificación
     * @param libro (requerido)
     * @param foja (requerido)
     * @param numero (opcional)
     * @return resultado
     * resultado con el estatus, mensaje y datos de la notificación
     */
    def enviarAFirmarDGEMSS(params) {
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def datosBitacora = [
            clase: "NotificacionService",
            metodo: "enviarAFirmarDGEMSS",
            nombre: "Envío para firma de la DGEMSS",
            descripcion: "Envío para firma de la DGEMSS",
            estatus: "ERROR"
        ]

        // Validación de parámetros recibidos
        if(!params.uuid){
            resultado.mensaje = 'El uuid de la notificacion es un dato requerido'
            return resultado
        }
        def notificacion = NotificacionProfesional.findByUuid(params.uuid)
        if(!notificacion){
            resultado.mensaje = 'notificacion no encontrada'
            return resultado
        }
        if(!notificacion.activo){
            resultado.mensaje = 'notificacion inactiva'
            return resultado
        }

        notificacion.estatusNotificacion = EstatusNotificacion.get(FIRMANDO_DGEMSS)

        if(!notificacion.save(flush:true)){
			notificacion.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar(datosBitacora)
            return resultado
		}

        // Se guarda la acción en la bitácora
        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = 'notificacion creada exitosamente'
		resultado.datos = notificacion

        return resultado
    }

    /**
     * Obtiene las notificaciones a firmar
     *
     * @param params con los parametros de pagínacion (opcional)
     *
     * @return resultado con el estatus, mensaje y notificaciones
     */
    def listarNotificacionesFirmar(params){
        def usuario = usuarioService.obtenerUsuarioLogueado()
		def roles = usuarioService.obtenerRoles()

        params.instituciones = null
        params.estatusNotificaciones = null
        params.tieneTramite = null
        params.esPublica = null

        if(roles.contains('ROLE_DIRECTOR_ESCUELA')){
            params.instituciones = usuario.instituciones
            params.estatusNotificaciones = [FIRMANDO_ESCUELA]
        }
        if(roles.contains('ROLE_AUTENTICADOR_DGEMSS')){
            params.instituciones = []
            params.estatusNotificaciones = [FIRMANDO_DGEMSS]
        }

        def notificaciones = listar(params)

        return notificaciones
    }

    /**
     * Obtiene las notificaciones finalizados por alumno
     *
     * @param params con los parametros de pagínacion (opcional)
     *
     * @return resultado con el estatus, mensaje y notificaciones
     */
    def listarNotificacionesFinalizadosPorAlumno(params){
        def usuario = usuarioService.obtenerUsuarioLogueado()
		def roles = usuarioService.obtenerRoles()

        params.instituciones = null
        params.estatusNotificaciones = [FINALIZADO]
        params.tieneTramite = null
        params.esPublica = null

        if(roles.contains('ROLE_GESTOR_ESCUELA') || roles.contains('ROLE_DIRECTOR_ESCUELA')){
            params.instituciones = usuario.instituciones
        }
        if(roles.contains('ROLE_AUTENTICADOR_DGEMSS')){
            params.instituciones = []
        }
        
        def notificaciones = listar(params)

        return notificaciones
    }

    /**
     * Obtiene las notificaciones sin tramite
     *
     * @param params con los parametros de pagínacion (opcional)
     *
     * @return resultado con el estatus, mensaje y notificaciones
     */
    def listarNotificacionesSinTramite(params){
		def usuario = usuarioService.obtenerUsuarioLogueado()
		def roles = usuarioService.obtenerRoles()

        params.estatusNotificaciones = [EN_ESPERA]
        params.tieneTramite = false
        params.esPublica = false

        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        // Parametros de paginado
        if(!params.max) params.max = 50
        if(!params.offset) params.offset = 0

        // Parametros de filtrado opcionales
        def institucionId = params.institucionId ? params.institucionId.toInteger() : null

        def criteria = {
           createAlias("estatusNotificacion", "e", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno", "a", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno.persona", "p", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno.planEstudios", "plan", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno.planEstudios.carrera", "c", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno.planEstudios.carrera.institucion", "inst", CriteriaSpecification.LEFT_JOIN)
            and{
                eq("activo", true)
                eq("e.id", EN_ESPERA)
 				eq("inst.id", institucionId)
                isNull("tramite")
                eq("inst.publica", false)
            }

            order("p.nombre", "asc")
        }

        def notificaciones = NotificacionProfesional.createCriteria().list(params, criteria)

        resultado.estatus = true
        resultado.mensaje = "Notificaciones consultados exitosamente"
        resultado.datos = notificaciones

        return resultado
    }

    /**
     * Permite realizar el registro de notificaciones
     * @param foto
     * @return resultado
     * resultado con el estatus, mensaje y datos del notificacion
     */
    def registrar(params) {
        def resultado = [
            estatus: false,
            pag: null,
            mensaje: '',
            datos: null
        ]

        def datosBitacora = [
            usuario: usuarioService.obtenerUsuarioLogueado(),
            clase: "NotificacionService",
            metodo: "registrar",
            nombre: "Registro de notificacion",
            descripcion: "Se registró una notificacion",
            estatus: "ERROR"
        ]

        // Validación de parámetros recibidos
        if(!params.alumnoId){
            resultado.mensaje = 'El id del alumno es un dato requerido'
            resultado.pag = 0
            return resultado
        }
        def alumno = Alumno.get(params.alumnoId)
        if(!alumno){
            resultado.mensaje = 'Alumno no encontrado'
            resultado.pag = 0
            return resultado
        }
        if(!alumno.activo){
            resultado.mensaje = 'Alumno inactivo'
            resultado.pag = 0
            return resultado
        }

        // Se valida que el alumno pertenezca a la misma institución que el usuario
        def institucionId = alumno.planEstudios.carrera.institucion.id
        if(!usuarioService.perteneceAInstitucion(institucionId)){
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
			return resultado
		}


       /* Valida que se hayan ingresado los datos*/ 
        if(!params.foto){
            resultado.mensaje = 'La foto es un dato requerido'
            return resultado
        }

        if(!params.opctitulacionId){
            resultado.mensaje = 'Ingrese el nombre del titulo'
            return resultado
        }

        if(!params.titulo){
            resultado.mensaje = 'Ingrese el nombre del titulo'
            return resultado
        }

        if(!params.presidente){
            resultado.mensaje = 'Ingrese el nombre del presidente'
            return resultado
        }

        if(!params.secretario){
            resultado.mensaje = 'Ingrese el nombre del secretario'
            return resultado
        }

        if(!params.titulo){
            resultado.mensaje = 'Ingrese el nombre del vocal'
            return resultado
        }



        def notificacion = new NotificacionProfesional()
        notificacion.uuid = efirmaService.generarUuid()
        notificacion.folioControl = generaIdentificadorEstructurado(alumno.planEstudios.carrera.institucion)
        notificacion.foto = efirmaService.base64toBytes(params.foto)
        notificacion.municipio = "CUERNAVACA"
        notificacion.opctitulacion = OpcTitulacion.get(params.opctitulacionId)
        notificacion.titulo = params.titulo
        notificacion.presidente = params.presidente
        notificacion.secretario = params.secretario
        notificacion.vocal = params.vocal
        notificacion.doc = TipoDocumento.get(params.docId)
        notificacion.alumno = alumno
        notificacion.estatusNotificacion = EstatusNotificacion.get(GENERADO)

        if(!notificacion.save(flush:true)){
			notificacion.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar(datosBitacora)
            return resultado
		}

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = 'Notificacion creada exitosamente'
		resultado.datos = notificacion

        return resultado
    }

    def generaIdentificadorEstructurado(institucion){
        def identificador = ""

        def fecha = new Date()

        identificador = (fecha.format("yyyyMMdd")?:"").trim().padLeft(8,"0")
        identificador += "17".trim().padLeft(2,"0")
        identificador += (institucion?.claveCt?:"").trim().padLeft(8,"0")
        identificador += (generaIdentificadorConsecutivo()?:"").padLeft(8,"0")

        return identificador
    }

    def generaIdentificadorConsecutivo() {
        def identificador = ""

        identificador = "${(NotificacionProfesional.count()?:0) + 1}"

        return identificador
    }

     /**
     * Permite realizar la consulta del registro seleccionado con su información correspondiente.
     * @param id
     * id del notificacion
     * @return resultado
     * resultado con el estatus, mensaje y datos del notificacion
     */
    def consultar(params) {
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }

        def notificacion = NotificacionProfesional.findByUuid(params.uuid)

        if(!notificacion){
            resultado.mensaje = 'No se ha encontrado la notificacion'
            return resultado
        }

        if(!notificacion.activo){
            resultado.mensaje = 'Notificacion inactiva'
            return resultado
        }


		def roles = usuarioService.obtenerRoles()
		if(roles.contains('ROLE_GESTOR_ESCUELA') || roles.contains('ROLE_DIRECTOR_ESCUELA')){
            // Se valida que la constancia pertenezca a la misma institución que el usuario. :D
            def institucionId = notificacion.alumno.planEstudios.carrera.institucion.id
            if(!usuarioService.perteneceAInstitucion(institucionId)){
                resultado.mensaje = 'No esta autorizado para realizar esta acción'
                return resultado
            }
		}

        resultado.estatus = true
        resultado.mensaje = 'Se ha encontrado la notificación'
        resultado.datos = notificacion

        return resultado
    }
    def consultarNotificacion(params) {
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }

        def notificacion = NotificacionProfesional.findByUuid(params.uuid)

        if(!notificacion || !notificacion.activo || notificacion.estatusNotificacion.id != 9){
            resultado.mensaje = 'No se ha encontrado la constancia'
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'La notificacion se ha encontrado'
        resultado.datos = notificacion

        return resultado
    }

     /**
     * Permite realizar la modificación de una notificacion
     * @param params
     * parametros del ciclo escolar
     * @param carreraId
     * id de la carrera
     * @return resultado
     * resultado con el estatus, mensaje y datos del ciclo escolar
     */
     
	def modificarFotografia(params) {
		def resultado = [
			estatus: false,
			mensaje: '',
            pag: null,
			datos: null
		]

        def datosBitacora = [
            clase: "NotificacionService",
            metodo: "modificarFotografia",
            nombre: "Modificación de fotografia",
            descripcion: "Se ha modificado su notificacion",
            estatus: "ERROR"
        ]

		if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }

        def notificacion = NotificacionProfesional.findByUuid(params.uuid)
        if(!notificacion){
            resultado.mensaje = 'No se ha encontrado la notificacion'
            return resultado
        }
        if(!notificacion.activo){
            resultado.mensaje = 'notificacion inactiva'
            return resultado
        }

        if(!params.foto){
            resultado.mensaje = 'La foto es un dato requerido'
            return resultado
        }

        params.foto = efirmaService.base64toBytes(params.foto)
        notificacion.doc = TipoDocumento.get(params.docId)
        notificacion.opctitulacion = OpcTitulacion.get(params.opctitulacionId)
        notificacion.titulo = params.titulo
        notificacion.presidente = params.presidente
        notificacion.secretario = params.secretario
        notificacion.vocal = params.vocal
        notificacion.foto = params.foto

        if(!notificacion.save(flush:true)){
			notificacion.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar(datosBitacora)
            return resultado
		}

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = 'La notificacion se ha modificado'
		resultado.datos = notificacion

        return resultado
	}

      /**
     * Permite eliminar una notificacion
     * @param params (requerido)
     * parametros de la carrera
     * @return resultado
     * resultado con el estatus, mensaje y datos de la carrera
     */
	def eliminar(params){
		def resultado = [
				estatus: false,
				mensaje: '',
				datos: null
		]

        def datosBitacora = [
            clase: "NotificacionService",
            metodo: "eliminar",
            nombre: "Eliminación de notificacion",
            descripcion: "Se eliminó una notificacion",
            estatus: "ERROR"
        ]

		if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }

        def notificacion = NotificacionProfesional.findByUuid(params.uuid)
        if(!notificacion){
            resultado.mensaje = 'Notificacion no encontrada'
            return resultado
        }
        if(!notificacion.activo){
            resultado.mensaje = 'Notificacion inactiva'
            return resultado
        }

        if(notificacion.estatusNotificacion.id != GENERADO){
            resultado.mensaje = 'El certificado no puede ser eliminado'
            return resultado
        }

        // Se valida que el certificado pertenesca a la misma institución que el usuario
        def institucionId = notificacion.alumno.planEstudios.carrera.institucion.id
        if(!usuarioService.perteneceAInstitucion(institucionId)){
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
			return resultado
		}

		notificacion.activo = false

		if(!notificacion.save(flush:true)){
			notificacion.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar(datosBitacora)
			return resultado
		}

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = 'Notificacion dada de baja exitosamente'
		resultado.datos = notificacion

		return resultado
	}


    /**
     * Permite realizar la modificación de una notificacion
     * @param params
     * parametros del ciclo escolar
     * @param carreraId
     * id de la carrera
     * @return resultado
     * resultado con el estatus, mensaje y datos del ciclo escolar
     */
	def modificar(params) {
		def resultado = [
			estatus: false,
			mensaje: '',
            pag: null,
			datos: null
		]

        def datosBitacora = [
            clase: "NotificacionService",
            metodo: "modificar",
            nombre: "Modificación de notificacion",
            descripcion: "Se modificó una notificacion",
            estatus: "ERROR"
        ]

		if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }

        def notificacion = NotificacionProfesional.findByUuid(params.uuid)
        if(!notificacion){
            resultado.mensaje = 'Notificacion no econtrada'
            return resultado
        }
        if(!notificacion.activo){
            resultado.mensaje = 'La notificacion esta inactiva'
            return resultado
        }

        // Se valida que la constancia se encuentre en estatus  de generado o rechazado
        if(notificacion.estatusNotificacion.id != GENERADO && notificacion.estatusNotificacion.id != RECHAZADO_DGEMSS){
            resultado.mensaje = 'No puede ser modificada'
            return resultado
        }

        // Se valida que la constancia pertenezca a la misma institución que el usuario
        def institucionId = notificacion.persona.alumnos[0].cicloEscolar.carrera.institucion.id
        if(!usuarioService.perteneceAInstitucion(institucionId)){
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
			return resultado
		}

        def foto = params.foto
        def opctitulacion = params.opctitulacion
        def titulo = params.titulo
        def vocal = params.vocal
        def secretario = params.secretario
        def presidente = params.presidente
        params.foto = null
        params.titulo = null
        params.opctitulacion = null
        params.vocal = null
        params.secretario = null
        params.presidente = null


		if(!foto){
            resultado.mensaje = 'La foto es un dato requerido'
            resultado.pag = 2
            return resultado
        }
        
        if(!params.fechaExpedicion){
            resultado.mensaje = 'La fecha de expedición es un dato requerido'
            resultado.pag = 1
            return resultado
        }

        def fechaExpedicion
        try{
            fechaExpedicion = new SimpleDateFormat("yyyy-MM-dd").parse(params.fechaExpedicion)
        }catch(ex){
            resultado.mensaje = 'La fecha no tiene un formato válido'
            resultado.pag = 1
            return resultado
        }

        params.foto = efirmaService.base64toBytes(foto)
        params.opctitulacion = opctitulacion
        params.titulo = titulo
        params.presidente = presidente
        params.secretario = secretario
        
        params.vocal = vocal


        notificacion.properties = params
        notificacion.fechaExpedicion = fechaExpedicion

        if(!notificacion.save(flush:true)){
			notificacion.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar(datosBitacora)
            return resultado
		}

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = 'La notificacion se ha modificado'
		resultado.datos = notificacion

		return resultado
	}

    /* *Nos permite rechazar diferebtes notificaciones segun el tipo de usuario
    *Segun el tipo de usuario realizo el chazo tal como veremos posteiormente en el codigo
    */
    def cambiarEstatusRechazado(params){
        def roles = usuarioService.obtenerRoles()

		if(roles.contains('ROLE_DIRECTOR_ESCUELA')){
            return rechazarNotificacionEscuela(params)
        }

        if(roles.contains('ROLE_REVISOR') || roles.contains('ROLE_REVISOR_PUBLICA')){
            return rechazarNotificacionRevisor(params)
        }
        
        if(roles.contains('ROLE_AUTENTICADOR_DGEMSS')){
            return rechazarNotificacionDGEMSS(params)
        }
	}

    def rechazarNotificacionEscuela(params){
		def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

        def datosBitacora = [
            clase: "NotificacionService",
            metodo: "rechazarNotificacionEscuela",
            nombre: "Rechazo de notificacion - ESCUELA",
            descripcion: "Rechazo de notificacion - ESCUELA",
            estatus: "ERROR"
        ]

        // Se validan los parametros recibidos
		if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }
        def notificacion = NotificacionProfesional.findByUuid(params.uuid)
        if(!notificacion){
            resultado.mensaje = 'Notificacion no encontrado'
            return resultado
        }
        if(!notificacion.activo){
            resultado.mensaje = 'Notificacion inactivo'
            return resultado
        }

        // Se valida que la notificacion se encuentre en un estatus válido para la acción
        def notificacionEstatusId = notificacion.estatusNotificacion.id
        def estatusValidos = [FIRMANDO_ESCUELA]

        if(!(notificacionEstatusId in estatusValidos)){
            resultado.mensaje = 'No se puede rechazar la notificacion'
            return resultado
        }

        
        // Se valida que el usuario sea un director de escuela
		def roles = usuarioService.obtenerRoles()
		if(!roles.contains('ROLE_DIRECTOR_ESCUELA')){
            resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
		}

        // Se valida que el notificacion pertenezca a la misma institución que el director
        def institucionId = notificacion.alumno.planEstudios.carrera.institucion.id
        if(!usuarioService.perteneceAInstitucion(institucionId)){
            resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
        }

		notificacion.estatusNotificacion = EstatusNotificacion.get(RECHAZADO_DIRECTOR)
        notificacion.comentarios = params.comentarios

		if(!notificacion.save(flush:true)){
			notificacion.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			return resultado
		}

        // Se cambie el estatus del alumno, sus evaluaciones y sus ciclos para que se puedan editar nuevamente
        if(!cambiarEstatusRegistros(notificacion, estatusRegistroService.EDITABLE)){
            transactionStatus.setRollbackOnly()
            resultado.mensaje = "Hubo un error al intentar enviar el certificado a firmar. Por favor inténtelo nuevamente."
			return resultado
        }

		resultado.estatus = true
		resultado.mensaje = 'Notificacion rechazada'
		resultado.datos = notificacion

		return resultado
	}

    def rechazarNotificacionRevisor(params){
		def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

        def datosBitacora = [
            clase: "NotificacionService",
            metodo: "rechazarNotificacionRevisor",
            nombre: "Rechazo de notificacion - REVISOR",
            descripcion: "Rechazo de notificacion - REVISOR",
            estatus: "ERROR"
        ]

		if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }

        if(!params.comentarios){
            resultado.mensaje = 'Los comentarios son un dato requerido'
            return resultado
        }

        def notificacion = NotificacionProfesional.findByUuid(params.uuid)
        if(!notificacion){
            resultado.mensaje = 'Notificacion no encontrado'
            return resultado
        }
        if(!notificacion.activo){
            resultado.mensaje = 'Notificacion inactivo'
            return resultado
        }

        def notificacionEstatusId = notificacion.estatusNotificacion.id
        def estatusValidos = [EN_REVISION, RECHAZADO_AUTENTICADOR]

        if(!(notificacionEstatusId in estatusValidos)){
            resultado.mensaje = 'No se puede cambiar el estatus'
            return resultado
        }

        // La validación solo se realiza si la acción la hace un usuario REVISOR
		def roles = usuarioService.obtenerRoles()
		if(!(roles.contains('ROLE_REVISOR') || roles.contains('ROLE_REVISOR_PUBLICA'))){
            resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
		}

        def firmaDirectorEscuela = notificacion.firmaDirectorEscuela

		notificacion.estatusNotificacion = EstatusNotificacion.get(RECHAZADO_DGEMSS)
		notificacion.comentarios = params.comentarios
        notificacion.firmaDirectorEscuela = null

		if(!notificacion.save(flush:true)){
			notificacion.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			return resultado
		}

        // Se cambie el estatus del alumno, sus evaluaciones y sus ciclos para que se puedan editar nuevamente
        if(!cambiarEstatusRegistros(notificacion, estatusRegistroService.EDITABLE)){
            transactionStatus.setRollbackOnly()
            resultado.mensaje = "Hubo un error al intentar rechazar la notificacion. Por favor inténtelo nuevamente."
			return resultado
        }

        firmaDirectorEscuela.delete()

		resultado.estatus = true
		resultado.mensaje = 'Notificacion modificado exitosamente'
		resultado.datos = notificacion

		return resultado
	}

    def rechazarNotificacionDGEMSS(params){
		def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

        def datosBitacora = [
            clase: "NotificacionService",
            metodo: "rechazarNotificacionDGEMSS",
            nombre: "Rechazo de notificacion - DGEMSS",
            descripcion: "Rechazo de notificacion - DGEMSS",
            estatus: "ERROR"
        ]

		if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }

        if(!params.comentarios){
            resultado.mensaje = 'Los comentarios son un dato requerido'
            return resultado
        }

        def notificacion = NotificacionProfesional.findByUuid(params.uuid)
        if(!notificacion){
            resultado.mensaje = 'Notificacion no encontrado'
            return resultado
        }
        if(!notificacion.activo){
            resultado.mensaje = 'Notificacion inactivo'
            return resultado
        }

        def notificacionEstatusId = notificacion.estatusNotificacion.id
        def estatusValidos = [FIRMANDO_DGEMSS]

        if(!(notificacionEstatusId in estatusValidos)){
            resultado.mensaje = 'No se puede cambiar el estatus'
            return resultado
        }

        // La validación solo se realiza si la acción la hace un usuario AUTENTICADOR
		def roles = usuarioService.obtenerRoles()
		if(!(roles.contains('ROLE_AUTENTICADOR_DGEMSS'))){
            resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
		}

		notificacion.estatusNotificacion = EstatusNotificacion.get(RECHAZADO_AUTENTICADOR)
		notificacion.comentarios = params.comentarios

		if(!notificacion.save(flush:true)){
			notificacion.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			return resultado
		}

		resultado.estatus = true
		resultado.mensaje = 'Notificacion modificado exitosamente'
		resultado.datos = notificacion

		return resultado
	}


    def enviarAFirmarEscuela(params){
		def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

        def datosBitacora = [
            clase: "NotificacionService",
            metodo: "enviarAFirmarEscuela",
            nombre: "Envío para firma de escuela",
            descripcion: "Envío para firma de escuela",
            estatus: "ERROR"
        ]

        // Se validan los parametros recibidos
		if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }

        // Se valida la constancia
        def notificacion = NotificacionProfesional.findByUuid(params.uuid)
        if(!notificacion){
            resultado.mensaje = 'Notificacion no encontrada'
            return resultado
        }
        if(!notificacion.activo){
            resultado.mensaje = 'Notificacion inactiva'
            return resultado
        }

        // Se valida que la notificacion se encuentre en un estatus válido para la acción
        def notificacionEstatusId = notificacion.estatusNotificacion.id
        def estatusValidos = [GENERADO, RECHAZADO_DGEMSS, RECHAZADO_DIRECTOR]

        if(!(notificacionEstatusId in estatusValidos)){
            resultado.mensaje = 'No se puede cambiar el estatus'
            return resultado
        }

        // Se valida que la acción la realice un usuario de tipo Gestor de Escuela
		def roles = usuarioService.obtenerRoles()
		if(!roles.contains('ROLE_GESTOR_ESCUELA')){
            resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
		}

        // Se valida que la notificacion pertenezca a la misma institución que el usuario
        def institucionId = notificacion.alumno.planEstudios.carrera.institucion.id
        if(!usuarioService.perteneceAInstitucion(institucionId)){
            resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
        }

        // Se asigna el nuevo estatus
		notificacion.estatusNotificacion = EstatusNotificacion.get(FIRMANDO_ESCUELA)
		
		if(!notificacion.save(flush:true)){
            transactionStatus.setRollbackOnly()
            resultado.mensaje = errorService.obtenerErrores(notificacion)[0]
			return resultado
		}

        // Se cambie el estatus del alumno, sus evaluaciones y sus ciclos para que no puedan se editados durante el proceso
        if(!cambiarEstatusRegistros(notificacion, estatusRegistroService.NO_EDITABLE)){
            transactionStatus.setRollbackOnly()
            resultado.mensaje = "Hubo un error al intentar enviar el certificado a firmar. Por favor inténtelo nuevamente."
			return resultado
        }

        // Se guarda la acción en la bitácora
        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = 'La accion fue exitosa'
		resultado.datos = notificacion

		return resultado
	}

     def cambiarEstatusRegistros(notificacion, estatusRegistroId){
        def nuevoEstatusRegistro = EstatusRegistro.get(estatusRegistroId)

        def alumno = notificacion.alumno

        // Se cambia el estatus del alumno si no esta bloqueado
        if(alumno.estatusRegistro.id != estatusRegistroService.BLOQUEADO){
            alumno.estatusRegistro = nuevoEstatusRegistro

            if(!alumno.save(flush: true)){
                return false
            }          
        }

        // Se cambia el estatus de las evaluaciones si no estan bloqueadas
        for (evaluacion in alumno.evaluaciones) {
            if(!evaluacion.activo) continue
            if(evaluacion.estatusRegistro.id == estatusRegistroService.BLOQUEADO) continue

            evaluacion.estatusRegistro = nuevoEstatusRegistro
            if(!evaluacion.save(flush: true)){
                return false
            }
        }

        // Se cambia el estatus de las inscripciones del alumno
        for (inscripcion in alumno.ciclosEscolares) {
            if(!inscripcion.activo) continue
            if(inscripcion.estatusRegistro.id == estatusRegistroService.BLOQUEADO) continue

            inscripcion.estatusRegistro = nuevoEstatusRegistro
            if(!inscripcion.save(flush: true)){
                return false
            }

        }

        // Se cambia el estatus de los ciclos a los que esta incrito el alumno si no estan bloqueados
        for (inscripcion in alumno.ciclosEscolares) {
            def cicloEscolar = inscripcion.cicloEscolar

            if(!inscripcion.activo) continue
            if(inscripcion.estatusRegistro.id == estatusRegistroService.BLOQUEADO) continue

            // Si lo que se intenta es habilitar el registro para su edición
            // se valida que el ciclo escolar no cuente con más relaciones bloquedas
            if(nuevoEstatusRegistro.id == estatusRegistroService.EDITABLE){
                def sql = new Sql(dataSource)
                def numInscripcionesBloqueadas = sql.rows(
                    "SELECT COUNT(*) as num " +
                    "FROM alumno_ciclo_escolar WHERE " + 
                    "activo = true AND " + 
                    "ciclo_escolar_id = ${cicloEscolar.id} AND " + 
                    "estatus_registro_id <> 1 " + 
                    "GROUP BY ciclo_escolar_id"
                )
                if(numInscripcionesBloqueadas) continue
            }

            cicloEscolar.estatusRegistro = nuevoEstatusRegistro
            
            if(!cicloEscolar.save(flush: true)){
                return false
            }

        }

        return true
    }

    def firmarNotificacion(params){
        def roles = usuarioService.obtenerRoles()

        if(roles.contains('ROLE_DIRECTOR_ESCUELA')){
            return firmarNotificacionEscuela(params)
        }
        
        if(roles.contains('ROLE_AUTENTICADOR_DGEMSS')){
            return firmarNotificacionDGEMSS(params)
        }
    }
    def obtenerCadenaOriginalFirmaEscuela(parametros, fechaGeneracion){

        def fechaGeneracionConstancia = new SimpleDateFormat("YYYY-MM-dd'T'hh:mm:ss").format(fechaGeneracion)

        def cadenaOriginal = "||2.0"

        cadenaOriginal += "|5" // tipoNotificacion
        cadenaOriginal += "|${parametros.uuid}"
        cadenaOriginal += "|${parametros.folioControl}"
        cadenaOriginal += "|${parametros.institucionId}"
        cadenaOriginal += "|${parametros.institucion}"
        cadenaOriginal += "|${parametros.institucionClaveCt}"
        cadenaOriginal += "|${parametros.carrera}"
        cadenaOriginal += "|${parametros.carreraNivel}"
        cadenaOriginal += "|${parametros.matricula}"
        cadenaOriginal += "|${parametros.curp}"
        cadenaOriginal += "|${parametros.nombre}"
        cadenaOriginal += "|${parametros.primerApellido}"
        cadenaOriginal += "|${parametros.segundoApellido}"
        cadenaOriginal += "|${parametros.fechaNacimiento}"
        cadenaOriginal += "|${parametros.tipoConstancia}"
        cadenaOriginal += "|${fechaGeneracionConstancia}"

        parametros.materias.each{ item ->
            item.each{ llave, valor ->
                cadenaOriginal += "|${valor}"
            }
        }
        parametros.materias2.each{ item ->
            item.each{ llave, valor ->
                cadenaOriginal += "|${valor}"
            }
        }

        cadenaOriginal += "||"

        return cadenaOriginal
    }


    def firmarNotificacionEscuela(params){
		def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

        def datosBitacora = [
            clase: "NotificacionService",
            metodo: "firmarCosntanciaEscuela",
            nombre: "Firma de notificacion - Escuela",
            descripcion: "Firma de notificacion - Escuela",
            estatus: "ERROR"
        ]

        // Se validan los parametros recibidos
        if(!params.firmaId){
            resultado.mensaje = "El id de la firma es un dato requerido"
            return resultado
        }

        def persona = usuarioService.obtenerUsuarioLogueado().persona
        def firmaElectronica = FirmaElectronica.get(params.firmaId)

        if(!firmaElectronica){
            resultado.mensaje = "Firma no encontrada"
            return resultado
        }

        def perteneceAUsuario = persona.firmasElectronicas.any{ firma -> firma.id == firmaElectronica.id }

        if(!perteneceAUsuario){
            resultado.mensaje = "Permiso denegado"
            return resultado
        }

        if(firmaElectronica.expiro()){
            resultado.mensaje = 'El certificado (.cer) ha expirado'
            return resultado
        }

        // Se valida la clave privada y contraseña
        def bytesClavePrivada
        def contrasena

        if(!firmaElectronica.archivoKey){
            if(!params.clavePrivada){
                resultado.mensaje = 'La clave privada es un dato requerido'
                return resultado
            }
            bytesClavePrivada = params.clavePrivada.getBytes()
        }else{
            bytesClavePrivada = firmaElectronica.archivoKey
        }

        if(!params.contrasena){
            resultado.mensaje = 'La contraseña es un dato requerido'
            return resultado
        }
        contrasena = params.contrasena

        def resultadoValidacion = efirmaService.validarContrasena(bytesClavePrivada, contrasena)
        if (!resultadoValidacion){
            resultado.mensaje = 'La clave privada o contraseña son incorrectos'
            return resultado
        }

        if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }

        def notificacion = NotificacionProfesional.findByUuid(params.uuid)
        if(!notificacion){
            resultado.mensaje = 'Notificacion no encontrado'
            return resultado
        }
        if(!notificacion.activo){
            resultado.mensaje = 'Notificacion inactivo'
            return resultado
        }

        // Se valida que el certificado se encuentre en un estatus válido para la acción
        def notificacionEstatusId = notificacion.estatusNotificacion.id
        def estatusValidos = [FIRMANDO_ESCUELA]

        if(!(notificacionEstatusId in estatusValidos)){
            resultado.mensaje = 'La notificacion no puede ser firmada'
            return resultado
        }

        def roles = usuarioService.obtenerRoles()

        def zona = ZoneId.systemDefault()
        def fechaFirma = Date.from(LocalDate.now().atStartOfDay(zona).toInstant())
        def parametros
        def cadenaOriginal

        parametros = obtenerParametrosNotificacion(notificacion)
        cadenaOriginal = obtenerCadenaOriginalFirmaEscuela(parametros, fechaFirma)

        def selloDigital = efirmaService.firmar(bytesClavePrivada, contrasena, cadenaOriginal)

        def firma = new Firma()
        firma.selloDigital = selloDigital
        firma.firmaElectronica = firmaElectronica
        firma.fechaFirma = fechaFirma

        if(!firma.save(flush:true)){
            firma.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			return resultado
        }

        notificacion.firmaDirectorEscuela = firma
        // Validar estatus de procedencia con el tramite id
        if(notificacion.alumno.planEstudios.carrera.institucion.publica){
            notificacion.estatusNotificacion = EstatusNotificacion.get(EN_REVISION)  
        }else if(notificacion.tramite != null){
            notificacion.estatusNotificacion = EstatusNotificacion.get(EN_REVISION)
        }else{
            notificacion.estatusNotificacion = EstatusNotificacion.get(EN_ESPERA)
        }

        if(!notificacion.save(flush:true)){
			notificacion.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			return resultado
		}

        // Se guarda la acción en la bitácora
        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

        resultado.estatus = true
        resultado.mensaje = "Notificacion firmada exitosamente"
        return resultado
    }

     def obtenerParametrosNotificacion(notificacion){
        def alumno = notificacion.alumno
        def persona = alumno.persona
        def planEstudios = alumno.planEstudios
        def carrera = alumno.planEstudios.carrera
        def institucion = carrera.institucion

        def asignaturas = Asignatura.createCriteria().list {
            createAlias("planEstudios", "p", CriteriaSpecification.LEFT_JOIN)
            createAlias("formacion", "f", CriteriaSpecification.LEFT_JOIN)
            createAlias("formacion.tipoFormacion", "tf", CriteriaSpecification.LEFT_JOIN)
            and{
                eq("activo", true)
                eq("tf.nombre", "BASICA")
                eq("p.id", planEstudios.id)
            }
        }

        // formateadores de fecha
        def formatoFechaXml = new SimpleDateFormat("YYYY-MM-dd'T'hh:mm:ss")
        def formatoFechaPdf = new SimpleDateFormat("dd/MM/yyyy")
        def formatoFechaRvoe = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy")

        def parametros = [:]
            // alumno
        parametros.matricula = alumno.matricula.trim().toUpperCase()
            // persona
        parametros.curp = persona.curp.trim().toUpperCase()
        parametros.nombre = persona.nombre.trim().toUpperCase()
        parametros.primerApellido = persona.primerApellido.trim().toUpperCase()
        parametros.segundoApellido = persona.segundoApellido ? persona.segundoApellido.trim().toUpperCase() : ""
        parametros.fechaNacimiento = formatoFechaPdf.parse(persona.fechaNacimiento)
        parametros.fechaNacimiento = formatoFechaXml.format(parametros.fechaNacimiento)
        parametros.fechaNacimiento = parametros.fechaNacimiento.trim().toUpperCase()
            //institucion
        parametros.institucionId = institucion.id
        parametros.institucion = institucion.nombre.trim().toUpperCase()
        parametros.institucionMunicipio = institucion.domicilio.municipio.trim().toUpperCase()
        parametros.institucionClaveCt = institucion.claveCt ? institucion.claveCt.toUpperCase() : ""
        parametros.logo = new ByteArrayInputStream(institucion.logo)
        parametros.rvoe = planEstudios.rvoe ? planEstudios.rvoe.trim().toUpperCase() : ""
        parametros.fechaRvoe = planEstudios.fechaRvoe ? formatoFechaRvoe.format(planEstudios.fechaRvoe).trim().toUpperCase() : ""
        parametros.fechaRvoeCadena = planEstudios.fechaRvoe ? formatoFechaXml.format(planEstudios.fechaRvoe).trim().toUpperCase() : ""
            //carrera
        parametros.carrera = carrera.nombre.trim().toUpperCase()
        parametros.modalidad = carrera.modalidad ? carrera.modalidad.nombre.trim().toUpperCase() : ""
        parametros.claveSeem = carrera.claveSeem ? carrera.claveSeem.trim().toUpperCase() : ""
        parametros.carreraNivel = carrera.nivel ? carrera.nivel.nombre.trim().toUpperCase() : ""
            //notificacion
        parametros.foto = new ByteArrayInputStream(notificacion.foto)
        parametros.uuid = notificacion.uuid
        parametros.folioControl = notificacion.folioControl
        parametros.opctitulacion = notificacion.opctitulacion.nombre
        parametros.titulo = notificacion.titulo
        parametros.presidente = notificacion.presidente
        parametros.doc = notificacion.doc.nombre
        parametros.vocal = notificacion.vocal
        parametros.secretario = notificacion.secretario

        parametros.numPaginas = 1

        if(notificacion.estatusNotificacion.id == FINALIZADO){
            //datos firmante escuela
            def datosCerDirector = notificacion.firmaDirectorEscuela.firmaElectronica

            parametros.firmanteEscuelaSello = notificacion.firmaDirectorEscuela.selloDigital
            parametros.firmanteEscuelaCer = efirmaService.bytesToBase64(datosCerDirector.archivoCer)
            parametros.firmanteEscuelaNoSerie = datosCerDirector.numeroSerieCer.trim().toUpperCase()
            parametros.firmanteEscuelaCurp = datosCerDirector.curpCer.trim().toUpperCase()
            parametros.firmanteEscuelaNombre = datosCerDirector.persona.nombre.trim().toUpperCase()
            parametros.firmanteEscuelaPrimerApellido = datosCerDirector.persona.primerApellido.trim().toUpperCase()
            parametros.firmanteEscuelaSegundoApellido = datosCerDirector.persona.segundoApellido.trim().toUpperCase()
            parametros.firmanteEscuelaNombreCompleto = datosCerDirector.nombreCer.trim().toUpperCase()
            parametros.firmanteEscuelaTitulo = datosCerDirector.persona.titulo.trim().toUpperCase()
            parametros.firmanteEscuelaFecha = formatoFechaXml.format(notificacion.firmaDirectorEscuela.fechaFirma)
            parametros.firmanteEscuelaCargo = datosCerDirector.persona.usuario.cargo.trim().toUpperCase()

            //datos firmante dgmess
            def datosCerAutenticador = notificacion.firmaAutenticadorDgemss.firmaElectronica

            parametros.firmanteDgemssSello = notificacion.firmaAutenticadorDgemss.selloDigital
            parametros.firmanteDgemssNoSerie = datosCerAutenticador.numeroSerieCer.trim().toUpperCase()
            parametros.firmanteDgemssCurp = datosCerAutenticador.curpCer.trim().toUpperCase()
            parametros.firmanteDgemssNombreCompleto = datosCerAutenticador.nombreCer.trim().toUpperCase()
            parametros.firmanteDgemssTitulo = datosCerAutenticador.persona.titulo.trim().toUpperCase()
            parametros.firmanteDgemssFecha = formatoFechaPdf.format(notificacion.firmaAutenticadorDgemss.fechaFirma)
            parametros.firmanteDgemssFechaCadena = formatoFechaXml.format(notificacion.firmaAutenticadorDgemss.fechaFirma)
            parametros.firmanteDgemssCargo = datosCerAutenticador.persona.usuario.cargo.trim().toUpperCase()

            //Generar código QR
            def url = "NotificacionProfesional/consultarNotificacion?uuid=${notificacion.uuid}"
            def resultadoQr = zxingService.generarQr(url)
            if (resultadoQr.estatus) {
                parametros.qr = new ByteArrayInputStream(resultadoQr.qr)
            }
        }

        return parametros
    }


    def firmarNotificacionDGEMSS(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

        def datosBitacora = [
            clase: "NotificacionService",
            metodo: "firmarNotificacionDGEMSS",
            nombre: "Firma de la notificacion - DGEMSS",
            descripcion: "Firma de notificacion - DGEMSS",
            estatus: "ERROR"
        ]

        // Se validan los parametros recibidos
        if(!params.firmaId){
            resultado.mensaje = "El id de la firma es un dato requerido"
            return resultado
        }

        def persona = usuarioService.obtenerUsuarioLogueado().persona
        def firmaElectronica = FirmaElectronica.get(params.firmaId)

        if(!firmaElectronica){
            resultado.mensaje = "Firma no encontrada"
            return resultado
        }

        def perteneceAUsuario = persona.firmasElectronicas.any{ firma -> firma.id == firmaElectronica.id }

        if(!perteneceAUsuario){
            resultado.mensaje = "Permiso denegado"
            return resultado
        }

        if(firmaElectronica.expiro()){
            resultado.mensaje = 'El certificado (.cer) ha expirado'
            return resultado
        }

        // Se valida la clave privada y contraseña
        def bytesClavePrivada
        def contrasena
        
        if(!firmaElectronica.archivoKey){
            if(!params.clavePrivada){
                resultado.mensaje = 'La clave privada es un dato requerido'
                return resultado
            }
            bytesClavePrivada = params.clavePrivada.getBytes()
        }else{
            bytesClavePrivada = firmaElectronica.archivoKey
        }

        if(!params.contrasena){
            resultado.mensaje = 'La contraseña es un dato requerido'
            return resultado
        }
        contrasena = params.contrasena

        def resultadoValidacion = efirmaService.validarContrasena(bytesClavePrivada, contrasena)
        if (!resultadoValidacion){
            resultado.mensaje = 'La clave privada o contraseña son incorrectos'
            return resultado
        }

        if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }
        
        def notificacion = NotificacionProfesional.findByUuid(params.uuid)
        if(!notificacion){
            resultado.mensaje = 'notificacion no encontrada'
            return resultado
        }
        if(!notificacion.activo){
            resultado.mensaje = 'notificacion inactiva'
            return resultado
        }

        // Se valida que la constancia se encuentre en un estatus válido para la acción
        def notificacionEstatusId = notificacion.estatusNotificacion.id
        def estatusValidos = [FIRMANDO_DGEMSS]

        if(!(notificacionEstatusId in estatusValidos)){
            resultado.mensaje = 'La constancia no puede ser firmada'
            return resultado
        }

        def roles = usuarioService.obtenerRoles()
        
        def zona = ZoneId.systemDefault()
        def fechaFirma = Date.from(LocalDate.now().atStartOfDay(zona).toInstant())
        def cadenaOriginal

        cadenaOriginal = obtenerCadenaOriginalFirmaDgemss(
            fechaFirma, 
            notificacion.firmaDirectorEscuela.selloDigital,
            firmaElectronica
        )
        
        def selloDigital = efirmaService.firmar(bytesClavePrivada, contrasena, cadenaOriginal)

        def firma = new Firma()
        firma.selloDigital = selloDigital
        firma.firmaElectronica = firmaElectronica
        firma.fechaFirma = fechaFirma

        if(!firma.save(flush:true)){
            firma.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			return resultado
        }
        
        notificacion.firmaAutenticadorDgemss = firma
        notificacion.estatusNotificacion = EstatusNotificacion.get(FINALIZADO)

        if(!notificacion.save(flush:true)){
			notificacion.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			return resultado
		}

        // Se cambie el estatus del alumno, sus evaluaciones y sus ciclos para que no puedan se modificados más
        if(!cambiarEstatusRegistros(notificacion, estatusRegistroService.BLOQUEADO)){
            transactionStatus.setRollbackOnly()
            resultado.mensaje = "Hubo un error al intentar enviar la constancia a firmar. Por favor inténtelo nuevamente."
			return resultado
        }

        // Se guarda la acción en la bitácora
        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

        resultado.estatus = true
        resultado.mensaje = "Constancia firmada exitosamente"
        return resultado
    }

     def obtenerCadenaOriginalFirmaDgemss(fechaExpedicion, selloDirectorEscuela, firmaElectronica){

        def fechaExpedicionConstancia = new SimpleDateFormat("YYYY-MM-dd'T'hh:mm:ss").format(fechaExpedicion)

        def cadenaOriginal = "||"

        cadenaOriginal += "${fechaExpedicionConstancia}"
        cadenaOriginal += "|${selloDirectorEscuela}"
        cadenaOriginal += "|${firmaElectronica.numeroSerieCer.trim().toUpperCase()}"
        cadenaOriginal += "|${firmaElectronica.curpCer.trim().toUpperCase()}"
        cadenaOriginal += "|${firmaElectronica.persona.usuario.cargo.trim().toUpperCase()}"
        
        cadenaOriginal += "||"

        return cadenaOriginal
    }

    def obtenerCadenaOriginalPdf(parametros){

        def cadenaOriginal = "||2.0"

        cadenaOriginal += "|5" // tipoNotificacion
        cadenaOriginal += "|${parametros.uuid}"
        cadenaOriginal += "|${parametros.folioControl}"
        cadenaOriginal += "|${parametros.institucionId}"
        cadenaOriginal += "|${parametros.institucion}"
        cadenaOriginal += "|${parametros.institucionClaveCt}"
        cadenaOriginal += "|${parametros.institucionMunicipio}"
        cadenaOriginal += "|${parametros.carrera}"
        cadenaOriginal += "|${parametros.matricula}"
        cadenaOriginal += "|${parametros.curp}"
        cadenaOriginal += "|${parametros.nombre}"
        cadenaOriginal += "|${parametros.primerApellido}"
        cadenaOriginal += "|${parametros.segundoApellido}"
        cadenaOriginal += "|${parametros.fechaNacimiento}"
        cadenaOriginal += "|${parametros.firmanteEscuelaFecha}"
        
        
        
        cadenaOriginal += "||"

        return cadenaOriginal
    }

    def generarDocumentosNotificacion(notificacionUuid){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

        def notificacion = NotificacionProfesional.findByUuid(notificacionUuid)
        def parametrosNotificacion = []
        def cadenaOriginal

        parametrosNotificacion = obtenerParametrosNotificacion(notificacion)
        cadenaOriginal = obtenerCadenaOriginalPdf(parametrosNotificacion)

        parametrosNotificacion.cadenaOriginal = cadenaOriginal
        // Se genera el archivo pdf
        def nombrePlantilla 
        if(parametrosNotificacion.doc == "Diploma"){
            nombrePlantilla="notificacion_especialidad"
        }else  if(parametrosNotificacion.doc == "Grado"){
            nombrePlantilla="notificacion_grado"
        }else  if(parametrosNotificacion.doc == "Titulo"){
            nombrePlantilla="notificacion"
        }

        def resultadoExpedicionPdf = documentoService.generar(nombrePlantilla, parametrosNotificacion)
        if (!resultadoExpedicionPdf.estatus) {
            resultado.mensaje = resultadoExpedicionPdf.mensaje
            return resultado
        }

        // Se genera el archivo xml
        def resultadoExpedicionXml = xmlService.generar(parametrosNotificacion)
        if (!resultadoExpedicionXml.estatus) {
            resultado.mensaje = resultadoExpedicionXml.mensaje
            return resultado
        }
        

        notificacion.pdf = resultadoExpedicionPdf.documento
        notificacion.xml = resultadoExpedicionXml.documento

		if(!notificacion.save(flush:true)){
			notificacion.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			return resultado
		}

		resultado.estatus = true
		resultado.mensaje = 'Documentos generados exitosamente'
		resultado.datos = notificacion

		return resultado
    }

}