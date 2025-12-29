package com.exchange.controller;

import com.exchange.model.dto.FileUploadRequest;
import com.exchange.model.entity.SharedFile;
import com.exchange.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/files")
@Slf4j
@Tag(name = "Files", description = "File storage and sharing endpoints")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/{sessionId}/upload")
    @Operation(summary = "Generate signed S3 upload URL")
    public ResponseEntity<Map<String, String>> generateUploadUrl(
            @PathVariable String sessionId,
            @Valid @RequestBody FileUploadRequest request) {
        String uploadUrl = fileStorageService.generateUploadUrl(sessionId, request.getFileName());
        Map<String, String> response = new HashMap<>();
        response.put("uploadUrl", uploadUrl);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{sessionId}")
    @Operation(summary = "List all files in a session")
    public ResponseEntity<List<SharedFile>> getSessionFiles(@PathVariable String sessionId) {
        List<SharedFile> files = fileStorageService.getSessionFiles(sessionId);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/{sessionId}/{fileId}/download")
    @Operation(summary = "Generate signed S3 download URL")
    public ResponseEntity<Map<String, String>> generateDownloadUrl(
            @PathVariable String sessionId,
            @PathVariable String fileId) {
        String downloadUrl = fileStorageService.generateDownloadUrl(sessionId, fileId);
        Map<String, String> response = new HashMap<>();
        response.put("downloadUrl", downloadUrl);
        return ResponseEntity.ok(response);
    }
}
