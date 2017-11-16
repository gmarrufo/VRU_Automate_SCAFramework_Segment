package com.viecore.pvs;

/*
 * Licensed Materials - Property of Viecore Inc.
 *
 * (C) Copyright Viecore Inc. 2002, 2003 All Rights Reserved
 */


import java.util.*;

import com.ibm.telephony.directtalk.*;
import com.ibm.telephony.beans.directtalk.*;
import com.ibm.telephony.beans.*;
import com.ibm.telephony.beans.media.*;


import com.viecore.util.log.*;
import com.viecore.util.configuration.*;

import com.viecore.pvs.business.*;
import com.viecore.pvs.*;
import com.viecore.pvs.tdd.TDDInteraction;
import com.viecore.pvs.transaction.*;


/**
 * This class contains functionality to speak
 * the welcome segment to the caller and obtain the language for the caller using TDD.  
 * 
 * @author Michael Ruggiero
 * @since JDK 1.3
 * @version 1.0  1/3/2003
 */ 
public class TDDWelcome implements WelcomeIF, ConfigurableIF, LoggingIF{                                         
  
  /**
    * Indicates if this object has been configured
    */
  protected boolean configured = false;      
                                         
  /**
    * Reference to the logger for the application
    */
  private LoggerIF logger = null;                                         
                                          
  /**
    * Level at which this object should log
    */
  private int logLevel = LoggerIF.OFF;
  
  /**
    * TDDInteraction object to speak welcome message
    */ 
  private TDDInteraction interaction = null;
  
  /**
    * Constant used to retrieve welcome message segment from Properties object
    */
  public static final String CONFIG_WELCOME_MESSAGE   = "WelcomeMessage";
  
  /**
    * Welcome message to be displayed to caller
    */
  private String welcomeMessage = null;
  
  /**
   *  Object used for Reporting the total number of TDD calls
   */
   
   private LogEventTransaction logEventTransaction = null;
      
  /**
    * This method initializes this object with a LoggerIF.
    *
    * @param loggerObject
    * LoggerIF object for logging information
    * 
    */
  public void initLoggerIF(LoggerIF loggerObject) {
    logger = loggerObject;
    if (logger != null) {
      logLevel = logger.getLogLevel();
     } 
    else {
      throw new NullPointerException("TDDWelcome.initLoggerIF: Unable to log!!!");
    }
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDWelcome.initLoggerIF: Exiting with logLevel = ["+logLevel+"].");  
  } //end of initLoggerIF
 
 
     
  /**
    * This method will log a message to the logger if one is referenced
    *
    * @param level  
    * the int level of the message
    *
    * @param msg     
    * the String message to log
    */
  protected void log(int level, String msg){
    if (logger != null){
      logger.log(level, msg);
    }
  } // end log       
  
  /**
    * This method extracts the configuration from the Properties object
    * and loads the data into class level objects, variables
    *
    * @param applicationPropertiesIn 
    * Properties object containing the configuration for the object
    * 
    * @throws ConfigurationException - 
    * Thrown if the Properties object is missing values or contains invalid entries.
    */
  public void initConfiguration(Properties applicationPropertiesIn) throws ConfigurationException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDWelcome.initConfiguration: Entering method"); 
    
    if (applicationPropertiesIn == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "TDDWelcome.initConfiguration: applicationsPropertiesIn is null");        
      throw new ConfigurationException("TDDWelcome.initConfiguration: applicationsPropertiesIn is null"); 
    } 
     
    welcomeMessage = validateStringProperty( applicationPropertiesIn,CONFIG_WELCOME_MESSAGE);
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDWelcome.initConfiguration: Welcome message is is ["+welcomeMessage+"]."); 
    
    // create and configure TDD interaction
    interaction = new TDDInteraction();
    interaction.initLoggerIF(logger);
    interaction.initConfiguration(applicationPropertiesIn);
    
     //instatiate a LogEventTransaction object, just log any type of error    
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDWelcome.getConfiguration: Instantiating a LogEventTransaction object."); 
    logEventTransaction = new LogEventTransaction();
    logEventTransaction.initLoggerIF(logger);
    logEventTransaction.initConfiguration(applicationPropertiesIn);
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDWelcome.initConfiguration: Exiting method");
  }// end method
     
  
  /**
    *
    * This method uses the TDDInteraction to play the welcome message.  Since
    * multilingual capabilities are not supported for TDD, returns null
    * is returned
    *
    * @param actionIn
    * event containing the latest call information
    *
    * @return 
    *   null, not used by TDD
    *
    * @throws HungupException
    * if the caller has hungup.
    *
    * @throws MaxAttemptsExceededException
	  * if the user failed to enter a valid selection
	  *
	  * @throws TransferException
	  * if the object retrieved from the hastable of locales is not a Locale
	  * @throws FailureTransferException if there is an unknown failure
	  *
    */  
  public Locale welcome(ActionStatusEvent actionIn) throws HungupException,MaxAttemptsExceededException, TransferException, FailureTransferException {    
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDWelcome.welcome: Entered method");
    
    // check parameters for null
    if(actionIn == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDWelcome.welcome: ActionStatusEvent passed to this method is null");
      throw new FailureTransferException("TDDWelcome.welcome: ActionStatusEvent passed to this method is null"); 
    }
    
    interaction.playMedia(actionIn, welcomeMessage);
    
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDWelcome.welcome: About to call logEventTransaction.logEvent for TOTAL_TDD_CALLS");  
    this.logEventTransaction.logEvent(actionIn,LogEventTransaction.TOTAL_TDD_CALLS);
   
    
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDWelcome.welcome: Exiting Method");
    // tdd does not support locales - returning an empty locale
    return null;
  }  

  /**
    * Returns the value of configured 
    *
    * @return 
    * true if the object has been configured, otherwise false
    *     
    */
  public boolean isConfigured(){
    return configured;
  }  
  
  /**
    * This is a generic method used to get and validate a string property. 
    *
    * @param propertyName
    * A String that contains the name of the property to retrieve and validate.
    *
    * @return
    *   property retreived from the Properties object
    *
    * @throws MissingPropertyConfigurationException
    * thrown if the retreived property is null
    */   
  private String validateStringProperty( Properties inputProperties,String propertyName)throws MissingPropertyConfigurationException{       
    String propertyValue = inputProperties.getProperty( propertyName );
    log( LoggerIF.DEBUG, "TDDWelcome.validateStringProperty: [" + propertyName + "]=[" + propertyValue + "]" ); 
        
    if ( propertyValue == null ){
      throw new MissingPropertyConfigurationException("TDDWelcome.validateStringProperty: Property [" + propertyName + "] is null"); 
    }                 
    return propertyValue;
        
  }// end validateStringProperty
  
  
}//endclass