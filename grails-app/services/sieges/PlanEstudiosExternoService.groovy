package sieges

import grails.gorm.transactions.Transactional
import org.hibernate.criterion.CriteriaSpecification

/**
 * Servicio que permite la administración de planes de estudio externos
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */
@Transactional
class PlanEstudiosExternoService {
    /**
     * Inyección de messageSource que contiene mensajes de validaciones para los atributos de cada dominio
     * Los mensajes de validación se encuentran en: grails-app/i18n/messages_es.properties
     */
    def messageSource
    /**
     * Inyección de AsignaturaExternaService que contiene la lógica de administración de asgnaturas externas
     */
    def asignaturaExternaService
    /**
     * Inyección de BitacoraService que contiene la lógica de administración de la bitacora
     */
    def bitacoraService

    /**
     * Registra un nuevo plan externo
     * @param nombre (Requerido)
     * @param carreraId (Requerido)
     * @param periodoId (Requerido)
     * @param turnoId (Opcional)
     * @param ciclos (Opcional)
     * @param calificacionMinima (Opcional)
     * @param calificacionMinimaAprobatoria (Opcional)
     * @param calificacionMaxima (Opcional)
     * @param horaInicio (Opcional)
     * @param horaFin (Opcional)
     */
    def registrar(params){
        def resultado = [
                estatus: false,
                mensaje: '',
                datos: null
        ]

        def planEstudios = new PlanEstudios(params)

        // Se validan los valores tipo Float
        try {
            if(params.calificacionMinima){
                planEstudios.calificacionMinima = Float.parseFloat(params.calificacionMinima)
            }
            if(params.calificacionMinimaAprobatoria){
                planEstudios.calificacionMinimaAprobatoria = Float.parseFloat(params.calificacionMinimaAprobatoria)
            }
            if(params.calificacionMaxima){
                planEstudios.calificacionMaxima = Float.parseFloat(params.calificacionMaxima)
            }
        }catch(NumberFormatException e) {
            resultado.mensaje = 'Las calificaciones deben de ser de tipo numérico'
            return resultado
        }

        // Se validan las relaciones con otras tablas
        def carrera = Carrera.get(params.carreraId)
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

        def periodo = Periodo.get(params.periodoId)
        if(!periodo){
            resultado.mensaje = 'Periodo no encontrado'
            return resultado
        }
        if(!periodo.activo){
            resultado.mensaje = 'Periodo inactivo'
            return resultado
        }

        def turno = null
        if(params.turnoId){
            turno = Turno.get(params.turnoId)
            if(!turno){
                resultado.mensaje = 'Turno no encontrado'
                return resultado
            }
            if(!turno.activo){
                resultado.mensaje = 'Turno inactivo'
                return resultado
            }
        }

        planEstudios.carrera = carrera
        planEstudios.periodo = periodo
        planEstudios.turno = turno

        if(planEstudios.save(flush:true)){
            resultado.estatus = true
            bitacoraService.registrar([clase:"PlanEstudiosExternoService", metodo:"registrar", nombre:"Registro del plan de estudios externo", descripcion:"Se registra el plan de estudios externo", estatus:"EXITOSO"])
        }else{
            // Se recorren los errores de validación y se asignan a la propiedad resultado.mensaje
            // Los mensajes de validación se encuentran en: grails-app/i18n/messages_es.properties
            planEstudios.errors.allErrors.each {
                resultado.mensaje = messageSource.getMessage(it, null)
            }
            bitacoraService.registrar([clase:"PlanEstudiosExternoService", metodo:"registrar", nombre:"Registro del plan de estudios externo", descripcion:"Se registra el plan de estudios externo", estatus:"ERROR"])
        }

        if(resultado.estatus){
            resultado.mensaje = 'Plan de Estudios creado exitosamente'
            resultado.datos = planEstudios
        }

        return resultado
    }

