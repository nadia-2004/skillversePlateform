package com.exchange.service;

import com.exchange.model.entity.SharedFile;
import com.exchange.model.entity.ExchangeSession;
import com.exchange.repository.SharedFileRepository;
import com.exchange.repository.ExchangeSessionRepository; // Ajout
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class FileStorageService {

        private final SharedFileRepository fileRepository;
        private final ExchangeSessionRepository sessionRepository; // Ajout
        private final S3Presigner s3Presigner;
        private final S3Client s3Client;

        @Value("${aws.s3.bucket-name}")
        private String bucketName;

    // Modifier le constructeur pour inclure ExchangeSessionRepository
        public FileStorageService(SharedFileRepository fileRepository,
                                ExchangeSessionRepository sessionRepository, // Ajout
                                S3Presigner s3Presigner,
                                S3Client s3Client) {
        this.fileRepository = fileRepository;
        this.sessionRepository = sessionRepository; // Initialisation
        this.s3Presigner = s3Presigner;
        this.s3Client = s3Client;
        }

        public String generateUploadUrl(String sessionId, String fileName) {
        String s3Key = generateS3Key(sessionId, fileName);
        
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .putObjectRequest(putObjectRequest)
                .build();

        String uploadUrl = s3Presigner.presignPutObject(presignRequest).url().toString();
        log.info("Generated upload URL for file: {} in session: {}", fileName, sessionId);
        return uploadUrl;
        }

        public String generateDownloadUrl(String sessionId, String fileId) {
        SharedFile file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(file.getS3Key())
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(1))
                .getObjectRequest(getObjectRequest)
                .build();

        String downloadUrl = s3Presigner.presignGetObject(presignRequest).url().toString();
        log.info("Generated download URL for file: {}", fileId);
        return downloadUrl;
        }

    // CORRECTION DE LA LIGNE 82 : remplacer .sessionId(sessionId) par .session(session)
        public SharedFile registerFileUpload(String sessionId, String uploadedBy, String fileName,
                                        Long fileSize, String fileType) {
        // Récupérer l'objet ExchangeSession depuis la base de données
        ExchangeSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + sessionId));
        
        String s3Key = generateS3Key(sessionId, fileName);
        
        SharedFile file = SharedFile.builder()
                .session(session) // CORRECTION ICI : utiliser .session() au lieu de .sessionId()
                .uploadedBy(uploadedBy)
                .fileName(fileName)
                .fileSize(fileSize)
                .fileType(fileType)
                .s3Key(s3Key)
                .build();

        SharedFile saved = fileRepository.save(file);
        log.info("File registered: {} for session: {}", saved.getId(), sessionId);
        return saved;
        }

        public List<SharedFile> getSessionFiles(String sessionId) {
        return fileRepository.findBySessionId(sessionId);
        }

        private String generateS3Key(String sessionId, String fileName) {
        return String.format("sessions/%s/files/%s-%s", 
                sessionId, UUID.randomUUID(), fileName.replaceAll("\\s+", "_"));
        }
}