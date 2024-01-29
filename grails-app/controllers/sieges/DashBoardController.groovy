package sieges

class DashBoardController {
    def dashBoardService
    def actaService
    def constanciaService
    def notificacionService
    def certificadoService

    def index() {
        def actas = actaService.listarActas(params).datos
        def constancias = constanciaService.listarConstancias(params).datos
        def notificaciones = notificacionService.listarNotificaciones(params).datos
        def certificados = certificadoService.listarCertificados(params).datos

        [
            conteoActa: actas.totalCount,
            conteoConstancia: constancias.totalCount,
            conteoNotificacion: notificaciones.totalCount,
            conteoCertificado: certificados.totalCount,
            datosCertificados: dashBoardService.datosCertificados(),
            datosConstancias: dashBoardService.datosConstancias(),
            datosNotificaciones: dashBoardService.datosNotificaciones(),
            datosActas: dashBoardService.datosActas(),
            datosCertificadosPublicas: dashBoardService.datosCertificadosPublicas(),
            datosCertificadosPrivadas: dashBoardService.datosCertificadosPrivadas(),
            datosConstanciasPrivadas: dashBoardService.datosConstanciasPrivadas(),
            datosNotificacionesPrivadas: dashBoardService.datosNotificacionesPrivadas(),
            datosActasPrivadas: dashBoardService.datosActasPrivadas(),
            datosInstituciones: dashBoardService.datosInstituciones(),
            datosAlumnos: dashBoardService.datosAlumnos(),
        ]
    }
}
