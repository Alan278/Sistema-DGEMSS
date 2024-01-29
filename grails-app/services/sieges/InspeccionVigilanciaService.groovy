package sieges

import grails.gorm.transactions.Transactional
import java.text.SimpleDateFormat
import org.hibernate.criterion.CriteriaSpecification
import groovy.json.JsonSlurper

/**
 * @author Alan Guevarin
 * @since 2023
 */
@Transactional
class InspeccionVigilanciaService {
	def messageSource
	def carreraService
	def bitacoraService
	def efirmaService
	def usuarioService
	def consultaPagoService
	def institucionService
    def cicloEscolarService
    def tipoTramiteService
    def pagoService

	def registrar(params) {
		def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

		def datosBitacora = [
            clase: "InspeccionVigilanciaService",
            metodo: "registrar",
            nombre: "Registro de un pago",
            descripcion: "Registro de un nuevo pago",
            estatus: "ERROR"
        ]

        if(!params.lineaCaptura && !params.serie && !params.folio){
            resultado.mensaje = "La linea de captura o el folio son un dato requerido"
            return resultado
        }

        def resultadoOperacion = pagoService.consultarPago(params, tipoTramiteService.INSPECCION_VIGILANCIA)
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

        // Se validan las certificados de para su pago
        if(!params.ciclosEscolares){
            resultado.mensaje = 'Los ciclos son un dato requerido'
            return resultado
        }

        // Se calcula el precio del certificado
        def uma = Uma.get(1)
        def tipoTramite = tipoTramiteService.obtener(tipoTramiteService.INSPECCION_VIGILANCIA)
        def precioInspeccionVigilancia = tipoTramite.costoUmas * uma.valor

        def numCiclosPagados = Math.floor(datosPago.conceptoValido.monto / precioInspeccionVigilancia)

        def ciclosId = []
        try{

            def jsonSlurper = new JsonSlurper()
            ciclosId = jsonSlurper.parseText(params.ciclosEscolares)

            if(!ciclosId){
                resultado.mensaje = 'Los ciclos son un dato requerido'
                return resultado
            }

            if(ciclosId.size > numCiclosPagados){
                resultado.mensaje = "Seleccione ${numCiclosPagados.toInteger()} ciclos"
                return resultado
            }

            for(cicloId in ciclosId){
                def ciclo = cicloEscolarService.obtener(cicloId)
                if(!ciclo){
                    resultado.mensaje = "Ciclo escolar no encontrado"
                    return resultado
                }
                if(ciclo.tramite != null){
                    resultado.mensaje = "El ciclo escolar ya cuenta con un trámite asignado"
                    return resultado
                }
            }
        }catch(Exception ex){
            resultado.mensaje = "Error en formato de ciclos ${ex}"
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

        if(!pago.save(flush:true)){
			// Se recorren los errores de validación y se asignan a la propiedad resultado.mensaje
			pago.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
            resultado.mensaje = "Error"
			bitacoraService.registrar(datosBitacora)
			return resultado
		}

        def tramite = new Tramite()
        tramite.numeroTramite = params.numeroTramite
        tramite.institucion = institucion
        tramite.pago = pago
        tramite.tipoTramite = TipoTramite.get(2)

        if(!tramite.save(flush:true)){
			// Se recorren los errores de validación y se asignan a la propiedad resultado.mensaje
			tramite.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar(datosBitacora)
            resultado.mensaje = "Error"
			return resultado
		}

		 // Se asigna el tramite a los certificados
		for(cicloId in ciclosId){
			def cicloEscolar = CicloEscolar.get(cicloId)
			cicloEscolar.tramite = tramite
            cicloEscolar.save()
		}

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = 'Trámite registrado exitosamente'
		resultado.datos = pago

        return resultado
	}


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

        def criteria = {
            createAlias("tipoTramite", "t", CriteriaSpecification.LEFT_JOIN)
            createAlias("pago", "p", CriteriaSpecification.LEFT_JOIN)
            createAlias("institucion", "i", CriteriaSpecification.LEFT_JOIN)
            and{
                eq("activo", true)
                isNotNull("pago")
                eq("t.id", 2)
                if(params.institucionId) eq("i.id", params.institucionId.toInteger())
				if(params.search){
					or{
                    	ilike("p.folio", "%${params.search}%")
                    	ilike("numeroTramite", "%${params.search}%")
					}
                }
            }
        }

        def ciclosEscolares = Tramite.createCriteria().list(params, criteria)

        if(ciclosEscolares.totalCount <= 0){
            resultado.mensaje = 'No se encontraron ciclos escolares pagados'
            resultado.datos = ciclosEscolares
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'Ciclos escolares consultados exitosamente'
        resultado.datos = ciclosEscolares

        return resultado
    }

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

        def tramite = Tramite.get(params.id)

        if(!tramite){
            resultado.mensaje = 'Tramite no encontrado'
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'Tramite encontrado exitosamente'
        resultado.datos = tramite

        return resultado
    }
}
