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
import com.ibm.ivr.eus.srv.macys.*;
// import com.ibm.ivr.eus.data.XferProperties;

public class CreateVRUAutomate extends HttpServlet implements Servlet{
	private static final long serialVersionUID = 1L;
	private static Logger LOGGER = Logger.getLogger(CreateVRUAutomate.class);
	private GetCurrentTimeStamp gcts = null;
	private Requester reqt = null;

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
		boolean testAutomationPort = false;

    	// Create the log Token for later use, use StringBuffer to reduce number of String objects
    	String logToken = new StringBuffer("[").append(callid).append("] ").toString();
    	
    	if (testCall){
    		LOGGER.info(new StringBuffer(logToken).append("Entering CreateVRUAutomate Handler"));
    	}else{
        	// Start Timer - Create a starter time stamp and put it in the log or saved for later use
        	String sStartTimer = "";
    		LOGGER.info(new StringBuffer(logToken).append("Entering CreateVRUAutomate Handler at: " + gcts.getDateTimeNow()));
    	}
    	
    	// Get session attributes
    	String automation_data = (String) session.getAttribute("automation_data");
    	String automation_rc = (String) session.getAttribute("automation_rc");
    	String automation_server = (String) session.getAttribute("automation_server");
    	String automation_status = (String) session.getAttribute("automation_status");
    	String automation_string = (String) session.getAttribute("automation_string");
    	String automation_timeout = (String) session.getAttribute("automation_timeout");
    	
    	// Populate automation_port
    	String[] automation_port = null;
    	automation_port = new String[4];
    	automation_port[0] = (String) session.getAttribute("automation_port.0");
    	automation_port[1] = (String) session.getAttribute("automation_port.1");
    	automation_port[2] = (String) session.getAttribute("automation_port.2");
    	automation_port[3] = (String) session.getAttribute("automation_port.3");
    	
    	// If statement to determine if automation_string variable has the proper values
    	if(automation_string.equals("") || automation_string.equals("AUTOMATION_STRING")){
    		automation_data = "RC=53|Missing Automation_String";
    	    automation_rc = "53";
    	    automation_status = "Missing Automation_String";
            session.setAttribute("automation_data", automation_data);
            session.setAttribute("automation_rc", automation_rc);
            session.setAttribute("automation_status", automation_status);
    		return;
    	}
    	
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

    	// Check if valid automation_port values are present otherwise it assigns some predefined ports
    	if(automation_port[0].equals("") || automation_port[0].equals("AUTOMATION_PORT.0") || automation_port[0].equals("0")){
    		for(int i = 0; i < 4; i++ ){
    			if(automation_port[i].equals("") || automation_port[i].equals("AUTOMATION_PORT")){
    				testAutomationPort = true;
    			}
    		}
    	}

    	if(testAutomationPort){
            automation_port[0] = "1";
            automation_port[1] = "22223";
    	}else{
            automation_port[0] = "1";
            automation_port[1] = automation_port[0]; // Is this correct?
    	}
    	
    	// Assign values to variables
    	int autorc = -1;
    	int counter = 0;
    	
    	// Instantiate the Requester
    	reqt = new Requester();
    	
    	// While statement based on autorc and counter
    	while(autorc != 0 && counter < Integer.parseInt(automation_port[0])){
    		counter = counter+1;
    	    automation_data = "";

    	    //automation_port = automation_port[counter]; Need input from Dan Abrams
    	      
    	    // Execute the Socket
    	    autorc = reqt.run(automation_server, automation_port, automation_string);

    	    // Switch statement based on return value from Requester
    	    switch(autorc){
    	    	case 0:
    	    		if(reqt.message.indexOf("|") != 0){
    	    			// Strip function in T-REXX which will strip automation_data (reqt.message) and get the automation_rc and automation_status values
    	                // PARSE VAR reqt.message "RC=" automation_rc "|" automation_status
    	    		}else{
    	    			// Strip function in T-REXX which will strip automation_data (reqt.message) and get the automation_rc and automation_status values	
    	                // PARSE VAR reqt.message "RC=" automation_rc "," automation_status
    	    		}
    	    		
    	    		// automation_func variable is not present anywhere
    	    		/*
    	            IF automation_func = 'HARDWARETICKET' THEN
    	            DO
    	               PARSE VAR automation_status "TICKETNUMBER=" problem_number .
    	               say 'Ticket Number = 'problem_number
    	            END
    	    		*/
    	    		
    	    		if(automation_rc.equals("-1")){
    	    			autorc = Integer.parseInt(automation_rc);
    	    		}
    	    		break;
    	    	case 51:
	    	       automation_data = "RC=51|Timeout waiting for Response from " + automation_server;
	               automation_rc = "51";
	               automation_status = "No Response from " + automation_server;
	               session.setAttribute("automation_data", automation_data);
	               session.setAttribute("automation_rc", automation_rc);
	               session.setAttribute("automation_status", automation_status);
    	    		break;
    	    	case 50:
    	    		automation_data = "RC=50|Could Not Communicate with " + automation_server;
    	    		automation_rc = "50";
    	    		automation_status = "Could Not Communicate with " + automation_server;
  	            	session.setAttribute("automation_data", automation_data);
  	            	session.setAttribute("automation_rc", automation_rc);
  	            	session.setAttribute("automation_status", automation_status);
    	    		break;
    	    	default:
    	    		automation_data = "RC=52|Unexpected return";
    	            automation_rc = "52";
    	            automation_status = "Unexpected return";
    	            session.setAttribute("automation_data", automation_data);
    	            session.setAttribute("automation_rc", automation_rc);
    	            session.setAttribute("automation_status", automation_status);
    	    		break;
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
    		LOGGER.info(new StringBuffer(logToken).append("Exiting CreateVRUAutomate Handler"));
    	}else{
	    	// Stop Timer - Create a stop time stamp and put it in the log or saved for later use
	    	String sStopTimer = "";
	    	LOGGER.info(new StringBuffer(logToken).append("Exiting CreateVRUAutomate Handler at: " + gcts.getDateTimeNow() ));
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
}