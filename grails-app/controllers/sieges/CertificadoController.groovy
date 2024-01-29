package sieges

import java.text.SimpleDateFormat
import grails.plugin.springsecurity.annotation.Secured


class CertificadoController {

    def alumnoService
    def institucionService
    def carreraService
    def cicloEscolarService
    def documentoService
    def efirmaService
    def zxingService
    def certificadoService
    def personaService
    def evaluacionService
    def estatusCertificadoService
    def usuarioService
    def planEstudiosService

    /**
     * Permite listar todos los certificados
     *
     * @param params con parametros de pagínado (opcional)
     */
    @Secured(['ROLE_GESTOR_ESCUELA', 'ROLE_DIRECTOR_ESCUELA', 'ROLE_AUTENTICADOR_DGEMSS', 'ROLE_REVISOR', 'ROLE_REVISOR_PUBLICA'])
    def listarCertificados(){
        def instituciones = institucionService.obtenerActivos(params).datos
        def carreras = carreraService.obtenerActivos(params).datos
        def planesEstudios = planEstudiosService.obtenerActivos(params).datos
        def estatusCertificado = estatusCertificadoService.obtenerActivos(params).datos
        def certificados = certificadoService.listarCertificados(params).datos

        [
            certificados: certificados,
            conteo: certificados.totalCount,
            instituciones: instituciones,
            carreras: carreras,
            planesEstudios: planesEstudios,
            estatusCertificado: estatusCertificado
        ]
    }

    /**
     * Permite listar los certificados a revisar
     *
     * @param params con parametros de pagínado (opcional)
     */
    @Secured(['ROLE_REVISOR', 'ROLE_REVISOR_PUBLICA'])
    def listarCertificadosRevisar(){
        def instituciones = institucionService.obtenerActivos(params).datos
        def carreras = carreraService.obtenerActivos(params).datos
        def estatusCertificado = estatusCertificadoService.obtenerActivos(params).datos
        def certificados = certificadoService.listarCertificadosRevisar(params).datos

        [
            certificados: certificados,
            conteo: certificados.totalCount,
            carreras: carreras,
            estatusCertificado: estatusCertificado,
            instituciones: instituciones
        ]
    }

    /**
     * Permite listar los certificados a firmar
     *
     * @param params con parametros de pagínado (opcional)
     */
    @Secured(['ROLE_DIRECTOR_ESCUELA', 'ROLE_AUTENTICADOR_DGEMSS'])
    def listarFirmasCertificados(){
        def instituciones = institucionService.obtenerActivos(params).datos
        def carreras = carreraService.obtenerActivos(params).datos
        def planesEstudios = planEstudiosService.obtenerActivos(params).datos
        def certificados = certificadoService.listarCertificadosFirmar(params).datos
        def usuario = usuarioService.obtenerUsuarioLogueado()

        [
            certificados: certificados,
            conteo: certificados.totalCount,
            usuario: usuario,
            carreras: carreras,
            planesEstudios: planesEstudios,
            instituciones: instituciones
        ]
    }

    /**
     * Permite listar los certificados finalizados por alumno
     *
     * @param params con parametros de pagínado (opcional)
     */
    @Secured(['ROLE_GESTOR_ESCUELA', 'ROLE_DIRECTOR_ESCUELA', 'ROLE_AUTENTICADOR_DGEMSS'])
    def listarCertificadosAlumno(){
        def alumno = alumnoService.consultar(params).datos
        params.alumnoId = alumno.id
        def certificados = certificadoService.listarCertificadosFinalizadosPorAlumno(params).datos

        [
            alumno: alumno,
            conteo: certificados.totalCount,
            certificados: certificados
        ]
    }

    /**
     * Permite listar los alumnos para la generación de un certificado
     *
     * @param institucionId (opcional)
     * Id de la institución para filtrar los registros 
     * @param carreraId (opcional)
     * Id de la carrera para filtrar los registros
     * @param search (opcional)
     * Nombre, curp o matricula del alumno a buscar
     */
    @Secured(['ROLE_GESTOR_ESCUELA'])
    def listarAlumnos(){
        def instituciones = institucionService.obtenerActivos(params)
        def carreras = carreraService.obtenerActivos(params)
        def planesEstudios = planEstudiosService.obtenerActivos(params)
        def alumnos = alumnoService.listar(params)

        [
            instituciones: instituciones.datos,
            carreras: carreras.datos,
            planesEstudios: planesEstudios.datos,
            alumnos: alumnos.datos,
            conteo: alumnos.datos.totalCount
        ]
    }

    /**
     * Permite mostrar la vista solicitud donde se sube y registra un certificado
     * @param Id
     * Id del alumno al que se desea generar un certificado
     */
    @Secured('ROLE_GESTOR_ESCUELA')
    def solicitud() {
        def alumno = alumnoService.consultar(params).datos

        [alumno: alumno]
    }

