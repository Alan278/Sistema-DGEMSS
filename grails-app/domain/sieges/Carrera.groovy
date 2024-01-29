package sieges

/**
 * Carrera Model class
 * @authors Dominguez Luis, Navez Leslie
 * @since 2021
 */

class Carrera {
	Integer id
	String claveSeem
	String nombre
	String claveDgp
	// String rvoe
	// Date fechaRvoe
	Boolean activo = true
	Date fechaRegistro = new Date()
	Date ultimaActualizacion

	// String fechaRvoeFormato
	Integer numCertificados
	Integer numPlanesEstudio

	static belongsTo = [
		institucion: Institucion,
		nivel: Nivel,
		modalidad: Modalidad,
		area: Area
	]

	static hasMany = [
		planesEstudio: PlanEstudios,
		// ciclosEscolares: CicloEscolar,
		// alumnos: Alumno
	]

	static constraints = {
		claveSeem nullable: true, maxSize: 16
		nombre nullable: false, maxSize: 512, blank: false
		claveDgp nullable: true, maxSize: 16
		// rvoe nullable: true, maxSize: 16
		// fechaRvoe nullable: true
		activo nullable: false
		fechaRegistro nullable: false
		ultimaActualizacion nullable: true
		institucion nullable: false
		nivel nullable: true
		modalidad nullable: true
		area nullable: true
	}

	static mapping = {
		version false
		// fechaRvoe sqlType: "DATE"
		// fechaRvoeFormato formula: "date_format(fecha_rvoe, '%d/%m/%Y')"
		numCertificados formula: '''(SELECT count(c.id) from carrera c
									INNER JOIN plan_estudios p ON p.carrera_id = c.id
									INNER JOIN alumno a ON a.plan_estudios_id = p.id
									INNER JOIN certificado cer ON cer.alumno_id = a.id
									WHERE c.id = id
									AND cer.estatus_certificado_id = 9
									AND cer.activo = true)'''
		numPlanesEstudio formula: '''(SELECT count(c.id) from carrera c
									INNER JOIN plan_estudios p ON p.carrera_id = c.id
									WHERE c.id = id
									AND p.activo = true)'''
	}

	def equals(Carrera obj){
		if(!nombre.equals(obj.nombre)) return false
		if(!claveSeem.equals(obj.claveSeem)) return false
		if(!claveDgp.equals(obj.claveDgp)) return false
		// if(!rvoe.equals(obj.rvoe)) return false
		// if(!fechaRvoe.equals(obj.fechaRvoe)) return false
		if(!(activo == obj.activo)) return false
		if(!(institucion.id == obj.institucion.id)) return false
		if(!nivel && obj.nivel) return false
		if( nivel && !obj.nivel) return false
		if(!nivel && !obj.nivel) return true
		if(!(nivel.id == obj.nivel.id)) return false
		if(!modalidad && obj.modalidad) return false
		if( modalidad && !obj.modalidad) return false
		if(!modalidad && !obj.modalidad) return true
		if(!(modalidad.id == obj.modalidad.id)) return false
		if(!area && obj.area) return false
		if( area && !obj.area) return false
		if(!area && !obj.area) return true
		if(!(area.id == obj.area.id)) return false
		return true
	}
}
