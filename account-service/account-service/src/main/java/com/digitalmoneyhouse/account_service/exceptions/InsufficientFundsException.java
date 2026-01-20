package com.digitalmoneyhouse.account_service.exceptions;

public class InsufficientFundsException extends RuntimeException{

    public InsufficientFundsException(String message) {
        super(message);
    }
}
