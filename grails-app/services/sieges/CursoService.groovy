package sieges

import grails.gorm.transactions.Transactional
import org.hibernate.criterion.CriteriaSpecification

/**
 * CursoService para la gestión  de los cursos
 * @authors Dominguez Luis, Navez Leslie
 * @since 2021
 */

@Transactional
class CursoService {
    /**
     * Inyección de messageSource que contiene mensajes de validaciones para los atributos de cada dominio
     * Los mensajes de validación se encuentran en: grails-app/i18n/messages_es.properties
     */
    def messageSource
    /**
     * Inyección de BitacoraService que contiene la lógica de administración de la bitacora
     */
    def bitacoraService
    def usuarioService

/**
 * Permite realizar el registro de los cursos.
 * @param params (requerido)
 * parametros del curso
 * @param institucionId (requerido)
 * Id de la institución
 * @return resultado
 * resultado con el estatus, mensaje y datos del curso
 */
    def registrar(params){
        def resultado =[
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def institucion = Institucion.get(params.institucionId)

        if(!institucion){
            resultado.mensaje = 'Institucion no encontrada'
            return resultado
        }
        if(!institucion.activo){
            resultado.mensaje = 'Institucion inactiva'
            return resultado
        }

        def curso = new Curso(params)
        curso.institucion = institucion

        if(curso.save(flush:true)){
            resultado.estatus = true
            bitacoraService.registrar([clase:"CursoService", metodo:"registrar", nombre:"Registro del curso", descripcion:"Se registra el curso", estatus:"EXITOSO"])
        }else{
            curso.errors.allErrors.each {
                resultado.mensaje = messageSource.getMessage(it, null)
            }
            bitacoraService.registrar([clase:"CursoService", metodo:"registrar", nombre:"Registro del curso", descripcion:"Se registra el curso", estatus:"ERROR"])
        }

        if(resultado.estatus){
            resultado.mensaje = 'Curso creado exitosamente'
            resultado.datos = curso
        }

        return resultado
    }

/**
 * Permite generar el listado de las carreras que se encuentran registradas, además de permitir realizar la
 busqueda por nombre y el filtrado a través de la institución
 * @param params (requerido)
 * parametros del curso
 * @param institucionId (requerido)
 * id de la institución
 * @return resultado
 * resultado con el estatus, mensaje y datos del curso
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

        def criteria = {
            createAlias("institucion", "i", CriteriaSpecification.LEFT_JOIN)
            and{
                eq("activo", true)
                if(params.search){
                    or{
                        ilike("nombre", "%${params.search}%")
                    }
                }
                if(params.institucionId){
                    eq("i.id", institucionId)
                }
            }
        }

        def cursos = Curso.createCriteria().list(params,criteria)

        if(cursos.totalCount <= 0){
            resultado.mensaje = 'No se encontraron cursos'
            resultado.datos = cursos
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'Cursos consultados exitosamente'
        resultado.datos = cursos

        return resultado
    }

/**
 * Permite mostrar la lista de los cursos que se encuentran activos
 * @param params (requerido)
 * parametros del curso
 * @return resultado
 * resultado con el estatus, mensaje y datos del curso
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

        def cursos = Curso.createCriteria().list {
            createAlias("institucion", "i", CriteriaSpecification.LEFT_JOIN)
            and{
                eq("activo", true)
                if(params.institucionId){
                    eq("i.id", institucionId)
                }
            }
        }
        resultado.estatus = true
        resultado.mensaje = 'Cursos consultados exitosamente'
        resultado.datos = cursos

        return resultado
    }

/**
 * Permite realizar la consulta del registro seleccionado con su información correspondiente.
 * @param params (requerido)
 * parametros del curso
 * @return resultado
 * resultado con el estatus, mensaje y datos del curso
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

        def curso = Curso.get(params.id)

        if(!curso){
            resultado.mensaje = 'Curso no encontrado'
            return resultado
        }

        if(!curso.activo){
            resultado.mensaje = 'Curso inactivo'
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'Curso encontrado exitosamente'
        resultado.datos = curso

        return resultado
    }
/**
 * Permite realizar la modificación del curso seleccionado
 * @param params
 * parametros del curso
 * @param institucionId (requerido)
 * Id de la institución
 * @return resultado
 * resultado con el estatus, mensaje y datos del curso
 */
    def modificar(params) {
        def resultado = [
                estatus: false,
                mensaje: '',
                datos: null
        ]

        if(!params.id){
            resultado.mensaje = 'El id es un dato requerido'
            return resultado
        }

        def curso = Curso.get(params.id)

        if(!curso){
            resultado.mensaje = 'Curso no encontrado'
            return resultado
        }
        if(!curso.activo){
            resultado.mensaje = 'Curso inactivo'
            return resultado
        }

        def institucion = Institucion.get(params.institucionId)

        if(!institucion){
            resultado.mensaje = 'Institucion no encontrada'
            return resultado
        }
        if(!institucion.activo){
            resultado.mensaje = 'Institucion inactiva'
            return resultado
        }

        def cursoAux = new Curso(curso.properties)
        cursoAux.institucion = curso.institucion

        curso.properties = params
        curso.institucion = institucion

        if(!curso.equals(cursoAux))
            curso.ultimaActualizacion = new Date()

        if(curso.save(flush:true)){
            resultado.estatus = true
            bitacoraService.registrar([clase:"CursoService", metodo:"modificar", nombre:"Modificación del curso", descripcion:"Se modifica el curso", estatus:"EXITOSO"])
        }else{
            curso.errors.allErrors.each {
                resultado.mensaje = messageSource.getMessage(it, null)
            }
            bitacoraService.registrar([clase:"CursoService", metodo:"modificar", nombre:"Modificación del curso", descripcion:"Se modifica el curso", estatus:"ERROR"])
        }

        if(resultado.estatus){
            resultado.mensaje = 'Curso modificado exitosamente'
            resultado.datos = curso
        }

        return resultado
    }

/**
 * Permite eliminar el curso seleccionada
 * @param params (requerido)
 * parametros del curso
 * @return resultado
 * resultado con el estatus, mensaje y datos del curso
 */
    def eliminar(params) {
        def resultado = [
                estatus: false,
                mensaje: '',
                datos: null
        ]

        //Verifica si se encontro un id
        if(!params.id){
            resultado.mensaje = 'El id es un dato requerido'
            return resultado
        }
        //En caso de tenerlo se define una variable asignandole los paramatros del id recibido
        def curso = Curso.get(params.id)
        //Verifica si encontro el curso con ese id
        if(!curso){
            resultado.mensaje = 'Curso no encontrado'
            return resultado
        }
        //En caso de encontrarlo modifica el campo de activo a falso
        curso.activo = false
        //Verifica si se realizó el guardado de forma correcta
        if(!curso.save(flush:true)){
            //En caso de no ser así muestra los errores que ocurrieron
            curso.errors.allErrors.each {
                resultado.mensaje = messageSource.getMessage(it, null)
            }
            bitacoraService.registrar([clase:"CursoService", metodo:"eliminar", nombre:"Eliminación del curso", descripcion:"Se elimina el curso", estatus:"ERROR"])
            return resultado
        }
        bitacoraService.registrar([clase:"CursoService", metodo:"eliminar", nombre:"Eliminación del curso", descripcion:"Se elimina el curso", estatus:"EXITOSO"])
        //En caso de guardarlo, muestra el resultado
        resultado.estatus = true
        resultado.mensaje = 'Curso dado de baja exitosamente'
        resultado.datos = curso

        return resultado
    }
}
