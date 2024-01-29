package sieges

import grails.gorm.transactions.Transactional
import org.hibernate.criterion.CriteriaSpecification

/**
 * Servicio que permite la administración de personal institucional
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */
@Transactional
class PersonalInstitucionalService {
	/**
      * Inyección de messageSource que contiene mensajes de validaciones para los atributos de cada dominio
      * Los mensajes de validación se encuentran en: grails-app/i18n/messages_es.properties
      */
	def messageSource
	/**
	 * Inyección de BitacoraService que contiene la lógica de administración de la bitacora
	 */
	def bitacoraService
	def usuarioService

	/**
      * Registra un nuevo personal
      * @param institucionId (Requerido)
      * @param cargoId (Requerido)
      * @param nombreCargo (Requerido)
      * @param nombre (Requerido)
      * @param primerApellido (Requerido)
      * @param segundoApellido (Opcional)
      * @param curp (Requerido)
      * @param rfc (Opcional)
      * @param telefono (Requerido)
      * @param correoElectronico (Requerido)
      */
	def registrar(params) {
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		// Se validan las relaciones con otras tablas
		def institucion = Institucion.get(params.institucionId)
		if(!institucion){
		    resultado.mensaje = 'Institución no encontrada'
		    return resultado
		}
		if(!institucion.activo){
		    resultado.mensaje = 'Institución inactiva'
		    return resultado
		}
		def roles = usuarioService.obtenerRoles()
		if(usuarioService.esSupervisorPublico()){
			if(!institucion.publica){
				resultado.mensaje = 'Institución privada'
				return resultado
			}
		}else if(institucion.publica){
			resultado.mensaje = 'Institución pública'
			return resultado
		}

		def cargoInstitucional = CargoInstitucional.get(params.cargoId)
		if(!cargoInstitucional){
		    resultado.mensaje = 'Cargo Institucional no encontrado'
		    return resultado
		}
		if(!cargoInstitucional.activo){
		    resultado.mensaje = 'Cargo Institucional inactivo'
		    return resultado
		}

		//Se verifica si el curp ya se encuentra registrado
		def persona = null
		if(params.curp){
			def personas = Persona.createCriteria().list {
				ilike("curp", "%${params.curp}%")
			}
			//En dicho caso se ocupa la persona con esa curp
			personas.each{personaAux ->
				persona = personaAux
			}
		}
		//En caso contrario se crea una nueva persona con los datos recibidos
		if(persona == null)	persona = new Persona(params)

		def personalInstitucional = new PersonalInstitucional(params)
		personalInstitucional.institucion = institucion
		personalInstitucional.cargoInstitucional = cargoInstitucional

		// Se inicia una transacción para eliminar la persona
		// en caso de ocurrir algun problema con el guardado del personalInstitucional
		Persona.withTransaction {statusPersona ->
			if(persona.save(flush:true)){
				personalInstitucional.persona = persona
				if(personalInstitucional.save(flush:true)){
					resultado.estatus = true
					bitacoraService.registrar([clase:"PersonalInstitucionalService", metodo:"registrar", nombre:"Registro del personal institucional", descripcion:"Se registra el personal institucional", estatus:"EXITOSO"])
				}else{
					// Se recorren los errores de validación y se asignan a la propiedad resultado.mensaje
					statusPersona.setRollbackOnly()
					personalInstitucional.errors.allErrors.each {
						resultado.mensaje = messageSource.getMessage(it, null)
					}
					bitacoraService.registrar([clase:"PersonalInstitucionalService", metodo:"registrar", nombre:"Registro del personal institucional", descripcion:"Se registra el personal institucional", estatus:"ERROR"])
				}
			}else{
				persona.errors.allErrors.each {
					resultado.mensaje = messageSource.getMessage(it, null)
				}
				bitacoraService.registrar([clase:"PersonalInstitucionalService", metodo:"registrar", nombre:"Registro de la persona", descripcion:"Se registra la persona", estatus:"ERROR"])
			}
		}

		if(resultado.estatus){
			resultado.mensaje = 'Personal Institucional creado exitosamente'
			resultado.datos = personalInstitucional
		}

		return resultado
	}

	/**
	 * Obtiene el personal activo con parametros de paginación y filtrado
	 * @param institucionId (Opcional)
	 * Identificador de la institución
	 * @param search (Opcional)
	 * Nombre del personal
	 */
	def listar(params){
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: [
				personalInstitucional: null
			]
		]

		// Parametros de paginación
		if(!params.max) params.max = 3
		if(!params.offset) params.offset = 0
		if(!params.sort) params.sort = 'id'
		if(!params.order) params.order = 'asc'

