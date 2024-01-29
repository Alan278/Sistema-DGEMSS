package sieges
import grails.gorm.transactions.Transactional
import groovy.json.JsonSlurper

import java.text.SimpleDateFormat
import org.hibernate.criterion.CriteriaSpecification
import static org.grails.datastore.gorm.GormStaticApi.*
import org.apache.commons.codec.binary.Base64
import org.apache.commons.ssl.PKCS8Key
import org.apache.commons.codec.binary.Base64
import org.apache.commons.io.FileUtils
import groovy.sql.Sql
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import org.apache.commons.codec.binary.Base64


import grails.gorm.transactions.Transactional
import sieges.ActaProfesional

@Transactional
class ActaService {

    def servletContext
    def efirmaService
    def alumnoService
    def documentoService
    def xmlService
    def zxingService
    def numeroLetrasService
    def bitacoraService
    def messageSource
    def usuarioService
    def springSecurityService
    def actaPublicoService
    def errorService
    def estatusRegistroService
    def dataSource

    /**
    * @author Alan Guevarin
    * @since 2022
    */


     // Id de los EstatusActa
    final GENERADO = 1
    final FIRMANDO_ESCUELA = 2
    final RECHAZADO_DIRECTOR =3
    final EN_ESPERA = 4
    final EN_REVISION = 5
    final RECHAZADO_DGEMSS = 6
    final FIRMANDO_DGEMSS = 7
    final RECHAZADO_AUTENTICADOR = 8
    final FINALIZADO = 9

