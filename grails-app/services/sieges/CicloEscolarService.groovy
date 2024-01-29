package sieges

import grails.gorm.transactions.Transactional
import org.hibernate.criterion.CriteriaSpecification
import java.text.SimpleDateFormat

import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.DataFormatter;

/**
 * CicloEscolarService para la administración de los Ciclos Escolares
 * @authors Luis Dominguez
 * @since 2021
 */

@Transactional
class CicloEscolarService {
	/**
	 * Inyección de messageSource el cual contiene los mensajes para las validaciones de los atributos de la clase de
	 dominio del cicloEscolar
	 */
	def messageSource
	/**
	 * Inyección de BitacoraService que contiene la lógica de administración de la bitacora
	 */
	def bitacoraService
	def usuarioService
	def springSecurityService
	def estatusRegistroService
	def planEstudiosService
	def formatoService

	/**
	 * Permite realizar el registro de ciclos escolares.
	 * @param params (requerido)
	 * parametros del ciclo escolar
	 * @param carreraId (requerido)
	 * Id de la carrera
	 * @return resultado
	 * resultado con el estatus, mensaje y datos del ciclo escolar
	 */
	def registrar(params) {
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		def datosBitacora = [
            clase: "CicloEscolarService",
            metodo: "registrar",
            nombre: "Registro de ciclo escolar",
            descripcion: "Se registra un nuevo ciclo escolar",
            estatus: "ERROR"
        ]

		// Se obtienen las relaciones
		def planEstudios = planEstudiosService.obtener(params.planEstudiosId)
		if(!planEstudios){
			resultado.mensaje = 'Plan de estudios no encontrada'
			return resultado
		}

		// Se valida que la carrera pertenezca a la misma institución que el usuario
		def institucionId = planEstudios.carrera.institucion.id
        if(!usuarioService.perteneceAInstitucion(institucionId)){
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
			return resultado
		}

		// Se validan y formatean los datos recibidos
		def formatoFecha = "yyyy-MM-dd"
		def resultadoValidacion = validarDatosRegistro(params, formatoFecha, planEstudios)
		if (!resultadoValidacion.estatus) {
			resultado.mensaje = resultadoValidacion.mensaje
            return resultado
        }

		// Se asignan los datos con formato
		params = resultadoValidacion.datos

		def cicloEscolar = new CicloEscolar()
		cicloEscolar.nombre = params.nombre
		cicloEscolar.periodo = params.periodo
		cicloEscolar.fechaInicio = params.fechaInicio
		cicloEscolar.fechaFin = params.fechaFin
		cicloEscolar.planEstudios = planEstudios
		cicloEscolar.estatusRegistro = EstatusRegistro.get(estatusRegistroService.EDITABLE)

		if(!cicloEscolar.save(flush: true)){
			transactionStatus.setRollbackOnly()
			bitacoraService.registrar(datosBitacora)
			resultado.mensaje = "Error al guardar el ciclo escolar"
			return resultado
		}

		datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = "Ciclo Escolar creado exitosamente"
		resultado.datos = cicloEscolar

		return resultado
	}

	def validarDatosRegistro(params, formatoFecha, planEstudios){
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

		// Validación de reglas de negocio de la empresa
		resultadoValidacion = validarReglasNegocioRegistro(params, planEstudios)
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

		if(!params.periodo){
            resultado.mensaje = "El periodo es un dato requerido"
            return resultado
        }

		if(!params.fechaInicio){
            resultado.mensaje = "La fecha de inicio es un dato requerido"
            return resultado
        }

		if(!params.fechaFin){
            resultado.mensaje = "La fecha de fin es un dato requerido"
            return resultado
        }

		resultado.estatus = true
		return resultado
	}

	def validarFormatoDatosRegistro(params, formatoFecha){
		def resultado = [
			estatus: false,
			mensaje: ""
		]

		if(!formatoService.isDate(params.fechaInicio, formatoFecha)){
			resultado.mensaje = "La fecha de inicio no cuenta con un formato válido"
			return resultado
        }

		if(!formatoService.isDate(params.fechaFin, formatoFecha)){
			resultado.mensaje = "La fecha de fin no cuenta con un formato válido"
			return resultado
        }

		if(!formatoService.isPositiveInteger(params.periodo)){
			resultado.mensaje = "El periodo debe de ser un número entero"
			return resultado
        }

		resultado.estatus = true
		return resultado
	}

