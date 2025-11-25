package com.example.projeto_avalia.service;

import com.example.projeto_avalia.exceptions.BadRequestException;
import com.example.projeto_avalia.exceptions.ResourceNotFoundException;
import com.example.projeto_avalia.model.Disciplina;
import com.example.projeto_avalia.repository.DisciplinaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;

    public Disciplina criarDisciplina(String name) {
        disciplinaRepository.findByName(name)
                .ifPresent(d -> { throw new BadRequestException("Disciplina já existe"); });

        Disciplina disciplina = Disciplina.builder()
                .name(name)
                .build();
        return disciplinaRepository.save(disciplina);
    }

    public List<Disciplina> listarDisciplinas() {
        return disciplinaRepository.findAllByOrderByIdDesc();
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

        disciplinaRepository.delete(disciplina);
    }

    public List<Disciplina> buscarPorNome(String name) {
        return disciplinaRepository.findByNameContainingIgnoreCaseOrderByNameAsc(name);
    }

    public Disciplina buscarPorId(Long id) {
        return disciplinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada"));
    }

}
