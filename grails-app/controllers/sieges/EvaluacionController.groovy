package sieges

/**
 * Controlador que permite el enlace entre el modelo y las vistas de evaluaciones
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */
class EvaluacionController {
    /**
     * Inyección de InstitucionService que contiene la lógica de administración de instituciones
     */
    def institucionService
    /**
     * Inyección de CarreraService que contiene la lógica de administración de carreras
     */
    def carreraService
    /**
     * Inyección de CicloEscolarService que contiene la lógica de administración de ciclos escolares
     */
    def cicloEscolarService
    /**
     * Inyección de AlumnoService que contiene la lógica de administración de alumnos
     */
    def alumnoService
    /**
     * Inyección de AsignaturaService que contiene la lógica de administración de asignaturas
     */
    def asignaturaService
    /**
     * Inyección de EvaluacionService que contiene la lógica de administración de evaluaciones
     */
    def evaluacionService
    def tipoEvaluacionService

    /**
     * Obtiene los catálogos necesarios para la vista 'registro'
     */
    def registro(){
        def alumno = alumnoService.consultar(params).datos
        def tiposEvaluacion = tipoEvaluacionService.select(alumno)
        def asignaturas = asignaturaService.obtenerPorCiclosDelAlumno(alumno)
        def asignatura = asignaturaService.obtener(params.asignaturaId)

        if(!asignatura){
            asignatura = asignaturas[0]
        }

        [
            alumno: alumno,
            tiposEvaluacion: tiposEvaluacion,
            asignaturas: asignaturas.datos,
            asignatura: asignatura
        ]
    }

    /**
     * Registra una nueva evaluación
     * @param id (Requerido)
     * Identificador del alumno
     * @param calificacion (Requerido)
     * Calificación del alumno
     * @param asignaturaId (Requerido)
     * Identificador de la asignatura
     */
    def registrar(){
        def resultado = evaluacionService.registrar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            redirect(action: "listar", params: params)
        }else{
            def alumno = alumnoService.consultar(params).datos
            redirect(action: "registro", params: params)
        }
        return
    }

    /**
     * Obtiene las evaluaciones activas para mostrarlas en la vista 'listar'
     * @param id (requerido)
     * Identificador del alumno
     * @param search (Opcional)
     * Nombre de la asignatura
     */
    def listar(){
        def alumno = alumnoService.consultar(params).datos
        def evaluaciones = evaluacionService.listar(params).datos
        def ciclosEscolares = cicloEscolarService.obtenerActivos(params).datos

        def promedio = evaluacionService.calcularPromedio(evaluaciones)

        [
            evaluaciones: evaluaciones,
            ciclosEscolares: ciclosEscolares,
            conteo: evaluaciones ? evaluaciones.totalCount : 0,
            promedio: promedio,
            alumno: alumno
        ]
    }

    /**
     * Obtiene una evaluación para mostrarla en la vista 'consultar'
     * @param evaluacionId (Requerido)
     * Identificador de la evaluación
     */
    def consultar(){
        def resultado = evaluacionService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect(action: "listar", params: params)
            return
        }

        [evaluacionAlumno: resultado.datos]
    }

    /**
     * Obtiene los datos necesarios para la vista 'modificacion'
     * @param id (Requerido)
     * Identificador del alumno
     * @param evaluacionId (Requerido)
     * Identificador de la evaluación
     * @param carreraId (Requerido)
     * Identificador de la carrera
     */
    def modificacion(){
        def alumno = alumnoService.consultar(params).datos
        params.carreraId = alumno.planEstudios.carrera.id
        def evaluacion = evaluacionService.consultar(params).datos
        def tiposEvaluacion = tipoEvaluacionService.select(alumno)

        [
            alumno: alumno,
            tiposEvaluacion: tiposEvaluacion,
            evaluacion: evaluacion
        ]
    }

    /**
     * Modifica los datos de una evaluación
     * @param evaluacionId (Requerido)
     * Identificador de la evaluación
     * @param calificacion (Requerido)
     * Calificación del alumno
     * @param asignaturaId (Requerido)
     * Identificador de la asignatura
     */
    def modificar(){
        def resultado = evaluacionService.modificar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            redirect(action: "listar", params: params)
            return
        }
        def alumno = alumnoService.consultar(params).datos
        redirect(action: "modificacion" , params:params)
    }

    /**
     * Realiza una baja lógica de una evaluación
     * @param evaluacionId (Requerido)
     * Identificador de la evaluación
     */
    def eliminar(){
        def evaluacion = evaluacionService.consultar(params).datos
        def alumnoId = evaluacion.alumno.id
        def resultado = evaluacionService.eliminar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje
        params.id = alumnoId

        redirect(action: "listar", params: params)

        return
    }

    def subirExcel(){
        def alumno = Alumno.get(params.id)

        [alumno: alumno]
    }

    def cargarPorExcel(){
        def resultado = evaluacionService.cargarPorExcel(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            redirect (action: "listar", params:[id:params.id])
            return
        }

        redirect(action: "subirExcel", params:[id:params.id])
    }

    def descargarPlantilla(){
        def plantilla = grailsApplication.mainContext.getResource("plantillasExcel/evaluaciones_plantilla.xlsx").file.getAbsoluteFile()
        response.setContentType("application/octet-stream")
        response.setHeader("Content-disposition", "attachment; filename=evaluaciones_plantilla.xlsx")
        response.outputStream << plantilla.getBytes()
        return
    }
}
