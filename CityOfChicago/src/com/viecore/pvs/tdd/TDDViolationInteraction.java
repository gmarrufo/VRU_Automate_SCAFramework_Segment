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
 * This class contains functionality to obtain and confirm violation number from
 * the caller using TDD.
 * 
 * @author Michael Ruggiero
 * @since JDK 1.3
 * @version 1.0 01/30/2003
 */
 
public class TDDViolationInteraction extends TDDFieldEntryInteraction implements ViolationInteractionIF {

  /**
    * Constant used to retrieve maximum digits for violation number from Properties
    */
  public static final String CONFIG_VIOLATION_MAX_DIGITS	= "ViolationNumberMaximumDigits";
  
  /**
    * Constant used to retrieve minimum digits for violation number from Properties
    */
  public static final String CONFIG_VIOLATION_MIN_DIGITS	= "ViolationNumberMinimumDigits";
  
  /**
    * Constant used to retrieve violation entry segment from Properties
    */
  public static final String CONFIG_VIOLATION_ENTRY_MESSAGE	= "ViolationEntryMessage";
  
  /**
    * Constant used to retrieve confirmation segment from Properties	
    */
  public static final String CONFIG_VIOLATION_CONFIRMATION_MESSAGE = "ViolationConfirmationMessage";
  
  /**
    * Constant used to retrieve violation invalid segment from Properties
    */
  public static final String CONFIG_VIOLATION_INVALID_MESSAGE	= "ViolationInvalidMessage";
  
  /**
    * Constant used to retrieve scheduling warning segment from Properties
    */
  public static final String CONFIG_VIOLATION_SCHEDULE_WARNING_MESSAGE = "ViolationScheduleWarningMessage";
  
  /**
    * Constant used to retrieve duplicate hearing segment from Properties
    */
  public static final String CONFIG_DUPLICATE_HEARING_MESSAGE = "DuplicateHearingMessage";
  
  /**
    * Constant used to retrieve not eligible segment from Properties
    */
  public static final String CONFIG_VIOLATION_NOT_ELIGIBLE_MESSAGE = "ViolationNotEligibleMessage";
  
  /**
    * Constant used to retrieve not available segment from Properties	
    */
  public static final String CONFIG_VIOLATION_NOT_AVAILABLE_MESSAGE	= "ViolationNotAvailableMessage";
  
  /**
    * Maximum amount of digits for ticket
    */
  private int violationNumberMaxDigits;
  
  /**
    * Minimum amount of digits for ticket
    */
  private int violationNumberMinDigits;
  
  /**
    * Segment to play for invalid	violation numbers
    */
  private String invalidMessage	= null;
  
  /**
    * Segment to play for hearing schedule warning
    */
  private String scheduleWarningMessage	= null;
  
  /**
    * Segment to play for duplicate hearings
    */
  private String duplicateHearingMessage= null;
  
  /**
    * Segment to play for violation not eligble
    */
  private String violationNotEligibleMessage = null;
  
  /**
    * Voice segment retrieved from Properties
    */
  private String violationNotAvailableMessage = null;
    
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
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDViolationInteraction.initConfiguration: Entered method.");
    
    // check properties for null
    if(properties == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDViolationInteraction.initConfiguration: Properties passed to this method null");
      throw new ConfigurationException("TDDViolationInteraction.initConfiguration: Properties passed to this method null");
    }
    
    // call the parent
    super.initConfiguration(properties);
    
    // segment asking for violation number max digits
    String stringMax = validateStringProperty(properties, CONFIG_VIOLATION_MAX_DIGITS);
    // parse the integer
    try{
      violationNumberMaxDigits = Integer.parseInt(stringMax);
    }
    catch(NumberFormatException nfe){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDViolationInteraction.initConfiguration: Violation max digits value [" + stringMax + "] in properties is not a parsable integer. ["+nfe.toString()+"]");
      throw new ConfigurationException("TDDViolationInteraction.initConfiguration: Violation max digits value [" + stringMax + "] in properties is not a parsable integer. ["+nfe.toString()+"]");
    }
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDViolationInteraction.initConfiguration: Violation max digits value is ["+violationNumberMaxDigits+"].");
    
    // segment asking for violation number min digits
    String stringMin = validateStringProperty(properties, CONFIG_VIOLATION_MIN_DIGITS);
    // parse the integer
    try{
      violationNumberMinDigits = Integer.parseInt(stringMin);
    }
    catch(NumberFormatException nfe){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDViolationInteraction.initConfiguration: Violation min digits value [" + stringMin + "] in properties is not a parsable integer. ["+nfe+"]");
      throw new ConfigurationException("TDDViolationInteraction.initConfiguration: Violation min digits value [" + stringMin + "] in properties is not a parsable integer. ["+nfe.toString()+"]");
    }
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDViolationInteraction.initConfiguration: Violation min digits value is ["+violationNumberMaxDigits+"].");
    
