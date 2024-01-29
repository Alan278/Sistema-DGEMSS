package sieges

/**
 * Asignatura Model class
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */

class Reporte {
    Integer id
    String nombre
    String consultaSql
    Boolean activo = true
    Date fechaRegistro = new Date()
    Date ultimaActualizacion

    static constraints = {
        nombre nullable: false, maxSize: 512, blank: false
        consultaSql nullable: false, maxSize: 512, blank: false
        activo nullable: false
        fechaRegistro nullable: false
        ultimaActualizacion nullable: true
    }

    static mapping = {
        version false
    }

    def equals(Reporte obj){
        if(!nombre.equals(obj.nombre)) return false
        if(!consultaSql.equals(obj.consultaSql)) return false
        if(!(activo == obj.activo)) return false
        return true
    }
}
