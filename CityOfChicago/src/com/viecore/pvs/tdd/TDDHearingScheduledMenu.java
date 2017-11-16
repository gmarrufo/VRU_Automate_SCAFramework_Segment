/*
 * Licensed Materials - Property of Viecore Inc.
 *
 * (C) Copyright Viecore Inc. 2002, 2003 All Rights Reserved
 */

package com.viecore.pvs.tdd;


import com.viecore.pvs.*;
import com.viecore.pvs.transaction.*;
import com.viecore.util.log.*;

import com.ibm.telephony.directtalk.*;
import com.ibm.telephony.beans.media.*;

import java.util.*;
import com.viecore.util.configuration.*;

import com.ibm.telephony.directtalk.*;
import com.ibm.telephony.beans.directtalk.*;
import com.ibm.telephony.beans.*;
import com.ibm.telephony.beans.media.*;

/**
 * This class is a menu that contains functionality to pull data from 
 * the session to speak different TDD segments based on the number of hearings scheduled.
 *
 *  @author Mehmet Tekkarismaz, Thomas Ryan
 *  @since jdk 1.3 
 *  @version 1.0 1/17/2003
 */


public class TDDHearingScheduledMenu extends TDDMenu{
  
  
  /**
    * Constant used to retrieve the number of violations scheduled from the Properties
    */
  public static final String SESSION_NUMBER_SCHEDULED = "NumberOfViolationsScheduled";
    
  /**
    * Constant used to retrieve the header segment to play when one 
    * violation has been scheduled from the Properties
    */
  public static final String CONFIG_ONE_VIOLATION_SCHEDULED = "OneViolationScheduledSegment";
  
  /**
    * Constant used to retrieve the header segment to play notifications for one violation 
    */
  public static final String CONFIG_ONE_VIOLATION_NOTIFICATION = "OneViolationNotificationSegment";
  
  /**
    * Constant used to retrieve the header segment to 
    * play when multiple violations have been scheduled from the Properties
    */
  public static final String CONFIG_MULTIPLE_VIOLATIONS_SCHEDULED = "MultipleViolationScheduledSegment";
  
  /**
    * Constant used to retrieve the header segment to play notifications for multiple violations 
    */
  public static final String CONFIG_MULTIPLE_VIOLATION_NOTIFICATION = "MultipleViolationsNotificationSegment";
  
  /**
    * One violation scheduled message
    */
  private String oneViolationScheduled = null;
  
  /**
    * One violation notification message
    */
  private String oneViolationNotification = null;
  
  /**
    * Multiple violations scheduled message
    */
  private String multipleViolationsScheduled = null;
  
  /**
    * Multiple violations notification message
    */
  private String multipleViolationNotification = null;
  
  
  /**
   *  Object used for Reporting the total number calls for booted inquiries
   */
   
   private LogEventTransaction logEventTransaction = null;
  
  /**
    * This method constructs a LogEventTransaction object and then calls super.initConfiguration.
    *
    * @param properties
    *     Properties object containing the configuration
    * @throws ConfigurationException
    *     if the Properties object does not contain the appropriate properties
    */
  public void initConfiguration(Properties properties) throws ConfigurationException{
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDHearingScheduledMenu.initConfiguration: Entered method.");
    
    //instatiate a LogEventTransaction object, just log any type of error    
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDHearingScheduledMenu.initConfiguration: Instantiating a LogEventTransaction object."); 
    logEventTransaction = new LogEventTransaction();
    logEventTransaction.initLoggerIF(logger);
    logEventTransaction.initConfiguration(properties); 
    
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDHearingScheduledMenu.initConfiguration: About to call super.initConfiguration(properties);."); 
    
    super.initConfiguration(properties);
 
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDHearingScheduledMenu.initConfiguration: Exiting method.");
 
  }//end initConfiguration
  
  /**
    * This method calls logEvent on the LogEventTransaction object and then calls 
    * super.presentMenu. 
    *
    * @param session
    *     Hashtable with call information
    * @param actionStatusEvent
    *     ActionStatusEvent containing the latest call information 
    * @return 
    *     contains the next action to be executed by the application
    */
    
  public NextAction presentMenu(Hashtable session, ActionStatusEvent actionStatusEvent){
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDHearingScheduledMenu.presentMenu: Entered method.");
     
    NextAction next = null;
    
    try{
      if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDHearingScheduledMenu.presentMenu: About to call logEventTransaction.logEvent for HEARINGS");  
      this.logEventTransaction.logEvent(actionStatusEvent,LogEventTransaction.HEARINGS);
    
      next = super.presentMenu(session, actionStatusEvent);
    
    }catch(HungupException he){
      if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDHearingScheduledMenu.presentMenu: HungupException "+he.toString());  
       
    }
        
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDHearingScheduledMenu.presentMenu: Exiting method.");
    
    return next;
    
  }//end presentMenu
  
