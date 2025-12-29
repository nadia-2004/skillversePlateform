package com.exchange.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String bookingId;
    private String teacherId;
    private String studentId;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    private LocalDateTime scheduledStart;
    private LocalDateTime actualStart;
    private LocalDateTime actualEnd;
    private Integer durationMinutes;
    private String objectives;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        status = SessionStatus.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum SessionStatus {
        PENDING, ACTIVE, COMPLETED, CANCELLED
    }
}
