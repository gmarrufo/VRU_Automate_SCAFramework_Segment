/*
 * Licensed Materials - Property of Viecore Inc.
 *
 * (C) Copyright Viecore Inc. 2002, 2003 All Rights Reserved
 */

package com.viecore.pvs.tdd;


import java.util.*;

import com.ibm.telephony.directtalk.*;
import com.ibm.telephony.beans.directtalk.*;
import com.ibm.telephony.beans.*;
import com.ibm.telephony.beans.media.*;
import com.ibm.telephony.beans.migrate.*;

import com.viecore.util.log.*;
import com.viecore.util.configuration.*;

import com.viecore.pvs.*;
import com.viecore.pvs.menu.*;


/**
 * This class contains functionality common to all TDD menus.  
 * This class represents a generic TDD menu with a configurable 
 * header message and configurable menu items. 
 * 
 * @author Michael Ruggiero
 * @since JDK 1.3
 * @version 1.1 12/30/2002
 */
 
public class TDDMenu extends AbstractMenu implements DoneListener, FailedListener, HungupListener{
  
  /**
    * TDDInteraction object to play the header message of the menu
    */
  private TDDInteraction tddInteraction = null;
  
  /**
    * Constant used to obtain header message segment from the Properties
    */
  public static final String CONFIG_HEADER_MESSAGE = "HeaderMessage";
  
  /**
    * Constant used to obtain footer message segment from the Properties
    */ 
  public static final String CONFIG_FOOTER_MESSAGE = "FooterMessage";
  
  /**
    * String containing the menu header for the current menu.
    */
  protected String currentMenuHeader = null;

  /**
    * String containing the header message for the menu which is overwritten by the 
    * specific menu.
    */
  protected String menuHeaderMessage = null;

  /**
    * Most recent ActionStatusEvent containing call information
    */
  protected ActionStatusEvent actionStatusEvent = null;
  
    /**
    * Constant used to retrieve the tdd state table name from the Properties
    */ 
  public static final String CONFIG_TDD_STATE_TABLE_NAME = "TDDStateTableName";
  
  /**
    * Constant used to retrieve the tdd state table entry point from the Properties
    */ 
  public static final String CONFIG_TDD_STATE_TABLE_ENTRY_POINT = "TDDStateTableEntryPoint";
  
  /**
    * Constant used to instruct state table to get data
    */ 
  public static final String INSTRUCTION_GET_KEY = "GK";
  
  /**
    * State table bean to invoke DT state tables
    */
  private StateTable stateTable = new StateTable();
  
  /**
    * Variable which indicates if user has hungup
    */
  private boolean userHungup = false;
  
  /**
    * Variable which indicates if user exceeded maximum attempts
    */
  private boolean entryTimeout = false;
  
  /**
    * Indicator for failure due to system error
    */
  private boolean userFailure = false;
  
  /**
    * String array used to pass data to state table
    */
  private String [] stringArray = null;
  
  /**
    * This method constructs a state table object and configures it
    * with the passed configuration. .
    *
    * @param properties
    *     Properties object containing the configuration
    * @throws ConfigurationException
    *     if the Properties object does not contain the appropriate properties
    */
  public void initConfiguration(Properties properties) throws ConfigurationException{
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.INFO,"TDDMenu.initConfiguration: Entered method.");
    
    //call parent's initConfiguration method    
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.initConfiguration: Calling parent class initConfiguration method.");
    super.initConfiguration(properties);
    
