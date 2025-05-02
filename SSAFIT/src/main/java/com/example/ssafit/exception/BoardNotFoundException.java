package com.example.ssafit.exception;

public class BoardNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public BoardNotFoundException() {
    }

    public BoardNotFoundException(String message) {
        super(message);
    }
}
