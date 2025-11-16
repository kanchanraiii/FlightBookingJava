package com.flightapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.flightapp.model.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long> {
}
