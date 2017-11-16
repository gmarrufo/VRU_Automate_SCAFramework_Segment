/*
 * Licensed Materials - Property of Viecore Inc.
 *
 * (C) Copyright Viecore Inc. 2002, 2003 All Rights Reserved
 */

package com.viecore.pvs.tdd;

import com.viecore.pvs.util.*;
import com.viecore.pvs.*;

import com.viecore.util.log.*;
import com.viecore.util.configuration.*;

import com.ibm.telephony.directtalk.*;
import com.ibm.telephony.beans.media.*;

import java.util.*;

/**
 * This class is a menu that contains functionality to pull data from the configuration 
 * and speak the locations and hours data to the caller.
 *
 *  @author Michael Ruggiero
 *  @since jdk 1.3 
 *  @version 1.0 1/28/2003
 */


public class TDDLocationsAndHoursMenu extends TDDMenu{
  
  
  /**
    * Constant to retrieve url message from Properties
    */
  public static final String CONFIG_URL_MESSAGE = "URLSegment";
    
  /**
    * Constant to retrieve list message from Properties
    */
  public static final String CONFIG_LIST_MESSAGE = "ListSegment";
    
  /**
    * Constant to retrieve pound key skip message from Properties
    */
  public static final String CONFIG_POUND_KEY_SKIP_MESSAGE = "PoundKeySkipSegment";
    
  /**
    * Constant to retrieve conducts hearings message from Properties
    */
  public static final String CONFIG_CONDUCTS_HEARINGS = "ConductsHearingsSegment";
  
  /**
    * Constant to retrieve to message from Properties
    */
  public static final String CONFIG_TO_MESSAGE = "ToSegment";
  
  /**
    * Constant to retrieve payment parking violations message from Properties
    */
  public static final String CONFIG_PAYMENT_PARKING_VIOLATIONS_MESSAGE = "PaymentParkingViolationsSegment";
  
  /**
    * Constant to retrieve payments only message from Properties 
    */
  public static final String CONFIG_PAYMENTS_ONLY_MESSAGE = "PaymentsOnlySegment";
  
  /**
    * Constant to retrieve saturday message from Properties 
    */
  public static final String CONFIG_SATURDAY_MESSAGE = "SaturdaySegment";
  
  /**
    * Constant to retrieve location payment open message from Properties
    */
  public static final String CONFIG_LOCATION_PAYMENT_OPEN = "LocationPaymentOpen";
  
  /**
    * Constant to retrieve location payment close message from Properties
    */
  public static final String CONFIG_LOCATION_PAYMENT_CLOSE = "LocationPaymentClose";
  
  /**
    * Constant to retrieve location hearing open message from Properties
    */
  public static final String CONFIG_LOCATION_HEARING_OPEN = "LocationHearingOpen";
  
  /**
    * Constant to retrieve location hearing close message from Properties
    */
  public static final String CONFIG_LOCATION_HEARING_CLOSE = "LocationHearingClose";
  
  /**
    * Constant to retrieve saturday open message from Properties
    */
  public static final String CONFIG_SATURDAY_OPEN = "SaturdayOpen";
  
  /**
    * Constant to retrieve saturday close message from Properties
    */
  public static final String CONFIG_SATURDAY_CLOSE = "SaturdayClose";
  
  /**
    * Constant to retrieve location from Properties
    */
  public static final String CONFIG_LOCATION = "Location";
  
  /**
    * Segment to play url message
    */
  private String url = null;
    
  /**
    * Segment to play list message 
    */
  private String list = null;
  
  /**
    * Segment to play pound key message
    */
  private String poundKey = null;
  
  /**
    * Segment to play conduct hearing message
    */
  private String conductHearings = null;
  
  /**
    * Segment to play to message
    */
  private String to = null;
  
  /**
    * Segment to play payment parking violations message
    */
  private String paymentParkingViolations = null;
  
  /**
    * Segment to play payment only message
    */
  private String paymentOnly = null;
  
  /**
    * Segment to play saturday message
    */
  private String saturday = null;
  
  /**
    * Vector of location objects to play to the caller
    */
  private Vector locations = new Vector();
  
  /**
    * Number of locations to be spoken
    */
  private int numberOfLocations = 0;
  
