package sieges

import java.text.SimpleDateFormat

class CarreraController {
    /**
     * Inyección del carreraService que contiene métodos para la gestión de carreras
     */
    def carreraService
    /**
     * Inyección del institucionService que contiene métodos para la gestión de instituciones
     */
    def institucionService
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
 * @return datos de los servicios requeridos
 */
    def registro(){
        def instituciones = institucionService.obtenerActivos()
        def niveles = nivelService.obtenerActivos(params)
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
 * Permite llamar al servicio para generar el registro de la carrera
 * @return resultado
 * resultado con el mensaje y estatus de la carrera y lo muestra en la vista
 */
    def registrar(){
        def resultado = carreraService.registrar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        // En caso de que permita realizar el registro lo manda a la vista de listar
        if(resultado.estatus){
            redirect action: "listar"
        }else{
            //En caso contrarío lo manda a la vista del registro en donde se muestra el mensaje de error
            redirect(action: "registro", params: params)
        }
        return
    }
/**
 * Permite llamar al servicio para listar los registros de las carreras, además de mostrar las instituciones activas para
 realizar el filtrado de las carreras
 * @return resultado
 * resultado con el listado de las carreras registradas y los muestra en la vista
 */
    def listar(){
        def resultado = carreraService.listar(params)
        def instituciones = institucionService.obtenerActivos(params)

        [
            instituciones: instituciones.datos,
            carreras: resultado.datos,
            conteo: resultado.datos.totalCount,
            parametros: params
        ]
    }

/**
 * Permite llamar al servicio para obtener los registros que se encuentran activos
 * @return carreras
 * datos de las carreras que se encuentran activas
 */
    def obtenerActivos(){
        def carreras = carreraService.obtenerActivos(params)

        respond carreras
    }

/**
 * Permite mandar a llamar el servicio para consultar una carrera en especifico
 * @return resultado
 * resultado del mensaje, estatus y datos de la carrera consultada y lo muestra en la vista
 */
    def consultar(){
        def resultado = carreraService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect action: "listar"
            return
        }

        [
            carrera: resultado.datos
        ]
    }

/**
 * Permite llamar los servicios necesarios para obtener la lista de los registros activos, así como la consulta de los datos
 guardados del registro de la carrera para realizar la modificación.
 * @return datos de los servicios requeridos
 */
    def modificacion(){
        def resultado = carreraService.consultar(params)
        def carrera = resultado.datos
        def instituciones = institucionService.obtenerActivos().datos
        def niveles = nivelService.obtenerActivos(params).datos
        def modalidades = modalidadService.obtenerActivos().datos
        def areas = areaService.obtenerActivos().datos

        if(!resultado.estatus){
            redirect action: "listar"
            return
        }

        if(params.nombre){
            carrera.properties = params
            carrera.institucion = Institucion.get(params.institucionId)
            carrera.nivel = Nivel.get(params.nivelId)
            carrera.modalidad = Modalidad.get(params.modalidadId)
            carrera.area = Area.get(params.areaId)
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
 * Permite llamar al servicio para generar la modificación de la carrera seleccionada
 * @return resultado
 * resultado con el mensaje y estatus de la carrera y lo muestra en la vista
 */
    def modificar(){
        def resultado = carreraService.modificar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            redirect action: "listar"
            return
        }
        redirect(action: "modificacion" , params:params)
    }

/**
 * Permite llamar al servicio para generar la eliminación de un carrera seleccionada
 * @return resultado
 * resultado con el mensaje y estatus de la carrera eliminada y lo muestra en la vista
 */
    def eliminar(){
        def resultado = carreraService.eliminar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect action: "listar"

        return
    }

    def subirExcel(){
        def instituciones = institucionService.obtenerActivos().datos
        def niveles = nivelService.obtenerActivos(params).datos

        [
            instituciones: instituciones,
            niveles: niveles
        ]
    }

    def cargarPorExcel(){
        def resultado = carreraService.cargarPorExcel(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            redirect action: "listar"
            return
        }

        redirect(action: "subirExcel" , params:params)
    }

    def descargarPlantilla(){
        def plantilla = grailsApplication.mainContext.getResource("plantillasExcel/carreras_plantilla.xlsx").file.getAbsoluteFile()
        response.setContentType("application/octet-stream")
        response.setHeader("Content-disposition", "attachment; filename=carreras_plantilla.xlsx")
        response.outputStream << plantilla.getBytes()
        return
    }

}