    /**
     * Obtiene los planes externos activos con parametros de paginación y filtrado
     * @param institucionId (Opcional)
     * Identificador de la institución
     * @param carreraId (Opcional)
     * Identificador de la carrera
     * @param search (Opcional)
     * Nombre del personal
     */
    def listar(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: [
                planesEstudios: null,
                numeroAsignaturas: [],
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
        def carreraId
        if(params.carreraId){
            carreraId = Integer.parseInt(params.carreraId)
        }

        def criteria = {
            createAlias("carrera.institucion", "i", CriteriaSpecification.LEFT_JOIN)
            createAlias("carrera", "c", CriteriaSpecification.LEFT_JOIN)

            and{
                eq("activo", true)
                eq("i.externa", true)
                if(params.search){
                    ilike("nombre", "%${params.search}%")
                }
                if(params.institucionId){
                    eq("i.id", institucionId)
                }
                if(params.carreraId){
                    eq("c.id", carreraId)
                }


            }
        }

        def planesEstudios = PlanEstudios.createCriteria().list(params,criteria)

        if(planesEstudios.totalCount <= 0){
            resultado.mensaje = 'No se encontraron Planes de Estudio'
            resultado.datos.planesEstudios = planesEstudios
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'Planes de Estudio consultados exitosamente'
        resultado.datos.planesEstudios = planesEstudios

        // Si el método es llamado desde carreraExternaService no se hace el conteo de asignaturas
        if(!params.notCount){
            // Se cuentan las asignaturas activas con las que cuenta cada plan
            planesEstudios.each{ plan ->
                params.planEstudiosId = plan.id.toString()
                def nAsignaturas = asignaturaExternaService.listar(params).datos.totalCount
                resultado.datos.numeroAsignaturas << nAsignaturas
            }
        }

        return resultado

    }

    /**
     * Obtiene los planes externos activos
     */
    def obtenerActivos(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        // Parametros de filtrado (opcionales)
        def carreraId
        if(params.carreraId){
           carreraId = Integer.parseInt(params.carreraId)
        }

        def planesEstudios = PlanEstudios.createCriteria().list {
            createAlias("carrera.institucion", "i", CriteriaSpecification.LEFT_JOIN)
            createAlias("carrera", "c", CriteriaSpecification.LEFT_JOIN)
            and{
                eq("activo", true)
                eq("i.externa", true)
                if(params.carreraId){
                    eq("c.id", carreraId)
                }
            }
        }

        resultado.estatus = true
        resultado.mensaje = 'Plan de Estudios consultados exitosamente'
        resultado.datos = planesEstudios

        return resultado
    }

    /**
     * Obtiene un plan externo específico
     * @param id (Requerido)
     * Identificador del plan
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

        def planEstudios = PlanEstudios.get(params.id)
        if(!planEstudios){
            resultado.mensaje = 'Plan de Estudios no encontrado'
            return resultado
        }
        if(!planEstudios.activo){
            resultado.mensaje = 'Plan de Estudios inactivo'
            return resultado
        }
        if(!planEstudios.carrera.institucion.externa){
            resultado.mensaje = 'El Plan de Estudios no pertenece a una institución externa'
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'Plan de Estudios encontrado exitosamente'
        resultado.datos = planEstudios

        return resultado
    }

    /**
     * Modifica los datos de un plan externo específico
     * @param id (Requerido)
     * Identificador del plan
     * @param nombre (Requerido)
     * @param carreraId (Requerido)
     * @param periodoId (Requerido)
     * @param turnoId (Opcional)
     * @param ciclos (Opcional)
     * @param calificacionMinima (Opcional)
     * @param calificacionMinimaAprobatoria (Opcional)
     * @param calificacionMaxima (Opcional)
     * @param horaInicio (Opcional)
     * @param horaFin (Opcional)
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

        def planEstudios = PlanEstudios.get(params.id)
        if(!planEstudios){
            resultado.mensaje = 'Plan de Estudios no encontrado'
            return resultado
        }
        if(!planEstudios.activo){
            resultado.mensaje = 'Plan de Estudios inactivo'
            return resultado
        }
        if(!planEstudios.carrera.institucion.externa){
            resultado.mensaje = 'El Plan de Estudios no pertenece a una institución externa'
            return resultado
        }

        // Se crean copias auxiliares de los objetos antes de asignar
        // los nuevos datos para verificar si se realizó algun cambio
        def planEstudiosAux = new PlanEstudios(planEstudios.properties)
        planEstudiosAux.carrera = planEstudios.carrera
        planEstudiosAux.periodo = planEstudios.periodo
        planEstudiosAux.turno = planEstudios.turno

        // Se validan las relaciones con otras tablas
        def carrera = Carrera.get(params.carreraId)
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

        def periodo = Periodo.get(params.periodoId)
        if(!periodo){
            resultado.mensaje = 'Periodo no encontrado'
            return resultado
        }
        if(!periodo.activo){
            resultado.mensaje = 'Periodo inactivo'
            return resultado
        }

        def turno = null
        if(params.turnoId){
            turno = Turno.get(params.turnoId)
            if(!turno){
                resultado.mensaje = 'Turno no encontrado'
                return resultado
            }
            if(!turno.activo){
                resultado.mensaje = 'Turno inactivo'
                return resultado
            }
        }

        // Se asignan los nuevos datos
        planEstudios.carrera = carrera
        planEstudios.periodo = periodo
        planEstudios.turno = turno
        planEstudios.properties = params

        // Se validan los valores tipo Float
        try {
            if(params.calificacionMinima){
                planEstudios.calificacionMinima = Float.parseFloat(params.calificacionMinima)
            }
            if(params.calificacionMinimaAprobatoria){
                planEstudios.calificacionMinimaAprobatoria = Float.parseFloat(params.calificacionMinimaAprobatoria)
            }
            if(params.calificacionMaxima){
                planEstudios.calificacionMaxima = Float.parseFloat(params.calificacionMaxima)
            }
        }catch(NumberFormatException e) {
            resultado.mensaje = 'Las calificaciones deben de ser de tipo numérico'
            return resultado
        }

        // En caso de cambios se modifica el campo de ultimaActualizacion
        if(!planEstudios.equals(planEstudiosAux))
            planEstudios.ultimaActualizacion = new Date()

        if(planEstudios.save(flush:true)){
            resultado.estatus = true
            bitacoraService.registrar([clase:"PlanEstudiosExternoService", metodo:"modificar", nombre:"Modificación del plan de estudios externo", descripcion:"Se modifica el plan de estudios externo", estatus:"EXITOSO"])
        }else{
            // Se recorren los errores de validación y se asignan a la propiedad resultado.mensaje
            // Los mensajes de validación se encuentran en: grails-app/i18n/messages_es.properties
            planEstudios.errors.allErrors.each {
                resultado.mensaje = messageSource.getMessage(it, null)
            }
            bitacoraService.registrar([clase:"PlanEstudiosExternoService", metodo:"modificar", nombre:"Modificación del plan de estudios externo", descripcion:"Se modifica el plan de estudios externo", estatus:"ERROR"])
        }

        if(resultado.estatus){
            resultado.mensaje = 'Plan de estudios modificado exitosamente'
            resultado.datos = planEstudios
        }

        return resultado
    }

    /**
     * Realiza una baja lógica de un plan externo específico
     * @param id (Requerido)
     * Identificador del plan
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

        def planEstudios = PlanEstudios.get(params.id)

        if(!planEstudios){
            resultado.mensaje = 'Plan de Estudios no encontrado'
            return resultado
        }
        if(!planEstudios.carrera.institucion.externa){
            resultado.mensaje = 'El Plan de Estudios no pertenece a una institución externa'
            return resultado
        }

        planEstudios.activo = false

        if(!planEstudios.save(flush:true)){
            // Se recorren los errores de validación y se asignan a la propiedad resultado.mensaje
            // Los mensajes de validación se encuentran en: grails-app/i18n/messages_es.properties
            planEstudios.errors.allErrors.each {
                resultado.mensaje = messageSource.getMessage(it, null)
            }
            bitacoraService.registrar([clase:"PlanEstudiosExternoService", metodo:"eliminar", nombre:"Eliminación del plan de estudios externo", descripcion:"Se elimina el plan de estudios", estatus:"ERROR"])
            return resultado
        }
        bitacoraService.registrar([clase:"PlanEstudiosExternoService", metodo:"eliminar", nombre:"Eliminación del plan de estudios externo", descripcion:"Se elimina el plan de estudios externo", estatus:"EXITOSO"])

        resultado.estatus = true
        resultado.mensaje = 'Plan de Estudios dado de baja exitosamente'
        resultado.datos = planEstudios

        return resultado
    }
}
