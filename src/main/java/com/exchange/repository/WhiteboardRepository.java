package com.exchange.repository;

import com.exchange.model.entity.Whiteboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WhiteboardRepository extends JpaRepository<Whiteboard, String> {
    Optional<Whiteboard> findBySessionId(String sessionId);
}
