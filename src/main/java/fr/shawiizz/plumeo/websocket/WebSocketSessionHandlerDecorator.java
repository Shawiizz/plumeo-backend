package fr.shawiizz.plumeo.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;
import org.springframework.web.socket.WebSocketHandler;

/**
 * WebSocket handler decorator to manage session lifecycle.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketSessionHandlerDecorator implements WebSocketHandlerDecoratorFactory {

    private final WebSocketSessionManager sessionManager;

    @Override
    public WebSocketHandler decorate(WebSocketHandler handler) {
        return new WebSocketHandlerDecorator(handler) {
            
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                Long userId = (Long) session.getAttributes().get("userId");
                String jwtToken = (String) session.getAttributes().get("jwtToken");
                
                if (userId != null && jwtToken != null) {
                    sessionManager.addSession(session, userId, jwtToken);
                    log.info("WebSocket session registered for user ID: {}", userId);
                }
                
                super.afterConnectionEstablished(session);
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                sessionManager.removeSession(session);
                log.info("WebSocket session removed: {}", session.getId());
                
                super.afterConnectionClosed(session, closeStatus);
            }
        };
    }
}