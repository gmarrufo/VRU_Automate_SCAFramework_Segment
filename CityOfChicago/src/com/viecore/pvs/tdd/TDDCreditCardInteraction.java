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
 * This class contains functionality to obtain and confirm credit card data from
 * the caller using TDD.
 * 
 * @author Michael Ruggiero
 * @since JDK 1.3
 * @version 1.0 01/16/2003
 */
 
public class TDDCreditCardInteraction extends TDDFieldEntryInteraction implements CreditCardInteractionIF {

  /**
    * Type of data being retrieved - Credit Card
    */
  public static final int DATA_TYPE_CREDIT_CARD = 0;
  
  /**
    * Type of data being retrieved - Zip Code
    */
  public static final int DATA_TYPE_ZIP_CODE = 1;
  
  /**
    * Type of data being retrieved - CID
    */
  public static final int DATA_TYPE_CID = 2;
  
  /**
    * Type of data being retrieved - American Express CID
    */
  public static final int DATA_TYPE_AMEX_CID = 3;
  
  /**
    * Type of data being retrieved - Expiration Date
    */
  public static final int DATA_TYPE_EXPIRATION_DATE = 4;
  
  /**
    * Type of data being retreived
    */
  private int dataType;
    
  /**
    * Constant used to retrieve maximum digits for credit card number from Properties
    */
  public static final String CONFIG_CREDIT_CARD_MAX_DIGITS	= "CreditCardNumberMaximumDigits";
  
  /**
    * Constant used to retrieve minimum digits for credit card number from Properties
    */
  public static final String CONFIG_CREDIT_CARD_MIN_DIGITS	= "CreditCardNumberMinimumDigits";
  
  /**
    * Constant used to retrieve credit card entry segment from Properties
    */
  public static final String CONFIG_CREDIT_CARD_ENTRY_MESSAGE	= "CreditCardEntryMessage";
  
  /**
    * Constant used to retrieve confirmation segment from Properties	
    */
  public static final String CONFIG_CREDIT_CARD_CONFIRMATION_MESSAGE = "CreditCardConfirmationMessage";
  
  /**
    * Credit card entry segment
    */
  private String creditCardEntryMessage = null;
  
  /**
    * Credit card confirmation segment
    */
  private String creditCardConfirmationMessage = null;
  
  /**
    * Maximum amount of digits for credit card
    */
  private int creditCardNumberMaxDigits;
  
  /**
    * Minimum amount of digits for credit card
    */
  private int creditCardNumberMinDigits;
  
  /**
    * Constant used to retrieve maximum digits for zip code from Properties
    */
  public static final String CONFIG_ZIP_CODE_MAX_DIGITS	= "ZipCodeMaximumDigits";
  
  /**
    * Constant used to retrieve min digits for zip code from Properties
    */
  public static final String CONFIG_ZIP_CODE_MIN_DIGITS	= "ZipCodeMinimumDigits";
  
  /**
    * Constant used to retrieve zip code entry segment from Properties
    */
  public static final String CONFIG_ZIP_CODE_ENTRY_MESSAGE	= "ZipCodeEntryMessage";
  
  /**
    * Constant used to retrieve confirmation segment from Properties	
    */
  public static final String CONFIG_ZIP_CODE_CONFIRMATION_MESSAGE = "ZipCodeConfirmationMessage";
  
  /**
    * Zip code entry segment
    */
  private String zipCodeEntryMessage = null;
  
  /**
    * Zip code confirmation segment
    */
  private String zipCodeConfirmationMessage = null;
  
  /**
    * Maximum amount of digits for zip code
    */
  private int zipCodeNumberMaxDigits;
  
  /**
    * Minimum amount of digits for zip code
    */
  private int zipCodeNumberMinDigits;
  
  /**
    * Constant used to retrieve maximum digits for month - year from Properties
    */
  public static final String CONFIG_EXPIRATION_DATE_MAX_DIGITS	= "ExpirationDateMaximumDigits";
  
  /**
    * Constant used to retrieve minimum digits for month - year from Properties
    */
  public static final String CONFIG_EXPIRATION_DATE_MIN_DIGITS	= "ExpirationDateMinimumDigits";
  
  /**
    * Constant used to retrieve expiration date entry segment from Properties
    */
  public static final String CONFIG_EXPIRATION_DATE_ENTRY_MESSAGE	= "ExpirationDateEntryMessage";
  
  /**
    * Constant used to retrieve confirmation segment from Properties	
    */
  public static final String CONFIG_EXPIRATION_DATE_CONFIRMATION_MESSAGE = "ExpirationDateConfirmationMessage";
  
  /**
    * Expiration date entry segment
    */
  private String expirationDateEntryMessage = null;
  