	def validarReglasNegocioRegistro(params, planEstudios){
		def resultado = [
			estatus: false,
			mensaje: "",
			datos: null
		]

		if(params.fechaInicio.compareTo(params.fechaFin) >= 0){
			resultado.mensaje = "La fecha de fin debe de ser mayor a la fecha de inicio"
			return resultado
		}

		def asiganturas = Asignatura.withCriteria {
            eq('activo', true)
            eq('periodo', params.periodo)
            eq('planEstudios', planEstudios)
        }

		if(!asiganturas){
			resultado.mensaje = "No existen asignaturas para el periodo ingresado"
			return resultado
		}

		resultado.estatus = true
		return resultado
	}

	def formatearDatosRegistro(params, formatoFecha){
		params.nombre = formatoService.toFlatString(params.nombre)
		params.periodo = formatoService.toFlatString(params.periodo)
		params.fechaInicio = new SimpleDateFormat(formatoFecha).parse(params.fechaInicio)
		params.fechaFin = new SimpleDateFormat(formatoFecha).parse(params.fechaFin)

		return params
	}

	/**
	 * Permite generar el listado de los ciclos escolares que se encuentran registrados, además de permitir realizar la
	 busqueda por nombre y el filtrado a través de la institución y la carrera.
	 * @param params (requerido)
	 * parametros del ciclo escolar
	 * @param institucionId (requerido)
	 * id de la institución
	 * @param carreraId (requerido)
	 * id de la carrera
	 * @return resultado
	 * resultado con el estatus, mensaje y datos del ciclo escolar
	 */
	def listar(params){
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: [
				ciclos: null,
				fechas: []
			]
		]

		def usuario = Usuario.get(springSecurityService.principal.id)
		def roles = usuarioService.obtenerRoles()

		if(!params.max) params.max = 50
		if(!params.offset) params.offset = 0
		if(!params.sort) params.sort = 'nombre'
		if(!params.order) params.order = 'asc'

		def institucionId
		if(params.institucionId){
			def institucionIdAux = Integer.parseInt(params.institucionId)
			if(roles.contains('ROLE_GESTOR_ESCUELA') && !usuarioService.perteneceAInstitucion(institucionIdAux)){
				institucionIdAux = null
			}
			institucionId = institucionIdAux
		}

		def carreraId
		if(params.carreraId){
		    carreraId = Integer.parseInt(params.carreraId)
		}

		def criteria = {
            createAlias("planEstudios", "p", CriteriaSpecification.LEFT_JOIN)
            createAlias("planEstudios.carrera", "c", CriteriaSpecification.LEFT_JOIN)
			createAlias("planEstudios.carrera.institucion", "i", CriteriaSpecification.LEFT_JOIN)
			and{
 				eq("activo", true)
 				if(params.search){
 				    ilike("nombre", "%${params.search}%")
 				}
 				if(institucionId){
 				    eq("i.id", institucionId)
 				}else{
					or{
						usuario.instituciones.each{ registro ->
							eq("i.id", registro.institucion.id)
						}
					}
				}
 				if(params.carreraId){
 				    eq("c.id", carreraId)
 				}
				if(params.planEstudiosId){
 				    eq("p.id", params.planEstudiosId.toInteger())
 				}
			}
		}

		def cicloEscolar = CicloEscolar.createCriteria().list(params,criteria)

		if(cicloEscolar.totalCount <= 0){
			resultado.mensaje = 'No se encontraron ciclos escolares'
			resultado.datos.ciclos = cicloEscolar
			return resultado
		}

		def formatter = new SimpleDateFormat("dd/MM/yyyy")

		cicloEscolar.each{ ciclo ->
			def fechasAux = [
				inicio: null,
				fin: null
			]
        		fechasAux.inicio = formatter.format(ciclo.fechaInicio)
        		fechasAux.fin = formatter.format(ciclo.fechaFin)
        		resultado.datos.fechas << fechasAux
		}

