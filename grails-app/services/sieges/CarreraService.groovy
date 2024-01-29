package sieges

import grails.gorm.transactions.Transactional
import org.hibernate.criterion.CriteriaSpecification
import java.text.SimpleDateFormat

import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.DataFormatter;

/**
 * CarreraService para la gestión de las carreras
 * @authors Dominguez Luis, Navez Leslie
 * @since 2021
 */

@Transactional
class CarreraService {
	/**
	 * Inyección de messageSource que contiene mensajes de validaciones para los atributos de cada dominio
	 * Los mensajes de validación se encuentran en: grails-app/i18n/messages_es.properties
	 */
	def messageSource
	/**
	 * Inyección del planEstudiosService que contiene métodos para la gestión de los planes de estudio.
	 */
	def planEstudiosService
	/**
	 * Inyección de BitacoraService que contiene la lógica de administración de la bitacora
	 */
	def bitacoraService
	def usuarioService
	def institucionService
	def nivelService
	def modalidadService
	def areaService
	def formatoService
	def springSecurityService

	/**
	 * Permite realizar el registro de las carreras.
	 * @param params (requerido)
	 * parametros de la carrera
	 * @param institucionId (requerido)
	 * Id de la institución
	 * @param nivelId
	 * Id del nivel
	 * @param modalidadId
	 * Id de la modalidad
	 * @param areaId
	 * Id del area
	 * @return resultado
	 * resultado con el estatus, mensaje y datos de la carrera
	 */
	def registrar(params){
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		def datosBitacora = [
            clase: "CarreraService",
            metodo: "registrar",
            nombre: "Registro de carrera",
            descripcion: "Se registra una nueva carrera",
            estatus: "ERROR"
        ]

		// Se obtienen las relaciones
		def institucion = institucionService.obtener(params.institucionId)
		if(!institucion){
			resultado.mensaje = 'Institucion no encontrada'
			return resultado
		}

		def nivel = nivelService.obtener(params.nivelId)
		if(!nivel){
			resultado.mensaje = 'Nivel no encontrado'
			return resultado
		}

		def modalidad = modalidadService.obtener(params.modalidadId)
		if(!modalidad){
			resultado.mensaje = 'Modalidad no encontrada'
			return resultado
		}

		def area = areaService.obtener(params.areaId)
		if(params.areaId && !area){
			resultado.mensaje = 'Area no encontrada'
			return resultado
		}

		def resultadoValidacion
        // Se validan los privilegios del usuario
		resultadoValidacion = validarPrivilegios(institucion, nivel)
		if (!resultadoValidacion) {
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
        }
        // Se validan y formatean los datos recibidos
		def formatoFecha = "yyyy-MM-dd"
		resultadoValidacion = validarDatosRegistro(params, formatoFecha)
		if (!resultadoValidacion.estatus) {
			resultado.mensaje = resultadoValidacion.mensaje
            return resultado
        }

		// Se asignan los datos con formato
		params = resultadoValidacion.datos

		def carrera = new Carrera()
		carrera.nombre = params.nombre
		carrera.claveSeem = params.claveSeem
		carrera.claveDgp = params.claveDgp
		carrera.institucion = institucion
		carrera.nivel = nivel
		carrera.modalidad = modalidad
		carrera.area = area

		if(!carrera.save(flush: true)){
			transactionStatus.setRollbackOnly()
			bitacoraService.registrar(datosBitacora)
			resultado.mensaje = "Error al guardar la carrera"
			return resultado
		}

		datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = "Carrera creada exitosamente"
		resultado.datos = carrera

		return resultado
	}

	def validarPrivilegios(institucion, nivel){

        // Se valida el privilegio del usuario sobre instituciones
		// privadas o públicas segun corresponda su rol
        def isPublic = institucion.publica
		if(!usuarioService.validarPrivilegioSobreTipoInstitucion(isPublic)){
			return false
		}

        // Se valida que el usuario pertenezca al mismo nivel que quiere registrar
        if(!usuarioService.perteneceANivel(nivel.id)){
			return false
		}

        return true
    }

