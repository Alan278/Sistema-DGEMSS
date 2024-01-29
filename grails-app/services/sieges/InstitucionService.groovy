package sieges

import grails.gorm.transactions.Transactional
import org.hibernate.criterion.CriteriaSpecification
import groovy.sql.Sql

import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.DataFormatter;
import sieges.Institucion

/**
 * Servicio que permite la administración de instituciones
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */
@Transactional
class InstitucionService {
	/**
      * Inyección de messageSource que contiene mensajes de validaciones para los atributos de cada dominio
      * Los mensajes de validación se encuentran en: grails-app/i18n/messages_es.properties
      */
	def messageSource
	/**
	 * Inyección de CarreraService que contiene la lógica de administración de carreras
	 */
	def carreraService
	/**
	 * Inyección de BitacoraService que contiene la lógica de administración de la bitacora
	 */
	def bitacoraService
	def efirmaService
	def usuarioService
	def dataSource
	def formatoService

	/**
	 * Registra una nueva institución
	 * @param nombre (Requerido)
	 * @param nombreComercial (Requerido)
	 * @param razonSocial (Requerido)
	 * @param claveCt (Opcional)
	 * @param claveDgp (Opcional)
	 * @param telefono (Requerido)
	 * @param correoElectronico (Requerido)
	 * @param estado (Requerido)
	 * @param referencias (Opcional)
	 * @param numeroExterior (Requerido)
	 * @param numeroInterior (Opcional)
	 * @param asentamiento (Requerido)
	 * @param localidad (Requerido)
	 * @param calle (Requerido)
	 * @param codigoPostal (Requerido)
	 * @param latitud (Opcional)
	 * @param longitud (Opcional)
	 * @param municipio (Requerido)
	 */
	def registrar(params) {
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		def datosBitacora = [
            clase: "InstitucionService",
            metodo: "registrar",
            nombre: "Registro de institución",
            descripcion: "Se registra una nueva institución",
            estatus: "ERROR"
        ]

		// Se valida y da formato al logo
		if(!params.logo){
			resultado.mensaje = "El logo es un dato requerido"
			return resultado
		}
		params.logo = efirmaService.base64toBytes(params.logo)

		// Se validan y formatean los datos recibidos
		def resultadoValidacion = validarDatosRegistro(params)
		if (!resultadoValidacion.estatus) {
			resultado.mensaje = resultadoValidacion.mensaje
            return resultado
        }

		// Se asignan los datos con formato
		params = resultadoValidacion.datos

		// Se define el tipo de institución con base en el usuario que la crea
		def roles = usuarioService.obtenerRoles()
		params.publica = usuarioService.esSupervisorPublico() ? true : false

		def domicilio = new Domicilio()
		domicilio.calle = params.calle
		domicilio.numeroInterior = params.numeroInterior
		domicilio.numeroExterior = params.numeroExterior
		domicilio.asentamiento = params.asentamiento
		domicilio.localidad = params.localidad
		domicilio.municipio = params.municipio
		domicilio.estado = params.estado
		domicilio.codigoPostal = params.codigoPostal
		domicilio.referencias = params.referencias
		domicilio.latitud = params.latitud
		domicilio.longitud = params.longitud

		if(!domicilio.save(flush: true)){
			transactionStatus.setRollbackOnly()
			bitacoraService.registrar(datosBitacora)
			resultado.mensaje = "Error al guardar la institución"
			return resultado
		}

		def institucion = new Institucion()
		institucion.nombre = params.nombre
		institucion.nombreComercial = params.nombreComercial
		institucion.razonSocial = params.razonSocial
		institucion.claveDgp = params.claveDgp
		institucion.claveCt = params.claveCt
		institucion.correoElectronico = params.correoElectronico
		institucion.telefono = params.telefono
		institucion.logo = params.logo
		institucion.publica = params.publica
		institucion.domicilio = domicilio

		if(!institucion.save(flush: true)){
			transactionStatus.setRollbackOnly()
			bitacoraService.registrar(datosBitacora)
			resultado.mensaje = "Error al guardar la institución"
			return resultado
		}

		datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = "Institución creada exitosamente"
		resultado.datos = institucion

		return resultado
	}

