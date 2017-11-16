package com.viecore.pvs;


import com.viecore.pvs.business.*;
import com.viecore.pvs.tdd.TDDMenu;
import com.viecore.pvs.*;
import com.viecore.util.log.*;
import com.viecore.util.configuration.*;

import java.util.*;
import java.text.*;

import com.ibm.telephony.directtalk.*;
import com.ibm.telephony.beans.directtalk.*;
import com.ibm.telephony.beans.*;
import com.ibm.telephony.beans.media.*;

/**
 *  This class is a menu that contains the functionality to pull violation data from the session
 *  and display different segments to the caller based on status.
 *
 *
 *  @author Thomas Ryan
 *  @since jdk 1.3 
 *  @version 1.0 1/15/2003
 *
 *
 */


public class TDDViolationStatusInquiryMenu extends TDDMenu{
  
/*---------------------
  Attributes
----------------------*/
  
 /**
  * Constant used to retrieve no payment received segment from Properties.
  */
  public static final String CONFIG_NO_PAYMENT_RECEIVED = "NoPaymentReceivedSegment";
  
 /**
  * Constant used to retrieve total amound owed segment from Properties
  */
  
  public static final String CONFIG_TOTAL_AMOUNT_OWED = "TotalAmountOwedSegment";
  
 /**
  * Constant used to retrieve balance possibly not reflected segment from Properties
  */ 
  
  public static final String CONFIG_BALANCE_MAY_NOT_REFLECT = "BalanceMayNotReflectSegment";
  
 /**
  * Constant used to retrieve paid in full segment from Properties
  */
  
  public static final String CONFIG_PAID_IN_FULL = "PaidInFullSegment";
  
 /**
  * Constant used to retrieve no further action segment from Properties.
  */
  
  public static final String CONFIG_NO_FURTHER_ACTION = "NoFurtherActionSegment";
  
 /**
  * Constant used to retrieve final determination segment from Properties 
  */
  
  public static final String CONFIG_FINAL_DETERMINATION = "FinalDeterminationSegment";
  
 /** 
  * Constant used to retrieve has been incurred segment from Properties
  */
  
  public static final String CONFIG_HAS_BEEN_INCURRED = "HasBeenIncurredSegment";
  
 /**
  * Constant used to retrieve too late to contest segment from Properties
  */
  
  public static final String CONFIG_TOO_LATE_TO_CONTEST = "TooLateToContestSegment";
  
 /**
  * Constant used to retrieve hearing officer not Liable segment from Properties
  */
  
  public static final String CONFIG_HEARING_OFFICER_NOT_LIABLE = "HearingOfficerNotLiableSegment";
  
 /**
  * Constant used to retrieve partially paid segment from Properties
  */
  
  public static final String CONFIG_PARTIALLY_PAID = "PartiallyPaidSegment";
  
 /**
  * Constant used to retrieve violation entry segment from Properties
  */
  
  public static final String CONFIG_AVOID_PENALTY_AND_ACTION = "AvoidPenaltyAndActionSegment";
  
 /**
  * Constant used to retrieve late penalty segment from Properties
  */
  
  public static final String CONFIG_LATE_PENALTY = "LatePenaltySegment";
  
 /** 
  * Constant used to retrieve avoid action segment from Properties
  */
  
  public static final String CONFIG_AVOID_ACTION = "AvoidActionSegment";
  
 /**
  * Constant used to retrieve violation invalid segment from Properties
  */
  
  public static final String CONFIG_CONTESTED_BY_MAIL = "ContestedByMailSegment";
  
 /**
  * Constant used to retrieve hearing scheduled segment from Properties
  */
  
  public static final String CONFIG_HEARING_SCHEDULED = "HearingScheduledSegment";
  
 /** 
  * Constant used to retrieve notification mailed segment from Properties
  */
  
  public static final String CONFIG_NOTIFICATION_MAILED = "NotificationMailedSegment";
  
 /**
  * Constant used to retrieve owner may appear segment from Properties
  */
  
  public static final String CONFIG_OWNER_MAY_APPEAR = "OwnerMayAppearSegment";
    
 /** 
  * Constant used to retrieve "and" segment from Properties
  */  
  
  public static final String CONFIG_AND = "AndSegment";
  
 /**
  * Constant used to retrieve Monday through Friday Segment from Properties
  */
  
  public static final String CONFIG_MONDAY_THROUGH_FRIDAY = "MondayThroughFridaySegment";
  
 /**
  * Constant used to retrieve hearing officer liable segment from Properties
  */
  
  public static final String CONFIG_HEARING_OFFICER_LIABLE = "HearingOfficerLiableSegment";
  
