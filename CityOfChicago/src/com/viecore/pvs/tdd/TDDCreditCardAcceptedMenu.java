/*
 * Licensed Materials - Property of Viecore Inc.
 *
 * (C) Copyright Viecore Inc. 2002, 2003 All Rights Reserved
 */

package com.viecore.pvs.tdd;

import com.viecore.pvs.*;
import com.viecore.util.log.*;

import com.ibm.telephony.directtalk.*;
import com.ibm.telephony.beans.media.*;

import java.util.*;
import com.viecore.util.configuration.*;

/**
 * This class is a menu that contains functionality to pull additional configuration 
 * data from the menu properties and display the configurable credit card accepted
 * message and display the confirmation number for the credit card charge.
 *
 *  @author Michael Ruggiero
 *  @since jdk 1.3 
 *  @version 1.0 1/27/2003
 */


public class TDDCreditCardAcceptedMenu extends TDDMenu{
  
  
  /**
    * Constant to retrieve credit card transaction accepted segment from Properties
    */
  public static final String CONFIG_TRANSACTION_ACCEPTED_SEGMENT = "CCTransactionAccepted";
    
  /**
    * Constant to retrieve thank you segment from Properties
    */
  public static final String CONFIG_THANK_YOU_SEGMENT = "PaymentThankYou";
    
  /**
    * Constant to retrieve confirmation number from Properties
    */
  public static final String SESSION_CONFIRMATION_NUMBER = "CCTransactionConfirmationNumber";
  
  /**
    * Segment to speak transaction accepted audio
    */
  private String transactionAcceptedSegment = null;
  
  /**
    * Segment to speak thank you audio
    */
  private String thankYouSegment = null;
  
  
  /**
    * This method calls setMenuOptions on its parent and then extracts the configuration
    * from the Properties object.
    *
    * @param properties
    * Must contain the following properties, defined as instance variables for the class 
    * <pre>              
    *       CONFIG_TRANSACTION_ACCEPTED_SEGMENT
    *       CONFIG_THANK_YOU_SEGMENT
    * </pre>
    * @throws ConfigurationException 
    *     Thrown if one of the properties is missing
    */ 
  public void setMenuOptions(Properties properties)throws ConfigurationException{
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDCreditCardAcceptedMenu.setMenuOptions: Entered method.");
    //null check on incoming Properties object 
    if(properties == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCreditCardAcceptedMenu.setMenuOptions: Properties object passed in is null.");
      throw new ConfigurationException("TDDCreditCardAcceptedMenu.setMenuOptions: Properties object passed in is null.");
    }  
    
    //call setMenuOptions on parent TDDMenu
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDCreditCardAcceptedMenu.setMenuOptions: Calling TDDMenu's setMenuOptions method.");
    super.setMenuOptions(properties);
    
    //extract the configuration from Properties object
    transactionAcceptedSegment = validateStringProperty(properties, CONFIG_TRANSACTION_ACCEPTED_SEGMENT, true);
    thankYouSegment = validateStringProperty(properties, CONFIG_THANK_YOU_SEGMENT, true);
    
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDCreditCardAcceptedMenu.setMenuOptions: Leaving method.");
  }//end setMenuOptions
  
   
  
  
  /**
    * This method uses the configured segments and values to construct the 
    * header message to play .  
    *
    * @param session
    *       Hashtable containing the violation object
    * @param locale 
    *       Locale containing language for caller
    * @return 
    *       header message String
    * @throws TransferException if ther header message cannot be built
    */ 
  public String setCallDetails(Hashtable session, Locale locale) throws TransferException{   
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDCreditCardAcceptedMenu.setCallDetails: Entered method.");
    //declare method level variables
    String confirmationNumber = null;
    
    //create a String for header message 
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDCreditCardAcceptedMenu.setCallDetails: Creating a string for the header message");
    String headerMessage = "";
    
    //null check for session
    if(session == null){
      if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "TDDCreditCardAcceptedMenu.setCallDetails: HashTable object passed into this method is null.");
      throw new TransferException("TDDCreditCardAcceptedMenu.setCallDetails: HashTable object passed into this method is null.");
    }
    
    //get confirmation number from session
    Object confObject = session.remove(SESSION_CONFIRMATION_NUMBER);
    if(confObject == null){
      if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "TDDCreditCardAcceptedMenu.setCallDetails: Confirmation number for the credit card transaction is not in session.");
      throw new TransferException("TDDCreditCardAcceptedMenu.setCallDetails: Confirmation number for the credit card transaction is not in session.");
    }
    
    if(confObject instanceof String == false){
      if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "TDDCreditCardAcceptedMenu.setCallDetails: Confirmation number for the credit card transaction is not a String.");
      throw new TransferException("TDDCreditCardAcceptedMenu.setCallDetails: Confirmation number for the credit card transaction is not a String.");
    } 
    
    confirmationNumber = (String) confObject;
        
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDCreditCardAcceptedMenu.setCallDetails: Confirmation number for credit card transaction is : ["+confirmationNumber+"]");
    
    //create transactionAcceptedSegment message
    headerMessage = transactionAcceptedSegment + confirmationNumber + thankYouSegment;
    
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDCreditCardAcceptedMenu.setCallDetails: Header message [" + headerMessage + "]");
    
    //return the header string
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDCreditCardAcceptedMenu.setCallDetails: Returning the header string and leaving method.");
    return headerMessage;
  }//end setCallDetails

}//end class TDDCreditCardAcceptedMenu























