package com.flightapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.flightapp.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByPnrOutbound(String pnr);
    Optional<Booking> findByPnrReturn(String pnr);

    List<Booking> findByContactEmail(String email);
}
