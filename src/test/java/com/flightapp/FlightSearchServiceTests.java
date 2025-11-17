package com.flightapp;

import com.flightapp.exceptions.ResourceNotFoundException;
import com.flightapp.exceptions.ValidationException;
import com.flightapp.model.Airline;
import com.flightapp.model.CityEnum;
import com.flightapp.model.FlightInventory;
import com.flightapp.model.TripType;
import com.flightapp.repository.FlightInventoryRepository;
import com.flightapp.request.FlightSearchRequest;
import com.flightapp.service.FlightSearchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlightSearchServiceTests {

    @Mock
    private FlightInventoryRepository inventoryRepository;

    @InjectMocks
    private FlightSearchService flightSearchService;

    private FlightSearchRequest validOneWayRequest(LocalDate travelDate) {
        FlightSearchRequest req = new FlightSearchRequest();
        req.setSourceCity(CityEnum.DELHI);
        req.setDestinationCity(CityEnum.MUMBAI);
        req.setTravelDate(travelDate);
        req.setTripType(TripType.ONE_WAY);
        return req;
    }

    private FlightInventory sampleFlight(LocalDate departureDate) {
        FlightInventory inv = new FlightInventory();
        inv.setFlightNumber("AI201");
        inv.setSourceCity(CityEnum.DELHI);
        inv.setDestinationCity(CityEnum.MUMBAI);
        inv.setDepartureDate(departureDate);
        inv.setDepartureTime(LocalTime.of(9, 0));
        inv.setPrice(4000.0);

        Airline airline = new Airline();
        airline.setAirlineName("Sample Airline");
        airline.setAirlineCode("SA");
        inv.setAirline(airline);

        return inv;
    }

    @Test
    @DisplayName("Search one-way flight with valid source, destination and date")
    void searchOneWayFlightWithValidDetails() {
        LocalDate futureDate = LocalDate.now().plusDays(5);
        FlightSearchRequest req = validOneWayRequest(futureDate);

        when(inventoryRepository.findBySourceCityAndDestinationCityAndDepartureDate(
                req.getSourceCity(), req.getDestinationCity(), req.getTravelDate()))
                .thenReturn(List.of(sampleFlight(futureDate)));

        List<FlightInventory> results = flightSearchService.searchFlights(req);

        assertEquals(1, results.size());
        assertEquals(futureDate, results.get(0).getDepartureDate());
    }

    @Test
    @DisplayName("Search round-trip flight with valid details")
    void searchRoundTripFlightWithValidDetails() {
        LocalDate travel = LocalDate.now().plusDays(10);
        LocalDate ret = travel.plusDays(3);

        FlightSearchRequest req = validOneWayRequest(travel);
        req.setTripType(TripType.ROUND_TRIP);
        req.setReturnDate(ret);

        when(inventoryRepository.findBySourceCityAndDestinationCityAndDepartureDate(
                req.getSourceCity(), req.getDestinationCity(), req.getTravelDate()))
                .thenReturn(List.of(sampleFlight(travel)));

        List<FlightInventory> results = flightSearchService.searchFlights(req);

        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("Search results contain flight date/time, airline and price")
    void searchResultsContainKeyFields() {
        LocalDate futureDate = LocalDate.now().plusDays(7);
        FlightSearchRequest req = validOneWayRequest(futureDate);

        FlightInventory inv = sampleFlight(futureDate);

        when(inventoryRepository.findBySourceCityAndDestinationCityAndDepartureDate(
                any(), any(), any())).thenReturn(List.of(inv));

        List<FlightInventory> results = flightSearchService.searchFlights(req);

        FlightInventory result = results.get(0);
        assertNotNull(result.getDepartureDate());
        assertNotNull(result.getDepartureTime());
        assertNotNull(result.getAirline());
        assertNotNull(result.getPrice());
    }

    @Test
    @DisplayName("Reject search when required field is missing")
    void rejectSearchWhenRequiredFieldMissing() {
        LocalDate futureDate = LocalDate.now().plusDays(5);
        FlightSearchRequest req = validOneWayRequest(futureDate);
        req.setSourceCity(null);

        assertThrows(ValidationException.class, () -> flightSearchService.searchFlights(req));
    }

    @Test
    @DisplayName("Reject search when source and destination are the same")
    void rejectSearchWhenSourceAndDestinationSame() {
        LocalDate futureDate = LocalDate.now().plusDays(5);
        FlightSearchRequest req = validOneWayRequest(futureDate);
        req.setDestinationCity(CityEnum.DELHI);

        assertThrows(ValidationException.class, () -> flightSearchService.searchFlights(req));
    }

    @Test
    @DisplayName("Reject search when travel date is in the past")
    void rejectSearchWhenDateInPast() {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        FlightSearchRequest req = validOneWayRequest(pastDate);

        assertThrows(ValidationException.class, () -> flightSearchService.searchFlights(req));
    }

    @Test
    @DisplayName("Return not found when no flights exist for route")
    void noFlightsForRouteThrowsNotFound() {
        LocalDate futureDate = LocalDate.now().plusDays(5);
        FlightSearchRequest req = validOneWayRequest(futureDate);

        when(inventoryRepository.findBySourceCityAndDestinationCityAndDepartureDate(
                req.getSourceCity(), req.getDestinationCity(), req.getTravelDate()))
                .thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> flightSearchService.searchFlights(req));
    }

    @Test
    @DisplayName("Search for flights on current day (today)")
    void searchFlightsForToday() {
        LocalDate today = LocalDate.now();
        FlightSearchRequest req = validOneWayRequest(today);

        when(inventoryRepository.findBySourceCityAndDestinationCityAndDepartureDate(
                req.getSourceCity(), req.getDestinationCity(), req.getTravelDate()))
                .thenReturn(List.of(sampleFlight(today)));

        List<FlightInventory> results = flightSearchService.searchFlights(req);

        assertEquals(1, results.size());
    }
}