package com.digitalmoneyhouse.auth_service.exceptions;

public class CustomJwtException extends RuntimeException{

    public CustomJwtException(String message) {
        super(message);
    }
}
