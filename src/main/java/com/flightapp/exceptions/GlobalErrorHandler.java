package com.flightapp.exceptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalErrorHandler {

    // Handle @Valid errors
    @ExceptionHandler(exception=MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationErrors(MethodArgumentNotValidException exception) {

        Map<String, String> errorMap = new HashMap<>();

        List<ObjectError> errors = exception.getBindingResult().getAllErrors();

        for (ObjectError err : errors) {

            String field = ((FieldError) err).getField();
            String message = err.getDefaultMessage();

            errorMap.put(field, message);
        }

        return errorMap;
    }

    // Handle custom validation exceptions
    @ExceptionHandler(ValidationException.class)
    public Map<String, String> handleCustomValidation(ValidationException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return error;
    }

    // Handle resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public Map<String, String> handleNotFound(ResourceNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return error;
    }

    // Catch-all fallback
    @ExceptionHandler(exception=Exception.class)
    public Map<String, String> handleOthers(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return error;
    }
}
