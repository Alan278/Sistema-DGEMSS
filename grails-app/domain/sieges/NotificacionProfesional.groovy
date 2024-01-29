package sieges

/**
 * Notificacion Model class
 * @authors Alan Guevarin
 * @since 2022
 */
class NotificacionProfesional {

    Integer id
    String uuid
	String folioControl
	String municipio
    byte[] pdf
	byte[] xml
	byte[] foto
    String titulo
    String presidente
    String vocal
    String secretario
	String comentarios
	Boolean duplicado = false
    Boolean activo = true
	Date fechaRegistro = new Date()
	Date ultimaActualizacion

    static belongsTo = [
		alumno: Alumno, 
		tramite: Tramite,
		firmaDirectorEscuela: Firma,
		firmaAutenticadorDgemss: Firma,
		estatusNotificacion: EstatusNotificacion,
		opctitulacion: OpcTitulacion,
		doc: TipoDocumento,
	]
    static constraints = {
		uuid nullable:false, blank: false, unique: true
		folioControl nullable:false, blank: false, unique: true
        pdf nullable:true
		xml nullable:true
		municipio nullable: true
		foto nullable: false, blank: false
		comentarios nullable: true
        titulo nullable:false, blank: false
        presidente nullable:false, blank: false
        presidente nullable:false, blank: false
		doc nullable:false, blank: false
        activo nullable: false, blank: false
		fechaRegistro nullable: false, blank: false
		ultimaActualizacion nullable: true

		alumno nullable: false
		estatusNotificacion nullable:false
		opctitulacion nullable:false
		tramite nullable: true
		firmaDirectorEscuela nullable:true
		firmaAutenticadorDgemss nullable:true

    }
    static mapping = {
		version false
		pdf sqlType: "MEDIUMBLOB" 
		xml sqlType: "MEDIUMBLOB"
		foto sqlType: "MEDIUMBLOB"
	}
}
