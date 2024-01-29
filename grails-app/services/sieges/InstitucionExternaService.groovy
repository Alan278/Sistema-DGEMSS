package sieges

import grails.gorm.transactions.Transactional

/**
 * Servicio que permite la administración de instituciones externas
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */
@Transactional
class InstitucionExternaService {
	/**
      * Inyección de messageSource que contiene mensajes de validaciones para los atributos de cada dominio
      * Los mensajes de validación se encuentran en: grails-app/i18n/messages_es.properties
      */
	def messageSource
	/**
	 * Inyección de CarreraExternaService que contiene la lógica de administración de carreras externas
	 */
	def carreraExternaService
	/**
	 * Inyección de BitacoraService que contiene la lógica de administración de la bitacora
	 */
	def bitacoraService

	/**
	 * Valida y registra una nueva institución externa en la base de datos
	 * @param nombre (Requerido)
	 * @param nombreComercial (Opcional)
	 * @param razonSocial (Opcional)
	 * @param claveCt (Opcional)
	 * @param claveDgp (Opcional)
	 * @param telefono (Opcional)
	 * @param correoElectronico (Opcional)
	 * @return Map con el estatus, mensaje y datos de la consulta
	 */
	def registrar(params) {
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		def institucion = new Institucion(params)
		institucion.externa = true

		if(institucion.save(flush:true)){
			resultado.estatus = true
			bitacoraService.registrar([clase:"InstitucionExternaService", metodo:"registrar", nombre:"Registro de la Institución externa", descripcion:"Se registra la institución externa", estatus:"EXITOSO"])
		}else{
			// Se recorren los errores de validación y se asignan a la propiedad resultado.mensaje
			institucion.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar([clase:"InstitucionExternaService", metodo:"registrar", nombre:"Registro de la Institución externa", descripcion:"Se registra la institución externa", estatus:"ERROR"])
		}

		if(resultado.estatus){
	       	resultado.mensaje = 'Institución creada exitosamente'
	       	resultado.datos = institucion
		}

		return resultado
	}

	/**
	 * Obtiene las instituciones externas activas con parametros de paginación y filtrado
	 * @param search (Opcional)
	 * Nombre de la institución por el cual se desea realizar una busqueda
	 * @return Map con el estatus, mensaje y datos de la consulta
	 */
	def listar(params){
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: [
				instituciones: null,
				numeroCarreras: [],
			]
		]

		// Parametros de paginación
		if(!params.max) params.max = 3
		if(!params.offset) params.offset = 0
		if(!params.sort) params.sort = 'id'
		if(!params.order) params.order = 'asc'

		def criteria = {
			and{
 				eq("activo", true)
 				eq("externa", true)
				if(params.search){
					ilike("nombre", "%${params.search}%")
				}
			}
		}

		def instituciones = Institucion.createCriteria().list(params,criteria)

		if(instituciones.totalCount <= 0){
			resultado.mensaje = 'No se encontraron instituciones'
			resultado.datos.instituciones = instituciones
			return resultado
		}

		resultado.estatus = true
		resultado.mensaje = 'Instituciones consultadas exitosamente'
		resultado.datos.instituciones = instituciones

		// Se cuentan las carreras externas activas con las que cuenta cada insttución
		instituciones.each{ institucion ->
			params.institucionId = institucion.id.toString()
			// Se asigna una bandera notCount para que en el método listar de carreraExternaService
			// no cuente los planes y asi no se consuman recursos inecesarios
			params.notCount = true
			def nCarreras = carreraExternaService.listar(params).datos.carreras.totalCount
			resultado.datos.numeroCarreras << nCarreras
		}

