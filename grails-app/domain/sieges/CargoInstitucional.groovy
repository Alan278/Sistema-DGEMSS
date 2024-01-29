package sieges

/**
 * Clase de modelo de cargo institucional
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */
class CargoInstitucional {
	Integer id
	String nombre
	Boolean activo = true
	Date fechaRegistro = new Date()
	Date ultimaActualizacion

	static hasMany = [
		personalInstitucional: PersonalInstitucional
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
