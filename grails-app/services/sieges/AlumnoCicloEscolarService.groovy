package sieges

import grails.gorm.transactions.Transactional
import org.hibernate.criterion.CriteriaSpecification

/*
 * @authors Alan Guevarin
 * @since 2022
 */
@Transactional
class AlumnoCicloEscolarService {
    def bitacoraService
    def usuarioService
    def alumnoService
    def cicloEscolarService
    def estatusRegistroService
    def dataSource

    def registro(params){
		def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

		def datosBitacora = [
            clase: "AlumnoCicloEscolarService",
            metodo: "registro",
            nombre: "Registro de alumno a ciclo escolar",
            descripcion: "Registro de alumno a ciclo escolar",
            estatus: "ERROR"
        ]

		def alumno = alumnoService.obtener(params.id)
		if(!alumno){
		    resultado.mensaje = 'Alumno no encontrado'
		    return resultado
		}

		def cicloEscolar = cicloEscolarService.obtener(params.cicloEscolarId)
		if(!cicloEscolar){
		    resultado.mensaje = 'Ciclo escolar no encontrado'
		    return resultado
		}

		// Se valida que la carrera pertenesca a la misma institución que el usuario
		def institucionId = cicloEscolar.planEstudios.carrera.institucion.id
        if(!usuarioService.perteneceAInstitucion(institucionId)){
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
			return resultado
		}

		def yaSeEncuentraRegistrado = AlumnoCicloEscolar.createCriteria().list{
			createAlias("alumno", "alumno", CriteriaSpecification.LEFT_JOIN)
			createAlias("cicloEscolar", "ciclo", CriteriaSpecification.LEFT_JOIN)
			and{
				eq('activo', true)
				eq('alumno.id', alumno.id)
				or{
					eq('ciclo.id', cicloEscolar.id)
					eq('ciclo.periodo', cicloEscolar.periodo)
				}
			}
		}

		if(yaSeEncuentraRegistrado){
			resultado.mensaje = "El alumno ya se encuentra inscrito en un ciclo escolar con el mismo periodo"
			return resultado
		}

		def alumnoCicloEscolar = new AlumnoCicloEscolar()
		alumnoCicloEscolar.alumno = alumno
		alumnoCicloEscolar.cicloEscolar = cicloEscolar
		alumnoCicloEscolar.estatusRegistro = EstatusRegistro.get(estatusRegistroService.EDITABLE)

		if(!alumnoCicloEscolar.save(flush: true)){
			bitacoraService.registrar(datosBitacora)
			return resultado
		}

		datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

        resultado.estatus = true
        resultado.mensaje = 'Ciclo escolar agregado exitosamente'
        return resultado
	}

    def obtenerPorAlumno(alumnoId){

		def ciclosEscolares = AlumnoCicloEscolar.createCriteria().list {
			createAlias("alumno", "a", CriteriaSpecification.LEFT_JOIN)
			createAlias("cicloEscolar", "ciclo", CriteriaSpecification.LEFT_JOIN)
			and{
				eq("activo", true)
				eq("a.id", alumnoId)
			}
            order("ciclo.periodo", "asc")
		}

		return ciclosEscolares
	}

    def obtener(id){
		def alumnoCicloEscolar = AlumnoCicloEscolar.get(id)

		if(!alumnoCicloEscolar) return null
		if(!alumnoCicloEscolar.activo) return null

		return alumnoCicloEscolar
	}

    /**
     * Permite eliminar una relación entre alumno y ciclo escolar
     * @param id (requerido) id del registro
     * @return resultado
     * resultado con el estatus, mensaje y datos del curso
     */
    def eliminar(params) {
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def datosBitacora = [
            clase: "AlumnoCicloEscolarService",
            metodo: "eliminar",
            nombre: "Eliminación de registro",
            descripcion: "Eliminación del registro de un alumno a un ciclo escolar",
            estatus: "ERROR"
        ]

        if(!params.id){
            resultado.mensaje = 'El id es un dato requerido'
            return resultado
        }

        def alumnoCicloEscolar = this.obtener(params.id)
        if(!alumnoCicloEscolar){
            resultado.mensaje = 'Registro no encontrado'
            return resultado
        }

        def institucionId = alumnoCicloEscolar.cicloEscolar.planEstudios.carrera.institucion.id
        if(!usuarioService.perteneceAInstitucion(institucionId)){
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
			return resultado
		}

        if(alumnoCicloEscolar.estatusRegistro.id != estatusRegistroService.EDITABLE){
		    resultado.mensaje = 'El registro ya se encuentra asociado a un certificado'
		    return resultado
		}

        alumnoCicloEscolar.activo = false
        if(!alumnoCicloEscolar.save(flush:true)){
            transactionStatus.setRollbackOnly()
            resultado.mensaje = 'Ocurrió un error al intentar eliminar el registro'
            return resultado
        }

        def cicloEscolar = alumnoCicloEscolar.cicloEscolar
        def alumno = alumnoCicloEscolar.alumno

        def evaluaciones = Evaluacion.findAllByCicloEscolarAndAlumnoAndActivo(cicloEscolar, alumno, true)

        for(evaluacion in evaluaciones){
            println(evaluacion)
            evaluacion.activo = false
            if(!evaluacion.save(flush:true)){
                transactionStatus.setRollbackOnly()
                resultado.mensaje = 'Ocurrió un error al intentar eliminar el registro'
                return resultado
            }
        }

        datosBitacora.estatus = "EXITOSO"
        bitacoraService.registrar(datosBitacora)

        resultado.estatus = true
        resultado.mensaje = 'Registro dado de baja exitosamente'
        resultado.datos = alumnoCicloEscolar

        return resultado
    }
}
