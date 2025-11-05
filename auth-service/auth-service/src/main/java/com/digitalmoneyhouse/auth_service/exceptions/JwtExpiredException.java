package com.digitalmoneyhouse.auth_service.exceptions;

public class JwtExpiredException extends JwtException{

    public JwtExpiredException(String message) {
        super(message);
    }
}