	def validarDatosRegistro(params){
		def resultado = [
			estatus: false,
			mensaje: "",
			datos: null
		]

		def resultadoValidacion

		// Se validan los datos requeridos
		resultadoValidacion = validarDatosRequeridosRegistro(params)
		if(!resultadoValidacion.estatus){
			resultado.mensaje = resultadoValidacion.mensaje
			return resultado
		}

		// Se valida el formato de los datos
		resultadoValidacion = validarFormatoDatosRegistro(params)
		if(!resultadoValidacion.estatus){
			resultado.mensaje = resultadoValidacion.mensaje
			return resultado
		}

		// Se le da el formato requerido a los datos
		params = formatearDatosRegistro(params)

		// Se validan reglas de negocio
		resultadoValidacion = validarReglasNegocioRegistro(params)
		if(!resultadoValidacion.estatus){
			resultado.mensaje = resultadoValidacion.mensaje
			return resultado
		}

		resultado.datos = params
		resultado.estatus = true
		return resultado
	}

	def validarDatosRequeridosRegistro(params){
		def resultado = [
			estatus: false,
			mensaje: ""
		]

		if(!params.nombre){
            resultado.mensaje = "El nombre es un dato requerido"
            return resultado
        }

		if(!params.nombreComercial){
            resultado.mensaje = "El nombre comercial es un dato requerido"
            return resultado
        }

		if(!params.razonSocial){
            resultado.mensaje = "La razón social es un dato requerido"
            return resultado
        }

		if(!params.correoElectronico){
            resultado.mensaje = "El correo es un dato requerido"
            return resultado
        }

		if(!params.telefono){
            resultado.mensaje = "El teléfono es un dato requerido"
            return resultado
        }

		if(!params.calle){
            resultado.mensaje = "La calle es un dato requerido"
            return resultado
        }

		if(!params.asentamiento){
            resultado.mensaje = "La colonia/asentamiento es un dato requerido"
            return resultado
        }

		if(!params.localidad){
            resultado.mensaje = "La localidad es un dato requerido"
            return resultado
        }

		if(!params.municipio){
            resultado.mensaje = "El municipio es un dato requerido"
            return resultado
        }

		if(!params.estado){
            resultado.mensaje = "El estado es un dato requerido"
            return resultado
        }

		if(!params.codigoPostal){
            resultado.mensaje = "El código postal es un dato requerido"
            return resultado
        }

		resultado.estatus = true
		return resultado
	}

	def validarFormatoDatosRegistro(params){
		def resultado = [
			estatus: false,
			mensaje: ""
		]

		if(!formatoService.isValidEmail(params.correoElectronico)){
			resultado.mensaje = "El correo electrónico no cuenta con un formato válido"
			return resultado
        }

		if(!formatoService.isValidPhoneNumber(params.telefono)){
			resultado.mensaje = "El teléfono no cuenta con un formato válido"
			return resultado
        }

		if(params.numeroInterior && !formatoService.isPositiveInteger(params.numeroInterior)){
			resultado.mensaje = "El número interior debe de ser un número entero positivo"
			return resultado
        }

		if(params.numeroExterior && !formatoService.isPositiveInteger(params.numeroExterior)){
			resultado.mensaje = "El número exterior debe de ser un número entero positivo"
			return resultado
        }

		if(!formatoService.isValidZipCode(params.codigoPostal)){
			resultado.mensaje = "El código postal no cuenta con un formato válido"
			return resultado
        }

		resultado.estatus = true
		return resultado
	}

	def validarReglasNegocioRegistro(params){
        def resultado = [
			estatus: false,
			mensaje: "",
			datos: null
		]

		def institucion = Institucion.findByClaveCtAndActivo(params.claveCt, true)
		if(institucion){
			resultado.mensaje = "Ya existe una institución con la clave CCT ingresada"
			return resultado
		}

		resultado.estatus = true
		return resultado
    }

