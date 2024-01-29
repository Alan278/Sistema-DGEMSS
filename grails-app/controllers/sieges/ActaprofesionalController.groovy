package sieges

import grails.converters.JSON
import groovy.json.JsonOutput
import groovy.json.JsonBuilder
import org.hibernate.criterion.CriteriaSpecification
import org.springframework.core.io.ByteArrayResource
import groovy.json.JsonSlurper 
import groovy.json.JsonOutput
import java.text.SimpleDateFormat
import grails.plugin.springsecurity.annotation.Secured
import static org.grails.datastore.gorm.GormStaticApi.*

class ActaprofesionalController {

    def alumnoService
    def institucionService
    def carreraService
    def cicloEscolarService
    def documentoService
    def efirmaService
    def zxingService
    def actaService
    def constanciaService
    def notificacionService
    def certificadoService
    def personaService
    def evaluacionService
    def estatusActaService
    def usuarioService
    def tramiteService
    def planEstudiosService
    /**
     * Permite listar todos los certificados
     *
     * @param params con parametros de pagínado (opcional)
     */
    @Secured(['ROLE_GESTOR_ESCUELA', 'ROLE_DIRECTOR_ESCUELA', 'ROLE_AUTENTICADOR_DGEMSS', 'ROLE_REVISOR', 'ROLE_REVISOR_PUBLICA', 'ROLE_PRESIDENTE_ESCUELA', 'ROLE_SECRETARIO_ESCUELA', 'ROLE_VOCAL_ESCUELA'])
    def listarActas(){
        def instituciones = institucionService.obtenerActivos(params).datos
        def carreras = carreraService.obtenerActivos(params).datos
        def planesEstudios = planEstudiosService.obtenerActivos(params).datos
        def estatusActa = estatusActaService.obtenerActivos(params).datos
        def actas = actaService.listarActas(params).datos

        [
            actas: actas,
            conteo: actas.totalCount,
            instituciones: instituciones,
            carreras: carreras,
            planesEstudios: planesEstudios,
            estatusActa: estatusActa
        ]
    }

    /**
     * Se obtenen las actas acorde las fechas establecidas por el usuario
     *
     * @param con los parametros de pagínacion (opcional)
     *
     * @return resultado con el estatus, mensaje y actas
     */

    @Secured(['ROLE_GESTOR_ESCUELA', 'ROLE_DIRECTOR_ESCUELA', 'ROLE_AUTENTICADOR_DGEMSS', 'ROLE_REVISOR', 'ROLE_REVISOR_PUBLICA', 'ROLE_PRESIDENTE_ESCUELA', 'ROLE_SECRETARIO_ESCUELA', 'ROLE_VOCAL_ESCUELA'])
    def consultarActasfecha(){
        def instituciones = institucionService.obtenerActivos(params).datos
        def carreras = carreraService.obtenerActivos(params).datos
        def planesEstudios = planEstudiosService.obtenerActivos(params).datos
        def actas = actaService.listarActas(params).datos
        def constancias = constanciaService.listarConstancias(params).datos
        def notificaciones = notificacionService.listarNotificaciones(params).datos
        def certificados = certificadoService.listarCertificados(params).datos
        def formatoFecha = new SimpleDateFormat("yyyy-MM-dd")
        def fechaInicio = params.fechaInicio ? formatoFecha.parse(formatoFecha.format(params.fechaInicio)) : null
        def fechaFin = params.fechaFin ? formatoFecha.parse(formatoFecha.format(params.fechaFin)) : null
    
        def registros = ActaProfesional.createCriteria().list {
            eq("activo", true)
            if (fechaInicio && fechaFin) {
                
                between("fechaRegistro", fechaInicio, fechaFin)
                
            }
        }
        def etiquetas = []
        def datos = []
        def conteos = [:]
        EstatusActa.list()?.each { estatus ->
            conteos[estatus.nombre] = registros.count {
                it.activo && it.estatusActa.nombre == estatus.nombre
            }
        }
        EstatusActa.list()?.each { estatus ->
            etiquetas.add(estatus.nombre)
            datos.add(conteos[estatus.nombre])
        }
        def datosJson = new JsonBuilder([
            labels: etiquetas,
            datasets: [
                [
                    label: "Cantidad",
                    data: datos
                ]
            ]
        ])

        def script = """
            var ctx = document.getElementById('myChart').getContext('2d');
            var myChart = new Chart(ctx, {
                type: 'bar',
                data: ${datosJson.toString()}
            });
        """

        render view: 'consultarActasfecha', model: [
            actas: actas,
            etiquetas: etiquetas, 
            datos: datos,
            conteos: conteos,
            instituciones: instituciones,
            carreras: carreras,
            planesEstudios: planesEstudios,
            registros: registros
        ]

        [
            actas: actas,
            etiquetas: etiquetas, 
            datos: datos,
            conteos: conteos,
            constancias: constancias,
            notificaciones: notificaciones,
            certificados: certificados,
            conteo: actas.totalCount,
            conteoC: constancias.totalCount,
            conteoN: notificaciones.totalCount,
            conteoCer: certificados.totalCount,
            instituciones: instituciones,
            carreras: carreras,
            planesEstudios: planesEstudios,

            registros: registros
        ]
    }

