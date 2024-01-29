package sieges

/**
 * Clase de modelo de estatus_usuario
 * @author Luis Dominguez, Leslie Navez
 * @since 2022
 */
class EstatusUsuario {

    Integer id
    String nombre
    String descripcion
    Boolean activo = true
    Date dateCreated
    Date lastUpdated

    static constraints = {
        nombre nullable: false, blank: false, maxSize: 10
        descripcion nullable: false, blank: false, maxSize: 50
        activo nullable: false
    }

    static mapping = {
        version false
        id generator: 'assigned'
    }
}
