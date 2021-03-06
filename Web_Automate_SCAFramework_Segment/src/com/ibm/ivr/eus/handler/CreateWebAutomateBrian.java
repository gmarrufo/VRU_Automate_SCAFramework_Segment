package com.ibm.ivr.eus.handler;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Properties;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import com.ibm.ivr.eus.common.GetCurrentTimeStamp;

public class CreateWebAutomateBrian extends HttpServlet implements Servlet{
	private static final long serialVersionUID = 1L;
	private static Logger LOGGER = Logger.getLogger(CreateWebAutomate.class);
	private GetCurrentTimeStamp gcts = null;
	
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    	// Instantiate the DateTimeNow
    	gcts = new GetCurrentTimeStamp();
    	
    	// Get session from Servlet request
    	HttpSession session = request.getSession(false);
    	
		String callid = (String) session.getAttribute("callid");
		boolean testCall = ((Boolean) session.getAttribute("testCall")).booleanValue();

    	// Create the log Token for later use, use StringBuffer to reduce number of String objects
    	String logToken = new StringBuffer("[").append(callid).append("] ").toString();
    	
    	if (testCall){
    		LOGGER.info(new StringBuffer(logToken).append("Entering CreateWebAutomate Handler"));
    	}else{
        	// Start Timer - Create a starter time stamp and put it in the log or saved for later use
    		LOGGER.info(new StringBuffer(logToken).append("Entering CreateWebAutomate Handler at: " + gcts.getDateTimeNow()));
    	}
    	
    	// Get session attributes
    	String automation_data = (String) session.getAttribute("automation_data");
    	String automation_func = (String) session.getAttribute("automation_func");
    	String automation_rc = (String) session.getAttribute("automation_rc");
    	String automation_status = (String) session.getAttribute("automation_status");
    	// String automation_timeout = (String) session.getAttribute("automation_timeout");
    	String automation_type = "GET";
    	String automation_url = (String) session.getAttribute("automation_url");
    	/*
    	String division_code = (String) session.getAttribute("division_code");
    	String store_number = (String) session.getAttribute("store_number");
    	String component_name = (String) session.getAttribute("component_name");
    	String device_type = (String) session.getAttribute("device_type");
    	String register_number = (String) session.getAttribute("register_number");
    	String floor_number = (String) session.getAttribute("floor_number");
    	String contact_extension = (String) session.getAttribute("contact_extension");
    	String device = device_type + register_number;
    	String parameters = "Division=" + URLEncoder.encode(division_code, "UTF-8") + "&Store=" + URLEncoder.encode(store_number, "UTF-8") + "&Component=" + URLEncoder.encode(component_name, "UTF-8") + "&Device=" + URLEncoder.encode(device, "UTF-8") + "&Floor=" + URLEncoder.encode(floor_number, "UTF-8") + "&Extension=" + URLEncoder.encode(contact_extension, "UTF-8");
    	automation_url = automation_url + parameters;
    	*/
    	String parameters = "";
    	
    	// Begin Hold Music
    	// Find out the proper functions in Framework
    	/* holdrc = trexx(Clear_Tones)
    	   holdrc = trexx(Play_Mod_Begin, 'music','0:1','yes','')
    	   IF rc = TREXX_HUP THEN
    	      DO
    	         call_status = 'HUP'
    	         RETURN 0
    	      END
    	*/

    	// Execute the Post
    	String result = executePost(automation_url, parameters, automation_type);

    	// Check if result is good
    	if(result == null){
    		automation_data = "RC=97|Could not communicate with URL";
            automation_rc = "97";
            automation_status = "Could not communicate with URL";    		
            session.setAttribute("automation_data", automation_data);
            session.setAttribute("automation_rc", automation_rc);
            session.setAttribute("automation_status", automation_status);
    	}else{
    		String temp_data = result.toUpperCase();
    		
    		if(result.indexOf("|") != 0){
    			// Strip function in T-REXX which will strip temp_data and get the automation_rc and automation_status values	
                // PARSE VAR temp_data "RC="automation_rc"|"automation_status .
    			automation_status = temp_data.replaceAll("RC='automation_rc'|", "");
	            session.setAttribute("automation_status", automation_status);
    		}else{
    			// Strip function in T-REXX which will strip temp_data and get the automation_rc and automation_status values	
                // PARSE VAR temp_data "RC=" automation_rc "," automation_status
    			automation_status = temp_data.replaceAll("RC=' automation_rc ',", "");
	            session.setAttribute("automation_status", automation_status);
    		}
    		
    		/*
    		IF automation_func = 'HARDWARETICKET' THEN
            DO
               PARSE VAR temp_data "TICKETNUMBER=" problem_number .
               say 'Ticket Number = 'problem_number
            END
    		*/
    		
    		if(automation_func.equals("HARDWARETICKET")){
    			String problem_number = temp_data.replaceAll("TICKETNUMBER=", "");
	            session.setAttribute("problem_number", problem_number);
    		}
    		
    		if(automation_rc.equals("531")){
                automation_status = "HardwareTicket (CreateTicket) SQL 2005 CONNECTION ERROR";
	            automation_data = "RC=531|HardwareTicket (CreateTicket) SQL 2005 CONNECTION ERROR";
	            session.setAttribute("automation_data", automation_data);
	            session.setAttribute("automation_status", automation_status);
    		}
    	}
    	
    	// End Hold Music
    	// Find out the proper functions in Framework
    	/* holdrc = trexx(Play_Mod_End, '1','0')
    	   IF holdrc = TREXX_HUP THEN
    	      DO
    	         call_status = 'HUP'
    	         RETURN 0
    	      END
    	*/
    	
    	if (testCall){
    		LOGGER.info(new StringBuffer(logToken).append("Exiting CreateWebAutomate Handler"));
    	}else{
	    	// Stop Timer - Create a stop time stamp and put it in the log or saved for later use
	    	LOGGER.info(new StringBuffer(logToken).append("Exiting CreateWebAutomate Handler at: " + gcts.getDateTimeNow()));
    	}    	
    	
    	return;
    }
	
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
    
    public String executePost(String targetURL, String urlParameters, String automation_type)  {
    	URL url;
    	HttpURLConnection connection = null;
    	try {
    		//Create connection
    		url = new URL(targetURL);
    		connection = (HttpURLConnection)url.openConnection();
    		connection.setRequestMethod(automation_type);
    		connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
    		connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
    		connection.setRequestProperty("Content-Language", "en-US");
    		connection.setUseCaches (false);
    		connection.setDoInput(true);
    		connection.setDoOutput(true);
    		
    		//Send request
    		DataOutputStream wr = new DataOutputStream (connection.getOutputStream ());
    		wr.writeBytes (urlParameters);
    		wr.flush ();
    		wr.close ();
    		
    		//Get Response
    		InputStream is = connection.getInputStream();
    		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
    		String line;
    		StringBuffer response = new StringBuffer();
    		
    		while((line = rd.readLine()) != null) {
    			response.append(line);
    			response.append('\r');
    		}
    		rd.close();
    		return response.toString();
    	}catch (Exception e){
    		e.printStackTrace();
    		return null;
    	}finally{
    		if(connection != null){
    			connection.disconnect();
    		}
    	}
    }
}