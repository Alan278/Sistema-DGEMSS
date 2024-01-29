package sieges

import java.text.SimpleDateFormat

class CarreraExternaController {
    /**
     * Inyección del institucionExternaService que contiene métodos para la gestión de instituciones foráneas / externas
     */
    def institucionExternaService
    /**
     * Inyección del carreraExternaService que contiene métodos para la gestión de carreras foráneas / externas
     */
    def carreraExternaService
    /**
     * Inyección del nivelService que contiene métodos para la gestión de niveles
     */
    def nivelService
    /**
     * Inyección del modalidadService que contiene métodos para la gestión de modalidades
     */
    def modalidadService
    /**
     * Inyección del areaService que contiene métodos para la gestión de areas
     */
    def areaService

/**
 * Permite llamar los servicios necesarios para obtener la lista de los registros activos y así realizar el registro de carreras
 foráneas / externas
 * @return datos de los servicios requeridos
 */
    def registro(){
        def instituciones = institucionExternaService.obtenerActivos()
        def niveles = nivelService.obtenerActivos()
        def modalidades = modalidadService.obtenerActivos()
        def areas = areaService.obtenerActivos()

        [
                instituciones: instituciones.datos,
                niveles: niveles.datos,
                modalidades: modalidades.datos,
                areas: areas.datos,
                carrera: params
        ]
    }

/**
 * Permite llamar al servicio para generar el registro de la carrera foránea / externa
 * @return resultado
 * resultado con el mensaje y estatus de la carrera foránea / externa y lo muestra en la vista
 */
    def registrar(){
        def resultado = carreraExternaService.registrar(params)
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
 * Permite llamar al servicio para listar los registros de las carreras foráneas / externas, además de mostrar las instituciones
 activas para realizar el filtrado de las carreras
 * @return resultado
 * resultado con el listado de las carreras foráneas / externas registradas y los muestra en la vista
 */
    def listar(){
        def resultado = carreraExternaService.listar(params)
        def instituciones = institucionExternaService.obtenerActivos()

        [
                instituciones: instituciones.datos,
                carreras: resultado.datos.carreras,
                conteo: resultado.datos.carreras.totalCount,
                numeroPlanes: resultado.datos.numeroPlanes,
                parametros: params
        ]
    }

/**
 * Permite llamar al servicio para obtener los registros que se encuentran activos
 * @return carreras foráneas / externas
 * datos de las carreras foráneas / externas que se encuentran activas
 */
    def obtenerActivos(){
        def carreras = carreraExternaService.obtenerActivos(params)

        respond carreras
    }

/**
 * Permite mandar a llamar el servicio para consultar una carrera foránea / externa en especifico
 * @return resultado
 * resultado del mensaje, estatus y datos de la carrera foránea / externa consultada y lo muestra en la vista
 */
    def consultar(){
        def resultado = carreraExternaService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect action: "listar"
            return
        }

        [
                carrera: resultado.datos,
        ]
    }

/**
 * Permite llamar los servicios necesarios para obtener la lista de los registros activos, así como la consulta de los datos
 guardados del registro de la carrera foránea / externa para realizar la modificación.
 * @return datos de los servicios requeridos
 */
    def modificacion(){
        def resultado = carreraExternaService.consultar(params)
        def carrera = resultado.datos
        def instituciones = institucionExternaService.obtenerActivos().datos
        def niveles = nivelService.obtenerActivos().datos
        def modalidades = modalidadService.obtenerActivos().datos
        def areas = areaService.obtenerActivos().datos

        if(params.nombre){
            carrera.properties = params
            carrera.institucion = Institucion.get(params.institucionId)
            carrera.nivel = Nivel.get(params.nivelId)
            carrera.modalidad = Modalidad.get(params.modalidadId)
            carrera.area = Area.get(params.areaId)
        }

        if(!resultado.estatus){
            redirect action: "listar"
            return
        }

        [
                instituciones: instituciones,
                niveles: niveles,
                modalidades: modalidades,
                areas: areas,
                carrera: carrera
        ]
    }

/**
 * Permite llamar al servicio para generar la modificación de la carrera foránea / externa seleccionada
 * @return resultado
 * resultado con el mensaje y estatus de la carrera foránea / externa y lo muestra en la vista
 */
    def modificar(){
        def resultado = carreraExternaService.modificar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            redirect action: "listar"
            return
        }
        redirect(action: "modificacion" , params:params)
    }

/**
 * Permite llamar al servicio para generar la eliminación de un carrera foránea / externa seleccionada
 * @return resultado
 * resultado con el mensaje y estatus de la carrera foránea / externa eliminada y lo muestra en la vista
 */
    def eliminar(){
        def resultado = carreraExternaService.eliminar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect action: "listar"

        return
    }
}