    /**
     * Permite registrar un certificado
     *
     * @param alumnoId (requerido)
     * Id del alumno al que se desea generar un certificado
     * @param foto (requerido)
     * Foto del alumno
     *
     */
    @Secured('ROLE_GESTOR_ESCUELA')
    def registrar() {
        def resultado = certificadoService.registrar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        params.foto = null

        if(!resultado.estatus){
            redirect(action: "solicitud", params: [id: params.alumnoId, duplicado: params.duplicado])
            return
        }

        redirect(action: "listarCertificados")
        return
    }

    /**
     * Permite mostrar la vista para modificar la fotografía de un certificado
     *
     * @param uuid (requerido)
     * Uuid del certificado a modificar
     *
     */
    @Secured('ROLE_GESTOR_ESCUELA')
    def subirFotoModificacion(){
        def certificado = certificadoService.consultar(params).datos
        def bphoto = Base64.getEncoder().encodeToString(certificado.foto)

        [
            certificado: certificado,
            bphoto: bphoto
        ]
    }

    /**
     * Permite modificar la fotografía de un certificado
     *
     * @param uuid (requerido)
     * Uuid del certificado a modificar
     * @params foto (requerido)
     */
    @Secured('ROLE_GESTOR_ESCUELA')
    def modificarFotografia() {
        def resultado = certificadoService.modificarFotografia(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        params.foto = null

        if(!resultado.estatus){
            redirect(action: "subirFotoModificacion", params: params)
            return
        }

        redirect(action: "listarCertificados")
        return
    }

    /**
     * Permite mostrar la vista con los datos de un certificado a revisar
     *
     * @param uuid (requerido)
     * Uuid del certificado a revisar
     */
    @Secured(['ROLE_REVISOR', 'ROLE_REVISOR_PUBLICA', 'ROLE_DIRECTOR_ESCUELA', 'ROLE_AUTENTICADOR_DGEMSS'])
    def revision(){
        def resultado = certificadoService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect(action: "listarCertificados", params: params)
            return
        }

        def certificado = resultado.datos
        def evaluaciones = evaluacionService.consultarPorCertificado([
            alumnoId: certificado.alumno.id,
            fechaRegistro: certificado.fechaRegistro,
        ]).datos
        def bphoto = Base64.getEncoder().encodeToString(certificado.foto)

        def promedio = evaluacionService.calcularPromedio(evaluaciones)
        promedio = promedio == 10 ? 10 : String.format("%.01f", promedio)

        def formatter = new SimpleDateFormat("yyyy-MM-dd")
        def fechaUltimaAcreditacion =  formatter.format(evaluacionService.obtenerFechaUltimaAcreditacion(certificado))

        [
            certificado: certificado,
            evaluaciones: evaluaciones,
            promedio: promedio,
            fechaUltimaAcreditacion: fechaUltimaAcreditacion,
            bphoto: bphoto
        ]
    }

    /**
     * Permite mostrar la vista para asignar datos de control a un certificado
     *
     * @param uuid (requerido)
     * Uuid del certificado a validar
     *
     */
    @Secured(['ROLE_REVISOR', 'ROLE_REVISOR_PUBLICA'])
    def registro(){
        def certificado = certificadoService.consultar(params).datos
        [ certificado: certificado ]
    }

    /**
     * Permite asignar datos de control a un certificado
     *
     * @param uuid (requerido)
     * Uuid del certificado a validar
     * @param libro (requerido)
     * @param foja (requerido)
     * @param numero (opcional)
     *
     */
    @Secured(['ROLE_REVISOR', 'ROLE_REVISOR_PUBLICA'])
    def aceptar(){
        def resultado = certificadoService.enviarAFirmarDGEMSS(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect(action: "registro", params: params + [id: params.personaId])
            return
        }

        redirect(action: "listarCertificadosRevisar")
        return
    }

    /**
     * Permite mostrar la vista para firmar un certificado
     *
     * @param uuid (requerido)
     * Uuid del certificado a firmar
     *
     */
    @Secured(['ROLE_DIRECTOR_ESCUELA', 'ROLE_AUTENTICADOR_DGEMSS'])
    def firmar(){
        def certificado = certificadoService.consultar(params).datos
        def usuario = usuarioService.obtenerUsuarioLogueado()

        [
            certificado: certificado,
            usuario: usuario
        ]
    }

