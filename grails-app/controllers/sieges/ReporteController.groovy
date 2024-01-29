package sieges

/**
 * Controlador que permite el enlace entre el modelo y las vistas de reportes
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */
class ReporteController {
    /**
     * Inyección de ReporteService que contiene la lógica de administración de reportes
     */
    def reporteService

    /**
     * Permite mostrar la vista 'registro'
     */
    def registro(){}

    /**
     * Permite hacer el registro de un reporte
     * @param nombre (Requerido)
     * Nombre del reporte
     * @param sql (Requerido)
     * Sentencia sql para realizar el reporte
     */
    def registrar(){
        def resultado = reporteService.registrar(params)
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
     * Obtiene los reportes activos para mostrarlas en la vista 'listar'
     * @param search (Opcional)
     * Nombre del reporte
     */
    def listar(){
        def resultado = reporteService.listar(params)

        [
            reportes: resultado.datos,
            conteo: resultado.datos.totalCount
        ]
    }

    /**
     * Obtiene un reporte para mostrarlo en la vista 'consultar'
     * @param id (Requerido)
     * Identificador del reporte
     */
    def consultar(){
        def resultado = reporteService.consultar(params)
        flash.estatus = resultado.estatusConsulta
        flash.mensaje = resultado.mensaje

        if(!resultado.estatusConsulta){
            redirect action: "listar"
            return
        }

        [
            reporte: resultado.datos,
            consulta: resultado.consulta
        ]
    }

    /**
     * Obtiene los datos necesarios para la vista 'modificacion'
     * @param id (Requerido)
     * Identificador del reporte
     */
    def modificacion(){
        def resultado = reporteService.consultar(params)

        if(!resultado.estatus){
            redirect action: "listar"
            return
        }

        [reporte: resultado.datos]
    }

    /**
     * Permite modificar los datos de un reporte
     * @param id (Requerido)
     * Identificador del reporte
     * @param nombre (Requerido)
     * Nombre del reporte
     * @param sql (Requerido)
     * Sentencia sql para realizar el reporte
     */
    def modificar(){
        def resultado = reporteService.modificar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            redirect action: "listar"
            return
        }
        redirect(action: "modificacion" , params:params)
    }

    /**
     * Realiza una baja lógica de un reporte
     * @param id (Requerido)
     * Identificador del reporte
     */
    def eliminar(){
        def resultado = reporteService.eliminar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect action: "listar"

        return
    }

    def descargarExcel(){
        def resultado = reporteService.obtenerExcel(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            def excel = resultado.datos
            response.setContentType("application/octet-stream")
            response.setHeader("Content-disposition", "attachment; filename=Reporte.xlsx")
            response.outputStream << excel
            return
        }

        redirect(action: "consultar", params: params)
        return
    }

}
