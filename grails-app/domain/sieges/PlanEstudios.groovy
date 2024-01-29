package sieges

/**
 * Clase de modelo de plan de estudios
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */
class PlanEstudios {
	Integer id
	String nombre
	Float calificacionMinima
	Float calificacionMinimaAprobatoria
	Float calificacionMaxima
	String horaInicio
	String horaFin
	Integer ciclos
	String rvoe
	Date fechaRvoe

	Boolean activo = true
	Date fechaRegistro = new Date()
	Date ultimaActualizacion

	String fechaRvoeFormato
	String fechaRvoeFormatoFormulario
	Integer numCertificados
	Integer numAsignaturas
	Integer numFormaciones
	Integer numFormacionesIndividuales

	static belongsTo = [
		carrera: Carrera,
		periodo: Periodo,
		turno: Turno
	]

	static hasMany = [
		asignaturas: Asignatura,
		alumnos: Alumno,
		ciclosEscolares: CicloEscolar,
		formaciones: Formacion,
	]

	static constraints = {
		nombre nullable: false, maxSize: 512, blank: false
		calificacionMinima nullable: true
		calificacionMinimaAprobatoria nullable: true
		calificacionMaxima nullable: true
		horaInicio nullable: true, maxSize: 6
		horaFin nullable: true, maxSize: 6
		ciclos nullable: true
		activo nullable: false
		fechaRegistro nullable: false
		ultimaActualizacion nullable: true
		carrera nullable: false
		periodo nullable: false
		turno nullable: true
		rvoe nullable: true, maxSize: 16
		fechaRvoe nullable: true
	}

	static mapping = {
		version false
		fechaRvoe sqlType: "DATE"
		fechaRvoeFormato formula: "date_format(fecha_rvoe, '%d/%m/%Y')"
		fechaRvoeFormatoFormulario formula: "date_format(fecha_rvoe, '%Y-%m-%d')"
		numCertificados formula: '''(SELECT count(p.id) from plan_estudios p
									INNER JOIN alumno a ON a.plan_estudios_id = p.id
									INNER JOIN certificado cer ON cer.alumno_id = a.id
									WHERE p.id = id
									AND cer.estatus_certificado_id = 9
									AND cer.activo = true)'''
		numAsignaturas formula: '''(SELECT count(p.id) from plan_estudios p
									INNER JOIN asignatura a ON a.plan_estudios_id = p.id
									WHERE p.id = id
									AND a.activo = true)'''
		numFormaciones formula: '''(SELECT count(p.id) from plan_estudios p
									INNER JOIN formacion f ON f.plan_estudios_id = p.id
									WHERE p.id = id
									AND f.activo = true)'''
		numFormacionesIndividuales formula: '''(SELECT count(p.id) from plan_estudios p
									INNER JOIN formacion f ON f.plan_estudios_id = p.id
									WHERE p.id = id
									AND f.general = false
									AND f.activo = true)'''
	}

	def equals(PlanEstudios obj){
		if(!nombre.equals(obj.nombre)) return false
		if(!calificacionMinima.equals(obj.calificacionMinima)) return false
		if(!calificacionMinimaAprobatoria.equals(obj.calificacionMinimaAprobatoria)) return false
		if(!calificacionMaxima.equals(obj.calificacionMaxima)) return false
		if(!horaInicio.equals(obj.horaInicio)) return false
		if(!horaFin.equals(obj.horaFin)) return false
		if(!(ciclos == obj.ciclos)) return false
		if(!(activo == obj.activo)) return false
		if(!(carrera.id == obj.carrera.id)) return false
		if(!(periodo.id == obj.periodo.id)) return false
		if(!turno && obj.turno) return false
		if( turno && !obj.turno) return false
		if(!turno && !obj.turno) return true
		if(!(turno.id == obj.turno.id)) return false
		return true
	}
}
