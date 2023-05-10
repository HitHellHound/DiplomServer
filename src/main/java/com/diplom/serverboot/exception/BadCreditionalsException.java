package com.diplom.serverboot.exception;

public class BadCreditionalsException extends Exception {
    public BadCreditionalsException() {
    }

    public BadCreditionalsException(String message) {
        super(message);
    }
}
