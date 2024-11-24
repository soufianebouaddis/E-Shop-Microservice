package com.e_shop.product_service.exception;

public class ProductNotFound extends RuntimeException{
    public  ProductNotFound(String message) {
        super(message);
    }

    public ProductNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
