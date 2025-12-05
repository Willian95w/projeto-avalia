package com.example.projeto_avalia.service;

import com.example.projeto_avalia.exceptions.BadRequestException;
import com.example.projeto_avalia.exceptions.ResourceNotFoundException;
import com.example.projeto_avalia.model.Disciplina;
import com.example.projeto_avalia.model.Professor;
import com.example.projeto_avalia.model.User;
import com.example.projeto_avalia.model.UserRole;
import com.example.projeto_avalia.repository.DisciplinaRepository;
import com.example.projeto_avalia.repository.ProfessorRepository;
import com.example.projeto_avalia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;
    private final UserRepository userRepository;
    private final ProfessorRepository professorRepository;

    private User getUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado."));
    }

    public Disciplina criarDisciplina(String name) {
        disciplinaRepository.findByName(name)
                .ifPresent(d -> { throw new BadRequestException("Disciplina já existe"); });

        Disciplina disciplina = Disciplina.builder()
                .name(name)
                .ativo(true)
                .build();
        return disciplinaRepository.save(disciplina);
    }

    public List<Disciplina> listarDisciplinas() {
        User usuario = getUsuarioAutenticado();

        if (usuario.getRole() == UserRole.COORDENADOR) {
            return disciplinaRepository.findAllByOrderByIdDesc();
        }

        return professorRepository.findByUserId(usuario.getId())
                .map(Professor::getSubjects)
                .orElse(List.of());
    }

    public Disciplina editarDisciplina(Long id, String newName) {
        Disciplina disciplina = disciplinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada"));

        disciplina.setName(newName);
        return disciplinaRepository.save(disciplina);
    }

    public void excluirDisciplina(Long id) {
        Disciplina disciplina = disciplinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada"));

        disciplina.setAtivo(false);
        disciplinaRepository.save(disciplina);
    }

    public List<Disciplina> buscarPorNome(String name) {
        return disciplinaRepository.findByNameContainingIgnoreCaseOrderByNameAsc(name);
    }

    public Disciplina buscarPorId(Long id) {
        return disciplinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada"));
    }

}
