package sieges

/**
 * Turno Model class
 * @authors Dominguez Luis, Navez Leslie
 * @since 2021
 */

class Turno {
    Integer id
    String nombre
    Boolean activo = true
    Date fechaRegistro = new Date()
    Date ultimaActualizacion

    static hasMany = [
            planesEstudio: PlanEstudios
    ]

    static constraints = {
        nombre nullable: false, maxSize: 128, blank: false
        activo nullable: false
        fechaRegistro nullable: false
        ultimaActualizacion nullable: true
    }

    static mapping = {
        version false
    }
}
