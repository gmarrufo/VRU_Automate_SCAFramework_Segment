package com.viecore.pvs;


/*
 * Licensed Materials - Property of Viecore Inc.
 *
 * (C) Copyright Viecore Inc. 2002 All Rights Reserved
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
import com.viecore.util.database.*;
import com.viecore.util.*;
import com.viecore.core.*;

import com.viecore.pvs.menu.*;
import com.viecore.pvs.business.*;
import com.viecore.pvs.menu.*;
import com.viecore.pvs.dtmf.*;
import com.viecore.pvs.transaction.*;



/**
 * This class will be the main thread of the application.  
 * It is the main entry point into the application.  
 * Each instance of this class will handle a DirectTalk node.  
 * 
 * @author Susan Hammond
 * @since JDK 1.3
 * @version 1.0  12/18/2002
 */
 
public class ViolationSystem extends Voicelet implements VoiceletListener, DoneListener, FailedListener,
                                                 HungupListener, UncaughtFailedListener,
                                                 UncaughtHungupListener{
                                                 
  /**
    * Reference to the logger
    */
    private LoggerIF logger = null;
    
  /**
    * Log level retrieved from the logger.
    */
    private int logLevel = LoggerIF.OFF;
    
  /**
    * Reference to a DirectTalk bean used to receive calls from the user
    */
    private DirectTalk directTalk = null;
    
  /**
    * Reference to the INIFile which will store the configuration 
    * for the application and menus
    */
    private INIFile iniFile = null;
    
  /**
    * Reference to the most current ActionStatusEvent
    */
    private ActionStatusEvent actionStatusEvent = null;
    
  /**
    * Reference to the table of menus, keyed by the menu name
    */
    private Hashtable menuTable = null;
    
  /**
    * String representing whether the node is configured for TDD or DTMF
    */
    private String presentation = "DTMF";
    
  /**
    * Boolean indicating if caller has hungup
    */
    private boolean hungup = false;
    
  /**
    * Boolean indicating if application is shutting down
    */
    private boolean shutdown = false;
    
  /**
    * Properties object containing the main configuration values
    */
    private Properties applicationProperties = null;
      

    
   
  /**
    * Constant used to retrieve the application properties from the INIFile object
    */
    public static final String CONFIG_MAIN_SECTION = "ApplicationConfig";
    
  /**
    * Constant used to retrieve the presentation type, DTMF or TDD from the Properties object
    */
    public static final String CONFIG_PRESENTATION = "Presentation";
    
  /**
    * Constant used to retrieve the configuration file path from the DirectTalk properties
    */
    public static final String CONFIG_FILE_PATH = "IniFilePath";
    
  /**
    * Constant used to retrieve menu configurations from application 
    * config - Menu1, Menu2,….,Menun.
    */
    public static final String CONFIG_MENU = "Menu";
    
  /**
    * Constant used to retrieve the default 
    * host error segment from the application properties and to set 
    * the host error segment in the menu properties
    */
    public static final String CONFIG_MENU_IMPLEMENTATION = "MenuImplementation";
   
  /**
    * Constant used to set the transfer number in the session
    */
    public static final String SESSION_TRANSFER_NUMBER = "TransferNumber";
    
  /**
    * Constant used to set the transfer segment in the session
    */
    public static final String SESSION_TRANSFER_SEGMENT = "TransferSegment";                                           
  
  
  /**
    * Reference to a Welcome object used to play the welcome message
    */  
    private Welcome welcome = null;                                             
  
  /**
    * Reference to a LogEventTransaction object used to create event reports
    */  
    private LogEventTransaction logEventTransaction = null;                                             
            
         
  /**   
    * This contsructor is called by DirectTalk to start the PVS application on a node. 
    * This method will create an instance of ViolationSystem, 
    * and registers this class listeners,
    * 
    *  
    */ 
  public ViolationSystem() {
    System.out.println("ViolationSystem.ViolationSystem In Constructor.");  
    try{
      addVoiceletListener(this);
		  directTalk = new DirectTalk();   
      directTalk.addDoneListener(this);   
      directTalk.addFailedListener(this);    
      directTalk.addHungupListener(this); 
      directTalk.addUncaughtFailedListener(this);
      directTalk.addUncaughtHungupListener(this);
		}  
		catch (Exception e) {
      System.out.println( "ViolationSystem.ViolationSystem: Exception made in constructor["+e.toString()+"]."); 
      throw new IllegalStateException("ViolationSystem.ViolationSystem: Exception made in constructor["+e.toString()+"].");
    }//end catch  	  
  }//end constructor  
    
   
  /**
    *  
    * This method obtains the ApplicationProperties object.
    * The values obtained are used to make an INIFile Object, initialize the 
    * LoggerIF and database pool, and retrieve and store the
    * "Presentation" and "MainMenu" values
    *    
    * @throws ConfigurationException  
    * if the business object cannot be configured correctly.
    */
  private void getConfiguration() throws ConfigurationException{ 
    System.out.println("ViolationSystem.getConfiguration. Entering method.");
    String iniFileName = null;
      
    // get the directTalk applicationProperties
    System.out.println("ViolationSystem.getConfiguration. About to get properties.");
    ApplicationProperties directTalkApplicationProperties = getApplicationProperties();
    if (directTalkApplicationProperties == null){
      throw new ConfigurationException("ViolationSystem.getConfiguration: directTalkApplicationProperties is null."); 
    }   
    System.out.println("ViolationSystem.getConfiguration. retrieved properties.");
           
    //get the INIFile path 
    System.out.println(directTalkApplicationProperties);
    iniFileName = directTalkApplicationProperties.getParameter(CONFIG_FILE_PATH); 
    System.out.println("ViolationSystem.getConfiguration. retrieved iniFilePath of ["+iniFileName+"]");
    if (iniFileName == null){
      throw new ConfigurationException("ViolationSystem.getConfiguration: iniFileName is null."); 
    }  
    try{  
      // Make an INIFile object
      iniFile = new INIFile(iniFileName); 
      System.out.println("ViolationSystem.getConfiguration. made iniFile Object");
      applicationProperties = (Properties)iniFile.getSection(CONFIG_MAIN_SECTION);      
      System.out.println("ViolationSystem.getConfiguration. retrived ["+CONFIG_MAIN_SECTION+"].");	
    }   
    catch (IOException ioe){
      System.out.println("ViolationSystem.getConfiguration. INIfile does not exist, or does not have the proper permissions. ["+ioe.toString()+"]."); 
      throw new ConfigurationException("ViolationSystem.getConfiguration: INIfile does not exist, or does not have the proper permissions. ["+ioe.toString()+"]."); 
    }
    catch (INIFileException ife){
      System.out.println("ViolationSystem.getConfiguration. Exception thrown making the iniFile ["+ife.toString()+"]."); 
      throw new ConfigurationException("ViolationSystem.getConfiguration: Exception thrown making the iniFile ["+ife.toString()+"]."); 
    }
    
    // create a logger reference based on specifications in ini file and retrieve its designated log level
    try{                                   
    	
    	LoggerFactory logFactory = new LoggerFactory();
    	logger = logFactory.createLogger(applicationProperties);
    	System.out.println("Logger created");
    	System.out.println(logger);
    	logLevel = logger.getLogLevel();
    	System.out.println(logLevel);
    	if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.getConfiguration: logLevel is ["+logLevel+"]."); 
    } 	
    catch (FactoryException fe){
      System.out.println("ViolationSystem.getConfiguration: Factory Exception caught while making Logger["+fe.toString()+"]."); 
      throw new ConfigurationException("ViolationSystem.getConfiguration: Factory Exception caught while making Logger ["+fe.toString()+"]."); 
    } 
    
    try{ 	
      DBPoolFactory dbPoolFactory = new DBPoolFactory();
      dbPoolFactory.initLoggerIF(logger);  	
      DatabaseConnectionPoolIF databaseConnectionPoolIF = dbPoolFactory.getDatabaseConnectionPool(applicationProperties);  	
    }
    catch (FactoryException fe){
      if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "ViolationSystem.getConfiguration: Factory Exception caught while making DatabaseConnectionPoolIF.["+fe.toString()+"]."); 
      throw new ConfigurationException("ViolationSystem.getConfiguration: Factory Exception caught while making DatabaseConnectionPoolIF. ["+fe.toString()+"]."); 
    }     	
    
    
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.getConfiguration: About to retrieve presentation type from applications properties"); 
 
    
    presentation = validateStringProperty(applicationProperties,CONFIG_PRESENTATION);  
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.getConfiguration: Presentation type is : ["+presentation+"]"); 
    
    //instantiate a Welcome object 
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.getConfiguration: Instantiating a Welcome object."); 
    welcome = new Welcome();
    welcome.initLoggerIF(logger);
    welcome.initConfiguration(applicationProperties); 
    
    //instatiate a LogEventTransaction object, just log any type of error    
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.getConfiguration: Instantiating a LogEventTransaction object."); 
    logEventTransaction = new LogEventTransaction();
    logEventTransaction.initLoggerIF(logger);
    logEventTransaction.initConfiguration(applicationProperties); 
        
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "ViolationSystem.getConfiguration: Ending method");
  }//end getConfiguration method  
              
         
  /**                                                                                           
    * This method is called by DirectTalk, after calling the constructor,
    * to start the PVS application on a node.                             
    * This method will last the duration of the application, until the variable                 
    * shutdown is set to true.                                                                  
    *                                                                                           
    * @param actionStatusIn                                                             
    * ActionStatusEvent, start application event passed from DirectTalk                                          
    */           
    public void startApplication(ActionStatusEvent actionStatusIn){            
      System.out.println(" In StartApplication with actionStatusEvent: ["+actionStatusIn.toString()+"]");   
      NextAction nextAction = null;
      MenuIF menuIF = null;
      boolean firstTime = true;
            
      try{    
        this.getConfiguration(); 
      }   
      catch (ConfigurationException ce) {
        if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "ViolationSystem.startApplication: ConfigurationException made in getConfiguration["+ce.toString()+"].");         
        throw new IllegalStateException("ViolationSystem.startApplication:ConfigurationException made in getConfiguration["+ce.toString()+"].");
      }//end catch
      try{ 
        if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.startApplication: about to call loadMenuBuisinessItems.");      
        this.loadMenuBusinessItems();
        if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.startApplication: returned from loadMenuBuisinessItems."); 
      }  
      catch (ConfigurationException ce) {
        if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "ViolationSystem.startApplication: ConfigurationExcetion made in loadMenuBusinessItems["+ce.toString()+"]."); 
        throw new IllegalStateException("ViolationSystem.startApplication:ConfigurationException made in loadMenuBusinessItems["+ce.toString()+"].");
      }//end catch  
      
      
      // system in steady state with everything built.  Waiting for call.  
      while (shutdown == false){
        try{
          hungup=false;
          // session for call
          Hashtable session = new Hashtable();  
          actionStatusEvent = null;         
  
          if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.startApplication: Waiting for call");
          waitForCall(actionStatusIn);        
          if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.startApplication: Received call");         
          
          //add one to total calls in report
          logEventTransaction.logEvent(actionStatusEvent, logEventTransaction.TOTAL_CALLS);
          
          nextAction = welcome.process(session, actionStatusEvent);
          if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.startApplication: Returned from welcome.process with next action : ["+nextAction.getReturnAction()+"]");         
            
          while (hungup==false){
            if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.startApplication:  next action is : ["+nextAction.getReturnAction()+"]");         
            int actionType = nextAction.getReturnAction();
          
            //menu 0, repeat 1, transfer 2, goodbye 3
            switch(actionType){          
              case NextAction.RETURN_ACTION_MENU:  
                if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.startApplication:In case ["+NextAction.RETURN_ACTION_MENU+"]");           
                String menuName = nextAction.getMenuName();
                if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.startApplication: Retrieved: ["+menuName+"].");
                try{
                  menuIF = getMenu(menuName);
                  if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.startApplication: assigned menuIF");
                }  
                catch (NoSuchElementException nsee){
                  if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "ViolationSystem.startApplication: NoSuchElementException returned from getMenuu. ["+nsee.toString()+"]."); 
                  //something is wrong, try to transfer this call
                  try{
                    transfer(actionStatusEvent, session, nextAction); 
                  } 
                  catch (ConfigurationException ce){
                    if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "ViolationSystem.startApplication: Exception returned from transfer. ["+ce.toString()+"].");   
                    try{            
                      goodbye(actionStatusIn,session); 
                    }  
                    catch (ConfigurationException cce){
                      if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "ViolationSystem.startApplication: Exception returned from goodbye. ["+cce.toString()+"].");             
                    }  
                  }              
                }  
           
                if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.startApplication: about to present menu");
                nextAction = menuIF.presentMenu(session, actionStatusEvent);
                if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.startApplication: returned from presentMenu");
         
             
                break;
            
              case NextAction.RETURN_ACTION_REPEAT: 
                // repeat the action  
                if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.startApplication:In case ["+NextAction.RETURN_ACTION_REPEAT+"].");         
                nextAction = menuIF.presentMenu(session, actionStatusEvent);
                break;
            
              case NextAction.RETURN_ACTION_TRANSFER:
                if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.startApplication:In case ["+NextAction.RETURN_ACTION_TRANSFER+"].");         
       
                try{       
                  nextAction = transfer(actionStatusEvent, session, nextAction);             
                }
                catch (ConfigurationException ce){
                  if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "ViolationSystem.startApplication: Exception returned from transfer. ["+ce.toString()+"].");   
                  try{             
                    goodbye(actionStatusEvent,session); 
                  }  
                  catch (ConfigurationException cce){
                    if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "ViolationSystem.startApplication: Exception returned from goodbye. ["+cce.toString()+"].");             
                  }   
                }    
                break;
                      
              case NextAction.RETURN_ACTION_GOODBYE:
                if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.startApplication:In case ["+NextAction.RETURN_ACTION_GOODBYE+"]."); 
                hungup=true;  
                try{           
                  goodbye(actionStatusEvent,session); 
                }
                catch (ConfigurationException ce){
                  if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "ViolationSystem.startApplication: Exception returned from goodbye. ["+ce.toString()+"].");             
                }  
                break;
              
              
             case NextAction.RETURN_ACTION_END:
                if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.startApplication:In case ["+NextAction.RETURN_ACTION_END+"]."); 
                hungup=true;  
            
                break;
              default:
                //this means enrollment action value is -1 (i.e. empty)
                if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"ViolationSystem.startApplication: Default case.");
              
                break;  
            }//end switch        
          }//end while (hungup==false)
        
        } 
        catch (Exception e){
          // here to catch null pointer or other uncaught exceptions to end this call and start next call
          if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"ViolationSystem.startApplication: Uncaught error, going to end call["+e.toString()+"].");     
        }
        if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.startApplication: Returning call to system to wait for another call.");
        returnCall(actionStatusEvent);             
      }//end while (shutdown == false) 
        
    //shutdown system
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.startApplication: ** System shutdown. **");
  }//end StartApplication    
    
  
  
  /**
    * This method retrieves a MenuIF from the menuTable with the given String 
    *  
    * @param menuName 
    * String representing the name of the menu 
    *
    * @return 
    * item stored in the hashtable 
    *
    * @throws NoSuchElementException  
    * if the menu Item is not in the hashtable
    */
  private MenuIF getMenu(String menuName) throws NoSuchElementException{
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "ViolationSystem.getMenu: Entered method.");
    MenuIF menuIF=null;
       
    menuIF = (MenuIF)menuTable.get(menuName);
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.getMenu: retrieved menuIF");
   
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "ViolationSystem.getMenu: Exiting method.");  
    return menuIF;
     
  }//end getMenu
  
  

  
  
  
  
  /**
    *  This method will get the default menu options from the application properties.  
    * In the cases where this data is not specified for each menu, the defaults will be used. 
    * This method will iterate through the application properties for menu entries. 
    * Menu1, Menu2, ……, Menun (where n is equal to the number of menus configured).  
    * For each menu entry in the application properties:
    *   1)	Extract the name of the menu (Menun)
    *   2)	Use the name of the menu to retrieve the menu properties from the INIFile
    *   3)	Extract the menu implementation from the menu properties.
    *   4)	Create/configure the MenuIF and store in the Menu Hashtable with the name as the key.  
    * This method calls initConfiguration with the application Properties and calls setMenuOptions 
    * with Properties for the menu (from step 2).
    *
    * @throws ConfigurationException  
    * if an exception is thrown loading business items
    *
    */
  private void loadMenuBusinessItems() throws ConfigurationException{
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "ViolationSystem.loadMenuBusinessItems: Entered method.");
    //instantiate menu table
    menuTable = new Hashtable();
    //declare method level variables
    int menuCount = 0;
    boolean isNull = false;
    MenuIF menuIF = null;
    Properties menuProperties = null;
    Class dbObj = null;
    String menuImplementation = null;
  
    while (isNull==false){
      String menuNumber =  new String ( CONFIG_MENU + menuCount );
      if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.loadMenuBusinessItems: About to get menuNumber: ["+menuNumber+"] from applicationsProperties.");
      String menuName = (String)applicationProperties.getProperty(menuNumber); 
      
      //if the menuName is not null, continue to make a MenuIF object
      if ((menuName != null) && (menuName.length() > 0)){
        if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.loadMenuBusinessItems: Extracted menuName: ["+menuName+"] from applicationsProperties.");
        try{
          menuProperties = (Properties)iniFile.getSection(menuName);	
        }  
        catch (INIFileException ife) {
          if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "ViolationSystem.loadMenuBusinessItems: IniFileException  caught while getting sectionName :["+menuName+"] ");        
          throw new ConfigurationException("ViolationSystem.loadMenuBusinessItems: IniFileException  caught while getting sectionName :["+menuName+"].");        
        }          
        try{  
          menuImplementation = validateStringProperty(menuProperties,CONFIG_MENU_IMPLEMENTATION);
        }  
        catch (MissingPropertyConfigurationException mpe) {
          if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "ViolationSystem.loadMenuBusinessItems:MissingPropertyConfigurationException thrown while trying to retrieve presentation ["+mpe.toString()+"]."); 
          throw new ConfigurationException("ViolationSystem.loadMenuBusinessItems: MissingPropertyConfigurationException thrown while trying to retrieve presentation ["+mpe.toString()+"].");
        } 
        //make a menuIF and store each in the hashtable with menuName as key
   
        //making a new object who's object name is menuName 
        try{
          dbObj = Class.forName(menuImplementation);
        }
        catch (ClassNotFoundException cnfe) {
          if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "ViolationSystem.loadMenuBusinessItems: ClassNotFoundException caught while trying to configure the MenuIF object ["+cnfe.toString()+"].");        
          throw new ConfigurationException("ViolationSystem.loadMenuBusinessItems: ClassNotFoundException caught while trying to configure the MenuIF object ["+cnfe.toString()+"].");        
        } 
        //the object is instantiated, then is cast to a abstractMenu and assigned to abstractMenu
        try{
          menuIF = (MenuIF)dbObj.newInstance();   
        } 
        catch (InstantiationException ie) {
          if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "ViolationSystem.loadMenuBusinessItems: InstantiationException caught while trying to configure the MenuIF object ["+ie.toString()+"].");        
          throw new ConfigurationException("ViolationSystem.loadMenuBusinessItems: InstantiationException caught while trying to configure the MenuIF object ["+ie.toString()+"].");        
        }  
        catch (IllegalAccessException iae) {
          if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "ViolationSystem.loadMenuBusinessItems: IllegalAccessException caught while trying to configure the MenuIF object ["+iae.toString()+"].");        
          throw new ConfigurationException("ViolationSystem.loadMenuBusinessItems: IllegalAccessException caught while trying to configure the MenuIF object ["+iae.toString()+"].");        
        }      
        if(menuIF instanceof LoggingIF){
          if (logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG,"ViolationSystem.loadMenuBusinessItems: Setting logger for ["+ menuName +"]");
          if(logger!=null){
            ((LoggingIF)menuIF).initLoggerIF(logger);
          }
          else{
            if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "ViolationSystem.loadMenuBusinessItems: Unable to set logger object for ["+ menuName +"]");
            throw new ConfigurationException("ViolationSystem.loadMenuBusinessItems: Unable to set logger object for ["+ menuName +"].");
          }
        }                             
        if(menuIF instanceof ConfigurableIF){
                    
          // check if abstractMenu implements the ConfigurableIF
          ((ConfigurableIF)menuIF).initConfiguration(applicationProperties);
            
        }//end if config
      
    
        //put the abstractMenu in the session hashtable      
      
        menuIF.setMenuOptions(menuProperties);
    
      
        menuTable.put(menuName,menuIF); 
        if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.loadMenuBusinessItems: Putting ["+menuName+"] in hashtable."); 
    
        
        //increment menuCount
        menuCount++;
      }      
      else{
      if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.loadMenuBusinessItems: There is no menu declared for ["+menuNumber+"] in applicationsProperties.");
        isNull = true;
      }    
    }//end while
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "ViolationSystem.loadMenuBusinessItems: Exiting method.");
  }//end loadMenuBusinessItems
  

  /**
    * Calls the returnCall method on the DirectTalk bean 
    * and cleans up session and variables for next user.
    *
    * @param actionIn  
    * ActionStatusEvent containing latest connection information for call
    *
     */
  private void returnCall(ActionStatusEvent actionIn) {
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "ViolationSystem.returnCall: entering method");
    // not really sure about this method and what it should do
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.returnCall: Returning call to direct talk bean");
    directTalk.returnCall(actionIn); 
    actionStatusEvent = null;
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "ViolationSystem.returnCall: exiting method");
 
  } // end endCall
  
  
  /**
    *  
    * This method first places the passed transfer number and segment 
    * in the session.  This method will construct and configure, and 
    * call process on a Transfer BusinessIF to transfer the call. 
    *  The next action returned from the BusinessIF is returned from this method.  
    *
    * @param actionIn
    * ActionStatusEvent containing latest connection information for call 
    *
    * @param session 
    * Hashtable session for the call 
    *
    * @param nextAction 
    * current NextAction containing the transfer elements  
    *
    * @return 
    * object resulting from process call on Transfer   
    *
    *
    * @throws ConfigurationException  
    * if the business object cannot be configured correctly.
    */
  private NextAction transfer(ActionStatusEvent actionIn, Hashtable session, NextAction nextAction ) throws ConfigurationException{  
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "ViolationSystem.transfer: Entered method.");
    String transferNumber  = nextAction.getTransferNumber();
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.transfer: Transfer number is ["+transferNumber+"].");
    String transferSegment = nextAction.getTransferSegment();
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.transfer: Transfer segment is ["+transferSegment+"].");
    if (transferNumber != null){
      session.put(SESSION_TRANSFER_NUMBER, transferNumber);
    }
    if (transferSegment !=null){  
      session.put(SESSION_TRANSFER_SEGMENT,transferSegment);
    }  
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "ViolationSystem.transfer: About to make transfer."); 
    Transfer transfer = new Transfer(); 
    transfer.initLoggerIF(logger);
    transfer.initConfiguration(applicationProperties); 
    
  
    nextAction = transfer.process(session,actionIn);
   
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "ViolationSystem.transfer: Exiting method.");
    return nextAction;
  
  }//end transfer
  
  
  
    
  /**
    *  
    * This method will construct and configure, and call process 
    * on a Goodbye BusinessIF to speak the goodbye segment to the caller
    * and end the call.  The next action returned from the BusinessIF 
    * is returned from this method. 
    *
    * @param actionIn
    * ActionStatusEvent containing latest connection information for call 
    *
    * @param session
    * Hashtable for the call  
    *
    * @return 
    * object resulting from process call on Goodbye  
    * 
    *
    * @throws ConfigurationException  
    * if the business object cannot be configured correctly.
    */
  private NextAction goodbye(ActionStatusEvent actionIn, Hashtable session) throws ConfigurationException{  
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "ViolationSystem.goodbye: Entered method.");
   
    Goodbye goodbye = new Goodbye();
    
    // hand the logger
    goodbye.initLoggerIF(logger);
    
    goodbye.initConfiguration(applicationProperties); 
     

    NextAction nextAction = goodbye.process(session,actionIn);
    
    hungup = true;
   
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "ViolationSystem.goodbye: Exiting method."); 
    return nextAction;
  
  }//end transfer
  
   
  /**
    *  
    * This method will construct, configure, and call process on a Welcome BusinessIF 
    * to do a database check, display a welcome message and obtain the language for the caller.  
    * The next action returned from the BusinessIF is returned from this method.  
    *
    * @param actionIn
    * ActionStatusEvent containing latest connection information for call 
    *
    * @param session
    * Hashtable for the call 
    *
    * @return 
    * object resulting from process call on Welcome 
    *
    * @throws ConfigurationException  
    * if the business object cannot be configured correctly.
    */
  private NextAction welcome(ActionStatusEvent actionIn, Hashtable session) throws ConfigurationException{  
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "ViolationSystem.welcome: Entered method."); 
 
    welcome = new Welcome();
    welcome.initLoggerIF(logger);
    welcome.initConfiguration(applicationProperties);
      
 
    NextAction nextAction = welcome.process( session, actionIn);
    
    
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "ViolationSystem.welcome: Leaving method."); 
    return nextAction;
  }
   
   
   
  /**
    *  
    * This method calls the DirectTalk waitForCall method.  Once this method call has 
    * returned we will wait for the ActionStatusEvent 
    * to be populated by the Done event.  Once this has been populated, not null - 
    * this method can return.  
    *
    * @param ase
    * ActionStatusEvent containing latest connection information for call 
    *
    */
  public void waitForCall(ActionStatusEvent ase){
      if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "ViolationSystem.waitForCall: Entered method.");
      if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.waitForCall: actionStatusEvent is ["+ase+"]");
      directTalk.waitForCall(ase);
      // The done event from the waitForCall method is not synchronous,
      // so we must explicitly wait for the done event before proceeding.
      if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.waitForCall: synchronizing");
      synchronized(this) {
        while(actionStatusEvent == null) {
          try{  
            wait();
          }         
          catch (InterruptedException ie){
            if (logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "ViolationSystem.waitForCall: InteruptedException caught during wait ["+ie.toString()+"].");           
            //do something
            //throw new ConfigurationException("ViolationSystem.waitForCall: InteruptedException caught during wait ["+ie.toString()+"].");   
          }            
        }//end while
      }//end synchronized
      if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.waitForCal: Leaving method.");

  } // end waitForCall



  /**
    * This method is called by DirectTalk to stop the application from accepting calls. 
    * This method will call cancelWait on the DirectTalk bean in the case that it is 
    * still waiting for a call.
    *
    * @param actionStatusEventIn  
    * stop application event passed from DirectTalk 
    */
  public void stopApplication(ActionStatusEvent actionStatusEventIn) {
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "ViolationSystem.stopApplication: Entering Method");
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.stopApplication: ReturnCode = ["+actionStatusEventIn.getCompletionCode()+"], Source object = ["+actionStatusEventIn.getSource()+"].");
    shutdown=true;
    directTalk.cancelWait();
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "ViolationSystem.stopApplication: Exiting Method");
  }// end  stopApplication








  /**
    * The DirectTalk bean calls this method upon completion of certain activities.  
    * This method simply logs event information and replaces the current ActionStatusEvent
    *  with the passed event.
    *
    * @param doneEvent   
    * ActionStatusEvent containing latest connection information for call
    */
  public void done(ActionStatusEvent doneEvent ) {
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "ViolationSystem.done: Entering Method");
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.done: ReturnCode = ["+doneEvent .getCompletionCode()+"], Source object = ["+doneEvent .getSource()+"].");
    actionStatusEvent = doneEvent ;
    
    // notify waiters for the done event
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.done: Synchronizing");
    synchronized(this) {
      notifyAll();
    }
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "ViolationSystem.done: Exiting Method");
  }// end done

  /**
    * The DirectTalk bean calls this method when a failure occurs. 
    * This method sets the hungup boolean to true and replaces the current 
    * ActionStatusEvent with the passed event.
    *
    * @param failedEvent
    * ActionStatusEvent containing latest connection information for call
    */
  public void failed(ActionStatusEvent failedEvent) {
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "ViolationSystem.failed: Entering Method");
    if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "ViolationSystem.failed: ReturnCode = ["+failedEvent.getCompletionCode()+"], Source object = ["+failedEvent.getSource()+"].");
    //
    hungup = true;
    actionStatusEvent = failedEvent;
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "ViolationSystem.failed: Exiting Method");
  }// end failed

  /**
    * The DirectTalk bean calls this method when a the caller has hungup.  
    * This method sets the hungup boolean to true and replaces the current 
    * ActionStatusEvent with the passed event.
    *
    * @param hungupEvent
    * HungupEvent containing latest connection information for call
    */
  public void hungup(HungupEvent hungupEvent) { 
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "ViolationSystem.hungup: Entering Method");
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.hungup: ReturnCode = ["+hungupEvent.getCompletionCode()+"], Source object = ["+hungupEvent.getSource()+"].");
    hungup = true;
    actionStatusEvent = hungupEvent;
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "ViolationSystem.hungup: Exiting Method");
  } // end hungup

  /**
    * The DirectTalk bean calls this method when a failure occurs and there is no listener
    * for registered to receive the event.  This method sets the hungup boolean to true and 
    * replaces the current ActionStatusEvent with the passed event.
    *
    * @param uncaughtFailedEvent 
    * UncaughtFailedEvent containing latest connection information for call
    */
  public void uncaughtFailed(UncaughtFailedEvent uncaughtFailedEvent) {  
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "ViolationSystem.uncaughtFailed: Entering Method");
    if(logLevel >= LoggerIF.ERR) log(LoggerIF.ERR, "ViolationSystem.uncaughtFailed: ReturnCode = ["+uncaughtFailedEvent.getCompletionCode()+"], Source object = ["+uncaughtFailedEvent.getSource()+"].");
    hungup = true;
    actionStatusEvent = uncaughtFailedEvent;
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "ViolationSystem.uncaughtFailed: Exiting Method");
  } // end uncaughtFailed
         
  /**
    *  
    * The DirectTalk bean calls this method when the caller has hungup 
    * and there is no listener to receive the event.  
    *
    * @param uncaughtHungupEvent
    * UncaughtHungupEvent containing latest connection information for call
    */
  public synchronized void uncaughtHungup(UncaughtHungupEvent uncaughtHungupEvent) {

    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "ViolationSystem.uncaughtHungup: Entered method.");
    if(logLevel >= LoggerIF.DEBUG) log(LoggerIF.DEBUG, "ViolationSystem.uncaughtHungup: ReturnCode = ["+uncaughtHungupEvent.getCompletionCode()+"], Source object = ["+uncaughtHungupEvent.getSource()+"].");
    hungup = true;
    actionStatusEvent = uncaughtHungupEvent;
    if(logLevel >= LoggerIF.INFO) log(LoggerIF.INFO, "ViolationSystem: Leaving method.");
  } // end uncaughtHungup  
  
  
  
 /**
   * This method will log a message to the logger if one is referenced
   *
   * @param level  
   * the level of the message
   *
   * @param msg     
   * the message to log
   */
  protected void log(int level, String msg){
    if (logger != null){
      logger.log(level, msg);
    }
    
  } // end log    
  
   /**
    * This is a generic method used to get and validate a string property. 
    *
    * @param propertyName
    *  A String that contains the name of the property to retrieve and validate.
    *
    * @return 
    * containing the property value retrieved from the property object.
    * 
    *
    * @throws MissingPropertyConfigurationException
    * thrown if the retrived property is null
    */   
  private String validateStringProperty( Properties inputProperties,String propertyName)throws MissingPropertyConfigurationException{       
    String propertyValue = inputProperties.getProperty( propertyName );
    log( LoggerIF.DEBUG, "ViolationSystem.validateStringProperty: [" + propertyName + "]=[" + propertyValue + "]" ); 
        
    if ( propertyValue == null ){
      throw new MissingPropertyConfigurationException("ViolationSystem.initConfiguration: property [" + propertyName + "] is null"); 
    }                 
    return propertyValue;
        
  }// end validateStringProperty
  

  /**
    * This is the main method used to call the application without directTalk
    * 
    */  
  
         
 /*  public static void main(String args[]){
     System.out.println("ViolationSystem: In Main.");  
     ActionStatusEvent ase = null;
    
    try{
      System.out.println("ViolationSystem: about to make a ViolationSystem.");
      ViolationSystem vs= new ViolationSystem();
    
      vs.startApplication(ase);
    }  
    catch (Exception e){
    }    
  }  */
   
                                                 
}//endclass                                                 