package com.example.projeto_avalia.service;

import com.example.projeto_avalia.dto.ProfessorRegisterDTO;
import com.example.projeto_avalia.dto.ProfessorUpdateDTO;
import com.example.projeto_avalia.exceptions.BadRequestException;
import com.example.projeto_avalia.exceptions.ResourceNotFoundException;
import com.example.projeto_avalia.model.Disciplina;
import com.example.projeto_avalia.model.Professor;
import com.example.projeto_avalia.model.User;
import com.example.projeto_avalia.model.UserRole;
import com.example.projeto_avalia.repository.ProfessorRepository;
import com.example.projeto_avalia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DisciplinaService disciplinaService;

    public Professor criarProfessor(ProfessorRegisterDTO dto, List<Disciplina> subjects) {

        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new BadRequestException("Usuário já existe com este e-mail.");
        }

        if (!dto.password().equals(dto.confirmPassword())) {
            throw new BadRequestException("As senhas não conferem.");
        }

        validarSenha(dto.password());

        User user = User.builder()
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .role(UserRole.PROFESSOR)
                .build();
        userRepository.save(user);

        Professor professor = Professor.builder()
                .name(dto.name())
                .phone(dto.phone())
                .user(user)
                .subjects(subjects)
                .build();

        return professorRepository.save(professor);
    }

    public List<Professor> listarProfessores() {
        return professorRepository.findAllByOrderByIdDesc();
    }

    public Professor editarProfessor(Long id, ProfessorUpdateDTO dto) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado"));

        if (dto.name() != null) professor.setName(dto.name());
        if (dto.phone() != null) professor.setPhone(dto.phone());

        if (dto.subjectIds() != null) {
            List<Disciplina> novasDisciplinas = dto.subjectIds().stream()
                    .map(disciplinaService::buscarPorId)
                    .toList();

            professor.getSubjects().clear();
            professor.getSubjects().addAll(novasDisciplinas);
        }

        if (dto.email() != null) {
            professor.getUser().setEmail(dto.email());
        }

        return professorRepository.save(professor);
    }

    public void excluirProfessor(Long id) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado"));

        professorRepository.delete(professor);
    }

    public List<Professor> buscarPorNome(String name) {
        return professorRepository.findByNameContainingIgnoreCaseOrderByNameAsc(name);
    }

    public Professor alterarSenha(Long professorId, String novaSenha, String confirmSenha) {
        if (!novaSenha.equals(confirmSenha)) {
            throw new BadRequestException("As senhas não conferem.");
        }

        validarSenha(novaSenha);

        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado"));

        professor.getUser().setPassword(passwordEncoder.encode(novaSenha));

        return professorRepository.save(professor);
    }

    public Professor buscarPorUsuarioId(Long userId) {
        return professorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado para este usuário."));
    }

    private void validarSenha(String senha) {
        if (senha == null || senha.length() < 6) {
            throw new BadRequestException("A senha deve ter no mínimo 6 caracteres.");
        }

        if (!Pattern.compile(".*[a-zA-Z].*").matcher(senha).matches()) {
            throw new BadRequestException("A senha deve conter pelo menos 1 letra.");
        }

        if (!Pattern.compile(".*[0-9].*").matcher(senha).matches()) {
            throw new BadRequestException("A senha deve conter pelo menos 1 número.");
        }
    }
}
