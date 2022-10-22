package com.andrewsha.marketplace.exception;

public class JwtUtilsException extends RuntimeException {
    private static final long serialVersionUID = -1344524353598643983L;

    public JwtUtilsException(String message) {
        super(message);
    }
}
