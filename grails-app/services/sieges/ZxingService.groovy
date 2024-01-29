package sieges

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter

class ZxingService {
    def generarQr(String contenido){
        def qr
        try {
            def content = contenido
            def qrCodeWriter = new QRCodeWriter()
            def bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 300, 300, Collections.singletonMap(EncodeHintType.MARGIN, 0))
            def bos = new ByteArrayOutputStream()
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", bos)
            qr = bos.toByteArray()
            bos.close()
        } catch (Exception ex) {
            ex.printStackTrace()
            return [
                    estatus: false,
                    mensaje: "No se pudo generar el codigo qr: ${ex.getCause()}"
            ]
        }

        return [
                estatus: true,
                mensaje: "QR generado exitosamente",
                qr: qr
        ]
    }
}
