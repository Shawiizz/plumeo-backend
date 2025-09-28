package fr.shawiizz.plumeo.websocket;

import fr.shawiizz.plumeo.entity.User;
import fr.shawiizz.plumeo.service.UserService;
import fr.shawiizz.plumeo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket handshake interceptor that validates JWT tokens and extracts user information.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtWebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        
        String token = extractTokenFromRequest(request);
        
        if (token == null) {
            log.warn("WebSocket handshake failed: No JWT token provided");
            return false;
        }

        try {
            Long userId = jwtUtil.extractUserId(token);
            
            if (userId == null) {
                log.warn("WebSocket handshake failed: Invalid JWT token format (no user ID)");
                return false;
            }
            
            // Validate token with user ID
            if (!jwtUtil.validateTokenWithUserId(token, userId)) {
                log.warn("WebSocket handshake failed: Invalid JWT token for user ID: {}", userId);
                return false;
            }

            User user = userService.findById(userId).orElse(null);
            
            // Store user information in WebSocket session attributes
            attributes.put("userId", userId);
            attributes.put("jwtToken", token);
            if (user != null) {
                attributes.put("userEmail", user.getEmail());
            }
            
            log.info("WebSocket handshake successful for user ID: {}", userId);
            return true;
            
        } catch (Exception e) {
            log.error("WebSocket handshake failed due to token processing error: ", e);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        if (exception != null) {
            log.error("WebSocket handshake completed with error: ", exception);
        } else {
            log.debug("WebSocket handshake completed successfully");
        }
    }

    /**
     * Extract JWT token from WebSocket handshake request.
     * Looks for token in query parameters or headers.
     */
    private String extractTokenFromRequest(ServerHttpRequest request) {
        // First, try to get token from query parameters (common for WebSocket)
        String query = request.getURI().getQuery();
        if (query != null && query.contains("token=")) {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("token=")) {
                    return param.substring(6); // Remove "token=" prefix
                }
            }
        }
        
        // Fallback to Authorization header
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        
        return null;
    }
}