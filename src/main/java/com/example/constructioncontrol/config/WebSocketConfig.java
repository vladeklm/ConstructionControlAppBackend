package com.example.constructioncontrol.config;

import com.example.constructioncontrol.security.WebSocketAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Конфигурация WebSocket для чата.
 * Использует STOMP протокол поверх WebSocket.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    public WebSocketConfig(WebSocketAuthInterceptor webSocketAuthInterceptor) {
        this.webSocketAuthInterceptor = webSocketAuthInterceptor;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Включаем простой брокер сообщений для отправки сообщений клиентам
        config.enableSimpleBroker("/topic", "/queue");
        // Префикс для сообщений, отправляемых клиентом на сервер
        config.setApplicationDestinationPrefixes("/app");
        // Префикс для сообщений пользователю
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Регистрируем endpoint для WebSocket соединения
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:3000", "http://frontend:3000")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Добавляем interceptor для аутентификации WebSocket соединений
        registration.interceptors(webSocketAuthInterceptor);
    }
}

