package sieges

import java.util.regex.Pattern;
/**
 * Servicio que permite convertir un numero a su equivalente en letras.
 * @author Luis Dominguez, Leslie Navez
 * @since 2022
 */
public class numeroLetrasService {

    def UNIDADES = ["", "uno ", "dos ", "tres ", "cuatro ", "cinco ", "seis ", "siete ", "ocho ", "nueve "]
    def DECENAS = ["diez ", "once ", "doce ", "trece ", "catorce ", "quince ", "dieciseis ",
        "diecisiete ", "dieciocho ", "diecinueve", "veinte ", "treinta ", "cuarenta ",
        "cincuenta ", "sesenta ", "setenta ", "ochenta ", "noventa "];
    def CENTENAS = ["", "ciento ", "doscientos ", "trecientos ", "cuatrocientos ", "quinientos ", "seiscientos ",
        "setecientos ", "ochocientos ", "novecientos "];

    def convertir(numero, mayusculas) {
        String literal = "";
        String parte_decimal;
        //si el numero utiliza (.) en lugar de (,) -> se reemplaza
        numero = numero.replace(".", ",");
        //si el numero no tiene parte decimal, se le agrega ,00
        if (numero.indexOf(",") == -1) {
            numero = numero + ",00";
        }
        //se valida formato de entrada -> 0,00 y 999 999 999,00
        if (Pattern.matches("\\d{1,9},\\d{1,2}", numero)) {
            //se divide el numero 0000000,00 -> entero y decimal
            def num = numero.split(",");
            //se convierte el numero a literal
            if (Integer.parseInt(num[0]) == 0) {//si el valor es cero
                literal = "cero ";
            } else if (Integer.parseInt(num[0]) > 999999) {//si es millon
                literal = getMillones(num[0]);
            } else if (Integer.parseInt(num[0]) > 999) {//si es miles
                literal = getMiles(num[0]);
            } else if (Integer.parseInt(num[0]) > 99) {//si es centena
                literal = getCentenas(num[0]);
            } else if (Integer.parseInt(num[0]) > 9) {//si es decena
                literal = getDecenas(num[0]);
            } else {//sino unidades -> 9
                literal = getUnidades(num[0]);
            }

            if (Integer.parseInt(num[1]) == 0) {//si el valor es cero
                parte_decimal = "";
            } else if (Integer.parseInt(num[1]) > 999999) {//si es millon
                parte_decimal = "punto " + getMillones(num[1]);
            } else if (Integer.parseInt(num[1]) > 999) {//si es miles
                parte_decimal = "punto " + getMiles(num[1]);
            } else if (Integer.parseInt(num[1]) > 99) {//si es centena
                parte_decimal = "punto " + getCentenas(num[1]);
            } else if (Integer.parseInt(num[1]) > 9) {//si es decena
                parte_decimal = "punto " + getDecenas(num[1]);
            } else {//sino unidades -> 9
                parte_decimal = "punto " + getUnidades(num[1]);
            }
            //devuelve el resultado en mayusculas o minusculas
            if (mayusculas) {
                return (literal + parte_decimal).toUpperCase();
            } else {
                return (literal + parte_decimal);
            }
        } else {//error, no se puede convertir
            return literal = null;
        }
    }

    /* funciones para convertir los numeros a literales */
    private String getUnidades(String numero) {// 1 - 9
        //si tuviera algun 0 antes se lo quita -> 09 = 9 o 009=9
        String num = numero.substring(numero.length() - 1);
        return UNIDADES[Integer.parseInt(num)];
    }

    private String getDecenas(String num) {// 99
        int n = Integer.parseInt(num);
        if (n < 10) {//para casos como -> 01 - 09
            return getUnidades(num);
        } else if (n > 19) {//para 20...99
            String u = getUnidades(num);
            if (u.equals("")) { //para 20,30,40,50,60,70,80,90
                return DECENAS[Integer.parseInt(num.substring(0, 1)) + 8];
            } else {
                return DECENAS[Integer.parseInt(num.substring(0, 1)) + 8] + "y " + u;
            }
        } else {//numeros entre 11 y 19
            return DECENAS[n - 10];
        }
    }

    private String getCentenas(String num) {// 999 o 099
        if (Integer.parseInt(num) > 99) {//es centena
            if (Integer.parseInt(num) == 100) {//caso especial
                return " cien ";
            } else {
                return CENTENAS[Integer.parseInt(num.substring(0, 1))] + getDecenas(num.substring(1));
            }
        } else {//por Ej. 099
            //se quita el 0 antes de convertir a decenas
            return getDecenas(Integer.parseInt(num) + "");
        }
    }

    private String getMiles(String numero) {// 999 999
        //obtiene las centenas
        String c = numero.substring(numero.length() - 3);
        //obtiene los miles
        String m = numero.substring(0, numero.length() - 3);
        String n = "";
        //se comprueba que miles tenga valor entero
        if (Integer.parseInt(m) > 0) {
            n = getCentenas(m);
            return n + "mil " + getCentenas(c);
        } else {
            return "" + getCentenas(c);
        }

    }

    private String getMillones(String numero) { //000 000 000
        //se obtiene los miles
        String miles = numero.substring(numero.length() - 6);
        //se obtiene los millones
        String millon = numero.substring(0, numero.length() - 6);
        String n = "";
        if (millon.length() > 1) {
            n = getCentenas(millon) + "millones ";
        } else {
            n = getUnidades(millon) + "millon ";
        }
        return n + getMiles(miles);
    }
}