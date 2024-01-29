package sieges

/**
 * Clase de modelo de intitución
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */
class Institucion {
	Integer id
	String nombre
	String nombreComercial
	String razonSocial
	String claveDgp
	String claveCt
	String correoElectronico
	String telefono
	byte[] logo
	Boolean externa = false
	Boolean activo = true
	Boolean publica = false
	Date fechaRegistro = new Date()
	Date ultimaActualizacion

	Integer numCertificados
	Integer numCarreras

	static belongsTo = [
		domicilio: Domicilio
	]

	static hasMany = [
		carreras: Carrera,
		usuarios: UsuarioInstitucion,
		tramites: Tramite
	]

	static constraints = {
		nombre nullable: false, blank: false, maxSize: 512
		nombreComercial nullable: true, maxSize: 512
		razonSocial nullable: true, maxSize: 512
		claveDgp nullable: true, maxSize: 16
		claveCt nullable: true, maxSize: 16
		correoElectronico nullable: true, email: true, maxSize: 64
		telefono nullable: true, minSize: 10, maxSize: 15, validator: {
				if(it != null){
					if (!it.matches("^([0-9]+\\d*)")) return ['notNumber']
				}
			}
		externa nullable: false, maxSize: 1
		activo nullable: false, maxSize: 1
		publica nullable: false, maxSize: 1
		logo nullable: true, blank: true
		fechaRegistro nullable: false
		ultimaActualizacion nullable: true
		domicilio nullable: true
	}

	static mapping = {
		version false
		logo sqlType: "MEDIUMBLOB"
		numCertificados formula: '''(SELECT count(i.id) from institucion i
									INNER JOIN carrera c ON c.institucion_id = i.id
									INNER JOIN plan_estudios p ON p.carrera_id = c.id
									INNER JOIN alumno a ON a.plan_estudios_id = p.id
									INNER JOIN certificado cer ON cer.alumno_id = a.id
									WHERE i.id = id
									AND cer.estatus_certificado_id = 9
									AND cer.activo = true)'''
		numCarreras formula: '''(SELECT count(i.id) from institucion i
									INNER JOIN carrera c ON c.institucion_id = i.id
									WHERE i.id = id
									AND c.activo = true)'''
	}

	def equals(Institucion obj){
		if(!nombre.equals(obj.nombre)) return false
		if(!nombreComercial.equals(obj.nombreComercial)) return false
		if(!razonSocial.equals(obj.razonSocial)) return false
		if(!claveDgp.equals(obj.claveDgp)) return false
		if(!claveCt.equals(obj.claveCt)) return false
		if(!correoElectronico.equals(obj.correoElectronico)) return false
		if(!telefono.equals(obj.telefono)) return false
		if(!(activo == obj.activo)) return false
		return true
	}
}