    /**
     * Permite firmar un certificado
     *
     * @param uuid (requerido)
     * Uuid del certificado a firmar
     *
     */
    @Secured(['ROLE_DIRECTOR_ESCUELA', 'ROLE_AUTENTICADOR_DGEMSS'])
    def firmarCertificado(){
        def resultado = certificadoService.firmarCertificado(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        params.clavePrivada = null

        if(!resultado.estatus){
            redirect(action: "firmar" , params:params)
            return
        }
        redirect(action: "listarFirmasCertificados" , params:params)
    }

    def modificacion(){
        def certificado = certificadoService.consultar(params).datos
        def formatter = new SimpleDateFormat("yyyy-MM-dd")
        def fechaExpedicion = formatter.format(certificado.fechaExpedicion)
        [
            certificado: certificado,
            fechaExpedicion: fechaExpedicion
        ]
    }

    def modificar(){
        if(params.foto){
            this.foto = params.foto
        }else{
            params.foto = this.foto
        }

        def resultado = certificadoService.modificar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.pag == 2){
            redirect(action: "subirFotoModificacion", params: params+[id: params.personaId])
            return
        }
        if(!resultado.estatus){
            redirect(action: "modificacion", params: params+[id: params.personaId])
            return
        }

        this.foto = null
        redirect(action: "listarCertificados", params: [id: params.personaId])
        return
    }

    def consultar(){
        def resultado = certificadoService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect(action: "listarCertificados" , params:params)
            return
        }
        def certificado = resultado.datos

        def evaluaciones = evaluacionService.consultarPorCertificado([
            alumnoId: certificado.alumno.id,
            fechaRegistro: certificado.fechaRegistro,
        ]).datos
        def bphoto = Base64.getEncoder().encodeToString(certificado.foto)

        def promedio = evaluacionService.calcularPromedio(evaluaciones)
        promedio = promedio == 10 ? 10 : String.format("%.01f", promedio)

        def formatter = new SimpleDateFormat("yyyy-MM-dd")
        def fechaUltimaAcreditacion =  formatter.format(evaluacionService.obtenerFechaUltimaAcreditacion(certificado))

