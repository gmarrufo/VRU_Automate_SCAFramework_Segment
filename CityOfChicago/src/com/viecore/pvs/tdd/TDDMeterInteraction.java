package com.viecore.pvs.tdd;


import java.util.*;

import com.ibm.telephony.directtalk.*;
import com.ibm.telephony.beans.directtalk.*;
import com.ibm.telephony.beans.*;
import com.ibm.telephony.beans.media.*;

import com.viecore.util.log.*;
import com.viecore.util.configuration.*;

import com.viecore.pvs.*;
import com.viecore.pvs.business.*;
import com.viecore.pvs.menu.*;
import com.viecore.pvs.util.*;


/**
 * This class contains functionality to obtain and confirm meter number from
 * the caller using TDD.
 * 
 * @author Michael Ruggiero
 * @since JDK 1.3
 * @version 1.0 01/28/2003
 */
 
public class TDDMeterInteraction extends TDDFieldEntryInteraction implements MeterInteractionIF {

  /**
    * Constant used to retrieve maximum digits for meter number from Properties
    */
  public static final String CONFIG_METER_MAX_DIGITS	= "MeterNumberMaximumDigits";
  
  /**
    * Constant used to retrieve minimum digits for meter number from Properties
    */
  public static final String CONFIG_METER_MIN_DIGITS	= "MeterNumberMinimumDigits";
  
  /**
    * Constant used to retrieve meter entry segment from Properties
    */
  public static final String CONFIG_METER_ENTRY_MESSAGE	= "MeterEntryMessage";
  
  /**
    * Constant used to retrieve confirmation segment from Properties	
    */
  public static final String CONFIG_METER_CONFIRMATION_MESSAGE = "MeterConfirmationMessage";
  
  /**
    * Constant used to retrieve scheduling warning segment from Properties
    */
  public static final String CONFIG_METER_PROBLEM_THANK_YOU_MESSAGE = "MeterProblemThankYouMessage";
  
  /**
    * Constant used to retrieve meter numbe rappears segment from Properties
    */
  public static final String CONFIG_METER_NUMBER_APPEARS_MESSAGE = "MeterNumberAppearsMessage";
  
  /**
    * Maximum amount of digits for meter
    */
  private int meterNumberMaxDigits;
  
  /**
    * Minimum amount of digits for meter
    */
  private int meterNumberMinDigits;
  
  /**
    * Segment to play for meter number appears
    */
  private String meterNumberAppearsMessage	= null;
  
  /**
    * Segment to play for duplicate hearings
    */
  private String meterProblemThankYouMessage = null;
    
  /**
    * Initializes this object with the configuration in the passed Properties object
    *
    * @param properties
    * Properties object containing the main application properties for the application
    *
    * @throws ConfigurationException if the configuration contained in the Properties object
    * is not valid
    */
  public void initConfiguration(Properties properties) throws ConfigurationException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDMeterInteraction.initConfiguration: Entered method.");
    
    // check properties for null
    if(properties == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDMeterInteraction.initConfiguration: Properties passed to this method null");
      throw new ConfigurationException("TDDMeterInteraction.initConfiguration: Properties passed to this method null");
    }
    
    // call the parent
    super.initConfiguration(properties);
    
    // segment asking for meter number max digits
    String stringMax = validateStringProperty(properties, CONFIG_METER_MAX_DIGITS);
    // parse the integer
    try{
      meterNumberMaxDigits = Integer.parseInt(stringMax);
    }
    catch(NumberFormatException nfe){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDMeterInteraction.initConfiguration: Meter max digits value [" + stringMax + "] in properties is not a parsable integer. ["+nfe.toString()+"]");
      throw new ConfigurationException("TDDMeterInteraction.initConfiguration: Meter max digits value in properties is not a parsable integer. ["+nfe.toString()+"]");
    }
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMeterInteraction.initConfiguration: Meter max digits value is ["+meterNumberMaxDigits+"].");
    
