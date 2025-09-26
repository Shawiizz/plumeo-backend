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

    // Map user email to their WebSocket sessions (a user can have multiple sessions)
    private final Map<String, Set<WebSocketSession>> userSessions = new ConcurrentHashMap<>();
    
    // Map session ID to user email for quick lookup
    private final Map<String, String> sessionToUser = new ConcurrentHashMap<>();
    
    // Map session ID to JWT token
    private final Map<String, String> sessionToToken = new ConcurrentHashMap<>();

    /**
     * Register a new WebSocket session for a user.
     */
    public void addSession(WebSocketSession session, String userEmail, String jwtToken) {
        String sessionId = session.getId();
        
        // Add to user sessions
        userSessions.computeIfAbsent(userEmail, k -> new CopyOnWriteArraySet<>()).add(session);
        
        // Add to lookup maps
        sessionToUser.put(sessionId, userEmail);
        sessionToToken.put(sessionId, jwtToken);
        
        log.info("WebSocket session {} added for user: {}", sessionId, userEmail);
    }

    /**
     * Remove a WebSocket session.
     */
    public void removeSession(WebSocketSession session) {
        String sessionId = session.getId();
        String userEmail = sessionToUser.remove(sessionId);
        
        if (userEmail != null) {
            Set<WebSocketSession> sessions = userSessions.get(userEmail);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    userSessions.remove(userEmail);
                }
            }
        }
        
        sessionToToken.remove(sessionId);
        
        log.info("WebSocket session {} removed for user: {}", sessionId, userEmail);
    }

    /**
     * Get all sessions for a specific user.
     */
    public Set<WebSocketSession> getUserSessions(String userEmail) {
        return userSessions.getOrDefault(userEmail, Set.of());
    }

    /**
     * Get user email for a specific session.
     */
    public String getUserEmail(WebSocketSession session) {
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
    public boolean hasActiveSessions(String userEmail) {
        Set<WebSocketSession> sessions = userSessions.get(userEmail);
        return sessions != null && !sessions.isEmpty();
    }

    /**
     * Get the number of active sessions for a user.
     */
    public int getActiveSessionCount(String userEmail) {
        Set<WebSocketSession> sessions = userSessions.get(userEmail);
        return sessions != null ? sessions.size() : 0;
    }

    /**
     * Get total number of active sessions.
     */
    public int getTotalActiveSessionCount() {
        return sessionToUser.size();
    }

    /**
     * Get all active user emails.
     */
    public Set<String> getActiveUsers() {
        return Set.copyOf(userSessions.keySet());
    }
}