package com.flightapp.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class BookingRequest {

    @NotNull
    private Long outboundFlightId;

    private Long returnFlightId;

    @NotBlank
    private String contactName;

    @Email
    @NotBlank
    private String contactEmail;

    @Valid
    @NotNull
    private List<PassengerRequest> passengers;

    // Getters + Setters
    public Long getOutboundFlightId() { 
    	return outboundFlightId; 
    }
    public void setOutboundFlightId(Long outboundFlightId) { 
    	this.outboundFlightId = outboundFlightId; 
    }

    public Long getReturnFlightId() { 
    	return returnFlightId; 
    }
    public void setReturnFlightId(Long returnFlightId) { 
    	this.returnFlightId = returnFlightId; 
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

    public List<PassengerRequest> getPassengers() { 
    	return passengers; 
    }
    public void setPassengers(List<PassengerRequest> passengers) { 
    	this.passengers = passengers; 
    }
}
