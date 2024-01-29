package sieges

/**
 * Constancia Model class
 * @authors Alan Guevarin
 * @since 2022
 */

class ConstanciaServicio {
    Integer id
	Integer opc
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
		tramite: Tramite,
		firmaDirectorEscuela: Firma,
		firmaAutenticadorDgemss: Firma,
		estatusConstancia: EstatusConstancia,
	]

    static constraints = {
		uuid nullable:false, blank: false, unique: true
		opc nullable:false, blank: false
		folioControl nullable:false, blank: false, unique: true
        pdf nullable:true
		xml nullable:true
		foto nullable: false, blank: false
		comentarios nullable: true
        activo nullable: false, blank: false
		fechaRegistro nullable: false, blank: false
		ultimaActualizacion nullable: true

		alumno nullable: false
		estatusConstancia nullable:false
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
