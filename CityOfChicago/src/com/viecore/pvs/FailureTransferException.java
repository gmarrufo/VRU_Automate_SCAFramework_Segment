package com.viecore.pvs;

/*
 * Licensed Materials - Property of Viecore Inc.
 *
 * (C) Copyright Viecore Inc. 2002, 2003 All Rights Reserved
 */

/**
 * Exception occuring when a system error occurs indicating a system forced
 * transfer
 *
 * @author Michael Ruggiero
 * @since JDK 1.3
 * @version 1.0 2/5/2003
 */
public class FailureTransferException extends Exception {

    /**
      * This constructor calls the parents constructor with the passed message
      *
      * @param message
      *   Error message
      */
    public FailureTransferException(String message){
      super(message);
    }

} //end TransferException