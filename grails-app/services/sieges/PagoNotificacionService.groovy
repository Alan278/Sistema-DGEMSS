package sieges

import grails.gorm.transactions.Transactional
import org.hibernate.criterion.CriteriaSpecification
import groovy.json.JsonSlurper
import java.text.SimpleDateFormat
import groovy.sql.Sql

/**
 * @author Alan Guevarin
 * @since 2022
 */

@Transactional
class PagoNotificacionService {
 /**
     * Inyección de messageSource el cual contiene los mensajes para las validaciones de los atributos de la clase de
     dominio del pago
     */
    def messageSource
    /**
     * Inyección de bitacoraService que contiene la lógica de administración de la bitácora
     */
    def bitacoraService
    def consultaPagoService
    def notificacionService
    def dataSource
    def tipoTramiteService
    def pagoService
    def institucionService

    /**
     * Registra un nuevo trámite
     * @params institucionId (requerido)
     * @params numero (requerido)
     * @params serie (requerido)
     * @params folio (requerido)
     * @params concepto (requerido)
     * @params fechaPago (requerido)
     * @params horaPago (requerido)
     * @params fechRecepcion (requerido)
     * @params cantidad (requerido)
     * @params importe (requerido)
     */
    def registrar(params){
        def resultado = [
                estatus: false,
                mensaje: '',
                datos: null
        ]

        def datosBitacora = [
            clase: "pagoNotificacionService",
            metodo: "registrar",
            nombre: "Registro de tramite de notificacion",
            descripcion: "Registro de un nuevo trámite",
            estatus: "ERROR"
        ]

        if(!params.lineaCaptura && !params.serie && !params.folio){
            resultado.mensaje = "La linea de captura o el folio son un dato requerido"
            return resultado
        }

        def resultadoOperacion = pagoService.consultarPago(params, tipoTramiteService.NOTIFICACION)
        if(!resultadoOperacion.estatus){
            resultado.mensaje = resultadoOperacion.mensaje
            return resultado
        }

        def datosPago = resultadoOperacion.datos

        def institucion = institucionService.obtener(params.institucionId)
        if(!institucion){
            resultado.mensaje = 'Institución no encontrada'
            return resultado
        }

        // Se validan las certificados
        if(!params.notificaciones){
            resultado.mensaje = 'Las notificaciones son un dato requerido'
            return resultado
        }

        def pago = new Pago()
        pago.horaPago = datosPago.horaPago
        pago.fechaPago = new SimpleDateFormat("dd/MM/yyyy").parse(datosPago.fechaPago)
        pago.lineaCaptura = datosPago.lineaCaptura
        pago.folio = datosPago.folio
        pago.fechaRecepcion = new SimpleDateFormat("yyyy-MM-dd").parse(params.fechaRecepcion)
        pago.idConcepto = datosPago.conceptoValido.idConcepto
        pago.concepto = datosPago.conceptoValido.descripcion
        pago.importe = datosPago.conceptoValido.monto

        // Se calcula el precio del certificado
        def uma = Uma.get(1)
        def tipoTramite = tipoTramiteService.obtener(tipoTramiteService.NOTIFICACION)
        def precioNotificacion = tipoTramite.costoUmas * uma.valor

        def numNotificacionesPagados = Math.floor(pago.importe / precioNotificacion)

        def notificacionesId = []
        try{

            def jsonSlurper = new JsonSlurper()
            notificacionesId = jsonSlurper.parseText(params.notificaciones)

            if(!notificacionesId){
                resultado.mensaje = 'Los notificaciones son un dato requerido'
                return resultado
            }

            if(notificacionesId.size > numNotificacionesPagados){
                resultado.mensaje = "Seleccione ${numNotificacionesPagados} notificaciones"
                return resultado
            }

            for(notificacionId in notificacionesId){
                def notificacion = NotificacionProfesional.get(notificacionId)
                if(!notificacion){
                    resultado.mensaje = "No se encontró el notificacion"
                    return resultado
                }
                if(!notificacion.activo){
                    resultado.mensaje = "El notificacion no se encuentra activo"
                    return resultado
                }
                if(notificacion.tramite != null){
                    resultado.mensaje = "El notificacion ya cuenta con un tramite asignado"
                    return resultado
                }
                notificacion.alumno.ciclosEscolares.each{ ciclo ->
                    if(ciclo.cicloEscolar.tramite == null){
                        resultado.mensaje = "Tiene ciclos con pagos pendientes"
                        return resultado
                    }
                }
            }
        }catch(Exception ex){
            resultado.mensaje = "Error en formato de notificaciones ${ex}"
            return resultado
        }

        def tramite = new Tramite()
        tramite.numeroTramite = params.numeroTramite
        tramite.institucion = institucion
        tramite.tipoTramite = TipoTramite.get(1)

        pago.addToTramites(tramite)

        if(!pago.save(flush:true)){
			// Se recorren los errores de validación y se asignan a la propiedad resultado.mensaje
			pago.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar(datosBitacora)
			return resultado
		}

        // Se asigna el tramite a los certificados
		notificacionesId.each{ notificacionId ->
			def notificacion = NotificacionProfesional.get(notificacionId)
			notificacion.tramite = tramite
			notificacion.estatusNotificacion = EstatusNotificacion.get(notificacionService.EN_REVISION)
            notificacion.save()
		}

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = 'Trámite creado exitosamente'
		resultado.datos = tramite

        return resultado
    }

