package sieges

/**
 * AsignaturasEquivalencia Model class
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */

class AsignaturasEquivalencia {
    Integer id
    Integer numPrograma
    String asignaturaCursada
    String asignaturaEquivalente
    Float calificacion
    Boolean activo = true
    Date fechaRegistro = new Date()
    Date ultimaActualizacion

    static belongsTo = [
        equivalencia: Equivalencia
    ]

    static constraints = {
        numPrograma nullable:true
        asignaturaCursada nullable:false, blank:false
        asignaturaEquivalente nullable:false, blank:false
        calificacion nullable:false
        ultimaActualizacion nullable: true
    }

    static mapping = {
        version false
    }
}
