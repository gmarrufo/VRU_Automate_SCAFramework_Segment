package com.viecore.pvs.tdd;

/*
 * Licensed Materials - Property of Viecore Inc.
 *
 * (C) Copyright Viecore Inc. 2002, 2003 All Rights Reserved
 */

import java.util.*;
import java.io.*;
import java.text.*;

import com.ibm.telephony.directtalk.*;
import com.ibm.telephony.beans.directtalk.*;
import com.ibm.telephony.beans.*;
import com.ibm.telephony.beans.media.*;


import com.viecore.util.log.*;
import com.viecore.util.configuration.*;

import com.viecore.pvs.business.*;
import com.viecore.pvs.*;




/**
  * This class defines methods used to speak TDD messages to the caller during the transfer process.  
  * 
  * @author Michael Ruggiero
  * @since JDK 1.3
  * @version 1.0  1/21/2003
 */ 
public class TDDTransferInteraction extends AbstractTransferInteraction {  
                                                     
  /**                                                    
    * Reference to the TDDInteraction object used to speak messages to the caller
    */
  private TDDInteraction interaction  = null; 

  /**
    * This method creates and configures the TDDInteraction object and calls the parent initConfiguration.
    * 
    * @param applicationProperties 
    * Properties object containing the configuration for the object
    * 
    * @throws ConfigurationException - 
    * Thrown if the Properties object is missing values or contains invalid entries.
    */
  public void initConfiguration(Properties applicationProperties) throws ConfigurationException{
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, " TDDTransferInteraction.initConfiguration: Entered method."); 
    if (applicationProperties == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "TDDTransferInteraction.initConfiguration: applicationsProperties is null");        
      throw new ConfigurationException("TDDTransferInteraction.initConfiguration: applicationsProperties is null"); 
    }  
       
    super.initConfiguration(applicationProperties);
    
    interaction = new TDDInteraction();
    interaction.initLoggerIF(logger);
    interaction.initConfiguration(applicationProperties);

    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, " TDDTransferInteraction.initConfiguration: Exited method."); 
  }//end method
  
  
  
  
  /**
    * This method speaks the configured call being transferred segment and uses
    * the TDDInteraction to speak it to the caller.
    *
    * @param actionStatusEvent 
    * ActionStatusEvent containing the latest call information 
    *
    * @param locale     
    * Locale containing language for caller  
    *
    * @throws HungupException
    * if the caller has hungup.
    * @throws FailureTransferException if there is an unknown failure
    */
  public void speakCallBeingTransferred(ActionStatusEvent actionStatusEvent, Locale locale ) throws HungupException, FailureTransferException{
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, " TDDTransferInteraction.speakCallBeingTransferred: Entered method ");       
    interaction.playMedia(actionStatusEvent, duringHoursSegment);
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, " TDDTransferInteraction.speakCallBeingTransferred: Exited method.");
  }
 
  /**
    * This speaks the passed business transfer segment using the TDDInteraction
    * to speak to the data to the caller.
    *
    * @param actionStatusEvent 
    * ActionStatusEvent containing the latest call information 
    *
    * @param locale   
    * Locale containing language for caller  
    *
    * @param transferSegment   
    * String containing the session transfer message
    *
    * @throws HungupException
    * if the caller has hungup.
    * @throws FailureTransferException if there is an unknown failure 
    */
  public void speakBusinessMessage(ActionStatusEvent actionStatusEvent,Locale locale, String transferSegment ) throws HungupException, FailureTransferException{
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, " TDDTransferInteraction.speakBusinessMessage: Entered method.");
    interaction.playMedia(actionStatusEvent, transferSegment);
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, " TDDTransferInteraction.speakBusinessMessage: Exited method.");
  }
 
 
 

 
  /**
    * This method constructs a message with the configured after hours transfer 
    * segments and uses the TDDInteraction object to speak the data to the caller.
    *
    * @param actionStatusEvent 
    * ActionStatusEvent containing the latest call information 
    *
    * @param locale     
    * Locale containing language for caller  
    *
    *
    * @param weekdayOpenCalendar
    * Calendar containing the time CSRs become available on weekdays
    *
    * @param weekdayCloseCalendar
    * Calendar containing the time CSRs are no longer available on weekdays
    *
    * @param saturdayOpenCalendar 
    * Calendar containing the time CSRs become available on Saturdays
    *
    * @param saturdayCloseCalendar
    * Calendar containing the time CSRs are no longer available on Saturdays
    * 
    * @throws HungupException
    * if the caller has hungup.
    * @throws FailureTransferException if there is an unknown failure
    */
  public void speakPlayHoursCallback(ActionStatusEvent actionStatusEvent, Locale locale, 
                                                    Calendar weekdayOpenCalendar, Calendar weekdayCloseCalendar, Calendar saturdayOpenCalendar,   
                                                      Calendar saturdayCloseCalendar) throws HungupException, FailureTransferException{
                                                      
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, " TDDTransferInteraction.speakPlayHoursCallback: Entered method."); 

    String message = afterHoursHeader + weekdayOpenCalendar + afterHoursTo + weekdayCloseCalendar + afterHoursMondayFriday + afterHoursAnd + saturdayOpenCalendar + afterHoursTo + saturdayCloseCalendar + afterHoursTrailer1 + afterHoursTrailer2;
    
    try{
      interaction.playMedia(actionStatusEvent, message);
    }  
    catch (HungupException he){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "TDDTransferInteraction.speakPlayHoursCallback: HungupException caught while trying to play closed message.["+he.toString()+"].");        
      throw new HungupException("TDDTransferInteraction.speakPlayHoursCallback: HungupException caught while trying to play closed message.["+he.toString()+"].");  
    }
    
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, " TDDTransferInteraction.speakPlayHoursCallback: Exiting method.");
                                                      
  }                                                        

}//end class