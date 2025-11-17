package com.flightapp;

import com.flightapp.model.*;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class ModelTests {

    @Test
    void testAirlineModel() {
        Airline airline = new Airline();
        airline.setAirlineId(1L);
        airline.setAirlineName("Indigo");

        assertEquals(1L, airline.getAirlineId());
        assertEquals("Indigo", airline.getAirlineName());
    }

  
    @Test
    void testSeatModel() {
        Seat seat = new Seat();
        seat.setSeatNo("12A");
        seat.setAvailable(true);

        assertEquals("12A", seat.getSeatNo());
        assertTrue(seat.isAvailable());
    }

   
    @Test
    void testPassengerModel() {
        Passenger p = new Passenger();

        p.setPassengerId(10L);
        p.setName("John Doe");
        p.setAge(25);
        p.setGender("MALE");
        p.setSeatOutbound("14B");
        p.setSeatReturn("16C");
        p.setMeal(MealType.VEG);

        assertEquals(10L, p.getPassengerId());
        assertEquals("John Doe", p.getName());
        assertEquals(25, p.getAge());
        assertEquals("MALE", p.getGender());
        assertEquals("14B", p.getSeatOutbound());
        assertEquals("16C", p.getSeatReturn());
        assertEquals(MealType.VEG, p.getMeal());
    }

    
    @Test
    void testBookingModel() {
        Booking booking = new Booking();

        FlightInventory inv = new FlightInventory();
        booking.setBookingId(99L);
        booking.setOutboundFlight(inv);
        booking.setReturnFlight(inv);

        booking.setPnrOutbound("PNR123");
        booking.setPnrReturn("PNR789");

        booking.setContactName("Alice");
        booking.setContactEmail("alice@gmail.com");
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setTotalPassengers(3);

        assertEquals(99L, booking.getBookingId());
        assertEquals("PNR123", booking.getPnrOutbound());
        assertEquals("PNR789", booking.getPnrReturn());
        assertEquals("Alice", booking.getContactName());
        assertEquals("alice@gmail.com", booking.getContactEmail());
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
        assertEquals(3, booking.getTotalPassengers());
        assertNotNull(booking.getOutboundFlight());
        assertNotNull(booking.getReturnFlight());
    }

    
    @Test
    void testCityEnum() {
        assertNotNull(CityEnum.valueOf("DELHI"));
        assertEquals(5, CityEnum.values().length); 
    }

    @Test
    void testMealTypeEnum() {
        assertEquals(MealType.VEG, MealType.valueOf("VEG"));
    }

    @Test
    void testBookingStatusEnum() {
        assertEquals(BookingStatus.CONFIRMED, BookingStatus.valueOf("CONFIRMED"));
    }

    @Test
    void testTripTypeEnum() {
        assertEquals(TripType.ROUND_TRIP, TripType.valueOf("ROUND_TRIP"));
    }
}
