package sieges

/**
 * Certificado Model class
 * @authors Alan Guevarin
 * @since 2022
 */

class Certificado {
	Integer id
	Integer libro
	Integer foja
	Integer numero
	String municipio
	String uuid
	String folioControl
	byte[] pdf
	byte[] xml
	byte[] foto
	String comentarios
	Boolean duplicado = false
	Boolean activo = true
	Date fechaRegistro = new Date()
	Date ultimaActualizacion

	static belongsTo = [
		alumno: Alumno,
		estatusCertificado: EstatusCertificado,
		tramite: Tramite,
		firmaDirectorEscuela: Firma,
		firmaAutenticadorDgemss: Firma,
	]

	static constraints = {
		libro nullable: true
		foja nullable: true
		numero nullable: true
		municipio nullable: true
		uuid nullable:false, blank: false, unique: true
		folioControl nullable:false, blank: false, unique: true
		pdf nullable:true
		xml nullable:true
		foto nullable: false, blank: false
		comentarios nullable: true
		activo nullable: false, blank: false
		fechaRegistro nullable: false, blank: false
		ultimaActualizacion nullable: true

		alumno nullable: false
		estatusCertificado nullable:false
		tramite nullable: true
		firmaDirectorEscuela nullable:true
		firmaAutenticadorDgemss nullable:true
	}

	static mapping = {
		version false
		pdf sqlType: "MEDIUMBLOB"
		xml sqlType: "MEDIUMBLOB"
		foto sqlType: "MEDIUMBLOB"
		comentarios sqlType: "TEXT"
	}
}
