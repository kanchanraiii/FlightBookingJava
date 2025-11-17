package com.flightapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flightapp.model.Booking;
import com.flightapp.repository.BookingRepository;

@Service
public class TicketService {

    @Autowired
    private BookingRepository bookingRepository;

    public Booking getTicketByPnr(String pnr) {

        Booking booking = bookingRepository.findByPnrOutbound(pnr)
                .orElseGet(() -> bookingRepository.findByPnrReturn(pnr)
                        .orElseThrow(() -> new RuntimeException("PNR not found")));

        return booking;
    }
}
