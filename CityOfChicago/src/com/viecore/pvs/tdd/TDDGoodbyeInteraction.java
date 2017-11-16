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
  * This class defines methods used to speak TDD messages to the caller during the goodbye process.  
  * 
  * @author Michael Ruggiero
  * @since JDK 1.3
  * @version 1.0 date 1/28/2003
 */ 
public class TDDGoodbyeInteraction extends AbstractGoodbyeInteraction{ 

                                                     
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
    * @throws ConfigurationException 
    * Thrown if the Properties object is missing values or contains invalid entries.
    */
  public void initConfiguration(Properties applicationProperties) throws ConfigurationException{ 
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, " TDDGoodbyeInteraction.initConfiguration: Entered method."); 
    if (applicationProperties == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "TDDGoodbyeInteraction.initConfiguration: applicationsProperties is null");        
      throw new ConfigurationException("TDDGoodbyeInteraction.initConfiguration: applicationsProperties is null"); 
    }  
    
   
    super.initConfiguration(applicationProperties);
    
    interaction = new TDDInteraction();
    interaction.initLoggerIF(logger);
    interaction.initConfiguration(applicationProperties);

 
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, " TDDGoodbyeInteraction.initConfiguration: Exited method."); 
  }//end method
  
  
  /**
    * This method speaks the configured goodbye segment to the caller. 
    *  This method creates an Announcement bean with the configured segment 
    * and passed Locale and uses the TDDInteraction to speak it to the caller.
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
  public void speakGoodbye(ActionStatusEvent actionStatusEvent, Locale locale) throws HungupException, FailureTransferException{
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, " TDDGoodbyeInteraction.speakGoodbye: Entered method."); 
    
    interaction.playMedia(actionStatusEvent, goodbyeSegment);   
    
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, " TDDGoodbyeInteraction.speakGoodbye: exited method."); 
  
  }//end method

  

}//end class