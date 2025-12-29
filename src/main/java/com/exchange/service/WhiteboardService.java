package com.exchange.service;

import com.exchange.model.entity.Whiteboard;
import com.exchange.model.entity.ExchangeSession;
import com.exchange.repository.WhiteboardRepository;
import com.exchange.repository.ExchangeSessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class WhiteboardService {

    private final WhiteboardRepository whiteboardRepository;
    private final ExchangeSessionRepository sessionRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public WhiteboardService(WhiteboardRepository whiteboardRepository,
                            ExchangeSessionRepository sessionRepository,
                            SimpMessagingTemplate messagingTemplate) {
        this.whiteboardRepository = whiteboardRepository;
        this.sessionRepository = sessionRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public Whiteboard getOrCreateWhiteboard(String sessionId) {
        return whiteboardRepository.findBySessionId(sessionId)
                .orElseGet(() -> createWhiteboard(sessionId));
    }

    private Whiteboard createWhiteboard(String sessionId) {
        ExchangeSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        Whiteboard whiteboard = Whiteboard.builder()
                .session(session)  
                .content("{\"drawings\":[]}")
                .build();
        
        Whiteboard saved = whiteboardRepository.save(whiteboard);
        log.info("Whiteboard created for session: {}", sessionId);
        return saved;
    }

    public void updateWhiteboardContent(String sessionId, String content) {
        Whiteboard whiteboard = getOrCreateWhiteboard(sessionId);
        whiteboard.setContent(content);
        whiteboardRepository.save(whiteboard);
        
        // Broadcast update to all connected clients
        messagingTemplate.convertAndSend(
                "/topic/whiteboard/" + sessionId,
                content
        );
        log.debug("Whiteboard updated for session: {}", sessionId);
    }

    public Whiteboard getWhiteboard(String sessionId) {
        return getOrCreateWhiteboard(sessionId);
    }

    public void clearWhiteboard(String sessionId) {
        Whiteboard whiteboard = getOrCreateWhiteboard(sessionId);
        whiteboard.setContent("{\"drawings\":[]}");
        whiteboardRepository.save(whiteboard);
        
        messagingTemplate.convertAndSend(
                "/topic/whiteboard/" + sessionId,
                "{\"drawings\":[]}"
        );
        log.info("Whiteboard cleared for session: {}", sessionId);
    }
}