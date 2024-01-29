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
class ConstanciaservicioController {

    def alumnoService
    def institucionService
    def carreraService
    def cicloEscolarService
    def documentoService
    def efirmaService
    def zxingService
    def constanciaService
    def personaService
    def evaluacionService
    def estatusConstanciaService
    def usuarioService
    def tramiteService
    def planEstudiosService

    /**
     * Permite listar todos los certificados
     *
     * @param params con parametros de pagínado (opcional)
     */
    @Secured(['ROLE_GESTOR_ESCUELA', 'ROLE_DIRECTOR_ESCUELA', 'ROLE_AUTENTICADOR_DGEMSS', 'ROLE_REVISOR', 'ROLE_REVISOR_PUBLICA'])
    def listarConstancias(){
       def instituciones = institucionService.obtenerActivos(params).datos
        def carreras = carreraService.obtenerActivos(params).datos
        def planesEstudios = planEstudiosService.obtenerActivos(params).datos
        def estatusConstancia = estatusConstanciaService.obtenerActivos(params).datos
        def constancias = constanciaService.listarConstancias(params).datos

        [
            constancias: constancias,
            conteo: constancias.totalCount,
            instituciones: instituciones,
            carreras: carreras,
            planesEstudios: planesEstudios,
            estatusConstancia: estatusConstancia
        ]
    }
     @Secured(['ROLE_GESTOR_ESCUELA', 'ROLE_DIRECTOR_ESCUELA', 'ROLE_AUTENTICADOR_DGEMSS', 'ROLE_REVISOR', 'ROLE_REVISOR_PUBLICA', 'ROLE_PRESIDENTE_ESCUELA', 'ROLE_SECRETARIO_ESCUELA', 'ROLE_VOCAL_ESCUELA'])
    def consultarConstanciasfecha(){
        def instituciones = institucionService.obtenerActivos(params).datos
        def carreras = carreraService.obtenerActivos(params).datos
        def planesEstudios = planEstudiosService.obtenerActivos(params).datos
        def constancias = constanciaService.listarConstancias(params).datos
        def formatoFecha = new SimpleDateFormat("yyyy-MM-dd")
        def fechaInicio = params.fechaInicio ? formatoFecha.parse(formatoFecha.format(params.fechaInicio)) : null
        def fechaFin = params.fechaFin ? formatoFecha.parse(formatoFecha.format(params.fechaFin)) : null
    
        def registros = ConstanciaServicio.createCriteria().list {
            eq("activo", true)
            if (fechaInicio && fechaFin) {
                between("fechaRegistro", fechaInicio, fechaFin)
            }
        }
        def etiquetas = []
        def datos = []
        def conteos = [:]
        EstatusConstancia.list()?.each { estatus ->
            conteos[estatus.nombre] = registros.count {
                it.activo && it.estatusConstancia.nombre == estatus.nombre
            }
        }
        EstatusConstancia.list()?.each { estatus ->
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

        render view: 'consultarConstanciasfecha', model: [
            constancias: constancias,
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
            constancias: constancias,
            instituciones: instituciones,
            carreras: carreras,
            planesEstudios: planesEstudios,

            registros: registros
        ]
    }

    /**
     * Permite listar los certificados a firmar
     *
     * @param params con parametros de pagínado (opcional)
     */
    @Secured(['ROLE_AUTENTICADOR_DGEMSS'])
    def listarConstanciasFirmar(){
        def instituciones = institucionService.obtenerActivos(params).datos
        def carreras = carreraService.obtenerActivos(params).datos
        def estatusConstancia = estatusConstanciaService.obtenerActivos(params).datos
        def constancias = constanciaService.listarConstanciaFirmar(params).datos

        [
            constancias: constancias,
            conteo: constancias.totalCount,
            carreras: carreras,
            estatusConstancia: estatusConstancia,
            instituciones: instituciones
        ]
    }

    /**
     * Permite listar los certificados a revisar
     *
     * @param params con parametros de pagínado (opcional)
     */
    @Secured(['ROLE_REVISOR', 'ROLE_REVISOR_PUBLICA'])
    def listarConstanciasRevisar(){
        def instituciones = institucionService.obtenerActivos(params).datos
        def carreras = carreraService.obtenerActivos(params).datos
        def estatusConstancia = estatusConstanciaService.obtenerActivos(params).datos
        def constancias = constanciaService.listarConstanciasRevisar(params).datos

        [
            constancias: constancias,
            conteo: constancias.totalCount,
            carreras: carreras,
            estatusConstancia: estatusConstancia,
            instituciones: instituciones
        ]
    }

    /**
     * Permite listar las constancias a firmar
     *
     * @param params con parametros de pagínado (opcional)
     */
    @Secured(['ROLE_DIRECTOR_ESCUELA', 'ROLE_AUTENTICADOR_DGEMSS'])
    def listarFirmasConstancias(){
        def instituciones = institucionService.obtenerActivos(params).datos
        def carreras = carreraService.obtenerActivos(params).datos
        def planesEstudios = planEstudiosService.obtenerActivos(params).datos
        def constancias = constanciaService.listarConstanciasFirmar(params).datos
        def usuario = usuarioService.obtenerUsuarioLogueado()

        [
            constancias: constancias,
            conteo: constancias.totalCount,
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
    def listarConstanciasAlumno(){
        def alumno = alumnoService.consultar(params).datos
        params.alumnoId = alumno.id
        def constancias = constanciaService.listarConstaciasFinalizadosPorAlumno(params).datos

        [
            alumno: alumno,
            conteo: constancias.totalCount,
            constancias: constancias
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
        def planesEstudios = planEstudiosService.obtenerActivos(params).datos
        def alumnos = alumnoService.listar(params)

        [
            instituciones: instituciones.datos,
            carreras: carreras.datos,
            planesEstudios: planesEstudios,
            alumnos: alumnos.datos,
            conteo: alumnos.datos.totalCount
        ]
    }

    /**
     * Permite mostrar la vista solicitud donde se sube y registra la constancia
     * @param Id
     * Id del alumno al que se desea generar una constancia
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
        def resultado = constanciaService.registrar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        params.logo = null
        params.opc = null

        if(!resultado.estatus){
            redirect(action: "solicitud", params: [id: params.alumnoId])
            return
        }

        redirect(action: "listarConstancias")
        return
    }
    @Secured('ROLE_GESTOR_ESCUELA')
    def subirFotoModificacion(){
        def constancia = constanciaService.consultar(params).datos
        def bphoto = Base64.getEncoder().encodeToString(constancia.foto)

        [
            constancia: constancia,
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
        def resultado = constanciaService.modificarFotografia(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        params.foto = null

        if(!resultado.estatus){
            redirect(action: "subirFotoModificacion", params: params)
            return
        }

        redirect(action: "listarConstancias")
        return
    }

    def modificacion(){
        def constancia = constanciaService.consultar(params).datos
        def formatter = new SimpleDateFormat("yyyy-MM-dd")
        def fechaExpedicion = formatter.format(constancia.fechaExpedicion)
        [
            constancia: constancia,
            fechaExpedicion: fechaExpedicion
        ]
    }

    @Secured(['ROLE_DIRECTOR_ESCUELA', 'ROLE_REVISOR', 'ROLE_REVISOR_PUBLICA', 'ROLE_AUTENTICADOR_DGEMSS'])
    def cambiarEstatusRechazado(){
        def resultado = constanciaService.cambiarEstatusRechazado(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        def roles = usuarioService.obtenerRoles()
		if(roles.contains('ROLE_DIRECTOR_ESCUELA') || roles.contains('ROLE_AUTENTICADOR_DGEMSS')){
		    redirect(action: "listarFirmasConstancias")
        }else if(roles.contains('ROLE_REVISOR') || roles.contains('ROLE_REVISOR_PUBLICA')){
            redirect(action: "listarConstanciasRevisar")
        }
        return
    }

    @Secured('ROLE_DIRECTOR_ESCUELA')
    def cambiarEstatusRechazadoDirector(){
        def resultado = constanciaService.cambiarEstatusRechazadoDirector(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect(action: "listarConstancias")
        return
    }

    @Secured(['ROLE_REVISOR', 'ROLE_REVISOR_PUBLICA'])
    def cambiarEstatusRechazadoDgemss(){
        def resultado = constanciaService.cambiarEstatusRechazadoDgemss(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect(action: "listarConstanciasRevisar")
        return
    }

    @Secured('ROLE_AUTENTICADOR_DGEMSS')
    def cambiarEstatusRechazadoAutenticador(){
        def resultado = constanciaService.cambiarEstatusRechazadoAutenticador(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect(action: "listarConstanciasRevisar")
        return
    }

    def modificar(){
        if(params.foto){
            this.foto = params.foto
        }else{
            params.foto = this.foto
        }

        def resultado = constanciaService.modificar(params)
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
        redirect(action: "listarConstancias", params: [id: params.personaId])
        return
    }

    def consultar(){
        def resultado = constanciaService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect(action: "listarConstancias" , params:params)
            return
        }
        def constancia = resultado.datos

        def bphoto = Base64.getEncoder().encodeToString(constancia.foto)

        [
            constancia: constancia,
            bphoto: bphoto
        ]
    }

    
    def eliminar(){
        def resultado = constanciaService.eliminar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect action: "listarConstancias"

        return
    }
    @Secured('ROLE_GESTOR_ESCUELA')
    def cambiarEstatusFirmandoEscuela(){
        def resultado = constanciaService.enviarAFirmarEscuela(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect(action: "listarConstancias")
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
        def resultado = constanciaService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect(action: "listarConstancias" , params:params)
            return
        }
        def constancia = resultado.datos

        def bphoto = Base64.getEncoder().encodeToString(constancia.foto)

        [
            constancia: constancia,
            bphoto: bphoto
        ]
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
        def constancia = constanciaService.consultar(params).datos
        def usuario = usuarioService.obtenerUsuarioLogueado()

        [
            constancia: constancia,
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
    def firmarConstancia(){
        def resultado = constanciaService.firmarConstancia(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        params.clavePrivada = null

        if(!resultado.estatus){
            redirect(action: "firmar" , params:params)
            return
        }
        redirect(action: "listarFirmasConstancias" , params:params)
    }

    @Secured(['ROLE_REVISOR', 'ROLE_REVISOR_PUBLICA'])
    def registro(){
        def constancia = constanciaService.consultar(params).datos
        [ constancia: constancia ]
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
        def resultado = constanciaService.enviarAFirmarDGEMSS(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect(action: "registro", params: params + [id: params.personaId])
            return
        }

        redirect(action: "listarConstanciasRevisar")
        return
    }

    def consultarConstancia(){
        def resultado = constanciaService.consultarConstancia(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            return
        }

        def constancia = resultado.datos

        def bphoto = Base64.getEncoder().encodeToString(constancia.foto)

        [
            constancia: constancia,
            bphoto: bphoto
        ]
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
    def listarConstanciasInstitucion(){
        def institucion = institucionService.consultar(params).datos
        def constancias = constanciaService.listarConstanciasAFirmarPorInstitucion(params).datos
        def usuario = usuarioService.obtenerUsuarioLogueado()
        [
            constancias: constancias,
            conteo: constancias.size(),
            usuario: usuario,
            institucion: institucion,
        ]
    }
    @Secured('ROLE_AUTENTICADOR_DGEMSS')
    def firmaConstancias(){
        def usuario = usuarioService.obtenerUsuarioLogueado()
        [
            usuario: usuario
        ]
    }

    @Secured('ROLE_AUTENTICADOR_DGEMSS')
    def firmarConstancias(){
        def resultado = constanciaService.firmarConstancias(params)

        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect(action: "firmaConstancias" , params: params )
            return
        }

        redirect(action: "listarConstanciasInstitucion" , params: [id: params.institucionId] )
    }

    def descargarPdf(){
        def resultado = constanciaService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect(action: "listarConstancias", params: [id: params.personaId])
            return
        }

        def constancia = resultado.datos

        if(!constancia.pdf){
            constanciaService.generarDocumentosConstancia(constancia.uuid)
        }

        def pdf = constancia.pdf
        response.setContentType("application/octet-stream")
        response.setHeader("Content-disposition", "attachment; filename=${constancia.folioControl}.pdf")
        response.outputStream << pdf
        return
    }

    def mostrarPdf(){
        def resultado = constanciaService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            return
        }

        def constancia = resultado.datos

        if(!constancia.pdf){
            constanciaService.generarDocumentosConstancia(constancia.uuid)
        }

        def pdf = constancia.pdf
        render file: pdf, contentType: "application/pdf"
        return
    }

    def descargarXml(){
        def resultado = constanciaService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            def xml = resultado.datos.xml
            response.setContentType("application/octet-stream")
            response.setHeader("Content-disposition", "attachment; filename=${resultado.datos.folioControl}.xml")
            response.outputStream << xml
            return
        }

        redirect(action: "listarConstancias", params: [id: params.personaId])
        return

    }


    



}