	def formatearDatosRegistro(params){
		params.nombre = formatoService.toFlatString(params.nombre)
		params.nombreComercial = formatoService.toFlatString(params.nombreComercial)
		params.razonSocial = formatoService.toFlatString(params.razonSocial)
		params.claveDgp = formatoService.toFlatString(params.claveDgp)
		params.claveCt = formatoService.toFlatString(params.claveCt)
		params.correoElectronico = formatoService.toFlatString(params.correoElectronico)
		params.telefono = formatoService.toFlatString(params.telefono)
		params.calle = formatoService.toFlatString(params.calle)
		params.numeroInterior = formatoService.toFlatString(params.numeroInterior)
		params.numeroExterior = formatoService.toFlatString(params.numeroExterior)
		params.asentamiento = formatoService.toFlatString(params.asentamiento)
		params.localidad = formatoService.toFlatString(params.localidad)
		params.municipio = formatoService.toFlatString(params.municipio)
		params.estado = formatoService.toFlatString(params.estado)
		params.codigoPostal = formatoService.toFlatString(params.codigoPostal)
		params.referencias = formatoService.toFlatString(params.referencias)
		params.latitud = formatoService.toFlatString(params.latitud)
		params.longitud = formatoService.toFlatString(params.longitud)
		return params
	}

	/**
	 * Obtiene las instituciones activas con parametros de paginación y filtrado
	 * @param search (Opcional)
	 * Nombre de la institución
	 */
	def listar(params){
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: [
				instituciones: null,
				institucionesTotalCount: null,
				numeroCarreras: [],
			]
		]

		// Parametros de paginación
		if(!params.max) params.max = '50'
		if(!params.offset) params.offset = '0'
		if(!params.sort) params.sort = 'nombre'
		if(!params.order) params.order = 'asc'

		def roles = usuarioService.obtenerRoles()
		params.niveles = usuarioService.obtenerNivelesPorRol()

		def criteria = {
			createAlias("carreras", "c", CriteriaSpecification.LEFT_JOIN)
			createAlias("carreras.nivel", "n", CriteriaSpecification.LEFT_JOIN)
			and{
 				eq("activo", true)
 				eq("externa", false)
				if(usuarioService.esSupervisorPublico()){
 					eq("publica", true)
				}else{
					eq("publica", false)
				}
				if(params.search){
					or{
						ilike("nombre", "%${params.search}%")
						ilike("claveCt", "%${params.search}%")
					}
				}
				if(params.nivelId){
					eq("n.id", params.nivelId.toInteger())
				}
			}
			order(params.sort, params.order)
		}

		def instituciones = Institucion.createCriteria().listDistinct(criteria)
		resultado.datos.institucionesTotalCount = instituciones.size

		def institucionesAux = []
		def max = Integer.parseInt(params.max)
		def offset = Integer.parseInt(params.offset)
		for(int i = offset; i < max + offset ; i++){
			if(!instituciones[i]) break
			institucionesAux << instituciones[i]
		}
		instituciones = institucionesAux

		if(instituciones.size <= 0){
			resultado.mensaje = 'No se encontraron instituciones'
			resultado.datos.instituciones = instituciones
			return resultado
		}

		resultado.estatus = true
		resultado.mensaje = 'Instituciones consultadas exitosamente'
		resultado.datos.instituciones = instituciones

