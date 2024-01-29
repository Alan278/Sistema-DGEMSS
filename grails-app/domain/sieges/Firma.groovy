package sieges

/**
 * Certificado Model class
 * @authors Dominguez Luis, Navez Leslie
 * @since 2022
 */

class Firma {
	Integer id
	String selloDigital
	Date fechaFirma
	Boolean activo = true
	Date fechaRegistro = new Date()
	Date ultimaActualizacion = new Date()

	String fechaFirmaFormato

	static belongsTo = [
		firmaElectronica: FirmaElectronica,
	]

	static constraints = {
	}

	static mapping = {
		version false
		selloDigital sqlType: "TEXT"
		fechaFirma sqlType: "DATETIME"
		fechaFirmaFormato formula: "date_format(fecha_firma, '%d/%m/%Y')"
	}
}
