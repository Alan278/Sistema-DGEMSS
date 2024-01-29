package sieges

import grails.gorm.transactions.Transactional
import groovy.sql.Sql

import builders.dsl.spreadsheet.builder.poi.PoiSpreadsheetBuilder
import org.apache.commons.io.FileUtils;


/**
 * Servicio que permite la administración de los reportes
 * @author Luis Dominguez, Leslie Navez
 * @since 2021
 */

@Transactional
class ReporteService {
    /**
     * Inyección de messageSource que contiene mensajes de validaciones para los atributos de cada dominio
     * Los mensajes de validación se encuentran en: grails-app/i18n/messages_es.properties
     */
    def messageSource
    def dataSource

    /**
     * Permite realizar el registro de un reporte
     * @param nombre (Requerido)
     * @param sql (Requerido)
     * @return resultado
     * resultado con el estatus, mensaje y datos del reporte
     */
    def registrar(params){
        def resultado = [
                estatus: false,
                mensaje: '',
                datos: null
        ]
        def reporte = new Reporte(params)

        if(params.consultaSql.indexOf("delete") != -1 || params.consultaSql.indexOf("update") != -1) {
            resultado.mensaje = 'La sentencia sql no es válida'
            return resultado
        }

        if(reporte.save(flush:true)){
            resultado.estatus = true
        }else{
            // Se recorren los errores de validación y se asignan a la propiedad resultado.mensaje
            reporte.errors.allErrors.each {
                resultado.mensaje = messageSource.getMessage(it, null)
            }
        }

        if(resultado.estatus){
            resultado.mensaje = 'Reporte creado exitosamente'
            resultado.datos = reporte
        }

        return resultado
    }

    /**
     * Permite listar los reportes que se encuentran generados, además de generar la busqueda por medio del
     nombre
     * @param search (Opcional)
     * Nombre del reporte
     * @return resultado
     * resultado con el estatus, mensaje y datos del reporte
     */
    def listar(params){
        def resultado = [
                estatus: false,
                mensaje: '',
                datos: null
        ]

        if(!params.max) params.max = 3
        if(!params.offset) params.offset = 0
        if(!params.sort) params.sort = 'id'
        if(!params.order) params.order = 'asc'

        def criteria = {
            and{
                eq("activo", true)
                if(params.search){
                    ilike("nombre", "%${params.search}%")
                }
            }
        }

        def reportes = Reporte.createCriteria().list(params,criteria)

        if(reportes.totalCount <= 0){
            resultado.mensaje = 'No se encontraron reportes'
            resultado.datos = reportes
            return resultado
        }
        resultado.estatus = true
        resultado.mensaje = 'Reportes consultados exitosamente'
        resultado.datos = reportes

        return resultado
    }

    /**
     * Permite mostrar la información del reporte seleccionado realizandolo a travéz de una consulta sql
     * @param id (Requerido)
     * Id del reporte
     * @return resultado
     * resultado con el estatus, mensaje y datos del reporte
     */
    def consultar(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null,
            consulta: null,
            estatusConsulta: false,
        ]

        if(!params.id){
            resultado.mensaje = 'El id es un dato requerido'
            return resultado
        }

        def reporte = Reporte.get(params.id)

        if(!reporte){
            resultado.mensaje = 'Reporte no encontrado'
            return resultado
        }

        if(!reporte.activo){
            resultado.mensaje = 'Reporte inactivo'
            return resultado
        }

        def sql = new Sql(dataSource)

        def resultadoReporte
        try {
            if(!params.max) params.max = 3
            if(!params.offset) params.offset = 0

            resultadoReporte = sql.rows(reporte.consultaSql, params.offset, params.max)

            resultado.estatusConsulta = true
            resultado.consulta = resultadoReporte

        } catch (Exception ex){
            resultado.estatusConsulta = false
            resultado.mensaje = "No se pudo realizar la consulta: ${ex.getMessage()}"
        }

        resultado.estatus = true
        resultado.datos = reporte

