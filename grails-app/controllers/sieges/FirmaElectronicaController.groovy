package sieges

class FirmaElectronicaController {
    def firmaElectronicaService
    def usuarioService

    def consultar(){
        def usuario = usuarioService.obtenerUsuarioLogueado()
        [usuario: usuario]
    }

    def registroCer(){
        def usuario = usuarioService.obtenerUsuarioLogueado()
        [usuario: usuario]
    }

    def registrarCer(){
        def resultado = firmaElectronicaService.registrarCer(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect(action: "registroCer")
            return
        }

        redirect(action: "consultar")
        return
    }

    def registroKey(){
        def usuario = usuarioService.obtenerUsuarioLogueado()
        [usuario: usuario]
    }

    def registrarKey(){
        def resultado = firmaElectronicaService.registrarKey(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect(action: "registroKey", params: [id: params.id])
            return
        }

        redirect(action: "consultar")
        return
    }

    def eliminarCer(){
        def resultado = firmaElectronicaService.eliminarCer(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect(action: "consultar")
        return
    }
    
    def eliminarKey(){
        def resultado = firmaElectronicaService.eliminarKey(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect(action: "consultar")
        return
    }

}