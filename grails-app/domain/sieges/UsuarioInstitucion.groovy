package sieges

import grails.gorm.DetachedCriteria
import grails.compiler.GrailsCompileStatic

/**
 * Turno Model class
 * @authors Dominguez Luis, Navez Leslie
 * @since 2021
 */
class UsuarioInstitucion {
    Integer id

    static belongsTo = [
		usuario: Usuario,
        institucion: Institucion
	]

    static mapping = {
        version false
    }

    static boolean exists(long usuarioId, long institucionId) {
		criteriaFor(usuarioId, institucionId).count()
	}

	private static DetachedCriteria criteriaFor(long usuarioId, long institucionId) {
		UsuarioInstitucion.where {
			usuario == Usuario.load(usuarioId) &&
			institucion == Institucion.load(institucionId)
		}
	}

	static UsuarioInstitucion create(Usuario usuario, Institucion institucion, boolean flush = false) {
		def instance = new UsuarioInstitucion(usuario: usuario, institucion: institucion)
		instance.save(flush: flush)
		instance
	}

	static int removeAll(Usuario u) {
		u == null ? 0 : UsuarioInstitucion.where { usuario == u }.deleteAll() as int
	}
}
