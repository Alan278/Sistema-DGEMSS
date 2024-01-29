package sieges

import net.sf.jasperreports.engine.JREmptyDataSource
import net.sf.jasperreports.engine.JRExporterParameter
import net.sf.jasperreports.engine.JasperCompileManager
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.export.JRPdfExporter

class DocumentoService {
    def grailsApplication

    def generar(String nombreReporte, Map<String, Object> parametros){
        def pdfStream = new ByteArrayOutputStream()

        try {
            String reportName, dotJasperFileName
            reportName = grailsApplication.mainContext.getResource("plantillas/" + nombreReporte + ".jrxml").file.getAbsoluteFile()
            dotJasperFileName = reportName.substring(0, reportName.length() - 5) + "jasper"
            JasperCompileManager.compileReportToFile(reportName)
            JasperPrint print = JasperFillManager.fillReport(dotJasperFileName, parametros, new JREmptyDataSource())
            JRPdfExporter exporter = new JRPdfExporter()
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, print)
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, pdfStream)
            exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8")
            exporter.setParameter(JRExporterParameter.START_PAGE_INDEX, 0)
            exporter.setParameter(JRExporterParameter.END_PAGE_INDEX, parametros.numPaginas - 1)

            exporter.exportReport()

        } catch (Exception ex) {
            ex.printStackTrace()
            return [
                estatus: false,
                mensaje: "No se pudo generar el documento: ${ex.getCause()}"
            ]
        }

        return [
            estatus: true,
            mensaje: "Documento generado exitosamente",
            documento: pdfStream.toByteArray()
        ]
    }
}