        return resultado
    }

    def obtenerExcel(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null,
            consulta: null,
            estatusConsulta: false,
        ]

        if(!params.id){
            resultado.mensaje = 'El id es un dato requerido'
            return resultado
        }

        def reporte = Reporte.get(params.id)

        if(!reporte){
            resultado.mensaje = 'Reporte no encontrado'
            return resultado
        }

        if(!reporte.activo){
            resultado.mensaje = 'Reporte inactivo'
            return resultado
        }

        def sql = new Sql(dataSource)

        def resultadoReporte
        try {

            resultadoReporte = sql.rows(reporte.consultaSql)

            resultado.estatusConsulta = true
            resultado.consulta = resultadoReporte

        } catch (Exception ex){
            resultado.estatusConsulta = false
            resultado.mensaje = "No se pudo realizar la consulta: ${ex.getMessage()}"
            return resultado
        }

        def file = new File('spreadsheet.xlsx')
        file.createNewFile()

        PoiSpreadsheetBuilder.create(file).build {
            style ('headers') {
                border(bottom) {
                    style thick
                    color black
                }
                font {
                    style bold
                }
                background whiteSmoke
            }                                           
            sheet('Reporte') {
                row{
                    style 'headers'
                    resultadoReporte?.get(0)?.keySet().each{ nombreColumna ->
                        cell nombreColumna
                    }
                }
                resultadoReporte.each{ valoresFila ->
                    row{
                        valoresFila.entrySet().each{ valorColumna ->
                            cell{
                                value String.valueOf(valorColumna.value)
                                width auto
                            }
                        }
                    }
                }
            }
        }

        def excelBytes = FileUtils.readFileToByteArray(file);

        resultado.estatus = true
        resultado.datos = excelBytes

        return resultado
    }

    /**
     * Permite realizar la modificación del reporte seleccionado
     * @param id (Requerido)
     * Id del reporte
     * @return resultado
     * resultado con el estatus, mensaje y datos del curso
     */
    def modificar(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        if(!params.id){
            resultado.mensaje = 'El id es un dato requerido'
            return resultado
        }

        def reporte = Reporte.get(params.id)

        if(params.consultaSql.indexOf("delete") != -1 || params.consultaSql.indexOf("update") != -1) {
            resultado.mensaje = 'La sentencia sql no es válida'
            return resultado
        }

        if(!reporte){
            resultado.mensaje = 'Reporte no encontrado'
            return resultado
        }

        if(!reporte.activo){
            resultado.mensaje = 'Reporte inactivo'
            return resultado
        }

        def reporteAux = new Reporte(reporte.properties)

        reporte.properties = params

        if(!reporte.equals(reporteAux))
            reporte.ultimaActualizacion = new Date()

        if(reporte.save(flush:true)){
            resultado.estatus = true
        }else{
            // Se recorren los errores de validación y se asignan a la propiedad resultado.mensaje
            reporte.errors.allErrors.each {
                resultado.mensaje = messageSource.getMessage(it, null)
            }
        }

        if(resultado.estatus){
            resultado.mensaje = 'Reporte modificado exitosamente'
            resultado.datos = reporte
        }

        return resultado
    }

    /**
     * Permite generar una baja lógica del reporte seleccionada
     * @param id (requerido)
     * id del reporte
     * @return resultado
     * resultado con el estatus, mensaje y datos del reporte
     */
    def eliminar(params){
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        //Verifica si se encontro un id
        if(!params.id){
            resultado.mensaje = 'El id es un dato requerido'
            return resultado
        }
        //En caso de tenerlo se define una variable asignandole los paramatros del id recibido
        def reporte = Reporte.get(params.id)

        //Verifica si encontro el curso con ese id
        if(!reporte){
            resultado.mensaje = 'Reporte no encontrado'
            return resultado
        }
        //En caso de encontrarlo modifica el campo de activo a falso
        reporte.activo = false

        //Verifica si se realizó el guardado de forma correcta
        if(!reporte.save(flush:true)){
            //En caso de no ser así muestra los errores que ocurrieron
            reporte.errors.allErrors.each {
                resultado.mensaje = messageSource.getMessage(it, null)
            }
            return resultado
        }
        //En caso de guardarlo, muestra el resultado
        resultado.estatus = true
        resultado.mensaje = 'Reporte dado de baja exitosamente'
        resultado.datos = reporte

        return resultado
    }
}
