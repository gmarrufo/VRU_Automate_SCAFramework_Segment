//
// Generated By:JAX-WS RI IBM 2.1.6 in JDK 6 (JAXB RI IBM JAXB 2.1.10 in JDK 6)
//

package com.ibm.ivr.eus.websrv.belk;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

@WebServiceClient(name = "EUSTTBelkService", targetNamespace = "http://belk.eus.ibm.com", wsdlLocation = "WEB-INF/wsdl/EUSTTBelk.wsdl")
public class EUSTTBelkService
    extends Service
{
    private final static URL EUSTTBELKSERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(com.ibm.ivr.eus.websrv.belk.EUSTTBelkService.class.getName());

    static {
        URL url = null;
        try {
            url = com.ibm.ivr.eus.websrv.belk.EUSTTBelkService.class.getResource("/WEB-INF/wsdl/EUSTTBelk.wsdl");
            if (url == null) throw new MalformedURLException("/WEB-INF/wsdl/EUSTTBelk.wsdl does not exist in the module.");
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'WEB-INF/wsdl/EUSTTBelk.wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        EUSTTBELKSERVICE_WSDL_LOCATION = url;
    }

    public EUSTTBelkService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public EUSTTBelkService() {
        super(EUSTTBELKSERVICE_WSDL_LOCATION, new QName("http://belk.eus.ibm.com", "EUSTTBelkService"));
    }

    /**
     * 
     * @return
     *     returns EUSTTBelk
     */
    @WebEndpoint(name = "EUSTTBelk")
    public EUSTTBelk getEUSTTBelk() {
        return super.getPort(new QName("http://belk.eus.ibm.com", "EUSTTBelk"), EUSTTBelk.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns EUSTTBelk
     */
    @WebEndpoint(name = "EUSTTBelk")
    public EUSTTBelk getEUSTTBelk(WebServiceFeature... features) {
        return super.getPort(new QName("http://belk.eus.ibm.com", "EUSTTBelk"), EUSTTBelk.class, features);
    }
}