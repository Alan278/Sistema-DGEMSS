package sieges


import grails.gorm.transactions.Transactional
import org.hibernate.criterion.CriteriaSpecification

import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.DataFormatter;

/**
 * AsignaturaService para la gestión de las asignaturas
 * @authors Dominguez Luis, Navez Leslie
 * @since 2021
 */

@Transactional
class AsignaturaService {
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
    def planEstudiosService
    def formacionService
    def formatoService

    /**
     * Permite realizar el registro de las asignaturas.
     * @param params (requerido)
     * parametros de la asignatura
     * @param planEstudiosId (requerido)
     * Id del plan de estudios
     * @return resultado
     * resultado con el estatus, mensaje y datos de la asignatura
     */
    def registrar(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def datosBitacora = [
            clase: "AsignaturaService",
            metodo: "registrar",
            nombre: "Registro de asignatura",
            descripcion: "Se registra una nueva asignatura",
            estatus: "ERROR"
        ]

        // Se obtienen las relaciones
        def planEstudios = planEstudiosService.obtener(params.planEstudiosId)
        if(!planEstudios){
            resultado.mensaje = 'Plan de Estudios no encontrado'
            return resultado
        }
        def formacion = formacionService.obtener(params.formacionId)
        if(!formacion){
            resultado.mensaje = 'Formación no encontrada'
            return resultado
        }

        def resultadoValidacion
        // Se validan los privilegios del usuario
		resultadoValidacion = validarPrivilegios(planEstudios)
		if (!resultadoValidacion) {
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
        }
        // Se validan y formatean los datos recibidos
		resultadoValidacion = validarDatosRegistro(params, planEstudios, formacion)
		if (!resultadoValidacion.estatus) {
			resultado.mensaje = resultadoValidacion.mensaje
            return resultado
        }

        // Se asignan los datos con formato
		params = resultadoValidacion.datos

        def asignatura = new Asignatura()
        asignatura.nombre = params.nombre
		asignatura.clave = params.clave
		asignatura.creditos = params.creditos
		asignatura.horas = params.horas
		asignatura.periodo = params.periodo
		asignatura.orden = params.orden
        asignatura.formacion = formacion
        asignatura.planEstudios = planEstudios

        if(!asignatura.save(flush: true)){
			transactionStatus.setRollbackOnly()
			bitacoraService.registrar(datosBitacora)
			resultado.mensaje = "Error al guardar la asignatura"
			return resultado
		}

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = "Asignatura creada exitosamente"

        return resultado
    }

    def validarPrivilegios(planEstudios){

        // Se valida el privilegio del usuario sobre instituciones privadas o públicas segun corresponda su rol
        def isPublic = planEstudios.carrera.institucion.publica
		if(!usuarioService.validarPrivilegioSobreTipoInstitucion(isPublic)){
			return false
		}

        // Se valida que el usuario pertenezca al mismo nivel que el plan de estudios
        def nivelId = planEstudios.carrera.nivel.id
        if(!usuarioService.perteneceANivel(nivelId)){
			return false
		}

        return true
    }

