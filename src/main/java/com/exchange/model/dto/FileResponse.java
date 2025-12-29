package com.exchange.model.dto;

import com.exchange.model.entity.SharedFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileResponse {
    private String id;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private String uploadedBy;
    private String downloadUrl;
    private LocalDateTime createdAt;

    public static FileResponse from(SharedFile file) {
        return FileResponse.builder()
                .id(file.getId())
                .fileName(file.getFileName())
                .fileSize(file.getFileSize())
                .fileType(file.getFileType())
                .uploadedBy(file.getUploadedBy())
                .downloadUrl(file.getDownloadUrl())
                .createdAt(file.getCreatedAt())
                .build();
    }
}
