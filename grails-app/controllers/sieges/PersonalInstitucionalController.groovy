package sieges

/**
 * Controlador que permite el enlace entre el modelo y las vistas de personal
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */
class PersonalInstitucionalController {
    /**
     * Inyección de InstitucionService que contiene la lógica de administración de instituciones
     */
    def institucionService
    /**
     * Inyección de CargoInstitucionalService que contiene la lógica de administración de cargos
     */
    def cargoInstitucionalService
    /**
     * Inyección de PersonalInstitucionalService que contiene la lógica de administración del personal
     */
    def personalInstitucionalService

    /**
     * Obtiene los catálogos necesarios para la vista 'registro'
     */
    def registro(){
        def instituciones = institucionService.obtenerActivos(params)
        def cargos = cargoInstitucionalService.obtenerActivos(params)

        [
            instituciones: instituciones.datos,
            cargos: cargos.datos,
            personalInstitucional: params
        ]
    }

    /**
     * Registra un nuevo personal
     * @param institucionId (Requerido)
     * @param cargoId (Requerido)
     * @param nombreCargo (Requerido)
     * @param nombre (Requerido)
     * @param primerApellido (Requerido)
     * @param segundoApellido (Opcional)
     * @param curp (Requerido)
     * @param rfc (Opcional)
     * @param telefono (Requerido)
     * @param correoElectronico (Requerido)
     */
    def registrar(){
        def resultado = personalInstitucionalService.registrar(params)
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
     * Obtiene el personal activo para mostrarlos en la vista 'listar'
     * @param institucionId (Opcional)
     * Identificador de la institución
     * @param search (Opcional)
     * Nombre del personal
     */
    def listar(){
        def resultado = personalInstitucionalService.listar(params)
        def instituciones = institucionService.obtenerActivos(params)

        [
            instituciones: instituciones.datos,
            personalInstitucional: resultado.datos.personalInstitucional,
            conteo: resultado.datos.personalInstitucional.totalCount,
            parametros: params
        ]
    }

    /**
     * Obtiene un personal para mostrarlo en la vista 'consultar'
     * @param id (Requerido)
     * Identificador del personal
     */
    def consultar(){
        def resultado = personalInstitucionalService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect action: "listar"
            return
        }

        [personalInstitucional: resultado.datos]
    }

    /**
     * Obtiene los datos necesarios para la vista 'modificacion'
     * @param id (Requerido)
     * Identificador del personal
     */
    def modificacion(){
        def resultado = personalInstitucionalService.consultar(params)
        def personalInstitucional = resultado.datos
        def instituciones = institucionService.obtenerActivos(params).datos
        def cargos = cargoInstitucionalService.obtenerActivos(params).datos

        // Si se llama a este método desde el método 'modificar' por algun error
        // en los datos, se le asigna la información previamente modificada al objeto
        // para que el usuario no tenga que realizar todas las modificaciones nuevamente
        // y solo modifique el dato erroneo
        if(params.nombre){
            personalInstitucional.properties = params
            personalInstitucional.persona.domicilio.properties = params
            personalInstitucional.persona.properties = params
            personalInstitucional.institucion = Institucion.get(params.institucionId)
            personalInstitucional.cargoInstitucional = CargoInstitucional.get(params.cargoId)
        }

        if(!resultado.estatus){
            redirect action: "listar"
            return
        }

        [
            instituciones: instituciones,
            cargos: cargos,
            personalInstitucional: personalInstitucional
        ]
    }

    /**
     * Modifica los datos de un personal
     * @param id (Requerido)
     * Identificador del personal
     * @param institucionId (Requerido)
     * @param cargoId (Requerido)
     * @param nombreCargo (Requerido)
     * @param nombre (Requerido)
     * @param primerApellido (Requerido)
     * @param segundoApellido (Opcional)
     * @param curp (Requerido)
     * @param rfc (Opcional)
     * @param telefono (Requerido)
     * @param correoElectronico (Requerido)
     */
    def modificar(){
        def resultado = personalInstitucionalService.modificar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            redirect action: "listar"
            return
        }
        redirect(action: "modificacion" , params:params)
    }

    /**
     * Realiza una baja lógica de un personal
     * @param id (Requerido)
     * Identificador del personal
     */
    def eliminar(){
        def resultado = personalInstitucionalService.eliminar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect action: "listar"

        return
    }
}
