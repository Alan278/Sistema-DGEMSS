package sieges

import grails.gorm.transactions.Transactional
import org.hibernate.criterion.CriteriaSpecification

/**
 * @author Alan Guevarin
 * @since 2022
 */
@Transactional
class FirmaElectronicaService {
    def messageSource
    def usuarioService
    def efirmaService
    def bitacoraService

    def registrarCer(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def datosBitacora = [
            clase: "FirmaElectronicaService",
            metodo: "registrarCer",
            nombre: "Registro de archivo cer",
            descripcion: "Intento de registro de archivo cer",
            estatus: "ERROR"
        ]

        if(!params.cer){
            resultado.mensaje = "El archivo .cer es un dato requerido"
            return resultado
        }

        def datosCer = efirmaService.extraerDatosCertificado(params.cer.getBytes())

        if(!datosCer){
            resultado.mensaje = "Error al leer el archivo"
            return resultado
        }

        def persona = usuarioService.obtenerUsuarioLogueado().persona

        def tieneFirmaActiva = persona.firmasElectronicas.any{ firma -> !firma.expiro()}

        if(tieneFirmaActiva){
            resultado.mensaje = "Actualmente cuenta con una firma activa"
            return resultado
        }

        if(!persona.curp.equals(datosCer.curp)){
            resultado.mensaje = "La curp del certificado no corresponde con la registrada para este usuario"
            return resultado
        }

        def firmaElectronica = new FirmaElectronica()
            firmaElectronica.nombreCer = datosCer.nombre
            firmaElectronica.curpCer = datosCer.curp
            firmaElectronica.rfcCer = datosCer.rfc
            firmaElectronica.correoElectronicoCer =  datosCer.correoElectronico
            firmaElectronica.validoDesdeCer = datosCer.validoDesde
            firmaElectronica.validoHastaCer = datosCer.validoHasta
            firmaElectronica.numeroSerieCer = datosCer.numeroSerie
            firmaElectronica.archivoCer = params.cer.getBytes()
            firmaElectronica.persona = persona

        if(!firmaElectronica.save(flush: true)){
            firmaElectronica.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar(datosBitacora)
            return resultado
        }

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = 'Certificado registrado exitosamente'
		resultado.datos = firmaElectronica

        return resultado

    }

    def registrarKey(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def datosBitacora = [
            clase: "FirmaElectronicaService",
            metodo: "registrarKey",
            nombre: "Registro de archivo key",
            descripcion: "Intento de registro de archivo key",
            estatus: "ERROR"
        ]
        
        if(!params.id){
            resultado.mensaje = "El id del certificado es un dato requerido"
            return resultado
        }

        if(!params.key){
            resultado.mensaje = "El archivo .key es un dato requerido"
            return resultado
        }

        def persona = usuarioService.obtenerUsuarioLogueado().persona

        def firmaId = Integer.valueOf(params.id)
        def firmaElectronica = null
        persona.firmasElectronicas.each{ firma ->
            if(firma.id == firmaId){
                firmaElectronica = firma
            }
        }

        if(!firmaElectronica){
            resultado.mensaje = "Firma no encontrada"
            return resultado
        }

        if(firmaElectronica.expiro()){
            resultado.mensaje = "Firma expirada"
            return resultado
        }

        def bytesClavePrivada = params.key.getBytes()
        def contrasena = params.contrasena
        def resultadoValidacion = efirmaService.validarContrasena(bytesClavePrivada, contrasena)
        if (!resultadoValidacion){
            resultado.mensaje = 'La clave privada o contraseña son incorrectos'
            return resultado
        }

        def certificado = firmaElectronica.archivoCer
        resultadoValidacion = efirmaService.validarRelacionCertificadoClavePrivada(certificado, bytesClavePrivada, contrasena)

        if(!resultadoValidacion){
            resultado.mensaje = "La clave privada no corresponde al certificado registrado"
            return resultado
        }

        firmaElectronica.archivoKey = params.key.getBytes()
        
        if(!firmaElectronica.save(flush: true)){
            firmaElectronica.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar(datosBitacora)
            return resultado
        }

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = 'Clave privada registrada exitosamente'
		resultado.datos = firmaElectronica

        return resultado

    }

    def eliminarCer(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def datosBitacora = [
            clase: "FirmaElectronicaService",
            metodo: "eliminarCer",
            nombre: "Eliminación de archivo cer",
            descripcion: "Intento de eliminación de archivo cer",
            estatus: "ERROR"
        ]

        if(!params.id){
            resultado.mensaje = "El id es un dato requerido"
            return resultado
        }

        def persona = usuarioService.obtenerUsuarioLogueado().persona

        def firmaId = Integer.valueOf(params.id)

        def firmaElectronica = FirmaElectronica.get(firmaId)

        if(!firmaElectronica){
            resultado.mensaje = "Firma no encontrada"
            return resultado
        }

        def perteneceAUsuario = persona.firmasElectronicas.any{ firma -> firma.id == firmaElectronica.id }

        if(!perteneceAUsuario){
            resultado.mensaje = "Permiso denegado"
            return resultado
        }

        def certificado = Certificado.createCriteria().list {
			createAlias("firmaDirectorEscuela", "fAD", CriteriaSpecification.LEFT_JOIN)
			createAlias("firmaAutenticadorDgemss", "fDE", CriteriaSpecification.LEFT_JOIN)
			and{
				eq("activo", true)
                or{
				    eq("fDE.id", firmaElectronica.id)
				    eq("fAD.id", firmaElectronica.id)
                }
			}
		}

        if(certificado.size() > 0){
            resultado.mensaje = "No se puede eliminar el certificado porque ya ha sido ocupado para firmar"
            return resultado
        }

        firmaElectronica.delete()
        persona.firmasElectronicas = null

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = 'Certificado eliminado exitosamente'
		resultado.datos = firmaElectronica

        return resultado

    }

    def eliminarKey(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def datosBitacora = [
            clase: "FirmaElectronicaService",
            metodo: "eliminarKey",
            nombre: "Eliminación de archivo key",
            descripcion: "Intento de eliminación de archivo key",
            estatus: "ERROR"
        ]

        if(!params.id){
            resultado.mensaje = "El id es un dato requerido"
            return resultado
        }

        def persona = usuarioService.obtenerUsuarioLogueado().persona

        def firmaId = Integer.valueOf(params.id)
        def firmaElectronica = null
        persona.firmasElectronicas.each{ firma ->
            if(firma.id == firmaId){
                firmaElectronica = firma
            }
        }

        if(!firmaElectronica){
            resultado.mensaje = "Firma no encontrada"
            return resultado
        }

        firmaElectronica.archivoKey = null

        if(!firmaElectronica.save(flush: true)){
            firmaElectronica.errors.allErrors.each {
				resultado.mensaje = messageSource.getMessage(it, null)
			}
			bitacoraService.registrar(datosBitacora)
            return resultado
        }

        datosBitacora.estatus = "EXITOSO"
		bitacoraService.registrar(datosBitacora)

		resultado.estatus = true
		resultado.mensaje = 'Clave privada eliminada exitosamente'
		resultado.datos = firmaElectronica

        return resultado
    }

}
