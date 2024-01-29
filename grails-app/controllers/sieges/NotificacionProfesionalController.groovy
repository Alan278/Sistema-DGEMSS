package sieges

import grails.converters.JSON
import groovy.json.JsonOutput
import groovy.json.JsonBuilder
import org.hibernate.criterion.CriteriaSpecification
import groovy.json.JsonSlurper 
import groovy.json.JsonOutput
import java.text.SimpleDateFormat
import grails.plugin.springsecurity.annotation.Secured
import static org.grails.datastore.gorm.GormStaticApi.*

class NotificacionProfesionalController {

    def alumnoService
    def institucionService
    def carreraService
    def cicloEscolarService
    def documentoService
    def efirmaService
    def zxingService
    def notificacionService
    def personaService
    def evaluacionService
    def estatusNotificacionService
    def usuarioService
    def tramiteService
    def planEstudiosService
    def titulacionService
    /**
     * Permite listar todos los certificados
     *
     * @param params con parametros de pagínado (opcional)
     */
    @Secured(['ROLE_GESTOR_ESCUELA', 'ROLE_DIRECTOR_ESCUELA', 'ROLE_AUTENTICADOR_DGEMSS', 'ROLE_REVISOR', 'ROLE_REVISOR_PUBLICA'])
    def listarNotificaciones(){
        def instituciones = institucionService.obtenerActivos(params).datos
        def carreras = carreraService.obtenerActivos(params).datos
        def estatusNotificacion = estatusNotificacionService.obtenerActivos(params).datos
        def notificaciones = notificacionService.listarNotificaciones(params).datos

        [
            notificaciones: notificaciones,
            conteo: notificaciones.totalCount,
            instituciones: instituciones,
            carreras: carreras,
            estatusNotificacion: estatusNotificacion
        ]
    }

