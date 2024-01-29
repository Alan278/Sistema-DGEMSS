package sieges

import grails.gorm.transactions.Transactional
import java.text.SimpleDateFormat

/**
 * Servicio que permite la administración de revalidaciones
 * @authors Dominguez Luis, Navez Leslie
 * @since 2021
 */

@Transactional
class RevalidacionService {
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

        def fechaTermino
        try{
            fechaTermino = new SimpleDateFormat("yyyy-MM-dd").parse(params.fechaTermino)
        }catch(ex){
            resultado.mensaje = 'La fecha de término no tiene un formato válido'
            return resultado
        }
        
        def fechaExpedicion
        try{
            fechaExpedicion = new SimpleDateFormat("yyyy-MM-dd").parse(params.fechaExpedicion)
        }catch(ex){
            resultado.mensaje = 'La fecha no tiene un formato válido'
            return resultado
        }

        def parametros = [:]
        parametros.alumno = params.alumno
        parametros.nivelExterno = params.nivelExterno
        parametros.nivelInterno = params.nivelInterno
        parametros.institucion = params.institucion
        parametros.cicloEscolar = params.cicloEscolar
        parametros.estado = params.estado
        parametros.pais = params.pais
        parametros.expediente = params.expediente
        parametros.lugarExpedicion = params.lugarExpedicion
        parametros.folio = params.folio
        parametros.fechaTermino = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy").format(fechaTermino)
        parametros.fechaExpedicion = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy").format(fechaExpedicion)
        parametros.numPaginas = 1

        // Generar código QR
        def url = "http://localhost:8080/revalidacion/consultarPorExpediente?expediente=${parametros.expediente}"
        def resultadoQr = zxingService.generarQr(url)
        if (resultadoQr.estatus) {
            parametros.qr = new ByteArrayInputStream(resultadoQr.qr)
        }

        // Generar documento
        def resultadoExpedicion = documentoService.generar("revalidacion", parametros)

        if (!resultadoExpedicion.estatus) {
            resultado.mensaje = resultadoExpedicion.mensaje
            return resultado
        }

        def revalidacion = new Revalidacion(params)
        revalidacion.pdf = resultadoExpedicion.documento

        if(!revalidacion.save(flush:true)){
            revalidacion.errors.allErrors.each {
                resultado.mensaje = messageSource.getMessage(it, null)
            }
            bitacoraService.registrar([
                clase:"RevalidacionService",
                metodo:"registrar",
                nombre:"Registro de revalidación",
                descripcion:"Se intentó registrar una revalidación",
                estatus:"ERROR"
            ])
        }

        resultado.estatus = true
        resultado.mensaje = 'Revalidacion creada exitosamente'
        resultado.datos = revalidacion
        bitacoraService.registrar([
            clase:"RevalidacionService",
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

        def revalidaciones = Revalidacion.createCriteria().list(params,criteria)

        if(revalidaciones.totalCount <= 0){
            resultado.mensaje = 'No se encontraron revalidaciones'
            resultado.datos = revalidaciones
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'Revalidaciones consultados exitosamente'
        resultado.datos = revalidaciones

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

        def revalidacion = Revalidacion.findByExpediente(params.expediente)

        if(!revalidacion){
            resultado.mensaje = 'Revalidación no encontrada'
            return resultado
        }

        if(!revalidacion.activo){
            resultado.mensaje = 'Revalidación inactiva'
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'Revalidación encontrada exitosamente'
        resultado.datos = revalidacion

        return resultado
    }
}