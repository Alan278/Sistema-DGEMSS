package sieges

import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.conn.ssl.TrustAllStrategy
import org.apache.http.ssl.SSLContextBuilder

import javax.servlet.http.HttpSession
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.CookieStore
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.util.EntityUtils
import org.apache.http.Header
import org.apache.commons.io.IOUtils
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.grails.web.json.JSONObject
import org.springframework.web.context.request.RequestContextHolder

class RenapoService {
    def firmaElectronicaService

    def consultarDatosCurp(String curp){
        def datosRenapo = [
            estatus: true,
            mensaje: "",
            datos: null,
        ]

        def respuesta = consultarDatosCurpWs(curp)

        datosRenapo.estatus = respuesta.estatus
        datosRenapo.mensaje = respuesta.mensaje

        if(respuesta.estatus) {
            datosRenapo.datos = new DatosRenapo(respuesta.datos)
        }
        return datosRenapo

    }

    def consultarDatosCurpWs(String curp) {
        def token = "5dc77af0-d56e-42b3-9c60-3fb66f564f03"

        def result = [
            estatus: true,
            mensaje: "OK",
            datos: null,
        ]

        try {
            def json = "";

            String url = "https://wscurp.morelos.gob.mx/restful/curp.json";

            CloseableHttpClient client = HttpClients.custom().setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build()).setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
            HttpPost httpPost = new HttpPost(url);

            CookieStore cookieStore = new BasicCookieStore();
            HttpClientContext localContext = HttpClientContext.create();
            localContext.setCookieStore(cookieStore);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("token", token));
            params.add(new BasicNameValuePair("curp", curp));
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            httpPost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
            httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");
            httpPost.setHeader("Accept-Language", "es-ES,es;q=0.9,ca;q=0.8,en;q=0.7,gl;q=0.6,la;q=0.5");
            httpPost.setHeader("Cache-Control", "max-age=0");
            httpPost.setHeader("Connection", "keep-alive");
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPost.setHeader("Origin", "https://tituloelectronico.morelos.gob.mx");
            httpPost.setHeader("Referer", "https://tituloelectronico.morelos.gob.mx/tituloElectronico/capturarTitulo");
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36");

            CloseableHttpResponse response = client.execute(httpPost, localContext);

