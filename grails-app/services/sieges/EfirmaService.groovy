package sieges

import java.security.Principal
import java.security.PrivateKey
import java.security.Signature
import java.security.cert.CertificateFactory
import java.security.cert.Certificate
import java.security.cert.X509Certificate

import org.apache.commons.ssl.PKCS8Key
import org.apache.commons.codec.binary.Base64
import org.apache.commons.io.FileUtils

class EfirmaService {
    def firmar(byte[] clavePrivada, String contrasena, String cadenaOriginal){
        String resultado;
        try {
            PKCS8Key pkcs8Key = new PKCS8Key(clavePrivada, contrasena?.toCharArray());
            PrivateKey privateKey = pkcs8Key.getPrivateKey();

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(cadenaOriginal.getBytes("UTF-8"));

            resultado = Base64.encodeBase64String(signature.sign());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return resultado;
    }

    def validarContrasena(byte[] clavePrivada, String contrasena){
        def resultado = true
        try {
            PKCS8Key pkcs8Key = new PKCS8Key(clavePrivada, contrasena?.toCharArray())
        } catch (Exception ex) {
            resultado = false
        }
        return resultado
    }

    def verificar(byte[] certificado, String cadenaOriginal, String sello){
        boolean resultado;
        try {

            if (certificado) {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                Certificate cert = (X509Certificate) cf.generateCertificates(new ByteArrayInputStream(certificado)).iterator().next();

                Signature signature = Signature.getInstance("SHA256withRSA");
                signature.initVerify(cert.getPublicKey());
                signature.update(cadenaOriginal.getBytes("UTF-8"));

                resultado = signature.verify(Base64.decodeBase64(sello.getBytes("UTF-8")));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return resultado;
    }

    def extraerDatosCertificado(byte[] certificado) {
        def datos = [:]

        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificates(new ByteArrayInputStream(certificado)).iterator().next();


            // datos.contenido = cert
            datos.validoDesde = cert.getNotBefore()
            datos.validoHasta = cert.getNotAfter()
            datos.numeroSerie = new String(cert.getSerialNumber().toByteArray())

            datos.nombre = extraerValorAtributo(cert.getSubjectDN(), "CN")
            datos.curp = extraerValorAtributo(cert.getSubjectDN(), "SERIALNUMBER")
            datos.rfc = extraerValorAtributo(cert.getSubjectDN(), "OID.2.5.4.45")
            datos.correoElectronico = extraerValorAtributo(cert.getSubjectDN(), "EMAILADDRESS")

        } catch (Exception ex) {
            datos = null
            ex.printStackTrace()
        }

        return datos
    }

    def extraerValorAtributo(Principal principal, String nombreAtributo){
        int start = principal.getName().indexOf(nombreAtributo);
        String tmpName, name = "";
        if (start != -1) {
            tmpName = principal.getName().substring(start + nombreAtributo.length() +1);
            int end = tmpName.indexOf(",");
            if (end > 0) {
                name = tmpName.substring(0, end);
            } else {
                name = tmpName;
            }
        }
        return name;
    }

    def toByteArray(File archivo){

        FileInputStream fis = new FileInputStream(archivo);

        byte[] fbytes = new byte[(int) archivo.length()];

        fis.read(fbytes);
        fis.close();

        return fbytes;
    }

    def bytesToBase64(byte[] fbytes){
        return Base64.encodeBase64String(fbytes)
    }

    def byteArrayToFile(String path, byte[] fbytes){
        def file = new File(path)
        FileUtils.writeByteArrayToFile(file, fbytes)
        return file
    }

    def base64toBytes(String base64){
        return Base64.decodeBase64(base64)
    }
    def pdfToBytes(String base64) {
        return Base64.decodeBase64(base64)
    }

    def generarUuid() {
        def uuid = UUID.randomUUID()
        return uuid.toString()?.toUpperCase()
    }

    def validarRelacionCertificadoClavePrivada(byte[] certificado, byte[] clavePrivada, String contrasena) {
        def cadenaOriginal = "CADENA_ORIGINAL_PRUEBA"

        def sello = firmar(clavePrivada, contrasena, cadenaOriginal)

        def validacionSello = verificar(certificado, cadenaOriginal, sello)

        return validacionSello
    }
}
