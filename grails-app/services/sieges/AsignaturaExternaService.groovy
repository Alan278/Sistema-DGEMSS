package sieges


import grails.gorm.transactions.Transactional
import org.hibernate.criterion.CriteriaSpecification

/**
 * AsignaturaExternaService para la gestión de las asignaturas foráneas o externas
 * @authors Dominguez Luis, Navez Leslie
 * @since 2021
 */
@Transactional
class AsignaturaExternaService {
    /**
     * Inyección de messageSource que contiene mensajes de validaciones para los atributos de cada dominio
     * Los mensajes de validación se encuentran en: grails-app/i18n/messages_es.properties
     */
    def messageSource
    /**
     * Inyección de BitacoraService que contiene la lógica de administración de la bitacora
     */
    def bitacoraService

/**
 * Permite realizar el registro de las asignaturas tomando en cuenta si provienen de un plan de estudios externo o no.
 * @param params (requerido)
 * parametros de la asignatura foránea / externa
 * @param planEstudiosId (requerido)
 * Id del plan de estudios foráneo / externo
 * @return resultado
 * resultado con el estatus, mensaje y datos de la asignatura foránea / externa
 */
    def registrar(params){
        def resultado = [
                estatus: false,
                mensaje: '',
                datos: null
        ]

        def asignatura = new Asignatura(params)

        def planEstudios = PlanEstudios.get(params.planEstudiosId)

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

        asignatura.planEstudios = planEstudios

        if(asignatura.save(flush:true)){
            resultado.estatus = true
            bitacoraService.registrar([clase:"AsignaturaExternaService", metodo:"registrar", nombre:"Registro de la asignatura externa", descripcion:"Se registra la asignatura externa", estatus:"EXITOSO"])
        }else{
            asignatura.errors.allErrors.each {
                resultado.mensaje = messageSource.getMessage(it, null)
            }
            bitacoraService.registrar([clase:"AsignaturaExternaService", metodo:"registrar", nombre:"Registro de la asignatura externa", descripcion:"Se registra la asignatura externa", estatus:"ERROR"])
        }

        if(resultado.estatus){
            resultado.mensaje = 'Asignatura creada exitosamente'
            resultado.datos = asignatura
        }

        return resultado
    }
/**
 * Permite generar el listado de las asignaturas foráneas / externas que se encuentran registradas, además de permitir realizar la
   busqueda por nombre y el filtrado a través de la institución, carrera y plan de estudios foráneo / externo
 * @param params (requerido)
 * parametros de la asignatura
 * @param institucionId (requerido)
 * id de la institución foránea / externa
 * @param carreraId (requerido)
 * id de la carrera foránea / externa
 * @param planEstudiosId (requerido)
 * id del plan de estudios foráneo / externo
 * @return resultado
 * resultado con el estatus, mensaje y datos de la asignatura foránea / externa
 */
    def listar(params){
        def resultado = [
                estatus: false,
                mensaje: '',
                datos: null
        ]

        if(!params.max) params.max = 3
        if(!params.offset) params.offset = 0
        if(!params.sort) params.sort = 'id'
        if(!params.order) params.order = 'asc'

        def institucionId

        if(params.institucionId){
            institucionId = Integer.parseInt(params.institucionId)
        }

        def carreraId

        if(params.carreraId){
            carreraId = Integer.parseInt(params.carreraId)
        }

        def planEstudiosId

        if(params.planEstudiosId){
            planEstudiosId = Integer.parseInt(params.planEstudiosId)
        }

        def criteria = {
            createAlias("planEstudios.carrera.institucion", "i", CriteriaSpecification.LEFT_JOIN)
            createAlias("planEstudios.carrera", "c", CriteriaSpecification.LEFT_JOIN)
            createAlias("planEstudios", "p", CriteriaSpecification.LEFT_JOIN)

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
                if(params.planEstudiosId){
                    eq("p.id", planEstudiosId)
                }
            }
        }

        def asignaturas = Asignatura.createCriteria().list(params,criteria)

