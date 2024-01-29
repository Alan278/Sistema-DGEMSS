package sieges

class OpcTitulacion {

    Integer id
	String nombre
	Boolean activo = true
	Date fechaRegistro = new Date()
	Date ultimaActualizacion

	static hasMany = [
		actas: ActaProfesional,
        notificacion: NotificacionProfesional
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
