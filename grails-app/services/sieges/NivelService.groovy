package sieges

import grails.gorm.transactions.Transactional

/**
 * NivelService para el registro del nivel en la carrera
 * @authors Dominguez Luis, Navez Leslie
 * @since 2021
 */

@Transactional
class NivelService {
    def usuarioService

/**
 * Permite listar los niveles que se encuentran registrados, además de generar la busqueda de niveles por medio del nombre.
 * @param params
 * parametros del nivel
 * @param nombre (requerido)
 * El nombre del nivel
 * @return el estatus del resultado
 */
    def listar(params){
        def resultado = [
                estatus: false,
                mensaje: '',
                datos: null
        ]

        if(!params.max) params.max = 3
        if(!params.offset) params.offset = 0
        if(!params.sort) params.sort = 'id'
        if(!params.order) params.order = 'asc'

        def criteria = {
            and{
                eq("activo", true)
                if(params.search){
                    ilike("nombre", "%${params.search}%")
                }
            }
        }

        def niveles = Nivel.createCriteria().list(params,criteria)

        if(niveles.totalCount <= 0){
            resultado.mensaje = 'No se encontraron niveles'
            resultado.datos = niveles
            return resultado
        }
        resultado.estatus = true
        resultado.mensaje = 'Niveles consultados exitosamente'
        resultado.datos = niveles

        return resultado
    }
/**
 * Permite mostrar la lista de los niveles registrados que se encuentran activos
 * @param params
 * parametros del nivel
 * @return estatus del resultado
 */
    def obtenerActivos(params){
        def resultado = [
                estatus: false,
                mensaje: '',
                datos: null
        ]

        params.niveles = usuarioService.obtenerNivelesPorRol()

        def niveles = Nivel.createCriteria().list {
            and{
                eq("activo", true)
                or{
					params.niveles.each{ registro ->
						eq("id", registro)
					}
				}
            }
        }


        resultado.estatus = true
        resultado.mensaje = 'Niveles consultados exitosamente'
        resultado.datos = niveles

        return resultado
    }

/**
 * Permite mostrar el nivel consultado con su información correspondiente
 * @param id (requerido)
 * Id del nivel
 * @return estatus del resultado
 */
    def consultar(params) {
        def resultado = [
                estatus: false,
                mensaje: '',
                datos: null
        ]

        if(!params.id){
            resultado.mensaje = 'El id es un dato requerido'
            return resultado
        }

        def nivel = Nivel.get(params.id)

        if(!nivel){
            resultado.mensaje = 'Nivel no encontrado'
            return resultado
        }

        if(!nivel.activo){
            resultado.mensaje = 'Nivel inactivo'
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'Nivel encontrado exitosamente'
        resultado.datos = nivel

        return resultado
    }

    def obtener(id){
		def nivel = Nivel.get(id)

		if(!nivel) return null
		if(!nivel.activo) return null

		return nivel
	}

    def obtenerPorNombre(nombre){
		def nivel = Nivel.findByNombre(nombre)

		if(!nivel) return null
		if(!nivel.activo) return null

		return nivel
	}
}
