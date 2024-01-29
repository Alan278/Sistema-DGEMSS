package sieges

import grails.gorm.transactions.Transactional
import org.hibernate.criterion.CriteriaSpecification

@Transactional
class FormacionService {
	def planEstudiosService
	def formatoService
	def usuarioService
	def bitacoraService
	def tipoFormacionService

	/**
	 * Obtiene las formaciones activas
	 */
	def registrar(params){
		def resultado = [
			estatus: false,
			mensaje: "",
			datos: null
		]

		def datosBitacora = [
            clase: "FormacionService",
            metodo: "registrar",
            nombre: "Registro de formación",
            descripcion: "Se registra una formación",
            estatus: "ERROR"
        ]

		def planEstudios = planEstudiosService.obtener(params.planEstudiosId)
		if(!planEstudios){
			resultado.mensaje = "Plan de estudios no encontrado"
			return resultado
		}

		def resultadoValidacion
        // Se validan los privilegios del usuario
		resultadoValidacion = validarPrivilegios(planEstudios)
		if (!resultadoValidacion) {
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
        }

		def tipoFormacion = tipoFormacionService.obtener(params.tipoFormacionId)
		if(!tipoFormacion){
			resultado.mensaje = "Tipo de formación no encontrado"
			return resultado
		}

		def formacion = new Formacion()
		formacion.nombre = formatoService.toFlatString(params.nombre)
		formacion.requerida = params.requerida ? true : false
		formacion.general = params.general ? true : false
		formacion.planEstudios = planEstudios
		formacion.tipoFormacion = tipoFormacion

		if(!formacion.save(flush: true)){
			transactionStatus.setRollbackOnly()
			bitacoraService.registrar(datosBitacora)
			resultado.mensaje = "Error al guardar la formación"
			return resultado
		}

		datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = "Formación registrada exitosamente"
		resultado.datos = formacion

		return resultado
	}

	def validarPrivilegios(planEstudios){

        // Se valida el privilegio del usuario sobre instituciones
        // privadas o públicas segun corresponda su rol
        def isPublic = planEstudios.carrera.institucion.publica
		if(!usuarioService.validarPrivilegioSobreTipoInstitucion(isPublic)){
			return false
		}

        // Se valida que el usuario pertenezca al mismo nivel que el plan de estudios
        def nivelId = planEstudios.carrera.nivel.id
        if(!usuarioService.perteneceANivel(nivelId)){
			return false
		}

        return true
    }

	/**
	 * Obtiene las formaciones activas
	 */
	def modificar(params){
		def resultado = [
			estatus: false,
			mensaje: "",
			datos: null
		]

		def datosBitacora = [
            clase: "FormacionService",
            metodo: "modificar",
            nombre: "Modificación de formación",
            descripcion: "Se modifica una formación",
            estatus: "ERROR"
        ]

		def formacion = obtener(params.id)
		if(!formacion){
			resultado.mensaje = "Formación no encontrada"
			return resultado
		}

		def resultadoValidacion
        // Se validan los privilegios del usuario
		resultadoValidacion = validarPrivilegios(formacion.planEstudios)
		if (!resultadoValidacion) {
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
        }

		def tipoFormacion = tipoFormacionService.obtener(params.tipoFormacionId)
		if(!tipoFormacion){
			resultado.mensaje = "Tipo de formación no encontrado"
			return resultado
		}

		formacion.nombre = formatoService.toFlatString(params.nombre)
		formacion.requerida = params.requerida ? true : false
		formacion.general = params.general ? true : false
		formacion.tipoFormacion = tipoFormacion

		if(!formacion.save(flush: true)){
			transactionStatus.setRollbackOnly()
			bitacoraService.registrar(datosBitacora)
			resultado.mensaje = "Error al guardar la formación"
			return resultado
		}

		datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = "Formación modificada exitosamente"
		resultado.datos = formacion

		return resultado
	}

	/**
	 * Obtiene las formaciones activas
	 */
	def listar(params){
		def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

		if(!params.planEstudiosId){
			resultado.mensaje = "El plan de estudios es un dato requerido"
			return resultado
		}

		def criteria = {
			and{
				eq("activo", true)
				if(params.planEstudiosId){
					planEstudios {
						eq("id", params.planEstudiosId.toInteger())
					}
				}
			}
		}

		def formaciones = Formacion.createCriteria().list(criteria)

		resultado.estatus = true
		resultado.datos = formaciones
		return resultado
	}

    /**
	 * Obtiene las formaciones activas
	 */
	def obtenerActivos(params){
		def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

		def planEstudios = PlanEstudios.get(params.planEstudiosId)

		def criteria = {
			and{
				eq("activo", true)
				eq("planEstudios", planEstudios)
			}
		}

		def formaciones = Formacion.createCriteria().list(criteria)

		resultado.estatus = true
		resultado.mensaje = 'Formaciones consultadas exitosamente'
		resultado.datos = formaciones

		return resultado
	}

	def obtener(id){
		def formacion = Formacion.get(id)

		if(!formacion) return null
		if(!formacion.activo) return null

		return formacion
	}

	def obtenerPorNombreYPlanEstudios(nombre, planEstudios){
		def formacion = Formacion.findByNombreAndPlanEstudios(nombre, planEstudios)

		if(!formacion) return null
		if(!formacion.activo) return null

		return formacion
	}

	/**
	 * Obtiene las formaciones activas
	 */
	def obtenerFormacionesIndividuales(params){
		def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

		def planEstudios = PlanEstudios.get(params.planEstudiosId)
		def criteria = {
			and{
				eq("activo", true)
				eq("general", false)
				eq("planEstudios", planEstudios)
			}
		}

		def formaciones = Formacion.createCriteria().list(criteria)

		resultado.estatus = true
		resultado.mensaje = 'Formaciones consultadas exitosamente'
		resultado.datos = formaciones

		return resultado
	}

	def eliminar(params){
		def resultado = [
			estatus: false,
			mensaje: "",
			datos: null
		]

		def datosBitacora = [
            clase: "FormacionService",
            metodo: "eliminar",
            nombre: "Eliminación de formación",
            descripcion: "Se elimina la formación con id ${params.id}",
            estatus: "ERROR"
        ]

		def formacion = obtener(params.id)
		if(!formacion){
			resultado.mensaje = "Formación no encontrada"
			return resultado
		}

		formacion.activo = false

		if(!formacion.save(flush: true)){
			transactionStatus.setRollbackOnly()
			bitacoraService.registrar(datosBitacora)
			resultado.mensaje = "Error al eliminar la formación"
			return resultado
		}

		datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = "Formación eliminada exitosamente"
		resultado.datos = formacion

		return resultado
	}
}
