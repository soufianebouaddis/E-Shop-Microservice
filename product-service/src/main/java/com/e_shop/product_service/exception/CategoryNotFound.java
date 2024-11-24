package com.e_shop.product_service.exception;

public class CategoryNotFound extends RuntimeException{
    public  CategoryNotFound(String message) {
        super(message);
    }

    public CategoryNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
