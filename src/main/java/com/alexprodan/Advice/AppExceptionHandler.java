package com.alexprodan.Advice;

import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(HttpMessageNotWritableException.class)
    public Map<String,String> handleInvalidArguments(HttpMessageNotWritableException ex){
        Map<String,String> errors = new HashMap<>();
        return null;
    }
}
