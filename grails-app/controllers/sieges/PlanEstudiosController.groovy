package sieges

/**
 * Controlador que permite el enlace entre el modelo y las vistas de planes de estudio
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */
class PlanEstudiosController {
    /**
     * Inyección de InstitucionService que contiene la lógica de administración de instituciones
     */
    def institucionService
    /**
     * Inyección de PlanEstudiosService que contiene la lógica de administración de planes de estudio
     */
    def planEstudiosService
    /**
     * Inyección de CarreraService que contiene la lógica de administración de carreras
     */
    def carreraService
    /**
     * Inyección de PeriodoService que contiene la lógica de administración de periodos
     */
    def periodoService
    /**
     * Inyección de TurnoService que contiene la lógica de administración de turnos
     */
    def turnoService

    /**
     * Obtiene los catálogos necesarios para la vista 'registro'
     */
    def registro(){
        def instituciones = institucionService.obtenerActivos(params)
        def carreras = carreraService.obtenerActivos(params)
        def periodos = periodoService.obtenerActivos(params)
        def turnos = turnoService.obtenerActivos(params)

        [
            instituciones: instituciones.datos,
            carreras: carreras.datos,
            periodos: periodos.datos,
            turnos: turnos.datos,
            planEstudios: params
        ]
    }

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
    def registrar(){
        def resultado = planEstudiosService.registrar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            redirect action: "listar"
        }else{
            redirect(action: "registro", params: params)
        }
        return
    }

    /**
     * Obtiene los planes activos para mostrarlos en la vista 'listar'
     * @param institucionId (Opcional)
     * Identificador de la institución
     * @param carreraId (Opcional)
     * Identificador de la carrera
     * @param search (Opcional)
     * Nombre del personal
     */
    def listar(){
        def resultado = planEstudiosService.listar(params)
        def instituciones = institucionService.obtenerActivos(params)
        def carreras = carreraService.obtenerActivos(params)

        [
            instituciones: instituciones.datos,
            carreras: carreras.datos,
            planesEstudios: resultado.datos.planesEstudios,
            conteo: resultado.datos.planesEstudios.totalCount,
            parametros: params
        ]
    }

    /**
     * Obtiene un plan para mostrarlo en la vista 'consultar'
     * @param id (Requerido)
     * Identificador del plan
     */
    def consultar(){
        def resultado = planEstudiosService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect action: "listar"
            return
        }

        [planEstudios: resultado.datos]
    }

    /**
     * Obtiene los datos necesarios para la vista 'modificacion'
     * @param id (Requerido)
     * Identificador del plan
     */
    def modificacion(){
        def resultado = planEstudiosService.consultar(params)
        def planEstudios = resultado.datos
        def instituciones = institucionService.obtenerActivos(params).datos
        def carreras = carreraService.obtenerActivos(params).datos
        def periodos = periodoService.obtenerActivos(params).datos
        def turnos = turnoService.obtenerActivos(params).datos
        def carrera

        // Si se llama a este método desde el método 'modificar' por algun error
        // en los datos, se le asigna la información previamente modificada al objeto
        // para que el usuario no tenga que realizar todas las modificaciones nuevamente
        // y solo modifique el dato erroneo
        if(params.nombre){
            planEstudios.properties = params
            try {
                planEstudios.calificacionMinima = Float.parseFloat(params.calificacionMinima)
            }catch(NumberFormatException e) {}
            try {
                planEstudios.calificacionMinimaAprobatoria = Float.parseFloat(params.calificacionMinimaAprobatoria)
            }catch(NumberFormatException e) {}
            try {
                planEstudios.calificacionMaxima = Float.parseFloat(params.calificacionMaxima)
            }catch(NumberFormatException e) {}
            if(params.carreraId){
                carrera = Carrera.get(params.carreraId)
            }else{
                carrera = new Carrera()
                carrera.institucion = Institucion.get(params.institucionId)
            }
            planEstudios.carrera = carrera
            planEstudios.periodo = Periodo.get(params.periodoId)
            planEstudios.turno = Turno.get(params.turnoId)
        }

        if(!resultado.estatus){
            redirect action: "listar"
            return
        }

        [
            instituciones: instituciones,
            carreras: carreras,
            periodos: periodos,
            turnos: turnos,
            planEstudios: planEstudios
        ]
    }

    /**
     * Modifica los datos de un plan
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
    def modificar(){
        def resultado = planEstudiosService.modificar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            redirect action: "listar"
            return
        }
        redirect(action: "modificacion" , params:params)
    }

    /**
     * Realiza una baja lógica de un plan
     * @param id (Requerido)
     * Identificador del plan
     */
    def eliminar(){
        def resultado = planEstudiosService.eliminar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect action: "listar"

        return
    }

    def subirExcel(){
        def instituciones = institucionService.obtenerActivos().datos
        def carreras = carreraService.obtenerActivos(params).datos

        [
            instituciones: instituciones,
            carreras: carreras
        ]
    }

    def cargarPorExcel(){
        def resultado = planEstudiosService.cargarPorExcel(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            redirect action: "listar"
            return
        }

        redirect(action: "subirExcel" , params:params)
    }

    def descargarPlantilla(){
        def plantilla = grailsApplication.mainContext.getResource("plantillasExcel/planes_estudio_plantilla.xlsx").file.getAbsoluteFile()
        response.setContentType("application/octet-stream")
        response.setHeader("Content-disposition", "attachment; filename=planes_estudio_plantilla.xlsx")
        response.outputStream << plantilla.getBytes()
        return
    }
}