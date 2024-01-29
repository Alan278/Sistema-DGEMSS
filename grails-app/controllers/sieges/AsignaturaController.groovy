package sieges

class AsignaturaController {
    /**
     * Inyección del institucionService que contiene métodos para la gestión de instituciones
     */
    def institucionService
    /**
     * Inyección del carreraService que contiene métodos para la gestión de carreras
     */
    def carreraService
    /**
     * Inyección del asignaturaService que contiene métodos para la gestión de las asignaturas
     */
    def asignaturaService
    /**
     * Inyección del planEstudiosService que contiene métodos para la gestión de los planes de estudio
     */
    def planEstudiosService
    /**
     * Inyección del formacionService que contiene métodos para la gestión de formaciones
     */
    def formacionService


/**
 * Permite llamar los servicios necesarios para obtener la lista de los registros activos y así realizar el registro de asignaturas
 * @return datos de los servicios requeridos
 */
    def registro(){
        def instituciones = institucionService.obtenerActivos(params)
        def carreras = carreraService.obtenerActivos(params)
        def planesEstudios = planEstudiosService.obtenerActivos(params)
        def formaciones = formacionService.obtenerActivos(params)

        [
            instituciones: instituciones.datos,
            carreras: carreras.datos,
            planesEstudios: planesEstudios.datos,
            asignatura: params,
            formaciones: formaciones.datos
        ]
    }

/**
 * Permite llamar al servicio para generar el registro de la asignatura
 * @return resultado
 * resultado con el mensaje y estatus de la asignatura y lo muestra en la vista
 */
    def registrar(){
        def resultado = asignaturaService.registrar(params)
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
 * Permite listar los registros de las asignaturas, además de mostrar las instituciones, carreras y planes de estudios activos para
 realizar el filtrado de las asignaturas
 * @return resultado
 * resultado con el listado de las asignaturas registradas y los muestra en la vista
 */
    def listar(){
        def resultado = asignaturaService.listar(params)
        def instituciones = institucionService.obtenerActivos(params)
        def carreras = carreraService.obtenerActivos(params)
        def planesEstudios = planEstudiosService.obtenerActivos(params)

        [
            instituciones: instituciones.datos,
            carreras: carreras.datos,
            planesEstudios: planesEstudios.datos,
            asignaturas: resultado.datos,
            conteo: resultado.datos.totalCount,
            parametros: params
        ]
    }

/**
 * Permite mandar a llamar el servicio para consultar una asignatura en especifica
 * @return resultado
 * resultado del mensaje, estatus y datos de la asignatura consultada y lo muestra en la vista
 */
    def consultar(){
        def resultado = asignaturaService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect action: "listar"
            return
        }

        [asignatura: resultado.datos]
    }

/**
 * Permite llamar los servicios necesarios para obtener la lista de los registros activos, así como la consulta de los datos
 guardados del registro de la asignatura para realizar la modificación.
 * @return datos de los servicios requeridos
 */
    def modificacion(){
        def resultado = asignaturaService.consultar(params)
        def asignatura = resultado.datos
        def instituciones = institucionService.obtenerActivos(params).datos
        def carreras = carreraService.obtenerActivos(params).datos
        def planesEstudios = planEstudiosService.obtenerActivos(params).datos
        if(!params.planEstudiosId){
            params.planEstudiosId = asignatura.planEstudios.id
        }
        def formaciones = formacionService.obtenerActivos(params).datos

        def planEstudios
        def carrera

        if(params.nombre){
            asignatura.properties = params
            if(!params.planEstudiosId){
                if(params.carreraId){
                    carrera = Carrera.get(params.carreraId)
                }else{
                    carrera = new Carrera()
                    carrera.institucion = Institucion.get(params.institucionId)
                }
                planEstudios = new PlanEstudios()
                planEstudios.carrera = carrera
            }else{
                planEstudios = PlanEstudios.get(params.planEstudiosId)
            }
            asignatura.planEstudios = planEstudios
        }

        if(!resultado.estatus){
            redirect action: "listar"
            return
        }

        [
            instituciones: instituciones,
            carreras: carreras,
            planesEstudios: planesEstudios,
            asignatura: asignatura,
            formaciones: formaciones,
        ]
    }

/**
 * Permite llamar al servicio para generar la modificación de la asignatura seleccionada
 * @return resultado
 * resultado con el mensaje y estatus de la asignatura y lo muestra en la vista
 */
    def modificar(){
        def resultado = asignaturaService.modificar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            redirect action: "listar"
            return
        }
        redirect(action: "modificacion" , params:params)
    }

/**
 * Permite llamar al servicio para generar la eliminación de un asignatura seleccionada
 * @return resultado
 * resultado con el mensaje y estatus de la asignatura eliminada y lo muestra en la vista
 */
    def eliminar(){
        def resultado = asignaturaService.eliminar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect action: "listar"

        return
    }

    def subirExcel(){
        def instituciones = institucionService.obtenerActivos(params)
        def carreras = carreraService.obtenerActivos(params)
        def planesEstudios = planEstudiosService.obtenerActivos(params)

        [
            instituciones: instituciones.datos,
            carreras: carreras.datos,
            planesEstudios: planesEstudios.datos
        ]
    }

    def cargarPorExcel(){
        def resultado = asignaturaService.cargarPorExcel(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            redirect action: "listar"
            return
        }

        redirect(action: "subirExcel" , params:params)
    }

    def descargarPlantilla(){
        def plantilla = grailsApplication.mainContext.getResource("plantillasExcel/asignaturas_plantilla.xlsx").file.getAbsoluteFile()
        response.setContentType("application/octet-stream")
        response.setHeader("Content-disposition", "attachment; filename=asignaturas_plantilla.xlsx")
        response.outputStream << plantilla.getBytes()
        return
    }
}