		return resultado
	}

	/**
	 * Obtiene las instituciones externas activas
	 * @return Map con el estatus, mensaje y datos de la consulta
	 */
	def obtenerActivos(params){
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		def instituciones = Institucion.createCriteria().list {
			and{
				eq("activo", true)
				eq("externa", true)
			}
		}

		resultado.estatus = true
		resultado.mensaje = 'Instituciones consultadas exitosamente'
		resultado.datos = instituciones

		return resultado
	}

	/**
	 * Obtiene una institución externa específica
	 * @param id (Requerido)
	 * Identificador de la institución
	 * @return Map con el estatus, mensaje y datos de la consulta
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

		def institucion = Institucion.get(params.id)

		if(!institucion){
			resultado.mensaje = 'Institución no encontrada'
			return resultado
		}

		if(!institucion.activo){
			resultado.mensaje = 'Institución inactiva'
			return resultado
		}

		if(!institucion.externa){
			resultado.mensaje = 'Institución no externa'
			return resultado
		}

		resultado.estatus = true
		resultado.mensaje = 'Institución encontrada exitosamente'
		resultado.datos = institucion

		return resultado
	}

	/**
	 * Modifica los datos de una institución externa específica
	 * @param id (Requerido)
	 * Identificador de la institución
	 * @param nombre (Requerido)
	 * @param nombreComercial (Opcional)
	 * @param razonSocial (Opcional)
	 * @param claveCt (Opcional)
	 * @param claveDgp (Opcional)
	 * @param telefono (Opcional)
	 * @param correoElectronico (Opcional)
	 * @return Map con el estatus, mensaje y datos de la consulta
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

		def institucion = Institucion.get(params.id)

		if(!institucion){
			resultado.mensaje = 'Institución no encontrada'
			return resultado
		}

		if(!institucion.activo){
			resultado.mensaje = 'Institución inactiva'
			return resultado
		}

		if(!institucion.externa){
			resultado.mensaje = 'Institución no externa'
			return resultado
		}

		// Se crean copias auxiliares de los objetos antes de asignar
		// los nuevos datos para verificar si se realizó algun cambio
		def institucionAux = new Institucion(institucion.properties)

		// Se asignan los nuevos datos
		institucion.properties = params

		// En caso de cambios se modifica el campo de ultimaActualizacion
		if(!institucion.equals(institucionAux))
			institucion.ultimaActualizacion = new Date()

		if(institucion.save(flush:true)){
			resultado.estatus = true
			bitacoraService.registrar([clase:"InstitucionExternaService", metodo:"modificar", nombre:"Modificación de la Institución externa", descripcion:"Se modifica la institución externa", estatus:"EXITOSO"])
		}else{
			// Se recorren los errores de validación y se asignan a la propiedad resultado.mensaje
			institucion.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar([clase:"InstitucionExternaService", metodo:"modificar", nombre:"Modificación de la Institución externa", descripcion:"Se modifica la institución externa", estatus:"ERROR"])
		}

		if(resultado.estatus){
	       	resultado.mensaje = 'Institución modificada exitosamente'
	       	resultado.datos = institucion
		}

		return resultado
	}

	/**
	 * Realiza una baja lógica de una institución externa específica
	 * @param id (Requerido)
	 * Identificador de la institución
	 * @return Map con el estatus, mensaje y datos de la consulta
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

		def institucion = Institucion.get(params.id)

		if(!institucion){
			resultado.mensaje = 'Institución no encontrada'
			return resultado
		}

		if(!institucion.externa){
			resultado.mensaje = 'Institución no externa'
			return resultado
		}

		institucion.activo = false

		if(!institucion.save(flush:true)){
			// Se recorren los errores de validación y se asignan a la propiedad resultado.mensaje
			institucion.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar([clase:"InstitucionExternaService", metodo:"eliminar", nombre:"Eliminación de la Institución externa", descripcion:"Se elimina la institución externa", estatus:"ERROR"])
			return resultado
		}
		bitacoraService.registrar([clase:"InstitucionExternaService", metodo:"eliminar", nombre:"Eliminación de la Institución externa", descripcion:"Se elimina la institución externa", estatus:"EXITOSO"])

		resultado.estatus = true
       	resultado.mensaje = 'Institución dada de baja exitosamente'
       	resultado.datos = institucion

		return resultado
	}

}
