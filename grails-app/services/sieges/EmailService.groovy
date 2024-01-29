package sieges

import grails.gorm.transactions.Transactional

@Transactional
class EmailService {
    def mailService
    def grailsResourceLocator

    def notificar(String correoElectronico, String asunto, String plantilla, Map parametros){
        notificar(correoElectronico, asunto, plantilla, parametros, null)
    }

    def notificar(String correoElectronico, String asunto, String plantilla, Map parametros, List<Map> adjuntos){
        mailService.sendMail {
            multipart true
            from "SIEGES<tituloelectronico@morelos.gob.mx>"
            to "${correoElectronico?.trim()}"
            subject "${asunto}"
            inline "logoMorelos", "image/png", grailsResourceLocator.findResourceForURI("/images/logo_morelos.png")?.getFile()
            inline "logoAnfitrion", "image/png", grailsResourceLocator.findResourceForURI("/images/logo_anfitrion.png")?.getFile()
            inline "plecaMorelos", "image/png", grailsResourceLocator.findResourceForURI("/images/pleca_morelos.png")?.getFile()
            html view: "/notificacion/${plantilla}", model: parametros

            if (adjuntos) {
                adjuntos.each {
                    attach(it.fileName, it.contentType, it.bytes)
                }
            }

        }
    }
}
