package com.flightapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.flightapp.model.FlightInventory;
import com.flightapp.request.AddInventory;
import com.flightapp.service.FlightInventoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1.0/flight")
public class FlightInventoryController {

    @Autowired
    private FlightInventoryService inventoryService;

    @PostMapping("/airline/inventory/add")
    public FlightInventory addInventory(@Valid @RequestBody AddInventory req) {
        return inventoryService.addInventory(req);
    }
}
