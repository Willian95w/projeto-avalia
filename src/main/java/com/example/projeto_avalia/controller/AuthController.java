package com.example.projeto_avalia.controller;

import com.example.projeto_avalia.dto.AuthResponseDTO;
import com.example.projeto_avalia.dto.LoginDTO;
import com.example.projeto_avalia.dto.RegisterDTO;
import com.example.projeto_avalia.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterDTO dto) {
        return ResponseEntity.ok(authService.register(dto));
    }

    @PostMapping("/login-coordenador")
    public ResponseEntity<AuthResponseDTO> loginCoordenador(@RequestBody LoginDTO dto) {
        return ResponseEntity.ok(authService.loginCoordenador(dto));
    }

    @PostMapping("/login-professor")
    public ResponseEntity<AuthResponseDTO> loginProfessor(@RequestBody LoginDTO dto) {
        return ResponseEntity.ok(authService.loginProfessor(dto));
    }
}