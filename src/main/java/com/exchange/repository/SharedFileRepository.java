package com.exchange.repository;

import com.exchange.model.entity.SharedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SharedFileRepository extends JpaRepository<SharedFile, String> {
    // Chercher par l'ID de la session (via la relation)
    List<SharedFile> findBySessionId(String sessionId);
}