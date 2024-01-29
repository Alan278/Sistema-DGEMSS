package sieges

import grails.gorm.transactions.Transactional
import org.hibernate.criterion.CriteriaSpecification
import java.text.SimpleDateFormat

import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.DataFormatter;
import sieges.Evaluacion

/**
 * Servicio que permite la administración de evaluaciones
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */
@Transactional
class EvaluacionService {
	/**
      * Inyección de messageSource que contiene mensajes de validaciones para los atributos de cada dominio
      * Los mensajes de validación se encuentran en: grails-app/i18n/messages_es.properties
      */
	def messageSource
	/**
	 * Inyección de BitacoraService que contiene la lógica de administración de la bitacora
	 */
	def bitacoraService
	def estatusRegistroService
	def formatoService
	def alumnoService
	def asignaturaService
	def tipoEvaluacionService
	def usuarioService

	/**
	 * Registra una nueva evaluación
	 * @param id (Requerido)
	 * Identificador del alumno
	 * @param calificacion (Requerido)
	 * Calificación del alumno
	 * @param asignaturaId (Requerido)
	 * Identificador de la asignatura
	 */
	def registrar(params) {
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		def datosBitacora = [
            clase: "EvaluacionService",
            metodo: "registrar",
            nombre: "Registro de evaluación",
            descripcion: "Se registra una nueva evaluación",
            estatus: "ERROR"
        ]

		// Se validan las relaciones con otras tablas
		def alumno = alumnoService.obtener(params.id)
		if(!alumno){
			resultado.mensaje = 'Alumno no encontrado'
			return resultado
		}

		def asignatura = asignaturaService.obtener(params.asignaturaId)
		if(!asignatura){
			resultado.mensaje = 'Asignatura no encontrada'
			return resultado
		}

		def tipoEvaluacion = tipoEvaluacionService.obtener(params.tipoEvaluacionId)
		if(!tipoEvaluacion){
			tipoEvaluacion = tipoEvaluacionService.obtener(tipoEvaluacionService.NORMAL)
		}

		def resultadoValidacion
        // Se validan los privilegios del usuario
		resultadoValidacion = validarPrivilegios(asignatura, alumno)
		if (!resultadoValidacion) {
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
        }
        // Se validan y formatean los datos recibidos
		def formatoFecha = "yyyy-MM-dd"
		resultadoValidacion = validarDatosRegistro(params, asignatura, alumno, tipoEvaluacion, formatoFecha)
		if (!resultadoValidacion.estatus) {
			resultado.mensaje = resultadoValidacion.mensaje
            return resultado
        }

		// Se asignan los datos con formato
		params = resultadoValidacion.datos

		def evaluacion = new Evaluacion()
		evaluacion.calificacion = params.calificacion
		evaluacion.aprobada = params.aprobada
		evaluacion.cicloEscolar = params.cicloEscolar
		evaluacion.fechaAcreditacion = params.fechaAcreditacion
		evaluacion.tipoEvaluacion = tipoEvaluacion
		evaluacion.alumno = alumno
		evaluacion.asignatura = asignatura
		evaluacion.estatusRegistro = EstatusRegistro.get(estatusRegistroService.EDITABLE)

		if(!evaluacion.save(flush: true)){
			transactionStatus.setRollbackOnly()
			bitacoraService.registrar(datosBitacora)
			resultado.mensaje = "Error al guardar la evaluación"
			return resultado
		}

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = "Evaluacion registrada exitosamente"
        resultado.datos = evaluacion

        return resultado
	}

	def validarPrivilegios(asignatura, alumno){

		// Se valida que la asignatura pertenezca a la misma institucion que el usuario
		def institucion = asignatura.planEstudios.carrera.institucion
		if(!usuarioService.perteneceAInstitucion(institucion.id)){
			return false
		}

		// Se valida que el alumno pertenezca a la misma institucion que el usuario
		institucion = alumno.planEstudios.carrera.institucion
		if(!usuarioService.perteneceAInstitucion(institucion.id)){
			return false
		}

		return true
	}

