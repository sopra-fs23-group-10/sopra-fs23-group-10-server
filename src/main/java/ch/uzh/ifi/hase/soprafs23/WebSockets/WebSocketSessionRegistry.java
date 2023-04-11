package ch.uzh.ifi.hase.soprafs23.WebSockets;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;

@Component
public class WebSocketSessionRegistry {
    private final Map<String, StandardWebSocketSession> sessions = new ConcurrentHashMap<>();

    public void registerSession(String userId, StandardWebSocketSession session) {
        sessions.put(userId, session);
    }

    public void removeSession(String userId) {
        sessions.remove(userId);
    }

    public StandardWebSocketSession getSession(String userId) {
        return sessions.get(userId);
    }
}
