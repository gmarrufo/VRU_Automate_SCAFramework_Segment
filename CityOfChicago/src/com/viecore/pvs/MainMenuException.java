package com.viecore.pvs;

/*
 * Licensed Materials - Property of Viecore Inc.
 *
 * (C) Copyright Viecore Inc. 2002, 2003 All Rights Reserved
 */

/**
 * Exception occuring when the caller requests to be directed to
 * the main menu
 *
 * @author Michael Ruggiero
 * @since JDK 1.3
 * @version 1.0 1/17/2003
 */
public class MainMenuException extends Exception {

    public MainMenuException(String message){
      super(message);
    }

} //end MainMenuException