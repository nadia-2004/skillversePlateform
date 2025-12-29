package com.exchange.repository;

import com.exchange.model.entity.ExchangeSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeSessionRepository extends JpaRepository<ExchangeSession, String> {
    List<ExchangeSession> findByTeacherId(String teacherId);
    List<ExchangeSession> findByStudentId(String studentId);
    List<ExchangeSession> findByBookingId(String bookingId);
}
