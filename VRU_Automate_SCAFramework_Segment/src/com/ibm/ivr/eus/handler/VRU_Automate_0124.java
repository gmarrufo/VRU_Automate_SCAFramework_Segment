package com.ibm.ivr.eus.handler;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.ibm.ivr.eus.websv.Socket_Connection;
import org.apache.log4j.Logger;

public class VRU_Automate_0124  extends HttpServlet implements Servlet {
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static final String fileversion = " %1.14% ";

	private static Logger LOGGER = Logger.getLogger(VRU_Automate.class);

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {

		// get session from Servlet request, created if not existed yet
		HttpSession session = req.getSession(true);

		String callid = (String) session.getAttribute("callid");

		boolean testCall = ((Boolean) session.getAttribute("testCall")).booleanValue();

		//create the log Token for later use, use StringBuffer to reduce number
		// of String objects
		String logToken = new StringBuffer("[").append(callid).append("] ").toString();

		if (testCall)
			LOGGER.debug(new StringBuffer(logToken).append("Entering VRU_Automate handler"));


		// Get attributes from the request	
		String automation_data = (String) session.getAttribute("automation_data");
		String automation_port = (String) session.getAttribute("automation_port");
		String automation_rc = (String) session.getAttribute("automation_rc");
		String automation_server = (String) session.getAttribute("automation_server");
		String automation_status = (String) session.getAttribute("automation_status");
		String automation_string = (String) session.getAttribute("automation_string");
		String automation_timeout = (String) session.getAttribute("automation_timeout");
		String serial = (String) session.getAttribute("serial");
		String serial_cc = (String) session.getAttribute("serial_cc");
		String strRequest = null;
		String strResponse = null;
		Socket_Connection sc = null;

		int iAutomation_port;
		int iAutomation_timeout;
		long startTime = System.currentTimeMillis();

		// Debug Logging		
		if (testCall){
			LOGGER.info(new StringBuffer(logToken).append("automation_data = ").append(automation_data));	
			LOGGER.info(new StringBuffer(logToken).append("automation_port = ").append(automation_port));	
			LOGGER.info(new StringBuffer(logToken).append("automation_rc = ").append(automation_rc));	
			LOGGER.info(new StringBuffer(logToken).append("automation_server = ").append(automation_server));	
			LOGGER.info(new StringBuffer(logToken).append("automation_status = ").append(automation_status));	
			LOGGER.info(new StringBuffer(logToken).append("automation_string = ").append(automation_string));	
			LOGGER.info(new StringBuffer(logToken).append("serial = ").append(serial));
			LOGGER.info(new StringBuffer(logToken).append("serial_cc = ").append(serial_cc));			
			LOGGER.info(new StringBuffer(logToken).append("automation_timeout = ").append(automation_timeout));	
		}

		automation_rc = null;

		if (automation_port != null) {
			iAutomation_port = Integer.parseInt(automation_port);
		} else {
			iAutomation_port = 9999;
		}

		if (automation_timeout != null) {
			iAutomation_timeout = Integer.parseInt(automation_timeout);
		} else {
			iAutomation_timeout = 60;
		}

		// if automation_string is null or empty or default then set return code
		if ((automation_string == null) || automation_string.length() == 0 || (automation_string.equals("AUTOMATION_STRING"))) {
			automation_data = "RC=53|Missing Automation_String";
			automation_rc = "53";
			automation_status = "Missing Automation_String";
		} else {

			strRequest= automation_string;
			strResponse = null;

			// in test mode can set session variable automation_test to "true"
			// session variable automation_test_data is parsed to get the returned values 
			String automation_test = (String) session.getAttribute("automation_test");
			if(automation_test == null || !automation_test.equalsIgnoreCase("true")) {
				sc = new Socket_Connection();
				strResponse = sc.vru_automate(automation_server,iAutomation_port,strRequest,iAutomation_timeout, testCall, callid);
			} else {
				strResponse = (String) session.getAttribute("automation_test_data");
				if (testCall)
					LOGGER.info(new StringBuffer(logToken).append("automation_test is true, using automation_test_data = ").append(strResponse));
			}
			//RSA att global, UM voicemail, 
			// sim RSAPW Reset SUCCESS
			//strResponse = "RC=0,PASSWORD=NewPassword";
			// sim VMPW Reset SUCCESS
			//strResponse = "RC=0,(Successful,HOSTPASSWORD,Application=HOSTUSA,Serial=653006,Country=897)";
			// sim VMPW Reset FAIL
			//strResponse = "RC=100,CAUSE=(GetBluePagesFailed,HOSTPASSWORD,Application=HOSTUSA,Serial=653006,Country=xxx)";
			//strResponse = "RC=200,CAUSE=(NoIdLocated,HOSTPASSWORD,Application=HOSTUSA,Serial=653006,Country=897)";		
	
			automation_data = strResponse;
	
			if (strResponse != null && strResponse.length() >= 4) {
				int iRC = strResponse.indexOf("RC=");
				int iComma=strResponse.indexOf(",");
				if (iComma != -1) {
					automation_rc = strResponse.substring(iRC+3,iComma);
				} else 	if (iRC != -1) {
					// Only RC=xxx , nothing else
					automation_rc = strResponse.substring(iRC+3);
				}
			} else {
				automation_rc = new String("99");
			}
		}

		// Set session variables needed for reply
		session.setAttribute("automation_rc", automation_rc);	
		session.setAttribute("automation_status", automation_status);	
		session.setAttribute("automation_data", automation_data);
		long endTime = System.currentTimeMillis();

		Float seconds = (endTime - startTime) / 1000F;
		session.setAttribute("stopwatch_time", seconds.toString());

		// Set session variables needed for reply
		if (testCall) {
			LOGGER.info(new StringBuffer(logToken).append("returning automation_rc:").append(automation_rc));
			LOGGER.info(new StringBuffer(logToken).append("returning automation_status:").append(automation_status));
			LOGGER.info(new StringBuffer(logToken).append("returning automation_data:").append(automation_data));
			LOGGER.info(new StringBuffer(logToken).append("returning stopwatch_time:").append(seconds.toString()));
			LOGGER.info(new StringBuffer(logToken).append("Leaving VRU_Automate handler"));
		}	
		return;
	}
}