package sieges

/**
 * Controlador que permite el enlace entre el modelo y las vistas del módulo de revalidaciones
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */

class RevalidacionController {
    /**
     * Inyección de revalidacionService que contiene la lógica de administración de las revalidaciones
     */
    def revalidacionService

    /**
     * Obtiene los catálogos necesarios para la vista 'registro'
     */
    def registro(){
        [revalidacion: params]
    }

    /**
     * Permite llamar al servicio para generar el registro de la revalidación
     * @return resultado
     * resultado con el mensaje y estatus de la revalidación y lo muestra en la vista
     */
    def registrar(){
        def resultado = revalidacionService.registrar(params)
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
     * Obtiene las revalidaciones activas para mostrarlas en la vista 'listar'
     * @param search (Opcional)
     * Número de expediente
     */
    def listar(){
        def resultado = revalidacionService.listar(params)
        [
            revalidaciones: resultado.datos,
            conteo: resultado.datos.totalCount
        ]
    }

    /**
     * Obtiene una revalidación para mostrarla en la vista 'consultar'
     * @param id (Requerido)
     * Identificador de la revalidación
     */
    def consultar(){
        def resultado = revalidacionService.consultarPorExpediente(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect action: "listar"
            return
        }

        [
            revalidacion: resultado.datos
        ]
    }

    /**
     * Obtiene una equivalencia para generar la descarga en archivo pdf 'descargar'
     * @param id (Requerido)
     * Identificador de la equivalencia
     */
    def descargarPdf(){
        def resultado = revalidacionService.consultarPorExpediente(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            def pdf = resultado.datos.pdf
            response.setContentType("application/octet-stream")
            response.setHeader("Content-disposition", "attachment; filename=Revalidacion.pdf")
            response.outputStream << pdf
            return
        }

        redirect(action: "listarCertificados", params: [id: params.personaId])
        return

    }

    /**
     * Obtiene una revalidación para mostrarla en una nueva ventana en archivo pdf 'mostrar'
     * @param id (Requerido)
     * Identificador de la revalidación
     */
    def mostrarPdf(){
        def resultado = revalidacionService.consultarPorExpediente(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            def pdf = resultado.datos.pdf
            render file: pdf, contentType: "application/pdf"
        }
        return
    }

}
