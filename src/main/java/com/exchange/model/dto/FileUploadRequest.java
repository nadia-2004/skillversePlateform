package com.exchange.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUploadRequest {
    @NotBlank
    private String fileName;
    
    @NotNull
    private Long fileSize;
    
    @NotBlank
    private String fileType;
    
    @NotBlank
    private String uploadedBy;
}