  /**
    * Expiration date confirmation segment
    */
  private String expirationDateConfirmationMessage = null;
  
  /**
    * Maximum amount of digits for expiration date
    */
  private int expirationDateMaxDigits;
  
  /**
    * Minimum amount of digits for expiration date
    */
  private int expirationDateMinDigits;
  
  /**
    * Constant used to retrieve maximum digits for cid from Properties
    */
  public static final String CONFIG_CID_MAX_DIGITS	= "CIDMaximumDigits";
  
  /**
    * Constant used to retrieve minimum digits for cid from Properties
    */
  public static final String CONFIG_CID_MIN_DIGITS	= "CIDMinimumDigits";
  
  /**
    * Constant used to retrieve cid entry segment from Properties
    */
  public static final String CONFIG_CID_ENTRY_MESSAGE	= "CIDEntryMessage";
  
  /**
    * Constant used to retrieve confirmation segment from Properties	
    */
  public static final String CONFIG_CID_CONFIRMATION_MESSAGE = "CIDConfirmationMessage";
  
  /**
    * Constant used to retrieve CID can be found segment from Properties	
    */
  public static final String CONFIG_CID_CAN_BE_FOUND_MESSAGE = "CIDCanBeFoundMessage";
  
  /**
    * CID can be found segment
    */
  private String cidCanBeFoundMessage = null;
  
  /**
    * CID entry segment
    */
  private String cidEntryMessage = null;
  
  /**
    * CID confirmation segment
    */
  private String cidConfirmationMessage = null;
  
  /**
    * Maximum amount of digits for cid
    */
  private int cidMaxDigits;
  
  /**
    * Minimum amount of digits for cid
    */
  private int cidMinDigits;
  
  /**
    * Constant used to retrieve maximum digits for an Amex cid from Properties
    */
  public static final String CONFIG_AMEX_CID_MAX_DIGITS	= "AmexCIDMaximumDigits";
  
  /**
    * Constant used to retrieve minimum digits for an Amex cid from Properties
    */
  public static final String CONFIG_AMEX_CID_MIN_DIGITS	= "AmexCIDMinimumDigits";
  
  /**
    * Constant used to retrieve amex cid entry segment from Properties
    */
  public static final String CONFIG_AMEX_CID_ENTRY_MESSAGE	= "AmexCIDEntryMessage";
  
  /**
    * Constant used to retrieve amex CID can be found segment from Properties	
    */
  public static final String CONFIG_AMEX_CID_CAN_BE_FOUND_MESSAGE = "AmexCIDCanBeFoundMessage";
  
  /**
    * Amex CID can be found segment
    */
  private String amexCIDCanBeFoundMessage = null;
  
  /**
    * Amex CID entry segment
    */
  private String amexCIDEntryMessage = null;
  
  /**
    * Amex maximum amount of digits for cid
    */
  private int amexCIDMaxDigits;
  
  /**
    * Amex minimum amount of digits for cid
    */
  private int amexCIDMinDigits;
  
  /**
    * Constant used to retrieve amex CID can be found segment from Properties	
    */
  public static final String CONFIG_CREDIT_CARD_HOLD_MESSAGE = "CreditCardHoldMessage";
  
  /**
    * Message to play while caller is waiting for credit card authorization
    */
  private String holdMessage = null;
  


  /**
    * Initializes this object with the configuration in the passed Properties object
    *
    * @param properties
    *     Properties object containing the main application properties for the application
    *
    * @throws ConfigurationException if the configuration contained in the Properties object
    * is not valid
    */
  public void initConfiguration(Properties properties) throws ConfigurationException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDCreditCardInteraction.initConfiguration: Entered method.");
    
    // check properties for null
    if(properties == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCreditCardInteraction.initConfiguration: Properties passed to this method null");
      throw new ConfigurationException("TDDCreditCardInteraction.initConfiguration: Properties passed to this method null");
    }
    
    // call the parent
    super.initConfiguration(properties);
    
