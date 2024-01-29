package sieges

import groovy.json.JsonBuilder
import org.grails.web.json.JSONObject

class DatosRenapo {
    Long id
    String curp
    String nombre
    String apellidoPaterno
    String apellidoMaterno
    String sexo
    String fechaNacimiento
    String nacionalidad
    String documentoProbatorio
    String anioRegistro
    String foja
    String tomo
    String libro
    String numeroActa
    String crip
    String claveEntidadRegistro
    String claveMunicipioRegistro
    String numeroRegistroExtranjero
    String folioCarta
    String claveEntidadNacimiento
    String claveEntidadEmisora
    String estatusCurp
    String estatusOperacion
    String mensaje
    String tipoError
    String codigoError
    String idSesion
    String metodo

    def DatosRenapo(JSONObject datos){
        curp = datos?.isNull("curp")? null: datos?.curp
        nombre = datos?.isNull("nombre")? null: datos?.nombre
        apellidoPaterno = datos?.isNull("apellidoPaterno")? null: datos?.apellidoPaterno
        apellidoMaterno = datos?.isNull("apellidoMaterno")? null: datos?.apellidoMaterno
        sexo = datos?.isNull("sexo")? null: datos?.sexo
        fechaNacimiento = datos?.isNull("fechaNacimiento")? null: datos?.fechaNacimiento
        nacionalidad = datos?.isNull("nacionalidad")? null: datos?.nacionalidad
        documentoProbatorio = datos?.isNull("documentoProbatorio")? null: datos?.documentoProbatorio
        anioRegistro = datos?.isNull("anioRegistro")? null: datos?.anioRegistro
        foja = datos?.isNull("foja")? null: datos?.foja
        tomo = datos?.isNull("tomo")? null: datos?.tomo
        libro = datos?.isNull("libro")? null: datos?.libro
        numeroActa = datos?.isNull("numeroActa")? null: datos?.numeroActa
        crip = datos?.isNull("crip")? null: datos?.crip
        claveEntidadRegistro = datos?.isNull("claveEntidadRegistro")? null: datos?.claveEntidadRegistro
        claveMunicipioRegistro = datos?.isNull("claveMunicipioRegistro")? null: datos?.claveMunicipioRegistro
        numeroRegistroExtranjero = datos?.isNull("numeroRegistroExtranjero")? null: datos?.numeroRegistroExtranjero
        folioCarta = datos?.isNull("folioCarta")? null: datos?.folioCarta
        claveEntidadNacimiento = datos?.isNull("claveEntidadNacimiento")? null: datos?.claveEntidadNacimiento
        claveEntidadEmisora = datos?.isNull("claveEntidadEmisora")? null: datos?.claveEntidadEmisora
        estatusCurp = datos?.isNull("estatusCurp")? null: datos?.estatusCurp
        estatusOperacion = datos?.isNull("estatusOperacion")? null: datos?.estatusOperacion
        mensaje = datos?.isNull("mensaje")? null: datos?.mensaje
        tipoError = datos?.isNull("tipoError")? null: datos?.tipoError
        codigoError = datos?.isNull("codigoError")? null: datos?.codigoError
        idSesion = datos?.isNull("idSesion")? null: datos?.idSesion
        metodo = "WEB WS"
    }

    String getNombreCompleto(){
        return "${nombre} ${apellidoPaterno} ${apellidoMaterno?" ${apellidoMaterno}":""}"
    }

    String toPlainText(){
        toPlainText("")
    }

    String toPlainText(String prefijo){
        def detalle = "\n"

        detalle += "${prefijo}nombre: ${nombre?:""}\n"
        detalle += "${prefijo}curp: ${curp?:""}\n"
        detalle += "${prefijo}apellidoPaterno: ${apellidoPaterno?:""}\n"
        detalle += "${prefijo}apellidoMaterno: ${apellidoMaterno?:""}\n"
        detalle += "${prefijo}sexo: ${sexo?:""}\n"
        detalle += "${prefijo}fechaNacimiento: ${fechaNacimiento?:""}\n"
        detalle += "${prefijo}nacionalidad: ${nacionalidad?:""}\n"
        detalle += "${prefijo}documentoProbatorio: ${documentoProbatorio?:""}\n"
        detalle += "${prefijo}anioRegistro: ${anioRegistro?:""}\n"
        detalle += "${prefijo}foja: ${foja?:""}\n"
        detalle += "${prefijo}tomo: ${tomo?:""}\n"
        detalle += "${prefijo}libro: ${libro?:""}\n"
        detalle += "${prefijo}numeroActa: ${numeroActa?:""}\n"
        detalle += "${prefijo}crip: ${crip?:""}\n"
        detalle += "${prefijo}claveEntidadRegistro: ${claveEntidadRegistro?:""}\n"
        detalle += "${prefijo}claveMunicipioRegistro: ${claveMunicipioRegistro?:""}\n"
        detalle += "${prefijo}numeroRegistroExtranjero: ${numeroRegistroExtranjero?:""}\n"
        detalle += "${prefijo}folioCarta: ${folioCarta?:""}\n"
        detalle += "${prefijo}claveEntidadNacimiento: ${claveEntidadNacimiento?:""}\n"
        detalle += "${prefijo}claveEntidadEmisora: ${claveEntidadEmisora?:""}\n"
        detalle += "${prefijo}estatusCurp: ${estatusCurp?:""}\n"
        detalle += "${prefijo}estatusOperacion: ${estatusOperacion?:""}\n"
        detalle += "${prefijo}mensaje: ${mensaje?:""}\n"
        detalle += "${prefijo}tipoError: ${tipoError?:""}\n"
        detalle += "${prefijo}codigoError: ${codigoError?:""}\n"
        detalle += "${prefijo}idSesion: ${idSesion?:""}\n"
        detalle += "${prefijo}metodo: ${metodo?:""}\n"

        return detalle
    }
}

