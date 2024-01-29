package sieges

/**
 * Acta Profesional Model class
 * @authors Alan Guevarin
 * @since 2022
 */

class ActaProfesional {
    Integer id
    String uuid
	Integer libro
	Integer foja
	Integer numero
	String folioControl
	String presidente
	String secretario
	String vocal
	String municipio
    byte[] pdf
	byte[] xml
	byte[] foto
	byte[] archivopdf
    String titulo
	String comentarios
	Boolean duplicado = false
    Boolean activo = true
	Date fechaRegistro = new Date()
	Date ultimaActualizacion

    static belongsTo = [
		alumno: Alumno, 
		tramite: Tramite,
		firmaDirectorEscuela: Firma,
		firmaVocalEscuela: Firma,
		firmaPresidenteEscuela: Firma,
		firmaSecretarioEscuela: Firma,
		firmaAutenticadorDgemss: Firma,
		estatusActa: EstatusActa,
		opctitulacion: OpcTitulacion,
		doc: TipoDocumento,
		declaracion: Declaracion,

	]
    static constraints = {
		libro nullable: true
		foja nullable: true
		numero nullable: true
		uuid nullable:false, blank: false, unique: true
		folioControl nullable:false, blank: false, unique: true
        pdf nullable:true
		xml nullable:true
		municipio nullable: true
		foto nullable: false, blank: false
		archivopdf nullable: false, blank: false
		comentarios nullable: true
        opctitulacion nullable:false, blank: false
        titulo nullable:true
		doc nullable:false, blank: false
		declaracion nullable:false, blank: false
        activo nullable: false, blank: false
		fechaRegistro nullable: false, blank: false
		ultimaActualizacion nullable: true

		alumno nullable: false
		estatusActa nullable:false
		tramite nullable: true
		firmaDirectorEscuela nullable:true
		firmaVocalEscuela nullable:true
		firmaPresidenteEscuela nullable:true
		firmaSecretarioEscuela nullable:true
		firmaAutenticadorDgemss nullable:true

    }
    static mapping = {
		version false
		pdf sqlType: "MEDIUMBLOB" 
		xml sqlType: "MEDIUMBLOB"
		foto sqlType: "MEDIUMBLOB"
		archivopdf sqlType: "MEDIUMBLOB"
    }
}