	def validarDatosRegistro(params, asignatura, alumno, tipoEvaluacion, formatoFecha){
		def resultado = [
			estatus: false,
			mensaje: "",
			datos: null
		]

		def formacion = asignatura.formacion
		def planEstudios = asignatura.planEstudios
		def resultadoValidacion

		// Se validan los datos requeridos
		resultadoValidacion = validarDatosRequeridosRegistro(params, tipoEvaluacion)
		if(!resultadoValidacion.estatus){
			resultado.mensaje = resultadoValidacion.mensaje
			return resultado
		}

		// Se valida el formato de los datos
		resultadoValidacion = validarFormatoDatosRegistro(params, formacion, tipoEvaluacion, formatoFecha)
		if(!resultadoValidacion.estatus){
			resultado.mensaje = resultadoValidacion.mensaje
			return resultado
		}

		// Se le da el formato requerido a los datos
		params = formatearDatosRegistro(params, formacion, tipoEvaluacion, formatoFecha)

		resultadoValidacion = validarReglasNegocioRegistro(params, asignatura, alumno)
		if(!resultadoValidacion.estatus){
			resultado.mensaje = resultadoValidacion.mensaje
			return resultado
		}

		resultado.datos = params
		resultado.estatus = true
		return resultado
	}

	def validarDatosRequeridosRegistro(params, tipoEvaluacion){
		def resultado = [
			estatus: false,
			mensaje: ""
		]

		if(!params.calificacion){
			resultado.mensaje = "La calificación es un dato requerido"
            return resultado
		}

		if(tipoEvaluacion.id != 1 && !params.fechaAcreditacion){
			resultado.mensaje = "La fecha de acreditación es un dato requerido"
            return resultado
		}

		resultado.estatus = true
		return resultado
	}

	def validarFormatoDatosRegistro(params, formacion, tipoEvaluacion, formatoFecha){
		def resultado = [
			estatus: false,
			mensaje: ""
		]

		if(!formacion.requerida){
			if(!params.calificacion.equals("A") && !params.calificacion.equals("NA")){
				resultado.mensaje = "La calificación no cuenta con un formato válido"
				return resultado
			}
		}else{
			if(!formatoService.isPositiveNumeric(params.calificacion)){
				resultado.mensaje = "La calificación no cuenta con un formato válido"
				return resultado
			}
		}

		if(tipoEvaluacion.id != 1 && !formatoService.isDate(params.fechaAcreditacion, formatoFecha)){
			resultado.mensaje = "La fecha de acreditación no cuenta con un formato válido"
            return resultado
		}

		resultado.estatus = true
		return resultado
	}

	def validarReglasNegocioRegistro(params, asignatura, alumno){
        def resultado = [
			estatus: false,
			mensaje: "",
			datos: null
		]

		def formacion = asignatura.formacion
		def planEstudios = asignatura.planEstudios

		// Se valida que la calificación este dentro del rango establecido en el plan ed estudios
		if(formacion.requerida){
			if(params.calificacion < planEstudios.calificacionMinima){
				resultado.mensaje = "La calificación debe de ser mayor a ${planEstudios.calificacionMinima}"
				return resultado
			}

			if(params.calificacion > planEstudios.calificacionMaxima){
				resultado.mensaje = "La calificación debe de ser menor a ${planEstudios.calificacionMaxima}"
				return resultado
			}
		}

		params.cicloEscolar = null
		alumno.ciclosEscolares.each{ registro ->
			if(registro.activo && registro.cicloEscolar.periodo == asignatura.periodo){
				params.cicloEscolar = registro.cicloEscolar
			}
		}
		if(!params.cicloEscolar){
			resultado.mensaje = 'El alumno no esta incrito en el ciclo correspondiente'
			return resultado
		}

		def evaluacion = Evaluacion.findByAlumnoAndAsignaturaAndActivo(alumno, asignatura, true)
		if(evaluacion){
			resultado.mensaje = 'El alumno ya cuenta con una evaluación para esta asignatura'
			return resultado
		}

		resultado.estatus = true
		return resultado
    }