    // segment asking for meter number min digits
    String stringMin = validateStringProperty(properties, CONFIG_METER_MIN_DIGITS);
    // parse the integer
    try{
      meterNumberMinDigits = Integer.parseInt(stringMin);
    }
    catch(NumberFormatException nfe){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDMeterInteraction.initConfiguration: Meter min digits value [" + stringMin + "] in properties is not a parsable integer. ["+nfe.toString()+"]");
      throw new ConfigurationException("TDDMeterInteraction.initConfiguration: Meter min digits value in properties is not a parsable integer. ["+nfe.toString()+"]");
    }
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMeterInteraction.initConfiguration: Meter min digits value is ["+meterNumberMinDigits+"].");
    
    // segment asking for meter number
    entryMessage = validateStringProperty(properties, CONFIG_METER_ENTRY_MESSAGE);
    // segment confirmation
    confirmationMessage = validateStringProperty(properties, CONFIG_METER_CONFIRMATION_MESSAGE);
    // segment meter problem thank you
    meterProblemThankYouMessage = validateStringProperty(properties, CONFIG_METER_PROBLEM_THANK_YOU_MESSAGE);
    // segment meter number appears
    meterNumberAppearsMessage = validateStringProperty(properties, CONFIG_METER_NUMBER_APPEARS_MESSAGE);
    
