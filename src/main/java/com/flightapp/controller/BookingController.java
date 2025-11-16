package com.flightapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.flightapp.model.Booking;
import com.flightapp.request.BookingRequest;
import com.flightapp.service.BookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1.0/flight")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/booking/{flightId}")
    public Booking bookFlight(
            @PathVariable Long flightId,
            @Valid @RequestBody BookingRequest req) {

        return bookingService.bookFlight(flightId, req);
    }
}
