package com.jagadesh.inventoryservice.repository;

import com.jagadesh.inventoryservice.model.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedEventRepo extends JpaRepository<ProcessedEvent, String> {
}
