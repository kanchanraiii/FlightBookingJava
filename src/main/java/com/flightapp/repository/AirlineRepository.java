package com.flightapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.flightapp.model.Airline;

public interface AirlineRepository extends JpaRepository<Airline, Long> {
}