    // segment asking for violation number
    entryMessage = validateStringProperty(properties, CONFIG_VIOLATION_ENTRY_MESSAGE);
    // segment confirmation
    confirmationMessage = validateStringProperty(properties, CONFIG_VIOLATION_CONFIRMATION_MESSAGE);
    // segment invalid
    invalidMessage = validateStringProperty(properties, CONFIG_VIOLATION_INVALID_MESSAGE);
    // segment schedule hearing warning
    scheduleWarningMessage = validateStringProperty(properties, CONFIG_VIOLATION_SCHEDULE_WARNING_MESSAGE);
    // segment duplicate hearing
    duplicateHearingMessage = validateStringProperty(properties, CONFIG_DUPLICATE_HEARING_MESSAGE);
    // segment not eligible
    violationNotEligibleMessage = validateStringProperty(properties, CONFIG_VIOLATION_NOT_ELIGIBLE_MESSAGE);
    // segment not available
    violationNotAvailableMessage = validateStringProperty(properties, CONFIG_VIOLATION_NOT_AVAILABLE_MESSAGE);

    configured = true;
        
    //set configured flag to true
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDViolationInteraction.initConfiguration: Configuration complete, setting configured flag to true, leaving method.");

    
  }//end initConfiguration
  
  /**
    * This method speaks a prompt for the violation number to the caller
    * and returns the callers input
    *
    * @param actionIn 
    * ActionStatusEvent containing the latest call information
    * 
    * @param locale 
    * language to talk to the caller in
    *
    * @return
    * violation number
    *
    * @throws HungupException if the caller has hungup
    * @throws TransferException if the caller has requested a transfer
    * @throws MainMenuException if the caller has requested a to be directed to the main menu
    * @throws MaxAttemptsExceededException if the caller has exceeded the maximum amount of attempts
    * @throws FailureTransferException if there is an unknown failure
    */ 
  public String getViolationNumber(ActionStatusEvent actionIn, Locale locale) throws HungupException, TransferException, MainMenuException, MaxAttemptsExceededException, FailureTransferException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDViolationInteraction.getViolationNumber: Entered method.");
    
    // check parameters for null
    if(actionIn == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDWelcome.getViolationNumber: ActionStatusEvent passed to this method is null");
      throw new FailureTransferException("TDDWelcome.getViolationNumber: ActionStatusEvent passed to this method is null"); 
    }
    
    String violationNumber = getKeyData(actionIn, locale, violationNumberMaxDigits, violationNumberMinDigits);
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDViolationInteraction.getViolationNumber: Returning violation number." + violationNumber);
    return violationNumber;
  }
  
  /**
    * This method speaks the passed violation number to the caller and
    * prompts the caller for validation
    *
    * @param actionIn 
    * ActionStatusEvent containing the latest call information
    * 
    * @param locale 
    * language to talk to the caller in
    * 
    * @param violationNumber 
    * String containing violation number to confirm
    *
    * @return
    * indicating if the violation number was confirmed
    *
    * @throws HungupException if the caller has hungup
    * @throws TransferException if the caller has requested a transfer
    * @throws MainMenuException if the caller has requested a to be directed to the main menu
    * @throws MaxAttemptsExceededException if the caller has exceeded the maximum amount of attempts
    * @throws FailureTransferException if there is an unknown failure
    */ 
  public boolean confirmViolationNumber(ActionStatusEvent actionIn, Locale locale, String violationNumber) throws HungupException, TransferException, MainMenuException, MaxAttemptsExceededException, FailureTransferException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDViolationInteraction.confirmViolationNumber: Entered method.");
    
    // check parameters for null
    if(actionIn == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDViolationInteraction.confirmViolationNumber: ActionStatusEvent passed to this method is null");
      throw new FailureTransferException("TDDViolationInteraction.confirmViolationNumber: ActionStatusEvent passed to this method is null"); 
    }
    
    if(violationNumber == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDViolationInteraction.confirmViolationNumber: Violation number passed to this method is null");
      throw new FailureTransferException("TDDViolationInteraction.confirmViolationNumber: Violation number passed to this method is null"); 
    }
    
    boolean confirm = confirmKeyData(actionIn, locale, violationNumber);
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDViolationInteraction.confirmViolationNumber: Returning boolean." + confirm);
    return confirm;
  } 
  
  /**
    * This method speaks an invalid violation segment to the caller.
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
  public void speakInvalidViolation(ActionStatusEvent actionIn, Locale locale) throws HungupException, FailureTransferException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDViolationInteraction.speakInvalidViolation: Entered method.");
    
    // check parameters for null
    if(actionIn == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDViolationInteraction.speakInvalidViolation: ActionStatusEvent passed to this method is null");
      throw new FailureTransferException("TDDViolationInteraction.speakInvalidViolation: ActionStatusEvent passed to this method is null"); 
    }
    
    interaction.playMedia(actionIn, invalidMessage);
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDViolationInteraction.speakInvalidViolation: Returning");
  }
  
  /**
    * This method speaks a duplicate hearing segment to the caller.
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
  public void speakDuplicateHearingRequest(ActionStatusEvent actionIn, Locale locale) throws HungupException, FailureTransferException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDViolationInteraction.speakDuplicateHearingRequest: Entered method.");
    
    // check parameters for null
    if(actionIn == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDViolationInteraction.speakDuplicateHearingRequest: ActionStatusEvent passed to this method is null");
      throw new FailureTransferException("TDDViolationInteraction.speakDuplicateHearingRequest: ActionStatusEvent passed to this method is null"); 
    }
    
    interaction.playMedia(actionIn, duplicateHearingMessage);
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDViolationInteraction.speakDuplicateHearingRequest: Returning");
  }
  
  /**
    * This method speaks a schedule warning segment to the caller
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
  public void speakScheduleWarning(ActionStatusEvent actionIn, Locale locale) throws HungupException, FailureTransferException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDViolationInteraction.speakScheduleWarning: Entered method.");
    
     // check parameters for null
    if(actionIn == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDViolationInteraction.speakScheduleWarning: ActionStatusEvent passed to this method is null");
      throw new FailureTransferException("TDDViolationInteraction.speakScheduleWarning: ActionStatusEvent passed to this method is null"); 
    }
       
    interaction.playMedia(actionIn, scheduleWarningMessage);
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDViolationInteraction.speakScheduleWarning: Returning");
  }
  
  /**
    * This method speaks a violation not eligible segment to the caller.
    *
    * @param actionIn 
    * ActionStatusEvent containing the latest call information
    * 
    * @param locale 
    *language to talk to the caller in
    *
    * @throws HungupException if the caller has hungup
    * @throws FailureTransferException if there is an unknown failure
    */ 
  public void speakViolationNotEligible(ActionStatusEvent actionIn, Locale locale) throws HungupException, FailureTransferException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDViolationInteraction.speakViolationNotEligible: Entered method.");
    
    // check parameters for null
    if(actionIn == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDViolationInteraction.speakViolationNotEligible: ActionStatusEvent passed to this method is null");
      throw new FailureTransferException("TDDViolationInteraction.speakViolationNotEligible: ActionStatusEvent passed to this method is null"); 
    }
    
    interaction.playMedia(actionIn, violationNotEligibleMessage);
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDViolationInteraction.speakViolationNotEligible: Returning");
  }
  
  /**
    * This method speaks a violation not available segment to the caller.
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
  public void speakViolationNotAvailable(ActionStatusEvent actionIn, Locale locale) throws HungupException, FailureTransferException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDViolationInteraction.speakViolationNotAvailable: Entered method.");
    
    // check parameters for null
    if(actionIn == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDViolationInteraction.speakViolationNotAvailable: ActionStatusEvent passed to this method is null");
      throw new FailureTransferException("TDDViolationInteraction.speakViolationNotAvailable: ActionStatusEvent passed to this method is null"); 
    }
    
    interaction.playMedia(actionIn, violationNotAvailableMessage);
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDViolationInteraction.speakViolationNotAvailable: Returning");
  }
  
  /**
    * This method calls ViolationsValidation.validateViolationNumber.  If this 
    * method returns ViolationsValidation.VALID this method returns true.  If the
    * method returns ViolationsValidation.INVALID, this method uses the TDDInteractor
    * to play the default not valid segment in the grandparent class and retuns false.
    * If the method returns ViolationsValidation.VIOLATION_NOT_00_OR_90, this method
    * uses the TDDInteractor to play the violation not valid segment in this class
    * and returns false.
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
    * @throws FailureTransferException if there is a unknown failure
    */
  protected boolean validateData(ActionStatusEvent actionStatusEvent, Locale locale, String data) throws HungupException, FailureTransferException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDViolationInteraction.validateData: Entered method.");
    
    // check ase for null
    if (actionStatusEvent == null){
       if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDViolationInteraction.validateData: ActionStatusEvent is null.");
       throw new FailureTransferException("TDDViolationInteraction.validateData: ActionStatusEvent is null.");
    }
    
    // check data for null
    if (data == null){
       if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDViolationInteraction.validateData: Data is null.");
       throw new FailureTransferException("TDDViolationInteraction.validateData: Data is null.");
    }
    
    // call validation method
    int validStatus = ViolationsValidation.validateViolationNumber(data);
    
    // boolean to return
    boolean violationValid = false;
    switch(validStatus){
      case ViolationsValidation.VIOLATION_VALID:
        violationValid = true;
        break;
      case ViolationsValidation.VIOLATION_NOT_VALID:
        interaction.speakInvalidInputMessage(actionStatusEvent, locale);
        break;
      case ViolationsValidation.VIOLATION_NOT_00_OR_90:
        speakInvalidViolation(actionStatusEvent, locale);
        break;
    }
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDViolationInteraction.validateData: Returning");
    return violationValid;
  
  }

@Override
protected boolean validateData(ActionStatusEvent arg0, Locale arg1,
		String arg2, boolean arg3) throws HungupException,
		FailureTransferException {
	// TODO Auto-generated method stub
	return false;
}
   

}//end TDDViolationInteraction class