    // get String fields
    creditCardEntryMessage = getProperty(properties, CONFIG_CREDIT_CARD_ENTRY_MESSAGE, true);
    creditCardConfirmationMessage = getProperty(properties, CONFIG_CREDIT_CARD_CONFIRMATION_MESSAGE, true);
    String stringCreditCardMax = getProperty(properties, CONFIG_CREDIT_CARD_MAX_DIGITS, true);
    // parse the integer
    try{
      creditCardNumberMaxDigits = Integer.parseInt(stringCreditCardMax);
    }
    catch(NumberFormatException nfe){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCreditCardInteraction.initConfiguration: Credit card max digits value in properties is not a parsable integer. ["+nfe.toString()+"]");
      throw new ConfigurationException("TDDCreditCardInteraction.initConfiguration: Credit card max digits value in properties is not a parsable integer. ["+nfe.toString()+"]");
    }
    String stringCreditCardMin = getProperty(properties, CONFIG_CREDIT_CARD_MIN_DIGITS, true);
    // parse the integer
    try{
      creditCardNumberMinDigits = Integer.parseInt(stringCreditCardMin);
    }
    catch(NumberFormatException nfe){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCreditCardInteraction.initConfiguration: Credit card min digits value in properties is not a parsable integer. ["+nfe.toString()+"]");
      throw new ConfigurationException("TDDCreditCardInteraction.initConfiguration: Credit card min digits value in properties is not a parsable integer. ["+nfe.toString()+"]");
    }
    zipCodeEntryMessage = getProperty(properties, CONFIG_ZIP_CODE_ENTRY_MESSAGE, true);
    zipCodeConfirmationMessage = getProperty(properties, CONFIG_ZIP_CODE_CONFIRMATION_MESSAGE, true);
    String stringZipCodeMax = getProperty(properties, CONFIG_ZIP_CODE_MAX_DIGITS, true);
    // parse the integer
    try{
      zipCodeNumberMaxDigits = Integer.parseInt(stringZipCodeMax);
    }
    catch(NumberFormatException nfe){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCreditCardInteraction.initConfiguration: Zip code max digits value in properties is not a parsable integer. ["+nfe.toString()+"]");
      throw new ConfigurationException("TDDCreditCardInteraction.initConfiguration: Zip code max digits value in properties is not a parsable integer. ["+nfe.toString()+"]");
    }
    String stringZipCodeMin = getProperty(properties, CONFIG_ZIP_CODE_MIN_DIGITS, true);
    // parse the integer
    try{
      zipCodeNumberMinDigits = Integer.parseInt(stringZipCodeMin);
    }
    catch(NumberFormatException nfe){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCreditCardInteraction.initConfiguration: Zip code min digits value in properties is not a parsable integer. ["+nfe.toString()+"]");
      throw new ConfigurationException("TDDCreditCardInteraction.initConfiguration: Zip code min digits value in properties is not a parsable integer. ["+nfe.toString()+"]");
    }
    expirationDateEntryMessage = getProperty(properties, CONFIG_EXPIRATION_DATE_ENTRY_MESSAGE, true);
    expirationDateConfirmationMessage = getProperty(properties, CONFIG_EXPIRATION_DATE_CONFIRMATION_MESSAGE, true);
    String stringExpirationDateMax = getProperty(properties, CONFIG_EXPIRATION_DATE_MAX_DIGITS, true);
    // parse the integer
    try{
      expirationDateMaxDigits = Integer.parseInt(stringExpirationDateMax);
    }
    catch(NumberFormatException nfe){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCreditCardInteraction.initConfiguration: Expiration date max digits value in properties is not a parsable integer. ["+nfe.toString()+"]");
      throw new ConfigurationException("TDDCreditCardInteraction.initConfiguration: Expiration date max digits value in properties is not a parsable integer. ["+nfe.toString()+"]");
    }
    String stringExpirationDateMin = getProperty(properties, CONFIG_EXPIRATION_DATE_MIN_DIGITS, true);
    // parse the integer
    try{
      expirationDateMinDigits = Integer.parseInt(stringExpirationDateMin);
    }
    catch(NumberFormatException nfe){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCreditCardInteraction.initConfiguration: Expiration date min digits value in properties is not a parsable integer. ["+nfe.toString()+"]");
      throw new ConfigurationException("TDDCreditCardInteraction.initConfiguration: Expiration date min digits value in properties is not a parsable integer. ["+nfe.toString()+"]");
    }
    cidEntryMessage = getProperty(properties, CONFIG_CID_ENTRY_MESSAGE, true);
    cidConfirmationMessage = getProperty(properties, CONFIG_CID_CONFIRMATION_MESSAGE, true);
    cidCanBeFoundMessage = getProperty(properties, CONFIG_CID_CAN_BE_FOUND_MESSAGE, true);
    String stringCIDMax = getProperty(properties, CONFIG_CID_MAX_DIGITS, true);
    // parse the integer
    try{
      cidMaxDigits = Integer.parseInt(stringCIDMax);
    }
    catch(NumberFormatException nfe){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCreditCardInteraction.initConfiguration: CID max digits value in properties is not a parsable integer. ["+nfe.toString()+"]");
      throw new ConfigurationException("TDDCreditCardInteraction.initConfiguration: CID max digits value in properties is not a parsable integer. ["+nfe.toString()+"]");
    }
    String stringCIDMin = getProperty(properties, CONFIG_CID_MIN_DIGITS, true);
    // parse the integer
    try{
      cidMinDigits = Integer.parseInt(stringCIDMin);
    }
    catch(NumberFormatException nfe){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCreditCardInteraction.initConfiguration: CID min digits value in properties is not a parsable integer. ["+nfe.toString()+"]");
      throw new ConfigurationException("TDDCreditCardInteraction.initConfiguration: CID min digits value in properties is not a parsable integer. ["+nfe.toString()+"]");
    }
    amexCIDEntryMessage = getProperty(properties, CONFIG_AMEX_CID_ENTRY_MESSAGE, true);
    amexCIDCanBeFoundMessage = getProperty(properties, CONFIG_AMEX_CID_CAN_BE_FOUND_MESSAGE, true);
    String stringAmexCIDMax = getProperty(properties, CONFIG_AMEX_CID_MAX_DIGITS, true);
    // parse the integer
    try{
      amexCIDMaxDigits = Integer.parseInt(stringAmexCIDMax);
    }
    catch(NumberFormatException nfe){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCreditCardInteraction.initConfiguration: Amex CID max digits value in properties is not a parsable integer. ["+nfe.toString()+"]");
      throw new ConfigurationException("TDDCreditCardInteraction.initConfiguration: Amex CID max digits value in properties is not a parsable integer. ["+nfe.toString()+"]");
    }
    String stringAmexCIDMin = getProperty(properties, CONFIG_AMEX_CID_MIN_DIGITS, true);
    // parse the integer
    try{
      amexCIDMinDigits = Integer.parseInt(stringAmexCIDMin);
    }
    catch(NumberFormatException nfe){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCreditCardInteraction.initConfiguration: Amex CID min digits value in properties is not a parsable integer. ["+nfe.toString()+"]");
      throw new ConfigurationException("TDDCreditCardInteraction.initConfiguration: Amex CID min digits value in properties is not a parsable integer. ["+nfe.toString()+"]");
    }
    
