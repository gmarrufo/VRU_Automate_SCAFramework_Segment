//
// Generated By:JAX-WS RI IBM 2.1.6 in JDK 6 (JAXB RI IBM JAXB 2.1.10 in JDK 6)
//

package com.ibm.ivr.eus.websrv.belk;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

@WebService(name = "EUSTTBelk", targetNamespace = "http://belk.eus.ibm.com")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface EUSTTBelk {
    /**
     * 
     * @param xReportsToPrint
     * @param xPrinterName
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(name = "xPrintOverNightReportsReturn", targetNamespace = "")
    @RequestWrapper(localName = "xPrintOverNightReports", targetNamespace = "http://belk.eus.ibm.com", className = "com.ibm.ivr.eus.websrv.belk.XPrintOverNightReports")
    @ResponseWrapper(localName = "xPrintOverNightReportsResponse", targetNamespace = "http://belk.eus.ibm.com", className = "com.ibm.ivr.eus.websrv.belk.XPrintOverNightReportsResponse")
    public String xPrintOverNightReports(
        @WebParam(name = "xPrinterName", targetNamespace = "")
        String xPrinterName,
        @WebParam(name = "xReportsToPrint", targetNamespace = "")
        String xReportsToPrint);
}