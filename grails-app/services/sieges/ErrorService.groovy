package sieges

import grails.gorm.transactions.Transactional

@Transactional
class ErrorService {

    def messageSource

    def obtenerErrores(obj){
        def errores = []
        obj.errors.allErrors.each {
            errores << messageSource.getMessage(it, null)
        }

        return errores
    }

}
