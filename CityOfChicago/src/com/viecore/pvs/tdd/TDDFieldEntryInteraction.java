package com.viecore.pvs.tdd;


import java.util.*;

import com.ibm.telephony.directtalk.*;
import com.ibm.telephony.beans.directtalk.*;
import com.ibm.telephony.beans.*;
import com.ibm.telephony.beans.media.*;
import com.ibm.telephony.beans.migrate.*;

import com.viecore.util.log.*;
import com.viecore.util.configuration.*;

import com.viecore.pvs.*;
import com.viecore.pvs.business.*;
import com.viecore.pvs.menu.*;


/**
 * This class contains functionality to speak segments to 
 * the caller and obtain data from the caller using TDD
 * 
 * @author Michael Ruggiero
 * @since JDK 1.3
 * @version 1.0 01/16/2003
 */
 
public abstract class TDDFieldEntryInteraction extends AbstractFieldEntryInteraction implements HungupListener, DoneListener, FailedListener{
  
  /**
    * Used to interact with TDD caller
    */ 
  protected TDDInteraction interaction = null;
  
    /**
    * Indicator of caller hang up 
    */ 
  protected boolean callerHungUp = false;
  
  /**
    * Indicator for caller entry timeout
    */ 
  protected boolean entryTimeout = false;
  
  /**
    * Indicator for unknown failure
    */
  private boolean unknownFailure = false;
  
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
  public static final String INSTRUCTION_GET_KEY = "GK";
  
  /**
    * State table bean to invoke DT state tables
    */
  private StateTable stateTable = new StateTable();
  
  /**
    * String array used to pass data to state table
    */
  private String [] stringArray = null;
  
  
  /**
    * Initializes this object with the configuration in the passed Properties object
    *
    * @param properties
    *     Properties object containing the main application properties for the application
    * @throws ConfigurationException if the configuration contained in the Properties object
    * is not valid
    */
  public void initConfiguration(Properties properties) throws ConfigurationException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDFieldEntryInteraction.initConfiguration: Entered method.");
    
    // check properties for null
    if(properties == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDFieldEntryInteraction.initConfiguration: Properties passed to this method null");
      throw new ConfigurationException("TDDFieldEntryInteraction.initConfiguration: Properties passed to this method null");
    }
    
    // call parent initConfiguration
    super.initConfiguration(properties);
    
    // create and configure TDD interaction
    interaction = new TDDInteraction();
    interaction.initLoggerIF(logger);
    interaction.initConfiguration(properties);
    
    // configure the state table bean
    //add listeners to state table bean
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDInteraction.initConfiguration: Adding listeners to state table bean.");
    stateTable.addDoneListener(this);
    stateTable.addFailedListener(this);
    stateTable.addHungupListener(this);
    String stateTableName = validateStringProperty(properties, CONFIG_TDD_STATE_TABLE_NAME);
    String stateTableEntryPoint = validateStringProperty(properties, CONFIG_TDD_STATE_TABLE_ENTRY_POINT);
    stateTable.setName(stateTableName);
    stateTable.setEntryPoint(stateTableEntryPoint);
    
    stringArray = new String [24];
            
    stringArray[0] = INSTRUCTION_GET_KEY;
    stringArray[1] = new Integer(timeout).toString();
    stringArray[2] = new Integer(maximumAttempts).toString();
    stringArray[3] = "5";
    stringArray[4] = transferKey + mainMenuKey + repeatKey + correctKey + incorrectKey;
    stringArray[5] = "";
    stringArray[6] = "";
    stringArray[7] = "";
    stringArray[8] = "";
    stringArray[9] = "";
    stringArray[10] = invalidMessage;
    stringArray[11] = systemErrorMessage;
    stringArray[12] = hostErrorMessage;
    stringArray[13] = correctMessage + pressOneMessage;
    stringArray[14] = incorrectMessage + pressTwoMessage;
    stringArray[15] = "";
    stringArray[16] = "";
    stringArray[17] = "";
    stringArray[18] = "";
    stringArray[19] = "";
    stringArray[20] = "";
    stringArray[21] = "";
    stringArray[22] = "";
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDFieldEntryInteraction.initConfiguration: Exiting method.");
    
