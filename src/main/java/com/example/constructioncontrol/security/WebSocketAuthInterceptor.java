package com.example.constructioncontrol.security;

import com.example.constructioncontrol.repository.UserRepository;
import com.example.constructioncontrol.util.JwtUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public WebSocketAuthInterceptor(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null) {
            StompCommand command = accessor.getCommand();
            
            // Проверяем аутентификацию при подключении
            if (StompCommand.CONNECT.equals(command)) {
                authenticateConnection(accessor);
            }
            // Проверяем аутентификацию при отправке сообщений
            else if (StompCommand.SEND.equals(command)) {
                // Сначала пытаемся восстановить из сохраненной аутентификации
                if (accessor.getUser() != null) {
                    restoreAuthenticationFromUser(accessor);
                }
                // Если не удалось восстановить, пытаемся получить токен из заголовков сообщения
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    authenticateMessage(accessor);
                }
                // Убеждаемся, что Principal установлен в accessor для передачи в контроллер
                if (accessor.getUser() == null && SecurityContextHolder.getContext().getAuthentication() != null) {
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    accessor.setUser(auth);
                }
                // Если все еще не аутентифицирован, логируем для отладки
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    System.err.println("Warning: WebSocket message sent without authentication. User: " + accessor.getUser());
                }
            }
            // Для других команд также восстанавливаем SecurityContext
            else if (accessor.getUser() != null) {
                restoreAuthenticationFromUser(accessor);
            }
        }
        
        return message;
    }
    
    private void restoreAuthenticationFromUser(StompHeaderAccessor accessor) {
        Object user = accessor.getUser();
        
        // Если это уже UsernamePasswordAuthenticationToken, используем его напрямую
        if (user instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) user;
            SecurityContext context = new SecurityContextImpl();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);
            return;
        }
        
        // Если это Principal, создаем аутентификацию на его основе
        if (user instanceof java.security.Principal) {
            java.security.Principal principal = (java.security.Principal) user;
            String login = principal.getName();
            
            // Получаем пользователя из базы данных и создаем аутентификацию
            try {
                userRepository.findByLogin(login).ifPresent(userAccount -> {
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                            login,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userAccount.getRole().name()))
                        );
                    SecurityContext context = new SecurityContextImpl();
                    context.setAuthentication(authentication);
                    SecurityContextHolder.setContext(context);
                });
            } catch (Exception e) {
                // Игнорируем ошибки, попробуем аутентифицировать через токен
                System.err.println("Failed to restore authentication from user: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private void authenticateConnection(StompHeaderAccessor accessor) {
        List<String> authHeaders = accessor.getNativeHeader("Authorization");
        
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String authHeader = authHeaders.get(0);
            
            if (authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                authenticateToken(token, accessor);
            }
        }
    }
    
    private void authenticateMessage(StompHeaderAccessor accessor) {
        List<String> authHeaders = accessor.getNativeHeader("Authorization");
        
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String authHeader = authHeaders.get(0);
            
            if (authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                authenticateToken(token, accessor);
            }
        }
    }
    
    private void authenticateToken(String token, StompHeaderAccessor accessor) {
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
                
                // Создаем новый SecurityContext и устанавливаем его
                SecurityContext context = new SecurityContextImpl();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
                
                // Также сохраняем в accessor для использования в других местах
                accessor.setUser(authentication);
            }
        } catch (Exception e) {
            // Если токен невалидный, соединение/сообщение будет отклонено
            // Логируем ошибку для отладки
            System.err.println("WebSocket authentication failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

