package com.e_shop.auth_service.common;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
@Data
public class CustomResponseEntity <T>{
    private boolean success;
    private String error;
    private HttpStatus statusCode;
    private String message;
    private T data;
    private HttpHeaders headers;
    public CustomResponseEntity(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public CustomResponseEntity(boolean success, T data, HttpStatus statusCode) {
        this.success = success;
        this.data = data;
        this.statusCode = statusCode;
    }

    public CustomResponseEntity(boolean success, T data, String error, HttpStatus statusCode, String message) {
        this.success = success;
        this.data = data;
        this.error = error;
        this.statusCode = statusCode;
        this.message = message;
    }

    public CustomResponseEntity(boolean success, String error, HttpStatus statusCode, String message, T data, HttpHeaders headers) {
        this.success = success;
        this.error = error;
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
        this.headers = headers;
    }

    public CustomResponseEntity(boolean success, HttpStatus statusCode, T data, HttpHeaders headers) {
        this.success = success;
        this.statusCode = statusCode;
        this.data = data;
        this.headers = headers;
    }

    public CustomResponseEntity(boolean success, HttpStatus statusCode, String message) {
        this.success = success;
        this.statusCode = statusCode;
        this.message = message;
    }
    public CustomResponseEntity(boolean success, HttpStatus statusCode,String error, String message) {
        this.success = success;
        this.statusCode = statusCode;
        this.error=error;
        this.message = message;
    }
    public CustomResponseEntity(boolean success,T data , HttpHeaders header,HttpStatus statusCode){
        this.success = success;
        this.data = data;
        this.headers = header;
        this.statusCode = statusCode;
    }
}
