package sieges

/**
 * Controlador que permite el enlace entre el modelo y las vistas de instituciones externas
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */
class InstitucionExternaController {
    /**
     * Inyección de InstitucionExternaService que contiene la lógica de administración de instituciones externas
     */
    def institucionExternaService

    /**
     * Obtiene los catálogos necesarios para la vista 'registro'
     */
    def registro(){
        [institucion: params]
    }

    /**
     * Registra una nueva institución externa
     * @param nombre (Requerido)
     * @param nombreComercial (Opcional)
     * @param razonSocial (Opcional)
     * @param claveCt (Opcional)
     * @param claveDgp (Opcional)
     * @param telefono (Opcional)
     * @param correoElectronico (Opcional)
     */
    def registrar(){
        def resultado = institucionExternaService.registrar(params)
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
     * Obtiene las instituciones externas activas para mostrarlas en la vista 'listar'
     * @param search (Opcional)
     * Nombre de la institución
     */
    def listar(){
        def resultado = institucionExternaService.listar(params)

        [
            instituciones: resultado.datos.instituciones,
            conteo: resultado.datos.instituciones.totalCount,
            numeroCarreras: resultado.datos.numeroCarreras
        ]
    }

    /**
     * Obtiene una institución externa para mostrarla en la vista 'consultar'
     * @param id (Requerido)
     * Identificador de la institución
     */
    def consultar(){
        def resultado = institucionExternaService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect action: "listar"
            return
        }

        [institucion: resultado.datos]
    }

    /**
     * Obtiene los datos necesarios para la vista 'modificacion'
     * @param id (Requerido)
     * Identificador de la institución
     */
    def modificacion(){
        def resultado = institucionExternaService.consultar(params)
        def institucion = resultado.datos

        // Si se llama a este método desde el método 'modificar' por algun error
        // en los datos, se le asigna la información previamente modificada al objeto
        // para que el usuario no tenga que realizar todas las modificaciones nuevamente
        // y solo modifique el dato erroneo
        if(params.nombre){
            institucion.properties = params
        }

        if(!resultado.estatus){
            redirect action: "listar"
            return
        }

        [institucion: institucion]
    }

    /**
     * Modifica los datos de una institución externa
     * @param id (Requerido)
     * Identificador de la institución
     * @param nombre (Requerido)
     * @param nombreComercial (Opcional)
     * @param razonSocial (Opcional)
     * @param claveCt (Opcional)
     * @param claveDgp (Opcional)
     * @param telefono (Opcional)
     * @param correoElectronico (Opcional)
     */
    def modificar(){
        def resultado = institucionExternaService.modificar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            redirect action: "listar"
            return
        }
        redirect(action: "modificacion" , params:params)
    }

    /**
     * Realiza una baja lógica de una institución externa
     * @param id (Requerido)
     * Identificador de la institución
     */
    def eliminar(){
        def resultado = institucionExternaService.eliminar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect action: "listar"

        return
    }
}