  /**
    * This method calls setMenuOptions on its parent and then extracts the configuration
    * from the Properties object.
    *
    * @param Properties properties: Must contain the following properties, defined as
    *        instance variables for the class   
    *       
    *       CONFIG_URL_MESSAGE
    *       CONFIG_LIST_MESSAGE
    *       CONFIG_POUND_KEY_SKIP_MESSAGE
    *       CONFIG_CONDUCTS_HEARINGS
    *       CONFIG_TO_MESSAGE
    *       CONFIG_PAYMENTS_ONLY_MESSAGE
    *       CONFIG_PAYMENT_PARKING_VIOLATIONS_MESSAGE
    *       CONFIG_LOCATION (1 to n)
    *       CONFIG_LOCATION_PAYMENT_OPEN (1 to n)
    *       CONFIG_LOCATION_PAYMENT_CLOSE (1 to n)
    *       CONFIG_LOCATION_HEARING_OPEN (1 to n)
    *       CONFIG_LOCATION_HEARING_CLOSE (1 to n)
    *       CONFIG_SATURDAY_OPEN (1 to n)
    *       CONFIG_SATURDAY_CLOSE (1 to n)
    * 
    * @throws ConfigurationException 
    *     Thrown if one of the properties is missing
    */ 
  public void setMenuOptions(Properties properties)throws ConfigurationException{
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDLocationsAndHoursMenu.setMenuOptions: Entered method.");
    //declare method scope variables
    int locationIndex = 1;
    String locationIndexString = null;
    String locationKey = null;
    boolean moreLocation = true; 
    Location location = null;
  
    //null check on incoming Properties object 
    if(properties == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDLocationsAndHoursMenu.setMenuOptions: Properties object passed in is null.");
      throw new ConfigurationException("TDDLocationsAndHoursMenu.setMenuOptions: Properties object passed in is null.");
    }  
    
    //call setMenuOptions on parent TDDMenu
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDLocationsAndHoursMenu.setMenuOptions: Calling TDDMenu's setMenuOptions method.");
    super.setMenuOptions(properties);
    
    //extract the configuration from Properties object
    url = validateStringProperty(properties, CONFIG_URL_MESSAGE, true);
    list = validateStringProperty(properties, CONFIG_LIST_MESSAGE, true);
    poundKey = validateStringProperty(properties, CONFIG_POUND_KEY_SKIP_MESSAGE, true);
    conductHearings = validateStringProperty(properties, CONFIG_CONDUCTS_HEARINGS, true);
    to = validateStringProperty(properties, CONFIG_TO_MESSAGE, true);
    paymentParkingViolations = validateStringProperty(properties, CONFIG_PAYMENT_PARKING_VIOLATIONS_MESSAGE, true);
    paymentOnly = validateStringProperty(properties, CONFIG_PAYMENTS_ONLY_MESSAGE, true);
    saturday = validateStringProperty(properties, CONFIG_SATURDAY_MESSAGE, true);
    
    //get Locations
    while(moreLocation){
      //form a location key name
      locationIndexString = Integer.toString(locationIndex);
      locationKey = CONFIG_LOCATION.concat(locationIndexString);
      
      //check if this key exists in properties
      boolean locationKeyExist = properties.containsKey(locationKey);  
      if(locationKeyExist == true){  
        if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDLocationsAndHoursMenu.setMenuOptions: Key ["+locationKey+"] exists in properties.");    
        String locationValue = validateStringProperty(properties, locationKey, true);
        
        //create a Location object
        if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDLocationsAndHoursMenu.setMenuOptions: Creating a new Location object."); 
        location = new Location();
        
        //set address in Location object
        if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDLocationsAndHoursMenu.setMenuOptions: Setting location address in Location object as ["+locationValue+"].");
        location.setAddress(locationValue);
        
        //get location hearing open hour segment for this index
        String locationHearingOpenKey = CONFIG_LOCATION_HEARING_OPEN.concat(locationIndexString);
        String locationHearingOpenValue = validateStringProperty(properties, locationHearingOpenKey, true);
        
        // get a calendar object from the String
        Calendar locationHearingOpenCalendar = createCalendar(locationHearingOpenValue);
        
        //set locationHearingOpenValue in Location object
        if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDLocationsAndHoursMenu.setMenuOptions: Setting  hearing open hour in Location object as ["+locationHearingOpenValue+"].");
        location.setHearingOpenHour(locationHearingOpenCalendar);
        
        //get location hearing close segments
        String locationHearingCloseKey = CONFIG_LOCATION_HEARING_CLOSE.concat(locationIndexString);
        String locationHearingCloseValue = validateStringProperty(properties, locationHearingCloseKey, true);
        
        // get a calendar object from the String
        Calendar locationHearingCloseCalendar = createCalendar(locationHearingCloseValue);
        
        //set locationHearingCloseValue in Location object
        if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDLocationsAndHoursMenu.setMenuOptions: Setting  hearing close hour in Location object as ["+locationHearingCloseValue+"].");
        location.setHearingCloseHour(locationHearingCloseCalendar);
        
        //get location payment open segments
        String locationPaymentOpenKey = CONFIG_LOCATION_PAYMENT_OPEN.concat(locationIndexString);
        String locationPaymentOpenValue = validateStringProperty(properties, locationPaymentOpenKey, true);
        
        // get a calendar object from the String
        Calendar locationPaymentOpenCalendar = createCalendar(locationPaymentOpenValue);
        
        //set locationPaymentOpenValue in Location object
        if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDLocationsAndHoursMenu.setMenuOptions: Setting  payment open hour in Location object as ["+locationPaymentOpenValue+"].");
        location.setPaymentOpenHour(locationPaymentOpenCalendar);
        
        //get location payment close segments
        String locationPaymentCloseKey = CONFIG_LOCATION_PAYMENT_CLOSE.concat(locationIndexString);
        String locationPaymentCloseValue = validateStringProperty(properties, locationPaymentCloseKey, true);
        
        // get a calendar object from the String
        Calendar locationPaymentCloseCalendar = createCalendar(locationPaymentCloseValue);
        
        //add locationPaymentCloseValue to vector
        if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDLocationsAndHoursMenu.setMenuOptions: Setting  payment close hour in Location object as ["+locationPaymentCloseValue+"].");
        location.setPaymentCloseHour(locationPaymentCloseCalendar);
        
        //check for saturday openings
        String saturdayOpenKey = CONFIG_SATURDAY_OPEN.concat(locationIndexString);
        boolean saturdayOpenKeyExist = properties.containsKey(saturdayOpenKey);  
        if(saturdayOpenKeyExist == true){  
          if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDLocationsAndHoursMenu.setMenuOptions: Key ["+saturdayOpenKey+"] exists in properties.");    
          //set saturdayOpen variable in Location object to true
          location.setSaturdayOpen(true);
          
          //check for saturday open hour
          String saturdayOpenValue = validateStringProperty(properties, saturdayOpenKey, true);
          
          // get a calendar object from the String
          Calendar saturdayOpenCalendar = createCalendar(saturdayOpenValue);
          
          //set saturdayOpenValue in Location object
          if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDLocationsAndHoursMenu.setMenuOptions: Setting  saturday open hour in Location object as ["+saturdayOpenValue+"].");
          location.setSaturdayOpenHour(saturdayOpenCalendar);
          
          //if there is an open hour then there must be a close hour, check for saturday close hour
          String saturdayCloseKey = CONFIG_SATURDAY_CLOSE.concat(locationIndexString);
          String saturdayCloseValue = validateStringProperty(properties, saturdayCloseKey, true);
          
          // get a calendar object from the String
          Calendar saturdayCloseCalendar = createCalendar(saturdayCloseValue);
          
          //set saturdayCloseValue in Location object
          if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDLocationsAndHoursMenu.setMenuOptions: Setting  saturday close hour in Location object as ["+saturdayCloseValue+"].");
          location.setSaturdayCloseHour(saturdayCloseCalendar);
        }//end if (saturdayOpenKeyExist == true)
        
        //add Location to vector
        if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDLocationsAndHoursMenu.setMenuOptions: Adding Location object ["+locationIndexString+"] to Vector.");
        locations.add(location);
        
        //increment index
        locationIndex++;       
      }
      else if(locationKeyExist == false){
        if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDLocationsAndHoursMenu.setMenuOptions: Key ["+locationKey+"] does not exists in properties.");    
        moreLocation = false;
      }  
    }//end while(moreLocation)  
    
    //assign the number of locations to class level numberOfLocations variable
    numberOfLocations = locationIndex - 1;
    if (logLevel >= LoggerIF.NOTICE) log(LoggerIF.NOTICE,"TDDLocationsAndHoursMenu.setMenuOptions: There are total of ["+numberOfLocations+"] locations.");    
    
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDLocationsAndHoursMenu.setMenuOptions: Leaving method.");
  }//end setMenuOptions
  
   
  
  
  /**
    * This method uses the configured segments and the vector of locations to form the 
    * String to play .
    *
    * @param session
    *       Hashtable containing the violation object
    * @param locale 
    *       Locale containing language for caller
    * @return String 
    *       header message
    *
    * @throws TransferException if the header is unable to be constructed
    */ 
  public String setCallDetails(Hashtable session, Locale locale) throws TransferException{   
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDLocationsAndHoursMenu.setCallDetails: Entered method.");
    
    //create a String for header message 
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDLocationsAndHoursMenu.setCallDetails: Creating a MediaSequence bean for header message.");
    String message = "";
    
    //null check for session
    if(session == null){
      if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "TDDLocationsAndHoursMenu.setCallDetails: HashTable object passed into this method is null.");
      throw new TransferException("TDDLocationsAndHoursMenu.setCallDetails: HashTable object passed into this method is null.");
    }
      
