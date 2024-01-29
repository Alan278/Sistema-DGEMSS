package sieges

import grails.gorm.transactions.Transactional
import org.hibernate.criterion.CriteriaSpecification

/**
 * CarreraExternaService para la administración de las carreras foráneas o externas
 * @authors Luis Dominguez, Leslie Navez
 * @since 2021
 */

@Transactional
class CarreraExternaService {
	/**
	 * Inyección de messageSource el cual contiene los mensajes para las validaciones de los atributos de la clase de
	 dominio de carrera
	 */
	def messageSource
	/**
	 * Inyección del planEstudiosExternoService que contiene métodos para la administración de los planes de estudio
	 foráneos / externos.
	 */
	def planEstudiosExternoService
	/**
	 * Inyección de BitacoraService que contiene la lógica de administración de la bitacora
	 */
	def bitacoraService

/**
 * Permite realizar el registro de las carreras tomando en cuenta si provienen de una institución foránea / externa o no.
 * @param params (requerido)
 * parametros de la asignatura foránea / externa
 * @param institucionId (requerido)
 * Id de la institución foránea / externa
 * @return resultado
 * resultado con el estatus, mensaje y datos de la carrera foránea / externa
 */
	def registrar(params){
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		def carrera = new Carrera(params)

		def institucion = Institucion.get(params.institucionId)
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

		def nivel = null
		if(params.nivelId){
			nivel = Nivel.get(params.nivelId)
			if(!nivel){
				resultado.mensaje = 'Nivel no encontrado'
				return resultado
			}
			if(!nivel.activo){
				resultado.mensaje = 'Nivel inactivo'
				return resultado
			}
		}

		def modalidad = null
		if(params.modalidadId){
			modalidad = Modalidad.get(params.modalidadId)
			if(!modalidad){
				resultado.mensaje = 'Modalidad no encontrada'
				return resultado
			}
			if(!modalidad.activo){
				resultado.mensaje = 'Modalidad inactiva'
				return resultado
			}
		}

		def area = null
		if(params.areaId){
			area = Area.get(params.areaId)
			if(!area){
				resultado.mensaje = 'Area no encontrada'
				return resultado
			}
			if(!area.activo){
				resultado.mensaje = 'Area inactiva'
				return resultado
			}
		}

		carrera.institucion = institucion
		carrera.nivel = nivel
		carrera.modalidad = modalidad
		carrera.area = area

		if(carrera.save(flush:true)){
			resultado.estatus = true
			bitacoraService.registrar([clase:"CarreraExternaService", metodo:"registrar", nombre:"Registro de la carrera externa", descripcion:"Se registra la carrera externa", estatus:"EXITOSO"])
		}else{
			carrera.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar([clase:"CarreraExternaService", metodo:"registrar", nombre:"Registro de la carrera externa", descripcion:"Se registra la carrera externa", estatus:"ERROR"])
		}

		if(resultado.estatus){
			resultado.mensaje = 'Carrera creada exitosamente'
			resultado.datos = carrera
		}

		return resultado

	}

/**
 * Permite generar el listado de las carreras foráneas / externas que se encuentran registradas, además de permitir realizar la
 busqueda por nombre y el filtrado a través de la institución foránea / externa
 * @param params (requerido)
 * parametros de la asignatura
 * @param institucionId (requerido)
 * id de la institución foránea / externa
 * @return resultado
 * resultado con el estatus, mensaje y datos de la carrera foránea / externa
 */
	def listar(params){
		def resultado = [
				estatus: false,
				mensaje: '',
				datos: [
					carreras: null,
					numeroPlanes: [],
				]
		]

		if(!params.max) params.max = 3
		if(!params.offset) params.offset = 0
		if(!params.sort) params.sort = 'id'
		if(!params.order) params.order = 'asc'

		def institucionId

		if(params.institucionId){
		   institucionId = Integer.parseInt(params.institucionId)
		}

		def criteria = {
			createAlias("institucion", "i", CriteriaSpecification.LEFT_JOIN)
			and{
				eq("activo", true)
				eq("i.externa", true)
				if(params.search){
					ilike("nombre", "%${params.search}%")
				}
				if(params.institucionId){
					eq("i.id", institucionId)
				}
			}
		}

		def carreras = Carrera.createCriteria().list(params,criteria)

		if(carreras.totalCount <= 0){
			resultado.mensaje = 'No se encontraron carreras'
			resultado.datos.carreras = carreras
			return resultado
		}

		resultado.estatus = true
		resultado.mensaje = 'Carreras consultadas exitosamente'
		resultado.datos.carreras = carreras

		if(!params.notCount){
			carreras.each{ carrera ->
				params.carreraId = carrera.id.toString()
				params.notCount = true
				def nPlanes = planEstudiosExternoService.listar(params).datos.planesEstudios.totalCount
				resultado.datos.numeroPlanes << nPlanes
			}
		}


		return resultado
	}

/**
 * Permite mostrar la lista de las carreras que se encuentran activas
 * @param params (requerido)
 * parametros de la carrera foránea / externa
 * @return resultado
 * resultado con el estatus, mensaje y datos de la carrera foránea / externa
 */
	def obtenerActivos(params){
		def resultado = [
				estatus: false,
				mensaje: '',
				datos: null
		]

		def institucionId

		if(params.institucionId){
		   institucionId = Integer.parseInt(params.institucionId)
		}

		def carreras = Carrera.createCriteria().list {
			createAlias("institucion", "i", CriteriaSpecification.LEFT_JOIN)
			and{
				eq("activo", true)
				eq("i.externa", true)
				if(params.institucionId){
					eq("i.id", institucionId)
				}
			}
		}


		resultado.estatus = true
		resultado.mensaje = 'Carreras consultadas exitosamente'
		resultado.datos = carreras

		return resultado
	}

/**
 * Permite realizar la consulta del registro seleccionado con su información correspondiente.
 * @param params (requerido)
 * parametros de la carrera foránea / externa
 * @return resultado
 * resultado con el estatus, mensaje y datos de la carrera foránea / externa
 */
	def consultar(params){
		def resultado = [
				estatus: false,
				mensaje: '',
				datos: null
		]

		if(!params.id){
			resultado.mensaje = 'El id es un dato requerido'
			return resultado
		}

		def carrera = Carrera.get(params.id)

		if(!carrera){
			resultado.mensaje = 'Carrera no encontrada'
			return resultado
		}

		if(!carrera.activo){
			resultado.mensaje = 'Carrera inactiva'
			return resultado
		}
		//Verifica si la carrera es perteneciente a una institución externa o no
		if(!carrera.institucion.externa){
			resultado.mensaje = 'La carrera no pertenece a una institución externa'
			return resultado
		}

		resultado.estatus = true
		resultado.mensaje = 'Carrera encontrada exitosamente'
		resultado.datos = carrera

		return resultado
	}

/**
 * Permite realizar la modificación de la carrera seleccionada
 * @param params
 * parametros de la carrera foránea / externa
 * @param institucionId
 * id de la institución foránea / externa
 * @return resultado
 * resultado con el estatus, mensaje y datos de la carrera foránea / externa
 */
	def modificar(params){
		def resultado = [
				estatus: false,
				mensaje: '',
				datos: null
		]

		if(!params.id){
			resultado.mensaje = 'El id es un dato requerido'
			return resultado
		}

		def carrera = Carrera.get(params.id)

		if(!carrera){
			resultado.mensaje = 'Carrera no encontrada'
			return resultado
		}

		if(!carrera.activo){
			resultado.mensaje = 'Carrera inactiva'
			return resultado
		}

		if(!carrera.institucion.externa){
			resultado.mensaje = 'La carrera no pertenece a una institución externa'
			return resultado
		}

		//Se crea una copia auxiliar del objeto para verificar si existe alguna modificación
		def carreraAux = new Carrera(carrera.properties)
		carreraAux.institucion = carrera.institucion
		carreraAux.modalidad = carrera.modalidad
		carreraAux.area = carrera.area
		carreraAux.nivel = carrera.nivel

		def institucion = Institucion.get(params.institucionId)
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

		def nivel = null
		if(params.nivelId){
			nivel = Nivel.get(params.nivelId)
			if(!nivel){
				resultado.mensaje = 'Nivel no encontrado'
				return resultado
			}
			if(!nivel.activo){
				resultado.mensaje = 'Nivel inactivo'
				return resultado
			}
		}

		def modalidad = null
		if(params.modalidadId){
			modalidad = Modalidad.get(params.modalidadId)
			if(!modalidad){
				resultado.mensaje = 'Modalidad no encontrada'
				return resultado
			}
			if(!modalidad.activo){
				resultado.mensaje = 'Modalidad inactiva'
				return resultado
			}
		}

		def area = null
		if(params.areaId){
			area = Area.get(params.areaId)

			if(!area){
				resultado.mensaje = 'Area no encontrada'
				return resultado
			}

			if(!area.activo){
				resultado.mensaje = 'Area inactiva'
				return resultado
			}
		}

		carrera.institucion = institucion
		carrera.nivel = nivel
		carrera.modalidad = modalidad
		carrera.area = area
		carrera.properties = params

		//En caso de cambios se modifica el campo de ultima actualización con la fecha actual en la que se realizo el cambio
		if(!carrera.equals(carreraAux))
			carrera.ultimaActualizacion = new Date()

		if(carrera.save(flush:true)){
			resultado.estatus = true
			bitacoraService.registrar([clase:"CarreraExternaService", metodo:"modificar", nombre:"Modificación de la carrera externa", descripcion:"Se modifica la carrera externa", estatus:"EXITOSO"])
		}else{
			carrera.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar([clase:"CarreraExternaService", metodo:"modificar", nombre:"Modificación de la carrera externa", descripcion:"Se modifica la carrera externa", estatus:"ERROR"])
		}

		if(resultado.estatus){
			resultado.mensaje = 'Carrera modificada exitosamente'
			resultado.datos = carrera
		}

		return resultado

	}

/**
 * Permite eliminar la carrera foránea / externa seleccionada
 * @param params (requerido)
 * parametros de la carrera foránea / externa
 * @return resultado
 * resultado con el estatus, mensaje y datos de la carrera foránea / externa
 */
	def eliminar(params){
		def resultado = [
				estatus: false,
				mensaje: '',
				datos: null
		]

		if(!params.id){
			resultado.mensaje = 'El id es un dato requerido'
			return resultado
		}

		def carrera = Carrera.get(params.id)

		if(!carrera){
			resultado.mensaje = 'Carrera no encontrada'
			return resultado
		}
		if(!carrera.institucion.externa){
			resultado.mensaje = 'La carrera no pertenece a una institución externa'
			return resultado
		}

		carrera.activo = false

		if(!carrera.save(flush:true)){
			carrera.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar([clase:"CarreraExternaService", metodo:"eliminar", nombre:"Eliminación de la carrera externa", descripcion:"Se elimina la carrera externa", estatus:"ERROR"])
			return resultado
		}
		bitacoraService.registrar([clase:"CarreraExternaService", metodo:"eliminar", nombre:"Eliminación de la carrera externa", descripcion:"Se elimina la carrera externa", estatus:"EXITOSO"])

		resultado.estatus = true
		resultado.mensaje = 'Carrera dada de baja exitosamente'
		resultado.datos = carrera

		return resultado
	}

}
