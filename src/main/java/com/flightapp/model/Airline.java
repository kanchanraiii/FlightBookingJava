package com.flightapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "airline")
public class Airline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long airlineId;

    @Column(nullable = false, unique = true)
    private String airlineCode;

    @Column(nullable = false)
    private String airlineName;

	public Long getAirlineId() {
		return airlineId;
	}

	public void setAirlineId(Long airlineId) {
		this.airlineId = airlineId;
	}

	public String getAirlineCode() {
		return airlineCode;
	}

	public void setAirlineCode(String airlineCode) {
		this.airlineCode = airlineCode;
	}

	public String getAirlineName() {
		return airlineName;
	}

	public void setAirlineName(String airlineName) {
		this.airlineName = airlineName;
	}

    
}
