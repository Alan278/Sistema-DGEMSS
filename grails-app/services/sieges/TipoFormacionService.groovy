package sieges

import grails.gorm.transactions.Transactional

@Transactional
class TipoFormacionService {

	// Id de los TIPOS
    final BASICA_ID = 1
    final PARAESCOLARES_ID = 2
    final PARA_EL_TRABAJO_ID = 3
    final PROPEDEUTICA_ID = 4
    final EXTRACURRICULARES_ID = 5

    def obtenerActivos() {
		def tiposFormacion = TipoFormacion.createCriteria().list {
			eq("activo", true)
		}
		return tiposFormacion
    }

	def obtener(id){
		def tipoFormacion = TipoFormacion.get(id)

		if(!tipoFormacion) return null
		if(!tipoFormacion.activo) return null

		return tipoFormacion
	}
}
