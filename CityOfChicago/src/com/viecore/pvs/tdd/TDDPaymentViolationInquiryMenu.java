/*
 * Licensed Materials - Property of Viecore Inc.
 *
 * (C) Copyright Viecore Inc. 2002, 2003 All Rights Reserved
 */

package com.viecore.pvs.tdd;



import com.viecore.pvs.*;
import com.viecore.pvs.business.*;
import com.viecore.util.log.*;
import com.viecore.util.configuration.*;
import com.ibm.telephony.directtalk.*;
import com.ibm.telephony.beans.media.*;
import java.util.*;

/**
 * This class is a menu that contains functionality to pull violation 
 * data from the session and speak different segments based on status.
 *
 *  @author Michael Ruggiero
 *  @since jdk 1.3 
 *  @version 1.0 1/22/2003
 */


public class TDDPaymentViolationInquiryMenu extends TDDMenu{
  
  
  /**
    * Constant to retrieve total amount due message from Properties
    */
  public static final String CONFIG_TOTAL_AMOUNT_DUE_SEGMENT = "TotalAmountDueSegment";
    
  /**
    * Constant to paid in full message from Properties
    */
  public static final String CONFIG_PAID_IN_FULL_SEGMENT = "PaidInFullSegment";
  
  /**
    * Constant to retrieve no further action from Properties
    */
  public static final String CONFIG_NO_FURTHER_ACTION = "NoFurtherActionSegment";
  
  /**
    * Constant to retrieve not liable message from Properties
    */
  public static final String CONFIG_NOT_LIABLE_SEGMENT = "NotLiableSegment";
  
  /**
    * Constant to retrieve not processed message from Properties
    */
  public static final String CONFIG_NOT_PROCESSED_SEGMENT = "NotProcessedSegment";
  
  /**
    * Constant to retrieve not found message from Properties
    */
  public static final String CONFIG_NOT_FOUND_SEGMENT = "NotFoundSegment";
  
  /**
    * Constant to retrieve Violation object from Properties
    */
  public static final String SESSION_VIOLATION = "Violation";
  
  /**
    * Segment to play total amount due message
    */
  private String totalAmountDue = null;
  
  /**
    * Segment to play paid in full message
    */
  private String paidInFull = null;
  
  /**
    * Segment to play no further action message
    */
  private String noFurtherAction = null;
  
  /**
    * Segment to play not liable message
    */
  private String notLiable = null;
  
  /**
    * Segment to play not processed message
    */
  private String notProcessed = null;
  
  /**
    * Segment to play not found message
    */
  private String notFound = null;
  

  
  /**
    * This method calls setMenuOptions on its parent and then extracts the configuration
    * from the Properties object.
    *
    * @param Properties properties: Must contain the following properties, defined as
    *        instance variables for the class   
    *       
    *       CONFIG_TOTAL_AMOUNT_DUE_SEGMENT
    *       CONFIG_PAID_IN_FULL_SEGMENT
    *       CONFIG_NO_FURTHER_ACTION
    *       CONFIG_NOT_LIABLE_SEGMENT
    *       CONFIG_NOT_PROCESSED_SEGMENT
    *       CONFIG_NOT_FOUND_SEGMENT
    *
    * @throws ConfigurationException 
    *     Thrown if one of the properties is missing
    */ 
  public void setMenuOptions(Properties properties)throws ConfigurationException{
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDPaymentViolationInquiryMenu.setMenuOptions: Entered method.");
    //null check on incoming Properties object 
    if(properties == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDPaymentViolationInquiryMenu.setMenuOptions: Properties object passed in is null.");
      throw new ConfigurationException("TDDPaymentViolationInquiryMenu.setMenuOptions: Properties object passed in is null.");
    }  
    
    //call setMenuOptions on parent TDDMenu
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDPaymentViolationInquiryMenu.setMenuOptions: Calling TDDMenu's setMenuOptions method.");
    super.setMenuOptions(properties);
    
    //extract the configuration from Properties object
    totalAmountDue = validateStringProperty(properties, CONFIG_TOTAL_AMOUNT_DUE_SEGMENT, true);
    paidInFull = validateStringProperty(properties, CONFIG_PAID_IN_FULL_SEGMENT, true);
    noFurtherAction = validateStringProperty(properties, CONFIG_NO_FURTHER_ACTION, true);
    notLiable = validateStringProperty(properties, CONFIG_NOT_LIABLE_SEGMENT, true);
    notProcessed = validateStringProperty(properties, CONFIG_NOT_PROCESSED_SEGMENT, true);
    notFound = validateStringProperty(properties, CONFIG_NOT_FOUND_SEGMENT, true);
    
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDPaymentViolationInquiryMenu.setMenuOptions: Exiting method.");
  }//end setMenuOptions
  
   
  
  
  /**
    * This method uses the configured segments and the Violation object 
    * from the session to form the String to play.  
    *
    * @param session
    *       Hashtable containing the violation object
    * @param locale 
    *       Locale containing language for caller
    * @return String 
    *       header message
    *
    * @throws TransferException if the header message cannot be built
    */ 
  public String setCallDetails(Hashtable session, Locale locale) throws TransferException{   
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDPaymentViolationInquiryMenu.setCallDetails: Entered method.");
    //declare method scope variables
    String paymentStatus = null;
    String ticketStatus = null;
    String noticeLevel = null;
    
    //create a header message 
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDPaymentViolationInquiryMenu.setCallDetails: Creating the header String.");
    String message = "";
    
    //null check for session
    if(session == null){
      if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "TDDPaymentViolationInquiryMenu.setCallDetails: HashTable object passed into this method is null.");
      throw new TransferException("TDDPaymentViolationInquiryMenu.setCallDetails: HashTable object passed into this method is null.");
    }
    