    holdMessage = getProperty(properties, CONFIG_CREDIT_CARD_HOLD_MESSAGE, true);
    
    //set configured flag to true
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDCreditCardInteraction.initConfiguration: Configuration complete, setting configured flag to true, leaving method.");
    configured = true;
    
  }//end initConfiguration
  
  /**
    * This method speaks a prompt for the credit card number to the caller
    * and returns the callers input
    *
    * @param actionIn 
    * ActionStatusEvent containing the latest call information
    *
    * @param locale 
    * language to talk to the caller in
    *
    * @return
    * credit card number
    *
    * @throws HungupException if the caller has hungup
    * @throws TransferException if the caller has requested a transfer
    * @throws MainMenuException if the caller has requested a to be directed to the main menu
    * @throws MaxAttemptsExceededException if the caller has exceeded the maximum amount of attempts
    * @throws FailureTransferException if there is an unknown failure
    */ 
  public String getCreditCardNumber(ActionStatusEvent actionIn, Locale locale) throws HungupException, TransferException, MainMenuException, MaxAttemptsExceededException, FailureTransferException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDCreditCardInteraction.getCreditCardNumber: Entered method.");
    
    // check ase for null
    if (actionIn == null){
       if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCreditCardInteraction.getCreditCardNumber: ActionStatusEvent is null.");
       throw new FailureTransferException("TDDCreditCardInteraction.getCreditCardNumber: ActionStatusEvent is null.");
    }
    
    // set data type
    dataType = DATA_TYPE_CREDIT_CARD;    
    // set the entry message
    entryMessage = creditCardEntryMessage;
    // call get the data
    String creditCardNumber = getKeyData(actionIn, locale, creditCardNumberMaxDigits, creditCardNumberMinDigits);
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDCreditCardInteraction.getCreditCardNumber: Returning credit card number");
    return creditCardNumber;
  }
  
  /**
    * This method speaks the passed credit card number to the caller and
    * prompts the caller for validation
    *
    * @param actionIn 
    * ActionStatusEvent containing the latest call information
    *
    * @param locale 
    * language to talk to the caller in
    *
    * @param creditCardNumber 
    * String containing credit card number to confirm
    *
    * @return
    * indicating if the credit card number was confirmed
    *
    * @throws HungupException if the caller has hungup
    * @throws TransferException if the caller has requested a transfer
    * @throws MainMenuException if the caller has requested a to be directed to the main menu
    * @throws MaxAttemptsExceededException if the caller has exceeded the maximum amount of attempts
    * @throws FailureTransferException if there is an unknown failure
    */ 
  public boolean confirmCreditCardNumber(ActionStatusEvent actionIn, Locale locale, String creditCardNumber) throws HungupException, TransferException, MainMenuException, MaxAttemptsExceededException, FailureTransferException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDCreditCardInteraction.confirmCreditCardNumber: Entered method.");
    
    // check ase for null
    if (actionIn == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCreditCardInteraction.confirmCreditCardNumber: ActionStatusEvent is null.");
      throw new FailureTransferException("TDDCreditCardInteraction.confirmCreditCardNumber: ActionStatusEvent is null.");
    }
    
    // check data for null
    if (creditCardNumber == null){
       if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCreditCardInteraction.confirmCreditCardNumber: Credit card number is null.");
       throw new FailureTransferException("TDDCreditCardInteraction.confirmCreditCardNumber: Credit card number is null.");
    }
    
    
    // set confirmation message
    confirmationMessage = creditCardConfirmationMessage;
    // confirm the data
    boolean confirm = confirmKeyData(actionIn, locale, creditCardNumber);
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDCreditCardInteraction.confirmCreditCardNumber: Returning boolean");
    return confirm;
  }
  
  /**
    * This method speaks a prompt for the zip code to the caller
    * and returns the callers input
    *
    * @param actionIn 
    * ActionStatusEvent containing the latest call information
    *
    * @param locale 
    * language to talk to the caller in
    *
    * @return
    * zip code
    *
    * @throws HungupException if the caller has hungup
    * @throws TransferException if the caller has requested a transfer
    * @throws MainMenuException if the caller has requested to be directed to the main menu
    * @throws MaxAttemptsExceededException if the caller has exceeded the maximum amount of attempts
    * @throws FailureTransferException if there is an unknown failure
    */ 
  public String getZipCode(ActionStatusEvent actionIn, Locale locale) throws HungupException, TransferException, MainMenuException, MaxAttemptsExceededException, FailureTransferException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDCreditCardInteraction.getZipCode: Entered method.");
    
    // check ase for null
    if (actionIn == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCreditCardInteraction.getZipCode: ActionStatusEvent is null.");
      throw new FailureTransferException("TDDCreditCardInteraction.getZipCode: ActionStatusEvent is null.");
    }
    
    // set data type
    dataType = DATA_TYPE_ZIP_CODE;    
    // set the entry message
    entryMessage = zipCodeEntryMessage;
    // call get the data
    String zipCode = getKeyData(actionIn, locale, zipCodeNumberMaxDigits, zipCodeNumberMinDigits);
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDCreditCardInteraction.getZipCode: Returning zip code");
    return zipCode;
  }
  
  /**
    * This method speaks the passed zip code to the caller and
    * prompts the caller for validation
    *
    * @param actionIn 
    * ActionStatusEvent containing the latest call information
    *
    * @param locale 
    * language to talk to the caller in
    * 
    * @param zipCode 
    * String containing zip code to confirm
    *
    * @return
    * indicating if the zip code card number was confirmed
    *
    * @throws HungupException if the caller has hungup
    * @throws TransferException if the caller has requested a transfer
    * @throws MainMenuException if the caller has requested a to be directed to the main menu
    * @throws MaxAttemptsExceededException if the caller has exceeded the maximum amount of attempts
    * @throws FailureTransferException if there is an unknown failure
    */ 
  public boolean confirmZipCode(ActionStatusEvent actionIn, Locale locale, String zipCode) throws HungupException, TransferException, MainMenuException, MaxAttemptsExceededException, FailureTransferException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDCreditCardInteraction.confirmZipCode: Entered method.");
    
    // check ase for null
    if (actionIn == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCreditCardInteraction.confirmZipCode: ActionStatusEvent is null.");
      throw new FailureTransferException("TDDCreditCardInteraction.confirmZipCode: ActionStatusEvent is null.");
    }
    
    // check data for null
    if (zipCode == null){
       if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCreditCardInteraction.confirmCreditCardNumber: Zip code is null.");
       throw new FailureTransferException("TDDCreditCardInteraction.confirmCreditCardNumber: Zip code is null.");
    }
    
    // set confirmation message
    confirmationMessage = zipCodeConfirmationMessage;
    // confirm the data
    boolean confirm = confirmKeyData(actionIn, locale, zipCode);
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDCreditCardInteraction.confirmZipCode: Returning boolean");
    return confirm;
  }
  
  /**
    * This method speaks a prompt for the expiration date to the caller
    * and returns the callers input
    *
    * @param actionIn 
    * ActionStatusEvent containing the latest call information
    *
    * @param locale 
    * language to talk to the caller in
    *
    * @return
    * expiration date
    *
    * @throws HungupException if the caller has hungup
    * @throws TransferException if the caller has requested a transfer
    * @throws MainMenuException if the caller has requested a to be directed to the main menu
    * @throws MaxAttemptsExceededException if the caller has exceeded the maximum amount of attempts
    * @throws FailureTransferException if there is an unknown failure
    */ 
  public String getExpirationDate(ActionStatusEvent actionIn, Locale locale) throws HungupException, TransferException, MainMenuException, MaxAttemptsExceededException, FailureTransferException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDCreditCardInteraction.getExpirationDate: Entered method.");
    
    // check ase for null
    if (actionIn == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCreditCardInteraction.getExpirationDate: ActionStatusEvent is null.");
      throw new FailureTransferException("TDDCreditCardInteraction.getExpirationDate: ActionStatusEvent is null.");
    }
    
    // set data type
    dataType = DATA_TYPE_EXPIRATION_DATE;    
    // set the entry message
    entryMessage = expirationDateEntryMessage;
    // call get the data
    String expirationDate = getKeyData(actionIn, locale, expirationDateMaxDigits, expirationDateMinDigits);
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDCreditCardInteraction.getExpirationDate: Returning expiration date");
    return expirationDate;
  }
  
  /**
    * This method speaks the passed expiration date to the caller and
    * prompts the caller for validation
    *
    * @param actionIn 
    * ActionStatusEvent containing the latest call information
    * 
    * @param locale 
    * language to talk to the caller in
    *
    * @param expirationDate 
    * String containing expiration date to confirm
    *
    * @return
    * indicating if the expiration date was confirmed
    *
    * @throws HungupException if the caller has hungup
    * @throws TransferException if the caller has requested a transfer
    * @throws MainMenuException if the caller has requested a to be directed to the main menu
    * @throws MaxAttemptsExceededException if the caller has exceeded the maximum amount of attempts
    * @throws FailureTransferException if there is an unknown failure
    */ 
  public boolean confirmExpirationDate(ActionStatusEvent actionIn, Locale locale, String expirationDate) throws HungupException, TransferException, MainMenuException, MaxAttemptsExceededException, FailureTransferException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDCreditCardInteraction.confirmExpirationDate: Entered method.");
    
    // check ase for null
    if (actionIn == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCreditCardInteraction.confirmExpirationDate: ActionStatusEvent is null.");
      throw new FailureTransferException("TDDCreditCardInteraction.confirmExpirationDate: ActionStatusEvent is null.");
    }
    
    // check data for null
    if (expirationDate == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCreditCardInteraction.confirmExpirationDate: Expiration date is null.");
      throw new FailureTransferException("TDDCreditCardInteraction.confirmExpirationDate: Expiration date is null.");
    }
    
    // set confirmation message
    confirmationMessage = expirationDateConfirmationMessage;
    // confirm the data
    boolean confirm = confirmKeyData(actionIn, locale, expirationDate);
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDCreditCardInteraction.confirmExpirationDate: Returning boolean");
    return confirm;
  }
  
  /**
    * This method speaks a prompt for the CID to the caller
    * and returns the callers input
    *
    * @param actionIn 
    * ActionStatusEvent containing the latest call information
    *
    * @param locale 
    * language to talk to the caller in
    *
    * @return
    * CID
    *
    * @throws HungupException if the caller has hungup
    * @throws TransferException if the caller has requested a transfer
    * @throws MainMenuException if the caller has requested a to be directed to the main menu
    * @throws MaxAttemptsExceededException if the caller has exceeded the maximum amount of attempts
    * @throws FailureTransferException if there is an unknown failure
    */ 
  public String getCID(ActionStatusEvent actionIn, Locale locale) throws HungupException, TransferException, MainMenuException, MaxAttemptsExceededException, FailureTransferException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDCreditCardInteraction.getCID: Entered method.");
    
    // check ase for null
    if (actionIn == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCreditCardInteraction.getCID: ActionStatusEvent is null.");
      throw new FailureTransferException("TDDCreditCardInteraction.getCID: ActionStatusEvent is null.");
    }
    
    // set data type
    dataType = DATA_TYPE_CID;    
    // set the entry message
    entryMessage = cidEntryMessage;
    // call get the data
    String cid = getKeyData(actionIn, locale, cidMaxDigits, cidMinDigits);
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDCreditCardInteraction.getCID: Returning cid");
    return cid;
  }
  
  /**
    * This method speaks a prompt for the Amex CID to the caller
    * and returns the callers input
    *
    * @param actionIn 
    * ActionStatusEvent containing the latest call information
    *
    * @param locale 
    * language to talk to the caller in
    *
    * @return
    * CID
    *
    * @throws HungupException if the caller has hungup
    * @throws TransferException if the caller has requested a transfer
    * @throws MainMenuException if the caller has requested a to be directed to the main menu
    * @throws MaxAttemptsExceededException if the caller has exceeded the maximum amount of attempts
    * @throws FailureTransferException if there is an unknown failure
    */ 
  public String getAmexCID(ActionStatusEvent actionIn, Locale locale) throws HungupException, TransferException, MainMenuException, MaxAttemptsExceededException, FailureTransferException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDCreditCardInteraction.getAmexCID: Entered method.");
    
    // check ase for null
    if (actionIn == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCreditCardInteraction.getAmexCID: ActionStatusEvent is null.");
      throw new FailureTransferException("TDDCreditCardInteraction.getAmexCID: ActionStatusEvent is null.");
    }
    
    // set data type
    dataType = DATA_TYPE_AMEX_CID;    
    // set the entry message
    entryMessage = amexCIDEntryMessage;
    // call get the data
    String cid = getKeyData(actionIn, locale, amexCIDMaxDigits, amexCIDMinDigits);
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDCreditCardInteraction.getAmexCID: Returning cid");
    return cid;
  }
  
  /**
    * This method speaks the passed cid to the caller and
    * prompts the caller for validation
    *
    * @param actionIn 
    * ActionStatusEvent containing the latest call information
    *
    * @param locale 
    * language to talk to the caller in
    *
    * @param cid 
    * String containing cid to confirm
    *
    * @return
    * indicating if the cid was confirmed
    *
    * @throws HungupException if the caller has hungup
    * @throws TransferException if the caller has requested a transfer
    * @throws MainMenuException if the caller has requested a to be directed to the main menu
    * @throws MaxAttemptsExceededException if the caller has exceeded the maximum amount of attempts
    * @throws FailureTransferException if there is an unknown failure
    */ 
  public boolean confirmCID(ActionStatusEvent actionIn, Locale locale, String cid) throws HungupException, TransferException, MainMenuException, MaxAttemptsExceededException, FailureTransferException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDCreditCardInteraction.confirmCID: Entered method.");
    
    // check ase for null
    if (actionIn == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCreditCardInteraction.confirmCID: ActionStatusEvent is null.");
      throw new FailureTransferException("TDDCreditCardInteraction.confirmCID: ActionStatusEvent is null.");
    }
    
    // check data for null
    if (cid == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCreditCardInteraction.confirmCID: CID is null.");
      throw new FailureTransferException("TDDCreditCardInteraction.confirmCID: CID is null.");
    }
    
    // set confirmation message
    confirmationMessage = cidConfirmationMessage;
    // confirm the data
    boolean confirm = confirmKeyData(actionIn, locale, cid);
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDCreditCardInteraction.confirmCID: Returning boolean");
    return confirm;
  }
  
  /**
    * This method speaks an hold segment to the caller.
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
  public void speakHoldMessage(ActionStatusEvent actionIn, Locale locale) throws HungupException, FailureTransferException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDCreditCardInteraction.speakHoldMessage: Entered method.");

    // check ase for null
    if (actionIn == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCreditCardInteraction.speakHoldMessage: ActionStatusEvent is null.");
      throw new FailureTransferException("TDDCreditCardInteraction.speakHoldMessage: ActionStatusEvent is null.");
    }
    
    interaction.playMedia(actionIn, holdMessage);
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDCreditCardInteraction.speakHoldMessage: Returning");
  }
  
  /**
    * This method calls ViolationsValidation.validateXXXXXXXXX based on what data type
    * is being validated.  If the data is valid this method returns true, otherwise it
    * returns false.
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
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDCreditCardInteraction.validateData: Entered method.");
    
    // check ase for null
    if (actionStatusEvent == null){
       if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCreditCardInteraction.validateData: ActionStatusEvent is null.");
       throw new FailureTransferException("TDDCreditCardInteraction.validateData: ActionStatusEvent is null.");
    }
    
    // check data for null
    if (data == null){
       if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCreditCardInteraction.validateData: Data is null.");
       throw new FailureTransferException("TDDCreditCardInteraction.validateData: Data is null.");
    }
    
    boolean validStatus = false;
    
    switch(dataType){
      // credit card
      case DATA_TYPE_CREDIT_CARD:
        // call validation method
        validStatus = ViolationsValidation.validateCreditCardNumber(data);
        break;
      case DATA_TYPE_ZIP_CODE:
        // call validation method
        validStatus = ViolationsValidation.validateZipCode(data);
        break;
      case DATA_TYPE_CID:
        // call validation method
        validStatus = ViolationsValidation.validateCID(data);
        break;
      case DATA_TYPE_AMEX_CID:
        // call validation method
        validStatus = ViolationsValidation.validateAmexCID(data);
        break;
      case DATA_TYPE_EXPIRATION_DATE:
        // call validation method
        validStatus = ViolationsValidation.validateExpirationDate(data);
        break;
      default:
        if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCreditCardInteraction.validateData: Invalid data type: " + dataType);
        break;
    }
    
    // if not valid tell user
    if(validStatus == false){
      interaction.speakInvalidInputMessage(actionStatusEvent, locale);
    }
   
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDCreditCardInteraction.validateData: Returning");
    return validStatus;
  
  }

  /**
    * This method attempts to retreive a value from a Properties object and throws
    * a ConfigurationException if the property is missing or if the value is invalid
    *
    * @param properties 
    * the Properties object to extract data
    *
    * @param property 
    * String key value for the value
    *
    * @param isRequired 
    * boolean indicating if value is required
    *
    *
    * @return 
    * the property value
    *
    * @throws ConfigurationException if there is a problem retrieving the data
    */
  private String getProperty(Properties properties, String property, boolean isRequired) throws ConfigurationException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDCreditCardInteraction.getProperty: Entering method");
    // get the value
    String value = properties.getProperty(property);
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDCreditCardInteraction.getProperty: Property: " + property + " Value: " + value);
    // if property is mandatory and data is null, throw an exception
    if(isRequired && value == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCreditCardInteraction.getProperty: Required Property: " + property + " missing from Properties");
      throw new MissingPropertyConfigurationException("TDDCreditCardInteraction.getProperty: Required Property: " + property + " missing from Properties");
    }
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDCreditCardInteraction.getProperty: Exiting method");
    return value;
  }// end getProperty
  
  
  /**
    * This method creates an EntryField bean with the entryMessage and uses the TDDFieldEntryInteraction
    * to query the caller for data.  Each time the data is received, it will be checked against
    * the configured transferKey (throw TransferException).  After it retrieves the data it will
    * call validateData to ensure the data is correct.  This method will attempt to get correct
    * data the configured amount of times.   
    *
    * @param actionStatusEvent
    *     ActionStatusEvent, event containing the latest call information
    *
    * @param locale
    *     Language for the application
    * 
    * @param maximumKeys 
    * max amount of keys the caller can enter
    * 
    * @param minimumKeys 
    * min amount of keys the caller can enter
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
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDCreditCardInteraction.getKeyData: Entered method.");
    
    // check parameters for null
    if(actionStatusEvent == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCreditCardInteraction.getKeyData: ActionStatusEvent passed to this method is null");
      throw new NullPointerException("TDDCreditCardInteraction.getKeyData: ActionStatusEvent passed to this method is null"); 
    }
      
    // create the voice segment to pass to getInput
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDCreditCardInteraction.getKeyData: Creating the segment to ask the caller.");
    
    String message = entryMessage + entryKeyMessage;
    
    // if cid, add extra segments
    if(dataType == DATA_TYPE_CID){
      message = message + cidCanBeFoundMessage;
    }
    if(dataType == DATA_TYPE_AMEX_CID){
      message = message + amexCIDCanBeFoundMessage;
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
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDCreditCardInteraction.getKeyData: Attempt: " + (attempts + 1));
      // get the data
      try{
        keyedData = interaction.getInput(actionStatusEvent, message, maximumKeys, minimumKeys);
      }
      catch(EntryTimeoutException ete){
        if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDCreditCardInteraction.getKeyData: Caller timed out");
      }
      
      // if keyed data is not equal to null, we got data from the user
      if(keyedData != null){
        // check for transfer request
        if(keyedData.equals(transferKey)){
          if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDCreditCardInteraction.getKeyData: Caller pressed " + transferKey + ", throwing TransferException");
          throw new TransferException("TDDCreditCardInteraction.getKeyData: Caller requested transfer");
        }
        
        // check if data is valid
        boolean dataValidated = validateData(actionStatusEvent, locale, keyedData);
        if(dataValidated){
          invalidData = false;
          if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDCreditCardInteraction.getKeyData: Data is valid " + keyedData);
        }
        else{
          // log bad data
          if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDCreditCardInteraction.getKeyData: Data is invalid " + keyedData);
        }
      }
      // increment the attempts
      attempts++;
    }
    
    // if data is invalid now, throw max attempts exception
    if(invalidData){
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDCreditCardInteraction.getKeyData: Max attempts have been exceeded, throwing MaxAttemptsExceededException");
      throw new MaxAttemptsExceededException("TDDCreditCardInteraction.getKeyData: Max attempts have been exceeded, throwing MaxAttemptsExceededException");
    }
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDCreditCardInteraction.getKeyData: Leaving method.");    
    return keyedData;
  }//end getKeyData

@Override
protected boolean validateData(ActionStatusEvent arg0, Locale arg1,
		String arg2, boolean arg3) throws HungupException,
		FailureTransferException {
	// TODO Auto-generated method stub
	return false;
}
  

}//end TDDCreditCardInteraction class