     /**
     * Obtiene las actas con parametros de paginación y filtrado
     *
     * @param estatusConstancias Filtrar por estatus de la constancia
     * @param institucionId Filtrar por una institución específica
     * @param instituciones Filtrar por instituciones a las que pertenece un usuario
     * @param estatusConstanciaId Filtrar por une status específico
     * @param tieneTramite Filtrar los que tienen o no tramites
     * @param esPublica Filtrar los que son o no de instituciones públicas
     * @param alumnoId Filtrar por alumno
     *
     * @return resultado con el estatus, mensaje y actas
     */
    def listar(params){

        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        // Parametros de paginado
        if(!params.max) params.max = 50
        if(!params.offset) params.offset = 0

        // Parametros de filtrado requeridos
        def instituciones = params.instituciones
        def estatusActas = params.estatusActas
        def tieneTramite = params.tieneTramite
        def esPublica = params.esPublica
        def mes = params.mes ? params.mes.toInteger():null
        def anio = params.anio ? params.anio.toInteger():null

        // Parametros de filtrado opcionales
        def institucionId = params.institucionId ? params.institucionId.toInteger() : null
        def carreraId = params.carreraId ? params.carreraId.toInteger() : null
        def planEstudiosId = params.planEstudiosId ? params.planEstudiosId.toInteger() : null
        def estatusActaId = params.estatusActaId ? params.estatusActaId.toInteger() : null
        def alumnoId = params.alumnoId ? params.alumnoId.toInteger() : null 
        def search = params.search ? params.search : null

        def criteria = {
            createAlias("estatusActa", "e", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno", "a", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno.persona", "p", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno.planEstudios", "plan", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno.planEstudios.carrera", "c", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno.planEstudios.carrera.institucion", "inst", CriteriaSpecification.LEFT_JOIN)
            and{
                eq("activo", true)

                or{
                    instituciones.each{ registro ->
                        eq("inst.id", registro.institucion.id)
                    }
                }

                or{
                    estatusActas.each{ registro ->
                        eq("e.id", registro)
                    }
                }

                if(institucionId != null){
 				    eq("inst.id", institucionId)
 				}

                if(estatusActaId != null){
                    eq("e.id", estatusActaId)
                }

                if(tieneTramite != null){
                    if(tieneTramite)
                        isNotNull("tramite")
                    else{
                        isNull("tramite")
                    }
                }

                if(esPublica != null){
                    eq("inst.publica", esPublica)
                }

                if(alumnoId != null){
                    eq("a.id", alumnoId)
                }

                if(search != null){
                    or{
                        ilike("p.nombre", "%${search}%")
                        ilike("p.primerApellido", "%${search}%")
                        ilike("p.segundoApellido", "%${search}%")
                        ilike("p.curp", "%${search}%")
                        ilike("a.matricula", "%${search}%")
                    }
                }
            }

            order("p.nombre", "asc")
        }


        def actas = ActaProfesional.createCriteria().list(params, criteria)

        resultado.estatus = true
        resultado.mensaje = "Las actas fueron consultadas exitosamenter ☻"
        resultado.datos = actas

        return resultado
    }

    /**
     * Se obtenen todas las actas
     *
     * @param con los parametros de pagínacion (opcional)
     * @Los roles que aparecen son para listar las actas acorde su rol
     *
     * @return resultado con el estatus, mensaje y actas
     */

    def listarActas(params){
        def usuario = usuarioService.obtenerUsuarioLogueado()
        def roles = usuarioService.obtenerRoles()

        params.instituciones = null
        params.estatusActas = null
        params.tieneTramite = null
        params.esPublica = null

        if(roles.contains('ROLE_GESTOR_ESCUELA') || roles.contains('ROLE_DIRECTOR_ESCUELA')|| roles.contains('ROLE_VOCAL_ESCUELA')|| roles.contains('ROLE_SECRETARIO_ESCUELA')|| roles.contains('ROLE_PRESIDENTE_ESCUELA')){
            params.instituciones = usuario.instituciones
        }
        if(roles.contains('ROLE_AUTENTICADOR_DGEMSS')){
            params.esPublica = params.isPublic ? (params.isPublic.equals("false") ? true : false) : false
            params.instituciones = []
        }

        def actas = listar(params)

        return actas
    }

    /**
     * Obtiene las actas a revisar
     *
     * @param params con los parametros de pagínacion (opcional)
     *
     * @return resultado con el estatus, mensaje y actas
     *
     * @Acorde al tipo de usuario se le mostrarán los documentos.
     */
    def listarActasRevisar(params){
        def usuario = usuarioService.obtenerUsuarioLogueado()
        def roles = usuarioService.obtenerRoles()

        params.instituciones = null
        params.estatusActas = [EN_REVISION, RECHAZADO_AUTENTICADOR]
        params.tieneTramite = null
        params.esPublica = null

        if(roles.contains('ROLE_REVISOR')){
            params.esPublica = false
        }

        if(roles.contains('ROLE_REVISOR_PUBLICA')){
            params.esPublica = true
        }

        if(roles.contains('ROLE_REVISOR') && roles.contains('ROLE_REVISOR_PUBLICA')){
            params.esPublica = null
        }

        def actas = listar(params)

        return actas
    }

    /**
     * Obtiene las actas a firmar
     *
     * @param params con los parametros de pagínacion (opcional)
     *
     * @return resultado con el estatus, mensaje y actas
     *
     * @Acorde al tipo de usuario se le mostrarán los documentos.
     */

    def listarActaFirmar(params){
        def usuario = usuarioService.obtenerUsuarioLogueado()
        def roles = usuarioService.obtenerRoles()

        params.instituciones = null
        params.estatusActas = [FIRMANDO_DGEMSS]
        params.tieneTramite = null
        params.esPublica = null

        if(roles.contains('ROLE_AUTENTICADOR_DGEMSS')){
            params.esPublica = false
        }

        if(roles.contains('ROLE_AUTENTICADOR_DGEMSS')){
            params.esPublica = true
        }

        if(roles.contains('ROLE_AUTENTICADOR_DGEMSS')){
            params.esPublica = null
        }

        def actas = listar(params)

        return actas
    }

     def listarActasAFirmarPorInstitucion(params){

        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def criteria = {
            createAlias("estatusActa", "e", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno", "a", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno.persona", "p", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno.planEstudios", "plan", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno.planEstudios.carrera", "c", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno.planEstudios.carrera.institucion", "inst", CriteriaSpecification.LEFT_JOIN)
            and{
                eq("activo", true)

                if(params.id)
                eq("inst.id", params.id.toInteger())

                or{
                    eq("e.id", FIRMANDO_DGEMSS)
                }

                if(params.search){
                    or{
                        ilike("p.nombre", "%${params.search}%")
                        ilike("p.primerApellido", "%${params.search}%")
                        ilike("p.segundoApellido", "%${params.search}%")
                        ilike("p.curp", "%${params.search}%")
                        ilike("a.matricula", "%${params.search}%")
                    }
                }
            }

            order("p.nombre", "asc")
        }

        def actas = ActaProfesional.createCriteria().list(criteria)

        resultado.estatus = true
        resultado.mensaje = "Actas consultadas exitosamente"
        resultado.datos = actas

        return resultado
    }

    
     /**
     * Permite realizar el registro de actas por parte del revisor de DGEMSS (Solo permite registrar lo parametros que se muestran a continuación)
     * @param libro (requerido)
     * @param foja (requerido)
     * @param numero (opcional)
     * @return resultado
     * resultado con el estatus, mensaje y datos del acta
     */
    def enviarAFirmarDGEMSS(params) {
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def datosBitacora = [
            clase: "ActaService",
            metodo: "enviarAFirmarDGEMSS",
            nombre: "Envío para firma de la DGEMSS",
            descripcion: "Envío para firma de la DGEMSS",
            estatus: "ERROR"
        ]

        // Validación de parámetros recibidos
        if(!params.uuid){
            resultado.mensaje = 'El uuid de la acta es un dato requerido'
            return resultado
        }
        def acta = ActaProfesional.findByUuid(params.uuid)
        if(!acta){
            resultado.mensaje = 'Acta no encontrada'
            return resultado
        }
        if(!acta.activo){
            resultado.mensaje = 'Acta inactiva'
            return resultado
        }

        if(!params.libro){
            resultado.mensaje = 'El libro es un dato requerido'
            return resultado
        }
        if(!params.libro.matches("[0-9]*")){
            resultado.mensaje = 'El libro debe de ser un número entero'
            return resultado
        }
        if(!params.foja){
            resultado.mensaje = 'La foja es un dato requerido'
            return resultado
        }
        if(!params.foja.matches("[0-9]*")){
            resultado.mensaje = 'La foja debe de ser un número entero'
            return resultado
        }
        if(!params.numero){
            resultado.mensaje = 'el numero es un dato requerido'
            return resultado
        }
        if(!params.numero.matches("[0-9]*")){
            resultado.mensaje = 'El numero debe de ser un número entero'
            return resultado
        }

        acta.libro = params.libro.toInteger()
        acta.foja = params.foja.toInteger()
        acta.numero = params.numero.toInteger()
        acta.estatusActa = EstatusActa.get(FIRMANDO_DGEMSS)

        if(!acta.save(flush:true)){
			acta.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar(datosBitacora)
            return resultado
		}

        // Se guarda la acción en la bitácora
        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = 'Acta creada exitosamente'
		resultado.datos = acta

        return resultado
    }

    /**
     * Obtiene las actas a firmar
     *
     * @Este apartado esta dirigido hacia el director y el autenticador.
     *
     * @param params con los parametros de pagínacion (opcional)
     *
     * @return resultado con el estatus, mensaje y actas
     */
    def listarActasFirmar(params){
        def usuario = usuarioService.obtenerUsuarioLogueado()
		def roles = usuarioService.obtenerRoles()

        params.instituciones = null
        params.estatusActas = null
        params.tieneTramite = null
        params.esPublica = null

        if(roles.contains('ROLE_DIRECTOR_ESCUELA')){
            params.instituciones = usuario.instituciones
            params.estatusActas = [FIRMANDO_ESCUELA]
        }
        if(roles.contains('ROLE_AUTENTICADOR_DGEMSS')){
            params.instituciones = []
            params.estatusActas = [FIRMANDO_DGEMSS]
        }

        def actas = listar(params)

        return actas
    }

    /**
     * Obtiene las actas finalizados por alumno
     *
     * @param params con los parametros de pagínacion (opcional)
     *
     * @return resultado con el estatus, mensaje y actas
     */
    def listarActasFinalizadosPorAlumno(params){
        def usuario = usuarioService.obtenerUsuarioLogueado()
		def roles = usuarioService.obtenerRoles()

        params.instituciones = null
        params.estatusActas = [FINALIZADO]
        params.tieneTramite = null
        params.esPublica = null

        if(roles.contains('ROLE_GESTOR_ESCUELA') || roles.contains('ROLE_DIRECTOR_ESCUELA')){
            params.instituciones = usuario.instituciones
        }
        if(roles.contains('ROLE_AUTENTICADOR_DGEMSS')){
            params.instituciones = []
        }
        
        def actas = listar(params)

        return actas
    }

    /**
     * Obtiene las actas sin tramite
     *
     * @param params con los parametros de pagínacion (opcional)
     *
     * @return resultado con el estatus, mensaje y actas
     */
    def listarActasSinTramite(params){
		def usuario = usuarioService.obtenerUsuarioLogueado()
		def roles = usuarioService.obtenerRoles()

        params.estatusActas = [EN_ESPERA]
        params.tieneTramite = false
        params.esPublica = false

        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        // Parametros de paginado
        if(!params.max) params.max = 50
        if(!params.offset) params.offset = 0

        // Parametros de filtrado opcionales
        def institucionId = params.institucionId ? params.institucionId.toInteger() : null

        def criteria = {
            createAlias("estatusActa", "e", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno", "a", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno.persona", "p", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno.planEstudios", "plan", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno.planEstudios.carrera", "c", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno.planEstudios.carrera.institucion", "inst", CriteriaSpecification.LEFT_JOIN)
            and{
                eq("activo", true)
                eq("e.id", EN_ESPERA)
 				eq("inst.id", institucionId)
                isNull("tramite")
                eq("inst.publica", false)
            }

            order("p.nombre", "asc")
        }

        def actas = ActaProfesional.createCriteria().list(params, criteria)

        resultado.estatus = true
        resultado.mensaje = "Notificaciones consultados exitosamente"
        resultado.datos = actas

        return resultado
    }

    /**
     * Permite realizar el registro de actas
     * @param foto
     * @return resultado
     * resultado con el estatus, mensaje y datos del acta
     */
    def registrar(params) {
        def resultado = [
            estatus: false,
            pag: null,
            mensaje: '',
            datos: null
        ]

        def datosBitacora = [
            usuario: usuarioService.obtenerUsuarioLogueado(),
            clase: "ActaService",
            metodo: "registrar",
            nombre: "Registro de notificacion",
            descripcion: "Se registró una notificacion",
            estatus: "ERROR"
        ]

        // Validación de parámetros recibidos
        if(!params.alumnoId){
            resultado.mensaje = 'El id del alumno es un dato requerido'
            resultado.pag = 0
            return resultado
        }
        def alumno = Alumno.get(params.alumnoId)
        if(!alumno){
            resultado.mensaje = 'Alumno no encontrado'
            resultado.pag = 0
            return resultado
        }
        if(!alumno.activo){
            resultado.mensaje = 'Alumno inactivo'
            resultado.pag = 0
            return resultado
        }

        // Se valida que el alumno pertenesca a la misma institución que el usuario
        def institucionId = alumno.planEstudios.carrera.institucion.id
        if(!usuarioService.perteneceAInstitucion(institucionId)){
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
			return resultado
		}


        
        if(!params.foto){
            resultado.mensaje = 'La foto es un dato requerido'
            return resultado
        }

        if(!params.archivopdf){
            resultado.mensaje = 'Se requiere de un archivo pdf'
            return resultado
        }

        if(!params.opctitulacionId){
            resultado.mensaje = 'Ingrese el nombre del titulo'
            return resultado
        }

        if(!params.declaracionId){
            resultado.mensaje = 'Ingrese el nombre del titulo'
            return resultado
        }

        if(!params.docId){
            resultado.mensaje = 'Ingrese el tipo de documento'
            return resultado
        }

        if(!params.presidente){
            resultado.mensaje = 'Ingrese el nombre del presidente'
            return resultado
        }

        if(!params.secretario){
            resultado.mensaje = 'Ingrese el nombre del secretario'
            return resultado
        }

        if(!params.vocal){
            resultado.mensaje = 'Ingrese el nombre del vocal'
            return resultado
        }

        if(!params.declaracionId){
            resultado.mensaje = 'Ingrese la declaracion del jurado'
            return resultado
        }


        def acta = new ActaProfesional()
        acta.uuid = efirmaService.generarUuid()
        acta.folioControl = generaIdentificadorEstructurado(alumno.planEstudios.carrera.institucion)
        acta.foto = efirmaService.base64toBytes(params.foto)
        acta.archivopdf = params.archivopdf.getBytes()
        acta.municipio = "CUERNAVACA"
        acta.opctitulacion = OpcTitulacion.get(params.opctitulacionId)
        acta.titulo = params.titulo
        acta.presidente = params.presidente
        acta.secretario = params.secretario
        acta.vocal = params.vocal
        acta.declaracion = Declaracion.get(params.declaracionId)
        acta.doc = TipoDocumento.get(params.docId)
        acta.alumno = alumno
        acta.estatusActa = EstatusActa.get(GENERADO)

        if(!acta.save(flush:true)){
			acta.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar(datosBitacora)
            return resultado
		}


        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = 'Acta creada exitosamente'
		resultado.datos = acta

        return resultado
    }

    def generaIdentificadorEstructurado(institucion){
        def identificador = ""

        def fecha = new Date()

        identificador = (fecha.format("yyyyMMdd")?:"").trim().padLeft(8,"0")
        identificador += "17".trim().padLeft(2,"0")
        identificador += (institucion?.claveCt?:"").trim().padLeft(8,"0")
        identificador += (generaIdentificadorConsecutivo()?:"").padLeft(8,"0")

        return identificador
    }

    def generaIdentificadorConsecutivo() {
        def identificador = ""

        identificador = "${(ActaProfesional.count()?:0) + 1}"

        return identificador
    }

     /**
     * Permite realizar la consulta del registro seleccionado con su información correspondiente.
     * @param id
     * id del acta
     * @return resultado
     * resultado con el estatus, mensaje y datos del acta
     */
    def consultar(params) {
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }

        def acta = ActaProfesional.findByUuid(params.uuid)

       

        if(!acta){
            resultado.mensaje = 'No se ha encontrado la acta'
            return resultado
        }

        if(!acta.activo){
            resultado.mensaje = 'Acta inactiva'
            return resultado
        }


		def roles = usuarioService.obtenerRoles()
		if(roles.contains('ROLE_GESTOR_ESCUELA') || roles.contains('ROLE_DIRECTOR_ESCUELA') || roles.contains('ROLE_VOCAL_ESCUELA') || roles.contains('ROLE_SECRETARIO_ESCUELA') || roles.contains('ROLE_PRESIDENTE_ESCUELA')){
            // Se valida que la acta pertenezca a la misma institución que el usuario. :D
            def institucionId = acta.alumno.planEstudios.carrera.institucion.id
            if(!usuarioService.perteneceAInstitucion(institucionId)){
                resultado.mensaje = 'No esta autorizado para realizar esta acción'
                return resultado
            }
		}

        resultado.estatus = true
        resultado.mensaje = 'Se ha encontrado la acta'
        resultado.datos = acta

        return resultado
    }
    /**
     * Permite realizar la consulta del registro acorde el QR.
     * @param id
     * id del acta
     * @return resultado
     * resultado con el estatus, mensaje y datos del acta
     */
    def consultarActa(params) {
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }

        def acta = ActaProfesional.findByUuid(params.uuid)

        if(!acta || !acta.activo || acta.estatusActa.id != 12){
            resultado.mensaje = 'No se ha encontrado la constancia'
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'La acta se ha encontrado'
        resultado.datos = acta

        return resultado
    }

    def modificar(params) {
		def resultado = [
			estatus: false,
			mensaje: '',
            pag: null,
			datos: null
		]

        def datosBitacora = [
            clase: "ConstanciaService",
            metodo: "modificar",
            nombre: "Modificación de constancia",
            descripcion: "Se modificó una constancia",
            estatus: "ERROR"
        ]

		if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }

        def acta = ActaProfesional.findByUuid(params.uuid)
        if(!acta){
            resultado.mensaje = 'Acta no econtrada'
            return resultado
        }
        if(!acta.activo){
            resultado.mensaje = 'La acta esta inactiva'
            return resultado
        }

        // Se valida que la acta se encuentre en estatus  de generado o rechazado
        if(acta.estatusActa.id != GENERADO && acta.estatusActa.id != RECHAZADO_DGEMSS){
            resultado.mensaje = 'No puede ser modificada'
            return resultado
        }

        // Se valida que la constancia pertenezca a la misma institución que el usuario
        def institucionId = acta.persona.alumnos[0].cicloEscolar.carrera.institucion.id
        if(!usuarioService.perteneceAInstitucion(institucionId)){
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
			return resultado
		}

        def foto = params.foto
        params.foto = null

		if(!foto){
            resultado.mensaje = 'La foto es un dato requerido'
            resultado.pag = 2
            return resultado
        }
        
        if(!params.fechaExpedicion){
            resultado.mensaje = 'La fecha de expedición es un dato requerido'
            resultado.pag = 1
            return resultado
        }

        def fechaExpedicion
        try{
            fechaExpedicion = new SimpleDateFormat("yyyy-MM-dd").parse(params.fechaExpedicion)
        }catch(ex){
            resultado.mensaje = 'La fecha no tiene un formato válido'
            resultado.pag = 1
            return resultado
        }

        params.foto = efirmaService.base64toBytes(foto)

        acta.properties = params
        acta.fechaExpedicion = fechaExpedicion

        if(!acta.save(flush:true)){
			acta.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar(datosBitacora)
            return resultado
		}

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = 'La constancia se ha modificado'
		resultado.datos = acta

		return resultado
	}


     /**
     * Permite rmodificar la fotografia del acta.
     * @param id
     * id del acta
     * @return resultado
     * resultado con el estatus, mensaje y datos del acta
     */
    def modificarFotografia(params) {
		def resultado = [
			estatus: false,
			mensaje: '',
            pag: null,
			datos: null
		]

        def datosBitacora = [
            clase: "ActaService",
            metodo: "modificarFotografia",
            nombre: "Modificación de fotografia",
            descripcion: "Se ha modificado su notificacion",
            estatus: "ERROR"
        ]

		if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }

        def acta = ActaProfesional.findByUuid(params.uuid)
        if(!acta){
            resultado.mensaje = 'No se ha encontrado la acta'
            return resultado
        }
        if(!acta.activo){
            resultado.mensaje = 'acta inactiva'
            return resultado
        }

        if(!params.foto){
            resultado.mensaje = 'La foto es un dato requerido'
            return resultado
        }

        params.foto = efirmaService.base64toBytes(params.foto)
        acta.archivopdf = params.archivopdf.getBytes()
        acta.opctitulacion = OpcTitulacion.get(params.opctitulacionId)
        acta.titulo = params.titulo
        acta.declaracion = Declaracion.get(params.declaracionId)
        acta.doc = TipoDocumento.get(params.docId)
        acta.presidente = params.presidente
        acta.secretario = params.secretario
        acta.vocal = params.vocal
        acta.foto = params.foto

        if(!acta.save(flush:true)){
			acta.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar(datosBitacora)
            return resultado
		}

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = 'La acta se ha modificado'
		resultado.datos = acta

        return resultado
	}

