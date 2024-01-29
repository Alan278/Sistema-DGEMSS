package sieges

import grails.gorm.transactions.Transactional
import groovy.json.JsonSlurper

import java.text.SimpleDateFormat
import org.hibernate.criterion.CriteriaSpecification
import groovy.sql.Sql
import java.time.LocalDate;
import java.time.ZoneId;


@Transactional
class CertificadoService {
    def servletContext
    def efirmaService
    def alumnoService
    def documentoService
    def xmlService
    def zxingService
    def numeroLetrasService
    def evaluacionService
    def bitacoraService
    def messageSource
    def usuarioService
    def springSecurityService
    def certificadoPublicoService
    def errorService
    def estatusRegistroService
    def dataSource

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


    /**
     * Obtiene los certificados con parametros de paginación y filtrado
     *
     * @param instituciones Filtrar por instituciones a las que pertenece un usuario
     * @param estatusCertificados Filtrar por estatus del certificado
     * @param institucionId Filtrar por una institución específica
     * @param estatusCertificadoId Filtrar por une status específico
     * @param tieneTramite Filtrar los que tienen o no tramites
     * @param esPublica Filtrar los que son o no de instituciones públicas
     * @param alumnoId Filtrar por alumno
     *
     * @return resultado con el estatus, mensaje y certificados
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
        def estatusCertificados = params.estatusCertificados
        def tieneTramite = params.tieneTramite
        def esPublica = params.esPublica

        // Parametros de filtrado opcionales
        def institucionId = params.institucionId ? params.institucionId.toInteger() : null
        def carreraId = params.carreraId ? params.carreraId.toInteger() : null
        def planEstudiosId = params.planEstudiosId ? params.planEstudiosId.toInteger() : null
        def estatusCertificadoId = params.estatusCertificadoId ? params.estatusCertificadoId.toInteger() : null
        def alumnoId = params.alumnoId ? params.alumnoId.toInteger() : null
        def search = params.search ? params.search : null

        def criteria = {
            createAlias("estatusCertificado", "e", CriteriaSpecification.LEFT_JOIN)
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
                    estatusCertificados.each{ registro ->
                        eq("e.id", registro)
                    }
                }

                if(institucionId){
 				    eq("inst.id", institucionId)
 				}
                if(carreraId){
 				    eq("c.id", carreraId)
 				}
                if(planEstudiosId){
 				    eq("plan.id", planEstudiosId)
 				}

                if(estatusCertificadoId != null){
                    eq("e.id", estatusCertificadoId)
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

        def certificados = Certificado.createCriteria().list(params, criteria)

        resultado.estatus = true
        resultado.mensaje = "Certificados consultados exitosamente"
        resultado.datos = certificados

        return resultado
    }

    /**
     * Obtiene todos los certificados
     *
     * @param params con los parametros de pagínacion (opcional)
     *
     * @return resultado con el estatus, mensaje y certificados
     */
    def listarCertificados(params){
        def usuario = usuarioService.obtenerUsuarioLogueado()
        def roles = usuarioService.obtenerRoles()

        params.instituciones = null
        params.estatusCertificados = null
        params.tieneTramite = null
        params.esPublica = null

        if(roles.contains('ROLE_GESTOR_ESCUELA') || roles.contains('ROLE_DIRECTOR_ESCUELA')){
            params.instituciones = usuario.instituciones
        }
        if(roles.contains('ROLE_AUTENTICADOR_DGEMSS')){
            params.esPublica = params.isPublic ? (params.isPublic.equals("true") ? true : false) : false
            params.instituciones = []
        }
        if(roles.contains('ROLE_REVISOR') || roles.contains('ROLE_RECEPTOR')){
            params.esPublica = false
            params.instituciones = []
        }
        if(roles.contains('ROLE_REVISOR_PUBLICA')){
            params.esPublica = true
            params.instituciones = []
        }

        def certificados = listar(params)

        return certificados
    }

    /**
     * Obtiene los certificados a revisar
     *
     * @param params con los parametros de pagínacion (opcional)
     *
     * @return resultado con el estatus, mensaje y certificados
     */
    def listarCertificadosRevisar(params){
        def usuario = usuarioService.obtenerUsuarioLogueado()
        def roles = usuarioService.obtenerRoles()

        params.instituciones = null
        params.estatusCertificados = [EN_REVISION, RECHAZADO_AUTENTICADOR]
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

        def certificados = listar(params)

        return certificados
    }

    /**
     * Obtiene los certificados a firmar
     *
     * @param params con los parametros de pagínacion (opcional)
     *
     * @return resultado con el estatus, mensaje y certificados
     */
    def listarCertificadosFirmar(params){
        def usuario = usuarioService.obtenerUsuarioLogueado()
		def roles = usuarioService.obtenerRoles()

        params.instituciones = null
        params.estatusCertificados = null
        params.tieneTramite = null
        params.esPublica = null

        if(roles.contains('ROLE_DIRECTOR_ESCUELA')){
            params.instituciones = usuario.instituciones
            params.estatusCertificados = [FIRMANDO_ESCUELA]
        }
        if(roles.contains('ROLE_AUTENTICADOR_DGEMSS')){
            params.instituciones = []
            params.estatusCertificados = [FIRMANDO_DGEMSS]
        }

        def certificados = listar(params)

        return certificados
    }

    /**
     * Obtiene los certificados finalizados por alumno
     *
     * @param params con los parametros de pagínacion (opcional)
     *
     * @return resultado con el estatus, mensaje y certificados
     */
    def listarCertificadosFinalizadosPorAlumno(params){
        def usuario = usuarioService.obtenerUsuarioLogueado()
		def roles = usuarioService.obtenerRoles()

        params.instituciones = null
        params.estatusCertificados = [FINALIZADO]
        params.tieneTramite = null
        params.esPublica = null

        if(roles.contains('ROLE_GESTOR_ESCUELA') || roles.contains('ROLE_DIRECTOR_ESCUELA')){
            params.instituciones = usuario.instituciones
        }
        if(roles.contains('ROLE_AUTENTICADOR_DGEMSS')){
            params.instituciones = []
        }

        def certificados = listar(params)

        return certificados
    }

