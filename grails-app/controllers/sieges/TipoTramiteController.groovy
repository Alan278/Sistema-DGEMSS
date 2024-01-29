package sieges

class TipoTramiteController {
    def tipoTramiteService

    /**
     * Permite listar todos los tipos de tramites activos
     */
    def listar() {
        [
            uma: Uma.get(1),
            tiposTramite: tipoTramiteService.listar()
        ]
    }

    /**
     * Muestra el formulario para la modificación de un tipo de tramite
     */
    def modificacion() {
        [
            tipoTramite: tipoTramiteService.obtener(params.id)
        ]
    }

    /**
     * Muestra el formulario para la modificación de un tipo de tramite
     */
    def modificar() {
        def resultado = tipoTramiteService.modificar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect action: "modificacion"
            return
        }

        redirect action: "listar"
        return
    }


}
