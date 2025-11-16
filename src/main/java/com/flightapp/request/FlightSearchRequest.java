package com.flightapp.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

import com.flightapp.model.CityEnum;

public class FlightSearchRequest {

    @NotNull
    private CityEnum sourceCity;

    @NotNull
    private CityEnum destinationCity;

    @NotNull
    private LocalDate travelDate;

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

    public LocalDate getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(LocalDate travelDate) {
        this.travelDate = travelDate;
    }
}
