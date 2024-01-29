package sieges

import java.text.SimpleDateFormat

class CertificacionController {
    /**
     * Inyección del certificacionService que contiene métodos para la gestión de pagos
     */
    def certificacionService
    /**
     * Inyección del institucionService que contiene métodos para la gestión de instituciones
     */
    def institucionService
    def certificadoService
    def consultaPagoService
    def tipoTramiteService
    def pagoService

    /**
     * Obtiene los catálogos necesarios para la vista de 'registro'
     */

    def registro(){
        def resultado = pagoService.consultarPago(params, tipoTramiteService.CERTIFICACION)
        if(!flash.mensaje){
            params.datosPago = resultado.datos
            flash.estatus = resultado.estatus
            flash.mensaje = resultado.mensaje
        }

        def instituciones = institucionService.obtenerActivos()

        [
            instituciones: instituciones.datos
        ]
    }

    def seleccionCertificados(){

        def institucion = Institucion.get(params.institucionId)
        def certificados = certificadoService.listarCertificadosSinTramite(params).datos

        [
            certificados: certificados,
            institucion: institucion,
            tipoTramite: tipoTramiteService.obtener(1),
            uma: Uma.get(1)
        ]
    }

    /**
     * Permite llamar al servicio para generar el registro del trámite
     * @return resultado
     * resultado con el mensaje y estatus del trámite y lo muestra en la vista
     */

    def registrar(){
        def resultado = certificacionService.registrar(params)
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
     * Permite listar los registros de los trámites
     * @return resultado
     * resultado con el listado de los trámites registrados y los muestra en la vista
     */

    def listar(){
        def resultado = certificacionService.listar(params)
        def instituciones = institucionService.obtenerActivos(params)

        [
                instituciones: instituciones.datos,
                tramites: resultado.datos,
                conteo: resultado.datos.totalCount,
                parametros: params
        ]
    }

    /**
     * Permite llamar al servicio para obtener los requisitos que se encuentran activos
     * @return pagos
     * datos de los pagos que se encuentran activos
     */

    def obtenerActivos(){
        def tramites = certificacionService.obtenerActivos(params)

        respond tramites
    }

    /**
     * Permite mandar a llamar el servicio para consultar un pago en específico
     * @return resultado
     * resultado del mensaje, estatus y datos del tramite consultado y lo muestra en la vista
     */

    def consultar(){
        def resultado = certificacionService.consultar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect action: "listar"
            return
        }

         [
            tramite: resultado.datos
         ]
    }

    /**
     * Permite llamar el servicio para obtener la lista de los registros activos, así como la consulta de los datos
     * guardados del registro del tramite para la realización de la modificación
     * @return datos del servicio requerido
     */

    def modificacion(){
        def resultado = certificacionService.consultar(params)
        def tramite = resultado.datos

        //Si se llama a este método desde el método 'modificar' por algún error
        //en los datos, se le asigna la información previamente modificada al objeto
        //para que el usuario no tenga que realizar todas las modificaciones nuevamente
        //y solo modifique el dato erroneo

        if(params.fechaPago){
            tramite.properties = params
            tramite.pago.properties = params
            tramite.pago.fechaPago = Date.parse("yyyy-MM-dd", params.fechaPago)
            tramite.pago.fechaRecepcion = Date.parse("yyyy-MM-dd", params.fechaRecepcion)

        }

        //Se define una variable con el formato de fecha y se le asigna al campo de fechaPago y fechaRecepcion
        def formatter = new SimpleDateFormat("yyyy-MM-dd")
        def fechaPago = formatter.format(tramite.pago.fechaPago)
        def fechaRecepcion = formatter.format(tramite.pago.fechaRecepcion)

        if(!resultado.estatus){
            redirect action: "listar"
            return
        }

        [
                fechaPago: fechaPago,
                fechaRecepcion: fechaRecepcion,
                tramite: tramite
        ]

    }

    /**
     * Permite llamar al servicio para generar la modificación del trámite seleccionado
     * @return resultado
     * resultado con el mensaje y estatus del trámite y lo muestra en la vista
     */
    def modificar(){
        def resultado = certificacionService.modificar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.mensaje){
            redirect action: "listar"
            return
        }
        redirect(action: "modificacion", params:params)
    }

    /**
     * Permite llamar al servicio para generar la eliminación de un trámite seleccionado
     * @return resultado
     * resultado con el mensaje y estutus del trámite eliminado y lo muestra en la vista
     */

    def eliminar(){
        def resultado = certificacionService.eliminar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect action: "listar"

        return
    }
}
