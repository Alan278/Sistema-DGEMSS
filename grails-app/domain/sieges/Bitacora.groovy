package sieges

/**
 * Asignatura Model class
 * @authors Luis Dominguez, Leslie Navez
 * @since 2021
 */

class Bitacora {
    Integer id
    String clase
    String metodo
    String nombre
    String descripcion
    String estatus
    Boolean activo = true
    Date fechaRegistro = new Date()
    Date ultimaActualizacion

    static belongsTo = [
		usuario: Usuario
	]

    static constraints = {
        clase nullable: false, maxSize: 128, blank: false
        metodo nullable: false, maxSize: 128, blank: false
        nombre nullable: false, maxSize: 1024, blank: false
        descripcion nullable: true, maxSize: 4096
        estatus nullable: false, maxSize: 24, blank: false
        usuario nullable: false
        activo nullable: false
        fechaRegistro nullable: false
        ultimaActualizacion nullable: true
    }

    static mapping = {
        version false
    }

}