    def validarDatosRegistro(params, formatoFecha){
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
		resultadoValidacion = validarFormatoDatosRegistro(params, formatoFecha)
		if(!resultadoValidacion.estatus){
			resultado.mensaje = resultadoValidacion.mensaje
			return resultado
		}

		// Se le da el formato requerido a los datos
		params = formatearDatosRegistro(params, formatoFecha)

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

		// if(!usuarioService.esSupervisorPublico()){
		// 	if(!params.rvoe){
		// 		resultado.mensaje = "El número de RVOE es un dato requerido"
		// 		return resultado
		// 	}

		// 	if(!params.fechaRvoe){
		// 		resultado.mensaje = "La fecha del RVOE es un dato requerido"
		// 		return resultado
		// 	}
		// }

		resultado.estatus = true
		return resultado
	}

	def validarFormatoDatosRegistro(params, formatoFecha){
		def resultado = [
			estatus: false,
			mensaje: ""
		]

		// if(!usuarioService.esSupervisorPublico()){
		// 	if(!formatoService.isDate(params.fechaRvoe, formatoFecha)){
		// 		resultado.mensaje = "La fecha del RVOE no cuenta con un formato válido"
		// 		return resultado
		// 	}
		// }

		resultado.estatus = true
		return resultado
	}

    def formatearDatosRegistro(params, formatoFecha){
		params.nombre = formatoService.toFlatString(params.nombre)
		params.claveSeem = formatoService.toFlatString(params.claveSeem)
		params.claveDgp = formatoService.toFlatString(params.claveDgp)
		// params.rvoe = params.rvoe ? formatoService.toFlatString(params.rvoe) : null
		// params.fechaRvoe = params.fechaRvoe ? new SimpleDateFormat(formatoFecha).parse(params.fechaRvoe) : null

		return params
	}

	/**
	* Permite generar el listado de las carreras que se encuentran registradas, además de permitir realizar la
	busqueda por nombre y el filtrado a través de la institución
	* @param params (requerido)
	* parametros de la carrera
	* @param institucionId (requerido)
	* id de la institución
	* @return resultado
	* resultado con el estatus, mensaje y datos de la carrera
	*/
	def listar(params){
		def resultado = [
				estatus: false,
				mensaje: '',
				datos: null
		]

		if(!params.max) params.max = 50
		if(!params.offset) params.offset = 0
		if(!params.sort) params.sort = 'nombre'
		if(!params.order) params.order = 'asc'

		def institucionId

		if(params.institucionId){
		   institucionId = Integer.parseInt(params.institucionId)
		}

		def roles = usuarioService.obtenerRoles()
		params.niveles = usuarioService.obtenerNivelesPorRol()

		def criteria = {
			createAlias("institucion", "i", CriteriaSpecification.LEFT_JOIN)
			createAlias("nivel", "n", CriteriaSpecification.LEFT_JOIN)
			and{
				eq("activo", true)
				eq("i.externa", false)
				if(usuarioService.esSupervisorPublico()){
 					eq("i.publica", true)
				}else{
					eq("i.publica", false)
				}
				if(params.search){
					ilike("nombre", "%${params.search}%")
				}
				if(params.institucionId){
					eq("i.id", institucionId)
				}
				or{
					params.niveles.each{ registro ->
						eq("n.id", registro)
					}
				}
			}
			order("i.nombre", "asc")
			order("nombre", "asc")
		}

		def carreras = Carrera.createCriteria().list(params,criteria)

		if(carreras.totalCount <= 0){
			resultado.mensaje = 'No se encontraron carreras'
			resultado.datos = carreras
			return resultado
		}

		resultado.estatus = true
		resultado.mensaje = 'Carreras consultadas exitosamente'
		resultado.datos = carreras

		return resultado
	}

