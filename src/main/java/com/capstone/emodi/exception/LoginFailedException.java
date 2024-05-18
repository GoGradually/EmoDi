// LoginFailedException.java
package com.capstone.emodi.exception;

public class LoginFailedException extends RuntimeException {
    public LoginFailedException(String message) {
        super(message);
    }
}