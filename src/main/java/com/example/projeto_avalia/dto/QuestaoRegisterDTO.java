package com.example.projeto_avalia.dto;

public record QuestaoRegisterDTO(
        String title,
        String answerA,
        String answerB,
        String answerC,
        String answerD,
        String answerE,
        String correctAnswer,
        Long subjectId
) {}