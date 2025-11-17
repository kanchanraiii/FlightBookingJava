package com.flightapp;

import com.flightapp.exceptions.ResourceNotFoundException;
import com.flightapp.exceptions.ValidationException;
import com.flightapp.model.Booking;
import com.flightapp.model.BookingStatus;
import com.flightapp.model.CityEnum;
import com.flightapp.model.FlightInventory;
import com.flightapp.model.TripType;
import com.flightapp.repository.BookingRepository;
import com.flightapp.repository.FlightInventoryRepository;
import com.flightapp.repository.PassengerRepository;
import com.flightapp.request.BookingRequest;
import com.flightapp.request.PassengerRequest;
import com.flightapp.service.BookingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTests {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FlightInventoryRepository inventoryRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @InjectMocks
    private BookingService bookingService;

    private FlightInventory outboundFlightWithSeats(int availableSeats) {
        FlightInventory flight = new FlightInventory();
        flight.setFlightNumber("BK101");
        flight.setSourceCity(CityEnum.DELHI);
        flight.setDestinationCity(CityEnum.MUMBAI);
        flight.setDepartureDate(LocalDate.now().plusDays(5));
        flight.setDepartureTime(LocalTime.of(10, 0));
        flight.setArrivalDate(LocalDate.now().plusDays(5));
        flight.setArrivalTime(LocalTime.of(12, 0));
        flight.setAvailableSeats(availableSeats);
        flight.setTotalSeats(availableSeats);
        flight.setPrice(5000.0);
        return flight;
    }

    private BookingRequest validBookingRequest(int passengerCount) {
        BookingRequest req = new BookingRequest();
        req.setOutboundFlightId(1L);
        req.setContactName("Test User");
        req.setContactEmail("user@example.com");
        req.setTripType(TripType.ONE_WAY);

        List<PassengerRequest> passengers = new ArrayList<>();
        for (int i = 0; i < passengerCount; i++) {
            PassengerRequest p = new PassengerRequest();
            p.setName("Passenger " + (i + 1));
            p.setAge(30);
            p.setGender("M");
            p.setSeatOutbound("A" + (i + 1));
            p.setMeal("Veg");
            passengers.add(p);
        }
        req.setPassengers(passengers);
        return req;
    }

    @Test
    @DisplayName("Book a single seat successfully and generate a PNR")
    void bookSingleSeatGeneratesPnr() {
        FlightInventory outbound = outboundFlightWithSeats(5);
        BookingRequest req = validBookingRequest(1);

        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(outbound));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Booking booking = bookingService.bookFlight(1L, req);

        assertNotNull(booking);
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
        assertEquals(1, booking.getTotalPassengers());
        assertNotNull(booking.getPnrOutbound());
        assertEquals(6, booking.getPnrOutbound().length());
        assertEquals(4, outbound.getAvailableSeats());
    }

    @Test
    @DisplayName("Book multiple seats and update available seats")
    void bookMultipleSeatsReducesAvailability() {
        FlightInventory outbound = outboundFlightWithSeats(10);
        BookingRequest req = validBookingRequest(3);

        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(outbound));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Booking booking = bookingService.bookFlight(1L, req);

        assertEquals(3, booking.getTotalPassengers());
        assertEquals(7, outbound.getAvailableSeats());
    }

    @Test
    @DisplayName("Book flight with passenger details, Veg meal and seat selection")
    void bookFlightWithPassengerDetailsAndMeal() {
        FlightInventory outbound = outboundFlightWithSeats(5);
        BookingRequest req = validBookingRequest(2);

        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(outbound));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Booking booking = bookingService.bookFlight(1L, req);

        assertNotNull(booking);
        assertEquals(2, booking.getTotalPassengers());
    }

    @Test
    @DisplayName("Reject booking when outbound flight is not found")
    void rejectBookingWhenFlightNotFound() {
        BookingRequest req = validBookingRequest(1);

        when(inventoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookingService.bookFlight(1L, req));
    }

    @Test
    @DisplayName("Reject booking when passenger list is empty")
    void rejectBookingWhenPassengersMissing() {
        BookingRequest req = validBookingRequest(0);
        req.setPassengers(new ArrayList<>());

        assertThrows(ValidationException.class, () -> bookingService.bookFlight(1L, req));
    }

    @Test
    @DisplayName("Reject booking when not enough seats are available")
    void rejectBookingWhenNotEnoughSeats() {
        FlightInventory outbound = outboundFlightWithSeats(1);
        BookingRequest req = validBookingRequest(2);

        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(outbound));

        assertThrows(ValidationException.class, () -> bookingService.bookFlight(1L, req));
    }

    @Test
    @DisplayName("Book the last available seat on a flight")
    void bookLastAvailableSeat() {
        FlightInventory outbound = outboundFlightWithSeats(1);
        BookingRequest req = validBookingRequest(1);

        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(outbound));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Booking booking = bookingService.bookFlight(1L, req);

        assertNotNull(booking);
        assertEquals(0, outbound.getAvailableSeats());
    }

    @Test
    @DisplayName("Book a round-trip successfully")
    void bookRoundTripSuccessfully() {
        FlightInventory outbound = outboundFlightWithSeats(5);
        FlightInventory returning = outboundFlightWithSeats(5);

        BookingRequest req = validBookingRequest(2);
        req.setTripType(TripType.ROUND_TRIP);
        req.setReturnFlightId(2L);

        // FIX: seatReturn required for round trip
        req.getPassengers().forEach(p -> p.setSeatReturn("B1"));

        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(outbound));
        when(inventoryRepository.findById(2L)).thenReturn(Optional.of(returning));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Booking booking = bookingService.bookFlight(1L, req);

        assertNotNull(booking);
        assertEquals(2, booking.getTotalPassengers());
        assertEquals(3, outbound.getAvailableSeats());
        assertEquals(3, returning.getAvailableSeats());
    }

    @Test
    @DisplayName("Reject round-trip booking when return flight not found")
    void rejectRoundTripWhenReturnFlightNotFound() {
        FlightInventory outbound = outboundFlightWithSeats(3);
        BookingRequest req = validBookingRequest(2);

        req.setTripType(TripType.ROUND_TRIP);
        req.setReturnFlightId(2L);

        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(outbound));
        when(inventoryRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> bookingService.bookFlight(1L, req));
    }

    @Test
    @DisplayName("Reject round-trip when not enough seats on return flight")
    void rejectRoundTripWhenNotEnoughSeats() {
        FlightInventory outbound = outboundFlightWithSeats(5);
        FlightInventory returning = outboundFlightWithSeats(1);

        BookingRequest req = validBookingRequest(3);
        req.setTripType(TripType.ROUND_TRIP);
        req.setReturnFlightId(2L);

        // FIX: seatReturn required
        req.getPassengers().forEach(p -> p.setSeatReturn("B1"));

        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(outbound));
        when(inventoryRepository.findById(2L)).thenReturn(Optional.of(returning));

        assertThrows(ValidationException.class,
                () -> bookingService.bookFlight(1L, req));
    }

    @Test
    @DisplayName("Reject round-trip when returnFlightId is missing")
    void rejectRoundTripWithoutReturnFlightId() {

        BookingRequest req = validBookingRequest(2);
        req.setTripType(TripType.ROUND_TRIP);
        req.setReturnFlightId(null);



        assertThrows(ValidationException.class,
                () -> bookingService.bookFlight(1L, req));
    }

    @Test
    @DisplayName("Reject booking when passenger age is invalid")
    void rejectInvalidPassengerAge() {
        FlightInventory outbound = outboundFlightWithSeats(5);

        BookingRequest req = validBookingRequest(1);
        req.getPassengers().get(0).setAge(-5);

        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(outbound));

        assertThrows(ValidationException.class,
                () -> bookingService.bookFlight(1L, req));
    }
}
