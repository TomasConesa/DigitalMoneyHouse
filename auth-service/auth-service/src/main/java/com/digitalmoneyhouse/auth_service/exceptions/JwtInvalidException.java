package com.digitalmoneyhouse.auth_service.exceptions;

public class JwtInvalidException extends CustomJwtException {

    public JwtInvalidException(String message) {
        super(message);
    }
}
