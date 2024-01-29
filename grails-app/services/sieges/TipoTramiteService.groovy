package sieges

import grails.gorm.transactions.Transactional

@Transactional
class TipoTramiteService {
	def formatoService
	def bitacoraService

	// Id´s de los tipos de trámite
	final CERTIFICACION = 1
	final INSPECCION_VIGILANCIA = 2
	final NOTIFICACION = 3
	final CONSTANCIA = 4
	final ACTA = 5

    /**
     * Lista todos los tipos de tramites activos
     */
    def listar() {
        return TipoTramite.findAllByActivo(true)
    }

    def obtener(id){
        def tipoTramite = TipoTramite.get(id)

		if(!tipoTramite) return null
		if(!tipoTramite.activo) return null

		return tipoTramite
    }

    def modificar(params) {
        def resultado = [
			estatus: false,
			mensaje: '',
			datos: null
		]

		def datosBitacora = [
            clase: "TipoTramiteService",
            metodo: "modificar",
            nombre: "Modificación de tipo de trámite",
            descripcion: "Se modifica un tipo de trámite",
            estatus: "ERROR"
        ]

        // Se obtienen el registro a modificar
		def tipoTramite = obtener(params.id)
		if(!tipoTramite){
			resultado.mensaje = 'Tipo de trámite no encontrado'
			return resultado
		}

        // Se validan y formatean los datos recibidos
		def resultadoValidacion = validarDatos(params)
		if (!resultadoValidacion.estatus) {
			resultado.mensaje = resultadoValidacion.mensaje
            return resultado
        }

		// Se obtienen los datos con formato
		params = resultadoValidacion.datos

        // Se asignan los nuevos datos
        tipoTramite.nombre = params.nombre
        tipoTramite.idConcepto = params.idConcepto
        tipoTramite.costoUmas = params.umas

        if(!tipoTramite.save(flush: true)){
            bitacoraService.registrar(datosBitacora)
            resultado.mensaje = "Error al actualizar el tipo de trámite"
            return resultado
        }

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = "Tipo de trámite actualizado exitosamente"
		resultado.datos = tipoTramite

		return resultado
    }

    def validarDatos(params){
		def resultado = [
			estatus: false,
			mensaje: "",
			datos: null
		]

		def resultadoValidacion

		// Se validan los datos requeridos
		resultadoValidacion = validarDatosRequeridos(params)
		if(!resultadoValidacion.estatus){
			resultado.mensaje = resultadoValidacion.mensaje
			return resultado
		}

		// Se valida el formato de los datos
		resultadoValidacion = validarFormatoDatos(params)
		if(!resultadoValidacion.estatus){
			resultado.mensaje = resultadoValidacion.mensaje
			return resultado
		}

		// Se le da el formato requerido a los datos
		params = formatearDatos(params)

		resultado.datos = params
		resultado.estatus = true
		return resultado
	}

	def validarDatosRequeridos(params){
		def resultado = [
			estatus: false,
			mensaje: ""
		]

		if(!params.nombre){
			resultado.mensaje = "El nombre es un dato requerido"
            return resultado
		}

		if(!params.umas){
            resultado.mensaje = "El costo en UMAS es un dato requerido"
            return resultado
        }

		resultado.estatus = true
		return resultado
	}

	def validarFormatoDatos(params){
		def resultado = [
			estatus: false,
			mensaje: ""
		]

		if(!formatoService.isPositiveNumeric(params.umas)){
			resultado.mensaje = "El costo en UMAS debe de ser de tipo numérico"
			return resultado
        }

		resultado.estatus = true
		return resultado
	}

	def formatearDatos(params){
		params.nombre = formatoService.toFlatString(params.nombre)
		params.umas = params.umas.trim().toFloat()

		return params
	}
}