    /**
     * Permite listar los certificados a revisar
     *
     * @param params con parametros de pagínado (opcional)
     */
    @Secured(['ROLE_REVISOR', 'ROLE_REVISOR_PUBLICA'])
    def listarActasRevisar(){
        def instituciones = institucionService.obtenerActivos(params).datos
        def carreras = carreraService.obtenerActivos(params).datos
        def estatusActa = estatusActaService.obtenerActivos(params).datos
        def actas = actaService.listarActasRevisar(params).datos

        [
            actas: actas,
            conteo: actas.totalCount,
            carreras: carreras,
            estatusActa: estatusActa,
            instituciones: instituciones
        ]
    }

    /**
     * Permite listar los certificados a firmar
     *
     * @param params con parametros de pagínado (opcional)
     */
    @Secured(['ROLE_AUTENTICADOR_DGEMSS'])
    def listarActasFirmar(){
        def instituciones = institucionService.obtenerActivos(params).datos
        def carreras = carreraService.obtenerActivos(params).datos
        def estatusActa = estatusActaService.obtenerActivos(params).datos
        def actas = actaService.listarActaFirmar(params).datos

        [
            actas: actas,
            conteo: actas.totalCount,
            carreras: carreras,
            estatusActa: estatusActa,
            instituciones: instituciones
        ]
    }

    /**
     * Permite listar las constancias a firmar
     *
     * @param params con parametros de pagínado (opcional)
     */
    @Secured(['ROLE_DIRECTOR_ESCUELA'])
    def listarFirmasActas(){
        def instituciones = institucionService.obtenerActivos(params).datos
        def carreras = carreraService.obtenerActivos(params).datos
        def planesEstudios = planEstudiosService.obtenerActivos(params).datos
        def actas = actaService.listarActasFirmar(params).datos
        def usuario = usuarioService.obtenerUsuarioLogueado()

        [
            actas: actas,
            conteo: actas.totalCount,
            usuario: usuario,
            carreras: carreras,
            planesEstudios: planesEstudios,
            instituciones: instituciones
        ]
    }
    /**
     * Permite listar las constancias a firmar
     *
     * @param params con parametros de pagínado (opcional)
     */
    @Secured(['ROLE_DIRECTOR_ESCUELA', 'ROLE_AUTENTICADOR_DGEMSS','ROLE_VOCAL_ESCUELA'])
    def listarActasVocal(){
        def instituciones = institucionService.obtenerActivos(params).datos
        def carreras = carreraService.obtenerActivos(params).datos
        def actas = actaService.listarActasFirmarVocal(params).datos
        def usuario = usuarioService.obtenerUsuarioLogueado()

        [
            actas: actas,
            conteo: actas.totalCount,
            usuario: usuario,
            carreras: carreras,
            instituciones: instituciones
        ]
    }

     /**
     * Permite listar las constancias a firmar
     *
     * @param params con parametros de pagínado (opcional)
     */
    @Secured(['ROLE_DIRECTOR_ESCUELA', 'ROLE_AUTENTICADOR_DGEMSS','ROLE_SECRETARIO_ESCUELA'])
    def listarActasSecretario(){
        def instituciones = institucionService.obtenerActivos(params).datos
        def carreras = carreraService.obtenerActivos(params).datos
        def actas = actaService.listarActasFirmarSecretario(params).datos
        def usuario = usuarioService.obtenerUsuarioLogueado()

        [
            actas: actas,
            conteo: actas.totalCount,
            usuario: usuario,
            carreras: carreras,
            instituciones: instituciones
        ]
    }

     /**
     * Permite listar las constancias a firmar
     *
     * @param params con parametros de pagínado (opcional)
     */
    @Secured(['ROLE_DIRECTOR_ESCUELA', 'ROLE_AUTENTICADOR_DGEMSS','ROLE_PRESIDENTE_ESCUELA'])
    def listarActasPresidente(){
        def instituciones = institucionService.obtenerActivos(params).datos
        def carreras = carreraService.obtenerActivos(params).datos
        def actas = actaService.listarActasFirmarPresidente(params).datos
        def usuario = usuarioService.obtenerUsuarioLogueado()

        [
            actas: actas,
            conteo: actas.totalCount,
            usuario: usuario,
            carreras: carreras,
            instituciones: instituciones
        ]
    }

