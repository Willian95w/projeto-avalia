package com.example.projeto_avalia.controller;

import com.example.projeto_avalia.dto.AlterarSenhaDTO;
import com.example.projeto_avalia.dto.ProfessorRegisterDTO;
import com.example.projeto_avalia.dto.ProfessorUpdateDTO;
import com.example.projeto_avalia.model.Professor;
import com.example.projeto_avalia.service.DisciplinaService;
import com.example.projeto_avalia.service.ProfessorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/professores")
@RequiredArgsConstructor
public class ProfessorController {

    private final ProfessorService professorService;
    private final DisciplinaService disciplinaService;

    @PostMapping
    public ResponseEntity<Professor> criarProfessor(@RequestBody ProfessorRegisterDTO dto) {
        List subjects = dto.subjectIds().stream()
                .map(disciplinaService::buscarPorId)
                .toList();

        Professor professor = professorService.criarProfessor(dto, subjects);
        return ResponseEntity.ok(professor);
    }

    @GetMapping
    public ResponseEntity<List<Professor>> listarProfessores() {
        List<Professor> professores = professorService.listarProfessores();
        return ResponseEntity.ok(professores);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Professor> editarProfessor(@PathVariable Long id, @RequestBody ProfessorUpdateDTO dto) {
        Professor professor = professorService.editarProfessor(id, dto);
        return ResponseEntity.ok(professor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirProfessor(@PathVariable Long id) {
        professorService.excluirProfessor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Professor>> buscarPorNome(@RequestParam String name) {
        List<Professor> professores = professorService.buscarPorNome(name);
        return ResponseEntity.ok(professores);
    }

    @PutMapping("/{id}/senha")
    public ResponseEntity<Professor> alterarSenha(@PathVariable Long id, @RequestBody AlterarSenhaDTO dto) {
        return ResponseEntity.ok(professorService.alterarSenha(id, dto.novaSenha(), dto.confirmSenha()));
    }
}