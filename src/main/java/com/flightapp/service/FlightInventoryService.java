package com.flightapp.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;

import com.flightapp.model.FlightInventory;
import com.flightapp.model.Airline;
import com.flightapp.repository.FlightInventoryRepository;
import com.flightapp.repository.AirlineRepository;
import com.flightapp.request.AddInventory;

@Service
public class FlightInventoryService {

    @Autowired
    private FlightInventoryRepository inventoryRepository;

    @Autowired
    private AirlineRepository airlineRepository;

    @SuppressWarnings("null")
	public FlightInventory addInventory(@Valid AddInventory req) {

        Airline airline = airlineRepository.findById(req.getAirlineId())
                .orElseThrow(() -> new RuntimeException("Airline not found"));

        FlightInventory inv = new FlightInventory();
        inv.setAirline(airline);
        inv.setFlightNumber(req.getFlightNumber());
        inv.setSourceCity(req.getSourceCity());
        inv.setDestinationCity(req.getDestinationCity());
        inv.setDepartureDate(req.getDepartureDate());
        inv.setDepartureTime(req.getDepartureTime());
        inv.setArrivalDate(req.getArrivalDate());
        inv.setArrivalTime(req.getArrivalTime());
        inv.setMealAvailable(req.isMealAvailable());
        inv.setTotalSeats(req.getTotalSeats());
        inv.setAvailableSeats(req.getTotalSeats());
        inv.setPrice(req.getPrice());

        return inventoryRepository.save(inv);
    }
}
