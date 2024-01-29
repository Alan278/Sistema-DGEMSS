package sieges

import grails.gorm.transactions.Transactional
import org.hibernate.criterion.CriteriaSpecification
import java.text.SimpleDateFormat

import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.DataFormatter;
import sieges.PlanEstudios

/**
 * Servicio que permite la administración de planes de estudio
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */
@Transactional
class PlanEstudiosService {
    /**
     * Inyección de messageSource que contiene mensajes de validaciones para los atributos de cada dominio
     * Los mensajes de validación se encuentran en: grails-app/i18n/messages_es.properties
     */
    def messageSource
    /**
     * Inyección de AsignaturaExternaService que contiene la lógica de administración de asgnaturas externas
     */
    def asignaturaService
    /**
     * Inyección de BitacoraService que contiene la lógica de administración de la bitacora
     */
    def bitacoraService
    def usuarioService
    def carreraService
    def periodoService
    def turnoService
    def formatoService
    def institucionService
    def tipoFormacionService

    /**
     * Registra un nuevo plan
     * @param nombre (Requerido)
     * @param carreraId (Requerido)
     * @param turnoId (Requerido)
     * @param periodoId (Requerido)
     * @param ciclos (Opcional)
     * @param calificacionMinima (Opcional)
     * @param calificacionMinimaAprobatoria (Opcional)
     * @param calificacionMaxima (Opcional)
     * @param horaInicio (Opcional)
     * @param horaFin (Opcional)
     */
    def registrar(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def datosBitacora = [
            clase: "PlanEstudiosService",
            metodo: "registrar",
            nombre: "Registro de plan de estudios",
            descripcion: "Se registra un nuevo plan de estudios",
            estatus: "ERROR"
        ]

        // Se obtienen las relaciones
        def carrera = carreraService.obtener(params.carreraId)
        if(!carrera){
            resultado.mensaje = 'Carrera no encontrada'
            return resultado
        }

        def periodo = periodoService.obtener(params.periodoId)
        if(!periodo){
            resultado.mensaje = 'Periodo no encontrado'
            return resultado
        }

        def turno = turnoService.obtener(params.turnoId)
        if(!turno){
            resultado.mensaje = 'Turno no encontrado'
            return resultado
        }

        def resultadoValidacion
        // Se validan los privilegios del usuario
		resultadoValidacion = validarPrivilegios(carrera)
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

        def planEstudios = new PlanEstudios()
        planEstudios.nombre = params.nombre
        planEstudios.ciclos = params.ciclos
        planEstudios.horaInicio = params.horaInicio
        planEstudios.horaFin = params.horaFin
        planEstudios.calificacionMinima = params.calificacionMinima
        planEstudios.calificacionMinimaAprobatoria = params.calificacionMinimaAprobatoria
        planEstudios.calificacionMaxima = params.calificacionMaxima
        planEstudios.rvoe = params.rvoe
        planEstudios.fechaRvoe = params.fechaRvoe
        planEstudios.carrera = carrera
        planEstudios.periodo = periodo
        planEstudios.turno = turno

        if(!planEstudios.save(flush: true)){
			transactionStatus.setRollbackOnly()
			bitacoraService.registrar(datosBitacora)
			resultado.mensaje = "Error al guardar el plan de estudios"
			return resultado
		}

        def formacionesPorDefecto = []

        def basica = new Formacion()
        basica.nombre = "BÁSICA"
        basica.requerida = true
		basica.general = true
        basica.tipoFormacion = tipoFormacionService.obtener(tipoFormacionService.BASICA_ID)
        basica.planEstudios = planEstudios
        formacionesPorDefecto << basica

        def paraescolares = new Formacion()
        paraescolares.nombre = "PARAESCOLARES"
        paraescolares.requerida = false
		paraescolares.general = true
        paraescolares.tipoFormacion = tipoFormacionService.obtener(tipoFormacionService.PARAESCOLARES_ID)
        paraescolares.planEstudios = planEstudios
        formacionesPorDefecto << paraescolares

        def propedeutica = new Formacion()
        propedeutica.nombre = "PROPEDÉUTICA"
        propedeutica.requerida = false
		propedeutica.general = true
        propedeutica.tipoFormacion = tipoFormacionService.obtener(tipoFormacionService.PROPEDEUTICA_ID)
        propedeutica.planEstudios = planEstudios
        formacionesPorDefecto << propedeutica

        for(formacion in formacionesPorDefecto){
            if(!formacion.save(flush: true)){
                transactionStatus.setRollbackOnly()
                resultado.mensaje = "Error al guardar el plan de estudios"
                return resultado
            }
        }

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = "Plan de estudios creado exitosamente"
        resultado.datos = planEstudios

        return resultado
    }

