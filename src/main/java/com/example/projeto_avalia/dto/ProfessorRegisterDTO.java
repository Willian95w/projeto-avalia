package com.example.projeto_avalia.dto;

import java.util.List;

public record ProfessorRegisterDTO(
        String name,
        String email,
        String phone,
        List<Long> subjectIds,
        String password,
        String confirmPassword
) {}