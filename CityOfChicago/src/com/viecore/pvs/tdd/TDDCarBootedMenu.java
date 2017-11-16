/*
 * Licensed Materials - Property of Viecore Inc.
 *
 * (C) Copyright Viecore Inc. 2002, 2003 All Rights Reserved
 */

package com.viecore.pvs.tdd;


import com.viecore.pvs.*;
import com.viecore.pvs.transaction.*;
import com.viecore.util.log.*;

import com.ibm.telephony.directtalk.*;
import com.ibm.telephony.beans.media.*;

import java.util.*;
import com.viecore.util.configuration.*;

import com.ibm.telephony.directtalk.*;
import com.ibm.telephony.beans.directtalk.*;
import com.ibm.telephony.beans.*;
import com.ibm.telephony.beans.media.*;

/**
 * This class is a menu that contains functionality to pull additional configuration 
 * data from the menu properties and display the configurable car booted segments and values.
 *
 *  @author Mehmet Tekkarismaz
 *  @since jdk 1.3 
 *  @version 1.0 1/16/2003
 */


public class TDDCarBootedMenu extends TDDMenu{
  
  
  /**
    * Constant used to retrieve release booted vehicle message from Properties
    */
  public static final String CONFIG_RELEASE_BOOTED_VEHICLE_MESSAGE = "ReleaseBootedVehicleSegment";
    
  /**
    * Constant used to retrieve boot fee message from Properties
    */
  public static final String CONFIG_BOOT_FEE_MESSAGE = "BootFeeSegment";
  
  /**
    * Constant used to retrieve tow fee message from Properties
    */
  public static final String CONFIG_TOW_FEE_MESSAGE = "TowFeeSegment";
  
  /**
    * Constant used to retrieve daily storage fee message from Properties
    */
  public static final String CONFIG_DAILY_STORAGE_FEE_MESSAGE = "DailyStorageFeeSegment";
  
  /**
    * Constant used to retrieve first message from Properties
    */
  public static final String CONFIG_FIRST_MESSAGE = "FirstSegment";
  
  /**
    * Constant used to retrieve after daily storage fee message from Properties
    */
  public static final String CONFIG_AFTER_DAILY_STORAGE_FEE_MESSAGE = "AfterDailyStorageFeeSegment";
  
  /**
    * Constant used to retrieve each day thereafter message from Properties
    */
  public static final String CONFIG_EACH_DAY_THEREAFTER_MESSAGE = "EachDayThereafterSegment";
  
  /**
    * Constant used to retrieve vehicle over message from Properties
    */
  public static final String CONFIG_VEHICLE_OVER_MESSAGE = "VehicleOverSegment";
  
  /**
    *  Constant used to retrieve owner challenge message from Properties 
    */
  public static final String CONFIG_OWNER_CHALLENGE_MESSAGE = "OwnerChallengeSegment";
  
  /**
    * Constant used to retrieve personal check message from Properties
    */
  public static final String CONFIG_PERSONAL_CHECK_MESSAGE = "PersonalCheckSegment";
  
  /**
    * Constant used to retrieve car pounds limit from Properties
    */
  public static final String CONFIG_POUNDS = "CarPounds";
  
  /**
    * Constant used to retrieve booted days from Properties
    */
  public static final String CONFIG_DAYS = "BootedDays";
  
  /**
    * Constant used to retrieve under boot fee from Properties
    */
  public static final String CONFIG_UNDER_BOOT_FEE = "UnderBootFee";
  
  /**
    * Constant used to retrieve under first days storage fee from Properties
    */
  public static final String CONFIG_UNDER_FIRST_DAYS_STORAGE_FEE = "UnderFirstDaysStorageFee";
  
  /**
    * Constant used to retrieve under later days storage fee from Properties
    */
   public static final String CONFIG_UNDER_LATER_DAYS_STORAGE_FEE = "UnderLaterDaysStorageFee";
  
  /**
    * Constant used to retrieve over boot fee from Properties
    */
  public static final String CONFIG_OVER_BOOT_FEE = "OverBootFee";
  
