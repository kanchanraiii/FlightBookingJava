package com.flightapp;

import com.flightapp.exceptions.ResourceNotFoundException;
import com.flightapp.model.Booking;
import com.flightapp.model.BookingStatus;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketRetrievalServiceTests {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FlightInventoryRepository inventoryRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    @DisplayName("Retrieve ticket details using a valid existing PNR")
    void retrieveTicketWithValidPnr() {
        String pnr = "ABC123";
        Booking booking = new Booking();
        booking.setPnrOutbound(pnr);
        booking.setContactName("Test User");
        booking.setContactEmail("user@example.com");
        booking.setStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findByPnrOutbound(pnr)).thenReturn(Optional.of(booking));

        Booking result = bookingService.getTicket(pnr);

        assertNotNull(result);
        assertEquals(pnr, result.getPnrOutbound());
        assertEquals("Test User", result.getContactName());
        assertEquals("user@example.com", result.getContactEmail());
    }

    @Test
    @DisplayName("Retrieved ticket details match what was stored at booking time")
    void retrievedDetailsMatchStoredBooking() {
        String pnr = "XYZ789";
        Booking booking = new Booking();
        booking.setPnrOutbound(pnr);
        booking.setContactName("Alice");
        booking.setContactEmail("alice@example.com");

        when(bookingRepository.findByPnrOutbound(pnr)).thenReturn(Optional.of(booking));

        Booking result = bookingService.getTicket(pnr);

        assertEquals("Alice", result.getContactName());
        assertEquals("alice@example.com", result.getContactEmail());
    }

    @Test
    @DisplayName("Reject retrieval when PNR does not exist")
    void rejectWhenPnrDoesNotExist() {
        when(bookingRepository.findByPnrOutbound(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookingService.getTicket("NOEXIST"));
    }

    @Test
    @DisplayName("Treat invalid PNR format the same as a non-existent PNR")
    void invalidPnrFormatTreatedAsNotFound() {
        when(bookingRepository.findByPnrOutbound(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookingService.getTicket("123"));
    }
}