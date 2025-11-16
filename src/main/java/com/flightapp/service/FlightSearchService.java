package com.flightapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;

import com.flightapp.model.FlightInventory;
import com.flightapp.repository.FlightInventoryRepository;
import com.flightapp.request.FlightSearchRequest;

@Service
public class FlightSearchService {

    @Autowired
    FlightInventoryRepository inventoryRepository;

    public List<FlightInventory> searchFlights(@Valid FlightSearchRequest req) {
        return inventoryRepository.findBySourceCityAndDestinationCityAndDepartureDate(
                req.getSourceCity(),
                req.getDestinationCity(),
                req.getTravelDate()
        );
    }
}