 /**
  * Constant used to retrieve failure to appear segment from Properties
  */
  
  public static final String CONFIG_FAILURE_TO_APPEAR = "FailureToAppearSegment";
  
 /**
  * Constant used to retrieve payment withing 21 days segment from Properties
  */
  
  public static final String CONFIG_PAYMENT_WITHIN_21 = "PaymentWithin21DaysSegment";
  
 /**
  * Contant used to retrieve failure to pay segment from Properties
  */
  
  public static final String CONFIG_FAILURE_TO_PAY = "FailureToPaySegment";
  
 /**
  * Constant used to retrieve violation object from session
  */
  
  public static final String SESSION_VIOLATION = "Violation";
    
 /**
  * No payment received message
  */ 
  
  private String noPaymentReceived = null;
  
 /**
  * Total amount due message
  */ 
  
  private String totalAmountOwed = null;
  
  /**
  * Balance may not reflect message
  */ 
  
  private String balanceMayNotReflect = null;
  
 /**
  * Paid in full message
  */ 
  
  private String paidInFull = null;
  
 /**
  * No further action required message
  */ 
  
  private String noFurtherAction = null;
  
 /**
  * Final determination message
  */ 
  
  private String finalDetermination = null;
  
 /**
  * Late penalty message
  */ 
  
  private String latePenalty = null;
  
 /**
  * Has been incurred message
  */ 
  
  private String hasBeenIncurred = null;
  
 /**
  * Too late to contest message
  */ 
  
  private String tooLateToContest = null;
  
 /**
  * Hearing officer not liable message
  */ 
  
  private String hearingOfficerNotLiable = null;
  
 /**
  * Partially paid message
  */ 
  
  private String partiallyPaid = null;
 
 /**
  * Avoid penalty and action message
  */ 
  
  private String avoidPenaltyAndAction = null;
     
 /**
  * Avoid action message
  */ 
  
  private String avoidActionSegment = null;
  
 /**
  * Contested by mail message
  */ 
  
  private String contestedByMail = null;
  
 /**
  * Hearing scheduled message
  */ 
  
  private String hearingScheduled = null;
  
 /**
  * Notification mailed message
  */ 
  
  private String notificationMailed = null;
  
 /**
  * Owner may appear message
  */ 
  
  private String ownerMayAppear = null;
  
 /**
  * And message
  */ 
  
  private String andSegment = null;
  
 /**
  * Monday thru Friday message
  */ 
  
  private String mondayThroughFriday = null;
  
 /**
  * Hearing officer liable message
  */ 
  
  private String hearingOfficerLiable = null;
  
 /**
  * Failure to appear message
  */ 
  
  private String failureToAppear = null;
  
 /**
  * Payment within 21 days message
  */ 
  
  private String paymentWithin21Days = null;
  
 /**
  * Failure to pay message
  */ 
  
  private String failureToPay = null;
  

/*-------------------------
  Methods
---------------------------*/
 