	def formatearDatosRegistro(params, formacion, tipoEvaluacion, formatoFecha){
		if(!formacion.requerida){
			if(params.calificacion.equals("A")){
				params.aprobada = true
			}else{
				params.aprobada = false
			}
			params.calificacion = null
		}else{
			params.calificacion = params.calificacion.trim().toFloat()
			params.aprobada = null
		}

		if(tipoEvaluacion.id == 1){
			params.fechaAcreditacion = null
		}else{
			params.fechaAcreditacion = new SimpleDateFormat(formatoFecha).parse(params.fechaAcreditacion)
		}

		return params
	}

	/**
	 * Obtiene las evaluaciones activas con parametros de paginación y filtrado
	 * @param id (requerido)
	 * Identificador del alumno
	 * @param search (Opcional)
	 * Nombre de la asignatura
	 */
	def listar(params){
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		// Parametros de paginación
		if(!params.max) params.max = 90
		if(!params.offset) params.offset = 0

		// Parametros de filtrado (opcionales)
		def id
		if(params.id){
		    id = Integer.parseInt(params.id)
		}

		def alumno = Alumno.get(id)
		def tieneCiclos = alumno.ciclosEscolares.any{ registro -> registro.activo }

		def evaluaciones = null
		if(tieneCiclos){
			def criteria = {
				createAlias("asignatura", "as", CriteriaSpecification.LEFT_JOIN)
				createAlias("alumno", "al", CriteriaSpecification.LEFT_JOIN)
				createAlias("cicloEscolar", "c", CriteriaSpecification.LEFT_JOIN)
				and{
					eq("activo", true)
					if(params.id){
						eq("al.id", id)
					}
					if(params.search){
						or{
							ilike("as.nombre", "%${params.search}%")
							ilike("c.periodo", "%${params.search}%")
						}
					}
					or{
                        alumno.ciclosEscolares.each{ registro ->
                            if(registro.activo){
                                eq("c.periodo", registro.cicloEscolar.periodo)
                            }
                        }
                    }
				}
				order("as.orden", "asc")
			}

			evaluaciones = Evaluacion.createCriteria().list(params,criteria)
		}

		resultado.estatus = true
		resultado.mensaje = 'Evaluaciones consultadas exitosamente'
		resultado.datos = evaluaciones

		return resultado
	}

	/**
	 * Obtiene las evaluaciones activas
	 */
	def obtenerActivos(params){
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		def evaluacion = Evaluacion.createCriteria().list {
			eq("activo", true)
		}

		resultado.estatus = true
		resultado.mensaje = 'Evaluaciones consultadas exitosamente'
		resultado.datos = evaluacion

		return resultado
	}

	/**
	 * Obtiene una evaluación específica
	 * @param evaluacionId (Requerido)
	 * Identificador de la evaluación
	 */
	def consultar(params) {
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		if(!params.evaluacionId){
			resultado.mensaje = 'El id es un dato requerido'
			return resultado
		}

		def evaluacion = Evaluacion.get(params.evaluacionId)

		if(!evaluacion){
			resultado.mensaje = 'Evaluacion no encontrada'
			return resultado
		}

		if(!evaluacion.activo){
			resultado.mensaje = 'Evaluacion inactiva'
			return resultado
		}

		resultado.estatus = true
		resultado.mensaje = 'Evaluacion encontrada exitosamente'
		resultado.datos = evaluacion

		return resultado
	}

