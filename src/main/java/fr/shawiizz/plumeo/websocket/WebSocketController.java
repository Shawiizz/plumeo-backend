package fr.shawiizz.plumeo.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

/**
 * Simple WebSocket controller for client-server communication.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final WebSocketSessionManager sessionManager;

    @MessageMapping("/data")
    public void handleData(@Payload String message, SimpMessageHeaderAccessor headerAccessor) {
        
        String userEmail = (String) headerAccessor.getSessionAttributes().get("userEmail");
        
        if (userEmail == null) {
            log.warn("Received data from unauthenticated session");
            return;
        }
        
        log.info("Received data from user {}: {}", userEmail, message);
    }

}