    /**
     * Obtiene los certificados sin tramite
     *
     * @param params con los parametros de pagínacion (opcional)
     *
     * @return resultado con el estatus, mensaje y certificados
     */
    def listarCertificadosSinTramite(params){
		def usuario = usuarioService.obtenerUsuarioLogueado()
		def roles = usuarioService.obtenerRoles()

        params.estatusCertificados = [EN_ESPERA]
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
            createAlias("estatusCertificado", "e", CriteriaSpecification.LEFT_JOIN)
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

        def certificados = Certificado.createCriteria().list(params, criteria)

        resultado.estatus = true
        resultado.mensaje = "Certificados consultados exitosamente"
        resultado.datos = certificados

        return resultado
    }

    /**
     * Permite realizar el registro de certificados
     * @param foto
     * @return resultado
     * resultado con el estatus, mensaje y datos del certificado
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
            clase: "CertificadoService",
            metodo: "registrar",
            nombre: "Registro de certificado",
            descripcion: "Se registró un certificado",
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

        // Se valida que el alumno tenga evaluaciones registradas
        def evaluaciones = Evaluacion.createCriteria().list {
            createAlias("alumno", "a", CriteriaSpecification.LEFT_JOIN)
            createAlias("alumno.persona", "p", CriteriaSpecification.LEFT_JOIN)
            and{
                eq("activo", true)
                eq("p.id", alumno.persona.id)
            }
        }

        if(evaluaciones.size == 0){
            resultado.mensaje = 'El alumno aun no concluye ninguna materia'
            return resultado
        }

        if(!params.foto){
            resultado.mensaje = 'La foto es un dato requerido'
            return resultado
        }

        def certificado = new Certificado()
        certificado.uuid = efirmaService.generarUuid()
        certificado.folioControl = generaIdentificadorEstructurado(alumno.planEstudios.carrera.institucion)
        certificado.foto = efirmaService.base64toBytes(params.foto)
        certificado.municipio = "CUERNAVACA"
        certificado.alumno = alumno
        certificado.duplicado = params.duplicado ? true : false
        certificado.estatusCertificado = EstatusCertificado.get(GENERADO)

        if(!certificado.save(flush:true)){
			certificado.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar(datosBitacora)
            return resultado
		}

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = 'Certificado creado exitosamente'
		resultado.datos = certificado

        return resultado
    }

    /**
     * Permite realizar la consulta del registro seleccionado con su información correspondiente.
     * @param id
     * id del certificado
     * @return resultado
     * resultado con el estatus, mensaje y datos del certificado
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

        def certificado = Certificado.findByUuid(params.uuid)

        if(!certificado){
            resultado.mensaje = 'Certificado no encontrado'
            return resultado
        }

        if(!certificado.activo){
            resultado.mensaje = 'Certificado inactivo'
            return resultado
        }


		def roles = usuarioService.obtenerRoles()
		if(roles.contains('ROLE_GESTOR_ESCUELA') || roles.contains('ROLE_DIRECTOR_ESCUELA')){
            // Se valida que el certificado pertenesca a la misma institución que el usuario
            def institucionId = certificado.alumno.planEstudios.carrera.institucion.id
            if(!usuarioService.perteneceAInstitucion(institucionId)){
                resultado.mensaje = 'No esta autorizado para realizar esta acción'
                return resultado
            }
		}

        resultado.estatus = true
        resultado.mensaje = 'Certificado encontrado exitosamente'
        resultado.datos = certificado

        return resultado
    }

    def consultarCertificado(params) {
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }

        def certificado = Certificado.findByUuid(params.uuid)

        if(!certificado || !certificado.activo || certificado.estatusCertificado.id != 9){
            resultado.mensaje = 'Certificado no encontrado'
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'Certificado encontrado exitosamente'
        resultado.datos = certificado

        return resultado
    }

    /**
     * Permite realizar la modificación de un certificado
     * @param params
     * parametros del ciclo escolar
     * @param carreraId
     * id de la carrera
     * @return resultado
     * resultado con el estatus, mensaje y datos del ciclo escolar
     */
	def modificar(params) {
		def resultado = [
			estatus: false,
			mensaje: '',
            pag: null,
			datos: null
		]

        def datosBitacora = [
            clase: "CertificadoService",
            metodo: "modificar",
            nombre: "Modificación de certificado",
            descripcion: "Se modificó un certificado",
            estatus: "ERROR"
        ]

		if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }

        def certificado = Certificado.findByUuid(params.uuid)
        if(!certificado){
            resultado.mensaje = 'Certificado no encontrado'
            return resultado
        }
        if(!certificado.activo){
            resultado.mensaje = 'Certificado inactivo'
            return resultado
        }

        // Se valida que el certificado se encuentre en estatus  de generado o rechazado
        if(certificado.estatusCertificado.id != GENERADO && certificado.estatusCertificado.id != RECHAZADO_DGEMSS){
            resultado.mensaje = 'El certificado no puede ser modificado'
            return resultado
        }

        // Se valida que el certificado pertenesca a la misma institución que el usuario
        def institucionId = certificado.persona.alumnos[0].cicloEscolar.planEstudios.carrera.institucion.id
        if(!usuarioService.perteneceAInstitucion(institucionId)){
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
			return resultado
		}

        def foto = params.foto
        params.foto = null

		if(!params.foto){
            resultado.mensaje = 'La foto es un dato requerido'
            return resultado
        }

        certificado.foto = efirmaService.base64toBytes(foto)
        certificado.duplicado = params.duplicado ? true : false

        if(!certificado.save(flush:true)){
			certificado.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar(datosBitacora)
            return resultado
		}

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = 'Certificado modificado exitosamente'
		resultado.datos = certificado