 /**
  * This method calls setMenuOptions on its parent and then extracts the configuration
  * from the Properties object.
  *
  * @param properties
  * Must contain the following properties, defined as instance variables for the class 
  *  <pre>       
  *       CONFIG_NO_PAYMENT_RECEIVED 
  *       CONFIG_TOTAL_AMOUNT_OWED
  *       CONFIG_BALANCE_MAY_NOT_REFLECT
  *       CONFIG_PAID_IN_FULL
  *       CONFIG_NO_FURTHER_ACTION
  *       CONFIG_FINAL_DETERMINATION
  *       CONFIG_LATE_PENALTY
  *       CONFIG_HAS_BEEN_INCURRED
  *       CONFIG_TOO_LATE_TO_CONTEST
  *       CONFIG_HEARING_OFFICER_NOT_LIABLE
  *       CONFIG_PARTIALLY_PAID
  *       CONFIG_AVOID_PENALTY_AND_ACTION
  *       CONFIG_AVOID_ACTION
  *       CONFIG_CONTESTED_BY_MAIL
  *       CONFIG_HEARING_SCHEDULED
  *       CONFIG_NOTIFICATION_SCHEDULED
  *       CONFIG_OWNER_MAY_APPEAR
  *       CONFIG_AND_SEGMENT
  *       CONFIG_MONDAY_THROUGH_FRIDAY
  *       CONFIG_HEARING_OFFICER_LIABLE
  *       CONFIG_FAILURE_TO_APPEAR
  *       CONFIG_PAYMENT_WITHIN_21
  *       CONFIG_FAILURE_TO_PAY
  *</pre>
  *
  * @throws ConfigurationException Thrown if one of the properties is missing
  */ 
  
  
  public void setMenuOptions(Properties properties)throws ConfigurationException{
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDViolationStatusInquiryMenu.setMenuOptions: Entered method.");
    
    super.setMenuOptions(properties);
    
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDViolationStatusInquiryMenu.setMenuOptions: Back from super.setMenuOptions.");
    
    
    noPaymentReceived = validateStringProperty(properties, CONFIG_NO_PAYMENT_RECEIVED, true);   
    //Get totalAmountOwed and check for null 
    totalAmountOwed = validateStringProperty(properties, CONFIG_TOTAL_AMOUNT_OWED, true);   
    //Get balanceMayNotReflect and check for null 
    balanceMayNotReflect = validateStringProperty(properties, CONFIG_BALANCE_MAY_NOT_REFLECT, true);   
    //Get paidInFull and check for null 
    paidInFull = validateStringProperty(properties, CONFIG_PAID_IN_FULL, true);    
    //Get noFurtherAction and check for null 
    noFurtherAction= validateStringProperty(properties, CONFIG_NO_FURTHER_ACTION, true);        
    //Get finalDetermination and check for null 
    finalDetermination = validateStringProperty(properties, CONFIG_FINAL_DETERMINATION, true);           
    //Get latePenalty and check for null 
    latePenalty = validateStringProperty(properties, CONFIG_LATE_PENALTY, true);    
    //Get hasBeenIncurred and check for null 
    hasBeenIncurred = validateStringProperty(properties, CONFIG_HAS_BEEN_INCURRED, true);    
    //Get tooLateToContest and check for null 
    tooLateToContest = validateStringProperty(properties, CONFIG_TOO_LATE_TO_CONTEST, true);    
    //Get hearingOfficerNotLiable and check for null 
    hearingOfficerNotLiable = validateStringProperty(properties, CONFIG_HEARING_OFFICER_NOT_LIABLE, true);    
    //Get partiallyPaid and check for null 
    partiallyPaid = validateStringProperty(properties, CONFIG_PARTIALLY_PAID, true);    
    //Get avoidPenaltyAndAction and check for null 
    avoidPenaltyAndAction = validateStringProperty(properties, CONFIG_AVOID_PENALTY_AND_ACTION, true);
    //Get avoidActionSegment and check for null 
    avoidActionSegment = validateStringProperty(properties, CONFIG_AVOID_ACTION, true);   
    //Get contestedByMail and check for null 
    contestedByMail = validateStringProperty(properties, CONFIG_CONTESTED_BY_MAIL, true);    
    //Get hearingScheduled and check for null 
    hearingScheduled = validateStringProperty(properties, CONFIG_HEARING_SCHEDULED, true);   
    //Get notificationMailed and check for null 
    notificationMailed = validateStringProperty(properties, CONFIG_NOTIFICATION_MAILED, true);    
    //Get ownerMayAppear and check for null 
    ownerMayAppear = validateStringProperty(properties, CONFIG_OWNER_MAY_APPEAR, true);    
    //Get andSegment and check for null 
    andSegment = validateStringProperty(properties, CONFIG_AND, true);    
    //Get mondayThroughFriday and check for null 
    mondayThroughFriday = validateStringProperty(properties, CONFIG_MONDAY_THROUGH_FRIDAY, true);   
    //Get hearingOfficerLiable and check for null 
    hearingOfficerLiable = validateStringProperty(properties, CONFIG_HEARING_OFFICER_LIABLE, true);    
    //Get failureToAppear and check for null 
    failureToAppear = validateStringProperty(properties, CONFIG_FAILURE_TO_APPEAR, true);   
    //Get paymentWithin21Days and check for null 
    paymentWithin21Days = validateStringProperty(properties, CONFIG_PAYMENT_WITHIN_21, true);    
    //Get failureToPay and check for null 
    failureToPay = validateStringProperty(properties, CONFIG_FAILURE_TO_PAY, true);   
    
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDViolationStatusInquiryMenu.setMenuOptions: Exiting method.");
    
  }//end setMenuOptions
  
 /**
  * This method retrieves a violation object from the session and checks the overall status
  * to see the appropriate segments to play.  A String is created from the segments and is
  * used to set the header message.   
  *
  * @param session
  * Hashtable containing the violation object
  *
  * @param locale 
  * Locale that contains the language for the caller
  * 
  * @return 
  * message to play to the caller
  *
  * @throws TransferException if there is a problem forming the message to play
  *
  */
  
