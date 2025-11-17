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

        
        if (req.getFlightNumber() == null || req.getFlightNumber().isBlank()) {
            throw new ValidationException("Flight number is a required field");
        }

        if (req.getSourceCity() == null) {
            throw new ValidationException("Source city is a required field");
        }

        if (req.getDestinationCity() == null) {
            throw new ValidationException("Destination city is a required field");
        }

        if (req.getSourceCity().equals(req.getDestinationCity())) {
            throw new ValidationException("Source and destination cities cannot be the same");
        }

        if (req.getTotalSeats() <= 0) {
            throw new ValidationException("Total seats must be greater than zero");
        }

        if (req.getPrice() <= 0) {
            throw new ValidationException("Price must be greater than 0");
        }
        
        if(req.getPrice()==null) {
        	throw new ValidationException("Price is required");
        }
        

       
        LocalDate departureDate = req.getDepartureDate();
        LocalDate arrivalDate = req.getArrivalDate();
        LocalTime departureTime = req.getDepartureTime();
        LocalTime arrivalTime = req.getArrivalTime();

        if (departureDate == null) {
            throw new ValidationException("Departure date is required");
        }

        if (arrivalDate == null) {
            throw new ValidationException("Arrival date is required");
        }

        if (departureTime == null) {
            throw new ValidationException("Departure time is required");
        }

        if (arrivalTime == null) {
            throw new ValidationException("Arrival time is required");
        }

       
        LocalDateTime depDT = LocalDateTime.of(departureDate, departureTime);
        LocalDateTime now = LocalDateTime.now();

        if (!depDT.isAfter(now)) {
            throw new ValidationException("Departure date & time must be in the future");
        }

        
        LocalDateTime arrDT = LocalDateTime.of(arrivalDate, arrivalTime);

        if (!arrDT.isAfter(depDT)) {
            throw new ValidationException("Arrival date & time must be after departure date & time");
        }

       
        if (inventoryRepository.existsByFlightNumberAndDepartureDate(
                req.getFlightNumber(), departureDate)) {

            throw new ValidationException(
                    "A flight with this number is already scheduled for the same date");
        }

       
        FlightInventory inv = new FlightInventory();
        inv.setAirline(airline);
        inv.setFlightNumber(req.getFlightNumber());
        inv.setSourceCity(req.getSourceCity());
        inv.setDestinationCity(req.getDestinationCity());
        inv.setDepartureDate(departureDate);
        inv.setDepartureTime(departureTime);
        inv.setArrivalDate(arrivalDate);
        inv.setArrivalTime(arrivalTime);
        inv.setMealAvailable(req.isMealAvailable());
        inv.setTotalSeats(req.getTotalSeats());
        inv.setAvailableSeats(req.getTotalSeats());
        inv.setPrice(req.getPrice());

        return inventoryRepository.save(inv);
    }
}
