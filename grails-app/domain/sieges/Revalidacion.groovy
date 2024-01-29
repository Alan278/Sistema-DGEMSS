package sieges

/**
 * Revalidación Model class
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */

class Revalidacion {
    Integer id
    String alumno
    String nivelExterno
    String nivelInterno
    String institucion
    String cicloEscolar
    String estado
    String pais
    String expediente
    String lugarExpedicion
    String folio
    Date fechaTermino
    Date fechaExpedicion
    byte[] pdf
    Boolean activo = true
    Date fechaRegistro = new Date()
    Date ultimaActualizacion

    String fechaExpedicionFormato
    String fechaTerminoFormato

    static constraints = {
        alumno nullable:false, blank:false
        nivelExterno nullable:false, blank:false
        nivelInterno nullable:false, blank:false
        institucion nullable:false, blank:false
        cicloEscolar nullable:false, blank:false
        estado nullable:false, blank:false
        pais nullable:false, blank:false
        expediente nullable:false, blank:false, unique: true
        lugarExpedicion nullable:false, blank:false
        folio nullable:false, blank:false
        fechaTermino nullable:false
        fechaExpedicion nullable:false
        ultimaActualizacion nullable: true
    }

    static mapping = {
        version false
		pdf sqlType: "MEDIUMBLOB"
		fechaExpedicion sqlType: "DATE"
        fechaTermino sqlType: "DATE"
		fechaExpedicionFormato formula: "date_format(fecha_expedicion, '%d/%m/%Y')"
        fechaTerminoFormato formula: "date_format(fecha_termino, '%d/%m/%Y')"
    }
}
