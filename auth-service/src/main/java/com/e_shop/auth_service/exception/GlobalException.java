package com.e_shop.auth_service.exception;
import com.e_shop.auth_service.response.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.http.HttpStatus;


@ControllerAdvice
public class GlobalException {
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return new ErrorResponse(404, "Endpoint not found: " + ex.getRequestURL());
    }
    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleExpiredJwtException(ExpiredJwtException ex) {
        return new ErrorResponse(500,"Session Expired "+ex.getMessage());
    }
}