    @Secured(['ROLE_GESTOR_ESCUELA', 'ROLE_DIRECTOR_ESCUELA', 'ROLE_AUTENTICADOR_DGEMSS', 'ROLE_REVISOR', 'ROLE_REVISOR_PUBLICA', 'ROLE_PRESIDENTE_ESCUELA', 'ROLE_SECRETARIO_ESCUELA', 'ROLE_VOCAL_ESCUELA'])
    def consultarNotificacionesfecha(){
        def instituciones = institucionService.obtenerActivos(params).datos
        def carreras = carreraService.obtenerActivos(params).datos
        def planesEstudios = planEstudiosService.obtenerActivos(params).datos
        def notificaciones = notificacionService.listarNotificaciones(params).datos
        def formatoFecha = new SimpleDateFormat("yyyy-MM-dd")
        def fechaInicio = params.fechaInicio ? formatoFecha.parse(formatoFecha.format(params.fechaInicio)) : null
        def fechaFin = params.fechaFin ? formatoFecha.parse(formatoFecha.format(params.fechaFin)) : null
    
        def registros = NotificacionProfesional.createCriteria().list {
            eq("activo", true)
            if (fechaInicio && fechaFin) {
                between("fechaRegistro", fechaInicio, fechaFin)
                
            }
        }
        def etiquetas = []
        def datos = []
        def conteos = [:]
        EstatusNotificacion.list()?.each { estatus ->
            conteos[estatus.nombre] = registros.count {
                it.activo && it.estatusNotificacion.nombre == estatus.nombre
            }
        }
        EstatusNotificacion.list()?.each { estatus ->
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

        render view: 'consultarNotificacionesfecha', model: [
            notificaciones: notificaciones,
            etiquetas: etiquetas, 
            datos: datos,
            conteos: conteos,
            instituciones: instituciones,
            carreras: carreras,
            planesEstudios: planesEstudios,
            registros: registros
        ]

        [
           
            etiquetas: etiquetas, 
            datos: datos,
            conteos: conteos,
            notificaciones: notificaciones,
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
    def listarNotificacionesRevisar(){
        def instituciones = institucionService.obtenerActivos(params).datos
        def carreras = carreraService.obtenerActivos(params).datos
        def estatusNotificacion = estatusNotificacionService.obtenerActivos(params).datos
        def notificaciones = notificacionService.listarNotificacionesRevisar(params).datos

        [
            notificaciones: notificaciones,
            conteo: notificaciones.totalCount,
            carreras: carreras,
            estatusNotificacion: estatusNotificacion,
            instituciones: instituciones
        ]
    }

    /**
     * Permite listar los certificados a firmar
     *
     * @param params con parametros de pagínado (opcional)
     */
    @Secured(['ROLE_AUTENTICADOR_DGEMSS'])
    def listarNotificacionesFirmar(){
        def instituciones = institucionService.obtenerActivos(params).datos
        def carreras = carreraService.obtenerActivos(params).datos
        def estatusNotificacion = estatusNotificacionService.obtenerActivos(params).datos
        def notificaciones = notificacionService.listarNotificacionFirmar(params).datos

        [
            notificaciones: notificaciones,
            conteo: notificaciones.totalCount,
            carreras: carreras,
            estatusNotificacion: estatusNotificacion,
            instituciones: instituciones
        ]
    }


    /**
     * Permite listar las constancias a firmar
     *
     * @param params con parametros de pagínado (opcional)
     */
    @Secured(['ROLE_DIRECTOR_ESCUELA', 'ROLE_AUTENTICADOR_DGEMSS'])
    def listarFirmasNotificaciones(){
        def instituciones = institucionService.obtenerActivos(params).datos
        def carreras = carreraService.obtenerActivos(params).datos
        def planesEstudios = planEstudiosService.obtenerActivos(params).datos
        def notificaciones = notificacionService.listarNotificacionesFirmar(params).datos
        def usuario = usuarioService.obtenerUsuarioLogueado()

        [
            notificaciones: notificaciones,
            conteo: notificaciones.totalCount,
            planesEstudios: planesEstudios,
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
    def listarNotificacionesAlumno(){
        def alumno = alumnoService.consultar(params).datos
        params.alumnoId = alumno.id
        def notificaciones = notificacionService.listarNotificacionesFinalizadosPorAlumno(params).datos

        [
            alumno: alumno,
            conteo: notificaciones.totalCount,
            notificaciones: notificaciones
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

        [alumno: alumno,
        tipodocumentos: tipodocumentos,
         opctitulaciones: opctitulaciones]
    }

    /**
     * Permite registrar una notificacion
     *
     * @param alumnoId (requerido)
     * Id del alumno al que se desea generar una notificación
     * @param foto (requerido)
     * Foto del alumno
     *
     */
    @Secured('ROLE_GESTOR_ESCUELA')
    def registrar() {
        def resultado = notificacionService.registrar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        params.foto = null
        params.opctitulacionId = null
        params.titulo = null
        params.presidente = null
        params.secretario= null
        params.vocal = null

        if(!resultado.estatus){
            redirect(action: "solicitud", params: [id: params.alumnoId])
            return
        }

        redirect(action: "listarNotificaciones")
        return
    }

    @Secured(['ROLE_REVISOR', 'ROLE_REVISOR_PUBLICA'])
    def registro(){
        def notificacion = notificacionService.consultar(params).datos
        [ notificacion: notificacion ]
    }


    def consultar(){
        def resultado = notificacionService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect(action: "listarNotificaciones" , params:params)
            return
        }
        def notificacion = resultado.datos

        def bphoto = Base64.getEncoder().encodeToString(notificacion.foto)

        [
            notificacion: notificacion,
            bphoto: bphoto
        ]
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
        def resultado = notificacionService.enviarAFirmarDGEMSS(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect(action: "registro", params: params + [id: params.personaId])
            return
        }

        redirect(action: "listarNotificacionesRevisar")
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
    def listarNotificacionesInstitucion(){
        def institucion = institucionService.consultar(params).datos
        def notificaciones = notificacionService.listarNotificacionesAFirmarPorInstitucion(params).datos
        def usuario = usuarioService.obtenerUsuarioLogueado()
        [
            notificaciones: notificaciones,
            conteo: notificaciones.size(),
            usuario: usuario,
            institucion: institucion,
        ]
    }
    @Secured('ROLE_AUTENTICADOR_DGEMSS')
    def firmaNotificaciones(){
        def usuario = usuarioService.obtenerUsuarioLogueado()
        [
            usuario: usuario
        ]
    }

    @Secured('ROLE_AUTENTICADOR_DGEMSS')
    def firmarNotificaciones(){
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
        def resultado = notificacionService.enviarAFirmarEscuela(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect(action: "listarNotificaciones")
        return
    }

    @Secured('ROLE_GESTOR_ESCUELA')
    def subirFotoModificacion(){
        def notificacion = notificacionService.consultar(params).datos
        def opctitulaciones = OpcTitulacion.getAll()
        def tipodocumentos = TipoDocumento.getAll()
        def bphoto = Base64.getEncoder().encodeToString(notificacion.foto)

        [
            notificacion: notificacion,
            bphoto: bphoto,
            tipodocumentos: tipodocumentos,
            opctitulaciones: opctitulaciones
        ]
    }

    def modificar(){
        if(params.foto){
            this.foto = params.foto
        }else{
            params.foto = this.foto
        }

        def resultado = notificacionService.modificar(params)
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
        redirect(action: "listarNotificaciones", params: [id: params.personaId])
        return
    }

    def eliminar(){
        def resultado = notificacionService.eliminar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect action: "listarNotificaciones"

        return
    }

    def consultarNotificacion(){
        def resultado = notificacionService.consultarNotificacion(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            return
        }

        def notificacion = resultado.datos

        def bphoto = Base64.getEncoder().encodeToString(notificacion.foto)

        [
            notificacion: notificacion,
            bphoto: bphoto
        ]
    }

     @Secured('ROLE_GESTOR_ESCUELA')
    def modificarFotografia() {
        def resultado = notificacionService.modificarFotografia(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        params.foto = null

        if(!resultado.estatus){
            redirect(action: "subirFotoModificacion", params: params)
            return
        }

        redirect(action: "listarNotificaciones")
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
        def resultado = notificacionService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect(action: "listarNotificaciones" , params:params)
            return
        }
        def notificacion = resultado.datos

        def bphoto = Base64.getEncoder().encodeToString(notificacion.foto)

        [
            notificacion: notificacion,
            bphoto: bphoto
        ]
    }

    @Secured(['ROLE_DIRECTOR_ESCUELA', 'ROLE_REVISOR', 'ROLE_REVISOR_PUBLICA', 'ROLE_AUTENTICADOR_DGEMSS'])
    def cambiarEstatusRechazado(){
        def resultado = notificacionService.cambiarEstatusRechazado(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        def roles = usuarioService.obtenerRoles()
		if(roles.contains('ROLE_DIRECTOR_ESCUELA') || roles.contains('ROLE_AUTENTICADOR_DGEMSS')){
		    redirect(action: "listarFirmasNotificaciones")
        }else if(roles.contains('ROLE_REVISOR') || roles.contains('ROLE_REVISOR_PUBLICA')){
            redirect(action: "listarNotificacionesRevisar")
        }
        return
    }

    @Secured('ROLE_DIRECTOR_ESCUELA')
    def cambiarEstatusRechazadoDirector(){
        def resultado = notificacionService.cambiarEstatusRechazadoDirector(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect(action: "listarNotificaciones")
        return
    }

    @Secured(['ROLE_REVISOR', 'ROLE_REVISOR_PUBLICA'])
    def cambiarEstatusRechazadoDgemss(){
        def resultado = notificacionService.cambiarEstatusRechazadoDgemss(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect(action: "listarNotificacionesRevisar")
        return
    }

    @Secured('ROLE_AUTENTICADOR_DGEMSS')
    def cambiarEstatusRechazadoAutenticador(){
        def resultado = notificacionService.cambiarEstatusRechazadoAutenticador(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect(action: "listarNotificacionesRevisar")
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
        def notificacion = notificacionService.consultar(params).datos
        def usuario = usuarioService.obtenerUsuarioLogueado()

        [
            notificacion: notificacion,
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
    def firmarNotificacion(){
        def resultado = notificacionService.firmarNotificacion(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        params.clavePrivada = null

        if(!resultado.estatus){
            redirect(action: "firmar" , params:params)
            return
        }
        redirect(action: "listarFirmasNotificaciones" , params:params)
    }

    def descargarPdf(){
        def resultado = notificacionService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect(action: "listarNotificaciones", params: [id: params.personaId])
            return
        }

        def notificacion = resultado.datos

        if(!notificacion.pdf){
            notificacionService.generarDocumentosNotificacion(notificacion.uuid)
        }

        def pdf = notificacion.pdf
        response.setContentType("application/octet-stream")
        response.setHeader("Content-disposition", "attachment; filename=${notificacion.folioControl}.pdf")
        response.outputStream << pdf
        return
    }

    def mostrarPdf(){
        def resultado = notificacionService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            return
        }

        def notificacion = resultado.datos

        if(!notificacion.pdf){
            notificacionService.generarDocumentosNotificacion(notificacion.uuid)
        }

        def pdf = notificacion.pdf
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
