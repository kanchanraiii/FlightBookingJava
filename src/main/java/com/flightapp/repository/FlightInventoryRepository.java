package com.flightapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.flightapp.model.FlightInventory;

public interface FlightInventoryRepository extends JpaRepository<FlightInventory, Long> {
}
