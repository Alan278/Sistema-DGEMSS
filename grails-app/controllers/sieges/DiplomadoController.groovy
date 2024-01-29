package sieges

/**
 * Controlador que permite el enlace entre el modelo y las vistas de diplomados
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */
class DiplomadoController {
    /**
     * Inyección de InstitucionService que contiene la lógica de administración de instituciones
     */
    def institucionService
    /**
     * Inyección de DiplomadoService que contiene la lógica de administración de diplomados
     */
    def diplomadoService

    /**
     * Obtiene los catálogos necesarios para la vista 'registro'
     */
    def registro(){
        def instituciones = institucionService.obtenerActivos(params)
        [instituciones: instituciones.datos]
    }

    /**
     * Registra un nuevo diplomado
     * @param nombre (Requerido)
     * Nombre del diplomado
     * @param institucionId (Requerido)
     * Identifcador de la institución
     */
    def registrar(){
        def resultado = diplomadoService.registrar(params)
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
     * Obtiene los diplomados activos para mostrarlos en la vista 'listar'
     * @param institucionId (Opcional)
     * Identificador de la institución
     * @param search (Opcional)
     * Nombre del diplomado
     */
    def listar(){
        def diplomados = diplomadoService.listar(params)
        def instituciones = institucionService.obtenerActivos(params)

        [
            instituciones: instituciones.datos,
            diplomados: diplomados.datos,
            conteo: diplomados.datos.totalCount
        ]
    }

    /**
     * Obtiene los datos de un diplomado para mostrarlos en la vista 'consultar'
     * @param id (Requerido)
     * Identificador del diplomado
     */
    def consultar(){
        def resultado = diplomadoService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect action: "listar"
            return
        }

        [diplomado: resultado.datos]
    }

    /**
     * Obtiene los datos necesarios para la vista 'modificacion'
     * @param id (Requerido)
     * Identificador del diplomado
     */
    def modificacion(){
        def resultado = diplomadoService.consultar(params)
        def instituciones = institucionService.obtenerActivos(params)

        if(!resultado.estatus){
            redirect action: "listar"
            return
        }

        [
            instituciones: instituciones.datos,
            diplomado: resultado.datos
        ]
    }

    /**
     * Modifica los datos de un diplomado
     * @param id (Requerido)
     * Identificador del diplomado
     * @param nombre (Requerido)
     * Nombre del diplomado
     * @param institucionId (Requerido)
     * Identifcador de la institución
     */
    def modificar(){
        def resultado = diplomadoService.modificar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            redirect action: "listar"
            return
        }
        redirect(action: "modificacion" , params:params)
    }

    /**
     * Realiza una baja lógica de un diplomado
     * @param id (Requerido)
     * Identificador del diplomado
     */
    def eliminar(){
        def resultado = diplomadoService.eliminar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect action: "listar"

        return
    }
}
