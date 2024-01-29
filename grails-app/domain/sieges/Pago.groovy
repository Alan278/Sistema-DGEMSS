
package sieges

/**
 * Pago Model class
 * @authors Dominguez Luis, Navez Leslie
 * @since 2022
 */

class Pago {
    Integer id
    String folio
    String lineaCaptura
    String idConcepto
    String concepto
    Date fechaPago
    String horaPago
    Date fechaRecepcion
    Integer cantidad
    Float importe
    Boolean activo = true
    Date fechaRegistro = new Date()
    Date ultimaActualizacion

    String fechaPagoFormato
    String fechaRecepcionFormato

    static hasMany = [
        tramites: Tramite,
    ]

    static constraints = {
        folio nullable: false, blank: false
        lineaCaptura nullable: false, blank: false
        idConcepto nullable: false, maxSize: 10, blank: false
        concepto nullable: false, maxSize: 512, blank: false
        fechaPago nullable: false
        horaPago nullable: false
        fechaRecepcion nullable: false
        cantidad nullable: true, blank: true
        importe nullable: false, blank: false
        fechaRegistro nullable: false
        ultimaActualizacion nullable: true
    }


    static mapping = {
        version false
        fechaPago sqlType: "DATE"
        fechaRecepcion sqlType: "DATE"
        fechaPagoFormato formula: "date_format(fecha_pago, '%d/%m/%Y')"
        fechaRecepcionFormato formula: "date_format(fecha_recepcion, '%d/%m/%Y')"
    }

    def equals(Pago obj){
        if(!serie.equals(obj.serie)) return false
        if(!folio.equals(obj.folio)) return false
        if(!concepto.equals(obj.concepto)) return false
        if(!fechaPago.equals(obj.fechaPago)) return false
        if(!horaPago.equals(obj.horaPago)) return false
        if(!fechaRecepcion.equals(obj.fechaRecepcion)) return false
        if(!(cantidad == obj.cantidad)) return false
        if(!(importe == obj.importe)) return false
        return true
    }

}