    // form base message
    message = url + list + numberOfLocations + poundKey;
    
    //get Locations from locations vector
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDLocationsAndHoursMenu.setCallDetails: About to go through ["+locations.size()+"] elements of locations vector.");
    Enumeration locationEnumeration = locations.elements();
    while (locationEnumeration.hasMoreElements()){
      //get location
      if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDLocationsAndHoursMenu.setCallDetails: Getting Location object.");
      Location locationObject = (Location) locationEnumeration.nextElement();
      
      //get location address
      if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDLocationsAndHoursMenu.setCallDetails: Getting location address.");
      String locationAddress = locationObject.getAddress();
      //create location address message
      message = message + locationAddress + conductHearings;
      
      //get hearing open hour
      if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDLocationsAndHoursMenu.setCallDetails: Getting hearing open time.");
      Calendar hearingOpen = locationObject.getHearingOpenHour();
      // add hearing open data to message
      message = message + hearingOpen + to;
      
      //get hearing close hour
      if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDLocationsAndHoursMenu.setCallDetails: Getting hearing close time.");
      Calendar hearingClose = locationObject.getHearingCloseHour();
      // add hearing close data to message
      message = message + hearingClose;
      
      //check if the location is open on saturdays
      if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDLocationsAndHoursMenu.setCallDetails: Checking if the location is open on Saturdays.");
      boolean open = locationObject.isSaturdayOpen();
      if(open){
        if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDLocationsAndHoursMenu.setCallDetails: Location is open on saturdays.");
        //set saturday segment in message
        message = message + saturday;
      
        //get saturday open hour
        if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDLocationsAndHoursMenu.setCallDetails: Getting saturday open time.");
        Calendar saturdayOpen = locationObject.getSaturdayOpenHour();
        // add saturday hearing open data to message
        message = message + saturdayOpen + to;
        
        //get saturday close hour
        if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDLocationsAndHoursMenu.setCallDetails: Getting saturday close time.");
        Calendar saturdayClose = locationObject.getSaturdayCloseHour();
        // add saturday hearing close data to message
        message = message + saturdayClose;
        
      }//end if(open)  
      
      // adding payment parking violations to String
      message = message + paymentParkingViolations;
      
      //get payment open hour
      if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDLocationsAndHoursMenu.setCallDetails: Getting payment open time.");
      Calendar paymentOpen = locationObject.getPaymentOpenHour();
      // add payment open data to message
      message = message + paymentOpen + to;
      
      //get payment close hour
      if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "TDDLocationsAndHoursMenu.setCallDetails: Getting payment close time.");
      Calendar paymentClose = locationObject.getPaymentCloseHour();
      // add payment close data to message
      message = message + paymentClose;
      
    }//end while 
    
    //return the String
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "TDDLocationsAndHoursMenu.setCallDetails: Leaving method.");
    return message;
  }//end setCallDetails
  
  /**
    * This method is used to create a Calendar object using the hour retrieved from configuration.
    *
    * @param hour
    *       String containing the hour in HHMM format
    * @return 
    *       object set with the specific hour
    *
    * @throws ConfigurationException if there is a problem creating the calendar object
    */ 
  private Calendar createCalendar(String hour) throws ConfigurationException{   
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "DTMFLocationsAndHoursMenu.createCalendar: Entered method with hour ["+hour+"].");
    //declare method scope variables
    int hourOfDay = 0;
    int minute = 0;
    
    //create Calendar object
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "DTMFLocationsAndHoursMenu.createCalendar: Creating a Calendar object.");
    Calendar calendar = Calendar.getInstance();
    
    if((hour != null) && (hour.length() ==4)){
      try{
        //get the hour section of the incoming parameter
        String hourOfDayString = hour.substring(0, 2);
        //parse it into an integer
        hourOfDay = Integer.parseInt(hourOfDayString);
        if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "DTMFLocationsAndHoursMenu.createCalendar: Hour of day is ["+hourOfDay+"].");
        //get the minute section of the incoming parameter
        String minuteString = hour.substring(2);
        //parse it into an integer
        minute = Integer.parseInt(minuteString);
        if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "DTMFLocationsAndHoursMenu.createCalendar: Minute is ["+minute+"].");
        //set specific hour
        if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "DTMFLocationsAndHoursMenu.createCalendar: Setting hour and minute in Calendar object.");
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        //set specific minute
        calendar.set(Calendar.MINUTE, minute);
      }
      catch(NumberFormatException nfe){
        if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "DTMFLocationsAndHoursMenu.createCalendar: Non parseable hour value in configuration. ["+nfe+"]");
        throw new InvalidPropertyConfigurationException("DTMFLocationsAndHoursMenu.createCalendar: Non parseable hour value in configuration. ["+nfe+"]");
      }
    }
    else{
      if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "DTMFLocationsAndHoursMenu.createCalendar: Incorrect hour format.");
      throw new InvalidPropertyConfigurationException("DTMFLocationsAndHoursMenu.createCalendar: Incorrect hour format.");
    }
    
    //return Calendar object
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "DTMFLocationsAndHoursMenu.createCalendar: Leaving method.");
    return calendar;    
  }//end createCalendar

  
}//end class























