package com.viecore.pvs.tdd;

import java.util.*;

import com.ibm.telephony.directtalk.*;
import com.ibm.telephony.beans.directtalk.*;
import com.ibm.telephony.beans.*;
import com.ibm.telephony.beans.migrate.*;
import com.ibm.telephony.beans.media.*;

import com.viecore.util.log.*;
import com.viecore.util.configuration.*;

import com.viecore.pvs.*;
import com.viecore.pvs.menu.*;
import com.viecore.pvs.business.*;


/**
 * This class contains functionality to speak segments to 
 * the caller and obtain data from the caller using TDD
 * 
 * @author Michael Ruggiero
 * @since JDK 1.3
 * @version 1.0 01/25/2003
 */
 
public class TDDInteraction implements DoneListener, FailedListener, HungupListener, ConfigurableIF, LoggingIF {

  
  /**
    * Reference to the logger for the application
    */
  private LoggerIF logger = null;
  
  /**
    * Level at which this object will log
    */
  private int logLevel = LoggerIF.OFF;
  
  /**
    * Indicates if this object has been configured
    */
  private boolean configured = false;
  
  /**
    * Amount of time for caller to key input
    */ 
  private int timeout;
  
  /**
    * Indicator of caller hang up 
    */ 
  private boolean callerHungUp = false;
  
  /**
    * Indicator for caller entry timeout
    */ 
  private boolean entryTimeout = false;
  
  /**
    * Indicator for unknown failure
    */
  private boolean unknownFailure = false;
  
  /**
    * Segment to be played if input keyed by caller is invalid
    */ 
  private String invalidMessage = null;
  
  /**
    * Error message to display if there is system problem
    */ 
  private String systemErrorMessage = null;
  
  /**
    * Error message to display if there is host problem
    */ 
  private String hostErrorMessage = null;
  
  /**
    * Constant used to retrieve the timeout from the Properties
    */ 
  public static final String CONFIG_TIMEOUT = "Timeout";
  
  /**
    * Constant used to retrieve the invalid entry message from the Properties
    */ 
  public static final String CONFIG_INVALID_MESSAGE = "InvalidMessage";
  
  /**
    * Constant used to retrieve the system error message from the Properties
    */ 
  public static final String CONFIG_SYSTEM_ERROR_MESSAGE = "SystemErrorMessage";
  
  /**
    * Constant used to retrieve the system error message from the Properties
    */ 
  public static final String CONFIG_HOST_ERROR_MESSAGE = "HostErrorMessage";
  
  /**
    * Constant used to retrieve the tdd state table name from the Properties
    */ 
  public static final String CONFIG_TDD_STATE_TABLE_NAME = "TDDStateTableName";
  
  /**
    * Constant used to retrieve the tdd state table entry point from the Properties
    */ 
  public static final String CONFIG_TDD_STATE_TABLE_ENTRY_POINT = "TDDStateTableEntryPoint";
  
  /**
    * Constant used to instruct state table to get data
    */ 
  public static final String INSTRUCTION_GET_DATA = "GD";
  
  /**
    * Constant used to instruct state table to send data
    */ 
  public static final String INSTRUCTION_SEND = "SEND";
  
  /**
    * State table bean to invoke DT state tables
    */
  private StateTable stateTable = new StateTable();
  
  /**
    * Initializes this object with the configuration in the passed Properties object
    *
    * @param properties
    *     Properties object containing the main application properties for the application
    * @throws ConfigurationException if the configuration contained in the Properties object
    * is not valid
    */
  public void initConfiguration(Properties properties) throws ConfigurationException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDInteraction.initConfiguration: Entered method.");
    
    // check properties for null
    if(properties == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDInteraction.initConfiguration: Properties passed to this method null");
      throw new ConfigurationException("TDDInteraction.initConfiguration: Properties passed to this method null");
    }
    
    //read Time allotted for caller entry from properties
    String timeoutString = validateStringProperty(properties, CONFIG_TIMEOUT);
    // parse the integer
    try{
      this.timeout = Integer.parseInt(timeoutString);
    }
    catch(NumberFormatException nfe){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDInteraction.initConfiguration: Timeout value [" + timeoutString + "] in properties is not a parsable integer ["+nfe.toString()+"]");
      throw new ConfigurationException("TDDInteraction.initConfiguration: Timeout value [" + timeoutString + "] in properties is not a parsable integer. ["+nfe.toString()+"]");
    }
       