  /**
    * Constant used to retrieve over first days storage fee from Properties
    */
  public static final String CONFIG_OVER_FIRST_DAYS_STORAGE_FEE = "OverFirstDaysStorageFee";
  
  /**
    * Constant used to retrieve over later days storage fee from Properties
    */
  public static final String CONFIG_OVER_LATER_DAYS_STORAGE_FEE = "OverLaterDaysStorageFee";
  
  /**
    * Constant used to retrieve under tow fee from Properties
    */
  public static final String CONFIG_UNDER_TOW_FEE = "UnderTowFee";
  
  /**
    * Constant used to retrieve over tow fee from Properties
    */
  public static final String CONFIG_OVER_TOW_FEE = "OverTowFee";
  
  /**
    * Constant used to retrieve challenge days from Properties
    */
  public static final String CONFIG_CHALLENGE_DAYS = "ChallengeDays";
  
  /**
    * Release booted vehicle message
    */
  private String releaseBootedVehicleSegment = null;
  
  /**
    * Boot fee message
    */
  private String bootFeeMessage = null;
  
  /**
    * Tow fee message
    */
  private String towFeeMessage = null;
  
  /**
    * Daily storage fee message  
    */
  private String dailyStorageFeeMessage = null;
  
  /**
    * First message
    */
  private String firstMessage = null;
  
  /**
    * After daily storage fee message
    */
  private String afterDailyStorageFeeMessage = null;
  
  /**
    * Each day thereafter message
    */
  private String eachDayThereafterMessage = null;
  
  /**
    * Vehicle over message
    */
  private String vehicleOverMessage = null;
  
  /**
    * Ownver challenge message
    */
  private String ownerChallengeMessage = null;
  /**
    * Personal check message
    */
  private String personalCheckMessage = null;
  
  /**
    * Amount of pounds used to determine different fee for vehicles above or below.
    */
  private String pounds = null;
  
  /**
    * Amount of days used to determine different fee for vehicles based on amount of storage days
    */
  private String days = null;
  
  /**
    * Boot fee for vehicles under configured weight
    */
  private String underBootFee = null;
  
  /**
    * Storage fee for vehicles under configured weight and with less 
    * days stored than the configured amount of storage days
    */
  private String underFirstDaysStorageFee = null;
  
  /**
    * Storage fee for vehicles under configured weight and 
    * with more days stored than the configured amount of storage days
    */
  private String underLaterDaysStorageFee = null;
  
  /**
    * Boot fee for vehicles over configured weight
    */
  private String overBootFee = null;
  
  /**
    * Storage fee for vehicles over configured weight and 
    * with less days stored than the configured amount of storage days
    */
  private String overFirstDaysStorageFee = null;
  
  /**
    * Storage fee for vehicles over configured weight and 
    * with more days stored than the configured amount of storage days
    */
  private String overLaterDaysStorageFee = null;
  
  /**
    * Tow fee for vehicles under certain pounds
    */
  private String underTowFee = null;
  
  /**
    * Tow fee for vehicles over certain pounds
    */
  private String overTowFee = null;
  
  /**
    * The duration of days which the registered owner may 
    * challenge the validity of the Boot by appearing in person within 
    */
  private String challengeDays = null;
  
  /**
   *  Object used for Reporting the total number calls for booted inquiries
   */
   
