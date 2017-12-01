package com.dssathe.cloudburst.repository;

import com.dssathe.cloudburst.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long>{

}
