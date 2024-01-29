package sieges
import sieges.Formacion
import sieges.TipoFormacion

class FormacionController {
    def formacionService
    def tipoFormacionService
    def planEstudiosService

    def listar() {
        def planEstudios = planEstudiosService.obtener(params.planEstudiosId)
        def resultado = formacionService.listar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect(controller: "planEstudios", action: "listar")
            return
        }

        [
            planEstudios: planEstudios,
            formaciones: resultado.datos
        ]
    }

    def registro() {
        [tiposFormaciones: tipoFormacionService.obtenerActivos()]
    }

    def registrar(){
        def resultado = formacionService.registrar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect action: "registro"
            return
        }
        redirect(action: "listar", params: [planEstudiosId: params.planEstudiosId])
    }

    def modificacion() {
        def formacion = formacionService.obtener(params.id)

        if(!formacion){
            redirect action: "listar"
            return
        }

        [
            formacion: formacionService.obtener(params.id),
            tiposFormaciones: tipoFormacionService.obtenerActivos()
        ]
    }

    def modificar(){
        def resultado = formacionService.modificar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect(action: "modificacion", params: params)
            return
        }
        redirect(action: "listar", params: [planEstudiosId: params.planEstudiosId])
    }

    def eliminar(){
        def resultado = formacionService.eliminar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect(action: "listar", params: [planEstudiosId: resultado.datos.planEstudios.id])
    }
}
