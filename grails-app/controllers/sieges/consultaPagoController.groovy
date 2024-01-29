package sieges

class consultaPagoController {
    /**
     * Inyección del consultaPagoService que contiene métodos para la gestión de pagos
     */
    def consultaPagoService

    def validarPago(){
        respond consultaPagoService.validarPago(params.serie, params.folio)
    }
}