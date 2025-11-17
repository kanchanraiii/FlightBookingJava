package com.flightapp;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.flightapp.exceptions.GlobalErrorHandler;
import com.flightapp.exceptions.ResourceNotFoundException;
import com.flightapp.exceptions.ValidationException;
import com.flightapp.model.CityEnum;
import com.flightapp.model.MealType;
import com.flightapp.model.TripType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.HttpMessageNotReadableException;
import java.time.format.DateTimeParseException;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class GlobalErrorHandlerTest {

    private final GlobalErrorHandler handler = new GlobalErrorHandler();

   

    @Test
    @DisplayName("Tests when custom validation fails")
    void testHandleCustomValidation() {
        ValidationException ex = new ValidationException("Custom validation failed");

        Map<String, String> response = handler.handleCustomValidation(ex);

        assertEquals("Custom validation failed", response.get("error"));
    }

    
    @Test
    @DisplayName("Tests for resource not found")
    void testHandleNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Item not found");

        Map<String, String> response = handler.handleNotFound(ex);

        assertEquals("Item not found", response.get("error"));
    }

   
    @Test
    @DisplayName("Tests for invalid city")
    void testInvalidCityEnum() {
        InvalidFormatException cause = new InvalidFormatException(null, "bad city", "X", CityEnum.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("JSON error", cause);

        Map<String, String> response = handler.handleInvalidJson(ex);

        assertTrue(response.get("error").contains("Invalid city"));
    }

    
    @Test
    @DisplayName("Test for invalid tripType")
    void testInvalidTripType() {
        InvalidFormatException cause = new InvalidFormatException(null, "bad", "ROUND_TRPPP", TripType.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("JSON error", cause);

        Map<String, String> response = handler.handleInvalidJson(ex);

        assertTrue(response.get("error").contains("Invalid trip type"));
    }

    
    @Test
    @DisplayName("Tests for invalid mealType")
    void testInvalidMealType() {
        InvalidFormatException cause = new InvalidFormatException(null, "bad", "veggie", MealType.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("JSON error", cause);

        Map<String, String> response = handler.handleInvalidJson(ex);

        assertTrue(response.get("error").contains("Invalid meal type"));
    }

    
    @Test
    @DisplayName("Tests for invalid mealAvailable")
    void testInvalidBoolean() {
        InvalidFormatException cause = new InvalidFormatException(null, "not_bool", "abc", Boolean.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("JSON error", cause);

        Map<String, String> response = handler.handleInvalidJson(ex);

        assertTrue(response.get("error").contains("true or false"));
    }

   
    @Test
    @DisplayName("Test for invalid date format")
    void testInvalidDateFormat() {
        DateTimeParseException cause = new DateTimeParseException("Bad date", "2025-13-01", 5);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("JSON error", cause);

        Map<String, String> response = handler.handleInvalidJson(ex);

        assertEquals("Invalid date format. Use yyyy-MM-dd", response.get("error"));
    }

  
    @Test
    @DisplayName("Test for invalid json format in requests")
    void testInvalidJsonFallback() {
        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException("JSON error", new RuntimeException("Unknown"));

        Map<String, String> response = handler.handleInvalidJson(ex);

        assertEquals("Invalid JSON request", response.get("error"));
    }

  
    @Test
    @DisplayName("Tests for other random failures")
    void testHandleOthers() {
        Exception ex = new Exception("Random failure");

        Map<String, String> response = handler.handleOthers(ex);

        assertEquals("Random failure", response.get("error"));
    }
}
