package sieges

import groovy.xml.MarkupBuilder
import org.apache.commons.io.FileUtils

class XmlService {

    def generar(parametros){
        def resultado = [
            estatus: false,
            mensaje: '',
            documento: null
        ]

        try {
            def archivoXml = new File("archivo.xml")

            def xmlWriter = new StringWriter()
            def xml = new MarkupBuilder(xmlWriter)

            xml.setDoubleQuotes(true)
            xml.setEscapeAttributes(true)
            xml.mkp.xmlDeclaration(version: "1.0", encoding: "UTF-8")

            xml."Dec"(
                "xmlns": "https://www.siged.sep.gob.mx/certificados/",
                "version": "2.0",
                "tipoCertificado": 5,
                "folioControl": parametros.folioControl,
                "sello": parametros.firmanteEscuelaSello,
                "certificadoResponsable": parametros.firmanteEscuelaCer,
                "noCertificadoResponsable": parametros.firmanteEscuelaNoSerie
            ) {
                "Ipes"(
                    "idNombreInstitucion": parametros.institucionId,
                    "nombreInstitucion": parametros.institucion,
                    "claveCt": parametros.institucionClaveCt,
                    "idCampus": "",
                    "campus": "",
                    "idEntidadFederativa": 17,
                    "entidadFederativa": "MORELOS",
                ) {
                    "Responsable"(
                        "curp": parametros.firmanteEscuelaCurp,
                        "nombre": parametros.firmanteEscuelaNombre,
                        "primerApellido": parametros.firmanteEscuelaPrimerApellido,
                        "segundoApellido": parametros.firmanteEscuelaSegundoApellido,
                        "idCargo": "",
                        "cargo": parametros.firmanteEscuelaCargo
                    )
                }
                "Rvoe"(
                    "numero": parametros.rvoe,
                    "fechaExpedicion": parametros.fechaRvoeCadena
                )
                "Carrera"(
                    "idCarrera": "",
                    "claveCarrera": "",
                    "nombreCarrera": parametros.carrera,
                    "idTipoPeriodo": "",
                    "tipoPeriodo": "",
                    "clavePlan": "",
                    "idNivelEstudios": "",
                    "nivelEstudios": parametros.carreraNivel,
                    "calificacionMinima": parametros.calificacionMinima,
                    "calificacionMaxima": parametros.calificacionMaxima,
                    "calificacionMinimaAprobatoria": parametros.calificacionMinimaAprobatoria,
                )
                "Alumno"(
                    "numeroControl": parametros.matricula,
                    "curp": parametros.curp,
                    "nombre": parametros.nombre,
                    "primerApellido": parametros.primerApellido,
                    "segundoApellido": parametros.segundoApellido,
                    "idGenero": "",
                    "fechaNacimiento": parametros.fechaNacimiento,
                    "foto": "",
                    "firmaAutografa": "",
                )
                "Expedicion"(
                    "idTipoCertificacion": parametros.tipoCertificado.equals('TOTAL') ? 79 : 80,
                    "tipoCertificacion": parametros.tipoCertificado,
                    "fecha": parametros.firmanteEscuelaFecha,
                    "idLugarExpedicion": "",
                    "lugarExpedicion": "CUERNAVACA MOR.",
                )
                "Asignaturas"(
                    "total": parametros.numeroAsignaturas,
                    "asignadas": parametros.numeroAsignaturasAcreditadas,
                    "promedio": parametros.promedio,
                    "totalCreditos": "",
                    "creditosObtenidos": ""
                ) {
                    parametros.materias.each{ asignatura ->
                        "Asignatura"(
                            "idAsignatura": "",
                            "claveAsignatura": asignatura.clave,
                            "nombre": asignatura.nombre,
                            "ciclo": asignatura.ciclo,
                            "calificacion": asignatura.calificacion,
                            "idObservaciones": "",
                            "observaciones": "",
                            "idTipoAsignatura": "",
                            "tipoAsignatura": "",
                            "creditos": "",
                        )
                    }
                }
                "Dreoe"(
                    "fechaDreoe": parametros.firmanteDgemssFechaCadena,
                    "selloDec": parametros.firmanteEscuelaSello,
                    "noCertificadoDreoe": parametros.firmanteDgemssNoSerie ,
                    "curp": parametros.firmanteDgemssCurp,
                    "nombreCompleto": parametros.firmanteDgemssNombreCompleto,
                    "idCargo": "",
                    "cargo": parametros.firmanteDgemssCargo,
                    "selloDreoe": parametros.firmanteDgemssSello,
                )
                "SepIPes"(
                    "version": "",
                    "folioDigital": "",
                    "fechaSepIpes": "",
                    "selloDreoe": "",
                    "noCertificadoSepIpes": "",
                    "selloSepIpes": "",
                )
                
            }

            archivoXml.withWriter("UTF-8") { writer ->
                writer.write(xmlWriter.toString())
            }

            resultado.estatus = true
            resultado.mensaje = "Documento generado exitosamente"
            resultado.documento = FileUtils.readFileToByteArray(archivoXml)

        } catch (Exception ex) {
            ex.printStackTrace()
            resultado.mensaje = "No se pudo generar el documento xml: ${ex.getCause()}"
        }

        return resultado
    }
}
