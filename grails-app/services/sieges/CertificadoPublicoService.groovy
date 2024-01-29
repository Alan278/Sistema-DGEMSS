package sieges

import grails.gorm.transactions.Transactional
import java.text.SimpleDateFormat
import org.hibernate.criterion.CriteriaSpecification

/**
 * Servicio que permite la generación de certificados públicos totales y parciales
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */
@Transactional
class CertificadoPublicoService {

    def evaluacionService
    def numeroLetrasService
    def zxingService
    def efirmaService

    // Id de los EstatusCertificado
    final GENERADO = 1
    final FIRMANDO_ESCUELA = 2
    final RECHAZADO_DIRECTOR = 3
    final EN_ESPERA = 4
    final EN_REVISION = 5
    final RECHAZADO_DGEMSS = 6
    final FIRMANDO_DGEMSS = 7
    final RECHAZADO_AUTENTICADOR = 8
    final FINALIZADO = 9


    def obtenerParametros(certificado){
        def alumno = certificado.alumno
        def persona = alumno.persona
        def carrera = alumno.planEstudios.carrera
        def planEstudios = alumno.planEstudios
        def institucion = carrera.institucion

        def evaluaciones = Evaluacion.createCriteria().list {
            createAlias("asignatura", "as", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno", "a", CriteriaSpecification.LEFT_JOIN)
            createAlias("cicloEscolar", "c", CriteriaSpecification.LEFT_JOIN)
            and{
                eq("activo", true)
                eq("a.id", alumno.id)
                le("dateCreated", certificado.fechaRegistro)
            }
            order("as.orden", "asc")
        }

        def ciclosEscolares = Evaluacion.createCriteria().list {
            createAlias("alumno", "a", CriteriaSpecification.LEFT_JOIN)
            createAlias("cicloEscolar", "c", CriteriaSpecification.LEFT_JOIN)
            and{
                eq("activo", true)
                eq("a.id", alumno.id)
                le("fechaRegistro", certificado.fechaRegistro)
            }
            order("c.fechaFin", "desc")
        }

        def asignaturas = Asignatura.createCriteria().list {
            createAlias("planEstudios", "p", CriteriaSpecification.LEFT_JOIN)
            and{
                eq("activo", true)
                eq("p.id", planEstudios.id)
            }
        }

        // formateadores de fecha
        def formatoFechaXml = new SimpleDateFormat("YYYY-MM-dd'T'hh:mm:ss")
        def formatoFechaPdf = new SimpleDateFormat("dd/MM/yyyy")

        def parametros = [:]
            // alumno
        parametros.matricula = alumno.matricula.trim().toUpperCase()
            // persona
        parametros.curp = persona.curp.trim().toUpperCase()
        parametros.nombre = persona.nombre.trim().toUpperCase()
        parametros.primerApellido = persona.primerApellido.trim().toUpperCase()
        parametros.segundoApellido = persona.segundoApellido ? persona.segundoApellido.trim().toUpperCase() : ""
        parametros.fechaNacimiento = formatoFechaPdf.parse(persona.fechaNacimiento)
        parametros.fechaNacimiento = formatoFechaXml.format(parametros.fechaNacimiento)
        parametros.fechaNacimiento = parametros.fechaNacimiento.trim().toUpperCase()
            //institucion
        parametros.institucionId = institucion.id
        parametros.institucion = institucion.nombre.trim().toUpperCase()
        parametros.institucionMunicipio = institucion.domicilio.municipio.trim().toUpperCase()
        parametros.institucionClaveCt = institucion.claveCt ? institucion.claveCt.trim().toUpperCase() : ""
            //carrera
        parametros.carrera = carrera.nombre.trim().toUpperCase()
        parametros.modalidad = carrera.modalidad ? carrera.modalidad.nombre.trim().toUpperCase() : ""
            //asignaturas
        parametros.numeroAsignaturas = asignaturas.size
        parametros.tipoCertificadoRedaccion = evaluaciones.size >= asignaturas.size ? "la TOTALIDAD" : (ciclosEscolares.size + " SEMESTRES")
        parametros.tipoCertificado = evaluaciones.size >= asignaturas.size ? "TOTAL" : "PARCIAL"
            //evaluaciones
        parametros.promedio = evaluacionService.calcularPromedio(evaluaciones)
        parametros.promedio = parametros.promedio == 10 ? 10 : String.format("%.01f", parametros.promedio)
            
            //certificado
        parametros.foto = new ByteArrayInputStream(certificado.foto)
        parametros.uuid = certificado.uuid
        parametros.folioControl = certificado.folioControl
        parametros.libro = certificado.libro ? certificado.libro : ""
        parametros.foja = certificado.foja ? certificado.foja : ""
        parametros.numero = certificado.numero ? certificado.numero : "" 
        parametros.municipio = certificado.municipio.trim().toUpperCase()
        parametros.fechaUltimaAcreditacion = formatoFechaPdf.format(ciclosEscolares[0].cicloEscolar.fechaFin)
        parametros.fechaUltimaAcreditacionCadena = formatoFechaXml.format(ciclosEscolares[0].cicloEscolar.fechaFin)
        parametros.fechaUltimaAcreditacionCadena = parametros.fechaUltimaAcreditacionCadena.trim().toUpperCase()

        parametros.numPaginas = 2
        parametros.numPlantilla = evaluaciones.size() > 45 ? 2 : 1
        parametros.numeroAsignaturasAcreditadas = 0
        parametros.materias = []
        evaluaciones.each{ evaluacion ->
            // Se define el nombre de la asignatura
            def nombre = evaluacion.asignatura.nombre.trim().toUpperCase()
            if(evaluacion.extraordinaria) nombre += " *"
            
            // Se define la calificación
            def calificacion 
            def calificacionLetra
            if(evaluacion.asignatura.formacion.tipoFormacion.nombre.equals("BASICA")){
                calificacion = evaluacion.calificacion
                if(evaluacion.calificacion == 10) calificacion = 10
                calificacionLetra = numeroLetrasService.convertir(String.valueOf(evaluacion.calificacion), true)
                if(calificacion >= 6){
                    parametros.numeroAsignaturasAcreditadas += 1
                }
            }else{
                calificacion = evaluacion.aprobada ? "A" : ""
                calificacionLetra = evaluacion.aprobada ? "ACREDITADA" : ""
            }

            def evaluacionAux = [
                clave: evaluacion.asignatura.clave ? evaluacion.asignatura.clave.toUpperCase() : "",
                nombre: nombre,
                ciclo: evaluacion.cicloEscolar.nombre.trim().toUpperCase(),
                calificacion: calificacion,
                calificacionLetra: calificacionLetra.trim().toUpperCase()
            ]
            parametros.materias.add(evaluacionAux)
        }

        if(certificado.estatusCertificado.id == FINALIZADO){
            //datos firmante escuela
            def datosCerDirector = certificado.firmaDirectorEscuela.firmaElectronica

            parametros.firmanteEscuelaSello = certificado.firmaDirectorEscuela.selloDigital
            parametros.firmanteEscuelaCer = efirmaService.bytesToBase64(datosCerDirector.archivoCer)
            parametros.firmanteEscuelaNoSerie = datosCerDirector.numeroSerieCer.trim().toUpperCase()
            parametros.firmanteEscuelaCurp = datosCerDirector.curpCer.trim().toUpperCase()
            parametros.firmanteEscuelaNombre = datosCerDirector.persona.nombre.trim().toUpperCase()
            parametros.firmanteEscuelaPrimerApellido = datosCerDirector.persona.primerApellido.trim().toUpperCase()
            parametros.firmanteEscuelaSegundoApellido = datosCerDirector.persona.segundoApellido.trim().toUpperCase()
            parametros.firmanteEscuelaNombreCompleto = datosCerDirector.nombreCer.trim().toUpperCase()
            parametros.firmanteEscuelaTitulo = datosCerDirector.persona.titulo.trim().toUpperCase()
            parametros.firmanteEscuelaFecha = formatoFechaXml.format(certificado.firmaDirectorEscuela.fechaFirma)

            //datos firmante dgmess
            def datosCerAutenticador = certificado.firmaAutenticadorDgemss.firmaElectronica

            parametros.firmanteDgemssSello = certificado.firmaAutenticadorDgemss.selloDigital
            parametros.firmanteDgemssNoSerie = datosCerAutenticador.numeroSerieCer.trim().toUpperCase()
            parametros.firmanteDgemssCurp = datosCerAutenticador.curpCer.trim().toUpperCase()
            parametros.firmanteDgemssNombreCompleto = datosCerAutenticador.nombreCer.trim().toUpperCase()
            parametros.firmanteDgemssTitulo = datosCerAutenticador.persona.titulo.trim().toUpperCase()
            parametros.firmanteDgemssFecha = formatoFechaPdf.format(certificado.firmaAutenticadorDgemss.fechaFirma)
            parametros.firmanteDgemssFechaCadena = formatoFechaXml.format(certificado.firmaAutenticadorDgemss.fechaFirma)

            //Generar código QR
            def url = "https://atenciondigital-educacion.morelos.gob.mx/certificado/consultarCertificado?uuid=${certificado.uuid}"
            def resultadoQr = zxingService.generarQr(url)
            if (resultadoQr.estatus) {
                parametros.qr = new ByteArrayInputStream(resultadoQr.qr)
            }
        }

        return parametros
    }

    def obtenerCadenaOriginalPdf(parametros){

        def cadenaOriginal = "||2.0"

        cadenaOriginal += "|5" // tipoCertificado
        cadenaOriginal += "|${parametros.uuid}"
        cadenaOriginal += "|${parametros.folioControl}"
        cadenaOriginal += "|${parametros.institucionId}"
        cadenaOriginal += "|${parametros.institucion}"
        cadenaOriginal += "|${parametros.institucionClaveCt}"
        cadenaOriginal += "|${parametros.institucionMunicipio}"
        cadenaOriginal += "|${parametros.carrera}"
        cadenaOriginal += "|MEDIO SUPERIOR"
        cadenaOriginal += "|${parametros.modalidad}"
        cadenaOriginal += "|6.0" // Calificación minima 
        cadenaOriginal += "|10"  // Calificación máxima
        cadenaOriginal += "|6.0" // Calificación minima aprobatoria
        cadenaOriginal += "|${parametros.matricula}"
        cadenaOriginal += "|${parametros.curp}"
        cadenaOriginal += "|${parametros.nombre}"
        cadenaOriginal += "|${parametros.primerApellido}"
        cadenaOriginal += "|${parametros.segundoApellido}"
        cadenaOriginal += "|${parametros.fechaNacimiento}"
        cadenaOriginal += "|${parametros.tipoCertificado}"
        cadenaOriginal += "|${parametros.firmanteEscuelaFecha}"
        cadenaOriginal += "|${parametros.municipio}"
        cadenaOriginal += "|26" // total asignaturas
        cadenaOriginal += "|${parametros.numeroAsignaturasAcreditadas}"
        cadenaOriginal += "|${parametros.promedio}"
        cadenaOriginal += "|${parametros.fechaUltimaAcreditacionCadena}"

        parametros.materias.each{ item ->
            item.each{ llave, valor ->
                cadenaOriginal += "|${valor}"
            }
        }
        parametros.materias2.each{ item ->
            item.each{ llave, valor ->
                cadenaOriginal += "|${valor}"
            }
        }
        
        cadenaOriginal += "||"

        return cadenaOriginal
    }

    def obtenerCadenaOriginalFirmaEscuela(parametros, fechaGeneracion){

        fechaGeneracion = new SimpleDateFormat("YYYY-MM-dd'T'hh:mm:ss").format(fechaGeneracion)

        def cadenaOriginal = "||2.0"

        cadenaOriginal += "|5" // tipoCertificado
        cadenaOriginal += "|${parametros.uuid}"
        cadenaOriginal += "|${parametros.folioControl}"
        cadenaOriginal += "|${parametros.institucionId}"
        cadenaOriginal += "|${parametros.institucion}"
        cadenaOriginal += "|${parametros.institucionClaveCt}"
        cadenaOriginal += "|${parametros.institucionMunicipio}"
        cadenaOriginal += "|${parametros.carrera}"
        cadenaOriginal += "|MEDIO SUPERIOR"
        cadenaOriginal += "|${parametros.modalidad}"
        cadenaOriginal += "|6.0" // Calificación minima 
        cadenaOriginal += "|10"  // Calificación máxima
        cadenaOriginal += "|6.0" // Calificación minima aprobatoria
        cadenaOriginal += "|${parametros.matricula}"
        cadenaOriginal += "|${parametros.curp}"
        cadenaOriginal += "|${parametros.nombre}"
        cadenaOriginal += "|${parametros.primerApellido}"
        cadenaOriginal += "|${parametros.segundoApellido}"
        cadenaOriginal += "|${parametros.fechaNacimiento}"
        cadenaOriginal += "|${parametros.tipoCertificado}"
        cadenaOriginal += "|${fechaGeneracion}"
        cadenaOriginal += "|${parametros.municipio}"
        cadenaOriginal += "|26" // total asignaturas
        cadenaOriginal += "|${parametros.numeroAsignaturasAcreditadas}"
        cadenaOriginal += "|${parametros.promedio}"
        cadenaOriginal += "|${parametros.fechaUltimaAcreditacionCadena}"

        parametros.materias.each{ item ->
            item.each{ llave, valor ->
                cadenaOriginal += "|${valor}"
            }
        }
        parametros.materias2.each{ item ->
            item.each{ llave, valor ->
                cadenaOriginal += "|${valor}"
            }
        }
        
        cadenaOriginal += "||"

        return cadenaOriginal
    }


    def obtenerCadenaOriginalFirmaDgemss(fechaExpedicion, selloDirectorEscuela, firmaElectronica){

        fechaExpedicion = new SimpleDateFormat("YYYY-MM-dd'T'hh:mm:ss").format(fechaExpedicion)

        def cadenaOriginal = "||"

        cadenaOriginal += "${fechaExpedicion}"
        cadenaOriginal += "|${selloDirectorEscuela}"
        cadenaOriginal += "|${firmaElectronica.numeroSerieCer}"
        cadenaOriginal += "|${firmaElectronica.curpCer}"
        cadenaOriginal += "|DIRECTOR GENERAL DE EDUCACIÓN MEDIA SUPERIOR Y SUPERIOR"
        
        cadenaOriginal += "||"

        return cadenaOriginal
    }
}
