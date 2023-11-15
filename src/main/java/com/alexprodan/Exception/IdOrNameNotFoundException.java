package com.alexprodan.Exception;

public class IdOrNameNotFoundException extends RuntimeException{

    private final String fieldName;

    public IdOrNameNotFoundException(String message, String fieldName) {
        super(message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
