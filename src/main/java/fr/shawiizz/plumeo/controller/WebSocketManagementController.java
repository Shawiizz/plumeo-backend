package fr.shawiizz.plumeo.controller;

import fr.shawiizz.plumeo.websocket.WebSocketSessionManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

/**
 * Simple REST controller for WebSocket status.
 */
@RestController
@RequestMapping("/api/websocket")
@RequiredArgsConstructor
@Tag(name = "WebSocket", description = "WebSocket status endpoints")
public class WebSocketManagementController {

    private final WebSocketSessionManager sessionManager;

    @Operation(summary = "Get WebSocket connection status")
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getWebSocketStatus() {
        return ResponseEntity.ok(Map.of(
            "totalActiveSessions", sessionManager.getTotalActiveSessionCount(),
            "activeUsers", sessionManager.getActiveUsers(),
            "userCount", sessionManager.getActiveUsers().size()
        ));
    }

    @Operation(summary = "Check if a user has active WebSocket sessions")
    @GetMapping("/user/{email}/status")
    public ResponseEntity<Map<String, Object>> getUserStatus(@PathVariable String email) {
        return ResponseEntity.ok(Map.of(
            "userEmail", email,
            "sessionCount", sessionManager.getActiveSessionCount(email),
            "hasActiveSessions", sessionManager.hasActiveSessions(email)
        ));
    }

    @Operation(summary = "Get all active users")
    @GetMapping("/users")
    public ResponseEntity<Set<String>> getActiveUsers() {
        return ResponseEntity.ok(sessionManager.getActiveUsers());
    }
}