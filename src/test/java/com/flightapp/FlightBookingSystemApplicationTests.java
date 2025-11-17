package com.flightapp;

import com.flightapp.exceptions.ResourceNotFoundException;
import com.flightapp.exceptions.ValidationException;
import com.flightapp.model.Airline;
import com.flightapp.model.CityEnum;
import com.flightapp.model.FlightInventory;
import com.flightapp.repository.AirlineRepository;
import com.flightapp.repository.FlightInventoryRepository;
import com.flightapp.request.AddInventory;
import com.flightapp.service.FlightInventoryService;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlightCreationApiTests {

    @Mock
    private FlightInventoryRepository inventoryRepository;

    @Mock
    private AirlineRepository airlineRepository;

    @InjectMocks
    private FlightInventoryService flightInventoryService;

    private AddInventory validRequest() {
        AddInventory req = new AddInventory();
        req.setAirlineId(1L);
        req.setFlightNumber("AI101");
        req.setSourceCity(CityEnum.DELHI);
        req.setDestinationCity(CityEnum.MUMBAI);

        LocalDate departureDate = LocalDate.now().plusDays(10);
        LocalDate arrivalDate = departureDate.plusDays(1);

        req.setDepartureDate(departureDate);
        req.setDepartureTime(LocalTime.of(10, 0));
        req.setArrivalDate(arrivalDate);
        req.setArrivalTime(LocalTime.of(12, 0));
        req.setTotalSeats(100);
        req.setPrice(5000.0);
        req.setMealAvailable(true);
        return req;
    }

    @Test
    @DisplayName("Add a new valid flight for an existing airline")
    void createNewValidFlightForExistingAirline() {

        // request ka dhancha banaliya
        AddInventory req = validRequest();
        // airline wala object
        Airline airline = new Airline();
        // iss repo mai koi entry ki id 1L ho, toh bhai return krdo
        when(airlineRepository.findById(1L)).thenReturn(Optional.of(airline));
        // exist nahi karta
        when(inventoryRepository.existsByFlightNumberAndDepartureDate(
                req.getFlightNumber(), req.getDepartureDate())).thenReturn(false);
        // simulation but not real saving
        when(inventoryRepository.save(any(FlightInventory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        // basically testing data se daldo
        FlightInventory result = flightInventoryService.addInventory(req);


        assertNotNull(result); // true
        assertEquals(req.getFlightNumber(), result.getFlightNumber()); // true
    }

    @Test
    @DisplayName("Add a flight with only minimum required fields")
    void createFlightWithMinimumRequiredFields() {
        AddInventory req = validRequest();
        req.setMealAvailable(false);
        Airline airline = new Airline();

        when(airlineRepository.findById(1L)).thenReturn(Optional.of(airline));
        when(inventoryRepository.existsByFlightNumberAndDepartureDate(
                req.getFlightNumber(), req.getDepartureDate())).thenReturn(false);
        when(inventoryRepository.save(any(FlightInventory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        FlightInventory result = flightInventoryService.addInventory(req);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Add a flight with all optional fields filled")
    void createFlightWithAllOptionalFields() {
        AddInventory req = validRequest();
        Airline airline = new Airline();

        when(airlineRepository.findById(1L)).thenReturn(Optional.of(airline));
        when(inventoryRepository.existsByFlightNumberAndDepartureDate(
                req.getFlightNumber(), req.getDepartureDate())).thenReturn(false);
        when(inventoryRepository.save(any(FlightInventory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        FlightInventory result = flightInventoryService.addInventory(req);

        assertNotNull(result);
        assertEquals(100, result.getTotalSeats());
    }

    @Test
    @DisplayName("Reject a flight when required fields are missing")
    void rejectFlightWithMissingRequiredFields() {
        AddInventory req = validRequest();
        req.setFlightNumber(null);
        Airline airline = new Airline();

        when(airlineRepository.findById(1L)).thenReturn(Optional.of(airline));

        assertThrows(ValidationException.class, () -> flightInventoryService.addInventory(req));
    }

    @Test
    @DisplayName("Reject a flight when price is not positive")
    void rejectFlightWithInvalidPrice() {
        AddInventory req = validRequest();
        req.setPrice(0.0);
        Airline airline = new Airline();

        when(airlineRepository.findById(1L)).thenReturn(Optional.of(airline));

        assertThrows(ValidationException.class, () -> flightInventoryService.addInventory(req));
    }

    @Test
    @DisplayName("Reject a flight with a negative seat count")
    void rejectFlightWithNegativeSeatCount() {
        AddInventory req = validRequest();
        req.setTotalSeats(-10);
        Airline airline = new Airline();

        when(airlineRepository.findById(1L)).thenReturn(Optional.of(airline));

        assertThrows(ValidationException.class, () -> flightInventoryService.addInventory(req));
    }

    @Test
    @DisplayName("Reject a flight where arrival time is before departure time")
    void rejectFlightWithArrivalBeforeDeparture() {
        AddInventory req = validRequest();
        req.setArrivalDate(req.getDepartureDate());
        req.setArrivalTime(req.getDepartureTime().minusHours(1));
        Airline airline = new Airline();

        when(airlineRepository.findById(1L)).thenReturn(Optional.of(airline));

        assertThrows(ValidationException.class, () -> flightInventoryService.addInventory(req));
    }

    @Test
    @DisplayName("Reject a flight with departure date in the past")
    void rejectFlightWithDepartureInPast() {
        AddInventory req = validRequest();
        LocalDate past = LocalDate.now().minusDays(1);
        req.setDepartureDate(past);
        req.setArrivalDate(past);
        Airline airline = new Airline();

        when(airlineRepository.findById(1L)).thenReturn(Optional.of(airline));

        assertThrows(ValidationException.class, () -> flightInventoryService.addInventory(req));
    }

    @Test
    @DisplayName("Reject a duplicate flight for the same airline, flight number and departure date")
    void rejectDuplicateFlight() {
        AddInventory req = validRequest();
        Airline airline = new Airline();

        when(airlineRepository.findById(1L)).thenReturn(Optional.of(airline));
        when(inventoryRepository.existsByFlightNumberAndDepartureDate(
                req.getFlightNumber(), req.getDepartureDate())).thenReturn(true);

        assertThrows(ValidationException.class, () -> flightInventoryService.addInventory(req));
    }
    
    @Test
    @DisplayName("Reject flight when airline does not exist")
    void rejectWhenAirlineDoesNotExist() {
        AddInventory req = validRequest();

        when(airlineRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> flightInventoryService.addInventory(req));
    }
    
    @Test
    @DisplayName("Reject flight when source and destination are same")
    void rejectInvalidCitySelection() {
        AddInventory req = validRequest();
        req.setDestinationCity(req.getSourceCity());

        Airline airline = new Airline();
        when(airlineRepository.findById(1L)).thenReturn(Optional.of(airline));

        assertThrows(ValidationException.class, () -> flightInventoryService.addInventory(req));
    }
    @Test
    @DisplayName("Reject flight when arrival date is before departure date")
    void rejectArrivalDateBeforeDepartureDate() {
        AddInventory req = validRequest();
        req.setArrivalDate(req.getDepartureDate().minusDays(1));

        Airline airline = new Airline();
        when(airlineRepository.findById(1L)).thenReturn(Optional.of(airline));

        assertThrows(ValidationException.class, () -> flightInventoryService.addInventory(req));
    }



}