	/**
	 * Permite mostrar la lista de las carreras que se encuentran activas
	 * @param params (requerido)
	 * parametros de la carrera
	 * @return resultado
	 * resultado con el estatus, mensaje y datos de la carrera
	 */
	def obtenerActivos(params){
		def resultado = [
				estatus: false,
				mensaje: '',
				datos: null
		]

		def usuario = Usuario.get(springSecurityService.principal.id)
		def roles = usuarioService.obtenerRoles()

		def institucionId
		if(params.institucionId){
			def institucionIdAux = Integer.parseInt(params.institucionId)
			if(roles.contains('ROLE_GESTOR_ESCUELA') && !usuarioService.perteneceAInstitucion(institucionIdAux)){
				institucionIdAux = null
			}
			institucionId = institucionIdAux
		}

		params.niveles = usuarioService.obtenerNivelesPorRol()

		def carreras = Carrera.createCriteria().list {
			createAlias("institucion", "i", CriteriaSpecification.LEFT_JOIN)
			createAlias("nivel", "n", CriteriaSpecification.LEFT_JOIN)
			and{
				eq("activo", true)
				eq("i.externa", false)
				if(usuarioService.esSupervisorPublico()){
 					eq("i.publica", true)
				}else if(
					roles.contains('ROLE_SUPERVISOR_POSGRADO') ||
					roles.contains('ROLE_SUPERVISOR_SUPERIOR') ||
					roles.contains('ROLE_SUPERVISOR_MEDIA') ||
					roles.contains('ROLE_SUPERVISOR_TECNICA') ||
					roles.contains('ROLE_SUPERVISOR_CONTINUA')
				){
					eq("i.publica", false)
				}
				if(params.institucionId){
					eq("i.id", institucionId)
				}
				or{
					params.niveles.each{ registro ->
						eq("n.id", registro)
					}
				}
			}
		}


		resultado.estatus = true
		resultado.mensaje = 'Carreras consultadas exitosamente'
		resultado.datos = carreras

		return resultado
	}

	/**
	 * Permite realizar la consulta del registro seleccionado con su información correspondiente.
	 * @param params (requerido)
	 * parametros de la carrera
	 * @return resultado
	 * resultado con el estatus, mensaje y datos de la carrera
	 */
	def consultar(params){
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		if(!params.id){
			resultado.mensaje = 'El id es un dato requerido'
			return resultado
		}

		def carrera = obtener(params.id)
		if(!carrera){
			resultado.mensaje = 'Carrera no encontrada'
			return resultado
		}

		//Verifica si la carrera es perteneciente a una institución externa o no
		if(carrera.institucion.externa){
			resultado.mensaje = 'La carrera pertenece a una institución externa'
			return resultado
		}
		if(!usuarioService.perteneceANivel(carrera.nivel.id)){
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
			return resultado
		}
		def roles = usuarioService.obtenerRoles()
		if(usuarioService.esSupervisorPublico()){
			if(!carrera.institucion.publica){
				resultado.mensaje = 'La carrera pertenece a una institución privada'
				return resultado
			}
		}else if(carrera.institucion.publica){
			resultado.mensaje = 'La carrera pertenece a una institución pública'
			return resultado
		}

		resultado.estatus = true
		resultado.mensaje = 'Carrera encontrada exitosamente'
		resultado.datos = carrera

		return resultado
	}

	def obtener(id){
		def carrera = Carrera.get(id)

		if(!carrera) return null
		if(!carrera.activo) return null

		return carrera
	}

	def obtenerPorNombre(nombre){
		def carrera = Carrera.findByNombre(nombre)

		if(!carrera) return null
		if(!carrera.activo) return null

		return carrera
	}

	// def obtenerPorRvoeInstitucion(rvoe, institucion){
	// 	def carrera = Carrera.findByRvoeAndInstitucion(rvoe, institucion)

	// 	if(!carrera) return null
	// 	if(!carrera.activo) return null

	// 	return carrera
	// }

