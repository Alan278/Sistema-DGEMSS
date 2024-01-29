package sieges

import grails.gorm.transactions.Transactional

/**
 * @author Alan Guevarin
 * @since 2022
 */

@Transactional
class EstatusNotificacionService {

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

		def estatus = EstatusNotificacion.createCriteria().list(params,criteria)

		if(estatus.totalCount <= 0){
			resultado.mensaje = 'No se encontraron estatus'
			resultado.datos = estatus
			return resultado
		}
		resultado.estatus = true
		resultado.mensaje = 'Estatus consultados exitosamente'
		resultado.datos = estatus

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

		def estatus = EstatusNotificacion.createCriteria().list {
			eq("activo", true)
		}

		resultado.estatus = true
		resultado.mensaje = 'Estatus consultados exitosamente'
		resultado.datos = estatus

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

		def estatus = EstatusNotificacion.get(params.id)

		if(!estatus){
			resultado.mensaje = 'Estatus no encontrado'
			return resultado
		}

		if(!estatus.activo){
			resultado.mensaje = 'Estatus inactivo'
			return resultado
		}

		resultado.estatus = true
		resultado.mensaje = 'Estatus encontrado exitosamente'
		resultado.datos = estatus

		return resultado
	}
}