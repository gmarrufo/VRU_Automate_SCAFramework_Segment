package com.viecore.pvs;


/*
 * Licensed Materials - Property of Viecore Inc.
 *
 * (C) Copyright Viecore Inc. 2002 All Rights Reserved
 */


/**
  * This class represents the next action to be taken by the system after returning
  * from a BusinessIF has finished processing or a menu selection has been made.   
  * 
  * @author Susan Hammond
  * @since JDK 1.3
  * @version 1.0  12/18/2002
  */ 
public class NextAction{

  /**
    * Integer representing next action to be taken
    */
  private int returnAction = RETURN_ACTION_MENU;
  
  /**
    * If the returnAction is RETURN_ACTION_MENU, this represents the name of the 
    * menu to present
    */
  private String menuName = null;
  
  /**
    * If the returnAction is RETURN_ACTION_TRANSFER, this represents the number 
    * to transfer to
    */
  private String transferNumber = null;

 /**
    * If the returnAction is RETURN_ACTION_TRANSFER, 
    * this represents the segment to play before transferring
    */
  private String transferSegment = null;

  /**
    * Constant representing a menu next action
    */
  public static final int RETURN_ACTION_MENU = 0;
  
  /**
    * Constant representing a repeat next action
    */
  public static final int RETURN_ACTION_REPEAT = 1;
  
  /**
    * Constant representing a transfer next action
    */
  public static final int RETURN_ACTION_TRANSFER = 2;
  
  /**
    * Constant representing a goodbye next action
    */
  public static final int RETURN_ACTION_GOODBYE = 3;
  

  /**
    * Constant representing a end next action
    */
  public static final int RETURN_ACTION_END = 4;


  /**
    *  gets the menu Name in this object
    *
    * @return 
    * name of the menu 
    */
  public String getMenuName(){
    return menuName;
  }//  
  
  /**
    *  sets the menu Name in this object
    *
    * @param name
    * String representing the menu name
    */
  public void setMenuName(String name){
    menuName = name;
  }//  
  
  
  
  /**
    *  gets the return action int in this object
    *
    * @return 
    * the int value of the action
    */
  public int getReturnAction(){
    return returnAction;
  }
  
    
  /**
    *  Sets the return action int in this object
    *
    * @param  numberIn
    * the int value of the action
    */
  public void setReturnAction(int numberIn){
    returnAction = numberIn;
  }
  
  /**
    *  gets the transfer number in this object
    *
    * @return  
    * number to transfer to 
    */
  public String getTransferNumber(){
    return transferNumber;
  }
  
  /**
    *  Sets the transfer number in this object
    *
    * @param number
    * String representing number to transfer to 
    */
  public void setTransferNumber(String number){
    transferNumber = number;
  }
  
  
  /**
    *  gets the transfer segment in this object
    *
    * @return 
    * segment name used to transfer
    */
  public String getTransferSegment(){
    return transferSegment;
  }
  
  /**
    *  Sets the transfer segment in this object
    *
    * @param name
    * segment name used to transfer
    */
  public void setTransferSegment(String name){
    transferSegment = name;
  }
  
}