    /**
     * Permite listar los certificados finalizados por alumno
     *
     * @param params con parametros de pagínado (opcional)
     */
    @Secured(['ROLE_GESTOR_ESCUELA', 'ROLE_DIRECTOR_ESCUELA', 'ROLE_AUTENTICADOR_DGEMSS'])
    def listarActasAlumno(){
        def alumno = alumnoService.consultar(params).datos
        params.alumnoId = alumno.id
        def actas = actaService.listarActasFinalizadosPorAlumno(params).datos

        [
            alumno: alumno,
            conteo: actas.totalCount,
            actas: actas
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
        def opctitulaciones = OpcTitulacion.getAll()
        def tipodocumentos = TipoDocumento.getAll()
        def declaraciones = Declaracion.getAll()

        [alumno: alumno,
        opctitulaciones: opctitulaciones,
        tipodocumentos: tipodocumentos,
        declaraciones: declaraciones ]
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
        params.archivopdf != null? request.getFile("archivopdf"): null
        def resultado = actaService.registrar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        params.foto = null
        params.opctitulacion = null
        params.titulo = null
        params.declaracion = null
        params.doc = null
        params.presidente = null
        params.secretario= null
        params.vocal = null
        params.archivopdf = null

        if(!resultado.estatus){
            redirect(action: "solicitud", params: [id: params.alumnoId, duplicado: params.duplicado])
            return
        }

        redirect(action: "listarActas")
        return
    }

    @Secured(['ROLE_REVISOR', 'ROLE_REVISOR_PUBLICA'])
    def registro(){
        def acta = actaService.consultar(params).datos
        [ acta: acta ]
    }


    def consultar(){
        def resultado = actaService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect(action: "listarActas" , params:params)
            return
        }
        def acta = resultado.datos

        def pdf = Base64.getEncoder().encodeToString(acta.archivopdf)

        def bphoto = Base64.getEncoder().encodeToString(acta.foto)
        [
            acta: acta,
            bphoto: bphoto,
            pdf: pdf
        ]

    }

   
    /**
     * Permite asignar datos de control a un acta
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
        def resultado = actaService.enviarAFirmarDGEMSS(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect(action: "registro", params: params + [id: params.personaId])
            return
        }

        redirect(action: "listarActasRevisar")
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
    def listarActasInstitucion(){
        def institucion = institucionService.consultar(params).datos
        def actas = actaService.listarActasAFirmarPorInstitucion(params).datos
        def usuario = usuarioService.obtenerUsuarioLogueado()
        [
            actas: actas,
            conteo: actas.size(),
            usuario: usuario,
            institucion: institucion,
        ]
    }
    @Secured('ROLE_AUTENTICADOR_DGEMSS')
    def firmaActas(){
        def usuario = usuarioService.obtenerUsuarioLogueado()
        [
            usuario: usuario
        ]
    }

    @Secured('ROLE_AUTENTICADOR_DGEMSS')
    def firmarActas(){
        def resultado = notificacionService.firmarNotificaciones(params)

        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect(action: "firmaConstancias" , params: params )
            return
        }

        redirect(action: "listarConstanciasInstitucion" , params: [id: params.institucionId] )
    }

    @Secured('ROLE_GESTOR_ESCUELA')
    def cambiarEstatusFirmandoEscuela(){
        def resultado = actaService.enviarAFirmarEscuela(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect(action: "listarActas")
        return
    }

    @Secured('ROLE_GESTOR_ESCUELA')
    def subirFotoModificacion(){
        def acta = actaService.consultar(params).datos
        def opctitulaciones = OpcTitulacion.getAll()
        def tipodocumentos = TipoDocumento.getAll()
        def declaraciones = Declaracion.getAll()
        def bphoto = Base64.getEncoder().encodeToString(acta.foto)

        [
            acta: acta,
            bphoto: bphoto,
            opctitulaciones: opctitulaciones,
            tipodocumentos: tipodocumentos,
            declaraciones: declaraciones
        ]
    }

    def modificar(){
        if(params.foto){
            this.foto = params.foto
        }else{
            params.foto = this.foto
        }

        def resultado = actaService.modificar(params)
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
        redirect(action: "listarActas", params: [id: params.personaId])
        return
    }

    def eliminar(){
        def resultado = actaService.eliminar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect action: "listarActas"

        return
    }

    def consultarActa(){
        def resultado = actaService.consultarActa(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            return
        }

        def acta = resultado.datos

        def bphoto = Base64.getEncoder().encodeToString(acta.foto)

        [
            acta: acta,
            bphoto: bphoto
        ]
    }

     @Secured('ROLE_GESTOR_ESCUELA')
    def modificarFotografia() {
        params.archivopdf != null? request.getFile("archivopdf"): null
        def resultado = actaService.modificarFotografia(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        params.foto = null

        if(!resultado.estatus){
            redirect(action: "subirFotoModificacion", params: params)
            return
        }

        redirect(action: "listarActas")
        return
    }

     /**
     * Permite mostrar la vista con los datos de un certificado a revisar
     *
     * @param uuid (requerido)
     * Uuid del certificado a revisar
     */
    @Secured(['ROLE_REVISOR', 'ROLE_REVISOR_PUBLICA', 'ROLE_DIRECTOR_ESCUELA', 'ROLE_AUTENTICADOR_DGEMSS', 'ROLE_PRESIDENTE_ESCUELA', 'ROLE_SECRETARIO_ESCUELA', 'ROLE_VOCAL_ESCUELA'])
    def revision(){
        def resultado = actaService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect(action: "listarActas" , params:params)
            return
        }
        def acta = resultado.datos

