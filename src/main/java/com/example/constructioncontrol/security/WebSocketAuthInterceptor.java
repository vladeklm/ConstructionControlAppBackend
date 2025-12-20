package com.example.constructioncontrol.security;

import com.example.constructioncontrol.util.JwtUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    public WebSocketAuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Получаем токен из заголовков
            List<String> authHeaders = accessor.getNativeHeader("Authorization");
            
            if (authHeaders != null && !authHeaders.isEmpty()) {
                String authHeader = authHeaders.get(0);
                
                if (authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    
                    try {
                        // Валидируем токен
                        String login = jwtUtil.getLoginFromToken(token);
                        String role = jwtUtil.getRoleFromToken(token);
                        
                        if (jwtUtil.validateToken(token, login)) {
                            // Устанавливаем аутентификацию
                            UsernamePasswordAuthenticationToken authentication = 
                                new UsernamePasswordAuthenticationToken(
                                    login,
                                    null,
                                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                                );
                            
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            accessor.setUser(authentication);
                        }
                    } catch (Exception e) {
                        // Если токен невалидный, соединение будет отклонено
                        return null;
                    }
                }
            }
        }
        
        return message;
    }
}

