package com.shivay.webflux.exceptionhandler;

import com.shivay.webflux.dto.InputFailedValidationResponse;
import com.shivay.webflux.exception.InputFailedValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MathServiceExceptionHandler {


    @ExceptionHandler(InputFailedValidationException.class)
    public ResponseEntity<InputFailedValidationResponse> handleExcpetion(InputFailedValidationException ex){
        InputFailedValidationResponse response  = new InputFailedValidationResponse();
        response.setErrorCode(ex.getErrorCode());
        response.setInput(ex.getInput());
        response.setMessage(ex.getMessage());
        return  ResponseEntity.badRequest().body(response);
    }
}