	/**
	 * Permite realizar la modificación de la carrera seleccionada
	 * @param params
	 * parametros de la carrera
	 * @param institucionId (requerido)
	 * Id de la institución
	 * @param nivelId
	 * Id del nivel
	 * @param modalidadId
	 * Id de la modalidad
	 * @param areaId
	 * Id del area
	 * @return resultado
	 * resultado con el estatus, mensaje y datos de la carrera
	 */
	def modificar(params){
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		def datosBitacora = [
            clase: "CarreraService",
            metodo: "modificar",
            nombre: "Modificacion de carrera",
            descripcion: "Se modifica una carrera",
            estatus: "ERROR"
        ]

		// Se obtiene y valida la carrera a modificar
		def carrera = obtener(params.id)
		if(!carrera){
			resultado.mensaje = 'Carrera no encontrada'
			return resultado
		}
		if(carrera.numCertificados != 0){
			resultado.mensaje = 'No se puede modificar la carrera ya que cuenta con certificados expedidos'
			return resultado
		}
			// Se valida que que el usuario pueda editar la carrera
		if (!validarPrivilegios(carrera.institucion, carrera.nivel)) {
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
        }

		// Se obtienen las relaciones
		def institucion = institucionService.obtener(params.institucionId)
		if(!institucion){
			resultado.mensaje = 'Institucion no encontrada'
			return resultado
		}

		def nivel = nivelService.obtener(params.nivelId)
		if(!nivel){
			resultado.mensaje = 'Nivel no encontrado'
			return resultado
		}

		def modalidad = modalidadService.obtener(params.modalidadId)
		if(!modalidad){
			resultado.mensaje = 'Modalidad no encontrada'
			return resultado
		}

		def area = areaService.obtener(params.areaId)
		if(params.areaId && !area){
			resultado.mensaje = 'Area no encontrada'
			return resultado
		}

		def resultadoValidacion
        // Se validan los privilegios del usuario para asignar la institución y nivel
		resultadoValidacion = validarPrivilegios(institucion, nivel)
		if (!resultadoValidacion) {
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
        }
        // Se validan y formatean los datos recibidos
		def formatoFecha = "yyyy-MM-dd"
		resultadoValidacion = validarDatosRegistro(params, formatoFecha)
		if (!resultadoValidacion.estatus) {
			resultado.mensaje = resultadoValidacion.mensaje
            return resultado
        }

		// Se obtienen los datos con formato
		params = resultadoValidacion.datos

		//Se crean copias auxiliares de los objetos
		def carreraAux = new Carrera(carrera.properties)
		carreraAux.institucion = carrera.institucion
		carreraAux.nivel = carrera.nivel
		carreraAux.modalidad = carrera.modalidad
		carreraAux.area = carrera.area

		carrera.nombre = params.nombre
		carrera.claveSeem = params.claveSeem
		carrera.claveDgp = params.claveDgp
		carrera.institucion = institucion
		carrera.nivel = nivel
		carrera.modalidad = modalidad
		carrera.area = area

		//En caso de cambios se modifica el campo de ultima actualización con la fecha actual en la que se realizo el cambio.
		if(!carrera.equals(carreraAux))
			carrera.ultimaActualizacion = new Date()

		if(!carrera.save(flush: true)){
			transactionStatus.setRollbackOnly()
			bitacoraService.registrar(datosBitacora)
			resultado.mensaje = "Error al guardar la carrera"
			return resultado
		}

		datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = "Carrera modificada exitosamente"
		resultado.datos = carrera

		return resultado

	}

