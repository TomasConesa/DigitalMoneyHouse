package com.digitalmoneyhouse.auth_service.exceptions;

public class JwtInvalidException extends JwtException{

    public JwtInvalidException(String message) {
        super(message);
    }
}