    //create an instance of TDDInteraction object to play header message of the menu  
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.initConfiguration: Creating an instance of TDDInteraction object.");
    tddInteraction = new TDDInteraction();
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.initConfiguration: Setting logger in TDDInteraction object.");
    tddInteraction.initLoggerIF(logger);
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.initConfiguration: Configuring TDDInteraction object.");
    tddInteraction.initConfiguration(properties);
    // configure the state table bean
    //add listeners to state table bean
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDInteraction.initConfiguration: Adding listeners to state table bean.");
    stateTable.addDoneListener(this);
    stateTable.addFailedListener(this);
    stateTable.addHungupListener(this);
    String stateTableName = validateStringProperty(properties, CONFIG_TDD_STATE_TABLE_NAME, true);
    String stateTableEntryPoint = validateStringProperty(properties, CONFIG_TDD_STATE_TABLE_ENTRY_POINT, true);
    stateTable.setName(stateTableName);
    stateTable.setEntryPoint(stateTableEntryPoint);
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDMenu.initConfiguration: Leaving method.");
  }//end initConfiguration
  
  
  
  /**
    * This method calls the parent class's setMenuOptions and then extracts additional 
    * menu-specific configuration from the menu Properties object.    
    * These Properties will override any values obtained during initConfiguration.  
    *
    * @param menuOptions
    *     Properties object containing the menu options configuration for the menu
    * @throws ConfigurationException
    *     if the Properties object does not contain the appropriate 
    */
  public void setMenuOptions(Properties menuOptions) throws ConfigurationException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDMenu.setMenuOptions: Entered method.");
    
    // check menuOptions for null
    if(menuOptions == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDMenu.setMenuOptions: Properties passed to this method are null");
      throw new ConfigurationException("TDDMenu.setMenuOptions: Properties passed to this method are null");
    } 
    
    //call parent's setMenuOptions method   
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.setMenuOptions: Calling AbstractMenu's setMenuOptions method.");
    super.setMenuOptions(menuOptions);
    
    //read header messages from menuOptions properties
    currentMenuHeader = menuOptions.getProperty(CONFIG_HEADER_MESSAGE);
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.setMenuOptions: Header message in menuOptions properties is ["+currentMenuHeader+"].");
    if(currentMenuHeader == null){
      // if this is null, set it to empty string
      currentMenuHeader = "";  
    }
    
    //read footer message from menuOptions properties
    String footerMessage = menuOptions.getProperty(CONFIG_FOOTER_MESSAGE);
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.setMenuOptions: Footer message in menuOptions properties is ["+footerMessage+"].");
    if(footerMessage == null){
      // if this is null, set it to empty string
      footerMessage = "";  
    }
        
      
    // get timeoutString
    String timeoutString =  new Integer(timeout).toString();
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.setMenuOptions: Timeout is : ["+timeoutString+"]");
    
    // get maximumAttemptsString
    String maximumAttemptsString =  new Integer(maximumAttempts).toString();
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.setMenuOptions: Max attempts is : ["+maximumAttemptsString+"]");
    
    // get size of menu selection
    int menuItemCount = menuSelection.size();
    String menuItemCountString =  new Integer(menuItemCount).toString();
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.setMenuOptions: Number of menu items is : ["+menuItemCountString+"]");
       
    
    // Create the array of strings to pass to the state table
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.setMenuOptions: Creating an array to talk to state table.");
    stringArray = new String [25];
            
    stringArray[0] = INSTRUCTION_GET_KEY;
    stringArray[1] = timeoutString;
    stringArray[2] = maximumAttemptsString;
    stringArray[3] = menuItemCountString;
    stringArray[4] = "";
    stringArray[5] = "";
    stringArray[6] = "";
    stringArray[7] = "";
    stringArray[8] = "";
    stringArray[9] = footerMessage;
    stringArray[10] = invalidMessage;
    stringArray[11] = systemErrorMessage;
    stringArray[12] = hostErrorMessage;
    stringArray[13] = timeoutMessage;
    stringArray[14] = "";
    stringArray[15] = "";
    stringArray[16] = "";
    stringArray[17] = "";
    stringArray[18] = "";
    stringArray[19] = "";
    stringArray[20] = "";
    stringArray[21] = "";
    stringArray[22] = "";
    stringArray[23] = "";
    stringArray[24] = "";  
      
      
    String validKeys = "";
    String optionMessage = "";
    
    // get the menu items
    for(int i = 0; i < menuItemCount; i++){
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.setMenuOptions: Getting menu item ["+i+"]");
      // get the element
      Option option = (Option) menuSelection.elementAt(i);
      
      // get the key
      validKeys = validKeys + option.getOptionKey();
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.setMenuOptions: Valid key for menu item ["+i+"] is ["+option.getOptionKey()+"]");
      
      // set the segment for the statetable
      Vector segments = option.getOptionSegment();
      // get the elements
      for(int l =0; l < segments.size(); l++){
        String segment = (String) segments.elementAt(l);
        if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.setMenuOptions: Segment ["+l+"] for menu item ["+i+"] is ["+segment+"]");
        optionMessage = optionMessage + " " + segment;
      } 
      int itemIndex = i + 14;
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.setMenuOptions: Setting menu item ["+i+"] in state table array index ["+itemIndex+"]");
      stringArray[itemIndex] = optionMessage;
    }
    
    // set the valid keys
    stringArray[4] = validKeys;
      
    stateTable.setParameters(stringArray);
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDMenu.setMenuOptions: Leaving method.");
        
  }//end setMenuOptions
  
  
  
  
  /**
    * This method then sets the header segment for the menu,  
    * presents the menu options to the caller and obtains a response.  
    * The response returned from this method will be used as the key to look up the 
    * action to take in the menuSelection table.  This call can return 4 types of objects:
 		*    MenuOption - create a NextAction object with the menu specified in the MenuOption
    *    TransferOption - create a NextAction object with the transfer number and segment specified
    *    RepeatOption - repeat setCallDetails, getMenuSelection, and menuSelection lookup.
    *    BusinessOption - call executeBusinessFunction and return the resulting NextAction object.
    * If getMenuSelection returns null (caller timed out or max tries exceeded), 
    * a NextAction object specifying a transfer with the configured transfer number and 
    * segment should be returned from this method.
    *
    * @param session
    *     Hashtable with call information
    * @param actionStatusEvent
    *     ActionStatusEvent containing the latest call information 
    * @return
    *     contains the next action to be executed by the application
    */
  public NextAction presentMenu(Hashtable session, ActionStatusEvent actionStatusEvent){
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDMenu.presentMenu: Entered method.");
    //declare local objects and variables
    NextAction nextAction = null;
    MenuOption menuOption = null;
    TransferOption transferOption = null;
    BusinessOption businessOption = null;
    String transferMessage = null;
    
    try{
      //null check on the session HashTable
      if(session == null){
        if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDMenu.presentMenu: Session HashTable passed in as null to the method.");
        transferMessage = systemErrorMessage;
        throw new TransferException("TDDMenu.presentMenu: Session HashTable passed in as null to the method.");
      }
      if(actionStatusEvent == null){
        if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDMenu.presentMenu: ActionStatusEvent passed in as null to the method.");
        transferMessage = systemErrorMessage;
        throw new TransferException("TDDMenu.presentMenu: ActionStatusEvent passed in as null to the method.");
      }
      
      //call setCallDetails to set the header segment for the menu
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.presentMenu: Calling setCallDetails to get the header message for the menu.");
      
      try{
        menuHeaderMessage = setCallDetails(session, null);
      }
      catch(TransferException te){
        if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDMenu.presentMenu: Could not build header message : " + te.toString());
        transferMessage = systemErrorMessage;
        throw new TransferException("TDDMenu.presentMenu: Could not build header message : " + te.toString());
      }
       
      //call getMenuSelection to present the menu options to the caller and obtain a response
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.presentMenu: Calling getMenuSelection to present the menu options to the caller and obtain a response.");
      String selection = getMenuSelection(actionStatusEvent, session);
      if (userFailure ==true) {
        if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"DTMFMenu.presentMenu: User failed to input selection.");
        nextAction = super.createTransferAction(null);
        //before leaving method reset class level variable userFailure
        userFailure = false;
        throw new TransferException("DTMFMenu.presentMenu: User failed to input selection.");
      } 
      if (userHungup ==true) {
        if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"DTMFMenu.presentMenu: User hungup.");
        userHungup = false;
        throw new HungupException("DTMFMenu.presentMenu: User hungup.");
      }       
      if((selection == null) || (selection.length() == 0)){
        if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"DTMFMenu.presentMenu: User selection is retrieved as null.");
        nextAction = super.createTransferAction(systemErrorMessage);
        //before leaving method reset class level variable userFailure
        throw new TransferException ("DTMFMenu.presentMenu: User selection is retrieved as null.");
      } 
       
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.presentMenu: User selection is ["+selection+"]");
              
      //use the response returned as key to look up action to take in the menuSelection table
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.presentMenu: Looking up option object in menuSelection table.");
      Option optionObject = (Option) super.menuSelection.get(selection);
      if(optionObject == null){
        if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDMenu.presentMenu: Unable to get Option object from menu selection using user selection ["+selection+"] as key.");
        transferMessage = systemErrorMessage;
        throw new TransferException("TDDMenu.presentMenu: Unable to get Option object from menu selection using user selection ["+selection+"] as key.");
      }      
      
      //identify option object
      int option = optionObject.getOptionType();
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.presentMenu: Option type is ["+option+"]");
      if(option == Option.OPTION_MENU){
        if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.presentMenu: MenuOption is selected.");
        //typecast Option object into MenuOption object
       
        if ( ! (optionObject instanceof MenuOption) ){
          if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDMenu.presentMenu: Unable to type cast Option object into a MenuOption object.");
          transferMessage = systemErrorMessage;
          throw new TransferException("TDDMenu.presentMenu: Unable to type cast Option object into a MenuOption object.");
        }
        
        menuOption = (MenuOption) optionObject;
     
        //get the menu name
        String menuName = menuOption.getMenuName();
        if((menuName == null) || (menuName.length() == 0)){
          if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDMenu.presentMenu: Unable to get menu name from MenuOption.");
          transferMessage = systemErrorMessage;
          throw new TransferException("TDDMenu.presentMenu: Unable to get menu name from MenuOption.");
        }       
        if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.presentMenu: Selected menu is ["+menuName+"]");
            
        //create a NextAction object with the menu specified
        if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.presentMenu: Creating a NextAction object.");
        nextAction = new NextAction();
        //set return action on NextAction as Menu
        if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.presentMenu: Setting return action of the NextAction object as ["+NextAction.RETURN_ACTION_MENU+"].");
        nextAction.setReturnAction(NextAction.RETURN_ACTION_MENU);
        //set menu name on Next action
        if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.presentMenu: Setting menu name ["+menuName+"] in NextAction object.");
        nextAction.setMenuName(menuName);
      }  
      else if(option == Option.OPTION_TRANSFER){
        if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.presentMenu: TransferOption is selected.");
        //typecast Option object into TransferOption object
        if ( ! (optionObject instanceof TransferOption) ){
          if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDMenu.presentMenu: Unable to type cast Option object into a TransferOption object.");
          transferMessage = systemErrorMessage;
          throw new TransferException("TDDMenu.presentMenu: Unable to type cast Option object into a TransferOption object.");
        }            
        transferOption = (TransferOption) optionObject;
         
        //get transfer number and segment 
        String transferNo = transferOption.getTransferNumber();
        if((transferNo == null) || (transferNo.length() == 0)){
          if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDMenu.presentMenu: Unable to get transfer number from TransferOption object.");
          //create a transfer next action with system error
          transferMessage = systemErrorMessage;
          throw new TransferException("TDDMenu.presentMenu: Unable to get transfer number from TransferOption object.");
        }       
        if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.presentMenu: Transfer number is ["+transferNo+"]");
        String transferSeg = transferOption.getTransferSegment();
        
        //create a NextAction object with the transfer number and segment specified
        if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.presentMenu: Creating a transfer NextAction object.");
        nextAction = createTransferAction(super.transferSegment);
      }    
      else if(option == Option.OPTION_REPEAT){
        if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.presentMenu: RepeatOption is selected.");
        //create a NextAction object 
        if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.presentMenu: Creating a NextAction object.");
        nextAction = new NextAction();
        //set return action on NextAction as Repeat
        if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.presentMenu: Setting return action of the NextAction object as ["+NextAction.RETURN_ACTION_REPEAT+"].");
        nextAction.setReturnAction(NextAction.RETURN_ACTION_REPEAT);
      }      
      else if(option == Option.OPTION_BUSINESS){
        if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.presentMenu: BusinessOption is selected.");
        //typecast Option object into BusinessOption object
        if (!(optionObject instanceof BusinessOption)){
          if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDMenu.presentMenu: Option cannot be cast to business option");
          //create a transfer next action with system error
          transferMessage = systemErrorMessage;
          throw new TransferException("TDDMenu.presentMenu: Option cannot be cast to business option");
        }  
        
        businessOption = (BusinessOption) optionObject;
       
        //get class name
        String className = businessOption.getClassName();
        if((className == null) || (className.length() == 0)){
          if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDMenu.presentMenu: Unable to get class name from BusinessOption object.");
          //create a transfer next action with system error
          transferMessage = systemErrorMessage;
          throw new TransferException("TDDMenu.presentMenu: Unable to get class name from BusinessOption object.");
        }       
        if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.presentMenu: Class name is ["+className+"]");
        //call executeBusinessFunction and return the resulting NextAction object
        if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.presentMenu: Calling executeBusinessFunction super method.");
        nextAction = super.executeBusinessFunction(session, actionStatusEvent, className);   
      }        
      else{
        if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDMenu.presentMenu: Unidentified option type received.");
        //create a transfer next action with system error
        transferMessage = systemErrorMessage;
        throw new TransferException("TDDMenu.presentMenu: Unidentified option type received.");
      }
    }
    catch (HungupException he) {
      //next action is end
      if (logLevel >= LoggerIF.WARNING) log(LoggerIF.WARNING,"TDDMenu.presentMenu: HungupException caught, setting Next action to ["+NextAction.RETURN_ACTION_END+"].");
      nextAction = new NextAction();
      nextAction.setReturnAction(NextAction.RETURN_ACTION_END);
    } 
    catch (TransferException te) {
      //super.createTransferAction already set nextAction
      //just need to leave method
      if (logLevel >= LoggerIF.WARNING) log(LoggerIF.WARNING,"TDDMenu.presentMenu: TransferException caught,  Next action was set to ["+NextAction.RETURN_ACTION_TRANSFER+"].");
    }
    catch (FailureTransferException te) {
      //super.createTransferAction already set nextAction
      nextAction = super.createTransferAction(super.systemErrorMessage);
      if (logLevel >= LoggerIF.WARNING) log(LoggerIF.WARNING,"TDDMenu.presentMenu: FailureTransferException caught,  Transfer with system error");
    }
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDMenu.presentMenu: Returning NextAction object and leving method.");
    return nextAction;
  }//end presentMenu


  

  /**
    * This method exists so that children classes can override it as a way of 
    * presenting custom header messages to the caller for certain menus.  
    * This implementation of the method does not do anything.
    *
    * @param session
    *     Hashtable for the call
    * @param callLocale
    *     Locale object for the call
    *
    * @return header message
    *
    * @throws TransferException if there is a problem building the header message
    */
  protected String setCallDetails(Hashtable session, Locale callLocale) throws TransferException{
    return currentMenuHeader;  
  }//end setCallDetails
  

  /**
    * This method retrieves the selection from the current menu object.  
    * This method returns null if the menu was unable to obtain a selection 
    * from the user (max tries exceeded or timeout).
    *
    * @param actionStatusEvent
    *     ActionStatusEvent containing latest call information
    * @param session
    *     Hashtable containing session for call
    * @return
    *     selection made by caller,
    *
    * @throws FailureTransferException if an unknown failure occurs
    */
  protected String getMenuSelection(ActionStatusEvent actionStatusEvent, Hashtable session) throws FailureTransferException{
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDMenu.getMenuSelection: Entered method.");
    //declare method variables
    String selection = null;
    String returnCode = null;
    String returnData = null;
    
    if(actionStatusEvent == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDMenu.getMenuSelection: ActionStatusEvent passed to this method is null.");  
      throw new FailureTransferException("TDDMenu.getMenuSelection: ActionStatusEvent passed to this method is null.");
    }
    
    if(session == null){
      if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDMenu.getMenuSelection: Session passed to this method is null.");  
      throw new FailureTransferException("TDDMenu.getMenuSelection: Session passed to this method is null.");
    }
    
    //call TDDInteraction's playMedia method to play the header message of the menu
    try{
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.getMenuSelection: Playing menu header message by using TDDInteraction object.");
      tddInteraction.playMedia(actionStatusEvent, menuHeaderMessage);
    }
    catch(HungupException he){
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.getMenuSelection: HungupException caught while playing header message for the menu.");
      userHungup = true;  
    }
      
    if(!userHungup){
     
      //set the string array created in this class in the stateTable object
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.getMenuSelection: Setting array of input parameters in stateTable object.");
      stateTable.setParameters(stringArray);
      
      //call action on state table to play segments and retrieve input
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.getMenuSelection: Calling action on StateTable bean.");
      stateTable.action(actionStatusEvent);
      
      // get the return status
      returnCode = stringArray[23];
      returnData = stringArray[24];
      
      userHungup = false;
      entryTimeout = false;
      
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.getMenuSelection: Return code : ["+returnCode+"]. Return data : ["+returnData+"].");
      
      // check return code for null
      if(returnCode == null){
        if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDMenu.getMenuSelection: Return code is null.");
      }
      else{
        if(returnCode.equals("U")){
          if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.getMenuSelection: Caller has hung up. Throwing HungupException.");
          userHungup = true;
        }
        else if(returnCode.equals("H") || returnCode.equals("E")){
          if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDMenu.getMenuSelection: System or host error on state table.");
        }
        else if(returnCode.equals("P") || returnCode.equals("M")){
          if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDMenu.getMenuSelection: Timeout or invalid entry");
          entryTimeout = true;
        }
        else if(returnCode.equals("OK")){
          if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu.getMenuSelection: GD successful");
          //set returnData to selection
          if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.NOTICE,"TDDMenu.getMenuSelection: Setting ["+returnData+"] as user selection.");
          selection = returnData;
        }
        else{
          if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR,"TDDMenu.getMenuSelection: Unknown return code");
        }
      }
      
      if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.NOTICE,"TDDMenu.getMenuSelection: User selection is : ["+selection+"].");
    }    
    //set menuHeaderMessage back to null since it's a class level variable
    menuHeaderMessage = null;
             
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDMenu.getMenuSelection: Leaving method.");
    return selection;
  }//end getMenuSelection

  /**
    * DoneListener's method, listens for done events raised by action beans.
    *
    * @param e
    *     ActionStatusEvent containing the latest call information 
    */
  public void done(ActionStatusEvent e) {
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu:done: Source for done is: ["+e.getSource().toString()+"]");
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu:done: Completion code for done is: "+e.getCompletionCode()+"]");
    // save event for the next bean
    actionStatusEvent = e;
  } 
  
  /**
    * FailedListener's method, listens for failed events.
    *
    * @param e
    *     ActionStatusEvent containing the latest call DEBUGrmation 
    */    
  public void failed(ActionStatusEvent e) {
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDMenu.failed: Entering method.");
    
    if (logLevel >= LoggerIF.ERR) log(LoggerIF.DEBUG,"TDDMenu:failed: Source for failed is: "+e.getSource().toString()+"]");
    userFailure = true;
    if (logLevel >= LoggerIF.ERR) log(LoggerIF.DEBUG,"TDDMenu:failed: Completion code for failed is: "+e.getCompletionCode()+"]");
        
    // save event for the next bean
    actionStatusEvent = e;
    
    if (logLevel >= LoggerIF.INFO) log(LoggerIF.INFO,"TDDMenu.failed: Leaving method.");
  }
  
  /**
    * HungupListener's method, listens for hungup events.
    *
    * @param e
    *       HungupEvent Indicates that the user has hung up.
    */
  public void hungup(HungupEvent e) {
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu:hungup: Source for hungup is: "+e.getSource().toString()+"]");
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu:hungup: Completion code for hungup is: "+e.getCompletionCode()+"]");
    //set class variable userHungUp
    if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"TDDMenu:failed: Setting userHungUp flag to true.");
    userHungup = true;
  }      
  
}//end TDDMenu class