      /**
     * Permite eliminar un certificado
     * @param params (requerido)
     * parametros de la carrera
     * @return resultado
     * resultado con el estatus, mensaje y datos de la carrera
     */

	def eliminar(params){
		def resultado = [
				estatus: false,
				mensaje: '',
				datos: null
		]

        def datosBitacora = [
            clase: "ActaService",
            metodo: "eliminar",
            nombre: "Eliminación de acta",
            descripcion: "Se eliminó una acta",
            estatus: "ERROR"
        ]

		if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }

        def acta = ActaProfesional.findByUuid(params.uuid)
        if(!acta){
            resultado.mensaje = 'Acta no encontrada'
            return resultado
        }
        if(!acta.activo){
            resultado.mensaje = 'Acta inactiva'
            return resultado
        }

        if(acta.estatusActa.id != GENERADO){
            resultado.mensaje = 'El acta no puede ser eliminada'
            return resultado
        }

        // Se valida que el acta pertenezca a la misma institución que el usuario
        def institucionId = acta.alumno.planEstudios.carrera.institucion.id
        if(!usuarioService.perteneceAInstitucion(institucionId)){
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
			return resultado
		}

		acta.activo = false

		if(!acta.save(flush:true)){
			acta.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar(datosBitacora)
			return resultado
		}

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = 'Acta dada de baja exitosamente'
		resultado.datos = acta