		// Parametros de filtrado (opcionales)
		def institucionId
		if(params.institucionId){
		   institucionId = Integer.parseInt(params.institucionId)
		}

		def roles = usuarioService.obtenerRoles()

		def criteria = {
			createAlias("persona", "p", CriteriaSpecification.LEFT_JOIN)
			createAlias("institucion", "i", CriteriaSpecification.LEFT_JOIN)
			and{
				eq("activo", true)
				if(usuarioService.esSupervisorPublico()){
 					eq("i.publica", true)
				}else{
					eq("i.publica", false)
				}
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
		}

		def personalInstitucional = PersonalInstitucional.createCriteria().list(params,criteria)

		if(personalInstitucional.totalCount <= 0){
			resultado.mensaje = 'No se encontró personal institucional'
			resultado.datos.personalInstitucional = personalInstitucional
			return resultado
		}

		resultado.estatus = true
		resultado.mensaje = 'Personal institucional consultado exitosamente'
		resultado.datos.personalInstitucional = personalInstitucional

		return resultado
	}

	/**
	 * Obtiene el personal activo
	 */
	def obtenerActivos(params){
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		def roles = usuarioService.obtenerRoles()
		
		def personalInstitucional = PersonalInstitucional.createCriteria().list {
			eq("activo", true)
			if(usuarioService.esSupervisorPublico()){
				eq("i.publica", true)
			}else{
				eq("i.publica", false)
			}
		}

		resultado.estatus = true
		resultado.mensaje = 'Personal Institucional consultado exitosamente'
		resultado.datos = personalInstitucional

		return resultado
	}

