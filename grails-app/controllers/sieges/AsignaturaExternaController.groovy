package sieges

/**
 * AsignaturaExternaController para la gestión de las asignaturas foráneas / externas
 * @authors Luis Dominguez, Leslie Navez
 * @since 2021
 */

class AsignaturaExternaController {
    /**
     * Inyección del institucionExternaService que contiene métodos para la administración de instituciones foráneas / externas
     */
    def institucionExternaService
    /**
     * Inyección del carreraExternaService que contiene métodos para la administración de carreras foráneas / externas
     */
    def carreraExternaService
    /**
     * Inyección del asignaturaExternaService que contiene métodos para la administración de asignaturas foráneas / externas
     */
    def asignaturaExternaService
    /**
     * Inyección del planEstudiosExternoService que contiene métodos para la administración de planes de estudio foráneos / externos
     */
    def planEstudiosExternoService

/**
 * Permite llamar los servicios necesarios para obtener la lista de los registros activos y así realizar el registro de asignaturas
 foráneas / externas
 * @return datos de los servicios requeridos
 */
    def registro(){
        def instituciones = institucionExternaService.obtenerActivos(params)
        def carreras = carreraExternaService.obtenerActivos(params)
        def planesEstudios = planEstudiosExternoService.obtenerActivos(params)

        [
                instituciones: instituciones.datos,
                carreras: carreras.datos,
                planesEstudios: planesEstudios.datos,
                asignatura: params
        ]
    }

/**
 * Permite llamar al servicio para generar el registro de la asignatura foránea / externa
 * @return resultado
 * resultado con el mensaje y estatus de la asignatura foránea / externa y lo muestra en la vista
 */
    def registrar(){
        def resultado = asignaturaExternaService.registrar(params)
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
 * Permite llamar al servicio para listar los registros de las asignaturas foráneas / externas, además de mostrar las instituciones,
 carreras y planes de estudios activos para realizar el filtrado de las asignaturas
 * @return resultado
 * resultado con el listado de las asignaturas foráneas / externas registradas y los muestra en la vista
 */
    def listar(){
        def resultado = asignaturaExternaService.listar(params)
        def instituciones = institucionExternaService.obtenerActivos(params)
        def carreras = carreraExternaService.obtenerActivos(params)
        def planesEstudios = planEstudiosExternoService.obtenerActivos(params)

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
 * Permite mandar a llamar el servicio para consultar una asignatura foránea / externa en especifica
 * @return resultado
 * resultado del mensaje, estatus y datos de la asignatura foránea / externa consultada y lo muestra en la vista
 */
    def consultar(){
        def resultado = asignaturaExternaService.consultar(params)
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
 guardados del registro de la asignatura foránea / externa para realizar la modificación.
 * @return datos de los servicios requeridos
 */
    def modificacion(){
        def resultado = asignaturaExternaService.consultar(params)
        def asignatura = resultado.datos
        def instituciones = institucionExternaService.obtenerActivos(params).datos
        def carreras = carreraExternaService.obtenerActivos(params).datos
        def planesEstudios = planEstudiosExternoService.obtenerActivos(params).datos
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
                asignatura: asignatura
        ]
    }

/**
 * Permite llamar al servicio para generar la modificación de la asignatura foránea / externa seleccionada
 * @return resultado
 * resultado con el mensaje y estatus de la asignatura foránea / externa y lo muestra en la vista
 */
    def modificar(){
        def resultado = asignaturaExternaService.modificar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            redirect action: "listar"
            return
        }
        redirect(action: "modificacion" , params:params)
    }

/**
 * Permite llamar al servicio para generar la eliminación de un asignatura foránea / externa seleccionada
 * @return resultado
 * resultado con el mensaje y estatus de la asignatura foránea / externa eliminada y lo muestra en la vista
 */
    def eliminar(){
        def resultado = asignaturaExternaService.eliminar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect action: "listar"

        return
    }
}
