package sieges

/**
 * Controlador que permite el enlace entre el modelo y las vistas de la bitácora
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */
class BitacoraController {
    /**
     * Inyección de ReporteService que contiene la lógica de administración de la bitácora
     */
    def bitacoraService

    /**
     * Obtiene los registros activos de la bitácora para mostrarlos en la vista 'listar'
     * @param search (Opcional)
     * Nombre del reporte
     */
    def listar(){
        def resultado = bitacoraService.listar(params)

        [
            bitacora: resultado.datos,
            conteo: resultado.datos.totalCount
        ]
    }

    /**
     * Obtiene un registro de la bitácora para mostrarlo en la vista 'consultar'
     * @param id (Requerido)
     * Identificador del registro
     */
    def consultar(){
        def resultado = bitacoraService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect action: "listar"
            return
        }

        [registro: resultado.datos]
    }
}
