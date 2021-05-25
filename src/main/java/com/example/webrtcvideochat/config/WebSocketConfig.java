package com.example.webrtcvideochat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;


/**
 * The main configuration class for WebSocket
 */
@Configuration
public class WebSocketConfig implements WebSocketConfigurer {

    /**
     * @see com.example.webrtcvideochat.socket.SocketHandler
     */
    private TextWebSocketHandler socketHandler;

    @Autowired
    public WebSocketConfig(TextWebSocketHandler socketHandler) {
        this.socketHandler = socketHandler;
    }

    /**
     * The method to register a new WebSocket handler on path(s).
     * @see com.example.webrtcvideochat.socket.SocketHandler
     * @param registry a registry to add a new handler and path
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(socketHandler, "/signal")
                .setAllowedOrigins("*");
    }

    /**
     * The method to create a new custom web socket container with the buffer size
     * @return the bean factory for creating a servlet server
     */
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxBinaryMessageBufferSize(8192);
        return container;
    }
}
