package com.exchange.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "shared_files")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedFile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private ExchangeSession session;

    private String uploadedBy;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private String s3Key;
    private String downloadUrl;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