    // get violation from session and check for correct class
    Object violationObject = session.get(SESSION_VIOLATION);
    if(violationObject == null){
      if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "TDDPaymentViolationInquiryMenu.setCallDetails: Violation object retreived from session is null.");
      throw new TransferException("TDDPaymentViolationInquiryMenu.setCallDetails: Violation object retreived from session is null.");
    }
    if(violationObject instanceof Violation == false){
      if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "TDDPaymentViolationInquiryMenu.setCallDetails: Object retreived from session is not a Violation.");
      throw new TransferException("TDDPaymentViolationInquiryMenu.setCallDetails: Object retreived from session is not a Violation.");
    }
    
    Violation violation = (Violation) violationObject;

    //get payment status
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDPaymentViolationInquiryMenu.setCallDetails: Getting payment status.");
    paymentStatus = violation.getPaymentStatus();
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDPaymentViolationInquiryMenu.setCallDetails: Payment status is : ["+paymentStatus+"]");
    
    //get ticket status
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDPaymentViolationInquiryMenu.setCallDetails: Getting ticket status.");
    ticketStatus = violation.getTicketStatus();
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDPaymentViolationInquiryMenu.setCallDetails: Ticket status is : ["+ticketStatus+"]");
    
    //get notice level
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDPaymentViolationInquiryMenu.setCallDetails: Getting notice level.");
    noticeLevel = violation.getNoticeLevel();
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDPaymentViolationInquiryMenu.setCallDetails: Notice level is : ["+noticeLevel+"]");
    
    // if payment is partial or none, want to display the total amount due
    if((violation.PAYMENT_STATUS_NONE.equals(paymentStatus)) || 
        (violation.PAYMENT_STATUS_PARTIAL.equals(paymentStatus))){
      if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDPaymentViolationInquiryMenu.setCallDetails: Payment status is none or partial, setting total amount due segment.");
      //get total amount due
      double amount = violation.getCurrentAmountDue();
      if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDPaymentViolationInquiryMenu.setCallDetails: Total amount due is ["+totalAmountDue+"]");
      //create total amount due message
      message = totalAmountDue + amount;
    }
    // if payment is paid, want to display paid in full disclaimer and no further action
    else if(violation.VIOLATION_STATUS_PAID.equals(ticketStatus)){
      if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDPaymentViolationInquiryMenu.setCallDetails: Ticket status is paid, setting paid in full message.");
      message = paidInFull + noFurtherAction;
    }
    // if dismissed, want not liable and no further action
    else if(violation.VIOLATION_STATUS_DISMISSED.equals(ticketStatus)){
      if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDPaymentViolationInquiryMenu.setCallDetails: Ticket status is dismissed, setting not liable message.");
      message = notLiable + noFurtherAction;
    }
    // if notice, check notice level to see if it was not processed
    else if((violation.VIOLATION_STATUS_NOTICE.equals(ticketStatus)) &&
            ((violation.NOTICE_LEVEL_SEIZE.equals(noticeLevel)) || (violation.NOTICE_LEVEL_DLS.equals(noticeLevel)))){
      if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDPaymentViolationInquiryMenu.setCallDetails: Ticket status is notice and notice level is seize or dls, setting not processed message");
      //create not processed message
      message = notProcessed;
    }
    else {
      if(logLevel >= LoggerIF.WARNING) log(LoggerIF.WARNING, "TDDPaymentViolationInquiryMenu.setCallDetails: No applicable data found in Violation object.");
      //set violation not found segment
      message = notFound;
    }
      
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDPaymentViolationInquiryMenu.setCallDetails: Message built [" + message + "]");
    
    //return the String
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDPaymentViolationInquiryMenu.setCallDetails: Returning the String and leaving method.");
    return message;
  }//end setCallDetails

}//end class























