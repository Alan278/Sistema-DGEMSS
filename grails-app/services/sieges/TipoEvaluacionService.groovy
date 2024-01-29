package sieges

import grails.gorm.transactions.Transactional
import sieges.TipoEvaluacion

@Transactional
class TipoEvaluacionService {

    final NORMAL = 1
    final EXTRAORDINARIA = 2

    def select(alumno) {
        // Si la institución es publica solo se muestran los tipos de evaluacion
        // NORMAL(id: 1) y EXTRAORDINARIO(id: 2)
        if(alumno.planEstudios.carrera.institucion.publica){
            return TipoEvaluacion.findAllByIdInListAndActivo([1,2], true)
        }

        return TipoEvaluacion.findAllByActivo(true)

    }

    def obtener(id) {
        def tipoEvaluacion = TipoEvaluacion.get(id)

        if(!tipoEvaluacion) return null
        if(!tipoEvaluacion.activo) return null

        return tipoEvaluacion
    }

    def obtenerPorNombre(nombre) {
        def tipoEvaluacion = TipoEvaluacion.findByNombreAndActivo(nombre, true)

        if(!tipoEvaluacion) return null
        if(!tipoEvaluacion.activo) return null

        return tipoEvaluacion
    }
}
