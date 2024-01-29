package sieges

import grails.gorm.transactions.Transactional

@Transactional
class TitulacionService {

     /**
	 * Inyección de messageSource que contiene mensajes de validaciones para los atributos de cada dominio
	 * Los mensajes de validación se encuentran en: grails-app/i18n/messages_es.properties
	 */
	def messageSource

	/**
	 * Obtiene las estatus activas con parametros de paginación
	 * @param search (Opcional)
	 * Nombre del estatus
	 */
	def listar(params){
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		// Parametros de paginación
		if(!params.max) params.max = 3
		if(!params.offset) params.offset = 0
		if(!params.sort) params.sort = 'id'
		if(!params.order) params.order = 'asc'

		def criteria = {
			and{
				eq("activo", true)
				if(params.search){
					ilike("nombre", "%${params.search}%")
				}
			}
		}

		def titulacion = OpcTitulacion.createCriteria().list(params,criteria)

		if(titulacion.totalCount <= 0){
			resultado.mensaje = 'No se encontraron titulacion'
			resultado.datos = titulacion
			return resultado
		}
		resultado.titulacion = true
		resultado.mensaje = 'titulacion consultados exitosamente'
		resultado.datos = titulacion

		return resultado
	}

	/**
	 * Obtiene las estatus activas
	 */
	def obtenerActivos(params){
		def resultado = [
				estatus: false,
				mensaje: '',
				datos: null
		]

		def titulacion = OpcTitulacion.createCriteria().list {
			eq("activo", true)
		}

		resultado.titulacion = true
		resultado.mensaje = 'Estatus consultados exitosamente'
		resultado.datos = titulacion

		return resultado
	}

	/**
	 * Obtiene un estatus específica
	 * @param id (Requerido)
	 * Identificador del estatus
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

		def titulacion = OpcTitulacion.get(params.id)

		if(!titulacion){
			resultado.mensaje = 'Estatus no encontrado'
			return resultado
		}

		if(!titulacion.activo){
			resultado.mensaje = 'Estatus inactivo'
			return resultado
		}

		resultado.titulacion = true
		resultado.mensaje = 'Estatus encontrado exitosamente'
		resultado.datos = titulacion

		return resultado
	}
}
