package com.shivay.webflux.exception;

public class InputFailedValidationException extends  RuntimeException{
    private static  final String MSG="allowed range is 10-20";
    private static final int errorCode =100;
    private  int input;

    public InputFailedValidationException(int input) {
        super(MSG);
        this.input = input;
    }

    public int getInput() {
        return input;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
