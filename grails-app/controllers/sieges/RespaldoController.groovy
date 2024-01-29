package sieges

/**
 * @author Alan Guevarin
 * @since 2023
 */
class RespaldoController {
    /**
     * Inyección de respaldoService que contiene la lógica de administración de los respaldos
     */
    def respaldoService

    /**
     * Permite registrar un respaldo
     * @return resultado
     * resultado con el mensaje y estatus del respaldo y lo muestra en la vista
     */
    def generar(){
        def resultado = respaldoService.generar()
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect action: "listar"

        return
    }


    /**
     * Permite generar la descarga de un respaldo
     * @return resultado
     * regresa la descarga en bytes
     */
    def descargar(){
        def resultado = [
            estatus: false,
            mensaje: 'Respaldo no encontrado'
        ]

        def file = new File('/home/Respaldos/' + params.nombre)

        if (file.exists()) {
            resultado.estatus = true
            resultado.mensaje = 'Respaldo descargado exitosamente'
            response.setContentType("application/octet-stream")
            response.setHeader("Content-disposition", "filename=${file.name}")
            response.outputStream << file.bytes
            return
        }

        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje
        redirect action: "listar"
        return
    }

    /**
     * Obtiene los respaldos activos para mostrarlas en la vista 'listar'
     * @param search (Opcional)
     * Nombre del respaldo
     */
    def listar(){
        def resultado = respaldoService.listar(params)

        [
                respaldo: resultado.datos,
                conteo: resultado.datos.size
        ]
    }

    /**
     * Realiza una baja lógica de un respaldo
     * @param id (Requerido)
     * Identificador del respaldo
     */
    def eliminar(){
        def resultado = respaldoService.eliminar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect action: "listar"

        return
    }


    def cargarSql(){}

    def ejecutarScriptSql() {
        def resultado = respaldoService.ejecutarScriptSql(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect(action: "listar")

        return
    }

}
