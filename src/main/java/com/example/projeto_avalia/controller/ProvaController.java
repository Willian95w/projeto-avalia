package com.example.projeto_avalia.controller;

import com.example.projeto_avalia.dto.ProvaRegisterDTO;
import com.example.projeto_avalia.model.Prova;
import com.example.projeto_avalia.service.ProvaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/provas")
@RequiredArgsConstructor
public class ProvaController {

    private final ProvaService provaService;

    @PostMapping
    public ResponseEntity<Prova> criarProva(@RequestBody ProvaRegisterDTO dto) {
        Prova novaProva = provaService.criarProva(dto);
        return ResponseEntity.ok(novaProva);
    }

    @GetMapping("/{id}/gerar-pdf")
    public ResponseEntity<byte[]> gerarPdfProva(@PathVariable Long id) {
        byte[] pdf = provaService.gerarPdfProva(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "prova.pdf");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}