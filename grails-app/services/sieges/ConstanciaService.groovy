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

/**
 * @author Alan Guevarin
 * @since 2022
 */


@Transactional
class ConstanciaService {
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

    // Id de los EstatusActa
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
     * Obtiene las constancias con parametros de paginación y filtrado
     *
     * @param instituciones Filtrar por instituciones a las que pertenece un usuario
     * @param estatusConstancias Filtrar por estatus de la constancia
     * @param institucionId Filtrar por una institución específica
     * @param estatusConstanciaId Filtrar por une status específico
     * @param tieneTramite Filtrar los que tienen o no tramites
     * @param esPublica Filtrar los que son o no de instituciones públicas
     * @param alumnoId Filtrar por alumno
     *
     * @return resultado con el estatus, mensaje y constancias
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
        def estatusConstancias = params.estatusConstancias
        def tieneTramite = params.tieneTramite
        def esPublica = params.esPublica

        // Parametros de filtrado opcionales
        def institucionId = params.institucionId ? params.institucionId.toInteger() : null
        def carreraId = params.carreraId ? params.carreraId.toInteger() : null
        def planEstudiosId = params.planEstudiosId ? params.planEstudiosId.toInteger() : null
        def estatusConstanciaId = params.estatusConstanciaId ? params.estatusConstanciaId.toInteger() : null
        def alumnoId = params.alumnoId ? params.alumnoId.toInteger() : null
        def search = params.search ? params.search : null

        def criteria = {
            createAlias("estatusConstancia", "e", CriteriaSpecification.LEFT_JOIN)
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
                    estatusConstancias.each{ registro ->
                        eq("e.id", registro)
                    }
                }

                if(institucionId != null){
 				    eq("inst.id", institucionId)
 				}

                if(estatusConstanciaId != null){
                    eq("e.id", estatusConstanciaId)
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

        def constancias = ConstanciaServicio.createCriteria().list(params, criteria)

        resultado.estatus = true
        resultado.mensaje = "Las constancias fueron consultadas exitosamenter ☻"
        resultado.datos = constancias

        return resultado
    }
    /**
     * Se obtienen las constancias
     *
     * @param params con los parametros de pagínacion (opcional)
     *
     * @return resultado con el estatus, mensaje y constancias
     */
    def listarConstancias(params){
        def usuario = usuarioService.obtenerUsuarioLogueado()
        def roles = usuarioService.obtenerRoles()

        params.instituciones = null
        params.estatusConstancias = null
        params.tieneTramite = null
        params.esPublica = null

        if(roles.contains('ROLE_GESTOR_ESCUELA') || roles.contains('ROLE_DIRECTOR_ESCUELA')){
            params.instituciones = usuario.instituciones
        }
        if(roles.contains('ROLE_AUTENTICADOR_DGEMSS')){
            params.esPublica = params.isPublic ? (params.isPublic.equals("false") ? true : false) : false
            params.instituciones = []
        }

        def constancias = listar(params)

        return constancias
    }

    def listarConstanciasRevisar(params){
        def usuario = usuarioService.obtenerUsuarioLogueado()
        def roles = usuarioService.obtenerRoles()

        params.instituciones = null
        params.estatusConstancias = [EN_REVISION, RECHAZADO_AUTENTICADOR]
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

        def constancias = listar(params)

        return constancias
    }

    def listarConstanciaFirmar(params){
        def usuario = usuarioService.obtenerUsuarioLogueado()
        def roles = usuarioService.obtenerRoles()

        params.instituciones = null
        params.estatusConstancias = [FIRMANDO_DGEMSS]
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
        
        def constancias = listar(params)

        return constancias
    }
    /**
     * Obtiene las constancias a firmar
     *
     * @param params con los parametros de pagínacion (opcional)
     *
     * @return resultado con el estatus, mensaje y constancias
     */
    def listarConstanciasFirmar(params){
        def usuario = usuarioService.obtenerUsuarioLogueado()
		def roles = usuarioService.obtenerRoles()

        params.instituciones = null
        params.estatusConstancias = null
        params.tieneTramite = null
        params.esPublica = null

        if(roles.contains('ROLE_DIRECTOR_ESCUELA')){
            params.instituciones = usuario.instituciones
            params.estatusConstancias = [FIRMANDO_ESCUELA]
        }
        if(roles.contains('ROLE_AUTENTICADOR_DGEMSS')){
            params.instituciones = []
            params.estatusConstancias = [FIRMANDO_DGEMSS]
        }

        def constancias = listar(params)

        return constancias
    }
    /**
     * Obtiene lss constancias finalizados por alumno
     *
     * @param params con los parametros de pagínacion (opcional)
     *
     * @return resultado con el estatus, mensaje y constancias
     */
    def listarConstaciasFinalizadosPorAlumno(params){
        def usuario = usuarioService.obtenerUsuarioLogueado()
		def roles = usuarioService.obtenerRoles()

        params.instituciones = null
        params.estatusConstancias = [FINALIZADO]
        params.tieneTramite = null
        params.esPublica = null

        if(roles.contains('ROLE_GESTOR_ESCUELA') || roles.contains('ROLE_DIRECTOR_ESCUELA')){
            params.instituciones = usuario.instituciones
        }
        if(roles.contains('ROLE_AUTENTICADOR_DGEMSS')){
            params.instituciones = []
        }
        
        def constancias = listar(params)

        return constancias
    }

