package org.AFM.rssbridge.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.AFM.rssbridge.news.model.News;
import org.AFM.rssbridge.user.model.SourceRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
public class MyRawWSHandler extends TextWebSocketHandler {

    private static final CopyOnWriteArraySet<WebSocketSession> newSessions = new CopyOnWriteArraySet<>();
    private static final CopyOnWriteArraySet<WebSocketSession> userSessions = new CopyOnWriteArraySet<>();
    private static final CopyOnWriteArraySet<WebSocketSession> adminSessions = new CopyOnWriteArraySet<>();

    private static final Map<WebSocketSession, String> sessionRoles = new ConcurrentHashMap<>();
    private static final Map<Long, WebSocketSession> userRequestSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String role = getRoleFromSession(session);
        sessionRoles.put(session, role);

        if ("admin".equals(role)) {
            adminSessions.add(session);
            log.info("Admin WebSocket connected: {}", session.getId());
        } else {
            newSessions.add(session);
            userSessions.add(session);
            Long requestId = extractRequestIdFromSession(session);
            if (requestId != null) {
                trackUserSession(requestId, session);
            }
            log.info("User WebSocket connected: {}", session.getId());
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        log.info("Received message from {}: {}", session.getId(), message.getPayload());
        session.sendMessage(new TextMessage("Hello from server!"));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String role = sessionRoles.remove(session);
        if ("admin".equals(role)) {
            adminSessions.remove(session);
            log.info("Admin WebSocket disconnected: {}", session.getId());
        } else {
            newSessions.remove(session);
            userSessions.remove(session);
            log.info("User WebSocket disconnected: {}", session.getId());
        }
    }

    public void broadcastNewsMessage(News news) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(news);
            for (WebSocketSession session : newSessions) {
                session.sendMessage(new TextMessage(jsonMessage));
            }
        }
        catch(IOException e){
                log.error("Error sending WebSocket message", e);
            }
        }

        public void broadcastToUser (SourceRequest sourceRequest, String message){
            WebSocketSession userSession = userRequestSessions.get(sourceRequest.getId());
            if (userSession != null && userSession.isOpen()) {
                try {
                    userSession.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    log.error("Error sending WebSocket message to user", e);
                }
            }
        }
        public void broadcastToAdmins (SourceRequest sourceRequest){
            try {
                String jsonMessage = objectMapper.writeValueAsString(sourceRequest);
                for (WebSocketSession session : adminSessions) {
                    session.sendMessage(new TextMessage(jsonMessage));
                }
            } catch (IOException e) {
                log.error("Error sending WebSocket message", e);
            }
        }
        public void trackUserSession (Long requestId, WebSocketSession session){
            userRequestSessions.put(requestId, session);
        }
        private String getRoleFromSession (WebSocketSession session){
            String uri = session.getUri().toString();
            return uri.contains("role=admin") ? "admin" : "user";
        }
        private Long extractRequestIdFromSession (WebSocketSession session){
            String query = session.getUri().getQuery();
            if (query != null && query.contains("requestId=")) {
                return Long.parseLong(query.split("requestId=")[1].split("&")[0]);
            }
            return null;
        }
    }
