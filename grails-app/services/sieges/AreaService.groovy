package sieges

import grails.gorm.transactions.Transactional

/**
 * Servicio que permite la administración de areas
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */
@Transactional
class AreaService {
	/**
	 * Inyección de messageSource que contiene mensajes de validaciones para los atributos de cada dominio
	 * Los mensajes de validación se encuentran en: grails-app/i18n/messages_es.properties
	 */
	def messageSource

	/**
	 * Obtiene las areas activas con parametros de paginación
	 * @param search (Opcional)
	 * Nombre del area
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

		def areas = Area.createCriteria().list(params,criteria)

		if(areas.totalCount <= 0){
			resultado.mensaje = 'No se encontraron areas'
			resultado.datos = areas
			return resultado
		}
		resultado.estatus = true
		resultado.mensaje = 'Areas consultadas exitosamente'
		resultado.datos = areas

		return resultado
	}

	/**
	 * Obtiene las areas activas
	 */
	def obtenerActivos(params){
		def resultado = [
				estatus: false,
				mensaje: '',
				datos: null
		]

		def areas = Area.createCriteria().list {
			eq("activo", true)
		}

		resultado.estatus = true
		resultado.mensaje = 'Areas consultadas exitosamente'
		resultado.datos = areas

		return resultado
	}

	/**
	 * Obtiene un area específica
	 * @param id (Requerido)
	 * Identificador del area
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

		def area = Area.get(params.id)

		if(!area){
			resultado.mensaje = 'Area no encontrada'
			return resultado
		}

		if(!area.activo){
			resultado.mensaje = 'Area inactiva'
			return resultado
		}

		resultado.estatus = true
		resultado.mensaje = 'Area encontrada exitosamente'
		resultado.datos = area

		return resultado
	}

	def obtener(id){
		def area = Area.get(id)

		if(!area) return null
		if(!area.activo) return null

		return area
	}

	def obtenerPorNombre(nombre){
		def area = Area.findByNombre(nombre)

		if(!area) return null
		if(!area.activo) return null

		return area
	}
}
