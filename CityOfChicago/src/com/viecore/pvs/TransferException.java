package com.viecore.pvs;

/*
 * Licensed Materials - Property of Viecore Inc.
 *
 * (C) Copyright Viecore Inc. 2002, 2003 All Rights Reserved
 */

/**
 * Exception occuring when the caller requests a transfer
 *
 * @author Michael Ruggiero
 * @since JDK 1.3
 * @version 1.0 1/17/2003
 */
public class TransferException extends Exception {

    /**
      * This constructor calls the parents constructor with the passed message
      *
      * @param message
      *   Error message
      */
    public TransferException(String message){
      super(message);
    }

} //end TransferException