		return resultado
	}

     /* *Nos permite rechazar diferebtes notificaciones segun el tipo de usuario
    *Segun el tipo de usuario realizo el chazo tal como veremos posteiormente en el codigo
    */
    def cambiarEstatusRechazado(params){
        def roles = usuarioService.obtenerRoles()

		if(roles.contains('ROLE_DIRECTOR_ESCUELA') || roles.contains('ROLE_PRESIDENTE_ESCUELA') || roles.contains('ROLE_SECRETARIO_ESCUELA') || roles.contains('ROLE_VOCAL_ESCUELA')){
            return rechazarActaEscuela(params)
        }

        if(roles.contains('ROLE_REVISOR') || roles.contains('ROLE_REVISOR_PUBLICA')){
            return rechazarActaRevisor(params)
        }
        
        if(roles.contains('ROLE_AUTENTICADOR_DGEMSS')){
            return rechazarActaDGEMSS(params)
        }
	}

    def rechazarActaEscuela(params){
		def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

        def datosBitacora = [
            clase: "ActaService",
            metodo: "rechazarActaEscuela",
            nombre: "Rechazo de notificacion - ESCUELA",
            descripcion: "Rechazo de notificacion - ESCUELA",
            estatus: "ERROR"
        ]

        // Se validan los parametros recibidos
		if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }
        def acta = ActaProfesional.findByUuid(params.uuid)
        if(!acta){
            resultado.mensaje = 'Acta no encontrado'
            return resultado
        }
        if(!acta.activo){
            resultado.mensaje = 'Acta inactivo'
            return resultado
        }

        // Se valida que el acta se encuentre en un estatus válido para la acción
        def actaEstatusId = acta.estatusActa.id
        def estatusValidos = [FIRMANDO_ESCUELA]

        if(!(actaEstatusId in estatusValidos)){
            resultado.mensaje = 'No se puede rechazar el acta'
            return resultado
        }

        
        // Se valida que el usuario sea un director de escuela
		def roles = usuarioService.obtenerRoles()
		if(!roles.contains('ROLE_DIRECTOR_ESCUELA')){
            resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
		}

        // Se valida que el acta pertenezca a la misma institución que el director
        def institucionId = acta.alumno.planEstudios.carrera.institucion.id
        if(!usuarioService.perteneceAInstitucion(institucionId)){
            resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
        }

		acta.estatusActa = EstatusActa.get(RECHAZADO_DIRECTOR)

		if(!acta.save(flush:true)){
			acta.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			return resultado
		}

        // Se cambie el estatus del alumno, sus evaluaciones y sus ciclos para que se puedan editar nuevamente
        if(!cambiarEstatusRegistros(acta, estatusRegistroService.EDITABLE)){
            transactionStatus.setRollbackOnly()
            resultado.mensaje = "Hubo un error al intentar enviar el certificado a firmar. Por favor inténtelo nuevamente."
			return resultado
        }

		resultado.estatus = true
		resultado.mensaje = 'Acta rechazada'
		resultado.datos = acta

		return resultado
	}

    def rechazarActaRevisor(params){
		def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

        def datosBitacora = [
            clase: "ActaService",
            metodo: "rechazarActaRevisor",
            nombre: "Rechazo de acta - REVISOR",
            descripcion: "Rechazo de acta - REVISOR",
            estatus: "ERROR"
        ]

		if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }

        if(!params.comentarios){
            resultado.mensaje = 'Los comentarios son un dato requerido'
            return resultado
        }

        def acta = ActaProfesional.findByUuid(params.uuid)
        if(!acta){
            resultado.mensaje = 'Notificacion no encontrado'
            return resultado
        }
        if(!acta.activo){
            resultado.mensaje = 'Notificacion inactivo'
            return resultado
        }

        def actaEstatusId = acta.estatusActa.id
        def estatusValidos = [EN_REVISION, RECHAZADO_AUTENTICADOR]

        if(!(actaEstatusId in estatusValidos)){
            resultado.mensaje = 'No se puede cambiar el estatus'
            return resultado
        }

        // La validación solo se realiza si la acción la hace un usuario REVISOR
		def roles = usuarioService.obtenerRoles()
		if(!(roles.contains('ROLE_REVISOR') || roles.contains('ROLE_REVISOR_PUBLICA'))){
            resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
		}

        def firmaDirectorEscuela = acta.firmaDirectorEscuela

		acta.estatusActa = EstatusActa.get(RECHAZADO_DGEMSS)
		acta.comentarios = params.comentarios
        acta.firmaDirectorEscuela = null

		if(!acta.save(flush:true)){
			acta.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			return resultado
		}

        // Se cambie el estatus del alumno, sus evaluaciones y sus ciclos para que se puedan editar nuevamente
        if(!cambiarEstatusRegistros(acta, estatusRegistroService.EDITABLE)){
            transactionStatus.setRollbackOnly()
            resultado.mensaje = "Hubo un error al intentar rechazar la acta. Por favor inténtelo nuevamente."
			return resultado
        }

        firmaDirectorEscuela.delete()

		resultado.estatus = true
		resultado.mensaje = 'Acta modificado exitosamente'
		resultado.datos = acta

		return resultado
	}

    def rechazarActaDGEMSS(params){
		def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

        def datosBitacora = [
            clase: "ActaService",
            metodo: "rechazarActaDGEMSS",
            nombre: "Rechazo de acta - DGEMSS",
            descripcion: "Rechazo de acta - DGEMSS",
            estatus: "ERROR"
        ]

		if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }

        if(!params.comentarios){
            resultado.mensaje = 'Los comentarios son un dato requerido'
            return resultado
        }

        def acta = ActaProfesional.findByUuid(params.uuid)
        if(!acta){
            resultado.mensaje = 'Acta no encontrado'
            return resultado
        }
        if(!acta.activo){
            resultado.mensaje = 'Acta inactivo'
            return resultado
        }

        def actaEstatusId = acta.estatusActa.id
        def estatusValidos = [FIRMANDO_DGEMSS]

        if(!(actaEstatusId in estatusValidos)){
            resultado.mensaje = 'No se puede cambiar el estatus'
            return resultado
        }

        // La validación solo se realiza si la acción la hace un usuario AUTENTICADOR
		def roles = usuarioService.obtenerRoles()
		if(!(roles.contains('ROLE_AUTENTICADOR_DGEMSS'))){
            resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
		}

		acta.estatusActa = EstatusActa.get(RECHAZADO_AUTENTICADOR)
		acta.comentarios = params.comentarios

		if(!acta.save(flush:true)){
			acta.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			return resultado
		}

		resultado.estatus = true
		resultado.mensaje = 'Acta modificado exitosamente'
		resultado.datos = acta

		return resultado
	}



    def enviarAFirmarEscuela(params){
		def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

        def datosBitacora = [
            clase: "ActaService",
            metodo: "enviarAFirmarEscuela",
            nombre: "Envío para firma de escuela",
            descripcion: "Envío para firma de escuela",
            estatus: "ERROR"
        ]

        // Se validan los parametros recibidos
		if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }

        // Se valida la acta
        def acta = ActaProfesional.findByUuid(params.uuid)
        if(!acta){
            resultado.mensaje = 'Acta no encontrado'
            return resultado
        }
        if(!acta.activo){
            resultado.mensaje = 'Acta inactivo'
            return resultado
        }

        // Se valida que la acta se encuentre en un estatus válido para la acción
        def actaEstatusId = acta.estatusActa.id
        def estatusValidos = [GENERADO, RECHAZADO_DGEMSS, RECHAZADO_DIRECTOR]

        if(!(actaEstatusId in estatusValidos)){
            resultado.mensaje = 'No se puede cambiar el estatus'
            return resultado
        }

        // Se valida que la acción la realice un usuario de tipo Gestor de Escuela
		def roles = usuarioService.obtenerRoles()
		if(!roles.contains('ROLE_GESTOR_ESCUELA')){
            resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
		}

        // Se valida que la acta pertenezca a la misma institución que el usuario
        def institucionId = acta.alumno.planEstudios.carrera.institucion.id
        if(!usuarioService.perteneceAInstitucion(institucionId)){
            resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
        }

        // Se asigna el nuevo estatus
		acta.estatusActa = EstatusActa.get(FIRMANDO_ESCUELA)
		
		if(!acta.save(flush:true)){
            transactionStatus.setRollbackOnly()
            resultado.mensaje = errorService.obtenerErrores(acta)[0]
			return resultado
		}

        // Se cambie el estatus del alumno, sus evaluaciones y sus ciclos para que no puedan se editados durante el proceso
        if(!cambiarEstatusRegistros(acta, estatusRegistroService.NO_EDITABLE)){
            transactionStatus.setRollbackOnly()
            resultado.mensaje = "Hubo un error al intentar enviar el certificado a firmar. Por favor inténtelo nuevamente."
			return resultado
        }

        // Se guarda la acción en la bitácora
        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = 'La accion fue exitosa'
		resultado.datos = acta

		return resultado
	}

    def cambiarEstatusRegistros(acta, estatusRegistroId){
        def nuevoEstatusRegistro = EstatusRegistro.get(estatusRegistroId)

        def alumno = acta.alumno

        // Se cambia el estatus del alumno si no esta bloqueado
        if(alumno.estatusRegistro.id != estatusRegistroService.BLOQUEADO){
            alumno.estatusRegistro = nuevoEstatusRegistro

            if(!alumno.save(flush: true)){
                return false
            }          
        }

        // Se cambia el estatus de las evaluaciones si no estan bloqueadas
        for (evaluacion in alumno.evaluaciones) {
            if(!evaluacion.activo) continue
            if(evaluacion.estatusRegistro.id == estatusRegistroService.BLOQUEADO) continue

            evaluacion.estatusRegistro = nuevoEstatusRegistro
            if(!evaluacion.save(flush: true)){
                return false
            }
        }

        // Se cambia el estatus de las inscripciones del alumno
        for (inscripcion in alumno.ciclosEscolares) {
            if(!inscripcion.activo) continue
            if(inscripcion.estatusRegistro.id == estatusRegistroService.BLOQUEADO) continue

            inscripcion.estatusRegistro = nuevoEstatusRegistro
            if(!inscripcion.save(flush: true)){
                return false
            }

        }

        // Se cambia el estatus de los ciclos a los que esta incrito el alumno si no estan bloqueados
        for (inscripcion in alumno.ciclosEscolares) {
            def cicloEscolar = inscripcion.cicloEscolar

            if(!inscripcion.activo) continue
            if(inscripcion.estatusRegistro.id == estatusRegistroService.BLOQUEADO) continue

            // Si lo que se intenta es habilitar el registro para su edición
            // se valida que el ciclo escolar no cuente con más relaciones bloquedas
            if(nuevoEstatusRegistro.id == estatusRegistroService.EDITABLE){
                def sql = new Sql(dataSource)
                def numInscripcionesBloqueadas = sql.rows(
                    "SELECT COUNT(*) as num " +
                    "FROM alumno_ciclo_escolar WHERE " + 
                    "activo = true AND " + 
                    "ciclo_escolar_id = ${cicloEscolar.id} AND " + 
                    "estatus_registro_id <> 1 " + 
                    "GROUP BY ciclo_escolar_id"
                )
                if(numInscripcionesBloqueadas) continue
            }

            cicloEscolar.estatusRegistro = nuevoEstatusRegistro
            
            if(!cicloEscolar.save(flush: true)){
                return false
            }

        }

        return true
    }

    def firmarActa(params){
        def roles = usuarioService.obtenerRoles()

        if(roles.contains('ROLE_DIRECTOR_ESCUELA')){
            return firmarActaEscuela(params)
        }
        
        if(roles.contains('ROLE_AUTENTICADOR_DGEMSS')){
            return firmarActaDGEMSS(params)
        }
    }
    def obtenerCadenaOriginalFirmaEscuela(parametros, fechaGeneracion){

        def fechaGeneracionActa = new SimpleDateFormat("YYYY-MM-dd'T'hh:mm:ss").format(fechaGeneracion)

        def cadenaOriginal = "||2.0"

        cadenaOriginal += "|5" // tipoCertificado
        cadenaOriginal += "|${parametros.uuid}"
        cadenaOriginal += "|${parametros.folioControl}"
        cadenaOriginal += "|${parametros.institucionId}"
        cadenaOriginal += "|${parametros.institucion}"
        cadenaOriginal += "|${parametros.institucionClaveCt}"
        cadenaOriginal += "|${parametros.carrera}"
        cadenaOriginal += "|${parametros.carreraNivel}"
        cadenaOriginal += "|${parametros.matricula}"
        cadenaOriginal += "|${parametros.curp}"
        cadenaOriginal += "|${parametros.nombre}"
        cadenaOriginal += "|${parametros.primerApellido}"
        cadenaOriginal += "|${parametros.segundoApellido}"
        cadenaOriginal += "|${parametros.fechaNacimiento}"
        cadenaOriginal += "|${fechaGeneracionActa}"

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
    /**
     * Permite registrar una acta
     *
     * @param alumnoId (requerido)
     * Id del alumno al que se desea generar una acta
     * @param foto
     * @param doc
     * @param n presidente
     * @param n vocal
     * @param n secertario
     * @param titulo
     * @param opcTitulacion
     * Foto del alumno 
     *
     */

    def firmarActaEscuela(params){
		def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

        def datosBitacora = [
            clase: "ActaService",
            metodo: "firmarActaEscuela",
            nombre: "Firma de notificacion - Escuela",
            descripcion: "Firma de notificacion - Escuela",
            estatus: "ERROR"
        ]

        // Se validan los parametros recibidos
        if(!params.firmaId){
            resultado.mensaje = "El id de la firma es un dato requerido"
            return resultado
        }

        def persona = usuarioService.obtenerUsuarioLogueado().persona
        def firmaElectronica = FirmaElectronica.get(params.firmaId)

        if(!firmaElectronica){
            resultado.mensaje = "Firma no encontrada"
            return resultado
        }

        def perteneceAUsuario = persona.firmasElectronicas.any{ firma -> firma.id == firmaElectronica.id }

        if(!perteneceAUsuario){
            resultado.mensaje = "Permiso denegado"
            return resultado
        }

        if(firmaElectronica.expiro()){
            resultado.mensaje = 'El certificado (.cer) ha expirado'
            return resultado
        }

        // Se valida la clave privada y contraseña
        def bytesClavePrivada
        def contrasena

        if(!firmaElectronica.archivoKey){
            if(!params.clavePrivada){
                resultado.mensaje = 'La clave privada es un dato requerido'
                return resultado
            }
            bytesClavePrivada = params.clavePrivada.getBytes()
        }else{
            bytesClavePrivada = firmaElectronica.archivoKey
        }

        if(!params.contrasena){
            resultado.mensaje = 'La contraseña es un dato requerido'
            return resultado
        }
        contrasena = params.contrasena

        def resultadoValidacion = efirmaService.validarContrasena(bytesClavePrivada, contrasena)
        if (!resultadoValidacion){
            resultado.mensaje = 'La clave privada o contraseña son incorrectos'
            return resultado
        }

        if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }

        def acta = ActaProfesional.findByUuid(params.uuid)
        if(!acta){
            resultado.mensaje = 'Certificado no encontrado'
            return resultado
        }
        if(!acta.activo){
            resultado.mensaje = 'Certificado inactivo'
            return resultado
        }

        // Se valida que el certificado se encuentre en un estatus válido para la acción
        def actaEstatusId = acta.estatusActa.id
        def estatusValidos = [FIRMANDO_ESCUELA]

        if(!(actaEstatusId in estatusValidos)){
            resultado.mensaje = 'La acta no puede ser firmada'
            return resultado
        }

        def roles = usuarioService.obtenerRoles()

        def zona = ZoneId.systemDefault()
        def fechaFirma = Date.from(LocalDate.now().atStartOfDay(zona).toInstant())
        def parametros
        def cadenaOriginal

        parametros = obtenerParametrosActa(acta)
        cadenaOriginal = obtenerCadenaOriginalFirmaEscuela(parametros, fechaFirma)

        def selloDigital = efirmaService.firmar(bytesClavePrivada, contrasena, cadenaOriginal)

        def firma = new Firma()
        firma.selloDigital = selloDigital
        firma.firmaElectronica = firmaElectronica
        firma.fechaFirma = fechaFirma

        if(!firma.save(flush:true)){
            firma.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			return resultado
        }

        acta.firmaDirectorEscuela = firma
        // Validar estatus de procedencia con el tramite id
        if(acta.alumno.planEstudios.carrera.institucion.publica){
            acta.estatusActa = EstatusActa.get(EN_REVISION)  
        }else if(acta.tramite != null){
            acta.estatusActa = EstatusActa.get(EN_REVISION)
        }else{
            acta.estatusActa = EstatusActa.get(EN_ESPERA)
        }

        if(!acta.save(flush:true)){
			acta.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			return resultado
		}

        // Se guarda la acción en la bitácora
        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

        resultado.estatus = true
        resultado.mensaje = "Constancia firmada exitosamente"
        return resultado
    }

    def obtenerParametrosActa(acta){
        def alumno = acta.alumno
        def persona = alumno.persona
        def planEstudios = alumno.planEstudios
        def carrera = alumno.planEstudios.carrera
        def institucion = carrera.institucion

        def asignaturas = Asignatura.createCriteria().list {
            createAlias("planEstudios", "p", CriteriaSpecification.LEFT_JOIN)
            createAlias("formacion", "f", CriteriaSpecification.LEFT_JOIN)
            createAlias("formacion.tipoFormacion", "tf", CriteriaSpecification.LEFT_JOIN)
            and{
                eq("activo", true)
                eq("tf.nombre", "BASICA")
                eq("p.id", planEstudios.id)
            }
        }

        // formateadores de fecha
        def formatoFechaXml = new SimpleDateFormat("YYYY-MM-dd'T'hh:mm:ss")
        def formatoFechaPdf = new SimpleDateFormat("dd/MM/yyyy")
        def formatoFechaRvoe = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy")

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
        parametros.institucionClaveCt = institucion.claveCt ? institucion.claveCt.toUpperCase() : ""
        parametros.logo = new ByteArrayInputStream(institucion.logo)
        parametros.rvoe = planEstudios.rvoe ? planEstudios.rvoe.trim().toUpperCase() : ""
        parametros.fechaRvoe = planEstudios.fechaRvoe ? formatoFechaRvoe.format(planEstudios.fechaRvoe).trim().toUpperCase() : ""
        parametros.fechaRvoeCadena = planEstudios.fechaRvoe ? formatoFechaXml.format(planEstudios.fechaRvoe).trim().toUpperCase() : ""
            //carrera
        parametros.carrera = carrera.nombre.trim().toUpperCase()
        parametros.modalidad = carrera.modalidad ? carrera.modalidad.nombre.trim().toUpperCase() : ""
        parametros.claveSeem = carrera.claveSeem ? carrera.claveSeem.trim().toUpperCase() : ""
        parametros.carreraNivel = carrera.nivel ? carrera.nivel.nombre.trim().toUpperCase() : ""
            //acta
        parametros.foto = new ByteArrayInputStream(acta.foto)
        parametros.uuid = acta.uuid
        parametros.folioControl = acta.folioControl
        parametros.opctitulacion = acta.opctitulacion.nombre
        parametros.titulo = acta.titulo
        parametros.presidente = acta.presidente
        parametros.vocal = acta.vocal
        parametros.secretario = acta.secretario
        parametros.doc = acta.doc.nombre
        parametros.declaracion = acta.declaracion.nombre
        parametros.libro = acta.libro ? acta.libro : ""
        parametros.foja = acta.foja ? acta.foja : ""
        parametros.numero = acta.numero ? acta.numero : ""
       

        parametros.numPaginas = 2

        if(acta.estatusActa.id == FINALIZADO){
            //datos firmantes escuela

            //Director
            def datosCerDirector = acta.firmaDirectorEscuela.firmaElectronica

            parametros.firmanteEscuelaSello = acta.firmaDirectorEscuela.selloDigital
            parametros.firmanteEscuelaCer = efirmaService.bytesToBase64(datosCerDirector.archivoCer)
            parametros.firmanteEscuelaNoSerie = datosCerDirector.numeroSerieCer.trim().toUpperCase()
            parametros.firmanteEscuelaCurp = datosCerDirector.curpCer.trim().toUpperCase()
            parametros.firmanteEscuelaNombre = datosCerDirector.persona.nombre.trim().toUpperCase()
            parametros.firmanteEscuelaPrimerApellido = datosCerDirector.persona.primerApellido.trim().toUpperCase()
            parametros.firmanteEscuelaSegundoApellido = datosCerDirector.persona.segundoApellido.trim().toUpperCase()
            parametros.firmanteEscuelaNombreCompleto = datosCerDirector.nombreCer.trim().toUpperCase()
            parametros.firmanteEscuelaTitulo = datosCerDirector.persona.titulo.trim().toUpperCase()
            parametros.firmanteEscuelaFecha = formatoFechaXml.format(acta.firmaDirectorEscuela.fechaFirma)
            parametros.firmanteEscuelaCargo = datosCerDirector.persona.usuario.cargo.trim().toUpperCase()

            //datos firmante dgmess
            def datosCerAutenticador = acta.firmaAutenticadorDgemss.firmaElectronica

            parametros.firmanteDgemssSello = acta.firmaAutenticadorDgemss.selloDigital
            parametros.firmanteDgemssNoSerie = datosCerAutenticador.numeroSerieCer.trim().toUpperCase()
            parametros.firmanteDgemssCurp = datosCerAutenticador.curpCer.trim().toUpperCase()
            parametros.firmanteDgemssNombreCompleto = datosCerAutenticador.nombreCer.trim().toUpperCase()
            parametros.firmanteDgemssTitulo = datosCerAutenticador.persona.titulo.trim().toUpperCase()
            parametros.firmanteDgemssFecha = formatoFechaPdf.format(acta.firmaAutenticadorDgemss.fechaFirma)
            parametros.firmanteDgemssFechaCadena = formatoFechaXml.format(acta.firmaAutenticadorDgemss.fechaFirma)
            parametros.firmanteDgemssCargo = datosCerAutenticador.persona.usuario.cargo.trim().toUpperCase()

            //Generar código QR
            def url = "actaprofesional/consultarActa?uuid=${acta.uuid}"
            def resultadoQr = zxingService.generarQr(url)
            if (resultadoQr.estatus) {
                parametros.qr = new ByteArrayInputStream(resultadoQr.qr)
            }
        }

        return parametros
    }

    /**
     * Permite firma una acta
     *
     * @param alumnoId (requerido)
     * Id del alumno al que se desea generar una acta
     * @param foto
     * @param doc
     * @param n presidente
     * @param n vocal
     * @param n secertario
     * @param titulo
     * @param opcTitulacion
     * @param firma_DirectorEscuela
     * Foto del alumno 
     *
     */
    def firmarActaDGEMSS(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

        def datosBitacora = [
            clase: "ActaService",
            metodo: "firmarActaDGEMSS",
            nombre: "Firma de la acta - DGEMSS",
            descripcion: "Firma de acta - DGEMSS",
            estatus: "ERROR"
        ]

        // Se validan los parametros recibidos
        if(!params.firmaId){
            resultado.mensaje = "El id de la firma es un dato requerido"
            return resultado
        }

        def persona = usuarioService.obtenerUsuarioLogueado().persona
        def firmaElectronica = FirmaElectronica.get(params.firmaId)

        if(!firmaElectronica){
            resultado.mensaje = "Firma no encontrada"
            return resultado
        }

        def perteneceAUsuario = persona.firmasElectronicas.any{ firma -> firma.id == firmaElectronica.id }

        if(!perteneceAUsuario){
            resultado.mensaje = "Permiso denegado"
            return resultado
        }

        if(firmaElectronica.expiro()){
            resultado.mensaje = 'El certificado (.cer) ha expirado'
            return resultado
        }

        // Se valida la clave privada y contraseña
        def bytesClavePrivada
        def contrasena
        
        if(!firmaElectronica.archivoKey){
            if(!params.clavePrivada){
                resultado.mensaje = 'La clave privada es un dato requerido'
                return resultado
            }
            bytesClavePrivada = params.clavePrivada.getBytes()
        }else{
            bytesClavePrivada = firmaElectronica.archivoKey
        }

        if(!params.contrasena){
            resultado.mensaje = 'La contraseña es un dato requerido'
            return resultado
        }
        contrasena = params.contrasena

        def resultadoValidacion = efirmaService.validarContrasena(bytesClavePrivada, contrasena)
        if (!resultadoValidacion){
            resultado.mensaje = 'La clave privada o contraseña son incorrectos'
            return resultado
        }

        if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }
        
        def acta = ActaProfesional.findByUuid(params.uuid)
        if(!acta){
            resultado.mensaje = 'acta no encontrada'
            return resultado
        }
        if(!acta.activo){
            resultado.mensaje = 'acta inactiva'
            return resultado
        }

        // Se valida que la constancia se encuentre en un estatus válido para la acción
        def actaEstatusId = acta.estatusActa.id
        def estatusValidos = [FIRMANDO_DGEMSS]

        if(!(actaEstatusId in estatusValidos)){
            resultado.mensaje = 'La acta no puede ser firmada'
            return resultado
        }

        def roles = usuarioService.obtenerRoles()
        
        def zona = ZoneId.systemDefault()
        def fechaFirma = Date.from(LocalDate.now().atStartOfDay(zona).toInstant())
        def cadenaOriginal

        cadenaOriginal = obtenerCadenaOriginalFirmaDgemss(
            fechaFirma, 
            acta.firmaDirectorEscuela.selloDigital,
            firmaElectronica
        )
        
        def selloDigital = efirmaService.firmar(bytesClavePrivada, contrasena, cadenaOriginal)

        def firma = new Firma()
        firma.selloDigital = selloDigital
        firma.firmaElectronica = firmaElectronica
        firma.fechaFirma = fechaFirma

        if(!firma.save(flush:true)){
            firma.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			return resultado
        }
        
        acta.firmaAutenticadorDgemss = firma
        acta.estatusActa = EstatusActa.get(FINALIZADO)

        if(!acta.save(flush:true)){
			acta.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			return resultado
		}

        // Se cambie el estatus del alumno, sus evaluaciones y sus ciclos para que no puedan se modificados más
        if(!cambiarEstatusRegistros(acta, estatusRegistroService.BLOQUEADO)){
            transactionStatus.setRollbackOnly()
            resultado.mensaje = "Hubo un error al intentar enviar la constancia a firmar. Por favor inténtelo nuevamente."
			return resultado
        }

        // Se guarda la acción en la bitácora
        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

        resultado.estatus = true
        resultado.mensaje = "Acta firmada exitosamente"
        return resultado
    }
    //enviarAfirmarD

    def obtenerCadenaOriginalFirmaDgemss(fechaExpedicion, selloDirectorEscuela, firmaElectronica){

        def fechaExpedicionActa = new SimpleDateFormat("YYYY-MM-dd'T'hh:mm:ss").format(fechaExpedicion)

        def cadenaOriginal = "||"
        cadenaOriginal += "|${selloDirectorEscuela}"
        cadenaOriginal += "|${firmaElectronica.numeroSerieCer.trim().toUpperCase()}"
        cadenaOriginal += "|${firmaElectronica.curpCer.trim().toUpperCase()}"
        cadenaOriginal += "|${firmaElectronica.persona.usuario.cargo.trim().toUpperCase()}"
        
        cadenaOriginal += "||"

        return cadenaOriginal
    }

    def obtenerCadenaOriginalPdf(parametros){

        def cadenaOriginal = "||2.0"

        cadenaOriginal += "|${parametros.uuid}"
        cadenaOriginal += "|${parametros.folioControl}"
        cadenaOriginal += "|${parametros.institucionId}"
        cadenaOriginal += "|${parametros.institucion}"
        cadenaOriginal += "|${parametros.institucionClaveCt}"
        cadenaOriginal += "|${parametros.institucionMunicipio}"
        cadenaOriginal += "|${parametros.carrera}"
        cadenaOriginal += "|${parametros.matricula}"
        cadenaOriginal += "|${parametros.curp}"
        cadenaOriginal += "|${parametros.nombre}"
        cadenaOriginal += "|${parametros.primerApellido}"
        cadenaOriginal += "|${parametros.segundoApellido}"
        cadenaOriginal += "|${parametros.fechaNacimiento}"
        cadenaOriginal += "|${parametros.firmanteEscuelaFecha}"
        
        
        
        cadenaOriginal += "||"

        return cadenaOriginal
    }

    //El nombre de la funcion es generarDocumentosActa
    //* Esta funcion la que realiza es la generacion de los documentos.
    //* Recibe como parametros todos los atributos de la clase ActaProfesional y tambien recibe los de las clases alumno e institución.
    def generarDocumentosActa(actaUuid){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

        def acta = ActaProfesional.findByUuid(actaUuid)
        def parametrosActa = []
        def cadenaOriginal

        parametrosActa = obtenerParametrosActa(acta)
        cadenaOriginal = obtenerCadenaOriginalPdf(parametrosActa)

        parametrosActa.cadenaOriginal = cadenaOriginal
        // Se genera el archivo pdf
        def nombrePlantilla 
        
        if(parametrosActa.doc == "Diploma"){
            nombrePlantilla="acta"
        }else  if(parametrosActa.doc == "Grado"){
            nombrePlantilla="acta_grado"
        }else  if(parametrosActa.doc == "Titulo"){
            nombrePlantilla="acta_profesional"
        }

        def resultadoExpedicionPdf = documentoService.generar(nombrePlantilla, parametrosActa)
        if (!resultadoExpedicionPdf.estatus) {
            resultado.mensaje = resultadoExpedicionPdf.mensaje
            return resultado
        }

        // Se genera el archivo xml
        def resultadoExpedicionXml = xmlService.generar(parametrosActa)
        if (!resultadoExpedicionXml.estatus) {
            resultado.mensaje = resultadoExpedicionXml.mensaje
            return resultado
        }
        

        acta.pdf = resultadoExpedicionPdf.documento
        acta.xml = resultadoExpedicionXml.documento

		if(!acta.save(flush:true)){
			acta.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			return resultado
		}

		resultado.estatus = true
		resultado.mensaje = 'Documentos generados exitosamente'
		resultado.datos = acta

		return resultado
    }

}