   private LogEventTransaction logEventTransaction = null;
   
  
  
  
  /**
    * This method constructs a LogEventTransaction object and then calls super.initConfiguration.
    *
    * @param properties
    *     Properties object containing the configuration
    * @throws ConfigurationException
    *     if the Properties object does not contain the appropriate properties
    */
  public void initConfiguration(Properties properties) throws ConfigurationException{
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDCarBootedMenu.initConfiguration: Entered method.");
    
    //instatiate a LogEventTransaction object, just log any type of error    
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDCarBootedMenu.initConfiguration: Instantiating a LogEventTransaction object."); 
    logEventTransaction = new LogEventTransaction();
    logEventTransaction.initLoggerIF(logger);
    logEventTransaction.initConfiguration(properties); 
    
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDCarBootedMenu.initConfiguration: About to call super.initConfiguration(properties);."); 
    
    super.initConfiguration(properties);
 
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDCarBootedMenu.initConfiguration: Exiting method.");
 
  }//end initConfiguration
    
    
    /**
    * This method calls logEvent on the LogEventTransaction object and then calls \
    * super.presentMenu. 
    *
    * @param session
    *     Hashtable with call information
    * @param actionStatusEvent
    *     ActionStatusEvent containing the latest call information 
    * @return 
    *     contains the next action to be executed by the application
    */
  public NextAction presentMenu(Hashtable session, ActionStatusEvent actionStatusEvent){
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDCarBootedMenu.presentMenu: Entered method.");
     
    NextAction next = null;
    
    try{
      if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDCarBootedMenu.presentMenu: About to call logEventTransaction.logEvent for BOOT_INFORMATION");  
      this.logEventTransaction.logEvent(actionStatusEvent,LogEventTransaction.BOOT_INFORMATION);
    
      next = super.presentMenu(session, actionStatusEvent);
    
    }catch(HungupException he){
      if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCarBootedMenu.presentMenu: HangupException "+he.toString());  
       
    }
    
    
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDCarBootedMenu.presentMenu: Exiting method.");
    
    return next;
    
  }//end presentMenu
  
  
  /**
    * This method calls setMenuOptions on its parent and then extracts the configuration
    * from the Properties object.
    *
    * @param Properties properties: Must contain the following properties, defined as
    *        instance variables for the class   
    *       
    *       CONFIG_RELEASE_BOOTED_VEHICLE_MESSAGE
    *       CONFIG_BOOT_FEE_MESSAGE
    *       CONFIG_TOW_FEE_MESSAGE
    *       CONFIG_DAILY_STORAGE_FEE_MESSAGE
    *       CONFIG_FIRST_MESSAGE
    *       CONFIG_AFTER_DAILY_STORAGE_FEE_MESSAGE
    *       CONFIG_EACH_DAY_THEREAFTER_MESSAGE
    *       CONFIG_VEHICLE_OVER_MESSAGE
    *       CONFIG_OWNER_CHALLENGE_MESSAGE
    *       CONFIG_PERSONAL_CHECK_MESSAGE
    *       CONFIG_POUNDS
    *       CONFIG_DAYS
    *       CONFIG_UNDER_BOOT_FEE
    *       CONFIG_UNDER_FIRST_DAYS_STORAGE_FEE
    *       CONFIG_UNDER_LATER_DAYS_STORAGE_FEE
    *       CONFIG_OVER_BOOT_FEE
    *       CONFIG_OVER_FIRST_DAYS_STORAGE_FEE
    *       CONFIG_OVER_LATER_DAYS_STORAGE_FEE
    *       CONFIG_UNDER_TOW_FEE
    *       CONFIG_OVER_TOW_FEE
    *       CONFIG_CHALLENGE_DAYS
    *
    * @throws ConfigurationException 
    *     Thrown if one of the properties is missing
    */ 
  public void setMenuOptions(Properties properties)throws ConfigurationException{
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDCarBootedMenu.setMenuOptions: Entered method.");
    //null check on incoming Properties object 
    if(properties == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDCarBootedMenu.setMenuOptions: Properties object passed in is null.");
      throw new ConfigurationException("TDDCarBootedMenu.setMenuOptions: Properties object passed in is null.");
    }  
    
    //call setMenuOptions on parent TDDMenu
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDCarBootedMenu.setMenuOptions: Calling TDDMenu's setMenuOptions method.");
    super.setMenuOptions(properties);
    
    //extract the configuration from Properties object
    releaseBootedVehicleSegment = validateStringProperty(properties, CONFIG_RELEASE_BOOTED_VEHICLE_MESSAGE, true);
    bootFeeMessage = validateStringProperty(properties, CONFIG_BOOT_FEE_MESSAGE, true);
    towFeeMessage = validateStringProperty(properties, CONFIG_TOW_FEE_MESSAGE, true);
    dailyStorageFeeMessage = validateStringProperty(properties, CONFIG_DAILY_STORAGE_FEE_MESSAGE, true);
    firstMessage = validateStringProperty(properties, CONFIG_FIRST_MESSAGE, true);
    afterDailyStorageFeeMessage = validateStringProperty(properties, CONFIG_AFTER_DAILY_STORAGE_FEE_MESSAGE, true);
    eachDayThereafterMessage = validateStringProperty(properties, CONFIG_EACH_DAY_THEREAFTER_MESSAGE, true);
    vehicleOverMessage = validateStringProperty(properties, CONFIG_VEHICLE_OVER_MESSAGE, true);
    ownerChallengeMessage = validateStringProperty(properties, CONFIG_OWNER_CHALLENGE_MESSAGE, true);
    personalCheckMessage = validateStringProperty(properties, CONFIG_PERSONAL_CHECK_MESSAGE, true);
    pounds = validateStringProperty(properties, CONFIG_POUNDS, true);
    days = validateStringProperty(properties, CONFIG_DAYS, true);
    underBootFee = validateStringProperty(properties, CONFIG_UNDER_BOOT_FEE, true);
    underFirstDaysStorageFee = validateStringProperty(properties, CONFIG_UNDER_FIRST_DAYS_STORAGE_FEE, true);
    underLaterDaysStorageFee = validateStringProperty(properties, CONFIG_UNDER_LATER_DAYS_STORAGE_FEE, true);
    overBootFee = validateStringProperty(properties, CONFIG_OVER_BOOT_FEE, true);
    overFirstDaysStorageFee = validateStringProperty(properties, CONFIG_OVER_FIRST_DAYS_STORAGE_FEE, true);
    overLaterDaysStorageFee = validateStringProperty(properties, CONFIG_OVER_LATER_DAYS_STORAGE_FEE, true);
    underTowFee = validateStringProperty(properties, CONFIG_UNDER_TOW_FEE, true);
    overTowFee = validateStringProperty(properties, CONFIG_OVER_TOW_FEE, true);
    challengeDays = validateStringProperty(properties, CONFIG_CHALLENGE_DAYS, true);
    
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDCarBootedMenu.setMenuOptions: Exiting method.");
  }//end setMenuOptions
  
   
  
  
  /**
    * This method uses the configured segments and values to create a header message to play .  
    *
    * @param session
    *       Hashtable containing the violation object
    * @param locale 
    *       Locale containing language for caller
    * @return String 
    *       header message
    * @throws TransferException if there is a problem building the header message
    */ 
  public String setCallDetails(Hashtable session, Locale locale) throws TransferException{   
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDCarBootedMenu.setCallDetails: Entered method.");
    
    String headerMessage = "";
    
    //null check for session and locale, log error and return empty String if true
    if(session == null){
      if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "TDDCarBootedMenu.setCallDetails: HashTable object passed into this method is null.");
      throw new TransferException("TDDCarBootedMenu.setCallDetails: HashTable object passed into this method is null.");
    }
    
    if(locale == null){
      if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "TDDCarBootedMenu.setCallDetails: Locale object passed into this method is null.");
      throw new TransferException("TDDCarBootedMenu.setCallDetails: Locale object passed into this method is null.");
    }
    
    //create header message
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDCarBootedMenu.setCallDetails: Creating header message.");
    headerMessage = releaseBootedVehicleSegment + pounds + bootFeeMessage + underBootFee + towFeeMessage + underTowFee + dailyStorageFeeMessage + underFirstDaysStorageFee + firstMessage + days + afterDailyStorageFeeMessage + underLaterDaysStorageFee + eachDayThereafterMessage + vehicleOverMessage + pounds + bootFeeMessage + overBootFee + towFeeMessage + overTowFee + dailyStorageFeeMessage + overFirstDaysStorageFee + firstMessage + days + afterDailyStorageFeeMessage + overLaterDaysStorageFee + eachDayThereafterMessage + ownerChallengeMessage + challengeDays + personalCheckMessage;  
    
    //return the header message 
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDCarBootedMenu.setCallDetails: Returning the header message and leaving method.");
    return headerMessage;
  }//end setCallDetails

}//end class























