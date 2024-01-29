package sieges

import grails.gorm.transactions.Transactional

@Transactional
class UmaService {
    def formatoService
    def bitacoraService

    def modificar(params) {
        def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		def datosBitacora = [
            clase: "UmaService",
            metodo: "modificar",
            nombre: "Modificación de UMA",
            descripcion: "Se modifica el valor de la UMA",
            estatus: "ERROR"
        ]

        if(!params.valor){
            resultado.mensaje = "El valor de la UMA es un dato requerido"
            return resultado
        }
        if(!formatoService.isPositiveNumeric(params.valor)){
            resultado.mensaje = "El valor debe de ser de tipo numérico"
            return resultado
        }

        def uma = Uma.get(1)
        uma.valor = params.valor.toFloat()

        if(!uma.save(flush: true)){
            bitacoraService.registrar(datosBitacora)
            resultado.mensaje = "Error al actualizar el valor de la UMA"
            return resultado
        }

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = "UMA actualizada exitosamente"
		resultado.datos = uma

		return resultado
    }
}
