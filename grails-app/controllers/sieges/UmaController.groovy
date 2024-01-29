package sieges

class UmaController {
    def umaService

    def modificacion() {
        [uma: Uma.get(1)]
    }

    def modificar() {
        def resultado = umaService.modificar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect action: "modificacion"
            return
        }

        redirect(controller: "tipoTramite", action: "listar")
        return
    }
}
