package com.flightapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flightapp.exceptions.ValidationException;
import com.flightapp.exceptions.ResourceNotFoundException;
import com.flightapp.model.Booking;
import com.flightapp.repository.BookingRepository;

@Service
public class TicketService {

    @Autowired
    private BookingRepository bookingRepository;

    public Booking getTicketByPnr(String pnr) {

        if (pnr == null || pnr.trim().isEmpty()) {
            throw new ValidationException("PNR cannot be empty");
        }

        if (pnr.length() != 6) {
            throw new ValidationException("PNR must be exactly 6 characters");
        }

        if (!pnr.matches("^[A-Z0-9]+$")) {
            throw new ValidationException("PNR must be alphanumeric");
        }

       
        return bookingRepository.findByPnrOutbound(pnr)
                .orElseGet(() ->
                    bookingRepository.findByPnrReturn(pnr)
                        .orElseThrow(() -> new ResourceNotFoundException("PNR not found"))
                );
    }
}