    /**
     * Obtiene los certificados sin tramite
     *
     * @param params con los parametros de pagínacion (opcional)
     *
     * @return resultado con el estatus, mensaje y certificados
     */
    def listarConstanciasSinTramite(params){
		def usuario = usuarioService.obtenerUsuarioLogueado()
		def roles = usuarioService.obtenerRoles()

        params.estatusConstancias = [EN_ESPERA]
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
            createAlias("estatusConstancia", "e", CriteriaSpecification.LEFT_JOIN)
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

        def certificados = ConstanciaServicio.createCriteria().list(params, criteria)

        resultado.estatus = true
        resultado.mensaje = "Constancias consultadas exitosamente"
        resultado.datos = certificados

        return resultado
    }
    /**
     * Permite realizar el registro de constancias
     * @param foto
     * @return resultado
     * resultado con el estatus, mensaje y datos de la constancia
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
            clase: "ConstanciasService",
            metodo: "registrar",
            nombre: "Registro de constancia",
            descripcion: "Se registró una constancia",
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

        if(!params.foto){
            resultado.mensaje = 'Se requiere de un logo'
            return resultado
        }

        def constancia = new ConstanciaServicio()
        constancia.uuid = efirmaService.generarUuid()
        constancia.foto = efirmaService.base64toBytes(params.foto)
        constancia.folioControl = generaIdentificadorEstructurado(alumno.planEstudios.carrera.institucion)
        constancia.opc = params.opc ? params.opc.toInteger():null
        constancia.alumno = alumno
        constancia.estatusConstancia = EstatusConstancia.get(GENERADO)

        if(!constancia.save(flush:true)){
			constancia.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar(datosBitacora)
            return resultado
		}

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = 'Constancia creada exitosamente'
		resultado.datos = constancia

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
    //rechazar
    def generaIdentificadorConsecutivo() {
        def identificador = ""

        identificador = "${(ConstanciaServicio.count()?:0) + 1}"

        return identificador
    }
     /**
     * Permite realizar la consulta del registro seleccionado con su información correspondiente.
     * @param id
     * id del constancia
     * @return resultado
     * resultado con el estatus, mensaje y datos de la constancia
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

        def constancia = ConstanciaServicio.findByUuid(params.uuid)

        if(!constancia){
            resultado.mensaje = 'No se ha encontrado la constancia'
            return resultado
        }

        if(!constancia.activo){
            resultado.mensaje = 'Constancia inactiva'
            return resultado
        }


		def roles = usuarioService.obtenerRoles()
		if(roles.contains('ROLE_GESTOR_ESCUELA') || roles.contains('ROLE_DIRECTOR_ESCUELA')){
            // Se valida que el certificado pertenesca a la misma institución que el usuario
            def institucionId = constancia.alumno.planEstudios.carrera.institucion.id
            if(!usuarioService.perteneceAInstitucion(institucionId)){
                resultado.mensaje = 'No esta autorizado para realizar esta acción'
                return resultado
            }
		}

        resultado.estatus = true
        resultado.mensaje = 'Se ha encontrado la constancia'
        resultado.datos = constancia

        return resultado
    }
    def consultarConstancia(params) {
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }

        def constancia = ConstanciaServicio.findByUuid(params.uuid)

        if(!constancia || !constancia.activo || constancia.estatusConstancia.id != 9){
            resultado.mensaje = 'No se ha encontrado la constancia'
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'La constancia se ha encontrado'
        resultado.datos = constancia

        return resultado
    }

    /**
     * Permite realizar la modificación de una constancia
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
            clase: "ConstanciaService",
            metodo: "modificar",
            nombre: "Modificación de constancia",
            descripcion: "Se modificó una constancia",
            estatus: "ERROR"
        ]

		if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }

        def constancia = ConstanciaServicio.findByUuid(params.uuid)
        if(!constancia){
            resultado.mensaje = 'Constancia no econtrada'
            return resultado
        }
        if(!constancia.activo){
            resultado.mensaje = 'La constancia esta inactiva'
            return resultado
        }

        // Se valida que la constancia se encuentre en estatus  de generado o rechazado
        if(constancia.estatusConstancia.id != GENERADO && constancia.estatusConstancia.id != RECHAZADO_DGEMSS){
            resultado.mensaje = 'No puede ser modificada'
            return resultado
        }

        // Se valida que la constancia pertenezca a la misma institución que el usuario
        def institucionId = constancia.persona.alumnos[0].cicloEscolar.carrera.institucion.id
        if(!usuarioService.perteneceAInstitucion(institucionId)){
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
			return resultado
		}

        def foto = params.foto
        params.foto = null

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

        constancia.properties = params
        constancia.fechaExpedicion = fechaExpedicion

        if(!constancia.save(flush:true)){
			constancia.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar(datosBitacora)
            return resultado
		}

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = 'La constancia se ha modificado'
		resultado.datos = constancia

		return resultado
	}

    /* *Nos permite rechazar diferentes constancias segun el tipo de usuario
    *Segun el tipo de usuario realizo el chazo tal como veremos posteiormente en el codigo
    */
    def cambiarEstatusRechazado(params){
        def roles = usuarioService.obtenerRoles()

		if(roles.contains('ROLE_DIRECTOR_ESCUELA')){
            return rechazarConstanciaEscuela(params)
        }

        if(roles.contains('ROLE_REVISOR') || roles.contains('ROLE_REVISOR_PUBLICA')){
            return rechazarConstanciaRevisor(params)
        }
        
        if(roles.contains('ROLE_AUTENTICADOR_DGEMSS')){
            return rechazarConstanciaDGEMSS(params)
        }
	}

