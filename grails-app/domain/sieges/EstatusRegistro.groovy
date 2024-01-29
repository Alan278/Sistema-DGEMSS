package sieges

/**
 * Clase de modelo de estatus_certificado
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */
class EstatusRegistro {
    Integer id
    String nombre
    Boolean activo = true
    Date fechaRegistro = new Date()
    Date ultimaActualizacion

    static constraints = {
        nombre nullable: false, maxSize: 50, blank: false
        activo nullable: false
        fechaRegistro nullable: false
        ultimaActualizacion nullable: true
    }

    static mapping = {
        version false
    }
}
