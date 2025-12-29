package com.exchange.controller;

import com.exchange.service.WhiteboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class WhiteboardWebSocketController {

    private final WhiteboardService whiteboardService;

    public WhiteboardWebSocketController(WhiteboardService whiteboardService) {
        this.whiteboardService = whiteboardService;
    }

    @MessageMapping("/whiteboard/{sessionId}")
    public void updateWhiteboard(
            @DestinationVariable String sessionId,
            @Payload String content) {
        log.debug("Whiteboard update received for session: {}", sessionId);
        whiteboardService.updateWhiteboardContent(sessionId, content);
    }
}