		return resultado
	}

	def listarPublicas(params){
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		// Parametros de paginación
		if(!params.max) params.max = '50'
		if(!params.offset) params.offset = '0'
		if(!params.sort) params.sort = 'id'
		if(!params.order) params.order = 'asc'


		def criteria = {
			and{
 				eq("activo", true)
 				eq("externa", false)
 				eq("publica", true)
				if(params.search){
					ilike("nombre", "%${params.search}%")
				}
			}
			order(params.sort, params.order)
		}

		def institucionesQuery = Institucion.createCriteria().list(params, criteria)

		def instituciones = [
			totalCount: institucionesQuery.totalCount,
			instituciones: []
		]

		for(institucion in institucionesQuery){
			def sql = new Sql(dataSource)
            def numCertificados = sql.rows('''
								SELECT COUNT(*) as num FROM certificado
								INNER JOIN alumno on alumno.id = certificado.alumno_id
								INNER JOIN plan_estudios on plan_estudios.id = alumno.plan_estudios_id
								INNER JOIN carrera on carrera.id = plan_estudios.carrera_id
								INNER JOIN institucion on carrera.institucion_id = institucion.id
								WHERE estatus_certificado_id = 7 and
								certificado.activo = 1 and
								institucion_id =
							''' + institucion.id)

			if(numCertificados[0].num <= 0) continue

			def institucionAux = [
				id: institucion.id,
				logo: institucion.logo,
				nombre: institucion.nombre,
				numCertificados: numCertificados[0].num,
			]
			instituciones.instituciones << institucionAux
		}

		resultado.estatus = true
		resultado.mensaje = 'Instituciones consultadas exitosamente'
		resultado.datos = instituciones

		return resultado
	}

	def listarPrivadas(params){
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		// Parametros de paginación
		if(!params.max) params.max = '50'
		if(!params.offset) params.offset = '0'
		if(!params.sort) params.sort = 'id'
		if(!params.order) params.order = 'asc'


		def criteria = {
			and{
 				eq("activo", true)
 				eq("externa", false)
 				eq("publica", false)
				if(params.search){
					ilike("nombre", "%${params.search}%")
				}
			}
			order(params.sort, params.order)
		}

		def institucionesQuery = Institucion.createCriteria().list(params, criteria)

		def instituciones = [
			totalCount: institucionesQuery.totalCount,
			instituciones: []
		]

		for(institucion in institucionesQuery){
			def sql = new Sql(dataSource)
            def numCertificados = sql.rows(
								"SELECT COUNT(*) as num FROM certificado " +
								"INNER JOIN alumno on alumno.id = certificado.alumno_id " +
								"INNER JOIN plan_estudios on plan_estudios.id = alumno.plan_estudios_id " +
								"INNER JOIN carrera on plan_estudios.carrera_id = carrera.id " +
								"INNER JOIN institucion on carrera.institucion_id = institucion.id " +
								"WHERE estatus_certificado_id = 7 and " +
								"certificado.activo = 1 and " +
								"institucion_id = ${institucion.id}"
							)

			if(numCertificados[0].num <= 0) continue

			def institucionAux = [
				id: institucion.id,
				logo: Base64.getEncoder().encodeToString(institucion.logo),
				nombre: institucion.nombre,
				numCertificados: numCertificados[0].num,
			]
			instituciones.instituciones << institucionAux
		}

		resultado.estatus = true
		resultado.mensaje = 'Instituciones consultadas exitosamente'
		resultado.datos = instituciones

		return resultado
	}

	/**
	 * Obtiene las instituciones activas
	 */
	def obtenerActivos(params){
		def resultado = [
				estatus: false,
				mensaje: '',
				datos: null
		]

		def usuario = usuarioService.obtenerUsuarioLogueado()
		def roles = usuarioService.obtenerRoles()

		def instituciones = Institucion.createCriteria().list {
			and{
				eq("activo", true)
				eq("externa", false)
				if(
					usuarioService.esSupervisorPublico() ||
					roles.contains('ROLE_REVISOR_PUBLICA')
				){
 					eq("publica", true)
				}else if(
					roles.contains('ROLE_SUPERVISOR_POSGRADO') ||
					roles.contains('ROLE_SUPERVISOR_SUPERIOR') ||
					roles.contains('ROLE_SUPERVISOR_MEDIA') ||
					roles.contains('ROLE_SUPERVISOR_TECNICA') ||
					roles.contains('ROLE_SUPERVISOR_CONTINUA') ||
					roles.contains('ROLE_REVISOR') ||
					roles.contains('ROLE_RECEPTOR')
				){
					eq("publica", false)
				}else if(roles.contains('ROLE_AUTENTICADOR_DGEMSS')){
					eq("publica", params.isPublic ? (params.isPublic.equals("true") ? true : false) : false)
				}

				if(roles.contains('ROLE_GESTOR_ESCUELA')){
					or{
						usuario.instituciones.each{ registro ->
							eq("id", registro.institucion.id)
						}
					}
 				}
				or{
                    usuario.instituciones.each{ registro ->
                        eq("id", registro.institucion.id)
                    }
                }
				order("nombre", "asc")
			}
		}


		resultado.estatus = true
		resultado.mensaje = 'Instituciones consultadas exitosamente'
		resultado.datos = instituciones

		return resultado
	}

	/**
	 * Obtiene una institución específica
	 * @param id (Requerido)
	 * Identificador de la institución
	 */
	def consultar(params) {
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		if(!params.id){
			resultado.mensaje = 'El id es un dato requerido'
			return resultado
		}

		def institucion = Institucion.get(params.id)

		if(!institucion){
			resultado.mensaje = 'Institución no encontrada'
			return resultado
		}

		if(!institucion.activo){
			resultado.mensaje = 'Institución inactiva'
			return resultado
		}

		if(institucion.externa){
			resultado.mensaje = 'Institución externa'
			return resultado
		}

		// def roles = usuarioService.obtenerRoles()
		// if(roles.contains('ROLE_SUPERVISOR_MEDIA_PUBLICA')){
		// 	if(!institucion.publica){
		// 		resultado.mensaje = 'Institución privada'
		// 		return resultado
		// 	}
		// }
		// if(roles.contains('ROLE_SUPERVISOR_MEDIA')){
		// 	if(institucion.publica){
		// 		resultado.mensaje = 'Institución pública'
		// 		return resultado
		// 	}
		// }

		resultado.estatus = true
		resultado.mensaje = 'Institución encontrada exitosamente'
		resultado.datos = institucion

		return resultado
	}

	/**
	 * Modifica los datos de una institución específica
	 * @param id (Requerido)
	 * Identificador de la institución
	 * @param nombre (Requerido)
	 * @param nombreComercial (Opcional)
	 * @param razonSocial (Opcional)
	 * @param claveCt (Opcional)
	 * @param claveDgp (Opcional)
	 * @param telefono (Opcional)
	 * @param correoElectronico (Opcional)
	 * @param externa (Requerido)
	 * @param estado (Requerido)
	 * @param referencias (Opcional)
	 * @param numeroExterior (Requerido)
	 * @param numeroInterior (Opcional)
	 * @param asentamiento (Requerido)
	 * @param localidad (Requerido)
	 * @param calle (Requerido)
	 * @param codigoPostal (Requerido)
	 * @param latitud (Opcional)
	 * @param longitud (Opcional)
	 * @param municipio (Requerido)
	 */
	def modificar(params) {
		def resultado = [
			estatus: false,
			pag: null,
			mensaje: '',
			datos: null
		]

		if(!params.id){
			resultado.mensaje = 'El id es un dato requerido'
			resultado.pag = 0
			return resultado
		}

		def institucion = Institucion.get(params.id)
		if(!institucion){
			resultado.mensaje = 'Institución no encontrada'
			resultado.pag = 0
			return resultado
		}
		if(!institucion.activo){
			resultado.mensaje = 'Institución inactiva'
			resultado.pag = 0
			return resultado
		}
		if(institucion.externa){
			resultado.mensaje = 'Institución externa'
			resultado.pag = 0
			return resultado
		}
		if(institucion.numCertificados != 0){
			resultado.mensaje = 'No se puede modificar la institución ya que cuenta con certificados expedidos'
			resultado.pag = 0
			return resultado
		}
		def roles = usuarioService.obtenerRoles()
		if(usuarioService.esSupervisorPublico()){
			if(!institucion.publica){
				resultado.mensaje = 'Institución privada'
				return resultado
			}
		}else if(institucion.publica){
			resultado.mensaje = 'Institución pública'
			return resultado
		}

		if(usuarioService.esSupervisorPublico()){
			params.publica = true
		}else{
			params.publica = false
		}

		if(!params.logo){
			resultado.pag = 2
			resultado.mensaje = "El logo es un dato requerido"
			return resultado
		}

		params.logo = efirmaService.base64toBytes(params.logo)

		// Se crean copias auxiliares de los objetos antes de asignar
		// los nuevos datos para verificar si se realizó algun cambio
		def institucionAux = new Institucion(institucion.properties)
		def domicilioAux = new Domicilio(institucion.domicilio.properties)

		// Se asignan los nuevos datos
		institucion.properties = params
		institucion.domicilio.properties = params

		// En caso de cambios se modifica el campo de ultimaActualizacion
		if(!institucion.equals(institucionAux))
			institucion.ultimaActualizacion = new Date()
		if(!institucion.domicilio.equals(domicilioAux))
			institucion.domicilio.ultimaActualizacion = new Date()

		// Se inicia una transacción para deshacer los cambios en el domicilio
		// en caso de ocurrir algun problema con el guardado de la institución
       	Domicilio.withTransaction {status ->
			if(institucion.domicilio.save(flush:true)){
				if(institucion.save(flush:true)){
					resultado.estatus = true
					bitacoraService.registrar([clase:"InstitucionService", metodo:"modificar", nombre:"Modificación de la Institución", descripcion:"Se modifica la institución", estatus:"EXITOSO"])
				}else{
					status.setRollbackOnly()
					institucion.errors.allErrors.each {
						resultado.mensaje = messageSource.getMessage(it, null)
					}
					resultado.pag = 1
					bitacoraService.registrar([clase:"InstitucionService", metodo:"modificar", nombre:"Modificación de la Institucion", descripcion:"Se modifica la institución", estatus:"ERROR"])
				}
			}else{
				// Se recorren los errores de validación y se asignan a la propiedad resultado.mensaje
				institucion.domicilio.errors.allErrors.each {
					resultado.mensaje = messageSource.getMessage(it, null)
				}
				resultado.pag = 1
				bitacoraService.registrar([clase:"InstitucionService", metodo:"modificar", nombre:"Modificación del domicilio", descripcion:"Se modifica el domicilio", estatus:"ERROR"])
			}
		}

		if(resultado.estatus){
	       	resultado.mensaje = 'Institución modificada exitosamente'
	       	resultado.datos = institucion
		}

		return resultado
	}

	/**
	 * Realiza una baja lógica de una institución específica
	 * @param id (Requerido)
	 * Identificador de la institución
	 */
	def eliminar(params) {
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		if(!params.id){
			resultado.mensaje = 'El id es un dato requerido'
			return resultado
		}

		def institucion = Institucion.get(params.id)

		if(!institucion){
			resultado.mensaje = 'Institución no encontrada'
			return resultado
		}

		if(institucion.externa){
			resultado.mensaje = 'Institución externa'
			return resultado
		}
		if(institucion.numCertificados != 0){
			resultado.mensaje = 'No se puede eliminar la institución ya que cuenta con certificados expedidos'
			resultado.pag = 0
			return resultado
		}
		def roles = usuarioService.obtenerRoles()
		if(usuarioService.esSupervisorPublico()){
			if(!institucion.publica){
				resultado.mensaje = 'Institución privada'
				return resultado
			}
		}else if(institucion.publica){
			resultado.mensaje = 'Institución pública'
			return resultado
		}

		institucion.activo = false

		if(!institucion.save(flush:true)){
			// Se recorren los errores de validación y se asignan a la propiedad resultado.mensaje
			institucion.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar([clase:"InstitucionService", metodo:"eliminar", nombre:"Eliminación de la Institucion", descripcion:"Se elimina la institución", estatus:"ERROR"])
			return resultado
		}
		bitacoraService.registrar([clase:"InstitucionService", metodo:"eliminar", nombre:"Eliminación de la Institucion", descripcion:"Se elimina la institución", estatus:"EXITOSO"])

		resultado.estatus = true
       	resultado.mensaje = 'Institución dada de baja exitosamente'
       	resultado.datos = institucion

		return resultado
	}

	def cargarPorExcel(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

		def datosBitacora = [
            clase: "InstitucionService",
            metodo: "cargarPorExcel",
            nombre: "Registro de instituciones",
            descripcion: "Se registran varias instituciones",
            estatus: "ERROR"
        ]

		if(!params.excel){
            resultado.mensaje = 'El excel es un dato requerido'
			return resultado
        }

		// Se define el tipo de institución con base en el usuario que la crea
		def roles = usuarioService.obtenerRoles()
		def isPublic = usuarioService.esSupervisorPublico() ? true : false

		// Se obtienen los registros del documento excel
		def resultadoOperacion = obtenerRegistros(params.excel)
		if(!resultadoOperacion.estatus){
			resultado.mensaje = resultadoOperacion.mensaje
			return resultado
		}

		def instituciones = resultadoOperacion.datos

		for(datosInstitucion in instituciones){
			def domicilio = new Domicilio()
			domicilio.calle = datosInstitucion.calle
			domicilio.numeroInterior = datosInstitucion.numeroInterior
			domicilio.numeroExterior = datosInstitucion.numeroExterior
			domicilio.asentamiento = datosInstitucion.asentamiento
			domicilio.localidad = datosInstitucion.localidad
			domicilio.municipio = datosInstitucion.municipio
			domicilio.estado = datosInstitucion.estado
			domicilio.codigoPostal = datosInstitucion.codigoPostal
			domicilio.referencias = datosInstitucion.referencias
			domicilio.latitud = datosInstitucion.latitud
			domicilio.longitud = datosInstitucion.longitud

			if(!domicilio.save(flush: true)){
				transactionStatus.setRollbackOnly()
				bitacoraService.registrar(datosBitacora)
				resultado.mensaje = "Error al guardar la institución. Fila ${datosInstitucion.rowNumber}"
				return resultado
			}

			def institucion = new Institucion()
			institucion.nombre = datosInstitucion.nombre
			institucion.nombreComercial = datosInstitucion.nombreComercial
			institucion.razonSocial = datosInstitucion.razonSocial
			institucion.claveDgp = datosInstitucion.claveDgp
			institucion.claveCt = datosInstitucion.claveCt
			institucion.correoElectronico = datosInstitucion.correoElectronico
			institucion.telefono = datosInstitucion.telefono
			institucion.publica = isPublic
			institucion.domicilio = domicilio

			if(!institucion.save(flush: true)){
				transactionStatus.setRollbackOnly()
				bitacoraService.registrar(datosBitacora)
				resultado.mensaje = "Error al guardar la institución. Fila ${datosInstitucion.rowNumber}"
				return resultado
			}
		}

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = "Instituciones creadas exitosamente"

		return resultado
	}

	def obtenerRegistros(excel){
		def resultado = [
            estatus: false,
            mensaje: '',
            datos: []
        ]

		try {
			def file = new ByteArrayInputStream(excel.getBytes())
			def workbook = WorkbookFactory.create(file)
			def sheet = workbook.getSheetAt(0)
			def rowIterator = sheet.iterator()

			DataFormatter formatter = new DataFormatter();

			// Se hace el salto de 11 lineas de encabezado
			(1..11).each { rowIterator.next() }

			while (rowIterator.hasNext()) {
				def row = rowIterator.next()
				def rowNumber = row.getRowNum() + 1

				// Si la primera columna de la fila esta vacia se toma como el final del documento
                if(!formatter.formatCellValue(row.getCell(0))) break

                def datosInstitucion = obtenerRegistroDeFila(row)

				// Se validan y formatean los datos recibidos
                def resultadoValidacion = validarDatosRegistro(datosInstitucion)
                if(!resultadoValidacion.estatus){
                    transactionStatus.setRollbackOnly()
                    resultado.mensaje = "${resultadoValidacion.mensaje}. Fila ${rowNumber}"
                    return resultado
                }

				// Se asignan los datos con formato
				datosInstitucion = resultadoValidacion.datos
				datosInstitucion.rowNumber = rowNumber

				resultado.datos << datosInstitucion
			}
			file.close()

		} catch (Exception ex) {
			resultado.mensaje = "Ocurrio un error"
			resultado.datos = null
			return resultado
		}

		resultado.estatus = true
		return resultado
	}

	def obtenerRegistroDeFila(row){
        DataFormatter formatter = new DataFormatter();

        def institucion = [
			nombre: formatter.formatCellValue(row.getCell(0)),
			nombreComercial: formatter.formatCellValue(row.getCell(1)),
			razonSocial: formatter.formatCellValue(row.getCell(2)),
			claveDgp: formatter.formatCellValue(row.getCell(3)),
			claveCt: formatter.formatCellValue(row.getCell(4)),
			correoElectronico: formatter.formatCellValue(row.getCell(5)),
			telefono: formatter.formatCellValue(row.getCell(6)),
			calle: formatter.formatCellValue(row.getCell(7)),
			numeroInterior: formatter.formatCellValue(row.getCell(8)),
			numeroExterior: formatter.formatCellValue(row.getCell(9)),
			asentamiento: formatter.formatCellValue(row.getCell(10)),
			localidad: formatter.formatCellValue(row.getCell(11)),
			municipio: formatter.formatCellValue(row.getCell(12)),
			estado: formatter.formatCellValue(row.getCell(13)),
			codigoPostal: formatter.formatCellValue(row.getCell(14)),
			referencias: formatter.formatCellValue(row.getCell(15)),
			latitud: formatter.formatCellValue(row.getCell(16)),
			longitud: formatter.formatCellValue(row.getCell(17)),
        ]

        return institucion
    }

	def obtener(id){
		def institucion = Institucion.get(id)

		if(!institucion) return null
		if(!institucion.activo) return null

		return institucion
	}

	def obtenerPorClaveCt(claveCt){
		def institucion = Institucion.findByClaveCtAndActivo(claveCt, true)

		if(!institucion) return null

		return institucion
	}

}
