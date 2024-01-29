package sieges

/**
 * AlumnoCicloEscolar Model class
 * @authors Dominguez Luis, Navez Leslie
 * @since 2021
 */

class AlumnoCicloEscolar {
	Integer id
	Boolean activo = true
	Date fechaRegistro = new Date()
	Date ultimaActualizacion

	Integer numEvaluaciones

	static belongsTo = [
		alumno: Alumno,
        cicloEscolar: CicloEscolar,
		estatusRegistro: EstatusRegistro,
	]

	static constraints = {
		alumno nullable: false
		cicloEscolar nullable: false
		ultimaActualizacion nullable: true
		estatusRegistro nullable: true
	}

	static mapping = {
		version false
		numEvaluaciones formula: '''(SELECT COUNT(*) FROM alumno_ciclo_escolar
									INNER JOIN evaluacion ON evaluacion.ciclo_escolar_id = alumno_ciclo_escolar.ciclo_escolar_id
									WHERE
									evaluacion.activo = true and
									alumno_ciclo_escolar.id = id)'''
	}
}
