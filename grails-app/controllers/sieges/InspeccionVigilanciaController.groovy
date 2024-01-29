package sieges

/**
 * Controlador que permite el enlace entre el modelo y las vistas de instituciones
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */
class InspeccionVigilanciaController {
    /**
     * Inyección de inspeccionVigilanciaService
     */
    def inspeccionVigilanciaService
    def institucionService
    def carreraService
    def cicloEscolarService
    def consultaPagoService
    def tipoTramiteService
    def pagoService

    /**
     * Obtiene los catálogos necesarios para la vista 'registro'
     */
    def registro(){

        def resultado = pagoService.consultarPago(params, tipoTramiteService.INSPECCION_VIGILANCIA)
        println(resultado)
        if(!flash.mensaje){
            params.datosPago = resultado.datos
            flash.estatus = resultado.estatus
            flash.mensaje = resultado.mensaje
        }

        def instituciones = institucionService.obtenerActivos(params).datos

        [
            instituciones: instituciones,
        ]
    }

    def seleccionCiclosEscolares(){
        def institucion = Institucion.get(params.institucionId)
        def ciclosEscolares = cicloEscolarService.listarSinTramite(params)

        [
            ciclosEscolares: ciclosEscolares,
            institucion: institucion,
            tipoTramite: tipoTramiteService.obtener(2),
            uma: Uma.get(1)
        ]
    }

    /**
     * Permite registrar un nuevo trámite de inspeccion y vigilancia
     */
    def registrar(){
        def resultado = inspeccionVigilanciaService.registrar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        println(resultado)

        if(!resultado.estatus){
            redirect(action: "registro", params: params)
            return
        }
        redirect action: "listar"
        return
    }

    def listar(){
        def instituciones = institucionService.obtenerActivos(params)
        def carreras = carreraService.obtenerActivos(params)
        def tramites = inspeccionVigilanciaService.listar(params)

        [
            instituciones: instituciones.datos,
            carreras: carreras.datos,
            tramites: tramites.datos,
            conteo: tramites.datos.totalCount
        ]
    }

    def consultar(){
        def resultado = inspeccionVigilanciaService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect action: "listar"
            return
        }

        [tramite: resultado.datos]
    }
}