    configured = true;
    
  }//end initConfiguration
  
  
  
  /**
    * This method creates an EntryField bean with the entryMessage and uses the TDDFieldEntryInteraction
    * to query the caller for data.  Each time the data is received, it will be checked against
    * the configured transferKey (throw TransferException).  After it retrieves the data it will
    * call validateData to ensure the data is correct.  This method will attempt to get correct
    * data the configured amount of times.   
    *
    * @param actionStatusEvent
    *     ActionStatusEvent containing the latest call information
    * @param locale
    *     Language for the application
    * @param maximumKeys 
    *     max amount of keys the caller can enter
    * @param minimumKeys 
    *     min amount of keys the caller can enter
    *
    * @return 
    * keyed data
    *
    * @throws HungupException if the caller has hungup.
	  * @throws TransferException if the caller requests a transfer.
	  * @throws MaxAttemptsExceededException if the caller cannot successfully
	  * enter data in the configured amount of attempts
	  * @throws FailureTransferException if there is an unknown failure
    */
  protected String getKeyData(ActionStatusEvent actionStatusEvent, Locale locale, int maximumKeys, int minimumKeys) throws HungupException, TransferException, MaxAttemptsExceededException, FailureTransferException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDFieldEntryInteraction.getKeyData: Entered method.");
    
    // check parameters for null
    if(actionStatusEvent == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDFieldEntryInteraction.getKeyData: ActionStatusEvent passed to this method is null");
      throw new NullPointerException("TDDFieldEntryInteraction.getKeyData: ActionStatusEvent passed to this method is null"); 
    }
    
    // current number of attempts
    int attempts = 0;
    // keyed data
    String keyedData = null;
    // boolean for invalid data
    boolean invalidData = true;
    // loop until we get valid data or we run out of attempts
    while(invalidData && (attempts < maximumAttempts)){
      keyedData = null;
      // boolean to keep track of timeout
      boolean timedOut = false;
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDFieldEntryInteraction.getKeyData: Attempt: " + (attempts + 1));
      // get the data
      try{
        keyedData = interaction.getInput(actionStatusEvent, entryMessage, maximumKeys, minimumKeys);
      }
      catch(EntryTimeoutException ete){
        if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDFieldEntryInteraction.getKeyData: Caller timed out");
      }
      
      // if keyed data is not equal to null, we got data from the user
      if(keyedData != null){
        // check for transfer request
        if(keyedData.equals(transferKey)){
          if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDFieldEntryInteraction.getKeyData: Caller pressed " + transferKey + ", throwing TransferException");
          throw new TransferException("TDDFieldEntryInteraction.getKeyData: Caller requested transfer");
        }
        
        // check if data is valid
        boolean passedValidation = validateData(actionStatusEvent, locale, keyedData);       
        if(passedValidation == true){
          invalidData = false;
          if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDFieldEntryInteraction.getKeyData: Data is valid " + keyedData);
        }
        else{
          // log bad data
          if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDFieldEntryInteraction.getKeyData: Data is invalid " + keyedData);
        }
      }
      // increment the attempts
      attempts++;
    }
    
    // if data is invalid now, throw max attempts exception
    if(invalidData){
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDFieldEntryInteraction.getKeyData: Max attempts have been exceeded, throwing MaxAttemptsExceededException");
      throw new MaxAttemptsExceededException("TDDFieldEntryInteraction.getKeyData: Max attempts have been exceeded, throwing MaxAttemptsExceededException");
    }
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDFieldEntryInteraction.getKeyData: Leaving method.");    
    return keyedData;
  }//end getKeyData
  
  
  
   /**
    * This method creates a Menu bean with the configured confirmationSegment in
    * the parent class and passed data.  This bean is configured with all the
    * values in the parent class and is used to retrieve the selection from the user.  
    *
    * @param actionStatusEvent 
    *    ActionStatusEvent containing latest call info
    * @param locale 
    *    language used to retrieve the data
    * @param data 
    *     String data to be confirmed
    *
    * @return 
    * indicates if the caller confirmed
    *
    * @throws HungupException if the caller hangs up
    * @throws TransferException if the caller has requested a transfer
    * @throws MainMenuException if the caller has requested the main menu
    * @throws MaxAttemptsExceededException if the caller has exceeded the maximum
    * amount of attempts allowed
    * @throws FailureTransferException if there is an unknown failure.
    */
  protected boolean confirmKeyData(ActionStatusEvent actionStatusEvent, Locale locale, String data) throws HungupException, TransferException, MainMenuException, MaxAttemptsExceededException, FailureTransferException{
                                                                  
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDFieldEntryInteraction.confirmKeyData: Entered method.");
    
    // check parameters for null
    if(actionStatusEvent == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDFieldEntryInteraction.confirmKeyData: ActionStatusEvent passed to this method is null");
      throw new NullPointerException("TDDFieldEntryInteraction.confirmKeyData: ActionStatusEvent passed to this method is null"); 
    }
    
    if(data == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDFieldEntryInteraction.confirmKeyData: data passed to this method is null");
      throw new NullPointerException("TDDFieldEntryInteraction.confirmKeyData: data passed to this method is null");
    }
    
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDFieldEntryInteraction.confirmKeyData: Creating bean");
    
    // place to store callers selection
    String menuSelection = repeatKey;
    
    // form the confirmation message

    stringArray[8] = confirmationMessage + data;
    
    // loop for repeats
    while(menuSelection.equals(repeatKey) == true){
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDFieldEntryInteraction.confirmKeyData: Presenting menu");
      stateTable.action(actionStatusEvent);
      
      // get the return status
      String returnCode = stringArray[23];
      
      callerHungUp = false;
      entryTimeout = false;
      unknownFailure = false;
      
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDFieldEntryInteraction.confirmKeyData: Return code :" + returnCode);
      
      // check return code for null
      if(returnCode == null){
        if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDFieldEntryInteraction.confirmKeyData: Return code is null.");
      }
      else{
        // caller hungup?
        if(returnCode.equals("U")){
          if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDFieldEntryInteraction.confirmKeyData: Caller has hung up. Throwing HungupException.");
          callerHungUp = true;
        }
        // error in state table/custom server?
        else if(returnCode.equals("H") || returnCode.equals("E")){
          if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDFieldEntryInteraction.confirmKeyData: System or host error on state table.");
        }
        // max errors/max timeouts?
        else if(returnCode.equals("P") || returnCode.equals("M")){
          if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.ERR,"TDDFieldEntryInteraction.confirmKeyData: Timeout or invalid entry");
          entryTimeout = true;
        }
        // get data is successful?
        else if(returnCode.equals("OK")){
          if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDFieldEntryInteraction.confirmKeyData: GD (get data) successful");
        }
        else{
          if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDFieldEntryInteraction.confirmKeyData: Unknown return code");
        }
      }
   
      menuSelection = stringArray[24];
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDFieldEntryInteraction.confirmKeyData: Got menu selection: " + menuSelection);
    }
    
    // check selection for 
    if(entryTimeout == true){
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDFieldEntryInteraction.confirmKeyData: Max attempts have been exceeded, throwing MaxAttemptsExceededException");
      throw new MaxAttemptsExceededException("TDDFieldEntryInteraction.confirmKeyData: Max attempts have been exceeded, throwing MaxAttemptsExceededException");
    }
    
    // check for hangup
    if(callerHungUp == true){
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDFieldEntryInteraction.confirmKeyData: Caller has hung up. Throwing HungupException.");
      throw new HungupException("TDDInteraction.confirmKeyData: Caller has hung up.");
    }
    
    //check the state of unknownFailure variable
    if(unknownFailure){
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDFieldEntryInteraction.playMedia: Unknown failure in action.");
      throw new FailureTransferException("TDDInteraction.playMedia: Unknown failure in action");
    }
    
    // check for main menu
    if(menuSelection.equals(mainMenuKey)){
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDFieldEntryInteraction.confirmKeyData: Caller has requested main menu. Throwing MainMenuException.");
      throw new MainMenuException("TDDInteraction.confirmKeyData: Caller has requested main menu.");
    }
    
    // check for transfer
    if(menuSelection.equals(transferKey)){
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDFieldEntryInteraction.confirmKeyData: Caller has requested transfer. Throwing TransferException.");
      throw new TransferException("TDDInteraction.confirmKeyData: Caller has requested transfer.");
    }
    
    // choose return
    boolean confirmed = false;
    if(menuSelection.equals(correctKey)){
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDFieldEntryInteraction.confirmKeyData: Caller has confirmed.");
      confirmed = true;
    }
    else if(menuSelection.equals(incorrectKey)){
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDFieldEntryInteraction.confirmKeyData: Caller did not confirm.");
      confirmed = false;
    }
    else{
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDFieldEntryInteraction.confirmKeyData: Invalid menu selection: " + menuSelection);
    }
    
    //return input
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDFieldEntryInteraction.getInput: Exiting method");
    return confirmed;
  }// end getInput
  
  
  
  /**
    * This abstract method attempts to validate the data to ensure it is the correct format.
    * This method will also play an error message specific to the implementation.
    *
    * @param actionStatusEvent 
    *     ActionStatusEvent containing latest call info
    * @param locale 
    *     language used to retrieve the data
    * @param data 
    *     String data to be confirmed
    *
    * @return 
    * indicates if the data was valid
    *
    * @throws HungupException if the caller hangs up
    * @throws FailureTransferException if there is an unknown failure
    */
  protected abstract boolean validateData(ActionStatusEvent actionStatusEvent, Locale locale, String data) throws HungupException, FailureTransferException; 
  
  /**
    * DoneListener's method, listens for done events raised by action beans.
    *
    * @param event 
    *     ActionStatusEvent passed from action methods
    */
  public void done(ActionStatusEvent event) {
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDFieldEntryInteraction:done: Source for done is:"+event.getSource().toString());
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDFieldEntryInteraction:done: Completion code for done is:"+event.getCompletionCode());
  }//end done 
  
  
  /**
    * FailedListener's method, listens for failed events.
    *
    * @param event 
    *     ActionStatusEvent passed from action methods
    */    
  public void failed(ActionStatusEvent event) {
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDFieldEntryInteraction:failed: Source for failed is:"+event.getSource().toString());
    unknownFailure = true;
    if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDFieldEntryInteraction:failed: Unknown failure: check completion code");
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDFieldEntryInteraction:failed: Completion code for failed is:"+event.getCompletionCode());
  }//end failed
  
  /**
    * HungupListener's method, listens for hungup events.
    *
    * @param event 
    *     HungupEvent passed from action methods
    */
  public void hungup(HungupEvent event) {
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDFieldEntryInteraction:hungup: Source for hungup is:"+event.getSource().toString());
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDFieldEntryInteraction:hungup: Completion code for hungup is:"+event.getCompletionCode());
    //set class variable callerHungUp
    callerHungUp = true;
  }//end hungup
  

}//end TDDFieldEntryInteraction class