    /*Permite rechazar la contancia por parte del director de la institución educativa.*/

    def rechazarConstanciaEscuela(params){
		def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

        def datosBitacora = [
            clase: "ConstanciaService",
            metodo: "rechazarConstanciaEscuela",
            nombre: "Rechazo de constancia - ESCUELA",
            descripcion: "Rechazo de constancia - ESCUELA",
            estatus: "ERROR"
        ]

        // Se validan los parametros recibidos
		if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }
        def constancia = ConstanciaServicio.findByUuid(params.uuid)
        if(!constancia){
            resultado.mensaje = 'Constancia no encontrada'
            return resultado
        }
        if(!constancia.activo){
            resultado.mensaje = 'Constancia inactiva'
            return resultado
        }

        // Se valida que el constancia se encuentre en un estatus válido para la acción
        def constanciaEstatusId = constancia.estatusConstancia.id
        def estatusValidos = [FIRMANDO_ESCUELA]

        if(!(constanciaEstatusId in estatusValidos)){
            resultado.mensaje = 'No se puede rechazar la constancia'
            return resultado
        }

        
        // Se valida que el usuario sea un director de escuela
		def roles = usuarioService.obtenerRoles()
		if(!roles.contains('ROLE_DIRECTOR_ESCUELA')){
            resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
		}

        // Se valida que la constancia pertenezca a la misma institución que el director
        def institucionId = constancia.alumno.carrera.institucion.id
        if(!usuarioService.perteneceAInstitucion(institucionId)){
            resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
        }

		constancia.estatusConstancia = EstatusConstancia.get(RECHAZADO_DIRECTOR)

		if(!constancia.save(flush:true)){
			constancia.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			return resultado
		}

        // Se cambie el estatus del alumno, sus evaluaciones y sus ciclos para que se puedan editar nuevamente
        if(!cambiarEstatusRegistros(constancia, estatusRegistroService.EDITABLE)){
            transactionStatus.setRollbackOnly()
            resultado.mensaje = "Hubo un error al intentar enviar el certificado a firmar. Por favor inténtelo nuevamente."
			return resultado
        }

		resultado.estatus = true
		resultado.mensaje = 'Constancia rechazada'
		resultado.datos = constancia

