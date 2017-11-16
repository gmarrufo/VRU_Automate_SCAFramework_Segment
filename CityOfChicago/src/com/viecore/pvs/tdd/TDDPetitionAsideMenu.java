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
 * data from the menu properties and display the petition aside segments and values.
 *
 *  @author Mehmet Tekkarismaz
 *  @since jdk 1.3 
 *  @version 1.0 1/27/2003
 */


public class TDDPetitionAsideMenu extends TDDMenu{
  
  
  /**
    * Constant to retrieve petition aside rule segment from Properties
    */
  public static final String CONFIG_PETITION_ASIDE_RULE_SEGMENT = "PetitionAsideRuleSegment";
    
  /**
    * Constant to retrieve not owner of the vehicle segment from Properties
    */
  public static final String CONFIG_NOT_OWNER_SEGMENT = "NotOwnerSegment";
  
  /**
    * Constant to retrieve fine already paid segment from Properties
    */
  public static final String CONFIG_FINE_PAID_SEGMENT = "FinePaidSegment";
  
  /**
    * Constant to retrieve failure to respond excusable segment from Properties
    */
  public static final String CONFIG_EXCUSABLE_SEGMENT = "ExcusableSegment";
  
  /**
    * Constant to retrieve petition procedure segment from Properties
    */
  public static final String CONFIG_PETITION_ASIDE_PROCEDURE_SEGMENT = "PetitionAsideProcedureSegment";
  
  /**
    * Constant to retrieve days of determination segment from Properties
    */
  public static final String CONFIG_DAYS_OF_DETERMINATION_SEGMENT = "DaysOfDeterminationSegment";
  
  /**
    * Constant to retrieve determination days from Properties
    */
  public static final String CONFIG_DETERMINATION_DAYS = "DeterminationDays";
  
  /**
    * Segment to speak petition aside rules message
    */
  private String petitionAsideRuleSegment = null;
      
  /** 
    * Segment to speak not owner message
    */
  private String notOwnerSegment = null;
  
  /**
    * Segment to speak fine paid message
    */
  private String finePaidSegment = null;
  
  /**
    * Segment to speak excusable message
    */
  private String excusableSegment = null;
  
  /**
    * Segment to speak petition aside procedure message
    */
  private String petitionAsideProcedureSegment = null;
  
  /**
    * Segment to speak days of determination message
    */
  private String daysOfDeterminationSegment = null;
  
  /**
    * Determination days
    */
  private String determinationDays = null;
    
  /**
    * This method calls setMenuOptions on its parent and then extracts the configuration
    * from the Properties object.
    *
    * @param Properties properties: Must contain the following properties, defined as
    *        instance variables for the class   
    *       
    *       CONFIG_PETITION_ASIDE_RULE_SEGMENT
    *       CONFIG_NOT_OWNER_SEGMENT
    *       CONFIG_FINE_PAID_SEGMENT
    *       CONFIG_EXCUSABLE_SEGMENT
    *       CONFIG_PETITION_ASIDE_PROCEDURE_SEGMENT
    *       CONFIG_DAYS_OF_DETERMINATION_SEGMENT
    *       CONFIG_DETERMINATION_DAYS
    *
    * @throws ConfigurationException 
    *     Thrown if one of the properties is missing
    */ 
  public void setMenuOptions(Properties properties)throws ConfigurationException{
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDPetitionAsideMenu.setMenuOptions: Entered method.");
    //null check on incoming Properties object 
    if(properties == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDPetitionAsideMenu.setMenuOptions: Properties object passed in is null.");
      throw new ConfigurationException("TDDPetitionAsideMenu.setMenuOptions: Properties object passed in is null.");
    }  
    
    //call setMenuOptions on parent TDDMenu
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDPetitionAsideMenu.setMenuOptions: Calling TDDMenu's setMenuOptions method.");
    super.setMenuOptions(properties);
    
    //extract the configuration from Properties object
    petitionAsideRuleSegment = validateStringProperty(properties, CONFIG_PETITION_ASIDE_RULE_SEGMENT, true);
    notOwnerSegment = validateStringProperty(properties, CONFIG_NOT_OWNER_SEGMENT, true);
    finePaidSegment = validateStringProperty(properties, CONFIG_FINE_PAID_SEGMENT, true);
    excusableSegment = validateStringProperty(properties, CONFIG_EXCUSABLE_SEGMENT, true);
    petitionAsideProcedureSegment = validateStringProperty(properties, CONFIG_PETITION_ASIDE_PROCEDURE_SEGMENT, true);
    daysOfDeterminationSegment = validateStringProperty(properties, CONFIG_DAYS_OF_DETERMINATION_SEGMENT, true);
    determinationDays = validateStringProperty(properties, CONFIG_DETERMINATION_DAYS, true);
        
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDPetitionAsideMenu.setMenuOptions: Leaving method.");
  }//end setMenuOptions
  
   
  
  
  /**
    * This method uses the configured segments and values to create a String to play .  
    * This String is used to set the header message in the menu bean.
    *
    * @param session
    *       Hashtable containing the violation object
    * @param locale 
    *       Locale containing language for caller
    * @return String
    *       header message
    *
    * @throws TransferException if header message cannot be built
    */ 
  public String setCallDetails(Hashtable session, Locale locale) throws TransferException{   
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDPetitionAsideMenu.setCallDetails: Entered method.");
    
    //create a String for header message 
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDPetitionAsideMenu.setCallDetails: Creating a String for header message.");
    String message = "";
    
    //null check for session
    if(session == null){
      if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "TDDPetitionAsideMenu.setCallDetails: HashTable object passed into this method is null.");
      throw new TransferException("TDDPetitionAsideMenu.setCallDetails: HashTable object passed into this method is null.");
    }
    //create petitionAsideRuleSegment audio  
    message = petitionAsideRuleSegment + notOwnerSegment + finePaidSegment + excusableSegment + petitionAsideProcedureSegment + determinationDays + daysOfDeterminationSegment;
    
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDPetitionAsideMenu.setCallDetails: Created petitionAsideRuleSegment message = [" + message + "]");
    
    //return the message
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDPetitionAsideMenu.setCallDetails: Returning the String and leaving method.");
    return message;
  }//end setCallDetails

}//end class TDDPetitionAsideMenu























