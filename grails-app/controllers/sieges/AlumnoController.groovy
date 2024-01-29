package sieges

class AlumnoController {
    /**
     * Inyección del alumnoService que contiene métodos para la gestión de alumnos
     */
    def alumnoService
    /**
     * Inyección del institucionService que contiene métodos para la gestión de instituciones
     */
    def institucionService
    /**
     * Inyección del carreraService que contiene métodos para la gestión de carreras
     */
    def carreraService
    /**
     * Inyección del cicloEscolarService que contiene métodos para la gestión de ciclos escolares
     */
    def cicloEscolarService
    /**
     * Inyección del estatusAlumnoService que contiene métodos para la gestión del estatus del alumno
     */
    def estatusAlumnoService
    /**
     * Inyección del cursoService que contiene métodos para la gestión de cursos
     */
    def cursoService
    /**
     * Inyección del diplomadoService que contiene métodos para la gestión de diplomados
     */
    def diplomadoService
    def alumnoCicloEscolarService
    def planEstudiosService
    def formacionService

/**
 * Permite llamar los servicios necesarios para obtener la lista de los registros activos y así realizar el registro de alumnos
 * @return datos de los servicios requeridos
 */
    def registro(){
        def instituciones = institucionService.obtenerActivos(params)
        def carreras = carreraService.obtenerActivos(params)
        def planesEstudios = planEstudiosService.obtenerActivos(params)
        def formaciones = formacionService.obtenerFormacionesIndividuales(params)
        def estatus = estatusAlumnoService.obtenerActivos(params)

        [
                instituciones: instituciones.datos,
                carreras: carreras.datos,
                planesEstudios: planesEstudios.datos,
                formaciones: formaciones.datos,
                estatus: estatus.datos,
        ]

    }

    def validarAlumno(){
        def resultado = alumnoService.validarAlumno(params)
        respond resultado
    }

    /**
    * Permite llamar al servicio para generar el registro del alumno
    * @return resultado
    * resultado con el mensaje y estatus del alumno y lo muestra en la vista
    */
    def registrar(){
        def resultado = alumnoService.registrar(params)
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
    * Permite llamar al servicio para listar los registros de los alumnos, además de mostrar las instituciones, carreras y ciclos
    escolares activas para realizar el filtrado de los alumnos
    * @return resultado
    * resultado con el listado de los alumnos registrados y los muestra en la vista
    */
    def listar(){
        def resultado = alumnoService.listar(params)
        def instituciones = institucionService.obtenerActivos(params)
        def carreras = carreraService.obtenerActivos(params)
        def planesEstudios = planEstudiosService.obtenerActivos(params)
        def ciclosEscolares = cicloEscolarService.obtenerActivos(params)

        [
            instituciones: instituciones.datos,
            carreras: carreras.datos,
            planesEstudios: planesEstudios.datos,
            alumnos: resultado.datos,
            conteo: resultado.datos.totalCount,
            parametros: params
        ]

    }
/**
 * Permite mandar a llamar el servicio para consultar un alumno en especifico
 * @return resultado
 * resultado del mensaje, estatus y datos del alumno consultado y lo muestra en la vista
 */
    def consultar(){
        def resultado = alumnoService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect action: "listar"
            return
        }

        [alumno: resultado.datos]
    }

/**
 * Permite llamar los servicios necesarios para obtener la lista de los registros activos, así como la consulta de los datos
 guardados del registro del alumno para realizar la modificación.
 * @return datos de los servicios requeridos
 */
    def modificacion(){
        def resultado = alumnoService.consultar(params)
        def instituciones = institucionService.obtenerActivos(params).datos
        def carreras = carreraService.obtenerActivos(params).datos
        def planesEstudios = planEstudiosService.obtenerActivos(params).datos
        def estatus = estatusAlumnoService.obtenerActivos(params).datos

        if(!resultado.estatus){
            redirect action: "listar"
            return
        }

        [
            instituciones: instituciones,
            carreras: carreras,
            planesEstudios: planesEstudios,
            estatus: estatus,
            alumno: resultado.datos
        ]
    }

/**
 * Permite llamar al servicio para generar la modificación del alumno seleccionado
 * @return resultado
 * resultado con el mensaje y estatus del alumno y lo muestra en la vista
 */
    def modificar(){
        def resultado = alumnoService.modificar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            redirect action: "listar"
            return
        }
        redirect(action: "modificacion" , params:params)
    }

    /**
    * Permite llamar al servicio para generar la eliminación de un alumno seleccionado
    * @return resultado
    * resultado con el mensaje y estatus del alumno eliminado y lo muestra en la vista
    */
    def eliminar(){
        def resultado = alumnoService.eliminar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect action: "listar"

        return
    }

    def subirExcel(){
        def instituciones = institucionService.obtenerActivos().datos
        def carreras = carreraService.obtenerActivos(params).datos
        def planesEstudios = planEstudiosService.obtenerActivos(params).datos

        [
            instituciones: instituciones,
            carreras: carreras,
            planesEstudios: planesEstudios
        ]
    }

    def cargarPorExcel(){
        def resultado = alumnoService.cargarPorExcel(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            redirect action: "listar"
            return
        }

        redirect(action: "subirExcel" , params:params)
    }

    def listarCiclosEscolares(){
        def alumno = alumnoService.consultar(params).datos
        def ciclosEscolares = alumnoCicloEscolarService.obtenerPorAlumno(alumno.id)

        [
            ciclosEscolares: ciclosEscolares,
            alumno: alumno
        ]
    }

    def agregarCicloEscolar(){
        def alumno = alumnoService.consultar(params).datos
        def ciclosEscolares = cicloEscolarService.obtenerParaInscripcion(alumno)

        [
            alumno: alumno,
            ciclosEscolares: ciclosEscolares
        ]
    }

    def registrarCicloEscolar(){
        def resultado = alumnoCicloEscolarService.registro(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect(action: "agregarCicloEscolar", params: params)
            return
        }

        redirect (action: "listarCiclosEscolares", params: params)
        return
    }

    def eliminarInscripcion(){
        def resultado = alumnoCicloEscolarService.eliminar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect(action: "listarCiclosEscolares", params: [id: resultado.datos.alumno.id])
        return
    }

    def subirExcelCiclosEscolares(){
        def alumno = alumnoService.consultar(params).datos

        [
            alumno: alumno
        ]
    }

    def descargarPlantilla(){
        def plantilla = grailsApplication.mainContext.getResource("plantillasExcel/alumnos_plantilla.xlsx").file.getAbsoluteFile()
        response.setContentType("application/octet-stream")
        response.setHeader("Content-disposition", "attachment; filename=alumnos_plantilla.xlsx")
        response.outputStream << plantilla.getBytes()
        return
    }
}