	/**
	 * Modifica los datos de una evaluación específica
	 * @param evaluacionId (Requerido)
	 * Identificador de la evaluación
	 * @param calificacion (Requerido)
	 * Calificación del alumno
	 * @param asignaturaId (Requerido)
	 * Identificador de la asignatura
	 */
	def modificar(params) {
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		def datosBitacora = [
            clase: "EvaluacionService",
            metodo: "modificar",
            nombre: "Modificación de evaluación",
            descripcion: "Se modifica una evaluación",
            estatus: "ERROR"
        ]

		def evaluacion = obtener(params.evaluacionId)
		if(!evaluacion){
            resultado.mensaje = 'Evaluación no encontrada'
            return resultado
        }

		if(evaluacion.estatusRegistro.id != estatusRegistroService.EDITABLE){
            resultado.mensaje = 'La evaluación no se puede editar'
            return resultado
        }

		def tipoEvaluacion = tipoEvaluacionService.obtener(params.tipoEvaluacionId)
		if(!tipoEvaluacion){
			resultado.mensaje = 'Tipo de evaluación no encontrado'
			return resultado
		}

		def alumno = evaluacion.alumno
		def asignatura = evaluacion.asignatura

		def resultadoValidacion
        // Se validan los privilegios del usuario
		resultadoValidacion = validarPrivilegios(asignatura, alumno)
		if (!resultadoValidacion) {
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
        }
        // Se validan y formatean los datos recibidos
		def formatoFecha = "yyyy-MM-dd"
		resultadoValidacion = validarDatosModificacion(params, asignatura, alumno, tipoEvaluacion, formatoFecha)
		if (!resultadoValidacion.estatus) {
			resultado.mensaje = resultadoValidacion.mensaje
            return resultado
        }

		// Se asignan los datos con formato
		params = resultadoValidacion.datos

		evaluacion.calificacion = params.calificacion
		evaluacion.aprobada = params.aprobada
		evaluacion.tipoEvaluacion = tipoEvaluacion
		evaluacion.fechaAcreditacion = params.fechaAcreditacion

		if(!evaluacion.save(flush: true)){
			transactionStatus.setRollbackOnly()
			bitacoraService.registrar(datosBitacora)
			resultado.mensaje = "Error al guardar la evaluación"
			return resultado
		}

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = "Evaluacion modificada exitosamente"
        resultado.datos = evaluacion

        return resultado
	}

	def validarDatosModificacion(params, asignatura, alumno, tipoEvaluacion, formatoFecha){
		def resultado = [
			estatus: false,
			mensaje: "",
			datos: null
		]

		def formacion = asignatura.formacion
		def planEstudios = asignatura.planEstudios
		def resultadoValidacion

		// Se validan los datos requeridos
		resultadoValidacion = validarDatosRequeridosRegistro(params, tipoEvaluacion)
		if(!resultadoValidacion.estatus){
			resultado.mensaje = resultadoValidacion.mensaje
			return resultado
		}

		// Se valida el formato de los datos
		resultadoValidacion = validarFormatoDatosRegistro(params, formacion, tipoEvaluacion, formatoFecha)
		if(!resultadoValidacion.estatus){
			resultado.mensaje = resultadoValidacion.mensaje
			return resultado
		}

		// Se le da el formato requerido a los datos
		params = formatearDatosRegistro(params, formacion, tipoEvaluacion, formatoFecha)

		resultadoValidacion = validarReglasNegocioModificacion(params, asignatura)
		if(!resultadoValidacion.estatus){
			resultado.mensaje = resultadoValidacion.mensaje
			return resultado
		}

		resultado.datos = params
		resultado.estatus = true
		return resultado
	}

	def validarReglasNegocioModificacion(params, asignatura){
        def resultado = [
			estatus: false,
			mensaje: "",
			datos: null
		]

		def formacion = asignatura.formacion
		def planEstudios = asignatura.planEstudios

		// Se valida que la calificación este dentro del rango establecido en el plan ed estudios
		if(formacion.requerida){
			if(params.calificacion < planEstudios.calificacionMinima){
				resultado.mensaje = "La calificación debe de ser mayor a ${planEstudios.calificacionMinima}"
				return resultado
			}

			if(params.calificacion > planEstudios.calificacionMaxima){
				resultado.mensaje = "La calificación debe de ser menor a ${planEstudios.calificacionMaxima}"
				return resultado
			}
		}

		resultado.estatus = true
		return resultado
    }

