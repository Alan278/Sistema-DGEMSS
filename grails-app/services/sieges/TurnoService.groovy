package sieges

/**
 * TurnoService para el registro del turno en el Plan de estudios
 * @authors Dominguez Luis, Navez Leslie
 * @since 2021
 */

class TurnoService {

/**
 * Permite listar los turnos que se encuentran registrados, además de generar la busqueda de turnos por medio del nombre.
 * @param params
 * @param nombre (requerido)
 * El nombre del turno
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

        def turnos = Turno.createCriteria().list(params,criteria)

        if(turnos.totalCount <= 0){
            resultado.mensaje = 'No se encontraron turnos'
            resultado.datos = turnos
            return resultado
        }
        resultado.estatus = true
        resultado.mensaje = 'Turnos consultados exitosamente'
        resultado.datos = turnos

        return resultado
    }
/**
 * Permite mostrar la lista de los turnos registrados que se encuentran activos
 * @param nombre (requerido)
 * nombre del turno
 * @return estatus, mensaje y datos del turno
 */
    def obtenerActivos(params){
        def resultado = [
                estatus: false,
                mensaje: '',
                datos: null
        ]
        def turnos = Turno.createCriteria().list {
            eq("activo", true)
        }

        resultado.estatus = true
        resultado.mensaje = 'Turnos consultados exitosamente'
        resultado.datos = turnos

        return resultado
    }

/**
 * Permite mostrar el turno consultado con su información correspondiente
 * @param id (requerido)
 * Id del turno
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

        def turno = Turno.get(params.id)

        if(!turno){
            resultado.mensaje = 'Turno no encontrado'
            return resultado
        }

        if(!turno.activo){
            resultado.mensaje = 'Turno inactivo'
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'Turno encontrado exitosamente'
        resultado.datos = turno

        return resultado
    }

    def obtener(id){
		def turno = Turno.get(id)

		if(!turno) return null
		if(!turno.activo) return null

		return turno
	}

    def obtenerPorNombre(nombre){
		def turno = Turno.findByNombre(nombre)

		if(!turno) return null
		if(!turno.activo) return null

		return turno
	}
}
