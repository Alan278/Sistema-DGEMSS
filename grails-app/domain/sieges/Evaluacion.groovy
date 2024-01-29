package sieges

/**
 * Clase de modelo de evaluación
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */
class Evaluacion {
	Integer id
	Float calificacion
	Boolean aprobada
	Date fechaAcreditacion
	Boolean activo = true

    Date dateCreated
    Date lastUpdated

	String fechaAcreditacionFormato

	static belongsTo = [
		alumno: Alumno,
		asignatura: Asignatura,
		cicloEscolar: CicloEscolar,
		estatusRegistro: EstatusRegistro,
		tipoEvaluacion: TipoEvaluacion
	]

	static constraints = {
		calificacion nullable: true
		aprobada nullable: true
		activo nullable: false
		estatusRegistro nullable: true
		tipoEvaluacion nullable: true
		cicloEscolar nullable: true
		dateCreated nullable: true
		lastUpdated nullable: true
		fechaAcreditacion nullable: true
	}

	static mapping = {
		version false
		fechaAcreditacion sqlType: "DATE"
		fechaAcreditacionFormato formula: "date_format(fecha_acreditacion, '%d/%m/%Y')"
	}
}
