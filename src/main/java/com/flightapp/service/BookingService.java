package com.flightapp.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

import com.flightapp.exceptions.ResourceNotFoundException;
import com.flightapp.exceptions.ValidationException;

import com.flightapp.model.Booking;
import com.flightapp.model.BookingStatus;
import com.flightapp.model.FlightInventory;
import com.flightapp.model.Passenger;
import com.flightapp.model.TripType;
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


  
    //helper functions
    private void validatePassengersExist(BookingRequest req) {
        if (req.getPassengers() == null || req.getPassengers().isEmpty()) {
            throw new ValidationException("At least one passenger is required");
        }
    }

    private void validateSeatAvailability(FlightInventory flight, int passengerCount, String type) {
        if (flight.getAvailableSeats() < passengerCount) {
            throw new ValidationException("Not enough seats available in " + type + " flight");
        }
    }

    private void validateTripType(BookingRequest req) {
        if (req.getTripType() == TripType.ROUND_TRIP && req.getReturnFlightId() == null) {
            throw new ValidationException("Return flight ID is required for round-trip booking");
        }
    }

    private void validatePassengerFields(PassengerRequest p, boolean isRoundTrip) {

        if (p.getAge() <= 0) {
            throw new ValidationException("Passenger age must be greater than 0");
        }

        if (p.getSeatOutbound() == null || p.getSeatOutbound().isBlank()) {
            throw new ValidationException("Passenger seatOutbound is required");
        }

        if (isRoundTrip &&
                (p.getSeatReturn() == null || p.getSeatReturn().isBlank())) {
            throw new ValidationException("Passenger seatReturn is required for round trip bookings");
        }

        if (p.getMeal() != null) {
            try {
                MealType.valueOf(p.getMeal().toUpperCase());
            } catch (Exception e) {
                throw new ValidationException("Invalid meal type: " + p.getMeal());
            }
        }
    }


    @SuppressWarnings("null")
    public Booking bookFlight(Long flightId, @Valid BookingRequest req) {

        validatePassengersExist(req);
        validateTripType(req);

        FlightInventory outbound = inventoryRepository.findById(req.getOutboundFlightId())
                .orElseThrow(() -> new ResourceNotFoundException("Outbound flight not found"));

        validateSeatAvailability(outbound, req.getPassengers().size(), "outbound");

        FlightInventory returning = null;

        if (req.getReturnFlightId() != null) {

            returning = inventoryRepository.findById(req.getReturnFlightId())
                    .orElseThrow(() -> new ResourceNotFoundException("Return flight not found"));

            validateSeatAvailability(returning, req.getPassengers().size(), "return");
        }

        String pnr = UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase();

        Booking booking = new Booking();
        booking.setOutboundFlight(outbound);
        booking.setReturnFlight(returning);
        booking.setContactName(req.getContactName());
        booking.setContactEmail(req.getContactEmail());
        booking.setTotalPassengers(req.getPassengers().size());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setPnrOutbound(pnr);

        booking = bookingRepository.save(booking);

        boolean isRoundTrip = (returning != null);

        for (PassengerRequest p : req.getPassengers()) {

            validatePassengerFields(p, isRoundTrip);

            Passenger ps = new Passenger();
            ps.setName(p.getName());
            ps.setAge(p.getAge());
            ps.setGender(p.getGender());
            ps.setSeatOutbound(p.getSeatOutbound());
            ps.setSeatReturn(p.getSeatReturn());

            if (p.getMeal() != null) {
                ps.setMeal(MealType.valueOf(p.getMeal().toUpperCase()));
            }

            ps.setBooking(booking);
            passengerRepository.save(ps);
        }

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
                .orElseThrow(() -> new ResourceNotFoundException("PNR not found"));
    }

    public void cancelTicket(String pnr) {

        Booking booking = getTicket(pnr);

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new ValidationException("Ticket is already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        FlightInventory out = booking.getOutboundFlight();
        out.setAvailableSeats(out.getAvailableSeats() + booking.getTotalPassengers());
        inventoryRepository.save(out);

        if (booking.getReturnFlight() != null) {
            FlightInventory ret = booking.getReturnFlight();
            ret.setAvailableSeats(ret.getAvailableSeats() + booking.getTotalPassengers());
            inventoryRepository.save(ret);
        }
    }

    public List<Booking> getHistory(String email) {
        List<Booking> list = bookingRepository.findByContactEmail(email);

        if (list.isEmpty()) {
            throw new ResourceNotFoundException("No bookings found for this email");
        }

        return list;
    }
}
