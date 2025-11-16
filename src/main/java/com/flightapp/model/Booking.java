package com.flightapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @Enumerated(EnumType.STRING)
    private TripType tripType;

    @ManyToOne
    @JoinColumn(name = "outbound_flight_id", nullable = false)
    private FlightInventory outboundFlight;

    @ManyToOne
    @JoinColumn(name = "return_flight_id")
    private FlightInventory returnFlight;

    @Column(nullable = false)
    private String pnrOutbound;

    private String pnrReturn;

    private String contactName;
    private String contactEmail;

    private int totalPassengers;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

	public Long getBookingId() {
		return bookingId;
	}

	public void setBookingId(Long bookingId) {
		this.bookingId = bookingId;
	}

	public TripType getTripType() {
		return tripType;
	}

	public void setTripType(TripType tripType) {
		this.tripType = tripType;
	}

	public FlightInventory getOutboundFlight() {
		return outboundFlight;
	}

	public void setOutboundFlight(FlightInventory outboundFlight) {
		this.outboundFlight = outboundFlight;
	}

	public FlightInventory getReturnFlight() {
		return returnFlight;
	}

	public void setReturnFlight(FlightInventory returnFlight) {
		this.returnFlight = returnFlight;
	}

	public String getPnrOutbound() {
		return pnrOutbound;
	}

	public void setPnrOutbound(String pnrOutbound) {
		this.pnrOutbound = pnrOutbound;
	}

	public String getPnrReturn() {
		return pnrReturn;
	}

	public void setPnrReturn(String pnrReturn) {
		this.pnrReturn = pnrReturn;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public int getTotalPassengers() {
		return totalPassengers;
	}

	public void setTotalPassengers(int totalPassengers) {
		this.totalPassengers = totalPassengers;
	}

	public BookingStatus getStatus() {
		return status;
	}

	public void setStatus(BookingStatus status) {
		this.status = status;
	}

    
    
}
