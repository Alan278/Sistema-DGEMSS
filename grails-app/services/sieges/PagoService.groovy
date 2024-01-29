package sieges

import grails.gorm.transactions.Transactional

@Transactional
class PagoService {
    def consultaPagoService
    def tipoTramiteService

    def consultarPago(params, tipoTramiteId){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def idConceptoValido = tipoTramiteService.obtener(tipoTramiteId)?.idConcepto

        if(params.lineaCaptura){
            if(existePagoConLineaCaptura(params.lineaCaptura, idConceptoValido)){
                resultado.mensaje = "El pago ya se encuentra registrado en un trámite"
                return resultado
            }
            resultado = consultaPagoService.consultaPago(params.lineaCaptura)
        }else{
            if(params.serie && params.folio){
                if(existePagoConFolio(params.serie, params.folio, idConceptoValido)){
                    resultado.mensaje = "El pago ya se encuentra registrado en un trámite"
                    return resultado
                }
                resultado = consultaPagoService.consultaPagoFolio(params.serie, params.folio)
            }else{
                return resultado
            }
        }

        if(!resultado.estatus) return resultado

        def conceptoValido = false
        resultado.datos.conceptos.each { concepto ->
            println(concepto.idConcepto)
            println(idConceptoValido)
            if(concepto.idConcepto.equals(idConceptoValido)){
                conceptoValido = true
                resultado.datos.conceptoValido = concepto
            }
        }

        if(!conceptoValido){
            resultado.estatus = false
            resultado.mensaje = "El concepto de pago no concuerda con el tipo de solicitud."
            resultado.datos = null
        }

        return resultado
    }

    /**
     * Permite saber si ya existe un pago registrado con un folio específico
     * @param folio (requerido)
     * folio del pago
     * @return Boolean
     */
    def existePagoConFolio(serie, folio, idConcepto){
        folio = "${serie}-${folio}"
        def pago = Pago.findByFolioAndIdConceptoAndActivo(folio, idConcepto, true)
        if(!pago) return false

        return true
    }

    /**
     * Permite saber si ya existe un pago registrado con una linea de captura específica
     * @param lineaCaptura (requerido)
     * lineaCaptura del pago
     * @return Boolean
     */
    def existePagoConLineaCaptura(lineaCaptura, idConcepto){
        def pago = Pago.findByLineaCapturaAndIdConceptoAndActivo(lineaCaptura, idConcepto, true)
        if(!pago) return false

        return true
    }
}
