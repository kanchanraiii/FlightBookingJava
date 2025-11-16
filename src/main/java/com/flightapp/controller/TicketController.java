package com.flightapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.flightapp.model.Booking;
import com.flightapp.service.BookingService;

@RestController
@RequestMapping("/api/v1.0/flight")
public class TicketController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/ticket/{pnr}")
    public Booking getTicket(@PathVariable String pnr) {
        return bookingService.getTicket(pnr);
    }

    @GetMapping("/booking/history/{email}")
    public List<Booking> getHistory(@PathVariable String email) {
        return bookingService.getHistory(email);
    }

    @DeleteMapping("/booking/cancel/{pnr}")
    public String cancelTicket(@PathVariable String pnr) {
        bookingService.cancelTicket(pnr);
        return "Booking cancelled successfully";
    }
}