    def validarPrivilegios(carrera){

        // Se valida el privilegio del usuario sobre instituciones
        // privadas o públicas segun corresponda su rol
        def isPublic = carrera.institucion.publica
		if(!usuarioService.validarPrivilegioSobreTipoInstitucion(isPublic)){
			return false
		}

        // Se valida que el usuario pertenezca al mismo nivel que el plan de estudios
        def nivelId = carrera.nivel.id
        if(!usuarioService.perteneceANivel(nivelId)){
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

        resultadoValidacion = validarReglasNegocio(params)
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

        if(!params.calificacionMinima){
            resultado.mensaje = "La calificación mínima es un dato requerido"
            return resultado
        }
        if(!params.calificacionMinimaAprobatoria){
            resultado.mensaje = "La calificación mínima aprobatoria es un dato requerido"
            return resultado
        }
        if(!params.calificacionMaxima){
            resultado.mensaje = "La calificación máxima es un dato requerido"
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

        if(!formatoService.isPositiveNumeric(params.calificacionMinima)){
            resultado.mensaje = "La calificación mínima debe de ser de tipo numérico"
            return resultado
        }
        if(!formatoService.isPositiveNumeric(params.calificacionMinimaAprobatoria)){
            resultado.mensaje = "La calificación mínima aprobatoria debe de ser de tipo numérico"
            return resultado
        }
        if(!formatoService.isPositiveNumeric(params.calificacionMaxima)){
            resultado.mensaje = "La calificación máxima debe de ser de tipo numérico"
            return resultado
        }
        if(params.ciclos && !formatoService.isPositiveInteger(params.ciclos)){
            resultado.mensaje = "El número de ciclos debe de ser un número entero"
            return resultado
        }

        if(!usuarioService.esSupervisorPublico() && params.fechaRvoe){
			if(!formatoService.isDate(params.fechaRvoe, formatoFecha)){
				resultado.mensaje = "La fecha del RVOE no cuenta con un formato válido"
				return resultado
			}
		}

		resultado.estatus = true
		return resultado
	}

    def validarReglasNegocio(params){
        def resultado = [
			estatus: false,
			mensaje: ""
		]

        if(params.calificacionMinima > params.calificacionMinimaAprobatoria){
            resultado.mensaje = "La calificación mínima aprobatoria debe de ser mayor a la mínima"
            return resultado
        }
        if(params.calificacionMinimaAprobatoria > params.calificacionMaxima){
            resultado.mensaje = "La calificación máxima debe de ser mayor a la mínima aprobatoria"
            return resultado
        }
        if(params.calificacionMinima == params.calificacionMaxima){
            resultado.mensaje = "La calificación máxima debe de ser mayor a la mínima"
            return resultado
        }

		resultado.estatus = true
		return resultado

    }

    def formatearDatosRegistro(params, formatoFecha){
		params.nombre = formatoService.toFlatString(params.nombre)
		params.ciclos = params.ciclos ? params.ciclos.trim().toInteger() : null
		params.horaInicio = params.horaInicio ? formatoService.toFlatString(params.horaInicio) : null
		params.horaFin = params.horaFin ? formatoService.toFlatString(params.horaFin) : null
		params.calificacionMinima = params.calificacionMinima.toFloat()
		params.calificacionMinimaAprobatoria = params.calificacionMinimaAprobatoria.toFloat()
		params.calificacionMaxima = params.calificacionMaxima.toFloat()
        params.rvoe = params.rvoe ? formatoService.toFlatString(params.rvoe) : null
		params.fechaRvoe = params.fechaRvoe ? new SimpleDateFormat(formatoFecha).parse(params.fechaRvoe) : null

		return params
	}

    /**
     * Obtiene los planes activos con parametros de paginación y filtrado
     * @param institucionId (Opcional)
     * Identificador de la institución
     * @param carreraId (Opcional)
     * Identificador de la carrera
     * @param search (Opcional)
     * Nombre del personal
     */
    def listar(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: [
                planesEstudios: null,
                numeroAsignaturas: [],
            ]
        ]

        // Parametros de paginación
        if(!params.max) params.max = 50
        if(!params.offset) params.offset = 0
        if(!params.sort) params.sort = 'id'
        if(!params.order) params.order = 'asc'

        // Parametros de filtrado (opcionales)
        def institucionId
        if(params.institucionId){
            institucionId = Integer.parseInt(params.institucionId)
        }
        def carreraId
        if(params.carreraId){
            carreraId = Integer.parseInt(params.carreraId)
        }

        def roles = usuarioService.obtenerRoles()
        params.niveles = usuarioService.obtenerNivelesPorRol()

        def criteria = {
            createAlias("carrera.institucion", "i", CriteriaSpecification.LEFT_JOIN)
            createAlias("carrera", "c", CriteriaSpecification.LEFT_JOIN)
            createAlias("carrera.nivel", "n", CriteriaSpecification.LEFT_JOIN)

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
                if(params.carreraId){
                    eq("c.id", carreraId)
                }
                or{
					params.niveles.each{ registro ->
						eq("n.id", registro)
					}
				}
            }

            order("i.nombre", "asc")
            order("c.nombre", "asc")
            order("nombre", "asc")
        }

        def planesEstudios = PlanEstudios.createCriteria().list(params,criteria)

        if(planesEstudios.totalCount <= 0){
            resultado.mensaje = 'No se encontraron Planes de Estudio'
            resultado.datos.planesEstudios = planesEstudios
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'Planes de Estudio consultados exitosamente'
        resultado.datos.planesEstudios = planesEstudios

        return resultado

    }

    /**
     * Obtiene los planes activos
     */
    def obtenerActivos(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        // Parametros de filtrado (opcionales)
        def carreraId
        if(params.carreraId){
           carreraId = Integer.parseInt(params.carreraId)
        }

        def roles = usuarioService.obtenerRoles()
        params.niveles = usuarioService.obtenerNivelesPorRol()

        def planesEstudios = PlanEstudios.createCriteria().list {
            createAlias("carrera.institucion", "i", CriteriaSpecification.LEFT_JOIN)
            createAlias("carrera", "c", CriteriaSpecification.LEFT_JOIN)
            createAlias("carrera.nivel", "n", CriteriaSpecification.LEFT_JOIN)
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
                if(params.carreraId){
                    eq("c.id", carreraId)
                }
                or{
					params.niveles.each{ registro ->
						eq("n.id", registro)
					}
				}
            }
        }

        resultado.estatus = true
        resultado.mensaje = 'Plan de Estudios consultados exitosamente'
        resultado.datos = planesEstudios

        return resultado
    }

