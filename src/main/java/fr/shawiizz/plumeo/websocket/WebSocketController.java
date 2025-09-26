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

    /**
     * Handle ping messages from clients (for keep-alive or testing).
     */
    @MessageMapping("/ping")
    public void handlePing(@Payload PingMessage message, SimpMessageHeaderAccessor headerAccessor) {
        
        String userEmail = (String) headerAccessor.getSessionAttributes().get("userEmail");
        
        if (userEmail == null) {
            log.warn("Received ping from unauthenticated session");
            return;
        }
        
        log.info("Received ping from user {}: {}", userEmail, message.getMessage());
        
        // Simple acknowledgment - could be extended to send response back to specific client
    }

    /**
     * Handle simple data messages from clients.
     */
    @MessageMapping("/data")
    public void handleData(@Payload DataMessage message, SimpMessageHeaderAccessor headerAccessor) {
        
        String userEmail = (String) headerAccessor.getSessionAttributes().get("userEmail");
        
        if (userEmail == null) {
            log.warn("Received data from unauthenticated session");
            return;
        }
        
        log.info("Received data from user {}: {}", userEmail, message.getData());
        
        // Process the data as needed - store in database, trigger business logic, etc.
    }

    // Simple DTO classes
    
    public static class PingMessage {
        private String message;
        
        public PingMessage() {}
        
        public PingMessage(String message) {
            this.message = message;
        }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    public static class DataMessage {
        private String data;
        private String type;
        
        public DataMessage() {}
        
        public DataMessage(String data, String type) {
            this.data = data;
            this.type = type;
        }
        
        public String getData() { return data; }
        public void setData(String data) { this.data = data; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }
}