  protected String setCallDetails(Hashtable session, Locale locale) throws TransferException{
   if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDViolationStatusInquiryMenu.setCallDetails: Entered method.");
   
    //create a String for the message
    String message = ""; 
    
    //Violation object from the session
    Violation violation = null;
    
    //status of the violation
    int status = -1;
        
    Object violationObject = session.get(this.SESSION_VIOLATION);
    if(violationObject == null){
      if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "TDDViolationStatusInquiryMenu.setCallDetails: Violation object returned from session is null");
      throw new TransferException("TDDViolationStatusInquiryMenu.setCallDetails: Violation object returned from session is null");
    }
    if(violationObject instanceof Violation == false){
      if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "TDDViolationStatusInquiryMenu.setCallDetails: Object returned from session is not a Violation");
      throw new TransferException("TDDViolationStatusInquiryMenu.setCallDetails: Object returned from session is not a Violation");
    }
    violation = (Violation)violationObject;
      
    //get the status of the violation
    status = violation.getOverallStatus();
    
    if(logLevel >= LoggerIF.NOTICE) log(LoggerIF.NOTICE, "TDDViolationStatusInquiryMenu.setCallDetails: About to perform switch with status = ["+status+"].");
    
    
    //Create a MediaSequence object based on the status of the violation    
    switch (status){
      
      case Violation.OVERALL_STATUS_PAID_IN_FULL:
        message = paidInFull + noFurtherAction;
        break;
        
      case Violation.OVERALL_STATUS_NOT_LIABLE:
        message = hearingOfficerNotLiable + noFurtherAction;
        break;
        
      case Violation.OVERALL_STATUS_FAILURE_TO_APPEAR:
        double currentAmountDue = violation.getCurrentAmountDue();
        message = failureToAppear + totalAmountOwed + currentAmountDue + paymentWithin21Days;
        break;
      
      case Violation.OVERALL_STATUS_FOUND_LIABLE:
        double liableCurrentAmountDue = violation.getCurrentAmountDue();
        message = hearingOfficerLiable + totalAmountOwed + liableCurrentAmountDue + paymentWithin21Days;
        break;
        
      case Violation.OVERALL_STATUS_CONTEST_BY_MAIL:
        message = contestedByMail;
        break;
      
      case Violation.OVERALL_STATUS_IN_PERSON_HEARING:
        message = hearingScheduled + notificationMailed;
        break;
        
      case Violation.OVERALL_STATUS_IN_PERSON_HEARING_SCHEDULED:    
        Calendar startDate = violation.getHearingStartDate();
        Calendar endDate = violation.getHearingEndDate();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        message = hearingScheduled + ownerMayAppear + format.format(startDate) + andSegment + format.format(endDate) + mondayThroughFriday;
        break;
      
      case Violation.OVERALL_STATUS_NO_PAYMENT_RECEIVED:
        double noPaymentCurrentAmountDue = violation.getCurrentAmountDue();
        message = noPaymentReceived + totalAmountOwed + noPaymentCurrentAmountDue + balanceMayNotReflect;
        break;
        
      case Violation.OVERALL_STATUS_FAILURE_TO_PAY:
        double failureCurrentAmountDue = violation.getCurrentAmountDue();
        message = failureToPay + totalAmountOwed + failureCurrentAmountDue + paymentWithin21Days;
        break;
      
      case Violation.OVERALL_STATUS_FINAL_DETERMINATION:
        double determinationFineAmount = violation.getFineAmount();
        double amountOwed = violation.getCurrentAmountDue();
        double penaltyOwed = violation.getPenaltyAmount();      
        message = finalDetermination + determinationFineAmount + latePenalty + penaltyOwed + hasBeenIncurred + totalAmountOwed + amountOwed + tooLateToContest;
        break;
      
      case Violation.OVERALL_STATUS_PARTIAL_PAID_LATE:
        double partialLatePenaltyAmount = violation.getPenaltyAmount();
        double partialLateAmountOwed = violation.getCurrentAmountDue();
        message = latePenalty + partialLatePenaltyAmount + hasBeenIncurred + totalAmountOwed + partialLateAmountOwed + avoidActionSegment;
        break;
      
      case Violation.OVERALL_STATUS_PARTIAL_ONE:
      case Violation.OVERALL_STATUS_PARTIAL_TWO:
        double partialFineAmount = violation.getFineAmount();
        double partialAmountOwed = violation.getCurrentAmountDue();
        message = partiallyPaid + partialFineAmount + totalAmountOwed + partialAmountOwed+ avoidActionSegment;
        break;
          
    }//end switch

   if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDViolationStatusInquiryMenu.setCallDetails: Final message: [" + message + "]");
   
   if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDViolationStatusInquiryMenu.setCallDetails: Exiting method.");
   
   return message;
     
  }//end setCallDetails

}//end class























