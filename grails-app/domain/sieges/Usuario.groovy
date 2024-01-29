package sieges

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
@EqualsAndHashCode(includes='username')
@ToString(includes='username', includeNames=true, includePackage=false)
class Usuario implements Serializable {

    private static final long serialVersionUID = 1

    String username
    String password
    boolean enabled = true
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired
    Boolean activo = true
    Date fechaRegistro = new Date()
    Date ultimaActualizacion
    String cargo
    String confirmToken

    static belongsTo = [
		persona: Persona,
        estatusUsuario: EstatusUsuario
	]

    static hasMany = [
        roles: UsuarioRol,
		instituciones: UsuarioInstitucion
	]

    Set<Rol> getAuthorities() {
        (UsuarioRol.findAllByUsuario(this) as List<UsuarioRol>)*.rol as Set<Rol>
    }

    static constraints = {
        password nullable: false, blank: false, password: true
        username nullable: false, blank: false, unique: true
        persona nullable: true
        activo nullable: false
        fechaRegistro nullable: false
		ultimaActualizacion nullable: true
        cargo nullable: false, blank: false
        confirmToken nullable: true
        estatusUsuario nullable: true
    }

    static mapping = {
	    password column: '`password`'
    }

    def equals(Usuario obj){
		if(!username.equals(obj.username)) return false
		if(!password.equals(obj.password)) return false
		if(!cargo.equals(obj.cargo)) return false
		if(!(activo == obj.activo)) return false
		return true
	}
}
