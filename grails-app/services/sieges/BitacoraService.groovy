package sieges

import grails.gorm.transactions.Transactional


/**
 * Servicio que permite la administración de la bitacora
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */

@Transactional
class BitacoraService {
    /**
     * Inyección de messageSource que contiene mensajes de validaciones para los atributos de cada dominio
     * Los mensajes de validación se encuentran en: grails-app/i18n/messages_es.properties
     */
    def messageSource
    def usuarioService

    /**
     * Permite realizar el registro de la bitacora
     * @param nombre (Requerido)
     * @param clase (Requerido)
     * @param metodo (Requerido)
     * @param estatus (Requerido)
     * @return resultado
     * resultado con el estatus, mensaje y datos de la bitacora
     */
    def registrar(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def bitacora = new Bitacora(params)
        bitacora.usuario = usuarioService.obtenerUsuarioLogueado()

        if(bitacora.save(flush:true)){
            resultado.estatus = true
        }else{
            // Se recorren los errores de validación y se asignan a la propiedad resultado.mensaje
            bitacora.errors.allErrors.each {
                resultado.mensaje = messageSource.getMessage(it, null)
            }
        }

        if(resultado.estatus){
            resultado.mensaje = 'Bitacora creada exitosamente'
            resultado.datos = bitacora
        }

        return resultado
    }

    /**
     * Permite listar las bitacoras que se encuentran registradas, además de generar la busqueda por medio
     del nombre.
     * @param search (Opcional)
     * Nombre de la bitacota
     * @return resultado
     * resultado con el estatus, mensaje y datos de la bitacora
     */
    def listar(params){
        def resultado = [
                estatus: false,
                mensaje: '',
                datos: null
        ]

        if(!params.max) params.max = 250
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

        def bitacoras = Bitacora.createCriteria().list(params,criteria)

        if(bitacoras.totalCount <= 0){
            resultado.mensaje = 'No se encontraron bitacoras'
            resultado.datos = bitacoras
            return resultado
        }
        resultado.estatus = true
        resultado.mensaje = 'Bitacoras consultadas exitosamente'
        resultado.datos = bitacoras

        return resultado
    }

    /**
     * Permite mostrar la bitacora seleccionada con su información correspondiente
     * @param id (Requerido)
     * Id de la bitacora
     * @return resultado
     * resultado con el estatus, mensaje y datos de la bitacora
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

        def bitacora = Bitacora.get(params.id)

        if(!bitacora){
            resultado.mensaje = 'Bitacora no encontrada'
            return resultado
        }

        if(!bitacora.activo){
            resultado.mensaje = 'Bitacora inactiva'
            return resultado
        }

        resultado.estatus = true
        resultado.mensaje = 'Bitacora encontrada exitosamente'
        resultado.datos = bitacora

        return resultado
    }
}
