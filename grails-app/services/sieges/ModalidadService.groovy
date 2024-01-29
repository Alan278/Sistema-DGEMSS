package sieges

import grails.gorm.transactions.Transactional

/**
 * Servicio que permite la administración de modalidades
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */
@Transactional
class ModalidadService {
    /**
     * Inyección de messageSource que contiene mensajes de validaciones para los atributos de cada dominio
     * Los mensajes de validación se encuentran en: grails-app/i18n/messages_es.properties
     */
    def messageSource

    /**
     * Obtiene las modalidades activas con parametros de paginación
     * @param search (Opcional)
     * Nombre del cargo
     */
    def listar(params){
        def resultado = [
                estatus: false,
                mensaje: '',
                datos: null
        ]

        // Parametros de paginación
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

        def modalidades = Modalidad.createCriteria().list(params,criteria)

        if(modalidades.totalCount <= 0){
            resultado.mensaje = 'No se encontraron modalidades'
            resultado.datos = modalidades
            return resultado
        }
        resultado.estatus = true
        resultado.mensaje = 'Modalidades consultadas exitosamente'
        resultado.datos = modalidades

        return resultado
    }

    /**
     * Obtiene las modalidades activas
     */
    def obtenerActivos(params){
        def resultado = [
                estatus: false,
                mensaje: '',
                datos: null
        ]

        def modalidades = Modalidad.createCriteria().list {
            eq("activo", true)
        }


        resultado.estatus = true
        resultado.mensaje = 'Modalidades consultadas exitosamente'
        resultado.datos = modalidades

        return resultado
    }

    /**
     * Obtiene una modalidad específica
     * @param id (Requerido)
     * Identificador de la modalidad
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

        def modalidad = Modalidad.get(params.id)

        if(!modalidad){
            resultado.mensaje = 'Modalidad no encontrada'
            return resultado
        }

        if(!modalidad.activo){
            resultado.mensaje = 'Modalidad inactiva'
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'Modalidad encontrada exitosamente'
        resultado.datos = modalidad

        return resultado
    }

    def obtener(id){
		def modalidad = Modalidad.get(id)

		if(!modalidad) return null
		if(!modalidad.activo) return null

		return modalidad
	}

    def obtenerPorNombre(nombre){
		def modalidad = Modalidad.findByNombre(nombre)

		if(!modalidad) return null
		if(!modalidad.activo) return null

		return modalidad
	}
}
