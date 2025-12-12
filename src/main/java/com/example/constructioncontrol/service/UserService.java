package com.example.constructioncontrol.service;

import com.example.constructioncontrol.dto.AuthResponse;
import com.example.constructioncontrol.dto.LoginRequest;
import com.example.constructioncontrol.dto.RegisterRequest;
import com.example.constructioncontrol.model.UserAccount;
import com.example.constructioncontrol.repository.UserRepository;
import com.example.constructioncontrol.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Проверка на существование пользователя с таким логином
        if (userRepository.existsByLogin(request.getLogin())) {
            throw new RuntimeException("User with login " + request.getLogin() + " already exists");
        }

        // Проверка на существование пользователя с таким email (если email указан)
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("User with email " + request.getEmail() + " already exists");
            }
        }

        // Создание нового пользователя
        UserAccount user = new UserAccount();
        user.setLogin(request.getLogin());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole() != null ? request.getRole() : com.example.constructioncontrol.model.UserRole.CUSTOMER);

        user = userRepository.save(user);

        // Генерация JWT токена
        String token = jwtUtil.generateToken(user.getLogin(), user.getId(), user.getRole().name());

        return new AuthResponse(
                token,
                user.getLogin(),
                user.getFullName(),
                user.getRole(),
                user.getEmail()
        );
    }

    public AuthResponse login(LoginRequest request) {
        UserAccount user = userRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new RuntimeException("Invalid login or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid login or password");
        }

        // Генерация JWT токена
        String token = jwtUtil.generateToken(user.getLogin(), user.getId(), user.getRole().name());

        return new AuthResponse(
                token,
                user.getLogin(),
                user.getFullName(),
                user.getRole(),
                user.getEmail()
        );
    }
}
