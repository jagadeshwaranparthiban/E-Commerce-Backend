package com.jagadesh.productQueryService.repository;

import com.jagadesh.productQueryService.model.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedEventRepo extends JpaRepository<ProcessedEvent,String> {
}
