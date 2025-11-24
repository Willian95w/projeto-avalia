package com.example.projeto_avalia.dto;

import com.example.projeto_avalia.model.UserRole;

public record RegisterDTO(
        String email,
        String password,
        UserRole role
) {}