        [
            certificado: certificado,
            evaluaciones: evaluaciones,
            promedio: promedio,
            fechaUltimaAcreditacion: fechaUltimaAcreditacion,
            bphoto: bphoto
        ]
    }

    def consultarCertificado(){
        def resultado = certificadoService.consultarCertificado(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            return
        }

        def certificado = resultado.datos

        def evaluaciones = evaluacionService.consultarPorCertificado([
            alumnoId: certificado.alumno.id,
            fechaRegistro: certificado.fechaRegistro,
        ]).datos

        def promedio = evaluacionService.calcularPromedio(evaluaciones)
        promedio = promedio == 10 ? 10 : String.format("%.01f", promedio)

        def formatter = new SimpleDateFormat("dd/MM/yyyy")
        def fechaUltimaAcreditacion =  formatter.format(evaluacionService.obtenerFechaUltimaAcreditacion(certificado))

        def bphoto = Base64.getEncoder().encodeToString(certificado.foto)

        [
            certificado: certificado,
            evaluaciones: evaluaciones,
            promedio: promedio,
            fechaUltimaAcreditacion: fechaUltimaAcreditacion,
            bphoto: bphoto
        ]
    }

    def descargarPdf(){
        def resultado = certificadoService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect(action: "listarCertificados", params: [id: params.personaId])
            return
        }

        def certificado = resultado.datos

        if(!certificado.pdf){
            certificadoService.generarDocumentosCertificado(certificado.uuid)
        }

        def pdf = certificado.pdf
        response.setContentType("application/octet-stream")
        response.setHeader("Content-disposition", "attachment; filename=${certificado.folioControl}.pdf")
        response.outputStream << pdf
        return
    }

    def mostrarPdf(){
        def resultado = certificadoService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            return
        }

        def certificado = resultado.datos

        if(!certificado.pdf){
            certificadoService.generarDocumentosCertificado(certificado.uuid)
        }

        def pdf = certificado.pdf
        render file: pdf, contentType: "application/pdf"
        return
    }

    def descargarXml(){
        def resultado = certificadoService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            def xml = resultado.datos.xml
            response.setContentType("application/octet-stream")
            response.setHeader("Content-disposition", "attachment; filename=${resultado.datos.folioControl}.xml")
            response.outputStream << xml
            return
        }

        redirect(action: "listarCertificados", params: [id: params.personaId])
        return

    }

    def eliminar(){
        def resultado = certificadoService.eliminar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect action: "listarCertificados"

        return
    }

    def generarDocumentosCertificado(){
        def certificado = Certificado.get(params.id)
        def resultado = certificadoService.generarDocumentosCertificado(certificado)

        render "Ok"
    }

    @Secured(['ROLE_DIRECTOR_ESCUELA', 'ROLE_REVISOR', 'ROLE_REVISOR_PUBLICA', 'ROLE_AUTENTICADOR_DGEMSS'])
    def cambiarEstatusRechazado(){
        def resultado = certificadoService.cambiarEstatusRechazado(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        def roles = usuarioService.obtenerRoles()
		if(roles.contains('ROLE_DIRECTOR_ESCUELA') || roles.contains('ROLE_AUTENTICADOR_DGEMSS')){
		    redirect(action: "listarFirmasCertificados")
        }else if(roles.contains('ROLE_REVISOR') || roles.contains('ROLE_REVISOR_PUBLICA')){
            redirect(action: "listarCertificadosRevisar")
        }
        return
    }

    @Secured('ROLE_GESTOR_ESCUELA')
    def cambiarEstatusFirmandoEscuela(){
        def resultado = certificadoService.enviarAFirmarEscuela(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect(action: "listarCertificados")
        return
    }

    @Secured('ROLE_DIRECTOR_ESCUELA')
    def cambiarEstatusRechazadoDirector(){
        def resultado = certificadoService.cambiarEstatusRechazadoDirector(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect(action: "listarCertificados")
        return
    }

    @Secured('ROLE_RECEPTOR')
    def cambiarEstatusEnRevision(){
        def resultado = certificadoService.cambiarEstatusEnRevision(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect(action: "listarCertificados")
        return
    }

    @Secured(['ROLE_REVISOR', 'ROLE_REVISOR_PUBLICA'])
    def cambiarEstatusFirmandoDgemss(){
        def resultado = certificadoService.cambiarEstatusFirmandoDgemss(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect(action: "listarCertificadosRevisar")
        return
    }

    @Secured(['ROLE_REVISOR', 'ROLE_REVISOR_PUBLICA'])
    def cambiarEstatusRechazadoDgemss(){
        def resultado = certificadoService.cambiarEstatusRechazadoDgemss(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect(action: "listarCertificadosRevisar")
        return
    }

    @Secured('ROLE_AUTENTICADOR_DGEMSS')
    def cambiarEstatusRechazadoAutenticador(){
        def resultado = certificadoService.cambiarEstatusRechazadoAutenticador(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect(action: "listarCertificadosRevisar")
        return
    }

    @Secured('ROLE_GESTOR_ESCUELA')
    def actualizarEvaluaciones(){
        def resultado = certificadoService.actualizarEvaluaciones(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect(action: "consultar", params: params)
        return
    }

    @Secured('ROLE_AUTENTICADOR_DGEMSS')
    def listarInstituciones(){
        def instituciones
        if(params.isPublic && params.isPublic.equals("true")){
            instituciones = institucionService.listarPublicas(params)
        }else{
            instituciones = institucionService.listarPrivadas(params)
        }
        [
            instituciones:instituciones.datos.instituciones,
            usuario: usuarioService.obtenerUsuarioLogueado(),
            conteo: instituciones.datos.totalCount
        ]
    }

    @Secured('ROLE_AUTENTICADOR_DGEMSS')
    def listarInstitucionesPrivadas(){
        def instituciones = institucionService.listarPrivadas(params)
        [
            instituciones:instituciones.datos.instituciones,
            conteo: instituciones.datos.totalCount
        ]
    }

    @Secured('ROLE_AUTENTICADOR_DGEMSS')
    def listarCertificadosInstitucion(){
        def institucion = institucionService.consultar(params).datos
        def certificados = certificadoService.listarCertificadosAFirmarPorInstitucion(params).datos
        def usuario = usuarioService.obtenerUsuarioLogueado()
        [
            certificados: certificados,
            conteo: certificados.size(),
            usuario: usuario,
            institucion: institucion,
        ]
    }

    @Secured('ROLE_AUTENTICADOR_DGEMSS')
    def firmaCertificados(){
        def usuario = usuarioService.obtenerUsuarioLogueado()
        [
            usuario: usuario
        ]
    }

    @Secured('ROLE_AUTENTICADOR_DGEMSS')
    def firmarCertificados(){
        def resultado = certificadoService.firmarCertificados(params)

        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect(action: "firmaCertificados" , params: params )
            return
        }

        redirect(action: "listarCertificadosInstitucion" , params: [id: params.institucionId] )
    }


    def datosFirma(){
    }

    def baseCer(){
        def datos = efirmaService.bytesToBase64(params.cer.getBytes())
        render datos
    }

    def datosCer(){
        def datos = efirmaService.extraerDatosCertificado(params.cer.getBytes())
        render datos
    }

    @Secured('ROLE_AUTENTICADOR_DGEMSS')
    def vistaVerificarFirma(){}

    def verificar(){
        def cer = params.cer.getBytes()
        def cadenaOriginal = params.cadenaOriginal
        def sello = params.sello
        def datos = efirmaService.verificar(cer, cadenaOriginal, sello)
        render datos
    }
}
