package com.exchange.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "whiteboards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Whiteboard {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "session_id", unique = true)
    private ExchangeSession session;

    @Column(columnDefinition = "jsonb")
    private String content;

    private LocalDateTime lastModified;

    @PrePersist
    protected void onCreate() {
        lastModified = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastModified = LocalDateTime.now();
    }
}
