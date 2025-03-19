package org.AFM.rssbridge.config.socket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Slf4j
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final MyRawWSHandler myRawWSHandler;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        log.info("✅ Registering WebSocket handler...");
        registry.addHandler(myRawWSHandler, "/ws")
                .setAllowedOrigins("*");
    }
}