    /**
     * Obtiene un plan específico
     * @param id (Requerido)
     * Identificador del plan
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

        def planEstudios = PlanEstudios.get(params.id)

        if(!planEstudios){
            resultado.mensaje = 'Plan de Estudios no encontrado'
            return resultado
        }

        if(!planEstudios.activo){
            resultado.mensaje = 'Plan de Estudios inactivo'
            return resultado
        }
        if(planEstudios.carrera.institucion.externa){
            resultado.mensaje = 'El Plan de Estudios pertenece a una institución externa'
            return resultado
        }
        def roles = usuarioService.obtenerRoles()
		if(usuarioService.esSupervisorPublico()){
			if(!planEstudios.carrera.institucion.publica){
                resultado.mensaje = 'El Plan de Estudios pertenece a una institución privada'
                return resultado
            }
		}else if(planEstudios.carrera.institucion.publica){
			resultado.mensaje = 'El Plan de Estudios pertenece a una institución pública'
			return resultado
		}

        if(!usuarioService.perteneceANivel(planEstudios.carrera.nivel.id)){
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
			return resultado
		}

        resultado.estatus = true
        resultado.mensaje = 'Plan de Estudios encontrado exitosamente'
        resultado.datos = planEstudios

        return resultado
    }

    /**
     * Modifica los datos de un plan específico
     * @param id (Requerido)
     * Identificador del plan
     * @param nombre (Requerido)
     * @param carreraId (Requerido)
     * @param turnoId (Requerido)
     * @param periodoId (Requerido)
     * @param ciclos (Opcional)
     * @param calificacionMinima (Opcional)
     * @param calificacionMinimaAprobatoria (Opcional)
     * @param calificacionMaxima (Opcional)
     * @param horaInicio (Opcional)
     * @param horaFin (Opcional)
     */
    def modificar(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def datosBitacora = [
            clase: "PlanEstudiosService",
            metodo: "modificar",
            nombre: "Modificación del plan de estudios",
            descripcion: "Se modifica un plan de estudios",
            estatus: "ERROR"
        ]

        // Se obtiene y valida el plan a modificar
        def planEstudios = obtener(params.id)
        if(!planEstudios){
            resultado.mensaje = 'Plan de Estudios no encontrado'
            return resultado
        }
        if(planEstudios.numCertificados){
            resultado.mensaje = 'El plan de estudios no puede ser modificado ya que cuenta con certificados expedidos'
			return resultado
        }
            // Se valida que que el usuario pueda editar el plan de estudios
		if (!validarPrivilegios(planEstudios.carrera)) {
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
        }

        // Se obtienen las relaciones
        def carrera = carreraService.obtener(params.carreraId)
        if(!carrera){
            resultado.mensaje = 'Carrera no encontrada'
            return resultado
        }

        def periodo = periodoService.obtener(params.periodoId)
        if(!periodo){
            resultado.mensaje = 'Periodo no encontrado'
            return resultado
        }

        def turno = turnoService.obtener(params.turnoId)
        if(!turno){
            resultado.mensaje = 'Turno no encontrado'
            return resultado
        }

        def resultadoValidacion
        // Se validan los privilegios del usuario
		resultadoValidacion = validarPrivilegios(carrera)
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

        // Se crean copias auxiliares de los objetos antes de asignar
        // los nuevos datos para verificar si se realizó algun cambio
        def planEstudiosAux = new PlanEstudios(planEstudios.properties)
        planEstudiosAux.carrera = planEstudios.carrera
        planEstudiosAux.periodo = planEstudios.periodo
        planEstudiosAux.turno = planEstudios.turno

        // Se asignan los nuevos datos
        planEstudios.nombre = params.nombre
        planEstudios.ciclos = params.ciclos
        planEstudios.horaInicio = params.horaInicio
        planEstudios.horaFin = params.horaFin
        planEstudios.calificacionMinima = params.calificacionMinima
        planEstudios.calificacionMinimaAprobatoria = params.calificacionMinimaAprobatoria
        planEstudios.calificacionMaxima = params.calificacionMaxima
        planEstudios.rvoe = params.rvoe
        planEstudios.fechaRvoe = params.fechaRvoe
        planEstudios.carrera = carrera
        planEstudios.periodo = periodo
        planEstudios.turno = turno

        // En caso de cambios se modifica el campo de ultimaActualizacion
        if(!planEstudios.equals(planEstudiosAux))
            planEstudios.ultimaActualizacion = new Date()

        if(!planEstudios.save(flush: true)){
			transactionStatus.setRollbackOnly()
			bitacoraService.registrar(datosBitacora)
			resultado.mensaje = "Error al modificar el plan de estudios"
			return resultado
		}

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = "Plan de estudios modificado exitosamente"
        resultado.datos = planEstudios

        return resultado
    }

