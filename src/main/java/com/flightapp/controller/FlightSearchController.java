package com.flightapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.flightapp.model.FlightInventory;
import com.flightapp.request.FlightSearchRequest;
import com.flightapp.service.FlightSearchService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1.0/flight")
public class FlightSearchController {

    @Autowired
    private FlightSearchService searchService;

    @PostMapping("/search")
    public List<FlightInventory> searchFlights(@Valid @RequestBody FlightSearchRequest req) {
        return searchService.searchFlights(req);
    }
}
