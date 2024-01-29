package sieges

import grails.gorm.transactions.Transactional
import org.hibernate.criterion.CriteriaSpecification
import groovy.json.JsonSlurper

import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.DataFormatter;

/**
 * @authors Alan Guevarin
 * @since 2022
 */

@Transactional
class AlumnoService {
	/**
	 * Inyección de messageSource que contiene mensajes de validaciones para los atributos de cada dominio
	 * Los mensajes de validación se encuentran en: grails-app/i18n/messages_es.properties
	 */
	def messageSource
	/**
	 * Inyección de BitacoraService que contiene la lógica de administración de la bitacora
	 */
	def bitacoraService
	def usuarioService
	def springSecurityService
	def renapoService
	def estatusAlumnoService
	def estatusRegistroService
	def formatoService
	def carreraService
	def planEstudiosService
	def cicloEscolarService

	/**
	* Permite realizar el registro de los alumnos tomando en cuenta el tipo en donde se quiera registrar.
	* @param matricula (requerido)
	* matricula del alumno
	* @return resultado
	* resultado con el estatus, mensaje y datos del alumno
	*/
	def registrar(params) {
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		def datosBitacora = [
            clase: "AlumnoService",
            metodo: "registrar",
            nombre: "Registro de alumno",
            descripcion: "Se registra un alumno",
            estatus: "ERROR"
        ]

		// Se obtienen las relaciones
		def planEstudios = planEstudiosService.obtener(params.planEstudiosId)
		if(!planEstudios){
		    resultado.mensaje = 'Plan de estudios no encontrado'
		    return resultado
		}

		def estatusAlumno = estatusAlumnoService.obtener(params.estatusAlumnoId)
		if(!estatusAlumno){
		    resultado.mensaje = 'Estatus no encontrado'
		    return resultado
		}

		// Se valida que el plan de estudios pertenesca a la misma institución que el usuario
		def institucionId = planEstudios.carrera.institucion.id
        if(!usuarioService.perteneceAInstitucion(institucionId)){
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
			return resultado
		}

		// Se validan y formatean los datos recibidos
		def resultadoValidacion = validarDatosRegistro(params, planEstudios)
		if (!resultadoValidacion.estatus) {
			resultado.mensaje = resultadoValidacion.mensaje
            return resultado
        }

		// Se asignan los datos con formato
		params = resultadoValidacion.datos

		// Se busca si ya existe una persona registrada con la curp recibida
		// En caso de que no se registra una nueva persona
		def persona = Persona.findByCurp(params.curp)
		if(!persona){
			def respuestaRenapo = renapoService.consultarDatosCurp(params.curp)
			if (!respuestaRenapo.estatus) {
				resultado.mensaje = respuestaRenapo.mensaje
				return resultado
			}
			def datosCurp = respuestaRenapo.datos

			persona = new Persona()
			persona.nombre = datosCurp.nombre?.trim()?.toUpperCase()
			persona.primerApellido = datosCurp.apellidoPaterno?.trim()?.toUpperCase()
			persona.segundoApellido = datosCurp.apellidoMaterno?.trim()?.toUpperCase()
			persona.curp = datosCurp.curp?.trim()?.toUpperCase()
			persona.sexo = datosCurp.sexo?.trim()?.toUpperCase()
			persona.entidadNacimiento = datosCurp.claveEntidadNacimiento?.trim()?.toUpperCase()
			persona.fechaNacimiento = datosCurp.fechaNacimiento?.trim()?.toUpperCase()
			persona.correoElectronico = params.correoElectronico
			persona.telefono = params.telefono

			if(!persona.save(flush: true)){
				transactionStatus.setRollbackOnly()
				bitacoraService.registrar(datosBitacora)
				resultado.mensaje = "Error al guardar al alumno"
				return resultado
			}
		}

		def alumno = new Alumno()
		alumno.matricula = params.matricula
		alumno.formacion = params.formacion
		alumno.estatusRegistro = EstatusRegistro.get(estatusRegistroService.EDITABLE)
		alumno.estatusAlumno = estatusAlumno
		alumno.planEstudios = planEstudios
		alumno.persona = persona

		if(!alumno.save(flush: true)){
			transactionStatus.setRollbackOnly()
			bitacoraService.registrar(datosBitacora)
			resultado.mensaje = "Error al guardar al alumno"
			return resultado
		}

		datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = "Alumno creado exitosamente"
		resultado.datos = alumno

		return resultado
	}

