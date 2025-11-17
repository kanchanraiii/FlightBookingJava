package com.flightapp;

import com.flightapp.exceptions.ResourceNotFoundException;
import com.flightapp.exceptions.ValidationException;
import com.flightapp.model.Booking;
import com.flightapp.model.BookingStatus;
import com.flightapp.model.CityEnum;
import com.flightapp.model.FlightInventory;
import com.flightapp.repository.BookingRepository;
import com.flightapp.repository.FlightInventoryRepository;
import com.flightapp.repository.PassengerRepository;
import com.flightapp.service.BookingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingCancellationServiceTests {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FlightInventoryRepository inventoryRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @InjectMocks
    private BookingService bookingService;

    private FlightInventory futureFlightWithSeats(int availableSeats, int totalPassengers) {
        FlightInventory flight = new FlightInventory();
        flight.setSourceCity(CityEnum.DELHI);
        flight.setDestinationCity(CityEnum.MUMBAI);
        flight.setDepartureDate(LocalDate.now().plusDays(3));
        flight.setDepartureTime(LocalTime.of(10, 0));
        flight.setArrivalDate(LocalDate.now().plusDays(3));
        flight.setArrivalTime(LocalTime.of(12, 0));
        flight.setAvailableSeats(availableSeats);
        flight.setTotalSeats(availableSeats + totalPassengers);
        return flight;
    }

    @Test
    @DisplayName("Cancel a confirmed booking and get back available seats")
    void cancelConfirmedBookingRestoresSeats() {
        String pnr = "CANCEL1";
        int passengers = 2;

        FlightInventory outbound = futureFlightWithSeats(5, passengers);

        Booking booking = new Booking();
        booking.setPnrOutbound(pnr);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setOutboundFlight(outbound);
        booking.setTotalPassengers(passengers);

        when(bookingRepository.findByPnrOutbound(pnr)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        bookingService.cancelTicket(pnr);

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertEquals(7, outbound.getAvailableSeats());
    }

    @Test
    @DisplayName("Reject cancellation when PNR does not exist")
    void rejectCancellationWhenPnrNotFound() {
        when(bookingRepository.findByPnrOutbound(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> bookingService.cancelTicket("UNKNOWN"));
    }

    @Test
    @DisplayName("Reject cancellation when booking is already cancelled")
    void rejectCancellationWhenAlreadyCancelled() {
        String pnr = "CANCEL2";

        Booking booking = new Booking();
        booking.setPnrOutbound(pnr);
        booking.setStatus(BookingStatus.CANCELLED);

        when(bookingRepository.findByPnrOutbound(pnr))
                .thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class,
                () -> bookingService.cancelTicket(pnr));
    }
}
