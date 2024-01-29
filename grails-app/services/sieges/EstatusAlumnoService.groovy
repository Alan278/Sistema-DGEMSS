package sieges

import grails.gorm.transactions.Transactional

/**
 * Servicio que permite la administración de estatus de alumnos
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */
@Transactional
class EstatusAlumnoService {
	/**
	 * Inyección de messageSource que contiene mensajes de validaciones para los atributos de cada dominio
	 * Los mensajes de validación se encuentran en: grails-app/i18n/messages_es.properties
	 */
	def messageSource

	/**
	 * Obtiene los estatus activos con parametros de paginación
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

		def estatusAlumno = EstatusAlumno.createCriteria().list(params,criteria)

		if(estatusAlumno.totalCount <= 0){
			resultado.mensaje = 'No se encontraron estatus'
			resultado.datos = estatusAlumno
			return resultado
		}
		resultado.estatus = true
		resultado.mensaje = 'Estatus consultados exitosamente'
		resultado.datos = estatusAlumno

		return resultado
	}

	/**
	 * Obtiene los estatus activos
	 */
	def obtenerActivos(params){
		def resultado = [
				estatus: false,
				mensaje: '',
				datos: null
		]

		def estatusAlumno = EstatusAlumno.createCriteria().list {
			eq("activo", true)
		}

		resultado.estatus = true
		resultado.mensaje = 'Estatus consultados exitosamente'
		resultado.datos = estatusAlumno

		return resultado
	}

	/**
	 * Obtiene un estatus específico
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

		def estatusAlumno = obtener(params.id)
		if(!estatusAlumno){
			resultado.mensaje = 'Estatus no encontrado'
			return resultado
		}

		resultado.estatus = true
		resultado.mensaje = 'Estatus encontrado exitosamente'
		resultado.datos = estatusAlumno

		return resultado
	}

	def obtener(id){
		def estatusAlumno = EstatusAlumno.get(id)

		if(!estatusAlumno) return null
		if(!estatusAlumno.activo) return null

		return estatusAlumno
	}

	def obtenerPorNombre(nombre){
		def estatusAlumno = EstatusAlumno.findByNombre(nombre)

		if(!estatusAlumno) return null
		if(!estatusAlumno.activo) return null

		return estatusAlumno
	}
}