	/**
	 * Permite eliminar la carrera seleccionada
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

		if(!params.id){
			resultado.mensaje = 'El id es un dato requerido'
			return resultado
		}

		def carrera = Carrera.get(params.id)

		if(!usuarioService.perteneceANivel(carrera.nivel.id)){
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
			return resultado
		}

		if(!carrera){
			resultado.mensaje = 'Carrera no encontrada'
			return resultado
		}
		if(carrera.institucion.externa){
			resultado.mensaje = 'La carrera pertenece a una institución externa'
			return resultado
		}
		def roles = usuarioService.obtenerRoles()
		if(usuarioService.esSupervisorPublico()){
			if(!carrera.institucion.publica){
				resultado.mensaje = 'La carrera pertenece a una institución privada'
				return resultado
			}
		}else if(carrera.institucion.publica){
			resultado.mensaje = 'La carrera pertenece a una institución pública'
			return resultado
		}

		carrera.activo = false

		if(!carrera.save(flush:true)){
			carrera.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar([clase:"CarreraService", metodo:"eliminar", nombre:"Eliminación de la carrera", descripcion:"Se elimina la carrera", estatus:"ERROR"])
			return resultado
		}
		bitacoraService.registrar([clase:"CarreraService", metodo:"eliminar", nombre:"Eliminación de la carrera", descripcion:"Se elimina la carrera", estatus:"EXITOSO"])

		resultado.estatus = true
		resultado.mensaje = 'Carrera dada de baja exitosamente'
		resultado.datos = carrera

		return resultado
	}

	def cargarPorExcel(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

		def datosBitacora = [
            clase: "CarreraService",
            metodo: "cargarPorExcel",
            nombre: "Registro de carreras",
            descripcion: "Se registran varias carreras",
            estatus: "ERROR"
        ]

        // Se obtienen las relaciones generales
		def institucion = institucionService.obtener(params.institucionId)
		if(!institucion){
			resultado.mensaje = 'Institución no encontrada'
			return resultado
		}

		def nivel = nivelService.obtener(params.nivelId)
		if(!nivel){
			resultado.mensaje = 'Nivel no encontrado'
			return resultado
		}

        // Se validan los privilegios del usuario
		def resultadoValidacion = validarPrivilegios(institucion, nivel)
		if (!resultadoValidacion) {
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
        }

        if(!params.excel){
            resultado.mensaje = 'El excel es un dato requerido'
			return resultado
        }

		def resultadoOperacion = obtenerRegistros(params.excel)
		if(!resultadoOperacion.estatus){
			resultado.mensaje = resultadoOperacion.mensaje
			return resultado
		}

		def carreras = resultadoOperacion.datos

		for(datosCarrera in carreras){

			def carrera = new Carrera()
			carrera.nombre = datosCarrera.nombre?.trim()?.toUpperCase()
			carrera.claveSeem = datosCarrera.claveSeem?.trim()?.toUpperCase()
			carrera.claveDgp = datosCarrera.claveDgp?.trim()?.toUpperCase()
			// carrera.rvoe = datosCarrera.rvoe?.trim()?.toUpperCase()
			// carrera.fechaRvoe = datosCarrera.fechaRvoe
			carrera.modalidad = datosCarrera.modalidad	
			carrera.area = datosCarrera.area
			carrera.nivel = nivel
			carrera.institucion = institucion

			if(!carrera.save(flush: true)){
				transactionStatus.setRollbackOnly()
				bitacoraService.registrar(datosBitacora)
				resultado.mensaje = "Error al guardar la carrera. Fila ${datosCarrera.rowNumber}"
				return resultado
			}

		}

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = "Carreras registradas exitosamente"

		return resultado
	}

	def obtenerRegistros(excel){
		def resultado = [
            estatus: false,
            mensaje: '',
            datos: []
        ]

		def formatoFecha = "MM/dd/yy"

		try {
			def file = new ByteArrayInputStream(excel.getBytes())
			def workbook = WorkbookFactory.create(file)
			def sheet = workbook.getSheetAt(0)
			def rowIterator = sheet.iterator()

			DataFormatter formatter = new DataFormatter();

			// Se hace el salto de 8 lineas de encabezado
			(1..7).each { rowIterator.next() }

			while (rowIterator.hasNext()) {
				def row = rowIterator.next()
				def rowNumber = row.getRowNum() + 1

                if(!formatter.formatCellValue(row.getCell(0))) break

               	def datosCarrera = obtenerRegistroDeFila(row)

                // Si validan las relaciones
				def modalidad = modalidadService.obtenerPorNombre(datosCarrera.modalidad)
				if(!modalidad){
					resultado.mensaje = 'Modalidad no encontrada'
					return resultado
				}

				def area = areaService.obtenerPorNombre(datosCarrera.area)
				if(datosCarrera.area && !area){
					resultado.mensaje = 'Area no encontrada'
					return resultado
				}

				// Se validan y formatean los datos recibidos
				def resultadoValidacion = validarDatosRegistro(datosCarrera, formatoFecha)
				if (!resultadoValidacion.estatus) {
					resultado.mensaje = "${resultadoValidacion.mensaje}. Fila ${rowNumber}"
					return resultado
				}

				// Se asignan los datos con formato
				datosCarrera = resultadoValidacion.datos
				datosCarrera.modalidad = modalidad
				datosCarrera.area = area
				datosCarrera.rowNumber = rowNumber

				resultado.datos << datosCarrera

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

        def carrera = [
            nombre: formatter.formatCellValue(row.getCell(0)),
            claveSeem: formatter.formatCellValue(row.getCell(1)),
            claveDgp: formatter.formatCellValue(row.getCell(2)),
            // rvoe: formatter.formatCellValue(row.getCell(3)),
            // fechaRvoe: formatter.formatCellValue(row.getCell(4)),
            modalidad: formatter.formatCellValue(row.getCell(5)),
            area: formatter.formatCellValue(row.getCell(6)),
        ]

        return carrera
    }

}
