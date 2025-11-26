package com.example.projeto_avalia.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ProvaRegisterDTO(
        String titulo,
        List<Long> idsQuestoes,
        LocalDateTime dataProva,
        Long professorResponsavelId,
        String tipoAvaliacao,
        Integer duracao,
        Double peso
) {}