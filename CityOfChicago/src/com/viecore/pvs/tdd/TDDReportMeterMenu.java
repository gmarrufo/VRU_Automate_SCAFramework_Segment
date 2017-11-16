/*
 * Licensed Materials - Property of Viecore Inc.
 *
 * (C) Copyright Viecore Inc. 2002, 2003 All Rights Reserved
 */

package com.viecore.pvs.tdd;



import com.viecore.pvs.*;
import com.viecore.pvs.business.*;
import com.viecore.pvs.transaction.*;
import com.viecore.pvs.util.*;
import com.viecore.util.log.*;

import com.viecore.util.configuration.*;

import com.ibm.telephony.beans.*;

import com.ibm.telephony.directtalk.*;
import com.ibm.telephony.beans.directtalk.*;
import com.ibm.telephony.beans.*;
import com.ibm.telephony.beans.media.*;

import java.util.*;

/**
 * This class is a menu that contains functionality to place the 
 * meter type problem selection into the session.
 *
 *  @author Michael Ruggiero, Thomas Ryan
 *  @since jdk 1.3 
 *  @version 1.0 1/22/2003
 */


public class TDDReportMeterMenu extends TDDMenu{
  
  
  /**
    * Constant to retrieve register time key from Properties
    */
  public static final String CONFIG_REGISTER_TIME_KEY = "RegisterTimeKey";
    
  /**
    * Constant to retrieve running fast key from Properties
    */
  public static final String CONFIG_RUNNING_FAST_KEY = "RunningFastKey";
    
  /**
    * Constant to retrieve coin accept key from Properties
    */
  public static final String CONFIG_COIN_ACCEPT_KEY = "CoinAcceptKey";
    
  /**
    * Constant to retrieve handle broken key from Properties
    */
  public static final String CONFIG_HANDLE_BROKEN_KEY = "HandleBrokenKey";
  
  /**
    * Constant to retrieve meter object from session
    */
  public static final String SESSION_METER = "Meter";
  
  /**
    * Constant to hold fully qualified Meter class name
    */
  public static final String METER_CLASS_NAME = "com.viecore.pvs.business.Meter";
  
  /**
    * Key pressed to select a meter reason of meter not registering time
    */
  private String registerTimeKey = null;
  
  /**
    * Key pressed to select a meter reason of meter running fast
    */
  private String runningFastKey = null;
  
  /**
    * Key pressed to select a meter reason of meter not accepting coin
    */
  private String coinAcceptKey = null;
  
  /**
    * Key pressed to select a meter reason of meter handle broken
    */
  private String handleBrokenKey = null;
  
  
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
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDReportMeterMenu.initConfiguration: Entered method.");
    
    //instatiate a LogEventTransaction object, just log any type of error    
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDReportMeterMenu.initConfiguration: Instantiating a LogEventTransaction object."); 
    logEventTransaction = new LogEventTransaction();
    logEventTransaction.initLoggerIF(logger);
    logEventTransaction.initConfiguration(properties); 
    
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDReportMeterMenu.initConfiguration: About to call super.initConfiguration(properties);."); 
    
    super.initConfiguration(properties);
 
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDReportMeterMenu.initConfiguration: Exiting method.");
 
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
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDReportMeterMenu.presentMenu: Entered method.");
     
    NextAction next = null;
    
    try{
      if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDReportMeterMenu.presentMenu: About to call logEventTransaction.logEvent for METER_PROBLEMS");  
      this.logEventTransaction.logEvent(actionStatusEvent,LogEventTransaction.METER_PROBLEMS);
    
      next = super.presentMenu(session, actionStatusEvent);
    
    }catch(HungupException he){
      if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDReportMeterMenu.presentMenu: HungupException "+he.toString());  
       
    }
        
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDReportMeterMenu.presentMenu: Exiting method.");
    
