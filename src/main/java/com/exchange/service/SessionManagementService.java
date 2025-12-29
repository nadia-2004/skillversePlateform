package com.exchange.service;

import com.exchange.model.entity.ExchangeSession;
import com.exchange.repository.ExchangeSessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Transactional
public class SessionManagementService {

    private final ExchangeSessionRepository sessionRepository;
    private final SessionCacheService cacheService;

    public SessionManagementService(ExchangeSessionRepository sessionRepository,
                                   SessionCacheService cacheService) {
        this.sessionRepository = sessionRepository;
        this.cacheService = cacheService;
    }

    public ExchangeSession createSession(String bookingId, String teacherId, String studentId,
                                        LocalDateTime scheduledStart, Integer durationMinutes,
                                        String objectives) {
        ExchangeSession session = ExchangeSession.builder()
                .bookingId(bookingId)
                .teacherId(teacherId)
                .studentId(studentId)
                .scheduledStart(scheduledStart)
                .durationMinutes(durationMinutes)
                .objectives(objectives)
                .status(ExchangeSession.SessionStatus.PENDING)
                .build();

        ExchangeSession saved = sessionRepository.save(session);
        cacheService.saveSession(saved);
        log.info("Session created: {} for booking: {}", saved.getId(), bookingId);
        return saved;
    }

    public ExchangeSession getSession(String sessionId) {
        ExchangeSession cached = cacheService.getSession(sessionId);
        if (cached != null) {
            return cached;
        }
        ExchangeSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        cacheService.saveSession(session);
        return session;
    }

    public ExchangeSession startSession(String sessionId) {
        ExchangeSession session = getSession(sessionId);
        session.setStatus(ExchangeSession.SessionStatus.ACTIVE);
        session.setActualStart(LocalDateTime.now());
        ExchangeSession updated = sessionRepository.save(session);
        cacheService.saveSession(updated);
        log.info("Session started: {}", sessionId);
        return updated;
    }

    public ExchangeSession endSession(String sessionId) {
        ExchangeSession session = getSession(sessionId);
        session.setStatus(ExchangeSession.SessionStatus.COMPLETED);
        session.setActualEnd(LocalDateTime.now());
        ExchangeSession updated = sessionRepository.save(session);
        cacheService.saveSession(updated);
        log.info("Session ended: {}", sessionId);
        return updated;
    }

    public ExchangeSession joinSession(String sessionId, String userId) {
        ExchangeSession session = getSession(sessionId);
        if (!ExchangeSession.SessionStatus.ACTIVE.equals(session.getStatus())) {
            throw new RuntimeException("Session is not active");
        }
        log.info("User {} joined session {}", userId, sessionId);
        return session;
    }

    public List<ExchangeSession> getTeacherSessions(String teacherId) {
        return sessionRepository.findByTeacherId(teacherId);
    }

    public List<ExchangeSession> getStudentSessions(String studentId) {
        return sessionRepository.findByStudentId(studentId);
    }
}