    def validarDatosRegistro(params, planEstudios, formacion){
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
		resultadoValidacion = validarReglasNegocioRegistro(params, planEstudios, formacion)
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

        if(!params.orden){
            resultado.mensaje = "El orden es un dato requerido"
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

        if(params.horas && !formatoService.isPositiveInteger(params.horas)){
			resultado.mensaje = "Las horas deben de ser un número entero positivo"
			return resultado
        }

		if(params.creditos && !formatoService.isPositiveInteger(params.creditos)){
			resultado.mensaje = "Los creditos deben de ser un número entero positivo"
			return resultado
        }

		if(!formatoService.isPositiveInteger(params.periodo)){
			resultado.mensaje = "El periodo deben de ser un número entero positivo"
			return resultado
        }

        if(!formatoService.isPositiveInteger(params.orden)){
			resultado.mensaje = "El orden deben de ser un número entero positivo"
			return resultado
        }

		resultado.estatus = true
		return resultado
	}

    def validarReglasNegocioRegistro(params, planEstudios, formacion){
        def resultado = [
			estatus: false,
			mensaje: "",
			datos: null
		]

        def existeAsignatura
        if(formacion.general){
		    existeAsignatura = Asignatura.createCriteria().list{
                and{
                    eq("activo", true)
                    eq("orden", params.orden)
                    eq("planEstudios", planEstudios)
                }
            }
        }else{
            existeAsignatura = Asignatura.createCriteria().list{
                createAlias("formacion", "f", CriteriaSpecification.LEFT_JOIN)
                and{
                    eq("activo", true)
                    eq("orden", params.orden)
                    eq("planEstudios", planEstudios)
                    or{
                        eq("f.general", true)
                        eq("f.id", formacion.id)
                    }
                }
            }
        }

        if(existeAsignatura){
			resultado.mensaje = "Ya existe una asignatura con el mismo orden"
			return resultado
        }

        if(formacion.general){
            existeAsignatura = Asignatura.createCriteria().list{
                and{
                    eq("activo", true)
                    eq("clave", params.clave ?: "")
                    eq("planEstudios", planEstudios)
                }
            }
        }else{
            existeAsignatura = Asignatura.createCriteria().list{
                createAlias("formacion", "f", CriteriaSpecification.LEFT_JOIN)
                and{
                    eq("activo", true)
                    eq("clave", params.clave ?: "")
                    eq("planEstudios", planEstudios)
                    or{
                        eq("f.general", true)
                        eq("f.id", formacion.id)
                    }
                }
            }
        }

        if(existeAsignatura){
			resultado.mensaje = "Ya existe una asignatura con la misma clave"
			return resultado
        }

		resultado.estatus = true
		return resultado
    }

    def formatearDatosRegistro(params){
		params.nombre = formatoService.toFlatString(params.nombre)
		params.clave = formatoService.toFlatString(params.clave)
		params.creditos = params.creditos ? params.creditos.trim().toInteger() : null
		params.horas = params.horas ? params.horas.trim().toInteger() : null
		params.periodo = params.periodo.trim().toInteger()
		params.orden = params.orden.trim().toInteger()

		return params
	}

    /**
     * Permite generar el listado de las asignaturas que se encuentran registradas, además de permitir realizar la 
     * busqueda por nombre y el filtrado a través de la institución, carrera y plan de estudios
     * @param params (requerido)
     * parametros de la asignatura
     * @param institucionId (requerido)
     * id de la institución
     * @param carreraId (requerido)
     * id de la carrera
     * @param planEstudiosId (requerido)
     * id del plan de estudios
     * @return resultado
     * resultado con el estatus, mensaje y datos de la asignatura
     */
    def listar(params){
        def resultado = [
                estatus: false,
                mensaje: '',
                datos: null
        ]

        if(!params.max) params.max = 50
        if(!params.offset) params.offset = 0

        def institucionId

        if(params.institucionId){
            institucionId = Integer.parseInt(params.institucionId)
        }

        def carreraId

        if(params.carreraId){
            carreraId = Integer.parseInt(params.carreraId)
        }

        def planEstudiosId

        if(params.planEstudiosId){
            planEstudiosId = Integer.parseInt(params.planEstudiosId)
        }

        def roles = usuarioService.obtenerRoles()
        params.niveles = usuarioService.obtenerNivelesPorRol()

        def criteria = {
            createAlias("planEstudios.carrera.institucion", "i", CriteriaSpecification.LEFT_JOIN)
            createAlias("planEstudios.carrera", "c", CriteriaSpecification.LEFT_JOIN)
            createAlias("planEstudios", "p", CriteriaSpecification.LEFT_JOIN)
            createAlias("planEstudios.carrera.nivel", "n", CriteriaSpecification.LEFT_JOIN)

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
                if(params.planEstudiosId){
                    eq("p.id", planEstudiosId)
                }
                or{
					params.niveles.each{ registro ->
						eq("n.id", registro)
					}
				}
            }

            order("i.nombre", "asc")
            order("c.nombre", "asc")
            order("p.nombre", "asc")
            order("orden", "asc")
        }

        def asignaturas = Asignatura.createCriteria().list(params,criteria)

        if(asignaturas.totalCount <= 0){
            resultado.mensaje = 'No se encontraron Asignaturas'
            resultado.datos = asignaturas
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'Asignaturas consultadas exitosamente'
        resultado.datos = asignaturas

        return resultado

    }

/**
 * Permite mostrar la lista de las asignaturas que se encuentran activas
 * @param params (requerido)
 * parametros de la asignatura
 * @return resultado
 * resultado con el estatus, mensaje y datos de la asignatura
 */
    def obtenerActivos(params){
        def resultado = [
                estatus: false,
                mensaje: '',
                datos: null
        ]

        def institucionId
        if(params.institucionId){
            institucionId = Integer.parseInt(params.institucionId)
        }

        def carreraId
        if(params.carreraId){
            carreraId = Integer.parseInt(params.carreraId)
        }

        def planEstudiosId
        if(params.planEstudiosId){
            planEstudiosId = Integer.parseInt(params.planEstudiosId)
        }

        def roles = usuarioService.obtenerRoles()
        params.niveles = usuarioService.obtenerNivelesPorRol()

        def asignaturas = Asignatura.createCriteria().list{
            createAlias("planEstudios.carrera.institucion", "i", CriteriaSpecification.LEFT_JOIN)
            createAlias("planEstudios.carrera", "c", CriteriaSpecification.LEFT_JOIN)
            createAlias("planEstudios", "p", CriteriaSpecification.LEFT_JOIN)
            createAlias("planEstudios.carrera.nivel", "n", CriteriaSpecification.LEFT_JOIN)
            and{
                eq("activo", true)
                eq("i.externa", false)
                if(usuarioService.esSupervisorPublico()){
 					eq("i.publica", true)
				}else{
					eq("i.publica", false)
				}
                if(params.institucionId){
                    eq("i.id", institucionId)
                }
                if(params.carreraId){
                    eq("c.id", carreraId)
                }
                if(params.planEstudiosId){
                    eq("p.id", planEstudiosId)
                }
                or{
					params.niveles.each{ registro ->
						eq("n.id", registro)
					}
				}
            }
        }


        resultado.estatus = true
        resultado.mensaje = 'Asignaturas consultadas exitosamente'
        resultado.datos = asignaturas

        return resultado
    }

    def obtenerPorCiclosDelAlumno(alumno){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def tieneCiclos = alumno.ciclosEscolares.any{ registro -> registro.activo }

        def asignaturas = null
        if(tieneCiclos){
            asignaturas = Asignatura.createCriteria().list{
                createAlias("planEstudios.carrera.institucion", "i", CriteriaSpecification.LEFT_JOIN)
                createAlias("planEstudios.carrera", "c", CriteriaSpecification.LEFT_JOIN)
                createAlias("planEstudios", "p", CriteriaSpecification.LEFT_JOIN)
                createAlias("planEstudios.carrera.nivel", "n", CriteriaSpecification.LEFT_JOIN)
                createAlias("formacion", "f", CriteriaSpecification.LEFT_JOIN)
                and{
                    eq("activo", true)
                    eq("i.externa", false)
                    eq("p.id", alumno.planEstudios.id)
                    if(alumno.formacion){
                        or{
                            eq("f.general", true)
                            eq("formacion", alumno.formacion)
                        }
                    }
                    or{
                        alumno.ciclosEscolares.each{ registro ->
                            if(registro.activo){
                                eq("periodo", registro.cicloEscolar.periodo)
                            }
                        }
                    }
                    alumno.evaluaciones.each{ evaluacion ->
                        if(evaluacion.activo){
                            ne("id", evaluacion.asignatura.id)
                        }
                    }
                }
                order("orden", "asc")
            }
        }


        resultado.estatus = true
        resultado.mensaje = 'Asignaturas consultadas exitosamente'
        resultado.datos = asignaturas

        return resultado
    }

    /**
     * Permite mostrar la lista de las asignaturas que se encuentran activas y aun no cuentan 
     * con una calificación asignada
     * @param params (requerido)
     * parametros de la asignatura
     * @return resultado
     * resultado con el estatus, mensaje y datos de la asignatura
     */
    def obtenerPorAlumno(params){
        def resultado = [
                estatus: false,
                mensaje: '',
                datos: null
        ]

        def institucionId
        if(params.institucionId){
            institucionId = Integer.parseInt(params.institucionId)
        }

        def carreraId
        if(params.carreraId){
            carreraId = Integer.parseInt(params.carreraId)
        }

        def planEstudiosId
        if(params.planEstudiosId){
            planEstudiosId = Integer.parseInt(params.planEstudiosId)
        }

        def asignaturas = Asignatura.createCriteria().list{
            createAlias("planEstudios.carrera.institucion", "i", CriteriaSpecification.LEFT_JOIN)
            createAlias("planEstudios.carrera", "c", CriteriaSpecification.LEFT_JOIN)
            createAlias("planEstudios", "p", CriteriaSpecification.LEFT_JOIN)
            and{
                eq("activo", true)
                eq("i.externa", false)
                if(params.institucionId){
                    eq("i.id", institucionId)
                }
                if(params.carreraId){
                    eq("c.id", carreraId)
                }
                if(params.planEstudiosId){
                    eq("p.id", planEstudiosId)
                }
            }
            order("orden", "asc")
        }

        def alumno = Alumno.get(params.id)

        def asignaturasAux = []
        asignaturas.each{ asignatura ->
            def x = false
            alumno.evaluaciones.each{ evaluacion ->
                if(asignatura.id == evaluacion.asignatura.id && evaluacion.activo == true && evaluacion.id != params.evaluacionId){
                    x = true
                }
            }
            if(!x){
                def asi = [
                    id: asignatura.id,
                    nombre: asignatura.nombre
                ]

                asignaturasAux << asi
            }
        }

        resultado.estatus = true
        resultado.mensaje = 'Asignaturas consultadas exitosamente'
        resultado.datos = asignaturasAux

        return resultado
    }


/**
 * Permite realizar la consulta del registro seleccionado con su información correspondiente.
 * @param params (requerido)
 * parametros de la asignatura
 * @return resultado
 * resultado con el estatus, mensaje y datos de la asignatura
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

        def asignatura = Asignatura.get(params.id)

        if(!asignatura){
            resultado.mensaje = 'Asignatura no encontrada'
            return resultado
        }

        if(!asignatura.activo){
            resultado.mensaje = 'Asignatura inactiva'
            return resultado
        }
        if(asignatura.planEstudios.carrera.institucion.externa){
            resultado.mensaje = 'La asignatura pertenece a una institución externa'
            return resultado
        }
        def roles = usuarioService.obtenerRoles()
		if(usuarioService.esSupervisorPublico()){
			if(!asignatura.planEstudios.carrera.institucion.publica){
                resultado.mensaje = 'La asignatura pertenece a una institución privada'
                return resultado
            }
		}else if(asignatura.planEstudios.carrera.institucion.publica){
			resultado.mensaje = 'La asignatura pertenece a una institución pública'
			return resultado
		}

        if(!usuarioService.perteneceANivel(asignatura.planEstudios.carrera.nivel.id)){
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
			return resultado
		}

        resultado.estatus = true
        resultado.mensaje = 'Asignatura encontrada exitosamente'
        resultado.datos = asignatura

        return resultado
    }
/**
 * Permite realizar la modificación de la asignatura seleccionada
 * @param params
 * parametros de la asignatura
 * @param planEstudiosId
 * id del plan de estudios
 * @return resultado
 * resultado con el estatus, mensaje y datos de la asignatura
 */
    def modificar(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def datosBitacora = [
            clase: "AsignaturaService",
            metodo: "modificar",
            nombre: "Modificación de asignatura",
            descripcion: "Se modifica una asignatura",
            estatus: "ERROR"
        ]

        def asignatura = obtener(params.id)
        if(!asignatura){
            resultado.mensaje = 'Asignatura no encontrada'
            return resultado
        }
        if(asignatura.planEstudios.numCertificados){
            resultado.mensaje = 'La asignatura no puede ser modificada ya que cuenta con certificados expedidos'
			return resultado
        }
            // Se valida que que el usuario pueda editar la asignatura
		if (!validarPrivilegios(asignatura.planEstudios)) {
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
        }

        // Se obtienen las relaciones
        def planEstudios = planEstudiosService.obtener(params.planEstudiosId)
        if(!planEstudios){
            resultado.mensaje = 'Plan de Estudios no encontrado'
            return resultado
        }
        def formacion = formacionService.obtener(params.formacionId)
        if(!formacion){
            resultado.mensaje = 'Formación no encontrada'
            return resultado
        }

        def resultadoValidacion
        // Se validan los privilegios del usuario
		resultadoValidacion = validarPrivilegios(planEstudios)
		if (!resultadoValidacion) {
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
            return resultado
        }
        // Se validan y formatean los datos recibidos
		resultadoValidacion = validarDatosModificacion(params, planEstudios, formacion, asignatura)
		if (!resultadoValidacion.estatus) {
			resultado.mensaje = resultadoValidacion.mensaje
            return resultado
        }

        // Se asignan los datos con formato
		params = resultadoValidacion.datos

        //Se crea una copia auxiliar del objeto para verificar si existe alguna modificación
        def asignaturaAux = new Asignatura(asignatura.properties)
        asignaturaAux.planEstudios = asignatura.planEstudios

        asignatura.nombre = params.nombre
		asignatura.clave = params.clave
		asignatura.creditos = params.creditos
		asignatura.horas = params.horas
		asignatura.periodo = params.periodo
		asignatura.orden = params.orden
        asignatura.formacion = formacion
        asignatura.planEstudios = planEstudios

        //En caso de cambios se modifica el campo de ultima actualización con la fecha actual en la que se realizo el cambio.
        if(!asignatura.equals(asignaturaAux))
            asignatura.ultimaActualizacion = new Date()

        if(!asignatura.save(flush: true)){
			transactionStatus.setRollbackOnly()
			bitacoraService.registrar(datosBitacora)
			resultado.mensaje = "Error al modificar la asignatura"
			return resultado
		}

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = "Asignatura modificada exitosamente"

        return resultado
    }

    def validarDatosModificacion(params, planEstudios, formacion, asignatura){
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
		resultadoValidacion = validarReglasNegocioModificacion(params, planEstudios, formacion, asignatura)
		if(!resultadoValidacion.estatus){
			resultado.mensaje = resultadoValidacion.mensaje
			return resultado
		}

		resultado.datos = params
		resultado.estatus = true
		return resultado
	}

    def validarReglasNegocioModificacion(params, planEstudios, formacion, asignatura){
        def resultado = [
			estatus: false,
			mensaje: "",
			datos: null
		]

        def existeAsignatura
        if(formacion.general){
		    existeAsignatura = Asignatura.createCriteria().list{
                and{
                    ne("id", asignatura.id)
                    eq("activo", true)
                    eq("orden", params.orden)
                    eq("planEstudios", planEstudios)
                }
            }
        }else{
            existeAsignatura = Asignatura.createCriteria().list{
                createAlias("formacion", "f", CriteriaSpecification.LEFT_JOIN)
                and{
                    ne("id", asignatura.id)
                    eq("activo", true)
                    eq("orden", params.orden)
                    eq("planEstudios", planEstudios)
                    or{
                        eq("f.general", true)
                        eq("f.id", formacion.id)
                    }
                }
            }
        }

        if(existeAsignatura){
			resultado.mensaje = "Ya existe una asignatura con el mismo orden"
			return resultado
        }

        if(formacion.general){
            existeAsignatura = Asignatura.createCriteria().list{
                and{
                    ne("id", asignatura.id)
                    eq("activo", true)
                    eq("clave", params.clave ?: "")
                    eq("planEstudios", planEstudios)
                }
            }
        }else{
            existeAsignatura = Asignatura.createCriteria().list{
                createAlias("formacion", "f", CriteriaSpecification.LEFT_JOIN)
                and{
                    ne("id", asignatura.id)
                    eq("activo", true)
                    eq("clave", params.clave ?: "")
                    eq("planEstudios", planEstudios)
                    or{
                        eq("f.general", true)
                        eq("f.id", formacion.id)
                    }
                }
            }
        }

        if(existeAsignatura){
			resultado.mensaje = "Ya existe una asignatura con la misma clave"
			return resultado
        }

		resultado.estatus = true
		return resultado
    }

/**
 * Permite eliminar la asignatura seleccionada
 * @param params (requerido)
 * parametros de la asignatura
 * @return resultado
 * resultado con el estatus, mensaje y datos de la asignatura
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

        def asignatura = Asignatura.get(params.id)

        if(!asignatura){
            resultado.mensaje = 'Asignatura no encontrada'
            return resultado
        }

        if(asignatura.planEstudios.carrera.institucion.externa){
            resultado.mensaje = 'La asignatura pertenece a una institución externa'
            return resultado
        }

        if(!usuarioService.perteneceANivel(asignatura.planEstudios.carrera.nivel.id)){
			resultado.mensaje = 'No esta autorizado para realizar esta acción'
			return resultado
		}

        def roles = usuarioService.obtenerRoles()
		if(usuarioService.esSupervisorPublico()){
			if(!asignatura.planEstudios.carrera.institucion.publica){
                resultado.mensaje = 'La asignatura pertenece a una institución privada'
                return resultado
            }
		}else if(asignatura.planEstudios.carrera.institucion.publica){
			resultado.mensaje = 'La asignatura pertenece a una institución pública'
			return resultado
		}

        asignatura.activo = false

        if(!asignatura.save(flush:true)){
            asignatura.errors.allErrors.each {
                resultado.mensaje = messageSource.getMessage(it, null)
            }
            bitacoraService.registrar([clase:"AsignaturaService", metodo:"eliminar", nombre:"Eliminación de la asignatura", descripcion:"Se elimina la asignatura", estatus:"ERROR"])
            return resultado
        }
        bitacoraService.registrar([clase:"AsignaturaService", metodo:"eliminar", nombre:"Eliminación de la asignatura", descripcion:"Se elimina la asignatura", estatus:"EXITOSO"])

        resultado.estatus = true
        resultado.mensaje = 'Asignatura dada de baja exitosamente'
        resultado.datos = asignatura

        return resultado
    }

    def cargarPorExcel(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def datosBitacora = [
            clase: "AsignaturaService",
            metodo: "cargarPorExcel",
            nombre: "Registro de asignaturas",
            descripcion: "Se registra varias asignaturas",
            estatus: "ERROR"
        ]

        // Se obtienen las relaciones
        def planEstudios = planEstudiosService.obtener(params.planEstudiosId)
        if(!planEstudios){
            resultado.mensaje = 'Plan de Estudios no encontrado'
            return resultado
        }

        def resultadoValidacion
        // Se validan los privilegios del usuario
		resultadoValidacion = validarPrivilegios(planEstudios)
		if (!resultadoValidacion) {
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

		def asignaturas = resultadoOperacion.datos

        for(datosAsignatura in asignaturas){
            def asignatura = new Asignatura()
            asignatura.nombre = datosAsignatura.nombre
            asignatura.clave = datosAsignatura.clave
            asignatura.creditos = datosAsignatura.creditos
            asignatura.horas = datosAsignatura.horas
            asignatura.periodo = datosAsignatura.periodo
            asignatura.orden = datosAsignatura.orden
            asignatura.formacion = datosAsignatura.formacion
            asignatura.planEstudios = planEstudios

            if(!asignatura.save(flush: true)){
				transactionStatus.setRollbackOnly()
				bitacoraService.registrar(datosBitacora)
				resultado.mensaje = "Error al guardar la asignatura. Fila ${datosAsignatura.rowNumber}"
				return resultado
			}
        }

        resultado.estatus = true
        resultado.mensaje = 'Asignaturas registradas exitosamente'
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

			DataFormatter formatter = new DataFormatter();

            // Se hace el salto de 8 lineas
			(1..7).each { rowIterator.next() }

			while (rowIterator.hasNext()) {
				def row = rowIterator.next()
                def rowNumber = row.getRowNum() + 1

                // Si la primera columna de la fila esta vacia se toma como el final del documento
                if(!formatter.formatCellValue(row.getCell(0))) break

                def datosAsignatura = obtenerRegistroDeFila(row)

                // Se obtienen las relaciones
                def formacion = formacionService.obtenerPorNombreYPlanEstudios(datosAsignatura.formacionId, planEstudios)
                if(!formacion){
                    resultado.mensaje = "Formación no encontrada. Fila ${rowNumber}"
                    return resultado
                }

                // Se validan y formatean los datos recibidos
				def resultadoValidacion = validarDatosRegistro(datosAsignatura, planEstudios, formacion)
                if (!resultadoValidacion.estatus) {
                    resultado.mensaje = "${resultadoValidacion.mensaje}. Fila ${rowNumber}"
                    return resultado
                }

                // Se asignan los datos con formato
		        datosAsignatura = resultadoValidacion.datos
		        datosAsignatura.formacion = formacion
                datosAsignatura.rowNumber = rowNumber

                resultado.datos << datosAsignatura
			}

			file.close()

		} catch (Exception ex) {
			resultado.mensaje = "Ocurrio un error ${ex}"
            resultado.datos = null
			return resultado
		}

        // Se validan las reglas de negocio
		def resultadoValidacionExcel = validarReglasNegocioExcel(resultado.datos)
		if (!resultadoValidacionExcel.estatus) {
			resultado.mensaje = resultadoValidacionExcel.mensaje
            return resultado
        }

		resultado.estatus = true
		return resultado
	}

    def obtenerRegistroDeFila(row){
        DataFormatter formatter = new DataFormatter();

        def asignatura = [
            nombre: formatter.formatCellValue(row.getCell(0)),
            clave: formatter.formatCellValue(row.getCell(1)),
            creditos: formatter.formatCellValue(row.getCell(2)),
            horas: formatter.formatCellValue(row.getCell(3)),
            periodo: formatter.formatCellValue(row.getCell(4)),
            orden: formatter.formatCellValue(row.getCell(5)),
            formacionId: formatter.formatCellValue(row.getCell(6)),
        ]

        return asignatura
    }

    def validarReglasNegocioExcel(asignaturas){
        def resultado = [
			estatus: false,
			mensaje: '',
		]

        def ordenArray = [
            general: [],
        ]
        def claveArray = [
            general: [],
        ]

        for(asignatura in asignaturas){
            def orden = asignatura.orden
            def clave = asignatura.clave
            def formacionNombre = asignatura.formacion.nombre

            if(!asignatura.formacion.general){
                if(!ordenArray.containsKey(asignatura.formacion.nombre)) {
                    ordenArray = ordenArray + [(formacionNombre): []]
                }

                if(!claveArray.containsKey(asignatura.formacion.nombre)) {
                    claveArray = claveArray + [(formacionNombre): []]
                }
            }

            println(ordenArray)
            println(claveArray)

            if(asignatura.formacion.general){
                for(item in ordenArray){
                    if(item.value.contains(orden)){
                        resultado.mensaje = "El orden se encuentra repetido. Fila ${asignatura.rowNumber}"
                        return resultado
                    }
                }

                for(item in claveArray){
                    if(clave && item.value.contains(clave)){
                        resultado.mensaje = "La clave se encuentra repetida. Fila ${asignatura.rowNumber}"
                        return resultado
                    }
                }
            }else{

                if(ordenArray.general.contains(orden) || ordenArray[(formacionNombre)] && ordenArray[(formacionNombre)].contains(orden)){
                    resultado.mensaje = "El orden se encuentra repetido. Fila ${asignatura.rowNumber}"
                    return resultado
                }


                if(claveArray.general.contains(clave) || claveArray[(formacionNombre)] && claveArray[(formacionNombre)].contains(clave)){
                    resultado.mensaje = "La clave se encuentra repetida. Fila ${asignatura.rowNumber}"
                    return resultado
                }
            }

            if(asignatura.formacion.general){
                ordenArray.general << orden
                claveArray.general << clave
            }else{
                ordenArray[(formacionNombre)] << orden
                claveArray[(formacionNombre)] << clave
            }
        }

        resultado.estatus = true
        return resultado
    }

    def obtener(id){
		def asignatura = Asignatura.get(id)

		if(!asignatura) return null
		if(!asignatura.activo) return null

		return asignatura
	}

    def obtener(periodo, clave, nombre, carrera){
		def asignatura = Asignatura.createCriteria().get{
			and{
				eq("activo", true)
				eq("periodo", periodo.trim())
                clave ? eq("clave", clave) : isNull("clave")
				eq("nombre", nombre.trim())
				planEstudios { carrera
                    eq("id", carrera.id)
                }
			}
		}

		if(!asignatura) return null
		if(!asignatura.activo) return null

		return asignatura
	}
}
