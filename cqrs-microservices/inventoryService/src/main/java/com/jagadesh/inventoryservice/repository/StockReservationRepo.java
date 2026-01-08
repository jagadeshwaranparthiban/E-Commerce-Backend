package com.jagadesh.inventoryservice.repository;

import com.jagadesh.inventoryservice.model.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockReservationRepo extends JpaRepository<StockReservation, String> {
    Optional<List<StockReservation>> findByOrderId(String orderId);

    @Query("SELECT DISTINCT sr.orderId FROM StockReservation sr WHERE sr.expiresAt < :time")
    List<String> findDistinctOrderIdsByExpiresAtBefore(Instant time);
}
