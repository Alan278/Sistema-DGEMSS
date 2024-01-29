package sieges

/**
 * Alumno Model class
 * @authors Dominguez Luis, Navez Leslie
 * @since 2021
 */

class Alumno {
	Integer id
	String matricula
	Boolean activo = true
	Date fechaRegistro = new Date()
	Date ultimaActualizacion

	Integer numCiclosEscolares
	Integer numEvaluaciones

	static belongsTo = [
		carrera: Carrera,
		persona: Persona,
		estatusRegistro: EstatusRegistro,
		estatusAlumno: EstatusAlumno,
		curso: Curso,
		diplomado: Diplomado,
		planEstudios: PlanEstudios,
		formacion: Formacion,
	]

	static hasMany = [
		evaluaciones: Evaluacion,
		ciclosEscolares: AlumnoCicloEscolar,
		certificados: Certificado,
		constancias: ConstanciaServicio,
		notificacion: NotificacionProfesional,
		actas: ActaProfesional,
	]

	static constraints = {
		matricula nullable: false, maxSize: 128, blank: false
		activo nullable: false
		fechaRegistro nullable: false
		ultimaActualizacion nullable: true
		curso nullable: true
		diplomado nullable: true
		carrera nullable: true
		estatusRegistro nullable: true
		planEstudios nullable: true
		formacion nullable: true
	}

	static mapping = {
		version false
		numCiclosEscolares formula: '''(SELECT COUNT(*) FROM alumno
										INNER JOIN alumno_ciclo_escolar ON alumno_ciclo_escolar.alumno_id = alumno.id
										WHERE alumno_ciclo_escolar.activo = true and
										alumno.id = id)'''
		numEvaluaciones formula: '''(SELECT COUNT(*) FROM alumno
										INNER JOIN evaluacion ON evaluacion.alumno_id = alumno.id
										WHERE
										evaluacion.activo = true and
										alumno.id = id)'''
	}

	def equals(Alumno obj){
		if(!matricula.equals(obj.matricula)) return false
		if(!(activo == obj.activo)) return false
		if(!(estatusAlumno.id == obj.estatusAlumno.id)) return false
		if(!curso && obj.curso) return false
		if(curso && !obj.curso) return false
		if(!curso && !obj.curso) return true
		if(!(curso.id == obj.curso.id)) return
		if(!diplomado && obj.diplomado) return false
		if(diplomado && !obj.diplomado) return false
		if(!diplomado && !obj.diplomado) return true
		if(!(diplomado.id == obj.diplomado.id)) return
		return true
	}
}