		resultado.estatus = true
		resultado.mensaje = 'Ciclos escolares consultados exitosamente'
		resultado.datos.ciclos = cicloEscolar

		return resultado
	}

	/**
	 * Permite mostrar la lista de los ciclo escolares que se encuentran activos
	 * @param params (requerido)
	 * parametros del ciclo escolar
	 * @return resultado
	 * resultado con el estatus, mensaje y datos del ciclo escolar
	 */
	def obtenerActivos(params){
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		def usuario = usuarioService.obtenerUsuarioLogueado()

		def carreraId
		if(params.carreraId){
			carreraId = Integer.parseInt(params.carreraId)
		}

		def cicloEscolar = CicloEscolar.createCriteria().list {
			createAlias("planEstudios", "p", CriteriaSpecification.LEFT_JOIN)
			createAlias("planEstudios.carrera", "c", CriteriaSpecification.LEFT_JOIN)
			createAlias("planEstudios.carrera.institucion", "i", CriteriaSpecification.LEFT_JOIN)
			and{
				eq("activo", true)
				if(params.carreraId){
					eq("c.id", carreraId)
				}
				if(params.planEstudiosId){
					eq("p.id", params.planEstudiosId.toInteger())
				}
				or{
					usuario.instituciones.each{ registro ->
						eq("i.id", registro.institucion.id)
					}
				}
			}
			order('nombre', 'asc')
		}


		resultado.estatus = true
		resultado.mensaje = 'Ciclos Escolares consultados exitosamente'
		resultado.datos = cicloEscolar

		return resultado
	}

	def listarSinTramite(params){
		return CicloEscolar.createCriteria().list {
			createAlias("planEstudios", "p", CriteriaSpecification.LEFT_JOIN)
			createAlias("planEstudios.carrera", "c", CriteriaSpecification.LEFT_JOIN)
			createAlias("planEstudios.carrera.institucion", "i", CriteriaSpecification.LEFT_JOIN)
			and{
				eq("activo", true)
				eq("i.id", params.institucionId.toInteger())
				isNull("tramite")
			}
			order('nombre', 'asc')
		}
	}

	/**
	 * Permite realizar la consulta del registro seleccionado con su información correspondiente.
	 * @param params (requerido)
	 * parametros del ciclo escolar
	 * @return resultado
	 * resultado con el estatus, mensaje y datos del ciclo escolar
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

		def cicloEscolar = CicloEscolar.get(params.id)

		if(!cicloEscolar){
			resultado.mensaje = 'Ciclo escolar no encontrado'
			return resultado
		}

		if(!cicloEscolar.activo){
			resultado.mensaje = 'Ciclo escolar inactivo'
			return resultado
		}

		resultado.estatus = true
		resultado.mensaje = 'Ciclo escolar encontrado exitosamente'
		resultado.datos = cicloEscolar

		return resultado
	}

	/**
	 * Permite realizar la modificación del ciclo escolar seleccionado
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
			datos: null
		]

		def datosBitacora = [
            clase: "CicloEscolarService",
            metodo: "modificar",
            nombre: "Modificación de ciclo escolar",
            descripcion: "Se modifica un ciclo escolar",
            estatus: "ERROR"
        ]

		def cicloEscolar = obtener(params.id)
		if(!cicloEscolar){
		    resultado.mensaje = 'Ciclo escolar no encontrado'
		    return resultado
		}
		if(cicloEscolar.estatusRegistro.id != estatusRegistroService.EDITABLE){
		    resultado.mensaje = 'Ciclo escolar no editable'
		    return resultado
		}

		// Se obtienen las relaciones
		def planEstudios = planEstudiosService.obtener(params.planEstudiosId)
		if(!planEstudios){
			resultado.mensaje = 'Plan de estudios no encontrado'
			return resultado
		}

		// Se valida que la carrera pertenezca a la misma institución que el usuario
		def institucionId = planEstudios.carrera.institucion.id
        if(!usuarioService.perteneceAInstitucion(institucionId)){
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
			return resultado
		}

		// Se validan y formatean los datos recibidos
		def formatoFecha = "yyyy-MM-dd"
		def resultadoValidacion = validarDatosRegistro(params, formatoFecha, planEstudios)
		if (!resultadoValidacion.estatus) {
			resultado.mensaje = resultadoValidacion.mensaje
            return resultado
        }

		// Se asignan los datos con formato
		params = resultadoValidacion.datos

		cicloEscolar.nombre = params.nombre
		cicloEscolar.fechaInicio = params.fechaInicio
		cicloEscolar.fechaFin = params.fechaFin
		cicloEscolar.planEstudios = planEstudios
		cicloEscolar.ultimaActualizacion = new Date()

		if(cicloEscolar.numAlumnos == 0){
			cicloEscolar.periodo = params.periodo
		}

		if(!cicloEscolar.save(flush: true)){
			transactionStatus.setRollbackOnly()
			bitacoraService.registrar(datosBitacora)
			resultado.mensaje = "Error al modificar el ciclo escolar"
			return resultado
		}

		datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = "Ciclo Escolar modificado exitosamente"
		resultado.datos = cicloEscolar

		return resultado
	}

	/**
	 * Permite eliminar el ciclo escolar seleccionado
	 * @param params (requerido)
	 * parametros del ciclo escolar
	 * @return resultado
	 * resultado con el estatus, mensaje y datos del ciclo escolar
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

		def cicloEscolar = CicloEscolar.get(params.id)

		if(!cicloEscolar){
			resultado.mensaje = 'Ciclo escolar no encontrado'
			return resultado
		}
		if(cicloEscolar.estatusRegistro.id != estatusRegistroService.EDITABLE){
		    resultado.mensaje = 'Ciclo escolar no editable'
		    return resultado
		}

		cicloEscolar.activo = false

		if(!cicloEscolar.save(flush:true)){
			cicloEscolar.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar([clase:"CicloEscolarService", metodo:"eliminar", nombre:"Eliminación del ciclo escolar", descripcion:"Se elimina el ciclo escolar", estatus:"ERROR"])
			return resultado
		}
		bitacoraService.registrar([clase:"CicloEscolarService", metodo:"eliminar", nombre:"Eliminación del ciclo escolar", descripcion:"Se elimina el ciclo escolar", estatus:"EXITOSO"])

		cicloEscolar.evaluaciones.each{ evaluacion ->
			evaluacion.activo = false
			evaluacion.save(flush:true)
		}

		cicloEscolar.alumnos.each{ registro ->
			registro.activo = false
			registro.save(flush:true)
		}

		resultado.estatus = true
       	resultado.mensaje = 'Ciclo escolar dado de baja exitosamente'
       	resultado.datos = cicloEscolar

		return resultado
	}

	def cargarPorExcel(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

		def datosBitacora = [
            clase: "CicloEscolarService",
            metodo: "cargarPorExcel",
            nombre: "Registro de ciclos escolares",
            descripcion: "Se registra varios ciclos escolares",
            estatus: "ERROR"
        ]

        // Se obtienen las relaciones generales
		def planEstudios = planEstudiosService.obtener(params.planEstudiosId)
		if(!planEstudios){
			resultado.mensaje = 'Plan de estudios no encontrado'
			return resultado
		}

		// Se valida que la carrera pertenesca a la misma institución que el usuario
		def institucionId = planEstudios.carrera.institucion.id
        if(!usuarioService.perteneceAInstitucion(institucionId)){
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
			return resultado
		}

        if(!params.excel){
            resultado.mensaje = 'El excel es un dato requerido'
			return resultado
        }

		def resultadoOperacion = obtenerRegistros(params.excel, planEstudios)
		if(!resultadoOperacion.estatus){
			resultado.mensaje = resultadoOperacion.mensaje
			return resultado
		}

		def ciclosEscolares = resultadoOperacion.datos
		def estatusRegistro = EstatusRegistro.get(estatusRegistroService.EDITABLE)

		for(datosCicloEscolar in ciclosEscolares){

			def cicloEscolar = new CicloEscolar()
			cicloEscolar.nombre = datosCicloEscolar.nombre
			cicloEscolar.periodo = datosCicloEscolar.periodo
			cicloEscolar.fechaInicio = datosCicloEscolar.fechaInicio
			cicloEscolar.fechaFin = datosCicloEscolar.fechaFin
			cicloEscolar.planEstudios = planEstudios
			cicloEscolar.estatusRegistro = estatusRegistro

			if(!cicloEscolar.save(flush: true)){
				transactionStatus.setRollbackOnly()
				bitacoraService.registrar(datosBitacora)
				resultado.mensaje = "Error al guardar el ciclo escolar. Fila ${datosCicloEscolar.rowNumber}"
				return resultado
			}

		}

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = "Ciclos escolares creados exitosamente"

		return resultado
	}

	def obtenerRegistros(excel, planEstudios){
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

                // Si la primera columna de la fila esta vacia se toma como el final del documento
                if(!formatter.formatCellValue(row.getCell(0))) break

               	def datosCicloEscolar = obtenerRegistroDeFila(row)

				// Se validan y formatean los datos recibidos
				def resultadoValidacion = validarDatosRegistro(datosCicloEscolar, formatoFecha, planEstudios)
				if (!resultadoValidacion.estatus) {
					resultado.mensaje = "${resultadoValidacion.mensaje}. Fila ${rowNumber}"
					return resultado
				}

				// Se asignan los datos con formato
				datosCicloEscolar = resultadoValidacion.datos
				datosCicloEscolar.rowNumber = rowNumber

				resultado.datos << datosCicloEscolar

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

        def cicloEscolar = [
            nombre: formatter.formatCellValue(row.getCell(0)),
            periodo: formatter.formatCellValue(row.getCell(1)),
            fechaInicio: formatter.formatCellValue(row.getCell(2)),
            fechaFin: formatter.formatCellValue(row.getCell(3)),
        ]

        return cicloEscolar
    }

	def listarAlumnos(params){
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		def usuario = usuarioService.obtenerUsuarioLogueado()

		if(!params.max) params.max = 300
		if(!params.offset) params.offset = 0
		if(!params.sort) params.sort = 'id'
		if(!params.order) params.order = 'asc'

		def criteria = {
			createAlias("alumno", "alumno", CriteriaSpecification.LEFT_JOIN)
			createAlias("alumno.persona", "persona", CriteriaSpecification.LEFT_JOIN)
			createAlias("cicloEscolar", "ciclo", CriteriaSpecification.LEFT_JOIN)
			createAlias("cicloEscolar.planEstudios", "plan", CriteriaSpecification.LEFT_JOIN)
			createAlias("cicloEscolar.planEstudios.carrera", "carrera", CriteriaSpecification.LEFT_JOIN)
			createAlias("cicloEscolar.planEstudios.carrera.institucion", "inst", CriteriaSpecification.LEFT_JOIN)
			and{
				eq("ciclo.id", params.id.toInteger())
				eq("activo", true)
				or{
					usuario.instituciones.each{ registro ->
						eq("inst.id", registro.institucion.id)
					}
				}
				if(params.search){
					or{
						ilike("persona.nombre", "%${params.search}%")
						ilike("persona.primerApellido", "%${params.search}%")
						ilike("persona.segundoApellido", "%${params.search}%")
						ilike("persona.curp", "%${params.search}%")
						ilike("alumno.matricula", "%${params.search}%")
					}
				}
			}
			order('persona.nombre', 'asc')
		}


		def inscripciones = AlumnoCicloEscolar.createCriteria().list(params, criteria)

		resultado.estatus = true
		resultado.mensaje = 'Alumnos consultados exitosamente'
		resultado.datos = inscripciones

		return resultado
	}

	def cargarPorExcelAlumnos(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def cicloEscolar = obtener(params.cicloEscolarId)
        if(!cicloEscolar){
            resultado.mensaje = 'Ciclo escolar no encontrado'
			return resultado
        }

        if(!params.excel){
            resultado.mensaje = 'El excel es un dato requerido'
			return resultado
        }

		def estatusRegistro = EstatusRegistro.get(1)

		try {
			def file = new ByteArrayInputStream(params.excel.getBytes())
			def workbook = WorkbookFactory.create(file)
			def sheet = workbook.getSheetAt(0)
			def rowIterator = sheet.iterator()

			DataFormatter formatter = new DataFormatter();
            // Se hace el salto de 8 lineas
			(1..7).each { rowIterator.next() }

			while (rowIterator.hasNext()) {
				def row = rowIterator.next()

                // Si la primera columna de la fila esta vacia se toma como el final del documento
                if(!formatter.formatCellValue(row.getCell(0))) break

               	def alumno = obtenerRegistroDeFilaAlumnos(row)

                def validarDatosRespuesta = validarDatosRegistroExcelAlumnos(alumno, cicloEscolar, row.getRowNum()+1)
                if(!validarDatosRespuesta.estatus){
                    transactionStatus.setRollbackOnly()
                    resultado.mensaje = validarDatosRespuesta.mensaje
                    return resultado
                }

                def alumnoCicloEscolar = new AlumnoCicloEscolar()
                alumnoCicloEscolar.alumno = alumno.datos
                alumnoCicloEscolar.cicloEscolar = cicloEscolar
                alumnoCicloEscolar.estatusRegistro = estatusRegistro

                if(!alumnoCicloEscolar.save(flush: true)){
					transactionStatus.setRollbackOnly()
					resultado.mensaje = "Error al al inscribir al alumno de fila ${row.getRowNum()+1}"
                    return resultado
				}
			}
			file.close()

		} catch (Exception ex) {
			resultado.mensaje = "Ocurrio un error ${ex}"
            transactionStatus.setRollbackOnly()
			return resultado
		}

        resultado.estatus = true
        resultado.mensaje = 'Alumnos registrados exitosamente'
        return resultado
	}

    def obtenerRegistroDeFilaAlumnos(row){
        DataFormatter formatter = new DataFormatter();

        def alumno = [
            matricula: formatter.formatCellValue(row.getCell(0)),
        ]

        return alumno
    }

    def validarDatosRegistroExcelAlumnos(alumno, cicloEscolar, rowNumber){
        def resultado = [
            estatus: false,
            mensaje: ""
        ]

        if(!alumno.matricula){
            resultado.mensaje = "La matrícula es un dato requerido (A${rowNumber})"
            return resultado
        }

		alumno.datos = Alumno.findByMatriculaAndPlanEstudiosAndActivo(alumno.matricula, cicloEscolar.planEstudios, true)
		if(!alumno.datos){
            resultado.mensaje = "Alumno no encontrado (A${rowNumber})"
            return resultado
        }

		def estaRegistrado = AlumnoCicloEscolar.findByAlumnoAndCicloEscolarAndActivo(alumno.datos, cicloEscolar, true)
		if(estaRegistrado){
            resultado.mensaje = "El alumno con matrícula ${alumno.matricula} ya se encuentra registrado en el ciclo escolar (A${rowNumber})"
            return resultado
        }

		def yaSeEncuentraRegistrado = AlumnoCicloEscolar.createCriteria().list{
			createAlias("alumno", "alumno", CriteriaSpecification.LEFT_JOIN)
			createAlias("cicloEscolar", "ciclo", CriteriaSpecification.LEFT_JOIN)
			and{
				eq('activo', true)
				eq('alumno.id', alumno.datos.id)
				or{
					eq('ciclo.id', cicloEscolar.id)
					eq('ciclo.periodo', cicloEscolar.periodo)
				}
			}
		}

		if(yaSeEncuentraRegistrado){
			resultado.mensaje = "El alumno ya se encuentra inscrito en un ciclo escolar con el mismo periodo (A${rowNumber})"
			return resultado
		}


        resultado.estatus = true
        return resultado
    }

	def obtenerParaInscripcion(alumno){
		def ciclosEscolares = CicloEscolar.createCriteria().list {
			and{
				eq("activo", true)
				eq("planEstudios", alumno.planEstudios)

				alumno.ciclosEscolares.each{ registro ->
					if(registro.activo){
						ne("id", registro.cicloEscolar.id)
						ne("periodo", registro.cicloEscolar.periodo)
					}
				}
			}
		}

		return ciclosEscolares
    }

	def obtener(id){
		def cicloEscolar = CicloEscolar.get(id)

		if(!cicloEscolar) return null
		if(!cicloEscolar.activo) return null

		return cicloEscolar
	}

}