	/**
	 * Realiza una baja lógica de una evaluación específica
	 * @param evaluacionId (Requerido)
	 * Identificador de la evaluación
	 */
	def eliminar(params) {
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		if(!params.evaluacionId){
			resultado.mensaje = 'El id es un dato requerido'
			return resultado
		}

		def evaluacion = Evaluacion.get(params.evaluacionId)

		if(!evaluacion){
			resultado.mensaje = 'Evaluación no encontrada'
			return resultado
		}

		if(evaluacion.estatusRegistro.id != estatusRegistroService.EDITABLE){
            resultado.mensaje = 'Evaluación no editable'
            return resultado
        }

		evaluacion.activo = false

		if(!evaluacion.save(flush:true)){
			// Se recorren los errores de validación y se asignan a la propiedad resultado.mensaje
			evaluacion.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar([clase:"EvaluacionService", metodo:"eliminar", nombre:"Eliminación de la evaluación", descripcion:"Se elimina la evaluación", estatus:"ERROR"])
			return resultado
		}
		bitacoraService.registrar([clase:"EvaluacionService", metodo:"eliminar", nombre:"Eliminación de la evaluación", descripcion:"Se elimina la evaluación", estatus:"EXITOSO"])

		resultado.estatus = true
       	resultado.mensaje = 'Evaluacion dada de baja exitosamente'
       	resultado.datos = evaluacion

		return resultado
	}

	/**
     * Calcula el promedio de un conjunto de evaluaciones 
     * @param evaluaciones
     * evaluaciones del alumno
     * @return promedio
     */
    def calcularPromedio(evaluaciones){
        def total = 0
        def promedio = 0
		def numEvaluaciones = 0
        
        if(!evaluaciones) return promedio

        evaluaciones.each{ evaluacion ->
			if(evaluacion.asignatura.formacion.requerida){
				total += evaluacion.calificacion
				numEvaluaciones += 1
			}
        }

		if(numEvaluaciones != 0 && total != 0){
    		promedio = total / numEvaluaciones
		}
        return promedio.floatValue()
    }

	/**
     * Obtiene un listado de evaluaciones correspondientes a la fecha de expedición de un certificado 
     * @param personaId
     * id de la persona
	 * @param fechaExpedicion
     * fecha de expedición del certificado
     * @return promedio
     */
	def consultarPorCertificado(params){
		def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		if(!params.alumnoId){
			resultado.mensaje = 'El id del alumno es un dato requerido'
			return resultado
		}

		if(!params.fechaRegistro){
			resultado.mensaje = 'La fecha de expedición del certificado es un dato requerido'
			return resultado
		}

		def evaluaciones = Evaluacion.createCriteria().list {
            createAlias("alumno", "a", CriteriaSpecification.LEFT_JOIN)
            createAlias("asignatura", "as", CriteriaSpecification.LEFT_JOIN)
            and{
                eq("activo", true)
                eq("a.id", params.alumnoId)
				le("dateCreated", params.fechaRegistro)
            }
			order("as.orden", "asc")
        }

		if(evaluaciones.size() <= 0){
			resultado.mensaje = 'No se encontraron evaluaciones'
			resultado.datos = evaluaciones
			return resultado
		}

		resultado.estatus = true
		resultado.mensaje = 'Evaluaciones consultadas exitosamente'
		resultado.datos = evaluaciones

		return resultado
	}