        if(asignaturas.totalCount <= 0){
            resultado.mensaje = 'No se encontraron Asignaturas'
            resultado.datos = asignaturas
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'Asignaturas consultadas exitosamente'
        resultado.datos = asignaturas

        return resultado

    }
/**
 * Permite mostrar la lista de las asignaturas que se encuentran activas
 * @param params (requerido)
 * parametros de la asignatura foránea / externa
 * @return resultado
 * resultado con el estatus, mensaje y datos de la asignatura foránea / externa
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

        def carreraId
        if(params.carreraId){
            carreraId = Integer.parseInt(params.carreraId)
        }

        def planEstudiosId
        if(params.planEstudiosId){
            planEstudiosId = Integer.parseInt(params.planEstudiosId)
        }

        def asignaturas = Asignatura.createCriteria().list{
            createAlias("planEstudios.carrera.institucion", "i", CriteriaSpecification.LEFT_JOIN)
            createAlias("planEstudios.carrera", "c", CriteriaSpecification.LEFT_JOIN)
            createAlias("planEstudios", "p", CriteriaSpecification.LEFT_JOIN)
            and{
                eq("activo", true)
                eq("i.externa", true)
                if(params.institucionId){
                    eq("i.id", institucionId)
                }
                if(params.carreraId){
                    eq("c.id", carreraId)
                }
                if(params.planEstudiosId){
                    eq("p.id", planEstudiosId)
                }
            }
        }


        resultado.estatus = true
        resultado.mensaje = 'Asignaturas consultadas exitosamente'
        resultado.datos = asignaturas

        return resultado
    }

/**
 * Permite realizar la consulta del registro seleccionado con su información correspondiente.
 * @param params (requerido)
 * parametros de la asignatura foránea / externa
 * @return resultado
 * resultado con el estatus, mensaje y datos de la asignatura foránea / externa
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

        def asignatura = Asignatura.get(params.id)
        if(!asignatura){
            resultado.mensaje = 'Asignatura no encontrada'
            return resultado
        }
        if(!asignatura.activo){
            resultado.mensaje = 'Asignatura inactiva'
            return resultado
        }
        if(!asignatura.planEstudios.carrera.institucion.externa){
            resultado.mensaje = 'La asignatura no pertenece a una institución externa'
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'Asignatura encontrada exitosamente'
        resultado.datos = asignatura

        return resultado
    }
/**
 * Permite realizar la modificación de la asignatura seleccionada
 * @param params
 * parametros de la asignatura foránea / externa
 * @param planEstudiosId
 * id del plan de estudios foráneo / externo
 * @return resultado
 * resultado con el estatus, mensaje y datos de la asignatura foránea / externa
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

        def asignatura = Asignatura.get(params.id)

        if(!asignatura){
            resultado.mensaje = 'Asignatura no encontrada'
            return resultado
        }
        if(!asignatura.activo){
            resultado.mensaje = 'Asignatura inactiva'
            return resultado
        }
        if(!asignatura.planEstudios.carrera.institucion.externa){
            resultado.mensaje = 'La asignatura no pertenece a una institución externa'
            return resultado
        }

        //Se crea una copia auxiliar del objeto para verificar si existe alguna modificación
        def asignaturaAux = new Asignatura(asignatura.properties)

        asignaturaAux.planEstudios = asignatura.planEstudios

        def planEstudios = PlanEstudios.get(params.planEstudiosId)

        if(!planEstudios){
            resultado.mensaje = 'Plan de Estudios no encontrado'
            return resultado
        }

        if(!planEstudios.activo){
            resultado.mensaje = 'Plan de Estudios inactivo'
            return resultado
        }

        asignatura.planEstudios = planEstudios
        asignatura.properties = params

        //En caso de cambios se modifica el campo de ultima actualización con la fecha actual en la que se realizo el cambio.
        if(!asignatura.equals(asignaturaAux))
            asignatura.ultimaActualizacion = new Date()

        if(asignatura.save(flush:true)){
            resultado.estatus = true
            bitacoraService.registrar([clase:"AsignaturaExternaService", metodo:"modificar", nombre:"Modificación de la asignatura externa", descripcion:"Se modifica la asignatura externa", estatus:"EXITOSO"])
        }else{
            asignatura.errors.allErrors.each {
                resultado.mensaje = messageSource.getMessage(it, null)
            }
            bitacoraService.registrar([clase:"AsignaturaExternaService", metodo:"modificar", nombre:"Modificación de la asignatura externa", descripcion:"Se modifica la asignatura externa", estatus:"ERROR"])
        }

        if(resultado.estatus){
            resultado.mensaje = 'Asignatura modificada exitosamente'
            resultado.datos = asignatura
        }

        return resultado

    }
/**
 * Permite eliminar la asignatura foránea / externa seleccionada
 * @param params (requerido)
 * parametros de la asignatura foránea / externa
 * @return resultado
 * resultado con el estatus, mensaje y datos de la asignatura foránea / externa
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

        def asignatura = Asignatura.get(params.id)

        if(!asignatura){
            resultado.mensaje = 'Asignatura no encontrada'
            return resultado
        }
        if(!asignatura.planEstudios.carrera.institucion.externa){
            resultado.mensaje = 'La asignatura no pertenece a una institución externa'
            return resultado
        }

        asignatura.activo = false

        if(!asignatura.save(flush:true)){
            asignatura.errors.allErrors.each {
                resultado.mensaje = messageSource.getMessage(it, null)
            }
            bitacoraService.registrar([clase:"AsignaturaExternaService", metodo:"eliminar", nombre:"Eliminación de la asignatura externa", descripcion:"Se elimina la asignatura externa", estatus:"ERROR"])
            return resultado
        }
        bitacoraService.registrar([clase:"AsignaturaExternaService", metodo:"eliminar", nombre:"Eliminación de la asignatura externa", descripcion:"Se elimina la asignatura externa", estatus:"EXITOSO"])

        resultado.estatus = true
        resultado.mensaje = 'Asignatura dada de baja exitosamente'
        resultado.datos = asignatura

        return resultado
    }

}