	def validarDatosRegistro(params, planEstudios){
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

		// Validación de reglas de negocio de la empresa
		def existeAlumnoConMatricula = Alumno.findByPlanEstudiosAndMatriculaAndActivo(planEstudios, params.matricula, true)
		if(existeAlumnoConMatricula){
			resultado.mensaje = "La matrícula ${params.matricula} ya se encuentra asignada"
			return resultado
		}

		def persona = Persona.findByCurp(params.curp)
		def existeAlumnoConCurp = Alumno.findByPlanEstudiosAndPersonaAndActivo(planEstudios, persona, true)
		if(existeAlumnoConCurp){
			resultado.mensaje = "El alumno con CURP ${params.curp} ya se encuentra registrado"
			return resultado
		}

		println(planEstudios.numFormacionesIndividuales)

		if(planEstudios.numFormacionesIndividuales && !params.formacion){
			resultado.mensaje = "La formación es un dato requerido"
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

		if(!params.curp){
			resultado.mensaje = "La curp es un dato requerido"
            return resultado
		}

		if(!params.matricula){
			resultado.mensaje = "La matricula es un dato requerido"
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

		if(params.correoElectronico && !formatoService.isValidEmail(params.correoElectronico)){
			resultado.mensaje = "El correo electrónico no cuenta con un formato válido"
			return resultado
        }

		if(params.telefono && !formatoService.isValidPhoneNumber(params.telefono)){
			resultado.mensaje = "El teléfono no cuenta con un formato válido"
			return resultado
        }

		resultado.estatus = true
		return resultado
	}

	def formatearDatosRegistro(params){
		params.curp = formatoService.toFlatString(params.curp)
		params.matricula = formatoService.toFlatString(params.matricula)
		params.correoElectronico = formatoService.toFlatString(params.correoElectronico)
		params.telefono = formatoService.toFlatString(params.telefono)
		params.formacion = Formacion.get(params.formacionId)

		return params
	}

	/**
	* Permite generar el listado de los alumnos que se encuentran registrados, además de permitir realizar la
	busqueda por nombre y el filtrado a través de la institución, carrera y ciclo escolar
	* @param params
	* parametros del alumno
	* @param institucionId (requerido)
	* id de la institución
	* @param carreraId (requerido)
	* id de la carrera
	* @param cicloEscolarId (requerido)
	* id del ciclo escolar
	* @return resultado
	* resultado con el estatus, mensaje y datos del alumno
	*/
	def listar(params){
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		def usuario = usuarioService.obtenerUsuarioLogueado()
		def roles = usuarioService.obtenerRoles()

		if(!params.max) params.max = 50
		if(!params.offset) params.offset = 0
		if(!params.sort) params.sort = 'id'
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
		if(params.carreraId) carreraId = Integer.parseInt(params.carreraId)

		def criteria = {
			createAlias("planEstudios", "plan", CriteriaSpecification.LEFT_JOIN)
			createAlias("planEstudios.carrera", "carrera", CriteriaSpecification.LEFT_JOIN)
			createAlias("planEstudios.carrera.institucion", "inst", CriteriaSpecification.LEFT_JOIN)
			createAlias("persona", "p", CriteriaSpecification.LEFT_JOIN)
			createAlias("estatusRegistro", "er", CriteriaSpecification.LEFT_JOIN)
			and{
				eq("activo", true)

				if(params.search){
					or{
						ilike("p.nombre", "%${params.search}%")
						ilike("p.primerApellido", "%${params.search}%")
						ilike("p.segundoApellido", "%${params.search}%")
						ilike("p.curp", "%${params.search}%")
						ilike("matricula", "%${params.search}%")
					}
				}

				or{
					usuario.instituciones.each{ registro ->
						eq("inst.id", registro.institucion.id)
					}
				}

				if(institucionId){
 				    eq("inst.id", institucionId)
 				}

				if(params.carreraId){
					eq("carrera.id", carreraId)
				}
				if(params.planEstudiosId){
					eq("plan.id", params.planEstudiosId.toInteger())
				}
			}
			order("p.nombre", "asc")
		}


		def alumnos = Alumno.createCriteria().list(params,criteria)

		if(alumnos.totalCount <= 0){
			resultado.mensaje = 'No se encontraron alumnos'
			resultado.datos = alumnos
			return resultado
		}

		resultado.estatus = true
		resultado.mensaje = 'Alumnos consultados exitosamente'
		resultado.datos = alumnos

		return resultado
	}

	/**
	* Permite mostrar la lista de los alumnos que se encuentran activas
	* @param params (requerido)
	* parametros del alumno
	* @return resultado
	* resultado con el estatus, mensaje y datos del alumno
	*/
	def obtenerActivos(params){
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		def alumnos = Alumno.createCriteria().list {
			eq("activo", true)
		}

		resultado.estatus = true
		resultado.mensaje = 'Alumnos consultados exitosamente'
		resultado.datos = alumnos

		return resultado
	}
/**
 * Permite realizar la consulta del registro seleccionado con su información correspondiente.
 * @param id
 * id del alumno
 * @return resultado
 * resultado con el estatus, mensaje y datos del alumno
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

		def alumno = Alumno.get(params.id)

		if(!alumno){
			resultado.mensaje = 'Alumno no encontrado'
			return resultado
		}

		if(!alumno.activo){
			resultado.mensaje = 'Alumno inactivo'
			return resultado
		}

		resultado.estatus = true
		resultado.mensaje = 'Alumno encontrado exitosamente'
		resultado.datos = alumno

		return resultado
	}

	/**
	* Permite realizar la modificación del alumno seleccionado
	* @param params (requerido)
	* parametros del alumno
	* @param cicloEscolarId
	* id del ciclo Escolar
	* @param cursoId
	* id del curso
	* @param diplomadoId
	* id del diplomado
	* @return resultado
	* resultado con el estatus, mensaje y datos de la asignatura foránea / externa
	*/
	def modificar(params) {
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		def datosBitacora = [
            clase: "AlumnoService",
            metodo: "modificar",
            nombre: "Modificación de alumno",
            descripcion: "Se modifica un alumno",
            estatus: "ERROR"
        ]

		if(!params.id){
			resultado.mensaje = 'El id del alumno es un dato requerido'
			return resultado
		}

		def alumno = Alumno.get(params.id)

		if(!alumno){
			resultado.mensaje = 'Alumno no encontrado'
			return resultado
		}
		if(!alumno.activo){
			resultado.mensaje = 'Alumno inactivo'
			return resultado
		}

		// Se crean copias auxiliares de los objetos
		// para verificar si se realizó algun cambio
		def alumnoAux = new Alumno(alumno.properties)
		alumnoAux.estatusAlumno = alumno.estatusAlumno
		alumnoAux.planEstudios = alumno.planEstudios
		alumnoAux.curso = alumno.curso
		alumnoAux.diplomado = alumno.diplomado

		def personaAux = new Persona()
		personaAux.nombre = alumno.persona.nombre
		personaAux.primerApellido = alumno.persona.primerApellido
		personaAux.segundoApellido = alumno.persona.segundoApellido
		personaAux.curp = alumno.persona.curp
		personaAux.rfc = alumno.persona.rfc
		personaAux.sexo = alumno.persona.sexo
		personaAux.entidadNacimiento = alumno.persona.entidadNacimiento
		personaAux.fechaNacimiento = alumno.persona.fechaNacimiento
		personaAux.correoElectronico = alumno.persona.correoElectronico
		personaAux.telefono = alumno.persona.telefono

		def estatusAlumno = EstatusAlumno.get(params.estatusAlumnoId)
		if(!estatusAlumno){
		    resultado.mensaje = 'Estatus no encontrado'
		    return resultado
		}
		if(!estatusAlumno.activo){
		    resultado.mensaje = 'Estatus inactivo'
		    return resultado
		}
		
		if(alumno.estatusRegistro.id == estatusRegistroService.EDITABLE){
			alumno.matricula = params.matricula
		}
		alumno.estatusAlumno = estatusAlumno
		alumno.persona.correoElectronico = params.correoElectronico?:null
		alumno.persona.telefono = params.telefono?:null

		// En caso de cambios se modifica el campo de ultimaActualizacion
		if(!alumno.equals(alumnoAux))
			alumno.ultimaActualizacion = new Date()
		if(!alumno.persona.equals(personaAux))
			alumno.persona.ultimaActualizacion = new Date()

		Persona.withTransaction {status ->
			if(alumno.persona.save(flush:true)){
				if(alumno.save(flush:true)){
					resultado.estatus = true
				}else{
					status.setRollbackOnly()
					alumno.errors.allErrors.each {
						resultado.mensaje = messageSource.getMessage(it, null)
					}
				}
			}else{
				alumno.persona.errors.allErrors.each {
					resultado.mensaje = messageSource.getMessage(it, null)
				}
			}
		}

		if(!resultado.estatus){
			bitacoraService.registrar(datosBitacora)
			return resultado
		}

		def listaCiclos = []
		
		if(params.listaCiclos){
			def error = false
			try{
				def jsonSlurper = new JsonSlurper()
				listaCiclos = jsonSlurper.parseText(params.listaCiclos)
				listaCiclos.each{ ciclo ->
					def cicloAux = AlumnoCicloEscolar.get(ciclo.id)
					if(!cicloAux){
						resultado.mensaje = "No se encontró el ciclo"
						error = true
					}
					if(cicloAux.estatusRegistro.id != estatusRegistroService.EDITABLE){
						resultado.mensaje = "Ciclo no editable"
						error = true
					}
				}
			}catch(Exception ex){
				resultado.mensaje = 'Error en formato de ciclos'
				error = true
			}
			if(error) return resultado
		}

		for (ciclo in listaCiclos) {
			def cicloAux = AlumnoCicloEscolar.get(ciclo.id)

			if(cicloAux.estatusRegistro.id != estatusRegistroService.EDITABLE) continue

			cicloAux.activo = false
			cicloAux.save(flush: true)
			
			alumno.evaluaciones.each{ evaluacion ->
				if(evaluacion.cicloEscolar.id == cicloAux.cicloEscolar.id){
					evaluacion.activo = false
					evaluacion.save(flush:true)
				}
			}
		}

		datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.mensaje = 'Alumno modificado exitosamente'
		resultado.datos = alumno
		return resultado
	}

/**
 * Permite eliminar el alumno seleccionado
 * @param id (requerido)
 * id del alumno seleccionado
 * @return resultado
 * resultado con el estatus, mensaje y datos del alumno
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

		def alumno = Alumno.get(params.id)

		if(!alumno){
			resultado.mensaje = 'Alumno no encontrado'
			return resultado
		}

		if(alumno.estatusRegistro.id != estatusRegistroService.EDITABLE){
			resultado.mensaje = 'No es posible eliminar al alumno ya que cuenta ya cuenta con certificados expedidos'
			return resultado
		}

		alumno.activo = false

		if(!alumno.save(flush:true)){
			alumno.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar([clase:"AlumnoService", metodo:"eliminar", nombre:"Eliminación del Alumno", descripcion:"Se elimina al alumno", estatus:"ERROR"])
			return resultado
		}

		alumno.evaluaciones.each{ evaluacion ->
			evaluacion.activo = false
			evaluacion.save(flush:true)
		}

		alumno.ciclosEscolares.each{ ciclo ->
			ciclo.activo = false
			ciclo.save(flush:true)
		}

		alumno.certificados.each{ certificado ->
			certificado.activo = false
			certificado.save(flush:true)
		}

		bitacoraService.registrar([clase:"AlumnoService", metodo:"eliminar", nombre:"Eliminación del Alumno", descripcion:"Se elimina al alumno", estatus:"EXITOSO"])

		resultado.estatus = true
		resultado.mensaje = 'Alumno dado de baja exitosamente'
		resultado.datos = alumno

		return resultado
	}

	def validarAlumno(params){
		def resultado = [
			estatus: false,
			encontrado: false,
			mensaje: '',
			alumno: null,
			persona: null,
		]

		if(!params.curp){
			resultado.mensaje = "La curp es una dato requerido"
			return resultado
		}

		if(!params.planEstudiosId){
			resultado.mensaje = "El plan de estudios es un dato requerido"
		    return resultado
		}

		def alumnos = Alumno.createCriteria().list {
			createAlias("persona", "p", CriteriaSpecification.LEFT_JOIN)
			createAlias("planEstudios", "plan", CriteriaSpecification.LEFT_JOIN)
			and{
				eq("plan.id", Integer.parseInt(params.planEstudiosId))
				ilike("p.curp", "%${params.curp}%")
				eq("activo", true)
			}
		}

		if(alumnos.size() <= 0){
			resultado.estatus = true
			return resultado
		}

		resultado.estatus = true
		resultado.encontrado = true
		resultado.alumno = alumnos[0]
		resultado.persona = alumnos[0].persona
		return resultado
	}

	def cargarPorExcel(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

		def datosBitacora = [
            clase: "AlumnoService",
            metodo: "cargarPorExcel",
            nombre: "Registro de alumnos",
            descripcion: "Se registra varios alumnos",
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

		def alumnos = resultadoOperacion.datos
		def estatusRegistro = EstatusRegistro.get(estatusRegistroService.EDITABLE)

		for(datosAlumno in alumnos){
			// Se busca si ya existe una persona registrada con la curp recibida
			// En caso de que no se registra una nueva persona
			def persona = Persona.findByCurp(datosAlumno.curp)
			if(!persona){
				def respuestaRenapo = renapoService.consultarDatosCurp(datosAlumno.curp)
				if (!respuestaRenapo.estatus) {
					resultado.mensaje = respuestaRenapo.mensaje
					return resultado
				}
				def datosCurp = respuestaRenapo.datos

				persona = new Persona()
				persona.nombre = datosCurp.nombre?.trim()?.toUpperCase()
				persona.primerApellido = datosCurp.apellidoPaterno?.trim()?.toUpperCase()
				persona.segundoApellido = datosCurp.apellidoMaterno?.trim()?.toUpperCase()
				persona.curp = datosCurp.curp?.trim()?.toUpperCase()
				persona.sexo = datosCurp.sexo?.trim()?.toUpperCase()
				persona.entidadNacimiento = datosCurp.claveEntidadNacimiento?.trim()?.toUpperCase()
				persona.fechaNacimiento = datosCurp.fechaNacimiento?.trim()?.toUpperCase()
				persona.correoElectronico = datosAlumno.correoElectronico
				persona.telefono = datosAlumno.telefono

				if(!persona.save(flush: true)){
					transactionStatus.setRollbackOnly()
					bitacoraService.registrar(datosBitacora)
					resultado.mensaje = "Error al guardar el alumno. Fila ${datosAlumno.rowNumber}"
					return resultado
				}
			}

			def alumno = new Alumno()
			alumno.matricula = datosAlumno.matricula
			alumno.estatusAlumno = datosAlumno.estatusAlumno
			alumno.estatusRegistro = estatusRegistro
			alumno.planEstudios = planEstudios
			alumno.persona = persona

			if(!alumno.save(flush: true)){
				transactionStatus.setRollbackOnly()
				bitacoraService.registrar(datosBitacora)
				resultado.mensaje = "Error al guardar el alumno. Fila ${datosAlumno.rowNumber}"
				return resultado
			}
		}

		datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

        resultado.estatus = true
        resultado.mensaje = 'Alumnos registrados exitosamente'
        return resultado
	}

	def obtenerRegistros(excel, planEstudios){
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

			DataFormatter formatter = new DataFormatter()

            // Se hace el salto de 8 lineas de encabezado
			(1..6).each { rowIterator.next() }

			while (rowIterator.hasNext()) {
				def row = rowIterator.next()
				def rowNumber = row.getRowNum() + 1

                // Si la primera columna de la fila esta vacia se toma como el final del documento
                if(!formatter.formatCellValue(row.getCell(0))) break

               	def datosAlumno = obtenerRegistroDeFila(row)

				// Se obtienen las relaciones
				def estatusAlumno = estatusAlumnoService.obtenerPorNombre(datosAlumno.estatusAlumnoId)
				if(!estatusAlumno){
					resultado.mensaje = 'Estatus no encontrado'
					return resultado
				}

				// Se validan y formatean los datos recibidos
                def resultadoValidacion = validarDatosRegistro(datosAlumno, planEstudios)
				if (!resultadoValidacion.estatus) {
					resultado.mensaje = "${resultadoValidacion.mensaje}. Fila ${rowNumber}"
					return resultado
				}

				// Se asignan los datos con formato
				datosAlumno = resultadoValidacion.datos
				datosAlumno.estatusAlumno = estatusAlumno
				datosAlumno.rowNumber = rowNumber

				resultado.datos << datosAlumno
			}

			file.close()

		} catch (Exception ex) {
            resultado.mensaje = "Ocurrio un error ${ex}"
			resultado.datos = null
			return resultado
		}

		resultado.estatus = true
		return resultado
	}

    def obtenerRegistroDeFila(row){
        DataFormatter formatter = new DataFormatter()

        def alumno = [
            curp: formatter.formatCellValue(row.getCell(0)),
            matricula: formatter.formatCellValue(row.getCell(1)),
            correoElectronico: formatter.formatCellValue(row.getCell(2)),
            telefono: formatter.formatCellValue(row.getCell(3)),
            estatusAlumnoId: formatter.formatCellValue(row.getCell(4)),
        ]

        return alumno
    }

	def obtener(id){
		def alumno = Alumno.get(id)

		if(!alumno) return null
		if(!alumno.activo) return null

		return alumno
	}
}
