package sieges

/**
 * PeriodoService para el registro del periodo en el Plan de estudios
 * @authors Dominguez Luis, Navez Leslie
 * @since 2021
 */

class PeriodoService {

/**
 * Permite listar los periodos que se encuentran registrados, además de generar la busqueda de periodos por medio del nombre.
 * @param params
 * parametros del periodo
 * @param nombre (requerido)
 * El nombre del periodo
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

        def periodos = Periodo.createCriteria().list(params,criteria)

        if(periodos.totalCount <= 0){
            resultado.mensaje = 'No se encontraron periodos'
            resultado.datos = periodos
            return resultado
        }
        resultado.estatus = true
        resultado.mensaje = 'Periodos consultados exitosamente'
        resultado.datos = periodos

        return resultado
    }

/**
 * Permite mostrar la lista de los periodos registrados que se encuentran activos
 * @param params
 * parametros del periodo
 * @return estatus del resultado
 */
    def obtenerActivos(params){
        def resultado = [
                estatus: false,
                mensaje: '',
                datos: null
        ]

        def periodos = Periodo.createCriteria().list {
            eq("activo", true)
        }


        resultado.estatus = true
        resultado.mensaje = 'Periodos consultados exitosamente'
        resultado.datos = periodos

        return resultado
    }
/**
 * Permite mostrar el periodo consultado con su información correspondiente
 * @param id (requerido)
 * Id del periodo
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

        def periodo = Periodo.get(params.id)

        if(!periodo){
            resultado.mensaje = 'Periodo no encontrado'
            return resultado
        }

        if(!periodo.activo){
            resultado.mensaje = 'Periodo inactivo'
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'Periodo encontrado exitosamente'
        resultado.datos = periodo

        return resultado
    }

    def obtener(id){
		def periodo = Periodo.get(id)

		if(!periodo) return null
		if(!periodo.activo) return null

		return periodo
	}

    def obtenerPorNombre(nombre){
		def periodo = Periodo.findByNombre(nombre)

		if(!periodo) return null
		if(!periodo.activo) return null

		return periodo
	}
}
