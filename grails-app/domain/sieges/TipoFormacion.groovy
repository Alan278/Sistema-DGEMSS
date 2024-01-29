package sieges

/**
 *Clase de dominio del Formación
 *@authors Dominguez Luis, Navez Leslie
 *@since 2022
 */

class TipoFormacion {
    Integer id
    String nombre
    Boolean activo = true
    Date fechaRegistro = new Date()
    Date ultimaActualizacion

    static hasMany = [
            formaciones: Formacion

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


