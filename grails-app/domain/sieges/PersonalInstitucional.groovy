package sieges

/**
 * Clase de modelo de personal institucional
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */
class PersonalInstitucional {
	Integer id
	String nombreCargo
	Boolean activo = true
	Date fechaRegistro = new Date()
	Date ultimaActualizacion

	static belongsTo = [
		persona: Persona,
		institucion: Institucion,
		cargoInstitucional: CargoInstitucional
	]

	static constraints = {
		nombreCargo nullable: false, maxSize: 512, blank: false
		activo nullable: false
		fechaRegistro nullable: false
		ultimaActualizacion nullable: true
		persona nullable: false
		institucion nullable: false
		cargoInstitucional nullable: false
	}

	static mapping = {
		version false
	}

	def equals(PersonalInstitucional obj){
		if(!nombreCargo.equals(obj.nombreCargo)) return false
		if(!(institucion.id == obj.institucion.id)) return false
		if(!(cargoInstitucional.id == obj.cargoInstitucional.id)) return false
		if(!(activo == obj.activo)) return false
		return true
	}
}