	/**
     * Obtiene la fecha de acriditación de la ultima asignatura.
     * @param evaluaciones
     * evaluaciones del alumno
     * @return promedio
     */
    def obtenerFechaUltimaAcreditacion(certificado){
		def ultimaEvaluacionPorCiclo = Evaluacion.createCriteria().list {
            createAlias("cicloEscolar", "c", CriteriaSpecification.LEFT_JOIN)
            and{
                eq("activo", true)
                eq("alumno", certificado.alumno)
                le("dateCreated", certificado.fechaRegistro)
            }
            order("c.fechaFin", "desc")
        }.first()

        def ultimaEvaluacionPorFechaAcreditacion = Evaluacion.createCriteria().list {
            and{
                eq("activo", true)
                eq("alumno", certificado.alumno)
                le("dateCreated", certificado.fechaRegistro)
            }
            order("fechaAcreditacion", "desc")
        }.first()

        if(ultimaEvaluacionPorCiclo.cicloEscolar.fechaFin > ultimaEvaluacionPorFechaAcreditacion.fechaAcreditacion){
            return ultimaEvaluacionPorCiclo.cicloEscolar.fechaFin
        }else{
            return ultimaEvaluacionPorFechaAcreditacion.fechaAcreditacion
        }

        return null
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

        def alumno = alumnoService.obtener(params.id)
        if(!alumno){
            resultado.mensaje = 'Alumno no encontrado'
			return resultado
        }

		// Se valida que el alumno pertenezca a la misma institucion que el usuario
		def institucion = alumno.planEstudios.carrera.institucion
		if(!usuarioService.perteneceAInstitucion(institucion.id)){
			return false
		}

        if(!params.excel){
            resultado.mensaje = 'El excel es un dato requerido'
			return resultado
        }

		def resultadoOperacion = obtenerRegistros(params.excel, alumno)
		if(!resultadoOperacion.estatus){
			resultado.mensaje = resultadoOperacion.mensaje
			return resultado
		}

		def evaluaciones = resultadoOperacion.datos
		def estatusRegistro = EstatusRegistro.get(estatusRegistroService.EDITABLE)

		for(datosEvaluacion in evaluaciones){

			def evaluacion = new Evaluacion()
			evaluacion.calificacion = datosEvaluacion.calificacion
			evaluacion.aprobada = datosEvaluacion.aprobada
			evaluacion.asignatura = datosEvaluacion.asignatura
			evaluacion.cicloEscolar = datosEvaluacion.cicloEscolar
			evaluacion.fechaAcreditacion = datosEvaluacion.fechaAcreditacion
			evaluacion.tipoEvaluacion = datosEvaluacion.tipoEvaluacion
			evaluacion.alumno = alumno
			evaluacion.estatusRegistro = estatusRegistro

			if(!evaluacion.save(flush: true)){
				transactionStatus.setRollbackOnly()
				bitacoraService.registrar(datosBitacora)
				resultado.mensaje = "Error al guardar evaluación de fila ${datosCicloEscolar.rowNumber}"
				return resultado
			}

		}

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = "Evaluaciones registradas exitosamente"

		return resultado
	}

	def obtenerRegistros(excel, alumno){
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

               	def datosEvaluacion = obtenerRegistroDeFila(row)

				def asignatura = asignaturaService.obtener(datosEvaluacion.periodo, datosEvaluacion.clave, datosEvaluacion.asignatura, alumno.planEstudios.carrera)
				if(!asignatura){
					resultado.mensaje = "Asignatura no encontrada. Fila ${rowNumber}"
					return resultado
				}

				// Se valida que la asignatura pertenezca a la misma institucion que el usuario
				def institucion = asignatura.planEstudios.carrera.institucion
				if(!usuarioService.perteneceAInstitucion(institucion.id)){
					return false
				}

				def tipoEvaluacion = tipoEvaluacionService.obtenerPorNombre(datosEvaluacion.tipoEvaluacion)
				if(!tipoEvaluacion){
					resultado.mensaje = 'Tipo de evaluación no encontrado'
					return resultado
				}

				def resultadoValidacion
				// Se validan y formatean los datos recibidos
				resultadoValidacion = validarDatosRegistro(datosEvaluacion, asignatura, alumno, tipoEvaluacion, formatoFecha)
				if (!resultadoValidacion.estatus) {
					resultado.mensaje = "${resultadoValidacion.mensaje}. Fila ${rowNumber}"
					return resultado
				}


				// Se asignan los datos con formato
				datosEvaluacion = resultadoValidacion.datos
				datosEvaluacion.asignatura = asignatura
				datosEvaluacion.tipoEvaluacion = tipoEvaluacion
				datosEvaluacion.rowNumber = rowNumber

				resultado.datos << datosEvaluacion

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

        def evaluacion = [
            periodo: formatter.formatCellValue(row.getCell(0)),
            clave: formatter.formatCellValue(row.getCell(1)),
            asignatura: formatter.formatCellValue(row.getCell(2)),
            calificacion: formatter.formatCellValue(row.getCell(3)),
            tipoEvaluacion: formatter.formatCellValue(row.getCell(4)),
            fechaAcreditacion: formatter.formatCellValue(row.getCell(5)),
        ]

        return evaluacion
    }

	def obtener(id){
		def evaluacion = Evaluacion.get(id)

		if(!evaluacion) return null
		if(!evaluacion.activo) return null

		return evaluacion
	}

}
