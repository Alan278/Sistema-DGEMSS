package sieges

class FirmaElectronica {
	Integer id
    byte [] archivoCer
    byte [] archivoKey
    String nombreCer
    String curpCer
    String rfcCer
    String correoElectronicoCer
    Date validoDesdeCer
    Date validoHastaCer
    String numeroSerieCer

    static belongsTo = [
		persona: Persona
	]

    static constraints = {
        archivoCer nullable: false
        archivoKey nullable: true
        nombreCer nullable: false, maxSize: 256
        curpCer nullable: false, maxSize: 64
        rfcCer nullable: false, maxSize: 64
        correoElectronicoCer nullable: false, maxSize: 256
        validoDesdeCer nullable: false
        validoHastaCer nullable: false
        numeroSerieCer nullable: false, unique: true
    }

    static mapping = {
        version false
        archivoCer sqlType: "BLOB"
        archivoKey sqlType: "BLOB"
    }

    def expiro(){
        def today = new Date()

        if(validoHastaCer.after(today)) return false

        return true
    }
}
