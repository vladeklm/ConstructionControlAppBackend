package com.example.constructioncontrol.dto;

import com.example.constructioncontrol.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String login;
    private String fullName;
    private UserRole role;
    private String email;
}

