package sieges

class Domicilio {
	Integer id
	String estado
	String municipio
	String localidad
	String asentamiento
	String codigoPostal
	String calle
	String numeroExterior
	String numeroInterior
	String referencias
	String latitud
	String longitud
	Boolean activo = true
	Date fechaRegistro = new Date()
	Date ultimaActualizacion

	static hasMany = [
		institucion: Institucion
	]

	static constraints = {
		estado nullable: false, maxSize: 128, blank: false
		municipio nullable: false, maxSize: 128, blank: false
		localidad nullable: false, maxSize: 128, blank: false
		asentamiento nullable: false, maxSize: 128, blank: false
		codigoPostal nullable: false, maxSize: 8, blank: false
		calle nullable: false, maxSize: 128, blank: false
		numeroExterior nullable: true, maxSize: 32
		numeroInterior nullable: true, maxSize: 32
		referencias nullable: true, maxSize: 1024
		latitud nullable: true, maxSize: 20
		longitud nullable: true, maxSize: 20
		activo nullable: false
		fechaRegistro nullable: false
		ultimaActualizacion nullable: true
	}

	static mapping = {
		version false
	}

	def equals(Domicilio obj){
		if(!estado.equals(obj.estado)) return false
		if(!municipio.equals(obj.municipio)) return false
		if(!localidad.equals(obj.localidad)) return false
		if(!asentamiento.equals(obj.asentamiento)) return false
		if(!codigoPostal.equals(obj.codigoPostal)) return false
		if(!calle.equals(obj.calle)) return false
		if(!numeroExterior.equals(obj.numeroExterior)) return false
		if(!numeroInterior.equals(obj.numeroInterior)) return false
		if(!referencias.equals(obj.referencias)) return false
		if(!latitud.equals(obj.latitud)) return false
		if(!longitud.equals(obj.longitud)) return false
		if(!activo == obj.activo) return false
		return true
	}
}
