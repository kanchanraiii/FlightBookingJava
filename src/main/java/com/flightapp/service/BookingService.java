package com.flightapp.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

import com.flightapp.model.Booking;
import com.flightapp.model.BookingStatus;
import com.flightapp.model.FlightInventory;
import com.flightapp.model.Passenger;
import com.flightapp.model.MealType;
import com.flightapp.repository.BookingRepository;
import com.flightapp.repository.FlightInventoryRepository;
import com.flightapp.repository.PassengerRepository;
import com.flightapp.request.BookingRequest;
import com.flightapp.request.PassengerRequest;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private FlightInventoryRepository inventoryRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @SuppressWarnings("null")
	public Booking bookFlight(Long flightId, @Valid BookingRequest req) {

        FlightInventory outbound = inventoryRepository.findById(req.getOutboundFlightId())
                .orElseThrow(() -> new RuntimeException("Outbound flight not found"));

        FlightInventory returning = null;

        if (req.getReturnFlightId() != null) {
            returning = inventoryRepository.findById(req.getReturnFlightId())
                    .orElseThrow(() -> new RuntimeException("Return flight not found"));
        }

        // Create booking
        Booking booking = new Booking();
        booking.setOutboundFlight(outbound);
        booking.setReturnFlight(returning);

        booking.setContactName(req.getContactName());
        booking.setContactEmail(req.getContactEmail());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setTotalPassengers(req.getPassengers().size());

        String pnr = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6).toUpperCase();
        booking.setPnrOutbound(pnr);

        booking = bookingRepository.save(booking);

        // Save passengers
        for (PassengerRequest p : req.getPassengers()) {
            Passenger ps = new Passenger();
            ps.setName(p.getName());
            ps.setAge(p.getAge());
            ps.setGender(p.getGender());
            ps.setSeatOutbound(p.getSeatOutbound());
            ps.setSeatReturn(p.getSeatReturn());

            if (p.getMeal() != null) {
                try {
                    ps.setMeal(MealType.valueOf(p.getMeal().toUpperCase()));
                } catch (Exception ignored) {}
            }

            ps.setBooking(booking);
            passengerRepository.save(ps);
        }

        // Reduce seats
        outbound.setAvailableSeats(outbound.getAvailableSeats() - req.getPassengers().size());
        inventoryRepository.save(outbound);

        if (returning != null) {
            returning.setAvailableSeats(returning.getAvailableSeats() - req.getPassengers().size());
            inventoryRepository.save(returning);
        }

        return booking;
    }

    

    public Booking getTicket(String pnr) {
        return bookingRepository.findByPnrOutbound(pnr)
                .orElseThrow(() -> new RuntimeException("PNR not found"));
    }

   

    public void cancelTicket(String pnr) {

        Booking booking = getTicket(pnr);
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // restore seats
        FlightInventory out = booking.getOutboundFlight();
        out.setAvailableSeats(out.getAvailableSeats() + booking.getTotalPassengers());
        inventoryRepository.save(out);

        FlightInventory ret = booking.getReturnFlight();
        if (ret != null) {
            ret.setAvailableSeats(ret.getAvailableSeats() + booking.getTotalPassengers());
            inventoryRepository.save(ret);
        }
    }
    
    public List<Booking> getHistory(String email) {
        return bookingRepository.findByContactEmail(email);
    }

}
