package sieges

/**
 * Clase de modelo de tipoEvaluacion
 * @author Luis Dominguez
 * @since 2022
 */
class TipoEvaluacion {
    Integer id
    String nombre
    String abreviatura

    Boolean activo = true
    Date dateCreated
    Date lastUpdated

    static hasMany = [
        evaluaciones: Evaluacion
    ]

    static constraints = {
        nombre nullable: false, blank: false, maxSize: 128
        abreviatura nullable: false, blank: false, maxSize: 10
    }

    static mapping = {
        version false
        id generator: 'assigned'
    }
}


