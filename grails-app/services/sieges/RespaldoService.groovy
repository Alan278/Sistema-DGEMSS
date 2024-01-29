package sieges

import grails.gorm.transactions.Transactional
import groovy.io.FileType

/*
 * @author Alan Jamir
 * @since 2023
 */

@Transactional
class RespaldoService {
    /**
     * Inyección de messageSource que contiene mensajes de validaciones para los atributos de cada dominio
     * Los mensajes de validación se encuentran en: grails-app/i18n/messages_es.properties
     */
    def messageSource
    def dataSource
    def servletContext 

    def listar(params){
        def resultado = [
            estatus: false,
            mensaje: 'No se encontraron respaldos',
            datos: []
        ]

        def list = []
        def dir = new File("/home/Respaldos")
        dir.eachFile (FileType.FILES) {
            file -> list << file
        }

        if(!params.search) params.search = ''

        def list_sort = list.sort()
        list_sort.reverse(true)

        list.each {
            if(it.name.indexOf(params.search) != -1) {
                resultado.datos << it.name
            }
        }

        if(resultado.datos.size > 0) resultado.mensaje = 'Respaldos consultados exitosamente'

        return resultado
    }


    def generar() {
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        try{
            def pb = new ProcessBuilder("/home/GeneracionRespaldos.sh")
            def p = pb.start()
            def reader = new BufferedReader(new InputStreamReader(p.getInputStream()))
            def line = null

            while ((line = reader.readLine()) != null){
                println(line)
            }

            resultado.estatus = true
            resultado.mensaje = "Respaldo generado exitosamente"
        } catch(Exception ex) {
            resultado.mensaje = "Ha ocurrido un problema al generar el respaldo. Inténtelo mas tarde"
        }

        return resultado
    }

    def eliminar(params) {
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        if(!params.nombre){
            resultado.mensaje = 'El nombre del respaldo es un dato requerido'
            return resultado
        }

        def file = new File('/home/Respaldos/' + params.nombre)

        if (file.exists()) {
            if (file.delete()){
                resultado.mensaje = "El fichero ha sido borrado satisfactoriamente"
                resultado.estatus = true
            }else{
                resultado.mensaje = "El fichero no puede ser borrado"
            }
        }

        return resultado
    }

    def ejecutarScriptSql(params) {
        def resultado = [
            estatus: false,
            mensaje: '',
            datos: null
        ]

        def archivoSqlParams = params.archivoSql

        def webRootDir = servletContext.getRealPath("/")
        def userDir = new File(webRootDir, "/cargarSqlTemp")
        userDir.mkdirs()
        
        def archivoSql = new File( userDir, archivoSqlParams.originalFilename)
        archivoSqlParams.transferTo(archivoSql)
        
        print(archivoSql.getAbsolutePath())

        try{
            def pb = new ProcessBuilder("/home/EjecutarScriptSql.sh", archivoSql.getAbsolutePath())
            def p = pb.start()
            def reader = new BufferedReader(new InputStreamReader(p.getInputStream()))
            def line = null

            while ((line = reader.readLine()) != null){
                println(line)
            }

            resultado.estatus = true
            resultado.mensaje = "Respaldo cargado exitosamente"
        } catch(Exception ex) {
            resultado.mensaje = "Ha ocurrido un problema al cargar el respaldo. Inténtelo mas tarde"
        }

        archivoSql.delete()
        return resultado
    }
}
