package sieges

import grails.gorm.transactions.Transactional
import org.hibernate.criterion.CriteriaSpecification

/**
 * Servicio que permite la administración de diplomados
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */
@Transactional
class DiplomadoService {
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
     * Registra un nuevo diplomado
     * @param nombre (Requerido)
     * Nombre del diplomado
     * @param institucionId (Requerido)
     * Identifcador de la institución
     */
    def registrar(params){
        def resultado =[
            estatus: false,
            mensaje: '',
            datos: null
        ]

        // Se validan las relaciones con otras tablas
        def institucion = Institucion.get(params.institucionId)
        if(!institucion){
            resultado.mensaje = 'Institucion no encontrada'
            return resultado
        }
        if(!institucion.activo){
            resultado.mensaje = 'Institucion inactiva'
            return resultado
        }

        def diplomado = new Diplomado(params)
        diplomado.institucion = institucion

        if(diplomado.save(flush:true)){
            resultado.estatus = true
            bitacoraService.registrar([clase:"DiplomadoService", metodo:"registrar", nombre:"Registro del diplomado", descripcion:"Se registra el diplomado", estatus:"EXITOSO"])
        }else{
            // Se recorren los errores de validación y se asignan a la propiedad resultado.mensaje
            diplomado.errors.allErrors.each {
                resultado.mensaje = messageSource.getMessage(it, null)
            }
            bitacoraService.registrar([clase:"DiplomadoService", metodo:"registrar", nombre:"Registro del diplomado", descripcion:"Se registra el diplomado", estatus:"ERROR"])
        }

        if(resultado.estatus){
            resultado.mensaje = 'Diplomado creado exitosamente'
            resultado.datos = diplomado
        }

        return resultado
    }

    /**
     * Obtiene los diplomados activos con parametros de paginación y filtrado
     * @param institucionId (Opcional)
     * Identificador de la institución
     * @param search (Opcional)
     * Nombre del diplomado
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

        // Parametros de filtrado (opcionales)
        def institucionId
        if(params.institucionId){
            institucionId = Integer.parseInt(params.institucionId)
        }

        def criteria = {
            createAlias("institucion", "i", CriteriaSpecification.LEFT_JOIN)
            and{
                eq("activo", true)
                if(params.search){
                    ilike("nombre", "%${params.search}%")
                }
                if(params.institucionId){
                    eq("i.id", institucionId)
                }
            }
        }

        def diplomados = Diplomado.createCriteria().list(params,criteria)

        if(diplomados.totalCount <= 0){
            resultado.mensaje = 'No se encontraron diplomados'
            resultado.datos = diplomados
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'Diplomados consultados exitosamente'
        resultado.datos = diplomados

        return resultado
    }

    /**
     * Obtiene los diplomados activos
     * @param institucionId (Opcional)
     * Identificador de la institución
     */
    def obtenerActivos(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        // Parametros de filtrado (opcionales)
        def institucionId
        if(params.institucionId){
            institucionId = Integer.parseInt(params.institucionId)
        }

        def diplomados = Diplomado.createCriteria().list {
            createAlias("institucion", "i", CriteriaSpecification.LEFT_JOIN)
            and{
                eq("activo", true)
                if(params.institucionId){
                    eq("i.id", institucionId)
                }
            }
        }

        resultado.estatus = true
        resultado.mensaje = 'Diplomados consultados exitosamente'
        resultado.datos = diplomados

        return resultado
    }

    /**
     * Obtiene un diplomado específico
     * @param id (Requerido)
     * Identificador del diplomado
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

        def diplomado = Diplomado.get(params.id)
        if(!diplomado){
            resultado.mensaje = 'Diplomado no encontrado'
            return resultado
        }
        if(!diplomado.activo){
            resultado.mensaje = 'Diplomado inactivo'
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'Diplomado encontrado exitosamente'
        resultado.datos = diplomado

        return resultado
    }

    /**
     * Modifica los datos de un diplomado específico
     * @param id (Requerido)
     * Identificador del diplomado
     * @param nombre (Requerido)
     * Nombre del diplomado
     * @param institucionId (Requerido)
     * Identifcador de la institución
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

        def diplomado = Diplomado.get(params.id)
        if(!diplomado){
            resultado.mensaje = 'Diplomado no encontrado'
            return resultado
        }
        if(!diplomado.activo){
            resultado.mensaje = 'Diplomado inactivo'
            return resultado
        }

        // Se validan las relaciones con otras tablas
        def institucion = Institucion.get(params.institucionId)
        if(!institucion){
            resultado.mensaje = 'Institucion no encontrada'
            return resultado
        }
        if(!institucion.activo){
            resultado.mensaje = 'Institucion inactiva'
            return resultado
        }

        // Se crean copias auxiliares de los objetos antes de asignar
        // los nuevos datos para verificar si se realizó algun cambio
        def diplomadoAux = new Diplomado(diplomado.properties)
        diplomadoAux.institucion = diplomado.institucion

        // Se asignan los nuevos datos
        diplomado.properties = params
        diplomado.institucion = institucion

        // En caso de cambios se modifica el campo de ultimaActualizacion
        if(!diplomado.equals(diplomadoAux))
            diplomado.ultimaActualizacion = new Date()

        if(diplomado.save(flush:true)){
            resultado.estatus = true
            bitacoraService.registrar([clase:"DiplomadoService", metodo:"modificar", nombre:"Modificación del diplomado", descripcion:"Se modifica el diplomado", estatus:"EXITOSO"])
        }else{
            // Se recorren los errores de validación y se asignan a la propiedad resultado.mensaje
            diplomado.errors.allErrors.each {
                resultado.mensaje = messageSource.getMessage(it, null)
            }
            bitacoraService.registrar([clase:"DiplomadoService", metodo:"modificar", nombre:"Modificación del diplomado", descripcion:"Se modifica el diplomado", estatus:"ERROR"])
        }

        if(resultado.estatus){
            resultado.mensaje = 'Diplomado modificado exitosamente'
            resultado.datos = diplomado
        }

        return resultado
    }

    /**
     * Realiza una baja lógica de un diplomado específico
     * @param id (Requerido)
     * Identificador del diplomado
     */
    def eliminar(params) {
        def resultado = [
                estatus: false,
                mensaje: '',
                datos: null
        ]

        if(!params.id){
            resultado.mensaje = 'El id es un dato requerido'
            return resultado
        }

        def diplomado = Diplomado.get(params.id)
        if(!diplomado){
            resultado.mensaje = 'Curso no encontrado'
            return resultado
        }

        diplomado.activo = false

        if(!diplomado.save(flush:true)){
            // Se recorren los errores de validación y se asignan a la propiedad resultado.mensaje
            diplomado.errors.allErrors.each {
                resultado.mensaje = messageSource.getMessage(it, null)
            }
            bitacoraService.registrar([clase:"DiplomadoService", metodo:"eliminar", nombre:"Eliminación del diplomado", descripcion:"Se elimina el diplomado", estatus:"ERROR"])
            return resultado
        }
        bitacoraService.registrar([clase:"DiplomadoService", metodo:"eliminar", nombre:"Eliminación del diplomado", descripcion:"Se elimina el diplomado", estatus:"EXITOSO"])

        resultado.estatus = true
        resultado.mensaje = 'Diplomado dado de baja exitosamente'
        resultado.datos = diplomado

        return resultado
    }
}
