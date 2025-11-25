package com.example.projeto_avalia.dto;

import java.util.List;

public record ProfessorRegisterDTO(
        String name,
        String email,
        String phone,
        List<Long> disciplinaIds,
        String password,
        String confirmPassword
) {}