        def bphoto = Base64.getEncoder().encodeToString(acta.foto)

        [
            acta: acta,
            bphoto: bphoto
        ]
    }

    @Secured(['ROLE_DIRECTOR_ESCUELA', 'ROLE_REVISOR', 'ROLE_REVISOR_PUBLICA', 'ROLE_AUTENTICADOR_DGEMSS'])
    def cambiarEstatusRechazado(){
        def resultado = actaService.cambiarEstatusRechazado(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        def roles = usuarioService.obtenerRoles()
		if(roles.contains('ROLE_DIRECTOR_ESCUELA') || roles.contains('ROLE_AUTENTICADOR_DGEMSS')){
		    redirect(action: "listarFirmasActas")
        }else if(roles.contains('ROLE_REVISOR') || roles.contains('ROLE_REVISOR_PUBLICA')){
            redirect(action: "listarActasRevisar")
        }
        return
    }

    @Secured('ROLE_DIRECTOR_ESCUELA')
    def cambiarEstatusRechazadoDirector(){
        def resultado = actaService.cambiarEstatusRechazadoDirector(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect(action: "listarActas")
        return
    }

    @Secured(['ROLE_REVISOR', 'ROLE_REVISOR_PUBLICA'])
    def cambiarEstatusRechazadoDgemss(){
        def resultado = actaService.cambiarEstatusRechazadoDgemss(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect(action: "listarActasRevisar")
        return
    }

    @Secured('ROLE_AUTENTICADOR_DGEMSS')
    def cambiarEstatusRechazadoAutenticador(){
        def resultado = actaService.cambiarEstatusRechazadoAutenticador(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect(action: "listarActasRevisar")
        return
    }


     /**
     * Permite mostrar la vista para firmar un certificado
     *
     * @param uuid (requerido)
     * Uuid del certificado a firmar
     *
     */
    @Secured(['ROLE_DIRECTOR_ESCUELA', 'ROLE_AUTENTICADOR_DGEMSS', 'ROLE_PRESIDENTE_ESCUELA', 'ROLE_SECRETARIO_ESCUELA', 'ROLE_VOCAL_ESCUELA'])
    def firmar(){
        def acta = actaService.consultar(params).datos
        def usuario = usuarioService.obtenerUsuarioLogueado()

        [
            acta: acta,
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
    @Secured(['ROLE_DIRECTOR_ESCUELA', 'ROLE_AUTENTICADOR_DGEMSS', 'ROLE_PRESIDENTE_ESCUELA', 'ROLE_SECRETARIO_ESCUELA', 'ROLE_VOCAL_ESCUELA'])
    def firmarActa(){
        def resultado = actaService.firmarActa(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        params.clavePrivada = null

        if(!resultado.estatus){
            redirect(action: "firmar" , params:params)
            return
        }
        redirect(action: "listarActas" , params:params)
    }

    def descargarPdf(){
        def resultado = actaService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect(action: "listarActas", params: [id: params.personaId])
            return
        }

        def acta = resultado.datos

        if(!acta.pdf){
            actaService.generarDocumentosActa(acta.uuid)
        }

        def pdf = acta.pdf
        response.setContentType("application/octet-stream")
        response.setHeader("Content-disposition", "attachment; filename=${acta.folioControl}.pdf")
        response.outputStream << pdf
        return
    }

    def mostrarPdf(){
        def resultado = actaService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            return
        }

        def acta = resultado.datos

        if(!acta.pdf){
            actaService.generarDocumentosActa(acta.uuid)
        }

        def pdf = acta.pdf
        render file: pdf, contentType: "application/pdf"
        return
    }

    def descargarXml(){
        def resultado = notificacionService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            def xml = resultado.datos.xml
            response.setContentType("application/octet-stream")
            response.setHeader("Content-disposition", "attachment; filename=${resultado.datos.folioControl}.xml")
            response.outputStream << xml
            return
        }

        redirect(action: "listarNotificaciones", params: [id: params.personaId])
        return

    }
}