  /**
    * This method calls setMenuOptions on its parent and then extracts the configuration
    * from the Properties object.
    *
    * @param Properties properties: Must contain the following properties, defined as
    *        instance variables for the class   
    *       
    *       CONFIG_ONE_VIOLATION_SCHEDULED
    *       CONFIG_MULTIPLE_VIOLATIONS_SCHEDULED
    *       CONFIG_ONE_VIOLATION_NOTIFICATION
    *       CONFIG_MULTIPLE_VIOLATION_NOTIFICATION
    *
    * @throws ConfigurationException 
    *     Thrown if one of the properties is missing
    */ 
  public void setMenuOptions(Properties properties)throws ConfigurationException{
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDHearingScheduledMenu.setMenuOptions: Entered method.");
    //null check on incoming Properties object 
    if(properties == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDHearingScheduledMenu.setMenuOptions: Properties object passed in is null.");
      throw new ConfigurationException("TDDHearingScheduledMenu.setMenuOptions: Properties object passed in is null.");
    }  
    
    //call setMenuOptions on parent TDDMenu
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDHearingScheduledMenu.setMenuOptions: Calling TDDMenu's setMenuOptions method.");
    super.setMenuOptions(properties);
    
    //extract the configuration from Properties object
    oneViolationScheduled = validateStringProperty(properties, CONFIG_ONE_VIOLATION_SCHEDULED, true);
    oneViolationNotification = validateStringProperty(properties, CONFIG_ONE_VIOLATION_NOTIFICATION, true);
    multipleViolationsScheduled = validateStringProperty(properties, CONFIG_MULTIPLE_VIOLATIONS_SCHEDULED, true);
    multipleViolationNotification = validateStringProperty(properties, CONFIG_MULTIPLE_VIOLATION_NOTIFICATION, true);
    
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDHearingScheduledMenu.setMenuOptions: Exiting method.");
  }//end setMenuOptions
  
   
  
  
  /**
    * This method retrieves the number of violations scheduled from the session.  
    * The number is used to determine whether the one violation segment is played 
    * or the multiple violation segment is played with the number.  
    * This method retrieves the configured segments and values to create a 
    * String to play.
    *
    * @param session
    *       Hashtable containing the violation object
    * @param locale 
    *       Locale containing language for caller
    * @return String
    *       header message
    * @throws TransferException if the header message cannot be constructed
    */ 
  public String setCallDetails(Hashtable session, Locale locale) throws TransferException{   
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDHearingScheduledMenu.setCallDetails: Entered method.");
    //declare method scope variables
    int violations = 0;
    
    //create a String for header message 
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDHearingScheduledMenu.setCallDetails: Creating a String for header message.");
    String message = "";
    
    //null check for session and locale
    if(session == null){
      if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "TDDHearingScheduledMenu.setCallDetails: HashTable object passed into this method is null.");
      throw new TransferException("TDDHearingScheduledMenu.setCallDetails: HashTable object passed into this method is null.");
    }
    
    //get number of violations scheduled from session
    Object violationsObject = session.get(SESSION_NUMBER_SCHEDULED);
    if(violationsObject == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDHearingScheduledMenu.setMenuOptions: ["+SESSION_NUMBER_SCHEDULED+"] session variable is empty.");
      throw new TransferException("TDDHearingScheduledMenu.setMenuOptions: ["+SESSION_NUMBER_SCHEDULED+"] session variable is empty.");
    }
    
    if(violationsObject instanceof String == false){
       if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDHearingScheduledMenu.setMenuOptions: Violations scheduled variable in the session was not a String");
       throw new TransferException("TDDHearingScheduledMenu.setMenuOptions: Violations scheduled variable in the session was not a String");
    }
      
    String numberOfViolationsScheduled = (String) violationsObject;
    if(logLevel >= LoggerIF.NOTICE) log(LoggerIF.NOTICE, "TDDHearingScheduledMenu.setMenuOptions: ["+SESSION_NUMBER_SCHEDULED+"] = ["+numberOfViolationsScheduled+"].");
      
    //check if there is one or multiple violations
    try{
      violations = Integer.parseInt(numberOfViolationsScheduled);
    }
    catch(NumberFormatException nfe){
      if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "TDDHearingScheduledMenu.setMenuOptions: ["+SESSION_NUMBER_SCHEDULED+"] is not a parseable number.");
      throw new TransferException("TDDHearingScheduledMenu.setMenuOptions: ["+SESSION_NUMBER_SCHEDULED+"] is not a parseable number.");
    }  
    
    if(violations == 1){
      if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDHearingScheduledMenu.setCallDetails: There is one violation.");  
      message = oneViolationScheduled + oneViolationNotification;
    }  
    else if(violations > 1){
      if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDHearingScheduledMenu.setCallDetails: There are multiple violations.");  
      message = numberOfViolationsScheduled + multipleViolationsScheduled + multipleViolationNotification;
    }  
    //return the header
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDHearingScheduledMenu.setCallDetails: Returning the header and leaving method.");
    return message;
  }//end setCallDetails

}//end class