		return resultado
	}

    /**
     * Permite realizar la modificación de un certificado
     * @param params
     * parametros del ciclo escolar
     * @param carreraId
     * id de la carrera
     * @return resultado
     * resultado con el estatus, mensaje y datos del ciclo escolar
     */
	def modificarFotografia(params) {
		def resultado = [
			estatus: false,
			mensaje: '',
            pag: null,
			datos: null
		]

        def datosBitacora = [
            clase: "CertificadoService",
            metodo: "modificarFotografia",
            nombre: "Modificación de fotografia",
            descripcion: "Se modificó un certificado",
            estatus: "ERROR"
        ]

		if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }

        def certificado = Certificado.findByUuid(params.uuid)
        if(!certificado){
            resultado.mensaje = 'Certificado no encontrado'
            return resultado
        }
        if(!certificado.activo){
            resultado.mensaje = 'Certificado inactivo'
            return resultado
        }

        // Se valida que el certificado se encuentre en un estatus válido para la acción
        def certificadoEstatusId = certificado.estatusCertificado.id
        def estatusValidos = [GENERADO, RECHAZADO_DGEMSS, RECHAZADO_DIRECTOR]

        if(!(certificadoEstatusId in estatusValidos)){
            resultado.mensaje = 'No se puede cambiar el estatus'
            return resultado
        }

        if(!params.foto){
            resultado.mensaje = 'La foto es un dato requerido'
            return resultado
        }

        params.foto = efirmaService.base64toBytes(params.foto)

        certificado.foto = params.foto
        certificado.duplicado = params.duplicado ? true : false

        if(!certificado.save(flush:true)){
			certificado.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar(datosBitacora)
            return resultado
		}

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = 'Certificado modificado exitosamente'
		resultado.datos = certificado

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
            clase: "CertificadoService",
            metodo: "eliminar",
            nombre: "Eliminación de certificado",
            descripcion: "Se eliminó un certificado",
            estatus: "ERROR"
        ]

		if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }

        def certificado = Certificado.findByUuid(params.uuid)
        if(!certificado){
            resultado.mensaje = 'Certificado no encontrado'
            return resultado
        }
        if(!certificado.activo){
            resultado.mensaje = 'Certificado inactivo'
            return resultado
        }

        if(certificado.estatusCertificado.id != GENERADO){
            resultado.mensaje = 'El certificado no puede ser eliminado'
            return resultado
        }

        // Se valida que el certificado pertenesca a la misma institución que el usuario
        def institucionId = certificado.alumno.planEstudios.carrera.institucion.id
        if(!usuarioService.perteneceAInstitucion(institucionId)){
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
			return resultado
		}

		certificado.activo = false

		if(!certificado.save(flush:true)){
			certificado.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar(datosBitacora)
			return resultado
		}

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = 'Certificado dado de baja exitosamente'
		resultado.datos = certificado

		return resultado
	}


    // MÉTODOS PARA EL PROCESO DE CERTIFICAIÓN


    /**
     * Permite enviar un certificado para su firma por parte de la escuela
     * @param uuid (requerido) uuid del certificado
     * @return resultado con el estatus, mensaje y datos de la carrera
     */
    def enviarAFirmarEscuela(params){
		def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

        def datosBitacora = [
            clase: "CertificadoService",
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

        // Se valida el certificado
        def certificado = Certificado.findByUuid(params.uuid)
        if(!certificado){
            resultado.mensaje = 'Certificado no encontrado'
            return resultado
        }
        if(!certificado.activo){
            resultado.mensaje = 'Certificado inactivo'
            return resultado
        }

        // Se valida que el certificado se encuentre en un estatus válido para la acción
        def certificadoEstatusId = certificado.estatusCertificado.id
        def estatusValidos = [GENERADO, RECHAZADO_DGEMSS, RECHAZADO_DIRECTOR]

        if(!(certificadoEstatusId in estatusValidos)){
            resultado.mensaje = 'No se puede cambiar el estatus'
            return resultado
        }

        // Se valida que la acción la realice un usuario de tipo Gestor de Escuela
		def roles = usuarioService.obtenerRoles()
		if(!roles.contains('ROLE_GESTOR_ESCUELA')){
            resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
		}

        // Se valida que el certificado pertenesca a la misma institución que el usuario
        def institucionId = certificado.alumno.planEstudios.carrera.institucion.id
        if(!usuarioService.perteneceAInstitucion(institucionId)){
            resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
        }

        // Se asigna el nuevo estatus
		certificado.estatusCertificado = EstatusCertificado.get(FIRMANDO_ESCUELA)
		certificado.comentarios = null

		if(!certificado.save(flush:true)){
            transactionStatus.setRollbackOnly()
            resultado.mensaje = errorService.obtenerErrores(certificado)[0]
			return resultado
		}

        // Se cambie el estatus del alumno, sus evaluaciones y sus ciclos para que no puedan se editados durante el proceso
        if(!cambiarEstatusRegistros(certificado, estatusRegistroService.NO_EDITABLE)){
            transactionStatus.setRollbackOnly()
            resultado.mensaje = "Hubo un error al intentar enviar el certificado a firmar. Por favor inténtelo nuevamente."
			return resultado
        }

        // Se guarda la acción en la bitácora
        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = 'Certificado modificado exitosamente'
		resultado.datos = certificado

		return resultado
	}

    /**
     * Permite firmar un certificado por parte de la escuela
     * @param firmaId (requerido) id del registro firma a acupar para firmar
     * @param clavePrivada (opcional)
     * @param contrasena (requerido)
     * @param uuid (requerido) uuid del certificado
     * @return resultado con el estatus, mensaje y datos de la carrera
     */
    def firmarCertificadoEscuela(params){
		def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

        def datosBitacora = [
            clase: "CertificadoService",
            metodo: "firmarCertificadoEscuela",
            nombre: "Firma de certificado - Escuela",
            descripcion: "Firma de certificado - Escuela",
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

        def certificado = Certificado.findByUuid(params.uuid)
        if(!certificado){
            resultado.mensaje = 'Certificado no encontrado'
            return resultado
        }
        if(!certificado.activo){
            resultado.mensaje = 'Certificado inactivo'
            return resultado
        }

        // Se valida que el certificado se encuentre en un estatus válido para la acción
        def certificadoEstatusId = certificado.estatusCertificado.id
        def estatusValidos = [FIRMANDO_ESCUELA]

        if(!(certificadoEstatusId in estatusValidos)){
            resultado.mensaje = 'El certificado no puede ser firmado'
            return resultado
        }

        def roles = usuarioService.obtenerRoles()

        def zona = ZoneId.systemDefault()
        def fechaFirma = Date.from(LocalDate.now().atStartOfDay(zona).toInstant())
        def parametros
        def cadenaOriginal

        parametros = obtenerParametrosCertificado(certificado)
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

        certificado.firmaDirectorEscuela = firma
        // Validar estatus de procedencia con el tramite id
        if(certificado.alumno.planEstudios.carrera.institucion.publica){
            certificado.estatusCertificado = EstatusCertificado.get(EN_REVISION)
        }else if(certificado.tramite != null){
            certificado.estatusCertificado = EstatusCertificado.get(EN_REVISION)
        }else{
            certificado.estatusCertificado = EstatusCertificado.get(EN_ESPERA)
        }

        if(!certificado.save(flush:true)){
			certificado.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			return resultado
		}

        // Se guarda la acción en la bitácora
        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

        resultado.estatus = true
        resultado.mensaje = "Certificado firmado exitosamente"
        return resultado
    }

    /**
     * Permite enviar el certificado a revición
     * @param firmaId (requerido) id del registro firma a acupar para firmar
     * @param clavePrivada (opcional)
     * @param contrasena (requerido)
     * @param uuid (requerido) uuid del certificado
     * @return resultado con el estatus, mensaje y datos de la carrera
     */
    def enviarARevision(params){
		def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

        def datosBitacora = [
            clase: "CertificadoService",
            metodo: "enviarARevision",
            nombre: "Envío a revisión",
            descripcion: "Envío a revisión",
            estatus: "ERROR"
        ]

        // Se validan los parametros recibidos
		if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }
        def certificado = Certificado.findByUuid(params.uuid)
        if(!certificado){
            resultado.mensaje = 'Certificado no encontrado'
            return resultado
        }
        if(!certificado.activo){
            resultado.mensaje = 'Certificado inactivo'
            return resultado
        }

        // Se valida que el certificado se encuentre en un estatus válido para la acción
        def certificadoEstatusId = certificado.estatusCertificado.id
        def estatusValidos = [EN_ESPERA]

        if(!(certificadoEstatusId in estatusValidos)){
            resultado.mensaje = 'El certificado no puede ser firmado'
            return resultado
        }

		certificado.estatusCertificado = EstatusCertificado.get(EN_REVISION)
		certificado.comentarios = null

		if(!certificado.save(flush:true)){
			certificado.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			return resultado
		}

         // Se guarda la acción en la bitácora
        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = 'Certificado modificado exitosamente'
		resultado.datos = certificado

		return resultado
	}

    /**
     * Permite realizar el registro de certificados
     * @param libro (requerido)
     * @param foja (requerido)
     * @param numero (opcional)
     * @return resultado
     * resultado con el estatus, mensaje y datos del certificado
     */
    def enviarAFirmarDGEMSS(params) {
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def datosBitacora = [
            clase: "CertificadoService",
            metodo: "enviarAFirmarDGEMSS",
            nombre: "Envío para firma de la DGEMSS",
            descripcion: "Envío para firma de la DGEMSS",
            estatus: "ERROR"
        ]

        // Validación de parámetros recibidos
        if(!params.uuid){
            resultado.mensaje = 'El uuid del certificado es un dato requerido'
            return resultado
        }
        def certificado = Certificado.findByUuid(params.uuid)
        if(!certificado){
            resultado.mensaje = 'Certificado no encontrado'
            return resultado
        }
        if(!certificado.activo){
            resultado.mensaje = 'Certificado inactivo'
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
        if(!params.numero.matches("[0-9]*")){
            resultado.mensaje = 'El número debe de ser un número entero'
            return resultado
        }

        certificado.libro = params.libro.toInteger()
        certificado.foja = params.foja.toInteger()
        certificado.numero = params.numero.toInteger()
        certificado.estatusCertificado = EstatusCertificado.get(FIRMANDO_DGEMSS)

        if(!certificado.save(flush:true)){
			certificado.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar(datosBitacora)
            return resultado
		}

        // Se guarda la acción en la bitácora
        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = 'Certificado validado exitosamente'
		resultado.datos = certificado

        return resultado
    }

    /**
     * Permite firmar un certificado por parte de la DGEMSS
     * @param firmaId (requerido) id del registro firma a acupar para firmar
     * @param clavePrivada (opcional)
     * @param contrasena (requerido)
     * @param uuid (requerido) uuid del certificado
     * @return resultado con el estatus, mensaje y datos de la carrera
     */
    def firmarCertificadoDGEMSS(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

        def datosBitacora = [
            clase: "CertificadoService",
            metodo: "firmarCertificadoDGEMSS",
            nombre: "Firma de certificado - DGEMSS",
            descripcion: "Firma de certificado - DGEMSS",
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
        
        def certificado = Certificado.findByUuid(params.uuid)
        if(!certificado){
            resultado.mensaje = 'Certificado no encontrado'
            return resultado
        }
        if(!certificado.activo){
            resultado.mensaje = 'Certificado inactivo'
            return resultado
        }

        // Se valida que el certificado se encuentre en un estatus válido para la acción
        def certificadoEstatusId = certificado.estatusCertificado.id
        def estatusValidos = [FIRMANDO_DGEMSS]

        if(!(certificadoEstatusId in estatusValidos)){
            resultado.mensaje = 'El certificado no puede ser firmado'
            return resultado
        }

        def roles = usuarioService.obtenerRoles()
        
        def zona = ZoneId.systemDefault()
        def fechaFirma = Date.from(LocalDate.now().atStartOfDay(zona).toInstant())
        def cadenaOriginal

        cadenaOriginal = obtenerCadenaOriginalFirmaDgemss(
            fechaFirma, 
            certificado.firmaDirectorEscuela.selloDigital,
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
        
        certificado.firmaAutenticadorDgemss = firma
        certificado.estatusCertificado = EstatusCertificado.get(FINALIZADO)

        if(!certificado.save(flush:true)){
			certificado.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			return resultado
		}

        // Se cambie el estatus del alumno, sus evaluaciones y sus ciclos para que no puedan se modificados más
        if(!cambiarEstatusRegistros(certificado, estatusRegistroService.BLOQUEADO)){
            transactionStatus.setRollbackOnly()
            resultado.mensaje = "Hubo un error al intentar enviar el certificado a firmar. Por favor inténtelo nuevamente."
			return resultado
        }

        // Se guarda la acción en la bitácora
        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

        resultado.estatus = true
        resultado.mensaje = "Certificado firmado exitosamente"
        return resultado
    }

    def firmarCertificado(params){
        def roles = usuarioService.obtenerRoles()

        if(roles.contains('ROLE_DIRECTOR_ESCUELA')){
            return firmarCertificadoEscuela(params)
        }
        
        if(roles.contains('ROLE_AUTENTICADOR_DGEMSS')){
            return firmarCertificadoDGEMSS(params)
        }
    }

    def cambiarEstatusRechazado(params){
        def roles = usuarioService.obtenerRoles()

		if(roles.contains('ROLE_DIRECTOR_ESCUELA')){
            return rechazarCertificadoEscuela(params)
        }

        if(roles.contains('ROLE_REVISOR') || roles.contains('ROLE_REVISOR_PUBLICA')){
            return rechazarCertificadoRevisor(params)
        }
        
        if(roles.contains('ROLE_AUTENTICADOR_DGEMSS')){
            return rechazarCertificadoDGEMSS(params)
        }
	}

    def rechazarCertificadoEscuela(params){
		def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

        def datosBitacora = [
            clase: "CertificadoService",
            metodo: "rechazarCertificadoEscuela",
            nombre: "Rechazo de certificado - ESCUELA",
            descripcion: "Rechazo de certificado - ESCUELA",
            estatus: "ERROR"
        ]

        // Se validan los parametros recibidos
		if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }
        if(!params.comentarios){
            resultado.mensaje = 'Los comentarios son un dato requerido'
            return resultado
        }

        def certificado = Certificado.findByUuid(params.uuid)
        if(!certificado){
            resultado.mensaje = 'Certificado no encontrado'
            return resultado
        }
        if(!certificado.activo){
            resultado.mensaje = 'Certificado inactivo'
            return resultado
        }

        // Se valida que el certificado se encuentre en un estatus válido para la acción
        def certificadoEstatusId = certificado.estatusCertificado.id
        def estatusValidos = [FIRMANDO_ESCUELA]

        if(!(certificadoEstatusId in estatusValidos)){
            resultado.mensaje = 'No se puede rechazar el certificado'
            return resultado
        }

        
        // Se valida que el usuario sea un director de escuela
		def roles = usuarioService.obtenerRoles()
		if(!roles.contains('ROLE_DIRECTOR_ESCUELA')){
            resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
		}

        // Se valida que el certificado pertenesca a la misma institución que el director
        def institucionId = certificado.alumno.planEstudios.carrera.institucion.id
        if(!usuarioService.perteneceAInstitucion(institucionId)){
            resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
        }

		certificado.estatusCertificado = EstatusCertificado.get(RECHAZADO_DIRECTOR)
		certificado.comentarios = params.comentarios

		if(!certificado.save(flush:true)){
			certificado.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			return resultado
		}

        // Se cambie el estatus del alumno, sus evaluaciones y sus ciclos para que se puedan editar nuevamente
        if(!cambiarEstatusRegistros(certificado, estatusRegistroService.EDITABLE)){
            transactionStatus.setRollbackOnly()
            resultado.mensaje = "Hubo un error al intentar enviar el certificado a firmar. Por favor inténtelo nuevamente."
			return resultado
        }

		resultado.estatus = true
		resultado.mensaje = 'Certificado modificado exitosamente'
		resultado.datos = certificado

		return resultado
	}

    def rechazarCertificadoRevisor(params){
		def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

        def datosBitacora = [
            clase: "CertificadoService",
            metodo: "rechazarCertificadoRevisor",
            nombre: "Rechazo de certificado - REVISOR",
            descripcion: "Rechazo de certificado - REVISOR",
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

        def certificado = Certificado.findByUuid(params.uuid)
        if(!certificado){
            resultado.mensaje = 'Certificado no encontrado'
            return resultado
        }
        if(!certificado.activo){
            resultado.mensaje = 'Certificado inactivo'
            return resultado
        }

        def certificadoEstatusId = certificado.estatusCertificado.id
        def estatusValidos = [EN_REVISION, RECHAZADO_AUTENTICADOR]

        if(!(certificadoEstatusId in estatusValidos)){
            resultado.mensaje = 'No se puede cambiar el estatus'
            return resultado
        }

        // La validación solo se realiza si la acción la hace un usuario REVISOR
		def roles = usuarioService.obtenerRoles()
		if(!(roles.contains('ROLE_REVISOR') || roles.contains('ROLE_REVISOR_PUBLICA'))){
            resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
		}

        def firmaDirectorEscuela = certificado.firmaDirectorEscuela

		certificado.estatusCertificado = EstatusCertificado.get(RECHAZADO_DGEMSS)
		certificado.comentarios = params.comentarios
        certificado.libro = null
        certificado.foja = null
        certificado.numero = null
        certificado.firmaDirectorEscuela = null

		if(!certificado.save(flush:true)){
			certificado.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			return resultado
		}

        // Se cambie el estatus del alumno, sus evaluaciones y sus ciclos para que se puedan editar nuevamente
        if(!cambiarEstatusRegistros(certificado, estatusRegistroService.EDITABLE)){
            transactionStatus.setRollbackOnly()
            resultado.mensaje = "Hubo un error al intentar rechazar el certificado. Por favor inténtelo nuevamente."
			return resultado
        }

        firmaDirectorEscuela.delete()

		resultado.estatus = true
		resultado.mensaje = 'Certificado modificado exitosamente'
		resultado.datos = certificado

		return resultado
	}

    def rechazarCertificadoDGEMSS(params){
		def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

        def datosBitacora = [
            clase: "CertificadoService",
            metodo: "rechazarCertificadoDGEMSS",
            nombre: "Rechazo de certificado - DGEMSS",
            descripcion: "Rechazo de certificado - DGEMSS",
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

        def certificado = Certificado.findByUuid(params.uuid)
        if(!certificado){
            resultado.mensaje = 'Certificado no encontrado'
            return resultado
        }
        if(!certificado.activo){
            resultado.mensaje = 'Certificado inactivo'
            return resultado
        }

        def certificadoEstatusId = certificado.estatusCertificado.id
        def estatusValidos = [FIRMANDO_DGEMSS]

        if(!(certificadoEstatusId in estatusValidos)){
            resultado.mensaje = 'No se puede cambiar el estatus'
            return resultado
        }

        // La validación solo se realiza si la acción la hace un usuario AUTENTICADOR
		def roles = usuarioService.obtenerRoles()
		if(!(roles.contains('ROLE_AUTENTICADOR_DGEMSS'))){
            resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
		}

		certificado.estatusCertificado = EstatusCertificado.get(RECHAZADO_AUTENTICADOR)
		certificado.comentarios = params.comentarios

		if(!certificado.save(flush:true)){
			certificado.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			return resultado
		}

		resultado.estatus = true
		resultado.mensaje = 'Certificado modificado exitosamente'
		resultado.datos = certificado

		return resultado
	}

    def cambiarEstatusRegistros(certificado, estatusRegistroId){
        def nuevoEstatusRegistro = EstatusRegistro.get(estatusRegistroId)

        def alumno = certificado.alumno

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

    def obtenerParametrosCertificado(certificado){
        def alumno = certificado.alumno
        def persona = alumno.persona
        def planEstudios = alumno.planEstudios
        def carrera = alumno.planEstudios.carrera
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

        def asignaturas = Asignatura.createCriteria().list {
            createAlias("planEstudios", "p", CriteriaSpecification.LEFT_JOIN)
            createAlias("formacion", "f", CriteriaSpecification.LEFT_JOIN)
            createAlias("formacion.tipoFormacion", "tf", CriteriaSpecification.LEFT_JOIN)
            and{
                eq("activo", true)
                eq("f.requerida", true)
                eq("p.id", planEstudios.id)
                or{
                    eq("f.general", true)
                    eq("formacion", alumno.formacion)
                }
            }
        }

        def fechaUltimaAcreditacion = evaluacionService.obtenerFechaUltimaAcreditacion(certificado)

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
            //carrera
        parametros.carrera = carrera.nombre.trim().toUpperCase()
        parametros.modalidad = carrera.modalidad ? carrera.modalidad.nombre.trim().toUpperCase() : ""
        parametros.claveSeem = carrera.claveSeem ? carrera.claveSeem.trim().toUpperCase() : ""
        parametros.carreraNivel = carrera.nivel ? carrera.nivel.nombre.trim().toUpperCase() : ""
            //asignaturas
        parametros.numeroAsignaturas = asignaturas.size
        parametros.tipoCertificado = evaluaciones.size >= asignaturas.size ? "TOTAL" : "PARCIAL"
            //evaluaciones
        parametros.promedio = evaluacionService.calcularPromedio(evaluaciones)
        parametros.promedio = parametros.promedio == 10 ? 10 : String.format("%.01f", parametros.promedio)
            //planEstudios
        parametros.rvoe = planEstudios.rvoe ? planEstudios.rvoe.trim().toUpperCase() : ""
        parametros.fechaRvoe = planEstudios.fechaRvoe ? formatoFechaRvoe.format(planEstudios.fechaRvoe).trim().toUpperCase() : ""
        parametros.fechaRvoeCadena = planEstudios.fechaRvoe ? formatoFechaXml.format(planEstudios.fechaRvoe).trim().toUpperCase() : ""
        parametros.calificacionMinima = planEstudios.calificacionMinima ? planEstudios.calificacionMinima : 5.0
        parametros.calificacionMaxima = planEstudios.calificacionMaxima ? (planEstudios.calificacionMaxima == 10 ? 10 : planEstudios.calificacionMaxima) : 10
        parametros.calificacionMinimaAprobatoria = planEstudios.calificacionMinimaAprobatoria ?
                                                   planEstudios.calificacionMinimaAprobatoria :
                                                   6.0
            //certificado
        parametros.foto = new ByteArrayInputStream(certificado.foto)
        parametros.uuid = certificado.uuid
        parametros.folioControl = certificado.folioControl
        parametros.libro = certificado.libro ? certificado.libro : ""
        parametros.foja = certificado.foja ? certificado.foja : ""
        parametros.numero = certificado.numero ? certificado.numero : ""
        parametros.municipio = certificado.municipio.trim().toUpperCase()
        parametros.fechaUltimaAcreditacion = formatoFechaPdf.format(fechaUltimaAcreditacion)
        parametros.fechaUltimaAcreditacionCadena = formatoFechaXml.format(fechaUltimaAcreditacion)
        parametros.fechaUltimaAcreditacionCadena = parametros.fechaUltimaAcreditacionCadena.trim().toUpperCase()
        parametros.duplicado = certificado.duplicado

        parametros.numPaginas = 2
        parametros.numEvaluaciones = evaluaciones.size()
        parametros.numeroAsignaturasAcreditadas = 0
        parametros.materias = []
        parametros.materias1P = []
        parametros.materias2P = []
        evaluaciones.eachWithIndex{ evaluacion, index ->
            // Se define el nombre de la asignatura
            def nombre = evaluacion.asignatura.nombre.trim().toUpperCase()

            // Se define la calificación
            def calificacion
            def calificacionLetra
            if(evaluacion.asignatura.formacion.requerida){
                calificacion = evaluacion.calificacion
                if(evaluacion.calificacion == 10) calificacion = 10
                calificacionLetra = numeroLetrasService.convertir(String.valueOf(evaluacion.calificacion), true)
                if(calificacion >= parametros.calificacionMinimaAprobatoria){
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
                calificacionLetra: calificacionLetra.trim().toUpperCase(),
                observaciones: evaluacion.tipoEvaluacion.id == 1 ?  "" : evaluacion.tipoEvaluacion.abreviatura.trim().toUpperCase()
            ]
            if(index > 72){
                parametros.materias2P.add(evaluacionAux)
            }else{
                parametros.materias1P.add(evaluacionAux)
            }
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
            parametros.firmanteEscuelaCargo = datosCerDirector.persona.usuario.cargo.trim().toUpperCase()

            //datos firmante dgmess
            def datosCerAutenticador = certificado.firmaAutenticadorDgemss.firmaElectronica

            parametros.firmanteDgemssSello = certificado.firmaAutenticadorDgemss.selloDigital
            parametros.firmanteDgemssNoSerie = datosCerAutenticador.numeroSerieCer.trim().toUpperCase()
            parametros.firmanteDgemssCurp = datosCerAutenticador.curpCer.trim().toUpperCase()
            parametros.firmanteDgemssNombreCompleto = datosCerAutenticador.nombreCer.trim().toUpperCase()
            parametros.firmanteDgemssTitulo = datosCerAutenticador.persona.titulo.trim().toUpperCase()
            parametros.firmanteDgemssFecha = formatoFechaPdf.format(certificado.firmaAutenticadorDgemss.fechaFirma)
            parametros.firmanteDgemssFechaCadena = formatoFechaXml.format(certificado.firmaAutenticadorDgemss.fechaFirma)
            parametros.firmanteDgemssCargo = datosCerAutenticador.persona.usuario.cargo.trim().toUpperCase()

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
        cadenaOriginal += "|${parametros.carreraNivel}"
        cadenaOriginal += "|${parametros.modalidad}"
        cadenaOriginal += "|${parametros.calificacionMinima}"
        cadenaOriginal += "|${parametros.calificacionMaxima}"
        cadenaOriginal += "|${parametros.calificacionMinimaAprobatoria}"
        cadenaOriginal += "|${parametros.matricula}"
        cadenaOriginal += "|${parametros.curp}"
        cadenaOriginal += "|${parametros.nombre}"
        cadenaOriginal += "|${parametros.primerApellido}"
        cadenaOriginal += "|${parametros.segundoApellido}"
        cadenaOriginal += "|${parametros.fechaNacimiento}"
        cadenaOriginal += "|${parametros.tipoCertificado}"
        cadenaOriginal += "|${parametros.firmanteEscuelaFecha}"
        cadenaOriginal += "|${parametros.municipio}"
        cadenaOriginal += "|${parametros.numeroAsignaturas}"
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

        println(cadenaOriginal)

        return cadenaOriginal
    }

    def obtenerCadenaOriginalFirmaEscuela(parametros, fechaGeneracion){

        def fechaGeneracionCertificado = new SimpleDateFormat("YYYY-MM-dd'T'hh:mm:ss").format(fechaGeneracion)

        def cadenaOriginal = "||2.0"

        cadenaOriginal += "|5" // tipoCertificado
        cadenaOriginal += "|${parametros.uuid}"
        cadenaOriginal += "|${parametros.folioControl}"
        cadenaOriginal += "|${parametros.institucionId}"
        cadenaOriginal += "|${parametros.institucion}"
        cadenaOriginal += "|${parametros.institucionClaveCt}"
        cadenaOriginal += "|${parametros.institucionMunicipio}"
        cadenaOriginal += "|${parametros.carrera}"
        cadenaOriginal += "|${parametros.carreraNivel}"
        cadenaOriginal += "|${parametros.modalidad}"
        cadenaOriginal += "|${parametros.calificacionMinima}"
        cadenaOriginal += "|${parametros.calificacionMaxima}"
        cadenaOriginal += "|${parametros.calificacionMinimaAprobatoria}"
        cadenaOriginal += "|${parametros.matricula}"
        cadenaOriginal += "|${parametros.curp}"
        cadenaOriginal += "|${parametros.nombre}"
        cadenaOriginal += "|${parametros.primerApellido}"
        cadenaOriginal += "|${parametros.segundoApellido}"
        cadenaOriginal += "|${parametros.fechaNacimiento}"
        cadenaOriginal += "|${parametros.tipoCertificado}"
        cadenaOriginal += "|${fechaGeneracionCertificado}"
        cadenaOriginal += "|${parametros.municipio}"
        cadenaOriginal += "|${parametros.numeroAsignaturas}"
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

        println(cadenaOriginal)

        return cadenaOriginal
    }


    def obtenerCadenaOriginalFirmaDgemss(fechaExpedicion, selloDirectorEscuela, firmaElectronica){

        def fechaExpedicionCertificado = new SimpleDateFormat("YYYY-MM-dd'T'hh:mm:ss").format(fechaExpedicion)

        def cadenaOriginal = "||"

        cadenaOriginal += "${fechaExpedicionCertificado}"
        cadenaOriginal += "|${selloDirectorEscuela}"
        cadenaOriginal += "|${firmaElectronica.numeroSerieCer.trim().toUpperCase()}"
        cadenaOriginal += "|${firmaElectronica.curpCer.trim().toUpperCase()}"
        cadenaOriginal += "|${firmaElectronica.persona.usuario.cargo.trim().toUpperCase()}"

        cadenaOriginal += "||"

        return cadenaOriginal
    }

    def generarDocumentosCertificado(certificadoUuid){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

        def certificado = Certificado.findByUuid(certificadoUuid)
        def parametrosCertificado = []
        def cadenaOriginal

        parametrosCertificado = obtenerParametrosCertificado(certificado)
        cadenaOriginal = obtenerCadenaOriginalPdf(parametrosCertificado)

        parametrosCertificado.cadenaOriginal = cadenaOriginal
        // Se genera el archivo pdf
        def nombrePlantilla
        if(certificado.alumno.planEstudios.carrera.institucion.publica){
            if(parametrosCertificado.numEvaluaciones <= 45){
                nombrePlantilla = "certificadoTbc"
            }else{
                nombrePlantilla = "certificadoTbc2P"
            }
        }else{
            if(parametrosCertificado.numEvaluaciones <= 62){
                nombrePlantilla = "certificado"
            }else if(parametrosCertificado.numEvaluaciones <= 73){
                nombrePlantilla = "certificado2P"
            }else{
                nombrePlantilla = "certificado3P"
            }
        }

        def resultadoExpedicionPdf = documentoService.generar(nombrePlantilla, parametrosCertificado)
        if (!resultadoExpedicionPdf.estatus) {
            resultado.mensaje = resultadoExpedicionPdf.mensaje
            return resultado
        }

        // Se genera el archivo xml
        def resultadoExpedicionXml = xmlService.generar(parametrosCertificado)
        if (!resultadoExpedicionXml.estatus) {
            resultado.mensaje = resultadoExpedicionXml.mensaje
            return resultado
        }

        certificado.pdf = resultadoExpedicionPdf.documento
        certificado.xml = resultadoExpedicionXml.documento

		if(!certificado.save(flush:true)){
			certificado.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			return resultado
		}

		resultado.estatus = true
		resultado.mensaje = 'Documentos generados exitosamente'
		resultado.datos = certificado

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

        identificador = "${(Certificado.count()?:0) + 1}"

        return identificador
    }

    def actualizarEvaluaciones(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
		]

		if(!params.uuid){
            resultado.mensaje = 'El uuid es un dato requerido'
            return resultado
        }

        def certificado = Certificado.findByUuid(params.uuid)
        if(!certificado){
            resultado.mensaje = 'Certificado no encontrado'
            return resultado
        }
        if(!certificado.activo){
            resultado.mensaje = 'Certificado inactivo'
            return resultado
        }

        def estatusValidos = [GENERADO, RECHAZADO_DIRECTOR, RECHAZADO_DGEMSS]

        if(!(certificado.estatusCertificado.id in estatusValidos)){
            resultado.mensaje = 'El certificado no puede ser actualizado'
            return resultado
        }

        // Se valida que el certificado pertenesca a la misma institución que el usuario
        def institucionId = certificado.alumno.planEstudios.carrera.institucion.id

        if(!usuarioService.perteneceAInstitucion(institucionId)){
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
			return resultado
		}

		certificado.fechaRegistro = new Date()

		if(!certificado.save(flush:true)){
			certificado.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			return resultado
		}

		resultado.estatus = true
		resultado.mensaje = 'Certificado modificado exitosamente'
		resultado.datos = certificado

		return resultado
    }

    def listarCertificadosAFirmarPorInstitucion(params){

        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def criteria = {
            createAlias("estatusCertificado", "e", CriteriaSpecification.LEFT_JOIN)
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

        def certificados = Certificado.createCriteria().list(criteria)

        resultado.estatus = true
        resultado.mensaje = "Certificados consultados exitosamente"
        resultado.datos = certificados

        return resultado
    }

    def firmarCertificados(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def datosBitacora = [
            clase: "CertificadoService",
            metodo: "firmarCertificados",
            nombre: "Firma de certificado - DGEMSS",
            descripcion: "Firma de certificado - DGEMSS",
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

        def estatusValidos = [FIRMANDO_DGEMSS]
        def listaCertificados = []

        def institucion = Institucion.get(params.id)

        listaCertificados = []

        try{

            def jsonSlurper = new JsonSlurper()
            def listaCertificadosJson = jsonSlurper.parseText(params.certificados)

            for(certificadoJson in listaCertificadosJson){
                def certificado = Certificado.get(certificadoJson)
                if(!certificado){
                    resultado.mensaje = "No se encontró el certificado"
                    return resultado
                }

                // Se valida que el certificado se encuentre en un estatus válido para la acción
                def certificadoEstatusId = certificado.estatusCertificado.id
                if(!(certificadoEstatusId in estatusValidos)){
                    resultado.mensaje = 'El certificado no puede ser firmado'
                    return resultado
                }

                // Se agrega al arreglo
                listaCertificados << certificado

            }
        }catch(Exception ex){
            resultado.mensaje = 'Error en formato de instituciones'
            return resultado
        }

        for(certificado in listaCertificados){

            def zona = ZoneId.systemDefault()
            def fechaFirma = Date.from(LocalDate.now().atStartOfDay(zona).toInstant())
            def cadenaOriginal

            cadenaOriginal = obtenerCadenaOriginalFirmaDgemss(
                fechaFirma,
                certificado.firmaDirectorEscuela.selloDigital,
                firmaElectronica
            )

            def selloDigital = efirmaService.firmar(bytesClavePrivada, contrasena, cadenaOriginal)

            def firma = new Firma()
            firma.selloDigital = selloDigital
            firma.firmaElectronica = firmaElectronica
            firma.fechaFirma = fechaFirma

            if(!firma.save(flush:true)){
                transactionStatus.setRollbackOnly()
                firma.errors.allErrors.each {
                    resultado.mensaje = messageSource.getMessage(it, null)
                }
                return resultado
            }

            certificado.firmaAutenticadorDgemss = firma
            certificado.estatusCertificado = EstatusCertificado.get(FINALIZADO)

            if(!certificado.save(flush:true)){
                transactionStatus.setRollbackOnly()
                certificado.errors.allErrors.each {
                    resultado.mensaje = messageSource.getMessage(it, null)
                }
                return resultado
            }

            // Se cambie el estatus del alumno, sus evaluaciones y sus ciclos para que no puedan se modificados más
            if(!cambiarEstatusRegistros(certificado, estatusRegistroService.BLOQUEADO)){
                transactionStatus.setRollbackOnly()
                resultado.mensaje = "Hubo un error al intentar enviar el certificado a firmar. Por favor inténtelo nuevamente."
                return resultado
            }
        }

        // Se guarda la acción en la bitácora
        datosBitacora.estatus = "EXITOSO"
        bitacoraService.registrar(datosBitacora)


        resultado.estatus = true
        resultado.mensaje = "Certificados firmados exitosamente"
        return resultado
    }
}
