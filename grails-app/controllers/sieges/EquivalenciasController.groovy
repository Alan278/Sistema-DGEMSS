package sieges

/**
 * Controlador que permite el enlace entre el modelo y las vistas del módulo de equivalencias
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */

class EquivalenciasController {
    /**
     * Inyección de equivalenciaService que contiene la lógica de administración de las equivalencias
     */
    def equivalenciaService

    /**
     * Obtiene los catálogos necesarios para la vista 'registro'
     */
    def registro(){
        [equivalencia: params]
    }

    /**
     * Permite llamar al servicio para generar el registro de la equivalencia
     * @return resultado
     * resultado con el mensaje y estatus de la equivalencia y lo muestra en la vista
     */
    def registrar(){
        def resultado = equivalenciaService.registrar(params)
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
     * Obtiene las equivalencias activas para mostrarlas en la vista 'listar'
     * @param search (Opcional)
     * Número de expediente
     */
    def listar(){
        def resultado = equivalenciaService.listar(params)
        [
            equivalencias: resultado.datos,
            conteo: resultado.datos.totalCount
        ]
    }

    /**
     * Obtiene una equivalencia para mostrarla en la vista 'consultar'
     * @param id (Requerido)
     * Identificador de la equivalencia
     */
    def consultar(){
        def resultado = equivalenciaService.consultarPorExpediente(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect action: "listar"
            return
        }

        [equivalencia: resultado.datos]
    }

    /**
     * Obtiene una equivalencia para generar la descarga en archivo pdf 'descargar'
     * @param id (Requerido)
     * Identificador de la equivalencia
     */
    def descargarPdf(){
        def resultado = equivalenciaService.consultarPorExpediente(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            def pdf = resultado.datos.pdf
            response.setContentType("application/octet-stream")
            response.setHeader("Content-disposition", "attachment; filename=Equivalencia.pdf")
            response.outputStream << pdf
            return
        }

        redirect(action: "listar")
        return
    }

    /**
     * Obtiene una equivalencia para mostrarla en una nueva ventana en archivo pdf 'mostrar'
     * @param id (Requerido)
     * Identificador de la equivalencia
     */
    def mostrarPdf(){
        def resultado = equivalenciaService.consultarPorExpediente(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            def pdf = resultado.datos.pdf
            render file: pdf, contentType: "application/pdf"
            return
        }
        render "Error"
        return
    }
}
