package com.yxg.football.backendweb.exceptions;

public class CircleException extends RuntimeException{
    public CircleException() {
    }

    public CircleException(String message) {
        super(message);
    }

    public CircleException(String message, Throwable cause) {
        super(message, cause);
    }
}
