package sieges

import java.text.SimpleDateFormat
import org.hibernate.criterion.CriteriaSpecification

class CicloEscolarController {
    /**
     * Inyección del institucionService que contiene métodos para la gestión de instituciones
     */
    def institucionService
    /**
     * Inyección del carreraService que contiene métodos para la gestión de carreras
     */
    def carreraService
    /**
     * Inyección del cicloEscolarService que contiene métodos para la gestión de ciclos escolares
     */
    def cicloEscolarService
    def planEstudiosService
    def alumnoCicloEscolarService

/**
 * Permite llamar los servicios necesarios para obtener la lista de los registros activos y así realizar el registro de ciclos
 escolares
 * @return datos de los servicios requeridos
 */
    def registro(){
        def instituciones = institucionService.obtenerActivos(params)
        def carreras = carreraService.obtenerActivos(params)
        def planesEstudios = planEstudiosService.obtenerActivos(params)

        [
                instituciones: instituciones.datos,
                carreras: carreras.datos,
                planesEstudios: planesEstudios.datos,
                cicloEscolar: params,
        ]
    }

/**
 * Permite llamar al servicio para generar el registro del ciclo escolar
 * @return resultado
 * resultado con el mensaje y estatus del ciclo escolar y lo muestra en la vista
 */
    def registrar(){
        def resultado = cicloEscolarService.registrar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            redirect action: "listar"
        }else{
            redirect(action: "registro", params: params)
        }
        return
    }

/**
 * Permite listar los registros de los ciclos escolares, además de mostrar las instituciones y carreras activas para
 realizar el filtrado de los ciclos escolares
 * @return resultado
 * resultado con el listado de los ciclos escolares registrados y los muestra en la vista
 */
    def listar(){
        def resultado = cicloEscolarService.listar(params)
        def instituciones = institucionService.obtenerActivos(params)
        def carreras = carreraService.obtenerActivos(params)
        def planesEstudios = planEstudiosService.obtenerActivos(params)

        [
            instituciones: instituciones.datos,
            carreras: carreras.datos,
            planesEstudios: planesEstudios.datos,
            ciclosEscolares: resultado.datos.ciclos,
            conteo: resultado.datos.ciclos.totalCount,
            fechas: resultado.datos.fechas,
            parametros: params
        ]

    }
/**
 * Permite llamar al servicio para obtener los registros que se encuentran activos
 * @return ciclosEscolares
 * datos de los ciclos escolares que se encuentran activos
 */
    def obtenerActivos(){
        def ciclosEscolares = cicloEscolarService.obtenerActivos(params)

        respond ciclosEscolares
    }

/**
 * Permite mandar a llamar el servicio para consultar un ciclo escolar en especifico
 * @return resultado
 * resultado del mensaje, estatus y datos del ciclos escolar consultado y lo muestra en la vista
 */
    def consultar(){
        def resultado = cicloEscolarService.consultar(params)
        def inscripciones = cicloEscolarService.listarAlumnos(params).datos
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(!resultado.estatus){
            redirect action: "listar"
            return
        }
        [
            cicloEscolar: resultado.datos,
            inscripciones: inscripciones,
            conteo: inscripciones.totalCount
        ]

    }

/**
 * Permite llamar los servicios necesarios para obtener la lista de los registros activos, así como la consulta de los datos
 guardados del registro del ciclo escolar para realizar la modificación.
 * @return datos de los servicios requeridos
 */
    def modificacion(){
        def cicloEscolar = cicloEscolarService.consultar(params).datos

        def parametros = params
        if(!params.institucionId){
            parametros.institucionId = cicloEscolar.planEstudios.carrera.institucion.id.toString()
            parametros.carreraId = cicloEscolar.planEstudios.carrera.id.toString()
            parametros.planEstudiosId = cicloEscolar.planEstudios.id.toString()
        }

        def instituciones = institucionService.obtenerActivos(parametros).datos
        def carreras = carreraService.obtenerActivos(parametros).datos
        def planesEstudios = planEstudiosService.obtenerActivos(parametros).datos

        [
            instituciones: instituciones,
            carreras: carreras,
            planesEstudios: planesEstudios,
            cicloEscolar: cicloEscolar
        ]
    }

/**
 * Permite llamar al servicio para generar la modificación del ciclo escolar seleccionado
 * @return resultado
 * resultado con el mensaje y estatus del ciclo escolar y lo muestra en la vista
 */
    def modificar(){
        def resultado = cicloEscolarService.modificar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            redirect action: "listar"
            return
        }
        redirect(action: "modificacion" , params:params)
    }

/**
 * Permite llamar al servicio para generar la eliminación de un ciclo escolar seleccionado
 * @return resultado
 * resultado con el mensaje y estatus del ciclo escolar eliminado y lo muestra en la vista
 */
    def eliminar(){
        def resultado = cicloEscolarService.eliminar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect action: "listar"

        return
    }

    def obtenerPorAlumno(){
        def resultado = cicloEscolarService.obtenerPorAlumno(params)
        respond resultado
    }

    def subirExcel(){
        def instituciones = institucionService.obtenerActivos().datos
        def carreras = carreraService.obtenerActivos(params).datos
        def planesEstudios = planEstudiosService.obtenerActivos(params).datos

        [
            instituciones: instituciones,
            carreras: carreras,
            planesEstudios: planesEstudios
        ]
    }

    def cargarPorExcel(){
        def resultado = cicloEscolarService.cargarPorExcel(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            redirect action: "listar"
            return
        }

        redirect(action: "subirExcel" , params:params)
    }

    def listarAlumnos(){
        def cicloEscolar = CicloEscolar.get(params.id)
        def inscripciones = cicloEscolarService.listarAlumnos(params).datos

        [
            cicloEscolar: cicloEscolar,
            inscripciones: inscripciones,
            conteo: inscripciones.totalCount
        ]
    }

    def subirExcelAlumnos(){
        def cicloEscolar = CicloEscolar.get(params.id)

        [
            cicloEscolar: cicloEscolar
        ]
    }

    def cargarPorExcelAlumnos(){
        def resultado = cicloEscolarService.cargarPorExcelAlumnos(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        if(resultado.estatus){
            redirect (action: "listarAlumnos", params: params)
            return
        }

        redirect(action: "subirExcelAlumnos" , params:params)
    }

    def eliminarInscripcion(){
        def resultado = alumnoCicloEscolarService.eliminar(params)
        flash.estatus = resultado.estatus
        flash.mensaje = resultado.mensaje

        redirect(action: "listarAlumnos", params: [id: resultado.datos.cicloEscolar.id])
        return
    }

    def descargarPlantilla(){
        def plantilla = grailsApplication.mainContext.getResource("plantillasExcel/ciclos_escolares_plantilla.xlsx").file.getAbsoluteFile()
        response.setContentType("application/octet-stream")
        response.setHeader("Content-disposition", "attachment; filename=ciclos_escolares_plantilla.xlsx")
        response.outputStream << plantilla.getBytes()
        return
    }

    def descargarPlantillaAlumnos(){
        def plantilla = grailsApplication.mainContext.getResource("plantillasExcel/alumnos_ciclo_escolar_plantilla.xlsx").file.getAbsoluteFile()
        response.setContentType("application/octet-stream")
        response.setHeader("Content-disposition", "attachment; filename=alumnos_ciclo_escolar_plantilla.xlsx")
        response.outputStream << plantilla.getBytes()
        return
    }

}
