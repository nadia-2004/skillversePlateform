package com.exchange.service;

import com.exchange.model.entity.ExchangeSession;
import com.exchange.repository.ExchangeSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionManagementServiceTest {

    @Mock
    private ExchangeSessionRepository sessionRepository;

    @Mock
    private SessionCacheService cacheService;

    @InjectMocks
    private SessionManagementService sessionService;

    private ExchangeSession testSession;

    @BeforeEach
    void setUp() {
        testSession = ExchangeSession.builder()
                .id("test-session-1")
                .bookingId("booking-1")
                .teacherId("teacher-1")
                .studentId("student-1")
                .status(ExchangeSession.SessionStatus.PENDING)
                .scheduledStart(LocalDateTime.now().plusHours(1))
                .durationMinutes(60)
                .build();
    }

    @Test
    void testCreateSession() {
        when(sessionRepository.save(any(ExchangeSession.class))).thenReturn(testSession);

        ExchangeSession created = sessionService.createSession(
                testSession.getBookingId(),
                testSession.getTeacherId(),
                testSession.getStudentId(),
                testSession.getScheduledStart(),
                testSession.getDurationMinutes(),
                "Test objectives"
        );

        assertNotNull(created);
        assertEquals("booking-1", created.getBookingId());
        verify(sessionRepository, times(1)).save(any(ExchangeSession.class));
        verify(cacheService, times(1)).saveSession(any(ExchangeSession.class));
    }

    @Test
    void testStartSession() {
        testSession.setStatus(ExchangeSession.SessionStatus.PENDING);
        when(cacheService.getSession("test-session-1")).thenReturn(null);
        when(sessionRepository.findById("test-session-1")).thenReturn(Optional.of(testSession));
        when(sessionRepository.save(any(ExchangeSession.class))).thenReturn(testSession);

        ExchangeSession started = sessionService.startSession("test-session-1");

        assertNotNull(started.getActualStart());
        verify(sessionRepository, times(1)).save(any(ExchangeSession.class));
    }

    @Test
    void testGetSessionNotFound() {
        when(cacheService.getSession("invalid-id")).thenReturn(null);
        when(sessionRepository.findById("invalid-id")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> sessionService.getSession("invalid-id"));
    }
}
