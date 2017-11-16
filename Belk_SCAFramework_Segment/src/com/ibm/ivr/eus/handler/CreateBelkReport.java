package com.ibm.ivr.eus.handler;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Properties;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ibm.ivr.eus.websrv.belk.EUSTTBelkService;
import com.ibm.ivr.eus.websrv.belk.EUSTTBelk;

import org.apache.log4j.Logger;

import com.ibm.ivr.eus.data.XferProperties;

import com.ibm.ivr.eus.common.LFileFunctions;

public class CreateBelkReport extends HttpServlet implements Servlet {

	private static final long serialVersionUID 					= 1L;
	private static Logger LOGGER 								= Logger.getLogger(CreateBelkReport.class);
	private static HashMap<String,String> call_values			=new HashMap<String,String>();
	private static String REPLACE_PRINTER_NAME					="REPLACE-PRINTERNAME";
	private static String REPLACE_REPORT_TYPE					="REPLACE-REPORTTYPE";

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    	
		// get session from Servlet request
		HttpSession session = request.getSession(false);
		initCallValues(session);
		
		if(call_values.get("servlet_flag").toString().equals("1")){
			processEUSTTSOAP_post(request,response);
			return;
		}
		String callid = (String) call_values.get("callid");
		boolean testCall = Boolean.getBoolean(call_values.get("testCall"));

		//create the log Token for later use, use StringBuffer to reduce number of String objects
		String logToken = new StringBuffer("[").append(callid).append("] ").toString();
		if (testCall)
			LOGGER.info(new StringBuffer(logToken).append("Entering CreateBelkReport Handler"));
		
		String report_url=(String)call_values.get("report_url");
		String printer_name=(String)call_values.get("printer_name");
		String report_type=(String)call_values.get("report_type");
		
		EUSTTBelkService service=new EUSTTBelkService();
		EUSTTBelk port=service.getEUSTTBelk();
		String result=port.xPrintOverNightReports(printer_name, report_type);
		String response_XMLFile=(String)call_values.get("response_XMLFile");
		if(result.length() > 0){
			LFileFunctions.writeToFile(logToken, response_XMLFile, result, false);
		}
		session.setAttribute("printer_response", result);
		return;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /*****************************************************************************
     *                             - EUSTTSOAP_post -                            *
     * This routine is for the new EUS Tools Team Web Service Survey Solution.   *
     * This routine processed the soap XML file Built previously and processes   *
     * the xml file via a webservice. This routine then parses out the returned  *
     * values of the XML to determin success or failure.                         *
     *---------------------------------------------------------------------------*
     * Return Codes:                                                             *
     *     0 - Successfully Processed request                                    *
     *    99 - Returned from WEBCONNECT automation_URL was not set or passed     *
     *    98 - Returned from WEBCONNECT automation_type was not set or passed    *
     *    97 - Returned from WEBCONNECT Timed out trying to Communicate to URL   *
     *    96 - Returned from WEBCONNECT XML file doesn't exist                   *
     *    94 - Generated here EUSTTSurvey_post unkown status from webservice     *
     *    93 - caller hung up during submission                                  * 
     *  (From web service denoting failure to process)                           *
     *  5300 - 5319 -> EUS Survey Codes                                          *
     *  26011 - 26220 -> Belk Automation                                         *
     *****************************************************************************/   
    protected void processEUSTTSOAP_post(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
 
		// get session from Servlet request
		HttpSession session = request.getSession(false);
		initCallValues(session);
		
		String callid = (String) call_values.get("callid");
		boolean testCall = Boolean.parseBoolean(call_values.get("testCall"));

		//create the log Token for later use, use StringBuffer to reduce number of String objects
		String logToken = new StringBuffer("[").append(callid).append("] ").toString();
		if (testCall)
			LOGGER.info(new StringBuffer(logToken).append("Entering CreateBelkReport Handler"));
		
		if(isValidURL()){
			callWebService();
		}
		
		if(testCall){
			LOGGER.debug(new StringBuffer(logToken).append("Returning automation_rc==" + call_values.get("automation_rc")));
			LOGGER.debug(new StringBuffer(logToken).append("Returning automation_data==" + call_values.get("automation_data")));
		}
		
		session.setAttribute("automation_rc", call_values.get("automation_rc"));
		session.setAttribute("automation_data", call_values.get("automation_data"));
    }
    
