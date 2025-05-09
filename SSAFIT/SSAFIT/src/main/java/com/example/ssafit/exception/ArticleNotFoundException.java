package com.example.ssafit.exception;

public class ArticleNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public ArticleNotFoundException() {
    }

    public ArticleNotFoundException(String message) {
        super(message);
    }
}
