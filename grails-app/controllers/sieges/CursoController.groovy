package sieges

class CursoController {
    /**
     * Inyección del institucionService que contiene métodos para la gestión de instituciones
     */
    def institucionService
    /**
     * Inyección del cursoService que contiene métodos para la gestión de cursos
     */
    def cursoService

/**
 * Permite llamar los servicios necesarios para obtener la lista de los registros activos y así realizar el registro de cursos
 * @return datos de los servicios requeridos
 */
    def registro(){
        def instituciones = institucionService.obtenerActivos(params)
        [instituciones: instituciones.datos]
    }

/**
 * Permite llamar al servicio para generar el registro del curso
 * @return resultado
 * resultado con el mensaje y estatus del curso y lo muestra en la vista
 */
    def registrar(){
        def resultado = cursoService.registrar(params)
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
 * Permite listar los registros de los cursos, además de mostrar las instituciones activas para  realizar el filtrado de los
 cursos
 * @return resultado
 * resultado con el listado de los cursos registrados y los muestra en la vista
 */
    def listar(){
        def cursos = cursoService.listar(params)
        def instituciones = institucionService.obtenerActivos(params)

        [
            instituciones: instituciones.datos,
            cursos: cursos.datos,
            conteo: cursos.datos.totalCount
        ]
    }

/**
 * Permite mandar a llamar el servicio para consultar un curso en especifico
 * @return resultado
 * resultado del mensaje, estatus y datos del curso consultado y lo muestra en la vista
 */
    def consultar(){
        def resultado = cursoService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect action: "listar"
            return
        }

        [curso: resultado.datos]
    }

/**
 * Permite llamar los servicios necesarios para obtener la lista de los registros activos, así como la consulta de los datos
 guardados del registro del curso para realizar la modificación.
 * @return datos de los servicios requeridos
 */
    def modificacion(){
        def resultado = cursoService.consultar(params)
        def instituciones = institucionService.obtenerActivos(params)

        if(!resultado.estatus){
            redirect action: "listar"
            return
        }

        [
            instituciones: instituciones.datos,
            curso: resultado.datos
        ]
    }

/**
 * Permite llamar al servicio para generar la modificación del curso seleccionado
 * @return resultado
 * resultado con el mensaje y estatus del curso y lo muestra en la vista
 */
    def modificar(){
        def resultado = cursoService.modificar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            redirect action: "listar"
            return
        }
        redirect(action: "modificacion" , params:params)
    }

/**
 * Permite llamar al servicio para generar la eliminación de un curso seleccionado
 * @return resultado
 * resultado con el mensaje y estatus del curso eliminado y lo muestra en la vista
 */
    def eliminar(){
        def resultado = cursoService.eliminar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect action: "listar"

        return
    }
}