    /**
     * InitCallValues - initializes the HashMap with the values from the callflow
     * @param session
     */
    private void initCallValues(HttpSession session){
    	try{
    		String report_url			=(String)session.getAttribute("report_url");
    		String printer_name			=(String)session.getAttribute("printer_name");
    		String report_type			=(String)session.getAttribute("report_type");
    		String automation_url		=(String)session.getAttribute("automation_url");
    		String automation_type		=(String)session.getAttribute("automation_type");
    		String automation_data		=(String)session.getAttribute("automation_data");
    		String soap_XMLFile			=(String)session.getAttribute("soap_XMLFile");
    		String response_XMLFile		=(String)session.getAttribute("response_XMLFile");
    		String automation_rc		=(String)session.getAttribute("automation_rc");
    		String callid 				=(String)session.getAttribute("callid");
    		String testCall				=(String)session.getAttribute("testCall");
    		String xmlTemplateFile		=(String)session.getAttribute("xml_report_template");
    		String automation_timeout	=(String)session.getAttribute("automation_timeout");
    		String servlet_flag			=(String)session.getAttribute("servlet_flag");
    		
    		// Now put into HashMap
    		call_values.clear();
    		call_values.put("report_url"		, report_url);
    		call_values.put("printer_name"		, printer_name);
    		call_values.put("report_type"		, report_type);
    		call_values.put("automation_url"	, automation_url);
    		call_values.put("automation_type"	, automation_type);
    		call_values.put("automation_data"	, automation_data);
    		call_values.put("soap_XMLFile"		, soap_XMLFile);
    		call_values.put("response_XMLFile"	, response_XMLFile);
    		call_values.put("automation_rc"		, automation_rc);
    		call_values.put("callid"            , callid);
    		call_values.put("testCall"          , testCall);
    		call_values.put("xml_report_template", xmlTemplateFile);
    		call_values.put("automation_timeout", automation_timeout);
    		call_values.put("servlet_flag"		, servlet_flag);
    		
    	}catch(Exception e){
    		LOGGER.error("Error Initializing call values Hash Map:"+e.toString());
    	}
    	
    	return;
    }
   
    /**
     * isValidURL - checks for a valid URL
     * @return
     */
    private boolean isValidURL(){
    	boolean valid=true;
    	
		Object automation_url=call_values.get("automation_url");
		Object automation_type=call_values.get("automation_type");
		
		if((automation_url == null) || automation_url.toString().equals("")){
			call_values.put("automation_data", "RC=99|AUTOMATION_URL not passed");
			call_values.put("automation_rc", "99");
			return false;
		}
		
		if((automation_type == null) || automation_type.toString().equals("")){
			call_values.put("automation_data", "RC=98|AUOTMATION_TYPE not passed");
			call_values.put("automation_rc", "98");			
			return false;
		}		
		
    	return valid;
    }
    
