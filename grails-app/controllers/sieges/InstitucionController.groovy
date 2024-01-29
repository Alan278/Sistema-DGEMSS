package sieges

import grails.plugin.springsecurity.annotation.Secured

/**
 * Controlador que permite el enlace entre el modelo y las vistas de instituciones
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */
class InstitucionController {
    /**
     * Inyección de InstitucionService que contiene la lógica de administración de instituciones
     */
    def institucionService
    def usuarioService
    def nivelService

    private parametros = null
    private logo = null

    /**
     * Obtiene los catálogos necesarios para la vista 'registro'
     */
    def registro(){
        if(params.claveCt){
            def institucion = institucionService.obtenerPorClaveCt(params.claveCt)
            if(institucion){
                flash.estatus = false
                flash.mensaje = "Ya existe una institución con la clave CCT ingresada"
                params.bloquear = true
            }else{
                params.bloquear = false
            }
        }
        [institucion: params]
    }

    def subirLogo(){}

    /**
     * Registra una nueva institución
     * @param nombre (Requerido)
     * @param nombreComercial (Requerido)
     * @param razonSocial (Requerido)
     * @param claveCt (Opcional)
     * @param claveDgp (Opcional)
     * @param telefono (Requerido)
     * @param correoElectronico (Requerido)
     * @param estado (Requerido)
     * @param referencias (Opcional)
     * @param numeroExterior (Requerido)
     * @param numeroInterior (Opcional)
     * @param asentamiento (Requerido)
     * @param localidad (Requerido)
     * @param calle (Requerido)
     * @param codigoPostal (Requerido)
     * @param latitud (Opcional)
     * @param longitud (Opcional)
     * @param municipio (Requerido)
     */
    def registrar(){
        def resultado = institucionService.registrar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            params.logo = null
            redirect(action: "registro", params: params)
            return
        }

        redirect(action: "listar")
        return
    }

    /**
     * Obtiene las instituciones activas para mostrarlas en la vista 'listar'
     * @param search (Opcional)
     * Nombre de la institución
     */
    def listar(){
        parametros = null
        logo = null
        def niveles = nivelService.obtenerActivos(params).datos
        def nivelesUsuario = usuarioService.obtenerNivelesPorRol()
        def resultado = institucionService.listar(params)

        [
            instituciones: resultado.datos.instituciones,
            conteo: resultado.datos.institucionesTotalCount,
            niveles: niveles,
            nivelesUsuario: nivelesUsuario,
            numeroCarreras: resultado.datos.numeroCarreras
        ]
    }

    /**
     * Obtiene una institución para mostrarla en la vista 'consultar'
     * @param id (Requerido)
     * Identificador de la institución
     */
    @Secured([
        "ROLE_SUPERVISOR_POSGRADO",
        "ROLE_SUPERVISOR_SUPERIOR",
        "ROLE_SUPERVISOR_MEDIA",
        "ROLE_SUPERVISOR_MEDIA_PUBLICA",
        "ROLE_SUPERVISOR_TECNICA",
        "ROLE_SUPERVISOR_TECNICA_PUBLICA",
        "ROLE_SUPERVISOR_CONTINUA",
        "ROLE_AUTENTICADOR_DGEMSS",
    ])
    def consultar(){
        def resultado = institucionService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect action: "listar"
            return
        }

        def logo
        if(resultado.datos.logo){
            logo = Base64.getEncoder().encodeToString(resultado.datos.logo)
        }

        [
            institucion: resultado.datos,
            logo: logo
        ]
    }

    /**
     * Obtiene los datos necesarios para la vista 'modificacion'
     * @param id (Requerido)
     * Identificador de la institución
     */
    def modificacion(){
        def resultado = institucionService.consultar(params)
        def institucion = resultado.datos

        // Si se llama a este método desde el método 'modificar' por algun error
        // en los datos, se le asigna la información previamente modificada al objeto
        // para que el usuario no tenga que realizar todas las modificaciones nuevamente
        // y solo modifique el dato erroneo
        if(parametros){
            institucion.properties = parametros
            institucion.domicilio.properties = parametros
        }

        if(!resultado.estatus){
            redirect action: "listar"
            return
        }

        [institucion: institucion]
    }

    /**
     * Modifica los datos de una institución
     * @param id (Requerido)
     * Identificador de la institución
     * @param nombre (Requerido)
     * @param nombreComercial (Opcional)
     * @param razonSocial (Opcional)
     * @param claveCt (Opcional)
     * @param claveDgp (Opcional)
     * @param telefono (Opcional)
     * @param correoElectronico (Opcional)
     * @param externa (Requerido)
     * @param estado (Requerido)
     * @param referencias (Opcional)
     * @param numeroExterior (Requerido)
     * @param numeroInterior (Opcional)
     * @param asentamiento (Requerido)
     * @param localidad (Requerido)
     * @param calle (Requerido)
     * @param codigoPostal (Requerido)
     * @param latitud (Opcional)
     * @param longitud (Opcional)
     * @param municipio (Requerido)
     */
    def modificar(){
        if(params.logo){
            logo = params.logo
        }else{
            params.logo = logo
        }
        parametros = parametros + params

        def resultado = institucionService.modificar(parametros)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.pag == 0){
            redirect(action: "listar")
            return
        }
        if(resultado.pag == 1){
            parametros.logo = null
            redirect(action: "modificacion", params: parametros)
            return
        }
        if(resultado.pag == 2){
            redirect(action: "subirLogoModificacion",  params: parametros)
            return
        }

        logo = null
        parametros = null
        redirect(action: "listar")
        return
    }

    /**
     * Realiza una baja lógica de una institución
     * @param id (Requerido)
     * Identificador de la institución
     */
    def eliminar(){
        def resultado = institucionService.eliminar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect action: "listar"

        return
    }

    def subirLogoModificacion(){
        parametros = params
        def institucion = institucionService.consultar(params).datos
        def logob64
        if(institucion.logo){
            logob64 = Base64.getEncoder().encodeToString(institucion.logo)
        }

        if(!logo){
            logo = logob64
        }

        [
            institucion: institucion,
            logo: logob64
        ]
    }

    def subirExcel(){}

    def cargarPorExcel(){
        def resultado = institucionService.cargarPorExcel(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            redirect action: "listar"
            return
        }

        redirect(action: "subirExcel" , params:params)
    }

    def descargarPlantilla(){
        def plantilla = grailsApplication.mainContext.getResource("plantillasExcel/instituciones_plantilla.xlsx").file.getAbsoluteFile()
        response.setContentType("application/octet-stream")
        response.setHeader("Content-disposition", "attachment; filename=instituciones_plantilla.xlsx")
        response.outputStream << plantilla.getBytes()
        return
    }
}