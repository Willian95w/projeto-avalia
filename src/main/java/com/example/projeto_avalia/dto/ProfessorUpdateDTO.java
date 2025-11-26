package com.example.projeto_avalia.dto;

import java.util.List;

public record ProfessorUpdateDTO(
        String name,
        String phone,
        String email,
        List<Long> subjectIds
) {}