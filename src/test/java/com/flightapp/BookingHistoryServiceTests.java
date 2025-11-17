package com.flightapp;

import com.flightapp.exceptions.ResourceNotFoundException;
import com.flightapp.model.Booking;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingHistoryServiceTests {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FlightInventoryRepository inventoryRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    @DisplayName("Get booking history for a user with multiple bookings")
    void getHistoryForUserWithMultipleBookings() {
        String email = "user@example.com";

        Booking b1 = new Booking();
        b1.setContactEmail(email);
        Booking b2 = new Booking();
        b2.setContactEmail(email);

        when(bookingRepository.findByContactEmail(email))
                .thenReturn(List.of(b1, b2));

        List<Booking> history = bookingService.getHistory(email);

        assertNotNull(history);
        assertEquals(2, history.size());
    }

    @Test
    @DisplayName("Get booking history for a user with a single booking")
    void getHistoryForUserWithSingleBooking() {
        String email = "single@example.com";

        Booking b1 = new Booking();
        b1.setContactEmail(email);

        when(bookingRepository.findByContactEmail(email))
                .thenReturn(List.of(b1));

        List<Booking> history = bookingService.getHistory(email);

        assertNotNull(history);
        assertEquals(1, history.size());
    }

    @Test
    @DisplayName("Reject booking history request when no bookings exist for email")
    void rejectHistoryWhenNoBookingsExist() {
        String email = "nobookings@example.com";

        when(bookingRepository.findByContactEmail(email))
                .thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class,
                () -> bookingService.getHistory(email));
    }

    @Test
    @DisplayName("Treat invalid email format same as email with no bookings")
    void invalidEmailFormatTreatedAsNoBookings() {
        when(bookingRepository.findByContactEmail(anyString()))
                .thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class,
                () -> bookingService.getHistory("not-an-email"));
    }
}
