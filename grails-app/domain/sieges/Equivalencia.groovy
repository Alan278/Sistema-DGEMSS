package sieges

/**
 * Equivalencia Model class
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */

class Equivalencia {
    Integer id
    String nivelExterno
    String nivelInterno
    String institucion
    String cicloEscolar
    String expediente
    String lugarExpedicion
    String folio
    String cct
    Date fechaExpedicion
    byte[] pdf
    Boolean activo = true
    Date fechaRegistro = new Date()
    Date ultimaActualizacion

    String fechaExpedicionFormato

    static hasMany = [
        asignaturas: AsignaturasEquivalencia
    ]

    static constraints = {
        nivelExterno nullable:false, blank:false
        nivelInterno nullable:false, blank:false
        institucion nullable:false, blank:false
        cicloEscolar nullable:false, blank:false
        expediente nullable:false, blank:false, unique: true
        lugarExpedicion nullable:false, blank:false
        folio nullable:false, blank:false
        cct nullable:false, blank:false
        fechaExpedicion nullable:false
        ultimaActualizacion nullable: true
    }

    static mapping = {
        version false
        pdf sqlType: "MEDIUMBLOB"
        fechaExpedicion sqlType: "DATE"
        fechaExpedicionFormato formula: "date_format(fecha_expedicion, '%d/%m/%Y')"
    }
}
