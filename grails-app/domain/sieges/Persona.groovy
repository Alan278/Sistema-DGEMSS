package sieges

class Persona {
	Integer id
	String nombre
	String primerApellido
	String segundoApellido
	String curp
	String rfc
	String sexo
	String entidadNacimiento
	String fechaNacimiento
	String correoElectronico
	String telefono
	String titulo
	Boolean activo = true
	Date fechaRegistro = new Date()
	Date ultimaActualizacion

	String nombreCompleto

	static belongsTo = [
		domicilio: Domicilio
	]

	static hasOne = [
		usuario: Usuario
	]

	static hasMany = [
		personalInstitucional: PersonalInstitucional,
		alumnos: Alumno,
		firmasElectronicas: FirmaElectronica
	]

	static constraints = {
		nombre nullable: false, maxSize: 128, blank: false
		primerApellido nullable: false, maxSize: 128, blank: false
		segundoApellido nullable: true, maxSize: 128
		curp nullable: true, minSize: 18, maxSize: 18, unique: true
		rfc nullable: true, minSize: 13, maxSize: 13
		sexo nullable: false, blank: false
		entidadNacimiento nullable: false, blank: false
		fechaNacimiento nullable: false, blank: false
		correoElectronico nullable: true, email: true, maxSize: 64
		telefono nullable: true, minSize: 10, maxSize: 15, validator: {
				if(it != null){
					if (!it.matches("[+-]?\\d*(\\.\\d+)?")) return ['notNumber']
				}
			}
		activo nullable: false
		fechaRegistro nullable: false
		ultimaActualizacion nullable: true
		domicilio nullable: true
		titulo nullable: true, maxSize: 5

		usuario nullable: true
	}

	static mapping = {
		version false
		firmasElectronicas sort: "id"
		nombreCompleto formula: "CONCAT_WS(' ', nombre, primer_apellido, segundo_apellido)"
	}

	def equals(Persona obj){
		if(!nombre.equals(obj.nombre)) return false
		if(!primerApellido.equals(obj.primerApellido)) return false
		if(!segundoApellido.equals(obj.segundoApellido)) return false
		if(!curp.equals(obj.curp)) return false
		if(!rfc.equals(obj.rfc)) return false
		if(!correoElectronico.equals(obj.correoElectronico)) return false
		if(!telefono.equals(obj.telefono)) return false
		if(!(activo == obj.activo)) return false
		return true
	}
}
