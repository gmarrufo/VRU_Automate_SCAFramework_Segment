package com.viecore.pvs;

/*
 * Licensed Materials - Property of Viecore Inc.
 *
 * (C) Copyright Viecore Inc. 2002, 2003 All Rights Reserved
 */

/**
 * Exception occuring when the caller hangs up during the application
 *
 * @author Mehmet Tekkarismaz
 * @since JDK 1.3
 * @version 1.0  1/8/2003
 */
public class HungupException extends Exception {

  /**
    * This method calls its parents constructor with the passed message
    *
    * @param message
    *   error message
    */
  public HungupException(String message){
    super(message);
  }

} //end HungupException