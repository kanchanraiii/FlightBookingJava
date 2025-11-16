package com.flightapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "seat_map")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatId;

    @ManyToOne
    @JoinColumn(name = "flight_id", nullable = false)
    private FlightInventory flight;

    @Column(nullable = false)
    private String seatNo;

    private boolean isAvailable;

	public Long getSeatId() {
		return seatId;
	}

	public void setSeatId(Long seatId) {
		this.seatId = seatId;
	}

	public FlightInventory getFlight() {
		return flight;
	}

	public void setFlight(FlightInventory flight) {
		this.flight = flight;
	}

	public String getSeatNo() {
		return seatNo;
	}

	public void setSeatNo(String seatNo) {
		this.seatNo = seatNo;
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

   
    
}
