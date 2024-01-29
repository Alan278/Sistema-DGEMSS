package sieges

import grails.gorm.DetachedCriteria
import groovy.transform.ToString

import org.codehaus.groovy.util.HashCodeHelper
import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
@ToString(cache=true, includeNames=true, includePackage=false)
class UsuarioRol implements Serializable {

	private static final long serialVersionUID = 1

	Usuario usuario
	Rol rol

	@Override
	boolean equals(other) {
		if (other instanceof UsuarioRol) {
			other.usuarioId == usuario?.id && other.rolId == rol?.id
		}
	}

    @Override
	int hashCode() {
	    int hashCode = HashCodeHelper.initHash()
        if (usuario) {
            hashCode = HashCodeHelper.updateHash(hashCode, usuario.id)
		}
		if (rol) {
		    hashCode = HashCodeHelper.updateHash(hashCode, rol.id)
		}
		hashCode
	}

	static UsuarioRol get(long usuarioId, long rolId) {
		criteriaFor(usuarioId, rolId).get()
	}

	static boolean exists(long usuarioId, long rolId) {
		criteriaFor(usuarioId, rolId).count()
	}

	private static DetachedCriteria criteriaFor(long usuarioId, long rolId) {
		UsuarioRol.where {
			usuario == Usuario.load(usuarioId) &&
			rol == Rol.load(rolId)
		}
	}

	static UsuarioRol create(Usuario usuario, Rol rol, boolean flush = false) {
		def instance = new UsuarioRol(usuario: usuario, rol: rol)
		instance.save(flush: flush)
		instance
	}

	static boolean remove(Usuario u, Rol r) {
		if (u != null && r != null) {
			UsuarioRol.where { usuario == u && rol == r }.deleteAll()
		}
	}

	static int removeAll(Usuario u) {
		u == null ? 0 : UsuarioRol.where { usuario == u }.deleteAll() as int
	}

	static int removeAll(Rol r) {
		r == null ? 0 : UsuarioRol.where { rol == r }.deleteAll() as int
	}

	static constraints = {
	    usuario nullable: false
		rol nullable: false, validator: { Rol r, UsuarioRol ur ->
			if (ur.usuario?.id) {
				if (UsuarioRol.exists(ur.usuario.id, r.id)) {
				    return ['userRole.exists']
				}
			}
		}
	}

	static mapping = {
		id composite: ['usuario', 'rol']
		version false
	}
}
