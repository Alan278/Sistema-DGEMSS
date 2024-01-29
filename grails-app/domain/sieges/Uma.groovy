package sieges

class Uma {
    Integer id
    Float valor
    Boolean activo = true
    Date dateCreated
    Date lastUpdated

    static constraints = {
    }

    static mapping = {
        version false
        id generator: 'assigned'
    }
}