    return next;
    
  }//end presentMenu
  
  
  
  /**
    * This method calls setMenuOptions on its parent and then extracts the configuration
    * from the Properties object.
    *
    * @param properties: Must contain the following properties, defined as instance variables for the class
    * <pre>              
    *       CONFIG_REGISTER_TIME_KEY
    *       CONFIG_RUNNING_FAST_KEY
    *       CONFIG_COIN_ACCEPT_KEY
    *       CONFIG_HANDLE_BROKEN_KEY
    * </pre>
    * @throws ConfigurationException 
    *     Thrown if one of the properties is missing
    */ 
  public void setMenuOptions(Properties properties)throws ConfigurationException{
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDReportMeterMenu.setMenuOptions: Entered method.");
    //null check on incoming Properties object 
    if(properties == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDReportMeterMenu.setMenuOptions: Properties object passed in is null.");
      throw new ConfigurationException("TDDReportMeterMenu.setMenuOptions: Properties object passed in is null.");
    }  
    
    //call setMenuOptions on parent TDDMenu
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDReportMeterMenu.setMenuOptions: Calling TDDMenu's setMenuOptions method.");
      super.setMenuOptions(properties);
    
    //extract the configuration from Properties object
    registerTimeKey = properties.getProperty(CONFIG_REGISTER_TIME_KEY);
    if((registerTimeKey == null) || (registerTimeKey.length() == 0)){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDReportMeterMenu.setMenuOptions: ["+CONFIG_REGISTER_TIME_KEY+"] property is missing.");
      throw new ConfigurationException("TDDReportMeterMenu.setMenuOptions: ["+CONFIG_REGISTER_TIME_KEY+"] property is missing.");  
    }  
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDReportMeterMenu.setMenuOptions: ["+CONFIG_REGISTER_TIME_KEY+"] = ["+registerTimeKey+"].");
    
    runningFastKey = properties.getProperty(CONFIG_RUNNING_FAST_KEY);
    if((runningFastKey == null) || (runningFastKey.length() == 0)){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDReportMeterMenu.setMenuOptions: ["+CONFIG_RUNNING_FAST_KEY+"] property is missing.");
      throw new ConfigurationException("TDDReportMeterMenu.setMenuOptions: ["+CONFIG_RUNNING_FAST_KEY+"] property is missing.");  
    }  
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDReportMeterMenu.setMenuOptions: ["+CONFIG_RUNNING_FAST_KEY+"] = ["+runningFastKey+"].");
    
    coinAcceptKey = properties.getProperty(CONFIG_COIN_ACCEPT_KEY);
    if((coinAcceptKey == null) || (coinAcceptKey.length() == 0)){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDReportMeterMenu.setMenuOptions: ["+CONFIG_COIN_ACCEPT_KEY+"] property is missing.");
      throw new ConfigurationException("TDDReportMeterMenu.setMenuOptions: ["+CONFIG_COIN_ACCEPT_KEY+"] property is missing.");  
    }  
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDReportMeterMenu.setMenuOptions: ["+CONFIG_COIN_ACCEPT_KEY+"] = ["+coinAcceptKey+"].");
    
    handleBrokenKey = properties.getProperty(CONFIG_HANDLE_BROKEN_KEY);
    if((handleBrokenKey == null) || (handleBrokenKey.length() == 0)){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDReportMeterMenu.setMenuOptions: ["+CONFIG_HANDLE_BROKEN_KEY+"] property is missing.");
      throw new ConfigurationException("TDDReportMeterMenu.setMenuOptions: ["+CONFIG_HANDLE_BROKEN_KEY+"] property is missing.");  
    }  
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDReportMeterMenu.setMenuOptions: ["+CONFIG_HANDLE_BROKEN_KEY+"] = ["+handleBrokenKey+"].");
    
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDReportMeterMenu.setMenuOptions: Leaving method.");
  }//end setMenuOptions
  
  
  /**
    * This method calls the parent getMenuSelection.  
    * In addition to retrieving the selection, it retrieves the meter 
    * from the session and updates it with the appropriate meter problem selected from the menu.
    *
    * @param session
    *       Hashtable containing the violation object
    *
    * @param actionStatusEvent
    *       ActionStatusEvent containing the latest call information
    * @return 
    * provides caller with next action to take
    * @throws FailureTransferException if there is an unknown failure
    */ 
  public String getMenuSelection(ActionStatusEvent actionStatusEvent, Hashtable session) throws FailureTransferException{   
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDReportMeterMenu.getMenuSelection: Entered method.");
    //declare method scope variables
    String meterProblem = null;
    String userSelection = null;
    //null check for session
    if(session == null){
      if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "TDDReportMeterMenu.setCallDetails: HashTable object passed into this method is null.");
    }
    else{
      //get meter oject from session
      if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDReportMeterMenu.getMenuSelection: Getting meter object from session.");
      Object object = session.get(SESSION_METER);
      if(object != null){
        Class classType = object.getClass();
        String className = classType.getName();
        if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDReportMeterMenu.getMenuSelection: The object's class name is ["+className+"]");
        if(METER_CLASS_NAME.equals(className)){      
          //cast meter to a Meter object
          Meter meter = (Meter) object;  
          //call parents getMenuSelection method to get user selection
          if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDReportMeterMenu.getMenuSelection: Calling TDDMenu's getMenuSelection method.");
          userSelection = super.getMenuSelection(actionStatusEvent, session);
          if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDReportMeterMenu.getMenuSelection: Selected key is ["+userSelection+"]");
          
          //determine meter problem using selection
          if((userSelection == null) || (userSelection.length() == 0)){
            if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDReportMeterMenu.getMenuSelection: User selection is received as null.");
          }
          else{  
            if(userSelection.equals(this.registerTimeKey)){  
              if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDReportMeterMenu.getMenuSelection: Meter problem is : Does not register time.");
              //set meter problem in object
              if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDReportMeterMenu.getMenuSelection: Setting problem in meter object as : Does not register time.");
              meter.setMeterProblemType(Meter.PROBLEM_TYPE_DOES_NOT_REGISTER_TIME);
            }
            else if(userSelection.equals(this.runningFastKey)){
              if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDReportMeterMenu.getMenuSelection: Meter problem is : Running fast.");
              //set meter problem in object
              if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDReportMeterMenu.getMenuSelection: Setting problem in meter object as : Running fast.");
              meter.setMeterProblemType(Meter.PROBLEM_TYPE_RUNNING_FAST);
            }  
            else if(userSelection.equals(this.coinAcceptKey)){
              if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDReportMeterMenu.getMenuSelection: Meter problem is : Does not accept coins.");
              //set meter problem in object
              if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDReportMeterMenu.getMenuSelection: Setting problem in meter object as : Does not accept coins.");
              meter.setMeterProblemType(Meter.PROBLEM_TYPE_DOES_NOT_ACCEPT_COINS);
            }  
            else if(userSelection.equals(this.handleBrokenKey)){
              if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDReportMeterMenu.getMenuSelection: Meter problem is : Broken handle.");
              //set meter problem in object
              if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDReportMeterMenu.getMenuSelection: Setting problem in meter object as : Broken handle.");
              meter.setMeterProblemType(Meter.PROBLEM_TYPE_HANDLE_BROKEN);
            }  
          }
        }
        else if(!METER_CLASS_NAME.equals(classType)){
          if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "TDDReportMeterMenu.getMenuSelection: Meter key object in session is not an instance of com.viecore.pvs.business.Meter class.");
        }
      }
      else{
        if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "TDDReportMeterMenu.getMenuSelection: Meter key object in session is null.");
      }            
    }
    //return user selection
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDReportMeterMenu.getMenuSelection: Leaving method.");
    return userSelection;
    
  }//end getMenuSelection

 
    
}//end class TDDReportMeterMenu























