package com.example.constructioncontrol.dto;

import com.example.constructioncontrol.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Login is required")
    @Size(min = 3, max = 50, message = "Login must be between 3 and 50 characters")
    private String login;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message="Please enter your phone number")
    @Pattern(regexp = "^(\\+7|8)(?:-\\d{3}){2}(?:-\\d{2}){2}",
            message = "Invalid phone number format")
    private String phone;

    private UserRole role = UserRole.CUSTOMER; // По умолчанию клиент
}

