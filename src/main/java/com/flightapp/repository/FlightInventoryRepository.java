package com.flightapp.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flightapp.model.CityEnum;
import com.flightapp.model.FlightInventory;

public interface FlightInventoryRepository extends JpaRepository<FlightInventory, Long> {
	List<FlightInventory> findBySourceCityAndDestinationCityAndDepartureDate(
	        CityEnum sourceCity,
	        CityEnum destinationCity,
	        LocalDate departureDate
	);

}
