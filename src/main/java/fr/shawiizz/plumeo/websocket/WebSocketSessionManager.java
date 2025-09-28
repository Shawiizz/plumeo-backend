package fr.shawiizz.plumeo.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Manages WebSocket sessions and associates them with user emails and JWT tokens.
 */
@Component
@Slf4j
public class WebSocketSessionManager {

    // Map user ID to their WebSocket sessions (a user can have multiple sessions)
    private final Map<String, Set<WebSocketSession>> userSessions = new ConcurrentHashMap<>();
    
    // Map session ID to user ID for quick lookup
    private final Map<String, String> sessionToUser = new ConcurrentHashMap<>();
    
    // Map session ID to JWT token
    private final Map<String, String> sessionToToken = new ConcurrentHashMap<>();

    /**
     * Register a new WebSocket session for a user.
     */
    public void addSession(WebSocketSession session, String userId, String jwtToken) {
        String sessionId = session.getId();
        userSessions.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>()).add(session);
        
        sessionToUser.put(sessionId, userId);
        sessionToToken.put(sessionId, jwtToken);
        
        log.info("WebSocket session {} added for user ID: {}", sessionId, userId);
    }

    /**
     * Remove a WebSocket session.
     */
    public void removeSession(WebSocketSession session) {
        String sessionId = session.getId();
        String userId = sessionToUser.remove(sessionId);
        
        if (userId != null) {
            Set<WebSocketSession> sessions = userSessions.get(userId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    userSessions.remove(userId);
                }
            }
        }
        
        sessionToToken.remove(sessionId);
        
        log.info("WebSocket session {} removed for user ID: {}", sessionId, userId);
    }

    /**
     * Get all sessions for a specific user.
     */
    public Set<WebSocketSession> getUserSessions(String userId) {
        return userSessions.getOrDefault(userId, Set.of());
    }

    /**
     * Get user ID for a specific session.
     */
    public String getUserId(WebSocketSession session) {
        return sessionToUser.get(session.getId());
    }

    /**
     * Get JWT token for a specific session.
     */
    public String getJwtToken(WebSocketSession session) {
        return sessionToToken.get(session.getId());
    }

    /**
     * Check if a user has active sessions.
     */
    public boolean hasActiveSessions(String userId) {
        Set<WebSocketSession> sessions = userSessions.get(userId);
        return sessions != null && !sessions.isEmpty();
    }

    /**
     * Get the number of active sessions for a user.
     */
    public int getActiveSessionCount(String userId) {
        Set<WebSocketSession> sessions = userSessions.get(userId);
        return sessions != null ? sessions.size() : 0;
    }

    /**
     * Get total number of active sessions.
     */
    public int getTotalActiveSessionCount() {
        return sessionToUser.size();
    }

    /**
     * Get all active user IDs.
     */
    public Set<String> getActiveUsers() {
        return Set.copyOf(userSessions.keySet());
    }
}