    /**
     * Permite generar el listado de los tramites que se encuentran registrados, además de permitir realizar la
     * busqueda por número de trámite y el filtrado a través de la institución
     * @params params (requerido)
     * parametros del trámite
     * @param institucionId (requerido)
     * id de la institución
     * @return resultado
     * resultado con el estatus, mensaje y datos del trámite
     */
    def listar(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        //Parametros de la paginación
        if(!params.max) params.max = 50
        if(!params.offset) params.offset = 0
        if(!params.sort) params.sort = 'id'
        if(!params.order) params.order = 'asc'

        def institucionId

        if(params.institucionId){
            institucionId. Integer.parseInt(params.institucionId)
        }

        def criteria = {
            createAlias("institucion", "i", CriteriaSpecification.LEFT_JOIN)
            createAlias("tipoTramite", "t", CriteriaSpecification.LEFT_JOIN)
            and{
                eq("activo", true)
                eq("i.externa",false)
                eq("t.id", 1)
                if(params.search){
                    iLike("numero","%${params.search}%")
                }
            }
        }

        def tramite = Tramite.createCriteria().list(params,criteria)

        if(tramite.totalCount <= 0){
            resultado.mensaje = 'No se encontraron tramites'
            resultado.datos = tramite
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'Trámites consultados exitosamente'
        resultado.datos = tramite

        return resultado

    }

    /**
     * Permite realizar la consulta del registro seleccionado con su información correspondiente.
     * @params id (requerido)
     * Identificador del trámite
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

        def tramite = Tramite.get(params.id)

        if(!tramite){
            resultado.mensaje = 'Trámite no encontrado'
            return resultado
        }

        if(!tramite.activo){
            resultado.mensaje = 'Trámite inactivo'
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'Trámite encontrado exitosamente'
        resultado.datos = tramite

        return resultado
    }

    /**
     * Permite Modificar los datos de un trámite específico.
     * @params id (requerido)
     * Identificador del trámite
     * @params numero (requerido)
     * @params serie (requerido)
     * @params folio (requerido)
     * @params concepto (requerido)
     * @params fechaPago (requerido)
     * @params horaPago (requerido)
     * @params fechaRecepcion (requerido)
     * @params cantidad (requerido)
     * @params importe (requerido)
     */

    def modificar (){
        def resultado = [
           estatus: false,
           mensaje: '',
           datos: null
        ]

        if(!params.id){
            resultado.mensaje = 'El id es un dato requerido'
            return resultado
        }

        def tramite = Tramite.get(params.id)
        if(!tramite){
            resultado.mensaje = 'Trámite no encontrado'
            return resultado
        }
        if(!tramite.activo){
            resultado.mensaje = 'Trámite inactivo'
            return resultado
        }

        //Se crean las copias auxiliares de los objetos antes de asignar
        //los nuevos datos para verificar si se realizó algún cambio

        def tramiteAux = new Tramite(tramite.properties)
        def pagoAux = new Pago(tramite.pago.properties)

        def institucion = Institucion.get(params.institucionId)

        if(!institucion){
            resultado.mensaje = 'Institución no encontrada'
            return resultado
        }

        if(!institucion.activo){
            resultado.mensaje = 'Institución inactica'
            return resultado
        }

        tramite.properties = params
        tramite.pago.properties = params
        tramite.pago.fechaPago = new SimpleDateFormat("yyyy-MM-dd").parse(params.fechaPago)
        tramite.pago.fechaRecepcion = new SimpleDateFormat("yyyy-MM-dd").parse(params.fechaRecepcion)
        tramite.institucion = institucion


        //En caso de cambios se modifica el campo de ultimaActualizacion
        if(!tramite.equals(tramiteAux))
            tramite.ultimaActualizacion = new Date()
        if(!tramite.pago.equals(pagoAux))
            tramite.pago.ultimaActualizacion = new Date()

        //Se inicia una transacción para deshacer los cambios en el pago
        //en caso de ocurrir algun problema con el guardado del trámite
        Pago.withTransaction {status ->
            if(tramite.pago.save(flush: true)){
                if(tramite.save(flush: true)){
                    resultado.estatus = true
                    bitacoraService.registrar([clase: "PagoNotificacionService", metodo:"modificar", nombre:"Modificacion de tramite de certificación", descripcion:"Se modifica el tramite", estatus: "EXITOSO"])
                }else{
                    status.setRollbackOnly()
                    tramite.errors.allErrors.each {
                        resultado.mensaje = messageSource.getMessage(it, null)
                    }
                    bitacoraService.registrar([clase: "PagoNotificacionService", metodo: "modificar", nombre:"Modificacion de tramite de certificación", descripcion: "Se modifica el tramite", estatus:"ERROR"])
                }
            }
        }

        if(resultado.estatus) {
            resultado.mensaje = "Tramite modificado exitosamente"
            resultado.datos = tramite
        }
        return resultado

    }

    /**
     * Realiza una baja lógica de un trámite específico
     * @param id (Requerido)
     * Identificador del trámite
     */

    def eliminar(params){
        def resultado = [
           estatus: flase,
           mensaje: '',
           datos: null
        ]

        if(params.id){
            resultado.mensaje = 'El id es un dato requerido'
            return resultado
        }

        def tramite = Tramite.get(params.id)

        if(!tramite){
            resultado.mensaje = 'Trámite no encontrado'
            return resultado
        }

        tramite.activo = false

        if(!tramite.save(flush:true)){
            //Se recorren los errores de validación y se asignan a la propiedad resultado.mensaje
            tramite.errors.allErrors.each{
                resultado.mensaje = messageSource.getMessage(it, null)
            }
            bitacoraService.registrar([clase:"PagoNotificacionService", metodo: "eliminar", nombre:"Eliminación del tramite", descripcion:"Se elimina el tramite", estatus:"EXITOSO"])
            return resultado
        }
        bitacoraService([clase:"PagoNotificacionService", metodo:"eliminar", nombre:"Eliminación del tramite", descripcion:"Se elimina el tramite", estatus:"ERROR"])

        resultado.estatus = true
        resultado.mensaje = 'Trámite dado de baja exitosamente'
        resultado.datos = tramite

        return resultado

    }

    /**
     * Obtiene los tramites con certificados pendientes por firmar
     * @param id (Requerido)
     * Identificador del trámite
     */
    def obtenerTramitesPendientes(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        //Parametros de la paginación
        if(!params.max) params.max = 50
        if(!params.offset) params.offset = 0
        if(!params.sort) params.sort = 'id'
        if(!params.order) params.order = 'asc'

        def institucionId

        if(params.institucionId){
            institucionId. Integer.parseInt(params.institucionId)
        }

        def sql = new Sql(dataSource)
        def tramite = sql.rows("SELECT tramite.* FROM tramite INNER JOIN certificado ON tramite.id = certificado.tramite_id WHERE certificado.estatus_certificado_id = 7 GROUP BY tramite.id;")

        resultado.estatus = true
        resultado.mensaje = 'Trámites consultados exitosamente'
        resultado.datos = tramite[0]

        return resultado
    }
}