    //set configured flag to true
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDMeterInteraction.initConfiguration: Configuration complete, setting configured flag to true, leaving method.");
    configured = true;
    
  }//end initConfiguration
  
  /**
    * This method speaks a prompt for the meter number to the caller
    * and returns the callers input
    *
    * @param actionIn 
    * ActionStatusEvent containing the latest call information
    *
    * @param locale 
    * language to talk to the caller in
    *
    * @return
    * meter number
    *
    * @throws HungupException if the caller has hungup
    * @throws TransferException if the caller has requested a transfer
    * @throws MainMenuException if the caller has requested a to be directed to the main menu
    * @throws MaxAttemptsExceededException if the caller has exceeded the maximum amount of attempts
    * @throws FailureTransferException if there is an unknown failure
    */ 
  public String getMeterNumber(ActionStatusEvent actionIn, Locale locale) throws HungupException, TransferException, MainMenuException, MaxAttemptsExceededException, FailureTransferException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDMeterInteraction.getMeterNumber: Entered method.");
    
    if(actionIn == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDMeterInteraction.getMeterNumber: ActionStatusEvent passed to this method is null.");
      throw new FailureTransferException("TDDMeterInteraction.getMeterNumber: ActionStatusEvent passed to this method is null.");
    }
    
    String meterNumber = getKeyData(actionIn, locale, meterNumberMaxDigits, meterNumberMinDigits);
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDMeterInteraction.getMeterNumber: Returning meter number." + meterNumber);
    return meterNumber;
  }
  
  /**
    * This method speaks the passed meter number to the caller and
    * prompts the caller for validation
    *
    * @param actionIn 
    * ActionStatusEvent containing the latest call information
    *
    * @param locale 
    * language to talk to the caller in
    *
    * @param meterNumber 
    * String containing meter number to confirm
    *
    * @return
    * indicating if the meter number was confirmed
    *
    * @throws HungupException if the caller has hungup
    * @throws TransferException if the caller has requested a transfer
    * @throws MainMenuException if the caller has requested a to be directed to the main menu
    * @throws MaxAttemptsExceededException if the caller has exceeded the maximum amount of attempts
    * @throws FailureTransferException if there is an unknown failure
    */ 
  public boolean confirmMeterNumber(ActionStatusEvent actionIn, Locale locale, String meterNumber) throws HungupException, TransferException, MainMenuException, MaxAttemptsExceededException, FailureTransferException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDMeterInteraction.confirmMeterNumber: Entered method.");
    
    if(actionIn == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDMeterInteraction.confirmMeterNumber: ActionStatusEvent passed to this method is null.");
      throw new FailureTransferException("TDDMeterInteraction.confirmMeterNumber: ActionStatusEvent passed to this method is null.");
    }
    
    if(meterNumber == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDMeterInteraction.confirmMeterNumber: Meter number passed to this method is null.");
      throw new FailureTransferException("TDDMeterInteraction.confirmMeterNumber: Meter number passed to this method is null.");
    }
    
    boolean confirm = confirmKeyData(actionIn, locale, meterNumber);
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDMeterInteraction.confirmMeterNumber: Returning boolean." + confirm);
    return confirm;
  } 
  
  /**
    * This method speaks an meter number appears segment to the caller.
    *
    * @param actionIn 
    * ActionStatusEvent containing the latest call information
    * 
    * @param locale 
    * language to talk to the caller in
    *
    * @throws HungupException if the caller has hungup
    * @throws FailureTransferException if there is an unknown failure
    */ 
  public void speakMeterNumberAppears(ActionStatusEvent actionIn, Locale locale) throws HungupException, FailureTransferException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDMeterInteraction.speakMeterNumberAppears: Entered method.");
    
    if(actionIn == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDMeterInteraction.speakMeterNumberAppears: ActionStatusEvent passed to this method is null.");
      throw new FailureTransferException("TDDMeterInteraction.speakMeterNumberAppears: ActionStatusEvent passed to this method is null.");
    }
    
    interaction.playMedia(actionIn, meterNumberAppearsMessage);
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDMeterInteraction.speakMeterNumberAppears: Returning");
  }
  
  /**
    * This method speaks a meter problem thank you message.
    *
    * @param actionIn 
    * ActionStatusEvent containing the latest call information
    * 
    * @param locale 
    * language to talk to the caller in
    *
    * @throws HungupException if the caller has hungup
    * @throws FailureTransferException if there is an unknown failure
    */ 
  public void speakMeterProblemThankYou(ActionStatusEvent actionIn, Locale locale) throws HungupException, FailureTransferException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDMeterInteraction.speakMeterProblemThankYou: Entered method.");
    
    if(actionIn == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDMeterInteraction.speakMeterProblemThankYou: ActionStatusEvent passed to this method is null.");
      throw new FailureTransferException("TDDMeterInteraction.speakMeterProblemThankYou: ActionStatusEvent passed to this method is null.");
    }
    
    interaction.playMedia(actionIn, meterProblemThankYouMessage);
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDMeterInteraction.speakMeterProblemThankYou: Returning");
  }

  /**
    * This method calls ViolationsValidation.validateMeterNumber.  If this 
    * method returns ViolationsValidation.VALID this method returns true.  If the
    * method returns ViolationsValidation.INVALID, this method uses the TDDInteractor to play
    * the default not valid segment in the grandparent class and retuns false. 
    *
    * @param actionStatusEvent 
    * ActionStatusEvent containing latest call info
    *
    * @param locale 
    * language used to retrieve the data
    *
    * @param data 
    * String data to be confirmed
    *
    * @return 
    * indicates if the data was valid
    *
    * @throws HungupException if the caller hangs up
    * @throws FailureTransferException if there is an unknown failure
    */
  protected boolean validateData(ActionStatusEvent actionStatusEvent, Locale locale, String data) throws HungupException, FailureTransferException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDMeterInteraction.validateData: Entered method.");
    
    // check ase for null
    if (actionStatusEvent == null){
       if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDMeterInteraction.validateData: ActionStatusEvent is null.");
       throw new FailureTransferException("TDDMeterInteraction.validateData: ActionStatusEvent is null.");
    }
    
    // check data for null
    if (data == null){
       if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDMeterInteraction.validateData: Data is null.");
       throw new FailureTransferException("TDDMeterInteraction.validateData: Data is null.");
    }
    
    // call validation method
    int validStatus = ViolationsValidation.validateMeterNumber(data);
    
    // boolean to return
    boolean meterValid = false;
    switch(validStatus){
      case ViolationsValidation.METER_VALID:
        meterValid = true;
        break;
      case ViolationsValidation.METER_NOT_VALID:
        interaction.speakInvalidInputMessage(actionStatusEvent, locale);
        break;
      default:
        if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDMeterInteraction.validateData: Meter valid status is not valid.");
    }
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDMeterInteraction.validateData: Returning");
    return meterValid;
  
  }

  /**
    * This method creates an EntryField bean with the entryMessage and uses the TDDInteraction
    * to query the caller for data.  Each time the data is received, it will be checked against
    * the configured transferKey (throw TransferException).  After it retrieves the data it will
    * call validateData to ensure the data is correct.  This method will attempt to get correct
    * data the configured amount of times.  On the first attempt this method will play the meter number
    * appears message, on the other attempts it will only speak the entry segment.
    *
    * @param actionStatusEvent
    * ActionStatusEvent containing the latest call information
    *
    * @param locale
    * Language for the application
    *
    * @param maximumKeys 
    * max amount of keys the caller can enter
    * 
    * @param minimumKeys 
    * max amount of keys the caller can enter
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
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDMeterInteraction.getKeyData: Entered method.");
    
    // check parameters for null
    if(actionStatusEvent == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDMeterInteraction.getKeyData: ActionStatusEvent passed to this method is null");
      throw new FailureTransferException("TDDMeterInteraction.getKeyData: ActionStatusEvent passed to this method is null"); 
    }
      
    // create the voice segment to pass to getInput
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMeterInteraction.getKeyData: Creating the voice segment to ask the caller.");
    
    // create not first time message
    String notFirstTimeMessage = entryMessage + entryKeyMessage;
    
    // create first time message
    String firstTimeMessage = entryMessage + entryKeyMessage + meterNumberAppearsMessage;
    
    // current number of attempts
    int attempts = 0;
    // keyed data
    String keyedData = null;
    // boolean for invalid data
    boolean invalidData = true;
    // loop until we get valid data or we run out of attempts
    while(invalidData && (attempts < maximumAttempts)){
      
      // boolean to keep track of timeout
      boolean timedOut = false;
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMeterInteraction.getKeyData: Attempt: " + (attempts + 1));
      // get the data
      try{
        // check if first time through
        if(attempts == 0){
          // play sequence with extra segment
          keyedData = interaction.getInput(actionStatusEvent, firstTimeMessage, maximumKeys, minimumKeys);
        }
        else{
          keyedData = interaction.getInput(actionStatusEvent, notFirstTimeMessage, maximumKeys, minimumKeys);
        }
      }
      catch(EntryTimeoutException ete){
        if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMeterInteraction.getKeyData: Caller timed out");
      }
      
      // if keyed data is not equal to null, we got data from the user
      if(keyedData != null){
        // check for transfer request
        if(keyedData.equals(transferKey)){
          if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMeterInteraction.getKeyData: Caller pressed " + transferKey + ", throwing TransferException");
          throw new TransferException("TDDMeterInteraction.getKeyData: Caller requested transfer");
        }
        
        // check if data is valid
        boolean dataValidated = validateData(actionStatusEvent, locale, keyedData);
        if(dataValidated == true){
          invalidData = false;
          if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMeterInteraction.getKeyData: Data is valid " + keyedData);
        }
        else{
          // log bad data
          if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMeterInteraction.getKeyData: Data is invalid " + keyedData);
        }
      }
      // increment the attempts
      attempts++;
    }
    
    // if data is invalid now, throw max attempts exception
    if(invalidData){
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMeterInteraction.getKeyData: Max attempts have been exceeded, throwing MaxAttemptsExceededException");
      throw new MaxAttemptsExceededException("TDDMeterInteraction.getKeyData: Max attempts have been exceeded, throwing MaxAttemptsExceededException");
    }
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDMeterInteraction.getKeyData: Leaving method.");    
    return keyedData;
  }//end getKeyData

@Override
protected boolean validateData(ActionStatusEvent arg0, Locale arg1,
		String arg2, boolean arg3) throws HungupException,
		FailureTransferException {
	// TODO Auto-generated method stub
	return false;
}
   

}//end TDDMeterInteraction class