package com.example.projeto_avalia.controller;

import com.example.projeto_avalia.dto.ProvaRegisterDTO;
import com.example.projeto_avalia.service.ProvaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/provas")
@RequiredArgsConstructor
public class ProvaController {

    private final ProvaService provaService;

    @PostMapping("/gerar")
    public ResponseEntity<byte[]> criarEGerarProva(@RequestBody ProvaRegisterDTO dto) {
        byte[] pdfBytes = provaService.criarEGerarProva(dto);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "prova.pdf");
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}