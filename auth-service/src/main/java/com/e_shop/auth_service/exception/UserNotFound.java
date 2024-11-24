package com.e_shop.auth_service.exception;

public class UserNotFound extends RuntimeException {
    public  UserNotFound(String message) {
        super(message);
    }

    public UserNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}