    /**
     * Realiza una baja lógica de un plan específico
     * @param id (Requerido)
     * Identificador del plan
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

        def planEstudios = PlanEstudios.get(params.id)

        if(!planEstudios){
            resultado.mensaje = 'Plan de Estudios no encontrado'
            return resultado
        }
        if(planEstudios.carrera.institucion.externa){
            resultado.mensaje = 'El Plan de Estudios pertenece a una institución externa'
            return resultado
        }
        if(!usuarioService.perteneceANivel(planEstudios.carrera.nivel.id)){
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
			return resultado
		}
        def roles = usuarioService.obtenerRoles()
		if(usuarioService.esSupervisorPublico()){
			if(!planEstudios.carrera.institucion.publica){
                resultado.mensaje = 'El Plan de Estudios pertenece a una institución privada'
                return resultado
            }
		}else if(planEstudios.carrera.institucion.publica){
			resultado.mensaje = 'El Plan de Estudios pertenece a una institución pública'
			return resultado
		}

        planEstudios.activo = false

        if(!planEstudios.save(flush:true)){
            // Se recorren los errores de validación y se asignan a la propiedad resultado.mensaje
            // Los mensajes de validación se encuentran en: grails-app/i18n/messages_es.properties
            planEstudios.errors.allErrors.each {
                resultado.mensaje = messageSource.getMessage(it, null)
            }
            bitacoraService.registrar([clase:"PlanEstudiosService", metodo:"eliminar", nombre:"Eliminación del plan de estudios", descripcion:"Se elimina el plan de estudios", estatus:"ERROR"])
            return resultado
        }
        bitacoraService.registrar([clase:"PlanEstudiosService", metodo:"eliminar", nombre:"Eliminación del plan de estudios", descripcion:"Se elimina el plan de estudios", estatus:"EXITOSO"])

        resultado.estatus = true
        resultado.mensaje = 'Plan de Estudios dado de baja exitosamente'
        resultado.datos = planEstudios

        return resultado
    }

    def cargarPorExcel(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def datosBitacora = [
            clase: "PlanEstudiosService",
            metodo: "registrar",
            nombre: "Registro de planes de estudio",
            descripcion: "Se registran varios planes de estudios",
            estatus: "ERROR"
        ]

        // Se obtienen las relaciones generales
		def institucion = institucionService.obtener(params.institucionId)
		if(!institucion){
			resultado.mensaje = 'Instituciòn no encontrada'
			return resultado
		}

        if(!params.excel){
            resultado.mensaje = 'El excel es un dato requerido'
			return resultado
        }

        def resultadoOperacion = obtenerRegistros(params.excel, institucion)
		if(!resultadoOperacion.estatus){
			resultado.mensaje = resultadoOperacion.mensaje
			return resultado
		}

		def planesEstudio = resultadoOperacion.datos

		for(datosPlan in planesEstudio){
            def planEstudios = new PlanEstudios()
            planEstudios.nombre = datosPlan.nombre
            planEstudios.ciclos = datosPlan.ciclos
            planEstudios.horaInicio = datosPlan.horaInicio
            planEstudios.horaFin = datosPlan.horaFin
            planEstudios.calificacionMinima = datosPlan.calificacionMinima
            planEstudios.calificacionMinimaAprobatoria = datosPlan.calificacionMinimaAprobatoria
            planEstudios.calificacionMaxima = datosPlan.calificacionMaxima
            planEstudios.carrera = datosPlan.carrera
            planEstudios.periodo = datosPlan.periodo
            planEstudios.turno = datosPlan.turno

            if(!planEstudios.save(flush: true)){
				transactionStatus.setRollbackOnly()
				bitacoraService.registrar(datosBitacora)
				resultado.mensaje = "Error al guardar el plan de estudios. Fila ${datosPlan.rowNumber}"
				return resultado
			}
        }

        resultado.estatus = true
        resultado.mensaje = 'Planes registrados exitosamente'
        return resultado
	}

    def obtenerRegistros(excel, institucion){
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

            // Se hace el salto de 8 lineas
			(1..7).each { rowIterator.next() }

			while (rowIterator.hasNext()) {
				def row = rowIterator.next()
                def rowNumber = row.getRowNum() + 1

                // Si la primera columna de la fila esta vacia se toma como el final del documento
                if(!formatter.formatCellValue(row.getCell(0))) break

                def datosPlan = obtenerRegistroDeFila(row)

                // Se obtienen las relaciones
                def carrera = carreraService.obtenerPorRvoeInstitucion(datosPlan.carreraRvoe, institucion)
                if(!carrera){
                    resultado.mensaje = "Carrera no encontrada. Fila ${rowNumber}"
                    return resultado
                }

                def periodo = periodoService.obtenerPorNombre(datosPlan.periodo)
                if(!periodo){
                    resultado.mensaje = "Periodo no encontrado. Fila ${rowNumber}"
                    return resultado
                }

                def turno = turnoService.obtenerPorNombre(datosPlan.turno)
                if(!turno){
                    resultado.mensaje = "Turno no encontrado. Fila ${rowNumber}"
                    return resultado
                }

                // Se validan y formatean los datos recibidos
				def resultadoValidacion = validarDatosRegistro(datosPlan)
                if (!resultadoValidacion.estatus) {
                    resultado.mensaje = "${resultadoValidacion.mensaje}. Fila ${rowNumber}"
                    return resultado
                }

                // Se asignan los datos con formato
		        datosPlan = resultadoValidacion.datos
		        datosPlan.carrera = carrera
		        datosPlan.periodo = periodo
		        datosPlan.turno = turno
                datosPlan.rowNumber = rowNumber

                resultado.datos << datosPlan
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
        DataFormatter formatter = new DataFormatter();

        def planEstudios = [
            carreraRvoe: formatter.formatCellValue(row.getCell(0)),
            nombre: formatter.formatCellValue(row.getCell(1)),
            periodo: formatter.formatCellValue(row.getCell(2)),
            ciclos: formatter.formatCellValue(row.getCell(3)),
            turno: formatter.formatCellValue(row.getCell(4)),
            horaInicio: formatter.formatCellValue(row.getCell(5)),
            horaFin: formatter.formatCellValue(row.getCell(6)),
            calificacionMinima: formatter.formatCellValue(row.getCell(7)),
            calificacionMinimaAprobatoria: formatter.formatCellValue(row.getCell(8)),
            calificacionMaxima: formatter.formatCellValue(row.getCell(9)),
        ]

        return planEstudios
    }

    def obtener(id){
		def planEstudios = PlanEstudios.get(id)

		if(!planEstudios) return null
		if(!planEstudios.activo) return null

		return planEstudios
	}
}
