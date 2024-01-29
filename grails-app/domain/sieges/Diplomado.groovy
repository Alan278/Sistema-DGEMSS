package sieges

/**
 * Clase de modelo de diplomado
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */
class Diplomado {
    Integer id
    String nombre
    Boolean activo = true
    Date fechaRegistro = new Date()
    Date ultimaActualizacion

    static belongsTo = [
            institucion: Institucion
    ]

    static hasMany = [
            alumnos: Alumno
    ]

    static constraints = {
        nombre nullable: false, maxSize: 128, blank: false
        activo nullable: false
        fechaRegistro nullable: false
        ultimaActualizacion nullable: true
        institucion nullable: false
    }

    static mapping = {
        version false
    }

    def equals(Diplomado obj){
        if(!nombre.equals(obj.nombre)) return false
        if(!activo == obj.activo) return false
        if(!institucion.id == obj.institucion.id) return false
        return true
    }
}
