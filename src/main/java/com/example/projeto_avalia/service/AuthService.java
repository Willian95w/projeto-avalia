package com.example.projeto_avalia.service;

import com.example.projeto_avalia.dto.AuthResponseDTO;
import com.example.projeto_avalia.dto.LoginDTO;
import com.example.projeto_avalia.dto.RegisterDTO;
import com.example.projeto_avalia.model.User;
import com.example.projeto_avalia.repository.UserRepository;
import com.example.projeto_avalia.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthResponseDTO register(RegisterDTO dto) {
        User user = User.builder()
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .role(dto.role())
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponseDTO(token);
    }

    public AuthResponseDTO login(LoginDTO dto) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
        );
        User user = (User) auth.getPrincipal();
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponseDTO(token);
    }
}