	/**
	 * Obtiene un personal específico
	 * @param id (Requerido)
	 * Identificador del personal
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

		def personalInstitucional = PersonalInstitucional.get(params.id)

		if(!personalInstitucional){
			resultado.mensaje = 'Personal Institucional no encontrado'
			return resultado
		}

		if(!personalInstitucional.activo){
			resultado.mensaje = 'Personal Institucional inactivo'
			return resultado
		}

		def roles = usuarioService.obtenerRoles()
		if(usuarioService.esSupervisorPublico()){
			if(!personalInstitucional.institucion.publica){
				resultado.mensaje = 'El personal pertenece a una institución privada'
				return resultado
			}
		}else if(personalInstitucional.institucion.publica){
			resultado.mensaje = 'El personal pertenece a una institución pública'
			return resultado
		}

		resultado.estatus = true
		resultado.mensaje = 'Personal Institucional encontrado exitosamente'
		resultado.datos = personalInstitucional

		return resultado
	}

	/**
	 * Modifica los datos de un personal específico
	 * @param id (Requerido)
	 * Identificador del personal
	 * @param institucionId (Requerido)
	 * @param cargoId (Requerido)
	 * @param nombreCargo (Requerido)
	 * @param nombre (Requerido)
	 * @param primerApellido (Requerido)
	 * @param segundoApellido (Opcional)
	 * @param curp (Requerido)
	 * @param rfc (Opcional)
	 * @param telefono (Requerido)
	 * @param correoElectronico (Requerido)
	 */
	def modificar(params) {
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		if(!params.id){
			resultado.mensaje = 'El id es un dato requerido'
			return resultado
		}

		def personalInstitucional = PersonalInstitucional.get(params.id)
		if(!personalInstitucional){
			resultado.mensaje = 'Personal Institucional no encontrado'
			return resultado
		}
		if(!personalInstitucional.activo){
			resultado.mensaje = 'Personal Institucional inactivo'
			return resultado
		}
		def roles = usuarioService.obtenerRoles()
		if(usuarioService.esSupervisorPublico()){
			if(!personalInstitucional.institucion.publica){
				resultado.mensaje = 'El personal Institucional pertenece a una institución privada'
				return resultado
			}
		}else if(personalInstitucional.institucion.publica){
			resultado.mensaje = 'El personal Institucional pertenece a una institución pública'
			return resultado
		}

		// Se validan las relaciones con otras tablas
		def institucion = Institucion.get(params.institucionId)
		if(!institucion){
			resultado.mensaje = 'institucion no encontrada'
			return resultado
		}
		if(!institucion.activo){
			resultado.mensaje = 'Institucion inactiva'
			return resultado
		}
		if(usuarioService.esSupervisorPublico()){
			if(!institucion.publica){
				resultado.mensaje = 'Institución privada'
				return resultado
			}
		}else if(institucion.publica){
			resultado.mensaje = 'Institución pública'
			return resultado
		}

		def cargoInstitucional = CargoInstitucional.get(params.cargoId)
		if(!cargoInstitucional){
			resultado.mensaje = 'Cargo Institucional no encontrado'
			return resultado
		}
		if(!cargoInstitucional.activo){
			resultado.mensaje = 'Cargo Institucional inactivo'
			return resultado
		}

		// Se crean copias auxiliares de los objetos antes de asignar
        // los nuevos datos para verificar si se realizó algun cambio
		def personalInstitucionalAux = new PersonalInstitucional(personalInstitucional.properties)
		personalInstitucionalAux.institucion = personalInstitucional.institucion
		personalInstitucionalAux.cargoInstitucional = personalInstitucional.cargoInstitucional
		def personaAux = new Persona(personalInstitucional.persona.properties)

		// Se asignan los nuevos datos
		personalInstitucional.properties = params
		personalInstitucional.persona.properties = params
		personalInstitucional.institucion = institucion
		personalInstitucional.cargoInstitucional = cargoInstitucional

		// En caso de cambios se modifica el campo de ultimaActualizacion
		if(!personalInstitucional.equals(personalInstitucionalAux))
			personalInstitucional.ultimaActualizacion = new Date()
		if(!personalInstitucional.persona.equals(personaAux))
			personalInstitucional.persona.ultimaActualizacion = new Date()

		// Se inicia una transacción para deshacer los cambios en la persona
		// en caso de ocurrir algun problema con el guardado del personal
		Persona.withTransaction {statusPersona ->
			if(personalInstitucional.persona.save(flush:true)){
				if(personalInstitucional.save(flush:true)){
					resultado.estatus = true
					bitacoraService.registrar([clase:"PersonalInstitucionalService", metodo:"modificar", nombre:"Moficiación del personal institucional", descripcion:"Se modifica el personal institucional", estatus:"EXITOSO"])
				}else{
					statusPersona.setRollbackOnly()
					personalInstitucional.errors.allErrors.each {
						resultado.mensaje = messageSource.getMessage(it, null)
					}
					bitacoraService.registrar([clase:"PersonalInstitucionalService", metodo:"modificar", nombre:"Modificación del personal institucional", descripcion:"Se modifica el personal institucional", estatus:"ERROR"])
				}
			}else{
				// Se recorren los errores de validación y se asignan a la propiedad resultado.mensaje
				personalInstitucional.persona.errors.allErrors.each {
					resultado.mensaje = messageSource.getMessage(it, null)
				}
				bitacoraService.registrar([clase:"PersonalInstitucionalService", metodo:"modificar", nombre:"Modificación de la persona", descripcion:"Se modifica la persona", estatus:"ERROR"])
			}
		}

		if(resultado.estatus){
			resultado.mensaje = 'Personal Institucional modificado exitosamente'
			resultado.datos = personalInstitucional
		}

		return resultado
	}

	/**
	 * Realiza una baja lógica de un personal específico
	 * @param id (Requerido)
	 * Identificador del personal
	 */
	def eliminar(params) {
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		if(!params.id){
			resultado.mensaje = 'El id es un dato requerido'
			return resultado
		}

		def personalInstitucional = PersonalInstitucional.get(params.id)

		if(!personalInstitucional){
			resultado.mensaje = 'Personal Institucional no encontrado'
			return resultado
		}
		def roles = usuarioService.obtenerRoles()
		if(usuarioService.esSupervisorPublico()){
			if(!personalInstitucional.institucion.publica){
				resultado.mensaje = 'Institución privada'
				return resultado
			}
		}else if(personalInstitucional.institucion.publica){
			resultado.mensaje = 'Institución pública'
			return resultado
		}

		personalInstitucional.activo = false

		if(!personalInstitucional.save(flush:true)){
			// Se recorren los errores de validación y se asignan a la propiedad resultado.mensaje
			personalInstitucional.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar([clase:"PersonalInstitucionalService", metodo:"eliminar", nombre:"Eliminación del personal institucional", descripcion:"Se elimina el personal institucional", estatus:"ERROR"])
			return resultado
		}
		bitacoraService.registrar([clase:"PersonalInstitucionalService", metodo:"eliminar", nombre:"Eliminación del personal institucional", descripcion:"Se elimina el personal institucional", estatus:"EXITOSO"])

		resultado.estatus = true
		resultado.mensaje = 'Personal Institucional dado de baja exitosamente'
		resultado.datos = personalInstitucional

		return resultado
	}

}
