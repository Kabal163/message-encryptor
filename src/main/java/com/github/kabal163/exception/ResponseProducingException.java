package com.github.kabal163.exception;

public class ResponseProducingException extends RuntimeException {

    public ResponseProducingException(String message, Throwable cause) {
        super(message, cause);
    }
}
