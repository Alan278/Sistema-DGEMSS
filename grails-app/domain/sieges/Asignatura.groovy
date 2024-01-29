package sieges

/**
 * Asignatura Model class
 * @authors Dominguez Luis, Navez Leslie
 * @since 2021
 */

class Asignatura {
	Integer id
	Integer orden
	String periodo
	String nombre
	String clave
	Integer horas
	Integer creditos
	Boolean activo = true
	Date fechaRegistro = new Date()
	Date ultimaActualizacion

	static belongsTo = [
		planEstudios: PlanEstudios,
		formacion: Formacion
	]

	static hasMany = [
		evaluaciones: Evaluacion
	]

	static constraints = {
		orden nullable: true
		nombre nullable: false, maxSize: 512, blank: false
		clave nullable: true, maxSize: 16
		periodo nullable: false, blank: false, maxSize: 11
		horas nullable: true
		activo nullable: false
		fechaRegistro nullable: false
		ultimaActualizacion nullable: true
		planEstudios nullable: false
		formacion nullable: true
		creditos nullable: true
	}

	static mapping = {
		version false
	}

	def equals(Asignatura obj){
		if(!nombre.equals(obj.nombre)) return false
		if(!clave.equals(obj.clave)) return false
		if(!periodo.equals(obj.periodo)) return false
		if(!(horas == obj.horas)) return false
		if(!(activo == obj.activo)) return false
		if(!(planEstudios.id == obj.planEstudios.id)) return false
		return true
	}
}
