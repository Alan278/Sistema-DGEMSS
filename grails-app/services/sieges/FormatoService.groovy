package sieges

import java.text.SimpleDateFormat
import groovy.json.JsonSlurper

class FormatoService {

    def isInteger(String cadena) {
        cadena = cadena.trim()
        try {
            Integer.parseInt(cadena)
            return true
        } catch (NumberFormatException excepcion) {
            return false
        }
    }

    def isPositiveInteger(String cadena) {
        cadena = cadena.trim()
        try {
            def number = Integer.parseInt(cadena)
            if(number < 0) return false
            return true
        } catch (NumberFormatException excepcion) {
            return false
        }
    }

    def isNumeric(String cadena) {
        cadena = cadena.trim()
        try {
            Float.parseFloat(cadena)
            return true
        } catch (NumberFormatException excepcion) {
            return false
        }
    }

    def isPositiveNumeric(String cadena) {
        cadena = cadena.trim()
        try {
            def number = Float.parseFloat(cadena)
            if(number < 0) return false
            return true
        } catch (NumberFormatException excepcion) {
            return false
        }
    }

    def isValidEmail(String cadena) {
        cadena = cadena.trim()
        def regexEmail = /^(([^<>()\[\]\\.,;:\s@”]+(\.[^<>()\[\]\\.,;:\s@”]+)*)|(“.+”))@((\[[0–9]{1,3}\.[0–9]{1,3}\.[0–9]{1,3}\.[0–9]{1,3}])|(([a-zA-Z\-0–9]+\.)+[a-zA-Z]{2,}))$/
        if(!cadena.matches(regexEmail)) return false
        return true
    }

    def isValidPhoneNumber(String cadena) {
        cadena = cadena.trim()
        def regexPhoneNumber = /^([+]?\d{10,15}$)/
        if(!cadena.matches(regexPhoneNumber)) return false
        return true
    }

    def isValidZipCode(String cadena) {
        cadena = cadena.trim()
        if(!isPositiveInteger(cadena)) return false
        if(cadena.length() > 5) return false
        return true
    }

    def isValidPassword(String cadena) {
        cadena = cadena.trim()
        def regexPassword = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[$@$!%*?&])([A-Za-z\d$@$!%*?&]|[^ ]){10,20}$/
        if(!cadena.matches(regexPassword)) return false
        return true
    }

    def isDate(String cadena, String format) {
        cadena = cadena.trim()
        try{
			def date = new SimpleDateFormat(format).parse(cadena)
            return true
		}catch(Exception exception){
			return false
		}
    }

    def toFlatString(String cadena){
        if(!cadena) return null

        // Se remplazan los saltos de linea por espacios en blanco
		def from = "(\n|\r)"
		def to = " "

        return cadena.trim().toUpperCase().replaceAll(from, to);
    }

    def toArray(listaJson){
        def array = []

        try{
            def jsonSlurper = new JsonSlurper()
            array = jsonSlurper.parseText(listaJson)
        }catch(Exception ex){
            return null
        }

        return array
    }
}
