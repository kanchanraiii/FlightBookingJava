package com.flightapp.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;

import com.flightapp.model.FlightInventory;
import com.flightapp.exceptions.ResourceNotFoundException;
import com.flightapp.exceptions.ValidationException;
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
                .orElseThrow(() -> new ResourceNotFoundException("Airline not found"));
        
        if(req.getPrice()<=0) {
        	throw new ValidationException("Price must be greater than 0");
        }
        
        if(req.getTotalSeats()<=0) {
        	throw new ValidationException("Seats must be greater than zero");
        }
        
        if(req.getSourceCity().equals(req.getDestinationCity())) {
        	throw new ValidationException("Source and destination cities cannot be same");
        }
        
        if(req.getSourceCity()==null) {
        	throw new ValidationException("Source is a required field");
        }
        
        if(req.getDestinationCity()==null) {
        	throw new ValidationException("Destination is a required field");
        }
        
        if(req.getFlightNumber()==null || req.getFlightNumber().isBlank()) {
        	throw new ValidationException("Flight no is a required field");
        }
        
        LocalDate departureDate = req.getDepartureDate();
        LocalDate arrivalDate = req.getArrivalDate();
        LocalTime departureTime = req.getDepartureTime();
        LocalTime arrivalTime = req.getArrivalTime();

        if (departureDate == null) {
            throw new ValidationException("Departure date is a required field");
        }

        if (arrivalDate == null) {
            throw new ValidationException("Arrival date is a required field");
        }

        if (departureTime == null) {
            throw new ValidationException("Departure time is a required field");
        }

        if (arrivalTime == null) {
            throw new ValidationException("Arrival time is a required field");
        }

        
        if (departureDate.isBefore(LocalDate.now())) {
            throw new ValidationException("Departure date cannot be in the past");
        }

        
        LocalDateTime depDT = LocalDateTime.of(departureDate, departureTime);
        LocalDateTime arrDT = LocalDateTime.of(arrivalDate, arrivalTime);

        if (!arrDT.isAfter(depDT)) {
            throw new ValidationException("Arrival date & time must be AFTER departure date & time");
        }

        
        
        

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