            if (response.getStatusLine().getStatusCode() != 200) {
                result.estatus = false
                result.mensaje = "No se pudo realizar la conexion con el servicio de validacion de CURP (GEM)"
            } else {
                def br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))

                def output
                while ((output = br.readLine()) != null) {
                    json += output;
                }

                result.datos = new JSONObject(json)
                result.mensaje = result.datos.mensaje
                if(result.datos.codigoError) result.estatus = false
            }

            EntityUtils.consume(response.getEntity());
            response.close();
            client.close();

        } catch (Exception ex) {
            ex.printStackTrace()
            result.estatus = false
            result.mensaje = "Ocurrio un error al realizar la validacion de la CURP"
        }

        return result
    }

    def validacionWeb(String curp){
        def result = [
            hecho: false
        ]
        
        if (webAbrirSesion()) {
            result = webObtenerCaptcha()
            
            if (result.hecho) {                
                getSession().validacionCurpRenapo = curp
            } else {
                result.mensaje = "No se pudo obtener el captcha"
            }
            
        } else {
            result.mensaje = "No se pudo realizar la conexion con el servicio de validacion de CURP"
        }
        
        return result
    }
    
    // Metodos de validacion via web
    
    def boolean webAbrirSesion() {
        boolean hecho = false;
        //String url = "https://consultas.curp.gob.mx/CurpSP/inicio2_2_0.jsp";
        String url = "https://consultas.curp.gob.mx/CurpSPpruebaRendimiento/renapo/inicio.jsp";

        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);

            CookieStore cookieStore = new BasicCookieStore();
            HttpClientContext localContext = HttpClientContext.create();
            localContext.setCookieStore(cookieStore);

            CloseableHttpResponse response = client.execute(httpPost, localContext);

            if (response.getStatusLine().getStatusCode() == 200) {
                hecho = true;
                getSession().sesionRenapo = cookieStore
            }
            
            EntityUtils.consume(response.getEntity());
            response.close();
            client.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return hecho;
    }
    
    def webObtenerCaptcha() {
        def result = [
            hecho: false
        ]
        
        String url = "https://consultas.curp.gob.mx/CurpSPpruebaRendimiento/captchaCurp";

        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);

            HttpClientContext localContext = HttpClientContext.create();
            localContext.setCookieStore(getSession().sesionRenapo);

            CloseableHttpResponse response = client.execute(httpPost, localContext);

            if (response.getStatusLine().getStatusCode() == 200) {
                InputStream content = response.getEntity().getContent();
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024 * 1024);
                IOUtils.copy(content, baos);

                def captcha = firmaElectronicaService.bytesToBase64(baos.toByteArray())
                
                result.hecho = true
                result.captcha = captcha
                
            }

            EntityUtils.consume(response.getEntity());
            response.close();
            client.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            result.estatus = ""
        }

        return result? result : new JSONObject("{}")
    }
    
    def consultarCurpWeb(String codigo){
        def result = [
            hecho: false
        ]
        
        result = obtenerDatosCurpWeb(codigo)
        
        if (result.hecho) {
            if (!validarDatosMinimos(result)) {
                result.hecho = false
                result.mensaje = "Los datos obtenidos son incompletos"
            }
        }        
        
        return result
    }
    
    def obtenerDatosCurpWeb(String codigo) {
        def result = [
            hecho: true
        ]
        
        String url = "https://consultas.curp.gob.mx/CurpSPpruebaRendimiento/renapo/curp21.do";
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("strtipo", "B"));
            params.add(new BasicNameValuePair("codigo", codigo));
            params.add(new BasicNameValuePair("strCurp", getSession().validacionCurpRenapo));
            httpPost.setEntity(new UrlEncodedFormEntity(params));
                        
            def cookie = ""
            for (int x = 0; x < getSession().sesionRenapo.getCookies().size(); x++) {
                cookie += "${getSession().sesionRenapo.getCookies().get(x).getName()}=${getSession().sesionRenapo.getCookies().get(x).getValue()}; "
            }
            
            httpPost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
            httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");
            httpPost.setHeader("Accept-Language", "es-ES,es;q=0.9,ca;q=0.8,en;q=0.7,gl;q=0.6,la;q=0.5");
            httpPost.setHeader("Cache-Control", "max-age=0");
            httpPost.setHeader("Connection", "keep-alive");
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPost.setHeader("Cookie", cookie);
            httpPost.setHeader("Host", "consultas.curp.gob.mx");
            httpPost.setHeader("Origin", "https://consultas.curp.gob.mx");
            httpPost.setHeader("Referer", "https://consultas.curp.gob.mx/CurpSPpruebaRendimiento/renapo/inicio.jsp");
            httpPost.setHeader("Sec-Fetch-Mode", "navigate");
            httpPost.setHeader("Sec-Fetch-Site", "same-origin");
            httpPost.setHeader("Sec-Fetch-User", "?1");
            httpPost.setHeader("Upgrade-Insecure-Requests", "1");
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36");
            
            CloseableHttpResponse response = client.execute(httpPost);
            
            BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String html = "";
            String line = "";
            while ((line = br.readLine()) != null) {
                html += line;
            }
            br.close();
                        
            if (response.getStatusLine().getStatusCode() == 200) {                
                result = procesarResultado(html);
            }

            EntityUtils.consume(response.getEntity());
            response.close();
            client.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            result.hecho = false
            result.mensaje = "Error al obtener los datos"
        }
        
        guardarDatosCurp(result)

        return result;
    }
    
    
    def procesarResultado(String html) {
        def result = [
            hecho: true
        ]
        
        try {
            Document doc = Jsoup.parse(html);
            
            Element mensaje = doc.select(".has-error,.container .panel-danger .alert-danger").first();
                        
            if (mensaje) {
                result.hecho = false
                result.mensaje = mensaje.text()
                return result
            }
            
            Elements filas = doc.select(".data_resume tr");
            Elements datos = null
            String[] partes = null
            String fieldName = null;
            String fieldValie = null;
            for (Element fila : filas) {
                partes = new String[2]
                partes[0] = fila.select(".field_name").text().trim()
                partes[1] = fila.select("strong").text().trim()
                result = identificarCampo(result, partes);
            }
            
            /*Elements datos = doc.select(".data_resume td");

            String[] partes;
            for (Element dato : datos) {
                partes = dato.text().split(":");

                if (partes.length > 0) {
                    partes[0] = partes[0].trim();
                    if (partes.length > 1) {
                        partes[1] = partes[1].trim();
                        result = identificarCampo(result, partes);
                    }
                }

            }*/
            
            result.foja = null
            result.tomo = null
            result.libro = null
            result.crip = null
            result.numeroRegistroExtranjero = null
            result.folioCarta = null
            result.claveEntidadEmisora = null
            result.estatusCurp = null
            result.estatusOperacion = null
            result.mensaje = null
            result.tipoError = null
            result.codigoError = null
            result.idSesion = null
            result.metodo = "WEB HTML"
            
        } catch (Exception ex) {
            ex.printStackTrace();
            result.hecho = false
            result.mensaje = "Error al procesar el resultado"
        }

        return result;
    }
    
    
    def identificarCampo(Map result, String[] keypar) {
        
        /*
        curp: result.CURP.text(),
                    nombre: result.nombres.text(),
                    apellidoPaterno: result.apellido1.text(),
                    apellidoMaterno: result.apellido2.text(),
                    sexo: result.sexo.text(),
                    fechaNacimiento: result.fechNac.text(),
                    nacionalidad: result.nacionalidad.text(),
                    documentoProbatorio: result.docProbatorio.text(),
                    anioRegistro: result.anioReg.text(),
                    *foja: result.foja.text(),
                    *tomo: result.tomo.text(),
                    *libro: result.libro.text(),
                    numeroActa: result.numActa.text(),
                    *crip: result.CRIP.text(),
                    claveEntidadRegistro: result.numEntidadReg.text(),
                    claveMunicipioRegistro: result.cveMunicipioReg.text(),
                    *numeroRegistroExtranjero: result.NumRegExtranjeros.text(),
                    *folioCarta: result.FolioCarta.text(),
                    claveEntidadNacimiento: result.cveEntidadNac.text(),
                    *claveEntidadEmisora: result.cveEntidadEmisora.text(),
                    estatusCurp: result.statusCurp.text(),
                    estatusOperacion: result.attribute("statusOper"),
                    mensaje: result.attribute("message"),
                    tipoError: result.attribute("TipoError"),
                    codigoError: result.attribute("CodigoError"),
                    idSesion: result.attribute("SessionID") 
        */
                
        switch (keypar[0]) {
            case "CURP:":
                result.curp = keypar[1]
                break;
            case "Nombre(s):":
                result.nombre = keypar[1]
                break;
            case "Primer apellido:":
                result.apellidoPaterno = keypar[1]
                break;
            case "Segundo apellido:":
                result.apellidoMaterno = keypar[1]
                break;
            case "Sexo:":

                if (keypar[1].equalsIgnoreCase("HOMBRE")) {
                    result.sexo = "H"
                } else if (keypar[1].equalsIgnoreCase("MUJER")) {
                    result.sexo = "M"
                }

                break;
            case "Fecha de nacimiento:":
                result.fechaNacimiento = keypar[1]
                break;
            case "Nacionalidad:":
                result.nacionalidad = keypar[1]
                break;
            case "Entidad de nacimiento:":
                switch (keypar[1]) {
                    case "AGUASCALIENTES":
                        result.claveEntidadNacimiento = "AS"
                        break;
                    case "BAJA CALIFORNIA SUR":
                        result.claveEntidadNacimiento = "BS"
                        break;
                    case "COAHUILA DE ZARAGOZA":
                        result.claveEntidadNacimiento = "CL"
                        break;
                    case "CHIAPAS":
                        result.claveEntidadNacimiento = "CS"
                        break;
                    case "DISTRITO FEDERAL":
                        result.claveEntidadNacimiento = "DF"
                        break;
                    case "GUANAJUATO":
                        result.claveEntidadNacimiento = "GT"
                        break;
                    case "HIDALGO":
                        result.claveEntidadNacimiento = "HG"
                        break;
                    case "MEXICO":
                        result.claveEntidadNacimiento = "MC"
                        break;
                    case "MORELOS":
                        result.claveEntidadNacimiento = "MS"
                        break;
                    case "NUEVO LEON":
                        result.claveEntidadNacimiento = "NL"
                        break;
                    case "PUEBLA":
                        result.claveEntidadNacimiento = "PL"
                        break;
                    case "QUINTANA ROO":
                        result.claveEntidadNacimiento = "QR"
                        break;
                    case "SINALOA":
                        result.claveEntidadNacimiento = "SL"
                        break;
                    case "TABASCO":
                        result.claveEntidadNacimiento = "TC"
                        break;
                    case "TLAXCALA":
                        result.claveEntidadNacimiento = "TL"
                        break;
                    case "YUCATAN":
                        result.claveEntidadNacimiento = "YN"
                        break;
                    case "NACIDO EN EL EXTRANJERO":
                        result.claveEntidadNacimiento = "NE"
                        break;
                    case "BAJA CALIFORNIA":
                        result.claveEntidadNacimiento = "BC"
                        break;
                    case "CAMPECHE":
                        result.claveEntidadNacimiento = "CC"
                        break;
                    case "COLIMA":
                        result.claveEntidadNacimiento = "CM"
                        break;
                    case "CHIHUAHUA":
                        result.claveEntidadNacimiento = "CH"
                        break;
                    case "DURANGO":
                        result.claveEntidadNacimiento = "DG"
                        break;
                    case "GUERRERO":
                        result.claveEntidadNacimiento = "GR"
                        break;
                    case "JALISCO":
                        result.claveEntidadNacimiento = "JC"
                        break;
                    case "MICHOACAN DE OCAMPO":
                        result.claveEntidadNacimiento = "MN"
                        break;
                    case "NAYARIT":
                        result.claveEntidadNacimiento = "NT"
                        break;
                    case "OAXACA":
                        result.claveEntidadNacimiento = "OC"
                        break;
                    case "QUERÉTARO":
                        result.claveEntidadNacimiento = "QT"
                        break;
                    case "SAN LUIS POTOSI":
                        result.claveEntidadNacimiento = "SP"
                        break;
                    case "SONORA":
                        result.claveEntidadNacimiento = "SR"
                        break;
                    case "TAMAULIPAS":
                        result.claveEntidadNacimiento = "TS"
                        break;
                    case "VERACRUZ":
                        result.claveEntidadNacimiento = "VZ"
                        break;
                    case "ZACATECAS":
                        result.claveEntidadNacimiento = "ZS"
                        break;
                }

                break;
            case "Documento probatorio:":
                result.documentoProbatorio = keypar[1]
                break;
            case "Entidad:":
                result.claveEntidadRegistro = keypar[1]
                break;
            case "Municipio:":
                result.claveMunicipioRegistro = keypar[1]
                break;
            case "Año:":
                result.anioRegistro = keypar[1]
                break;
            case "Número de acta:":
                result.numeroActa = keypar[1]
                break;
        }

        return result;
    }

    public boolean validarDatosMinimos(Map result) {
        boolean valido = true;

        if (result.curp == null) {
            valido = false;
        } else if (result.curp == "") {
            valido = false;
        }

        if (result.nombre == null) {
            valido = false;
        } else if (result.nombre == "") {
            valido = false;
        }

        if (result.apellidoPaterno == null) {
            valido = false;
        } else if (result.apellidoPaterno == "") {
            valido = false;
        }

        /*if (result.apellidoMaterno == null) {
            valido = false;
        } else if (result.apellidoMaterno == "") {
            valido = false;
        }*/

        if (result.sexo == null) {
            valido = false;
        } else if (result.sexo == "") {
            valido = false;
        }

        if (result.fechaNacimiento == null) {
            valido = false;
        } else if (result.fechaNacimiento == "") {
            valido = false;
        }

        if (result.claveEntidadNacimiento == null) {
            valido = false;
        } else if (result.claveEntidadNacimiento == "") {
            valido = false;
        }

        return valido;
    }
    
    def guardarDatosCurp(def datos){
        if (!datos) {
            return
        }
        
        def listaCurpValidado = getSession()?.listaCurpValidado
        
        if (!listaCurpValidado) {
            listaCurpValidado = []
        }
        
        def datosMinimos = [:]
        
        if (datos.hecho || datos.curp) {
            
            def existe
            if (listaCurpValidado) {
                existe = listaCurpValidado.find { it -> 
                    it.curp == datos.curp
                }
            }
            
            if (!existe) {
                datosMinimos.curp = datos.curp
                datosMinimos.nombre =  datos.nombre
                datosMinimos.apellidoPaterno =  datos.apellidoPaterno
                datosMinimos.apellidoMaterno =  datos.apellidoMaterno
                datosMinimos.sexo =  datos.sexo
                datosMinimos.fechaNacimiento =  datos.fechaNacimiento
                datosMinimos.claveEntidadNacimiento =  datos.claveEntidadNacimiento
                datosMinimos.metodo = "${datos.metodo} (GUARDADO)"
            }
            
            if (datosMinimos) {
                listaCurpValidado.add(datosMinimos)
            }
            
        }
        
        getSession()?.listaCurpValidado = listaCurpValidado
        
    }
    
    def consultarDatosCurpGuardado(String curp) {
        def result = [
            hecho: false
        ]
        
        def listaCurpValidado = getSession()?.listaCurpValidado
        if (listaCurpValidado) {
            def existe = listaCurpValidado.find { it -> 
                it.curp == curp
            }
            
            if (existe) {
                result.hecho = true
                result.datosMinimos = existe
            }
        }
        
        return result
    }
    
    /*
    public void extraerCookies() {
        BasicCookieStore bcs = new BasicCookieStore();
        for (Cookie ck : cookieStore.getCookies()) {
//            System.out.println("name: " + ck.getName());
//            System.out.println("value: " + ck.getValue());
//            System.out.println("comment: " + ck.getComment());
//            System.out.println("commentURL: " + ck.getCommentURL());
//            System.out.println("expiryDate: " + ck.getExpiryDate());
//            System.out.println("persistent: " + ck.isPersistent());
//            System.out.println("domain: " + ck.getDomain());
//            System.out.println("path: " + ck.getPath());
//            System.out.println("ports: " + ck.getPorts());
//            System.out.println("secure: " + ck.isSecure());
//            System.out.println("version: " + ck.getVersion());
//            System.out.println("expired: " + ck.isExpired(new Date()));
            
            BasicClientCookie cookie = new BasicClientCookie(ck.getName(), ck.getValue());
//            cookie.setComment(ck.getComment());
//            cookie.setExpiryDate(cookie.getExpiryDate());
            cookie.setDomain(ck.getDomain());
//            cookie.setPath(ck.getPath());
//            cookie.setSecure(ck.isSecure());
//            cookie.setVersion(ck.getVersion());


            bcs.addCookie(cookie);
            
        }
        
        cookieStore = bcs;
    }*/
    
    private HttpSession getSession() {
        return RequestContextHolder.currentRequestAttributes().getSession()
    }
}
