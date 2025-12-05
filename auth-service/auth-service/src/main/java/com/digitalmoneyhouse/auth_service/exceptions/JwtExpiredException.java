package com.digitalmoneyhouse.auth_service.exceptions;

public class JwtExpiredException extends CustomJwtException {

    public JwtExpiredException(String message) {
        super(message);
    }
}
