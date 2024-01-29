package sieges

/**
 * Tramite Model class
 * @authors Dominguez Luis, Navez Leslie
 * @since 2022
 */

class Tramite {
    Integer id
    String numeroTramite
    Boolean activo = true
    Date fechaRegistro = new Date()
    Date ultimaActualizacion

    static belongsTo = [
        pago: Pago,
        institucion: Institucion,
        tipoTramite: TipoTramite
    ]

    static hasMany = [
        certificados: Certificado,
        ciclosEscolares: CicloEscolar,
        constancias: ConstanciaServicio,
        notificacion: NotificacionProfesional,
        acta: ActaProfesional,
    ]

    static constraints = {
        numeroTramite nullable: false, blank: false, maxSize: 512
        fechaRegistro nullable: false
        ultimaActualizacion nullable: true
        institucion nullable: false
        pago nullable: false
        tipoTramite nullable: true
    }

    static mapping = {
        version false
    }

    def equals(Tramite obj){
        if(!numeroTramite.equals(obj.numeroTramite)) return false
        if(!(activo == obj.activo)) return false
        if(!(institucion.id == obj.institucion.id)) return false
        return true
    }

}
