package com.example.notification_service.integration;

import com.example.notification_service.dto.EmailRequest;
import com.example.notification_service.dto.PushNotificationRequest;
import com.example.notification_service.entity.NotificationHistory;
import com.example.notification_service.repository.NotificationHistoryRepository;
import com.example.notification_service.service.EmailService;
import com.example.notification_service.service.NotificationHistoryService;
import com.example.notification_service.service.NotificationPush;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class IntegrationFullIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NotificationHistoryRepository historyRepository;

    @Autowired
    private NotificationHistoryService historyService;

    @MockBean
    private EmailService emailService; // Mock pour éviter les appels SMTP externes

    @SpyBean
    private NotificationPush notificationPush; // Spy pour éviter les appels FCM externes

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        historyRepository.deleteAll();
        
        // Configurer le spy pour sauvegarder dans H2 sans appeler FCM
        doAnswer(invocation -> {
            String token = invocation.getArgument(0);
            String title = invocation.getArgument(1);
            String body = invocation.getArgument(2);
            
            NotificationHistory history = new NotificationHistory();
            history.setToken(token);
            history.setTitle(title);
            history.setBody(body);
            history.setDateSent(LocalDateTime.now());
            history.setStatus("SENT");
            
            historyService.save(history);
            return null;
        }).when(notificationPush).sendNotification(anyString(), anyString(), anyString());
    }

    @Test
    void sendEmail_shouldReturn200() throws Exception {
        EmailRequest request = new EmailRequest();
        request.setTo("test@example.com");
        request.setSubject("Test Subject");
        request.setBody("Test Body");
        request.setFrom("sender@example.com");

        doNothing().when(emailService).sendEmail(any());

        mockMvc.perform(post("/api/notifications/send-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void sendPush_shouldStoreInHistory() throws Exception {
        PushNotificationRequest request = new PushNotificationRequest();
        request.setToken("test-token-123");
        request.setTitle("Test Push");
        request.setBody("Hello Push");

        mockMvc.perform(post("/api/push")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        List<NotificationHistory> histories = historyRepository.findAll();
        assertThat(histories).hasSize(1);
        assertThat(histories.get(0).getToken()).isEqualTo("test-token-123");
        assertThat(histories.get(0).getTitle()).isEqualTo("Test Push");
    }

    @Test
    void getHistory_shouldReturnNotifications() throws Exception {
        // Créer des données de test directement en DB
        NotificationHistory history1 = new NotificationHistory();
        history1.setToken("token-1");
        history1.setTitle("Title 1");
        history1.setBody("Body 1");
        history1.setStatus("SENT");
        history1.setDateSent(LocalDateTime.now());
        historyRepository.save(history1);

        NotificationHistory history2 = new NotificationHistory();
        history2.setToken("token-2");
        history2.setTitle("Title 2");
        history2.setBody("Body 2");
        history2.setStatus("SENT");
        history2.setDateSent(LocalDateTime.now());
        historyRepository.save(history2);

        mockMvc.perform(get("/api/history"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].token").exists())
                .andExpect(jsonPath("$[1].token").exists());
    }
}