    invalidMessage = validateStringProperty(properties, CONFIG_INVALID_MESSAGE);
    systemErrorMessage = validateStringProperty(properties, CONFIG_SYSTEM_ERROR_MESSAGE);
    hostErrorMessage = validateStringProperty(properties, CONFIG_HOST_ERROR_MESSAGE);
    //add listeners to state table bean
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDInteraction.initConfiguration: Adding listeners to state table bean.");
    stateTable.addDoneListener(this);
    stateTable.addFailedListener(this);
    stateTable.addHungupListener(this);
    String stateTableName = validateStringProperty(properties, CONFIG_TDD_STATE_TABLE_NAME);
    String stateTableEntryPoint = validateStringProperty(properties, CONFIG_TDD_STATE_TABLE_ENTRY_POINT);
    stateTable.setName(stateTableName);
    stateTable.setEntryPoint(stateTableEntryPoint);
    
    //set configured flag to true
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDInteraction.initConfiguration: Configuration complete, setting configured flag to true, leaving method.");
    configured = true;
    
  }//end initConfiguration
  
  
  
  /**
    * This method creates an Announcement bean then initializes the bean with the mediaTypes 
    * and plays the segments to the caller. 
    *
    * @param aStatusEvent
    *     ActionStatusEvent event containing the latest call information
    * @param message
    *     String to present to caller
    *
    * @exception HungupException if the user caller hangs up
    * @exception FailureTransferException if there is an unexpected error
    */
  public void playMedia(ActionStatusEvent aStatusEvent, String message) throws  HungupException, FailureTransferException {
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDInteraction.playMedia: Entered method.");
    
    // check parameters for null
    if(aStatusEvent == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDInteraction.playMedia: ActionStatusEvent passed to this method is null");
      throw new NullPointerException("TDDInteraction.playMedia: ActionStatusEvent passed to this method is null"); 
    }
    
    if(message == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDInteraction.playMedia: String passed to this method is null");
      throw new NullPointerException("TDDInteraction.playMedia: String passed to this method is null");
    }
      
    // Create the array of strings to pass to the state table
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDInteraction.playMedia: Creating an array to talk to state table.");
    String [] stringArray = new String [25];
          
    stringArray[0] = INSTRUCTION_SEND;
    stringArray[1] = new Integer(timeout).toString();
    stringArray[2] = "";
    stringArray[3] = "";
    stringArray[4] = "";
    stringArray[5] = "";
    stringArray[6] = "";
    stringArray[7] = "";
    stringArray[8] = message;
    stringArray[9] = "";
    stringArray[10] = "";
    stringArray[11] = "";
    stringArray[12] = systemErrorMessage;
    stringArray[13] = hostErrorMessage;
    stringArray[14] = "";
    stringArray[15] = "";
    stringArray[16] = "";
    stringArray[17] = "";
    stringArray[18] = "";
    stringArray[19] = "";
    stringArray[20] = "";
    stringArray[21] = "";
    stringArray[22] = "";
    stringArray[23] = "";
    stringArray[24] = "";
    
    stateTable.setParameters(stringArray);
    
    callerHungUp = false;
    unknownFailure = false;
    
    //call action to play the segments to the caller
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDInteraction.playMedia: Calling action on state table bean.");
    stateTable.action(aStatusEvent);
    
    //check the state of entryTimeout variable
    if(unknownFailure){
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDInteraction.playMedia: Unknown failure in action.");
      throw new FailureTransferException("TDDInteraction.playMedia: Unknown failure in action");
    }
    //check the state of callerHungUp variable
    if(callerHungUp){
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDInteraction.playMedia: Caller has hung up. Throwing HungupException.");
      throw new HungupException("TDDInteraction.playMedia: Caller has hung up.");
    }
    
    // get the return status code
    String returnCode = stringArray[23];
    
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDInteraction.playMedia: Return code is : ["+returnCode+"]");
    
    // check return code for null
    if(returnCode == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDInteraction.playMedia: Return code is null.");
    }
    else{
      if(returnCode.equals("U")){
        if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDInteraction.playMedia: Caller has hung up. Throwing HungupException.");
        throw new HungupException("TDDInteraction.playMedia: Caller has hung up.");
      }
      else if(returnCode.equals("H") || returnCode.equals("E")){
        if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDInteraction.playMedia: System or host error on state table.");
      }
      else if(returnCode.equals("OK")){
        if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDInteraction.playMedia: SEND successful");
      }
      else{
        if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDInteraction.playMedia: Unknown return code");
      }
    }
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDInteraction.playMedia: Leaving method.");    
  }//end playMedia
  
  
  
   /**
    * This method creates an EntryField bean then calls action 
    * to play the segments and retrieve the input.
    *
    * @param aStatusEvent
    *     ActionStatusEvent, event containing the latest call information
    * @param message
    *     Message to send to the TDD caller
    * @param maximumKeys
    *     int, maximum amount of keys to be entered by the caller
    * @param minimumKeys
    *     int, minumum amount of keys to be entered by the caller
    *
    * @return Data entered by the caller.
    *
    * @exception HungupException if the caller hangs up
    * @exception EntryTimeoutException if the caller times out
    * @exception FailureTransferException if there is an unexpected error during action
    */
  public String getInput(ActionStatusEvent aStatusEvent, String message, 
                          int maximumKeys, int minimumKeys) throws  HungupException, EntryTimeoutException, FailureTransferException{
                                                                  
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDInteraction.getInput: Entered method.");
    
    // check parameters for null
    if(aStatusEvent == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDInteraction.getInput: ActionStatusEvent passed to this method is null");
      throw new NullPointerException("TDDInteraction.getInput: ActionStatusEvent passed to this method is null"); 
    }
    
    if(message == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDInteraction.getInput: Message passed to this method is null");
      throw new NullPointerException("TDDInteraction.getInput: Message passed to this method is null");
    }
    
   // Create the array of strings to pass to the state table
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDInteraction.getInput: Creating an array to talk to state table.");
    String [] stringArray = new String [25];
          
    stringArray[0] = INSTRUCTION_GET_DATA;
    stringArray[1] = new Integer(timeout).toString();
    stringArray[2] = "1";
    stringArray[3] = "";
    stringArray[4] = "";
    stringArray[5] = new Integer(minimumKeys).toString();
    stringArray[6] = new Integer(maximumKeys).toString();
    stringArray[7] = "S";
    stringArray[8] = message;
    stringArray[9] = "";
    stringArray[10] = invalidMessage;
    stringArray[11] = systemErrorMessage;
    stringArray[12] = hostErrorMessage;
    stringArray[13] = "";
    stringArray[14] = "";
    stringArray[15] = "";
    stringArray[16] = "";
    stringArray[17] = "";
    stringArray[18] = "";
    stringArray[19] = "";
    stringArray[20] = "";
    stringArray[21] = "";
    stringArray[22] = "";
    stringArray[23] = "";
    stringArray[24] = "";
    
    stateTable.setParameters(stringArray);
    
    callerHungUp = false;
    entryTimeout = false;
    unknownFailure = false;
    
    //call action on state table to play segments and retrieve input
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDInteraction.getInput: Calling action on StateTable bean.");
    stateTable.action(aStatusEvent);
    
    //check the state of entryTimeout variable
    if(unknownFailure){
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDInteraction.getInput: Unknown failure in action.");
      throw new FailureTransferException("TDDInteraction.getInput: Unknown failure in action");
    }
    //check the state of callerHungUp variable
    if(callerHungUp){
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDInteraction.getInput: Caller has hung up. Throwing HungupException.");
      throw new HungupException("TDDInteraction.getInput: Caller has hung up.");
    }
    //check the state of entryTimeout variable
    if(entryTimeout){
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDInteraction.getInput: Caller has exceeded timeout period.");
      throw new EntryTimeoutException ("TDDInteraction.getInput: Caller has exceeded timeout period.");
    }
    
    // get the return status
    String returnCode = stringArray[23];
    String returnData = stringArray[24];

    
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDInteraction.getInput: Return code :" + returnCode + " Return data: " + returnData);
    
    // check return code for null
    if(returnCode == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDInteraction.getInput: Return code is null.");
    }
    else{
      if(returnCode.equals("U")){
        if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDInteraction.getInput: Caller has hung up. Throwing HungupException.");
        callerHungUp = true;
      }
      else if(returnCode.equals("H") || returnCode.equals("E")){
        if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDInteraction.getInput: System or host error on state table.");
      }
      else if(returnCode.equals("P") || returnCode.equals("M")){
        if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDInteraction.getInput: Timeout or invalid entry");
        entryTimeout = true;
      }
      else if(returnCode.equals("OK")){
        if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDInteraction.getInput: GD successful");
      }
      else{
        if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDInteraction.getInput: Unknown return code");
      }
    }
    
    //return input
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDInteraction.getInput: Caller input is ["+returnData+"]. Returning input value.");
    return returnData;
  }// end getInput
  
  
  
  /**
    * This method creates an voice segment bean with the passed Locale and the 
    * configured invalid input message and calls playMedia to play the configured invalid message.
    *
    * @param aStatusEvent
    *     ActionStatusEvent event containing the latest call information
    * @param locale
    *     Locale, locale for the caller
    * @exception HungupException if the caller hangs up
    * @throws FailureTransferException if there is an unexpected error
    */
  public void speakInvalidInputMessage(ActionStatusEvent aStatusEvent, Locale locale) throws HungupException, FailureTransferException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDInteraction.speakInvalidInputMessage: Entered method.");
    
    // check parameters for null
    if(aStatusEvent == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDInteraction.speakInvalidInputMessage: ActionStatusEvent passed to this method is null");
      throw new NullPointerException("TDDInteraction.speakInvalidInputMessage: ActionStatusEvent passed to this method is null"); 
    }
    
    //call playMedia to play invalid input message
    playMedia(aStatusEvent, invalidMessage);
      
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDInteraction.speakInvalidInputMessage: Leaving method.");
  }//end speakInvalidInputMessage
  
  /**
    * Returns the configured boolean for this object stating if this object has been configured or not
    *
    * @return boolean indicating if the object has been configured
    */
  public boolean isConfigured() {
    return configured;
  }//end isConfigured  
  

  /**
    * DoneListener's method, listens for done events raised by action beans.
    *
    * @param event ActionStatusEvent passed from action methods
    */
  public void done(ActionStatusEvent event) {
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDInteraction:done: Source for done is:"+event.getSource().toString());
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDInteraction:done: Completion code for done is:"+event.getCompletionCode());
  }//end done 
  
  
  /**
    * FailedListener's method, listens for failed events.
    *
    * @param event ActionStatusEvent passed from action methods
    */    
  public void failed(ActionStatusEvent event) {
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDInteraction:failed: Source for failed is:"+event.getSource().toString());
    
    unknownFailure = true;
    
    if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDInteraction:failed: Unknown failure: Check completion code");
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDInteraction:failed: Completion code for failed is:"+event.getCompletionCode());
    
  }//end failed
  
  
  /**
    * HungupListener's method, listens for hungup events.
    *
    * @param event HungupEvent passed from action methods
    */
  public void hungup(HungupEvent event) {
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDInteraction:hungup: Source for hungup is:"+event.getSource().toString());
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDInteraction:hungup: Completion code for hungup is:"+event.getCompletionCode());
    //set class variable callerHungUp
    callerHungUp = true;
  }//end hungup      
  
  /**
    * This method initializes this object with a logger object - LoggerIF
    *
    * @param logger
    *  A reference to a logger object.
    */
  public void initLoggerIF( LoggerIF logger ){
    if(logger != null){
      this.logger = logger;
      logLevel = logger.getLogLevel();
      if (logLevel >= LoggerIF.DEBUG) log( LoggerIF.DEBUG, "TDDInteraction.initLoggerIF: Logger initialized." );
    }
  }//end initLoggerIF
  
  /**
    * This method logs a message to the logger in this object
    *
    * @param logLevel int representing level to log at
    * @param logMessage
    *  A String that contains the error information.
    */
  private void log( int logLevel, String logMessage ){
    if ( logger != null ){            
      logger.log(logLevel, logMessage);
    }
  }//end log
  
  /**
    * This is a generic method used to get and validate a string property. 
    *
    * @param inputProperties Properties object containing the configuration
    * @param propertyName
    *  A String that contains the name of the property to retrieve and validate.
    *
    * @return
    *  A string containing the property value retrieved from the property object.
    *
    * @throws MissingPropertyConfigurationException
    * thrown if the retreived property is null
    */   
  protected String validateStringProperty( Properties inputProperties,String propertyName)throws MissingPropertyConfigurationException{       
    String propertyValue = inputProperties.getProperty( propertyName );
    if (logLevel >= LoggerIF.DEBUG) log( LoggerIF.DEBUG, "TDDInteraction.validateStringProperty: [" + propertyName + "]=[" + propertyValue + "]" ); 
        
    if ( propertyValue == null ){
      if (logLevel >= LoggerIF.ERR) log( LoggerIF.ERR, "TDDInteraction.initConfiguration: property [" + propertyName + "] is null"); 
      throw new MissingPropertyConfigurationException("TDDInteraction.initConfiguration: property [" + propertyName + "] is null"); 
    }                 
    return propertyValue;
        
  }// end validateStringProperty

}//end TDDInteraction class