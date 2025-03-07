package org.AFM.rssbridge.config.socket;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.AFM.rssbridge.uitl.JwtTokenUtil;
import org.AFM.rssbridge.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthHandshakeInterceptor implements HandshakeInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthHandshakeInterceptor.class);
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        LOGGER.info("MY LORD, I am HERE!!!");
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String token = extractToken(servletRequest);

            LOGGER.info("Extracted Token: {}", token);

            if (token != null) {
                try {
                    String username = jwtTokenUtil.extractUsername(token);

                    User mockUser = new User();
                    mockUser.setIin(username);

                    if (jwtTokenUtil.validateAuthToken(token, mockUser)) {
                        attributes.put("username", username);

                        LOGGER.info("WebSocket authentication successful for user: {}", username);
                        return true;
                    } else {
                        LOGGER.warn("Invalid token for user: {}", username);
                    }
                } catch (Exception e) {
                    LOGGER.error("Token validation failed", e);
                }
            } else {
                LOGGER.warn("Missing or invalid token!");
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // No action needed
    }

    private String extractToken(ServletServerHttpRequest request) {
        String query = request.getServletRequest().getQueryString();
        LOGGER.info("Full Query String: {}", query);

        if (query != null && query.startsWith("token=")) {
            String token = query.substring(6);
            return token;
        }
        return null;
    }
}
