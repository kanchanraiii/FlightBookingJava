package com.flightapp.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "flight_inventory")
public class FlightInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flightId;

    @ManyToOne
    @JoinColumn(name = "airline_id", nullable = false)
    private Airline airline;

    @Column(name = "flight_number", nullable = false)
    private String flightNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_city", nullable = false)
    private CityEnum sourceCity;

    @Enumerated(EnumType.STRING)
    @Column(name = "destination_city", nullable = false)
    private CityEnum destinationCity;

    @Column(name = "departure_date", nullable = false)
    private LocalDate departureDate;

    @Column(name = "departure_time", nullable = false)
    private LocalTime departureTime;

    @Column(name = "arrival_date", nullable = false)
    private LocalDate arrivalDate;

    @Column(name = "arrival_time", nullable = false)
    private LocalTime arrivalTime;

    @Column(name = "meal_available", nullable = false)
    private boolean mealAvailable;

    @Column(name = "total_seats")
    private int totalSeats;

    @Column(name = "available_seats")
    private int availableSeats;

    @Column(name = "price")
    private double price;


	public Long getFlightId() {
		return flightId;
	}

	public void setFlightId(Long flightId) {
		this.flightId = flightId;
	}

	public Airline getAirline() {
		return airline;
	}

	public void setAirline(Airline airline) {
		this.airline = airline;
	}

	public String getFlightNumber() {
		return flightNumber;
	}

	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

	public CityEnum getSourceCity() {
		return sourceCity;
	}

	public void setSourceCity(CityEnum sourceCity) {
		this.sourceCity = sourceCity;
	}

	public CityEnum getDestinationCity() {
		return destinationCity;
	}

	public void setDestinationCity(CityEnum destinationCity) {
		this.destinationCity = destinationCity;
	}

	public LocalDate getDepartureDate() {
		return departureDate;
	}

	public void setDepartureDate(LocalDate departureDate) {
		this.departureDate = departureDate;
	}

	public LocalTime getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(LocalTime departureTime) {
		this.departureTime = departureTime;
	}

	public LocalDate getArrivalDate() {
		return arrivalDate;
	}

	public void setArrivalDate(LocalDate arrivalDate) {
		this.arrivalDate = arrivalDate;
	}

	public LocalTime getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(LocalTime arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public boolean isMealAvailable() {
		return mealAvailable;
	}

	public void setMealAvailable(boolean mealAvailable) {
		this.mealAvailable = mealAvailable;
	}

	public int getTotalSeats() {
		return totalSeats;
	}

	public void setTotalSeats(int totalSeats) {
		this.totalSeats = totalSeats;
	}

	public int getAvailableSeats() {
		return availableSeats;
	}

	public void setAvailableSeats(int availableSeats) {
		this.availableSeats = availableSeats;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

    
}
