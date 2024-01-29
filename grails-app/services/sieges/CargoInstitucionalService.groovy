package sieges

import grails.gorm.transactions.Transactional

/**
 * Servicio que permite la administración de cargos institucionales
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */
@Transactional
class CargoInstitucionalService {

	/**
	 * Obtiene los cargos activos con parametros de paginación
	 * @param search (Opcional)
	 * Nombre del cargo
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

		def cargosInstitucionales = CargoInstitucional.createCriteria().list(params,criteria)

		if(cargosInstitucionales.totalCount <= 0){
			resultado.mensaje = 'No se encontraron cargos institucionales'
			resultado.datos = cargosInstitucionales
			return resultado
		}
		resultado.estatus = true
		resultado.mensaje = 'Cargos Institucionales consultados exitosamente'
		resultado.datos = cargosInstitucionales

		return resultado
	}

	/**
	 * Obtiene los cargos activos
	 */
	def obtenerActivos(params){
		def resultado = [
				estatus: false,
				mensaje: '',
				datos: null
		]

		def cargosInstitucionales = CargoInstitucional.createCriteria().list {
			eq("activo", true)
		}

		resultado.estatus = true
		resultado.mensaje = 'Cargos Institucionales consultados exitosamente'
		resultado.datos = cargosInstitucionales

		return resultado
	}

	/**
	 * Obtiene un cargo específico
	 * @param id (Requerido)
	 * Identificador del cargo
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

		def cargoInstitucional = CargoInstitucional.get(params.id)

		if(!cargoInstitucional){
			resultado.mensaje = 'Cargo Institucional no encontrado'
			return resultado
		}

		if(!cargoInstitucional.activo){
			resultado.mensaje = 'Cargo Institucional inactivo'
			return resultado
		}

		resultado.estatus = true
		resultado.mensaje = 'Cargo Institucional encontrado exitosamente'
		resultado.datos = nivel

		return resultado
	}
}