    /**
     * callWebService - Calls the web service at the "automation_url".
     */
    private void callWebService(){
       	String soapMessage="";
    	String soapAction="";
    	StringBuffer outputString=new StringBuffer();
    	String responseString="";
    	HttpURLConnection httpConn=null;
    	InputStreamReader isr=null;
    	BufferedReader in=null;
    	OutputStream out=null;
    	OutputStreamWriter br=null;
    	int timeoutValue=30;
    	try{
			long start=System.currentTimeMillis();
    		String automation_url=call_values.get("automation_url");
    		String automation_type=call_values.get("automation_type");
    		Object automation_timeout=call_values.get("automation_timeout");
    		String eol = System.getProperty("line.separator");
    		boolean testCall= (Boolean)Boolean.parseBoolean(call_values.get("testCall").toString());
    		if(automation_timeout != null && !automation_timeout.equals("") )
    			timeoutValue=Integer.parseInt(automation_timeout.toString());
    		
    		URL wsURL=new URL(automation_url);
    		URLConnection connection=wsURL.openConnection();
    		httpConn=(HttpURLConnection)connection;
    		ByteArrayOutputStream bOut=new ByteArrayOutputStream();
    		
    		soapAction="urn:getMessage";
    		soapMessage=getXMLContentsFromTemplate(call_values.get("xml_report_template"));
    		if(soapMessage.length() < 1){
    			return;
    		}
    		soapMessage=soapMessage.replace(REPLACE_PRINTER_NAME,call_values.get("printer_name"));
    		soapMessage=soapMessage.replace(REPLACE_REPORT_TYPE, call_values.get("report_type"));
    		if(testCall)
    			LOGGER.debug("SoapMessage:"+soapMessage);
    		
    		byte[] buffers=new byte[soapMessage.length()];
    		buffers=soapMessage.getBytes();
    		bOut.write(buffers);
    		
    		byte[] byteArray=bOut.toByteArray();
    		
    		httpConn.setRequestProperty("SOAPAction", soapAction);   		
    		httpConn.setRequestProperty("Content-Type", "text/xml");
    		httpConn.setRequestMethod(automation_type);
    		httpConn.setConnectTimeout(timeoutValue);
    		httpConn.setDoOutput(true);
    		httpConn.setDoInput(true);
    		
    		br=new OutputStreamWriter(httpConn.getOutputStream());
 
    	    br.write(soapMessage);
    	    br.flush();
   		    
   		    //Read the response.
   		     isr =  new InputStreamReader(httpConn.getInputStream());
   		     in = new BufferedReader(isr);
   		     //Write the SOAP message response to a String.
   		     while ((responseString = in.readLine()) != null) {
   		         outputString.append(responseString).append(eol);
   		     }
			long finish=System.currentTimeMillis();
			long elapsed=finish-start;
			call_values.put("automation_data", outputString.toString());
			call_values.put("automation_rc", "0");		   		     
			if(testCall){
				LOGGER.debug("ELAPSED TIME FOR WEBSERVICE:"+elapsed);
				LOGGER.debug("OutputString=="+outputString.toString());
			}
    	}catch(IOException io){
			call_values.put("automation_data", "RC=93|:"+io.toString());
			call_values.put("automation_rc", "93");		   		
    		LOGGER.error("Error:" +io.toString(),io);
    	}catch(Exception e){
			call_values.put("automation_data", "RC=92|Error--"+e.toString());
			call_values.put("automation_rc", "92");		    		
    		LOGGER.error("Error:" +e.toString(),e);
    	}
    	finally{
    		try{
    			if(in != null){
    				in.close();
    			}
    			if (httpConn != null){
    				httpConn.disconnect();
    			}
    			if(isr != null){
    				isr.close();
    			}
    			if(out != null){
    				out.close();
    			}
    			if(br != null){
    				br.close();
    			}
    		}catch(IOException ioe){
    			LOGGER.error("Finally IOException:"+ioe.toString(), ioe);
    		}
    	}   	
    }
    
	   /**
	    * Reads the contents of the XML Template that will be used to send the soap Request to WebService
	    * @param filename
	    * @return
	    */
	   public String getXMLContentsFromTemplate(String filename){
		   StringBuffer xml_data=new StringBuffer();
		   FileInputStream fstream=null;
		   DataInputStream dstream=null;
		   BufferedReader bReader=null;
		   String strLine="";
		   File file=null;
		   String eol = System.getProperty("line.separator");
		   try{
			   file = new File(filename);
			   if(!file.exists()){
					call_values.put("automation_data", "RC=96|Specified SOAP or XML File not found");
					call_values.put("automation_rc", "96");		
					return "";
			   }
			   fstream=new FileInputStream(filename);
			   dstream=new DataInputStream(fstream);
			   bReader=new BufferedReader(new InputStreamReader(dstream));
			   
			   while ((strLine = bReader.readLine()) != null)   {
				   xml_data.append(strLine).append(eol);
			   }			   
		   }catch(Exception e){
			   LOGGER.error("Error in getXMLContentsFromTemplate:"+e.toString());
		   }finally{
			   try{
				   if(fstream != null){
					   fstream.close();
				   }
				   if(dstream != null){
					   dstream.close();
				   }		
				   if(bReader != null){
					   bReader.close();
				   }
			   }catch(IOException io){
				   LOGGER.error("Error Closing Readers in getXMLContentsFromTemplate--"+io.toString());
			   }
		   }
		   return xml_data.toString();
	   }
}