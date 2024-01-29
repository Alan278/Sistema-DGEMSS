package sieges

/**
 * Clase de dominio del Formación
 * @authors Dominguez Luis, Navez Leslie
 * @since 2022
 */

class Formacion {
    Integer id
    String nombre
    Boolean requerida = true
    Boolean general = true
    Boolean activo = true

    Date dateCreated
    Date lastUpdated

    static hasMany = [
        asignaturas: Asignatura
    ]

    static belongsTo = [
        tipoFormacion: TipoFormacion,
        planEstudios: PlanEstudios
    ]

    static constraints = {
        nombre nullable: false, maxSize: 128, blank: false
        activo nullable: false
        planEstudios nullable: true
        requerida nullable: true
        general nullable: true
        dateCreated nullable: true
        lastUpdated nullable: true
    }

    static mapping = {
        version false
    }


}
