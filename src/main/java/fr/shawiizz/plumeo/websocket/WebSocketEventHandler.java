package fr.shawiizz.plumeo.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;

/**
 * Handles WebSocket connection and disconnection events.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventHandler {

    private final WebSocketSessionManager sessionManager;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Handle WebSocket connection event.
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        log.info("WebSocket connection established. Session ID: {}", sessionId);

        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // Send "Hello world" message to test-topic when user connects
            messagingTemplate.convertAndSend("/topic/test-topic", Map.of(
                    "message", "Hello world",
                    "timestamp", System.currentTimeMillis()
            ));

            log.info("Sent Hello world message to /topic/test-topic for session: {}", sessionId);

        }).start();
    }

    /**
     * Handle WebSocket disconnection event.
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        log.info("WebSocket connection closed. Session ID: {}", sessionId);
        
        // Clean up session data
        // Note: The actual WebSocketSession cleanup is handled by the session manager
        // when the session is properly closed
    }
}