		return resultado
	}

    def rechazarConstanciaRevisor(params){
		def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

        def datosBitacora = [
            clase: "ConstanciaService",
            metodo: "rechazarConstanciaRevisor",
            nombre: "Rechazo de constancia - REVISOR",
            descripcion: "Rechazo de constancia - REVISOR",
            estatus: "ERROR"
        ]

		if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }

        def constancia = ConstanciaServicio.findByUuid(params.uuid)
        if(!constancia){
            resultado.mensaje = 'Constancia no encontrada'
            return resultado
        }
        if(!constancia.activo){
            resultado.mensaje = 'Constancia inactiva'
            return resultado
        }

        def constanciaEstatusId = constancia.estatusConstancia.id
        def estatusValidos = [EN_REVISION, RECHAZADO_AUTENTICADOR]

        if(!(constanciaEstatusId in estatusValidos)){
            resultado.mensaje = 'No se puede cambiar el estatus'
            return resultado
        }

        // La validación solo se realiza si la acción la hace un usuario REVISOR
		def roles = usuarioService.obtenerRoles()
		if(!(roles.contains('ROLE_REVISOR') || roles.contains('ROLE_REVISOR_PUBLICA'))){
            resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
		}

        def firmaDirectorEscuela = constancia.firmaDirectorEscuela

		constancia.estatusConstancia = EstatusConstancia.get(RECHAZADO_DGEMSS)
		constancia.comentarios = params.comentarios
        constancia.firmaDirectorEscuela = null

		if(!constancia.save(flush:true)){
			constancia.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			return resultado
		}

        // Se cambie el estatus del alumno, sus evaluaciones y sus ciclos para que se puedan editar nuevamente
        if(!cambiarEstatusRegistros(constancia, estatusRegistroService.EDITABLE)){
            transactionStatus.setRollbackOnly()
            resultado.mensaje = "Hubo un error al intentar rechazar la constancia. Por favor inténtelo nuevamente."
			return resultado
        }

        firmaDirectorEscuela.delete()

		resultado.estatus = true
		resultado.mensaje = 'Constancia modificado exitosamente'
		resultado.datos = constancia

		return resultado
	}

    def rechazarConstanciaDGEMSS(params){
		def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

        def datosBitacora = [
            clase: "ConstanciaService",
            metodo: "rechazarConstanciaDGEMSS",
            nombre: "Rechazo de constancia - DGEMSS",
            descripcion: "Rechazo de constancia - DGEMSS",
            estatus: "ERROR"
        ]

		if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }

        
        def constancia = ConstanciaServicio.findByUuid(params.uuid)
        if(!constancia){
            resultado.mensaje = 'Constancia no encontrada'
            return resultado
        }
        if(!constancia.activo){
            resultado.mensaje = 'Constancia inactiva'
            return resultado
        }

        def constanciaEstatusId = constancia.estatusConstancia.id
        def estatusValidos = [FIRMANDO_DGEMSS]

        if(!(constanciaEstatusId in estatusValidos)){
            resultado.mensaje = 'No se puede cambiar el estatus'
            return resultado
        }

        // La validación solo se realiza si la acción la hace un usuario AUTENTICADOR
		def roles = usuarioService.obtenerRoles()
		if(!(roles.contains('ROLE_AUTENTICADOR_DGEMSS'))){
            resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
		}

		constancia.estatusConstancia = EstatusConstancia.get(RECHAZADO_AUTENTICADOR)

		if(!constancia.save(flush:true)){
			constancia.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			return resultado
		}

		resultado.estatus = true
		resultado.mensaje = 'Constancia modificada exitosamente'
		resultado.datos = constancia

		return resultado
	}

    /**
     * Permite realizar la modificación de una constancia
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
            clase: "ConstanciaService",
            metodo: "modificarFotografia",
            nombre: "Modificación de fotografia",
            descripcion: "Se ha modificado su constancia",
            estatus: "ERROR"
        ]

		if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }

        def constancia = ConstanciaServicio.findByUuid(params.uuid)
        if(!constancia){
            resultado.mensaje = 'No se ha encontrado la constancia'
            return resultado
        }
        if(!constancia.activo){
            resultado.mensaje = 'Constancia inactiva'
            return resultado
        }

        if(!params.foto){
            resultado.mensaje = 'La foto es un dato requerido'
            return resultado
        }

        params.foto = efirmaService.base64toBytes(params.foto)
        constancia.opc = params.opc ? params.opc.toInteger():null
        constancia.foto = params.foto

        if(!constancia.save(flush:true)){
			constancia.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar(datosBitacora)
            return resultado
		}

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = 'La constancia se ha modificado'
		resultado.datos = constancia

        return resultado
	}

     /**
     * Permite eliminar un certificado
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
            clase: "ConstanciaService",
            metodo: "eliminar",
            nombre: "Eliminación de constancia",
            descripcion: "Se eliminó una constancia",
            estatus: "ERROR"
        ]

		if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }

        def constancia = ConstanciaServicio.findByUuid(params.uuid)
        if(!constancia){
            resultado.mensaje = 'Constancia no encontrada'
            return resultado
        }
        if(!constancia.activo){
            resultado.mensaje = 'Constancia inactiva'
            return resultado
        }

        if(constancia.estatusConstancia.id != GENERADO){
            resultado.mensaje = 'La constancia no puede ser eliminada'
            return resultado
        }

        // Se valida que la constancia pertenezca a la misma institución que el usuario
        def institucionId = constancia.alumno.planEstudios.carrera.institucion.id
        if(!usuarioService.perteneceAInstitucion(institucionId)){
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
			return resultado
		}

		constancia.activo = false

		if(!constancia.save(flush:true)){
			constancia.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar(datosBitacora)
			return resultado
		}

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = 'Constancia dada de baja exitosamente'
		resultado.datos = constancia

		return resultado
	}

    def enviarAFirmarEscuela(params){
		def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

        def datosBitacora = [
            clase: "ConstanciaService",
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
        def constancia = ConstanciaServicio.findByUuid(params.uuid)
        if(!constancia){
            resultado.mensaje = 'Constancia no encontrada'
            return resultado
        }
        if(!constancia.activo){
            resultado.mensaje = 'Constancia inactiva'
            return resultado
        }

        // Se valida que la constancia se encuentre en un estatus válido para la acción
        def constanciaEstatusId = constancia.estatusConstancia.id
        def estatusValidos = [GENERADO, RECHAZADO_DGEMSS, RECHAZADO_DIRECTOR]

        if(!(constanciaEstatusId in estatusValidos)){
            resultado.mensaje = 'No se puede cambiar el estatus'
            return resultado
        }

        // Se valida que la acción la realice un usuario de tipo Gestor de Escuela
		def roles = usuarioService.obtenerRoles()
		if(!roles.contains('ROLE_GESTOR_ESCUELA')){
            resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
		}

        // Se valida que la constancia pertenezca a la misma institución que el usuario
        def institucionId = constancia.alumno.planEstudios.carrera.institucion.id
        if(!usuarioService.perteneceAInstitucion(institucionId)){
            resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
        }

        // Se asigna el nuevo estatus
		constancia.estatusConstancia = EstatusConstancia.get(FIRMANDO_ESCUELA)
		
		if(!constancia.save(flush:true)){
            transactionStatus.setRollbackOnly()
            resultado.mensaje = errorService.obtenerErrores(constancia)[0]
			return resultado
		}

        // Se cambie el estatus del alumno, sus evaluaciones y sus ciclos para que no puedan se editados durante el proceso
        if(!cambiarEstatusRegistros(constancia, estatusRegistroService.NO_EDITABLE)){
            transactionStatus.setRollbackOnly()
            resultado.mensaje = "Hubo un error al intentar enviar el certificado a firmar. Por favor inténtelo nuevamente."
			return resultado
        }

        // Se guarda la acción en la bitácora
        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = 'La accion fue exitosa'
		resultado.datos = constancia

		return resultado
	}

    def cambiarEstatusRegistros(constancia, estatusRegistroId){
        def nuevoEstatusRegistro = EstatusRegistro.get(estatusRegistroId)

        def alumno = constancia.alumno

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

    def obtenerCadenaOriginalFirmaEscuela(parametros, fechaGeneracion){

        def fechaGeneracionConstancia = new SimpleDateFormat("YYYY-MM-dd'T'hh:mm:ss").format(fechaGeneracion)

        def cadenaOriginal = "||2.0"

        cadenaOriginal += "|5" // tipoConstancia
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

    def firmarConstancias(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def datosBitacora = [
            clase: "ConstanciaService",
            metodo: "firmarConstancias",
            nombre: "Firma de constancia - DGEMSS",
            descripcion: "Firma de constancia - DGEMSS",
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

        def estatusValidos = [FIRMANDO_DGEMSS]
        def listaConstancias = []

        def institucion = Institucion.get(params.id)

        listaConstancias = []

        try{

            def jsonSlurper = new JsonSlurper()
            def listaConstanciasJson = jsonSlurper.parseText(params.constancias)

            for(constanciaJson in listaConstanciasJson){
                def constancia = ConstanciaServicio.get(constanciaJson)
                if(!constancia){
                    resultado.mensaje = "No se encontró la constancia"
                    return resultado
                }

                // Se valida que la constancia se encuentre en un estatus válido para la acción
                def constanciaEstatusId = constancia.estatusConstancia.id
                if(!(constanciaEstatusId in estatusValidos)){
                    resultado.mensaje = 'La constancia no puede ser firmado'
                    return resultado
                }

                // Se agrega al arreglo
                listaConstancias << constancia

            }
        }catch(Exception ex){
            resultado.mensaje = 'Error en formato de instituciones'
            return resultado
        }

        for(constancia in listaConstancias){

            def zona = ZoneId.systemDefault()
            def fechaFirma = Date.from(LocalDate.now().atStartOfDay(zona).toInstant())
            def cadenaOriginal

            cadenaOriginal = obtenerCadenaOriginalFirmaDgemss(
                fechaFirma,
                constancia.firmaDirectorEscuela.selloDigital,
                firmaElectronica
            )

            def selloDigital = efirmaService.firmar(bytesClavePrivada, contrasena, cadenaOriginal)

            def firma = new Firma()
            firma.selloDigital = selloDigital
            firma.firmaElectronica = firmaElectronica
            firma.fechaFirma = fechaFirma

            if(!firma.save(flush:true)){
                transactionStatus.setRollbackOnly()
                firma.errors.allErrors.each {
                    resultado.mensaje = messageSource.getMessage(it, null)
                }
                return resultado
            }

            constancia.firmaAutenticadorDgemss = firma
            constancia.estatusConstancia = EstatusConstancia.get(FINALIZADO)

            if(!constancia.save(flush:true)){
                transactionStatus.setRollbackOnly()
                constancia.errors.allErrors.each {
                    resultado.mensaje = messageSource.getMessage(it, null)
                }
                return resultado
            }

            // Se cambie el estatus del alumno, sus evaluaciones y sus ciclos para que no puedan se modificados más
            if(!cambiarEstatusRegistros(constancia, estatusRegistroService.BLOQUEADO)){
                transactionStatus.setRollbackOnly()
                resultado.mensaje = "Hubo un error al intentar enviar el certificado a firmar. Por favor inténtelo nuevamente."
                return resultado
            }

            // Se guarda la acción en la bitácora
            datosBitacora.estatus = "EXITOSO"
            bitacoraService.registrar(datosBitacora)
        }


        resultado.estatus = true
        resultado.mensaje = "Constancias firmadas exitosamente"
        return resultado
    }

    def listarConstanciasAFirmarPorInstitucion(params){

        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def criteria = {
            createAlias("estatusConstancia", "e", CriteriaSpecification.LEFT_JOIN)
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

        def constancias = ConstanciaServicio.createCriteria().list(criteria)

        resultado.estatus = true
        resultado.mensaje = "Constancias consultadas exitosamente"
        resultado.datos = constancias

        return resultado
    }
    def firmarConstancia(params){
        def roles = usuarioService.obtenerRoles()

        if(roles.contains('ROLE_DIRECTOR_ESCUELA')){
            return firmarConstanciaEscuela(params)
        }
        
        if(roles.contains('ROLE_AUTENTICADOR_DGEMSS')){
            return firmarConstanciaDGEMSS(params)
        }
    }
    def firmarConstanciaEscuela(params){
		def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

        def datosBitacora = [
            clase: "ConstanciaService",
            metodo: "firmarCosntanciaEscuela",
            nombre: "Firma de constancia - Escuela",
            descripcion: "Firma de constancia - Escuela",
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

        def constancia = ConstanciaServicio.findByUuid(params.uuid)
        if(!constancia){
            resultado.mensaje = 'Constancia no encontrada'
            return resultado
        }
        if(!constancia.activo){
            resultado.mensaje = 'Constancia inactiva'
            return resultado
        }

        // Se valida que el certificado se encuentre en un estatus válido para la acción
        def constanciaEstatusId = constancia.estatusConstancia.id
        def estatusValidos = [FIRMANDO_ESCUELA]

        if(!(constanciaEstatusId in estatusValidos)){
            resultado.mensaje = 'La constancia no puede ser firmada'
            return resultado
        }

        def roles = usuarioService.obtenerRoles()

        def zona = ZoneId.systemDefault()
        def fechaFirma = Date.from(LocalDate.now().atStartOfDay(zona).toInstant())
        def parametros
        def cadenaOriginal

        parametros = obtenerParametrosConstancia(constancia)
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

        constancia.firmaDirectorEscuela = firma
        // Validar estatus de procedencia con el tramite id
        if(constancia.alumno.planEstudios.carrera.institucion.publica){
            constancia.estatusConstancia = EstatusConstancia.get(EN_REVISION)  
        }else if(constancia.tramite != null){
            constancia.estatusConstancia = EstatusConstancia.get(EN_REVISION)
        }else{
            constancia.estatusConstancia = EstatusConstancia.get(EN_ESPERA)
        }

        if(!constancia.save(flush:true)){
			constancia.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			return resultado
		}

        // Se guarda la acción en la bitácora
        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

        resultado.estatus = true
        resultado.mensaje = "Constancia firmada exitosamente"
        return resultado
    }
    
    def obtenerParametrosConstancia(constancia){
        def alumno = constancia.alumno
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
            //constancia
        parametros.foto = new ByteArrayInputStream(constancia.foto)
        parametros.uuid = constancia.uuid
        parametros.folioControl = constancia.folioControl
        parametros.opc = constancia.opc
        parametros.numPaginas = 1

        if(constancia.estatusConstancia.id == FINALIZADO){
            //datos firmante escuela
            def datosCerDirector = constancia.firmaDirectorEscuela.firmaElectronica

            parametros.firmanteEscuelaSello = constancia.firmaDirectorEscuela.selloDigital
            parametros.firmanteEscuelaCer = efirmaService.bytesToBase64(datosCerDirector.archivoCer)
            parametros.firmanteEscuelaNoSerie = datosCerDirector.numeroSerieCer.trim().toUpperCase()
            parametros.firmanteEscuelaCurp = datosCerDirector.curpCer.trim().toUpperCase()
            parametros.firmanteEscuelaNombre = datosCerDirector.persona.nombre.trim().toUpperCase()
            parametros.firmanteEscuelaPrimerApellido = datosCerDirector.persona.primerApellido.trim().toUpperCase()
            parametros.firmanteEscuelaSegundoApellido = datosCerDirector.persona.segundoApellido.trim().toUpperCase()
            parametros.firmanteEscuelaNombreCompleto = datosCerDirector.nombreCer.trim().toUpperCase()
            parametros.firmanteEscuelaTitulo = datosCerDirector.persona.titulo.trim().toUpperCase()
            parametros.firmanteEscuelaFecha = formatoFechaXml.format(constancia.firmaDirectorEscuela.fechaFirma)
            parametros.firmanteEscuelaCargo = datosCerDirector.persona.usuario.cargo.trim().toUpperCase()

            //datos firmante dgmess
            def datosCerAutenticador = constancia.firmaAutenticadorDgemss.firmaElectronica

            parametros.firmanteDgemssSello = constancia.firmaAutenticadorDgemss.selloDigital
            parametros.firmanteDgemssNoSerie = datosCerAutenticador.numeroSerieCer.trim().toUpperCase()
            parametros.firmanteDgemssCurp = datosCerAutenticador.curpCer.trim().toUpperCase()
            parametros.firmanteDgemssNombreCompleto = datosCerAutenticador.nombreCer.trim().toUpperCase()
            parametros.firmanteDgemssTitulo = datosCerAutenticador.persona.titulo.trim().toUpperCase()
            parametros.firmanteDgemssFecha = formatoFechaPdf.format(constancia.firmaAutenticadorDgemss.fechaFirma)
            parametros.firmanteDgemssFechaCadena = formatoFechaXml.format(constancia.firmaAutenticadorDgemss.fechaFirma)
            parametros.firmanteDgemssCargo = datosCerAutenticador.persona.usuario.cargo.trim().toUpperCase()

            //Generar código QR
            def url = "constanciaservicio/consultarConstancia?uuid=${constancia.uuid}"
            def resultadoQr = zxingService.generarQr(url)
            if (resultadoQr.estatus) {
                parametros.qr = new ByteArrayInputStream(resultadoQr.qr)
            }
        }

        return parametros
    }

     /**
     * Permite realizar el registro de certificados
     * @param libro (requerido)
     * @param foja (requerido)
     * @param numero (opcional)
     * @return resultado
     * resultado con el estatus, mensaje y datos del certificado
     */
    def enviarAFirmarDGEMSS(params) {
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def datosBitacora = [
            clase: "ConstanciaService",
            metodo: "enviarAFirmarDGEMSS",
            nombre: "Envío para firma de la DGEMSS",
            descripcion: "Envío para firma de la DGEMSS",
            estatus: "ERROR"
        ]

        // Validación de parámetros recibidos
        if(!params.uuid){
            resultado.mensaje = 'El uuid de la constancia es un dato requerido'
            return resultado
        }
        def constancia = ConstanciaServicio.findByUuid(params.uuid)
        if(!constancia){
            resultado.mensaje = 'Constancia no encontrada'
            return resultado
        }
        if(!constancia.activo){
            resultado.mensaje = 'Constancia inactiva'
            return resultado
        }

        constancia.estatusConstancia = EstatusConstancia.get(FIRMANDO_DGEMSS)

        if(!constancia.save(flush:true)){
			constancia.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar(datosBitacora)
            return resultado
		}

        // Se guarda la acción en la bitácora
        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = 'Constancia creada exitosamente'
		resultado.datos = constancia

        return resultado
    }
    def firmarConstanciaDGEMSS(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

        def datosBitacora = [
            clase: "ConstanciaService",
            metodo: "firmarConstanciaDGEMSS",
            nombre: "Firma de la constancia - DGEMSS",
            descripcion: "Firma de constancia - DGEMSS",
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
        
        def constancia = ConstanciaServicio.findByUuid(params.uuid)
        if(!constancia){
            resultado.mensaje = 'Constancia no encontrada'
            return resultado
        }
        if(!constancia.activo){
            resultado.mensaje = 'Constancia inactiva'
            return resultado
        }

        // Se valida que la constancia se encuentre en un estatus válido para la acción
        def constanciaEstatusId = constancia.estatusConstancia.id
        def estatusValidos = [FIRMANDO_DGEMSS]

        if(!(constanciaEstatusId in estatusValidos)){
            resultado.mensaje = 'La constancia no puede ser firmada'
            return resultado
        }

        def roles = usuarioService.obtenerRoles()
        
        def zona = ZoneId.systemDefault()
        def fechaFirma = Date.from(LocalDate.now().atStartOfDay(zona).toInstant())
        def cadenaOriginal

        cadenaOriginal = obtenerCadenaOriginalFirmaDgemss(
            fechaFirma, 
            constancia.firmaDirectorEscuela.selloDigital,
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
        
        constancia.firmaAutenticadorDgemss = firma
        constancia.estatusConstancia = EstatusConstancia.get(FINALIZADO)

        if(!constancia.save(flush:true)){
			constancia.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			return resultado
		}

        // Se cambie el estatus del alumno, sus evaluaciones y sus ciclos para que no puedan se modificados más
        if(!cambiarEstatusRegistros(constancia, estatusRegistroService.BLOQUEADO)){
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

        cadenaOriginal += "|5" // tipoConstancia
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
        cadenaOriginal += "|${parametros.tipoConstancia}"
        cadenaOriginal += "|${parametros.firmanteEscuelaFecha}"
        
        
        
        cadenaOriginal += "||"

        return cadenaOriginal
    }

    def generarDocumentosConstancia(constanciaUuid){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

        def constancia = ConstanciaServicio.findByUuid(constanciaUuid)
        def parametrosConstancia = []
        def cadenaOriginal

        parametrosConstancia = obtenerParametrosConstancia(constancia)
        cadenaOriginal = obtenerCadenaOriginalPdf(parametrosConstancia)

        parametrosConstancia.cadenaOriginal = cadenaOriginal
        // Se genera el archivo pdf
        def nombrePlantilla 
        if(parametrosConstancia.opc == 1){
            nombrePlantilla="constancia"
        }else  if(parametrosConstancia.opc == 2){
            nombrePlantilla="constancia_mayor"
        }else  if(parametrosConstancia.opc == 3){
            nombrePlantilla="constancia_inst"
        }

        def resultadoExpedicionPdf = documentoService.generar(nombrePlantilla, parametrosConstancia)
        if (!resultadoExpedicionPdf.estatus) {
            resultado.mensaje = resultadoExpedicionPdf.mensaje
            return resultado
        }

        // Se genera el archivo xml
        def resultadoExpedicionXml = xmlService.generar(parametrosConstancia)
        if (!resultadoExpedicionXml.estatus) {
            resultado.mensaje = resultadoExpedicionXml.mensaje
            return resultado
        }
        

        constancia.pdf = resultadoExpedicionPdf.documento
        constancia.xml = resultadoExpedicionXml.documento

		if(!constancia.save(flush:true)){
			constancia.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			return resultado
		}

		resultado.estatus = true
		resultado.mensaje = 'Documentos generados exitosamente'
		resultado.datos = constancia

		return resultado
    }
}
