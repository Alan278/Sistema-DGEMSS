package sieges

import grails.gorm.transactions.Transactional
import org.hibernate.criterion.CriteriaSpecification
import java.text.SimpleDateFormat
import groovy.json.JsonSlurper

/**
 * Servicio que permite la administración de equivalencias
 * @authors Dominguez Luis, Navez Leslie
 * @since 2021
 */

@Transactional
class EquivalenciaService {
    def servletContext
    def efirmaService
    def alumnoService
    def documentoService
    def zxingService
    def numeroLetrasService
    def evaluacionService

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
     * Permite realizar el registro de una revalidación.
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

        def fechaExpedicion
        try{
            fechaExpedicion = new SimpleDateFormat("yyyy-MM-dd").parse(params.fechaExpedicion)
        }catch(ex){
            resultado.mensaje = 'La fecha no tiene un formato válido'
            return resultado
        }

        def parametros = [:]
        parametros.nivelExterno = params.nivelExterno
        parametros.nivelInterno = params.nivelInterno
        parametros.institucion = params.institucion
        parametros.cicloEscolar = params.cicloEscolar
        parametros.expediente = params.expediente
        parametros.lugarExpedicion = params.lugarExpedicion
        parametros.folio = params.folio
        parametros.cct = params.cct
        parametros.fechaExpedicion = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy").format(fechaExpedicion)

        def jsonSlurper = new JsonSlurper()
        parametros.materias = jsonSlurper.parseText(params.materias)
        parametros.numPaginas = 1

        if(parametros.materias.size() > 1){
            parametros.numAsignaturas = "las siguientes ${parametros.materias.size()} asignaturas resultan equiparables"
        }else{
            parametros.numAsignaturas = "la siguiente ${parametros.materias.size()} asignatura resulta equiparable"
        }

        // Generar código QR
        def url = "http://localhost:8080/revalidacion/consultarPorExpediente?expediente=${parametros.expediente}"
        def resultadoQr = zxingService.generarQr(url)
        if (resultadoQr.estatus) {
            parametros.qr = new ByteArrayInputStream(resultadoQr.qr)
        }

        // Generar documento
        def resultadoExpedicion = documentoService.generar("equivalencia", parametros)
        if (!resultadoExpedicion.estatus) {
            resultado.mensaje = resultadoExpedicion.mensaje
            return resultado
        }

        def equivalencia = new Equivalencia(params)
        equivalencia.pdf = resultadoExpedicion.documento

        Equivalencia.withTransaction {status ->
            if(equivalencia.save(flush:true)){
                def error = false
                parametros.materias.any{ materia ->
                    def asignatura = new AsignaturasEquivalencia()
                    asignatura.asignaturaCursada = materia.asignaturaCursada
                    asignatura.asignaturaEquivalente = materia.asignaturaEquivalente
                    asignatura.calificacion = Float.parseFloat(materia.calificacion)
                    asignatura.equivalencia = equivalencia
                    if(!asignatura.save(flush:true)){
                        asignatura.errors.allErrors.each {
                            resultado.mensaje = messageSource.getMessage(it, null)
                        }
                        error = true
                        true
                    }
                }
                if(error){
                    def asignaturas = AsignaturasEquivalencia.createCriteria().list {
                        createAlias("equivalencia", "e", CriteriaSpecification.LEFT_JOIN)
                        eq("e.id", equivalencia.id)
                    }
                    asignaturas.each{ asignatura ->
                        asignatura.delete(flush:true)
                    }
                    status.setRollbackOnly()
                }else{
                    resultado.estatus = true
                }
            }else{
                equivalencia.errors.allErrors.each {
                    resultado.mensaje = messageSource.getMessage(it, null)
                }
            }
        }

        if(!resultado.estatus){
            bitacoraService.registrar([
                clase:"EquivalenciaService",
                metodo:"registrar",
                nombre:"Registro de revalidación",
                descripcion:"Se intentó registrar una revalidación",
                estatus:"ERROR"
            ])
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'Equivalencia creado exitosamente'
        resultado.datos = equivalencia
        bitacoraService.registrar([
            clase:"EquivalenciaService",
            metodo:"registrar",
            nombre:"Registro de revalidación",
            descripcion:"Se registró una revalidación",
            estatus:"EXITOSO"
        ])

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

        def criteria = {
            and{
                eq("activo", true)
                if(params.search){
                    or{
                        ilike("expediente", "%${params.search}%")
                    }
                }
            }
        }

        def equivalencias = Equivalencia.createCriteria().list(params,criteria)

        if(equivalencias.totalCount <= 0){
            resultado.mensaje = 'No se encontraron equivalencias'
            resultado.datos = equivalencias
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'Equivalencias consultados exitosamente'
        resultado.datos = equivalencias

        return resultado
    }

    /**
     * Permite realizar la consulta del registro seleccionado con su información correspondiente.
     * @param params (requerido)
     * parametros del curso
     * @return resultado
     * resultado con el estatus, mensaje y datos del curso
     */
    def consultarPorExpediente(params) {
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        if(!params.expediente){
            resultado.mensaje = 'El expediente es un dato requerido'
            return resultado
        }

        def equivalencia = Equivalencia.findByExpediente(params.expediente)

        if(!equivalencia){
            resultado.mensaje = 'Equivalencia no encontrada'
            return resultado
        }

        if(!equivalencia.activo){
            resultado.mensaje = 'Equivalencia inactiva'
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'Equivalencia encontrada exitosamente'
        resultado.datos = equivalencia

        return resultado
    }
}