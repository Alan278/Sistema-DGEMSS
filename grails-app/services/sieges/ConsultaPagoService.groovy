package sieges

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import wslite.soap.*

@CompileStatic
class ConsultaPagoService {
    String url = 'https://www.ingresos.morelos.gob.mx/ws_myt/consultas.asmx?WSDL'
    SOAPClient client = new SOAPClient(url)

    @CompileDynamic
    def consultaPagoFolio(String serie, String folio) {
        def resultado = [
			estatus: false,
			mensaje: 'Pago no encontrado',
			datos: null
		]

        def response = client.send(SOAPAction: 'http://tempuri.org/ConsultaPagoFolio',
            """<?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                <soap:Body>
                    <ConsultaPagoFolio xmlns="http://tempuri.org/">
                        <Serie>${serie}</Serie>
                        <Folio>${folio}</Folio>
                    </ConsultaPagoFolio>
                </soap:Body>
            </soap:Envelope>"""
        )

        def textResponse = response.ConsultaPagoFolioResponse.ConsultaPagoFolioResult.text()

        def listResponse
        try{
            listResponse = new XmlSlurper().parseText(textResponse)

            def datosPago = [
                folio: listResponse.Pago.FolioPago.text(),
                lineaCaptura: listResponse.Poliza.LineaCaptura.text(),
                rfc: listResponse.Contribuyente.RFC.text(),
                nombre: listResponse.Contribuyente.Nombre.text(),
                razonSocial: listResponse.Contribuyente.RazonSocial.text(),
                fechaPago: listResponse.Pago.FechaPago.text(),
                horaPago: listResponse.Pago.HoraPago.text(),
                conceptos: []
            ]

            listResponse?.Tramites?.children()?.each { tramite ->
                tramite?.Conceptos?.children()?.each { concepto ->
                    def conceptoAux = [
                        idConcepto: concepto?.'@IdConcepto'?.toString()?.trim()?:null,
                        descripcion: concepto?.'@Descripcion'?.toString()?.trim()?:null,
                        monto: Double.parseDouble(concepto?.'@Monto'?.toString()?.trim()?:"0")
                    ]
                    datosPago.conceptos << conceptoAux
                }
            }

            resultado.datos = datosPago
            resultado.estatus = true
            resultado.mensaje = "Pago consultado exitosamente"
        }catch(Exception ex){
            print(ex)
        }

        return resultado
    }

    @CompileDynamic
    def consultaPago(String lineaCaptura) {
        def resultado = [
			estatus: false,
			mensaje: 'Pago no encontrado',
			datos: null
		]

        def response = client.send(SOAPAction: 'http://tempuri.org/ConsultaPago',
            """<?xml version="1.0" encoding="utf-8"?>
            <soap12:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap12="http://www.w3.org/2003/05/soap-envelope">
            <soap12:Body>
                <ConsultaPago xmlns="http://tempuri.org/">
                <LineaCaptura>${lineaCaptura}</LineaCaptura>
                </ConsultaPago>
            </soap12:Body>
            </soap12:Envelope>"""
        )

        def textResponse = response.ConsultaPagoResponse.ConsultaPagoResult.text()

        def listResponse
        try{
            listResponse = new XmlSlurper().parseText(textResponse)

            def datosPago = [
                pagado: listResponse.Pago.Pagado.text(),
                folio: listResponse.Pago.FolioPago.text(),
                lineaCaptura: listResponse.Poliza.LineaCaptura.text(),
                rfc: listResponse.Contribuyente.RFC.text(),
                nombre: listResponse.Contribuyente.Nombre.text(),
                razonSocial: listResponse.Contribuyente.RazonSocial.text(),
                fechaPago: listResponse.Pago.FechaPago.text(),
                horaPago: listResponse.Pago.HoraPago.text(),
                conceptos: []
            ]

            listResponse?.Tramites?.children()?.each { tramite ->
                tramite?.Conceptos?.children()?.each { concepto ->
                    def conceptoAux = [
                        idConcepto: concepto?.'@IdConcepto'?.toString()?.trim()?:null,
                        descripcion: concepto?.'@Descripcion'?.toString()?.trim()?:null,
                        monto: Double.parseDouble(concepto?.'@Monto'?.toString()?.trim()?:"0")
                    ]
                    datosPago.conceptos << conceptoAux
                }
            }

            if(!datosPago.lineaCaptura){
                return resultado
            }

            resultado.datos = datosPago
            resultado.estatus = true
            resultado.mensaje = "Pago consultado exitosamente"
        }catch(Exception ex){
            return resultado
        }

        return resultado
    }
}
