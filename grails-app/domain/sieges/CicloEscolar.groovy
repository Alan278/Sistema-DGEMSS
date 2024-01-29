package sieges

/**
 * CicloEscolar Model class
 * @authors Dominguez Luis, Navez Leslie
 * @since 2021
 */

class CicloEscolar {
	Integer id
	String nombre
	String periodo
	Date fechaInicio
	Date fechaFin
	Boolean activo = true
	Date fechaRegistro = new Date()
	Date ultimaActualizacion

	String inicio
	String fin

	Integer numAlumnos

	static belongsTo = [
		carrera: Carrera,
		planEstudios: PlanEstudios,
		estatusRegistro: EstatusRegistro,
		tramite: Tramite,
	]

	static hasMany = [
		alumnos: AlumnoCicloEscolar,
		evaluaciones: Evaluacion
	]

	static constraints = {
		nombre nullable: false, maxSize: 128, blank: false
		periodo nullable: false
		fechaInicio nullable: false
		fechaFin nullable: false
		activo nullable: false
		fechaRegistro nullable: false
		ultimaActualizacion nullable: true
		estatusRegistro nullable: true
		tramite nullable: true
		planEstudios nullable: true
		carrera nullable: true
	}

	static mapping = {
		version false
		fechaInicio sqlType: "DATE"
		fechaFin sqlType: "DATE"
		inicio formula: "date_format(fecha_inicio, '%d/%m/%Y')"
		fin formula: "date_format(fecha_fin, '%d/%m/%Y')"
		numAlumnos formula: '''(SELECT COUNT(*) FROM alumno
								INNER JOIN alumno_ciclo_escolar ON alumno_ciclo_escolar.alumno_id = alumno.id
								INNER JOIN ciclo_escolar ON alumno_ciclo_escolar.ciclo_escolar_id = ciclo_escolar.id
								WHERE
								ciclo_escolar.id = id AND
								alumno.activo = 1 AND
								alumno_ciclo_escolar.activo = 1 AND
								ciclo_escolar.activo = 1)'''
	}

	def equals(CicloEscolar obj){
		if(!nombre.equals(obj.nombre)) return false
		if(!periodo.equals(obj.periodo)) return false
		if(!fechaInicio.equals(obj.fechaInicio)) return false
		if(!fechaFin.equals(obj.fechaFin)) return false
		if(!(activo == obj.activo)) return false
		return true
	}
}
