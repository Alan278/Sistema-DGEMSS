package sieges

import grails.gorm.transactions.Transactional

/**
 * RolService para el registro del rol en los usuarios
 * @authors Dominguez Luis, Navez Leslie
 * @since 2022
 */

@Transactional
class RolService{

/**
 * Permite listar los roles que se encuentran registrados, además de generar la busqueda de roles por medio del nombre.
 * @param params
 * parametros del rol
 * @param nombre (requerido)
 * El nombre del rol
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
                if(params.search){
                    ilike("nombre", "%${params.search}%")
                }
            }
        }

        def roles = Rol.createCriteria().list(params,criteria)

        if(roles.totalCount <= 0){
            resultado.mensaje = 'No se encontraron roles'
            resultado.datos = roles
            return resultado
        }
        resultado.estatus = true
        resultado.mensaje = 'Roles consultados exitosamente'
        resultado.datos = roles

        return resultado
    }
/**
 * Permite mostrar la lista de los roles registrados que se encuentran activos
 * @param params
 * parametros del rol
 * @return estatus del resultado
 */
    def obtenerActivos(params){
        def resultado = [
                estatus: false,
                mensaje: '',
                datos: null
        ]

        def roles = Rol.createCriteria().list{
            order("nombre", "asc")
        }


        resultado.estatus = true
        resultado.mensaje = 'Roles consultados exitosamente'
        resultado.datos = roles

        return resultado
    }

/**
 * Permite mostrar el rol consultado con su información correspondiente
 * @param id (requerido)
 * Id del rol
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

        def rol = Rol.get(params.id)

        if(!rol){
            resultado.mensaje = 'Rol no encontrado'
            return resultado
        }

        if(!rol.activo){
            resultado.mensaje = 'Rol inactivo'
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'Rol encontrado exitosamente'
        resultado.datos = rol

        return resultado
    }
}
