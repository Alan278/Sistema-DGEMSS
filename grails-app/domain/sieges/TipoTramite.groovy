package sieges

class TipoTramite {
    Integer id
    String nombre
    String descripcion
    String idConcepto
    Float costoUmas
    Boolean activo = true
    Date dateCreated
    Date lastUpdated

    static constraints = {
        nombre nullable: false, blank: false, maxSize: 50
        descripcion nullable: true, maxSize: 70
        activo nullable: false
        idConcepto nullable: true, maxSize: 50
        costoUmas nullable: true
    }

    static mapping = {
        version false
        id generator: 'assigned'
    }
}
