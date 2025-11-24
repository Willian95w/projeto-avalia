package com.example.projeto_avalia.controller;

import com.example.projeto_avalia.model.Disciplina;
import com.example.projeto_avalia.service.DisciplinaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/disciplinas")
@RequiredArgsConstructor
public class DisciplinaController {

    private final DisciplinaService disciplinaService;

    @GetMapping("/buscar")
    public List<Disciplina> buscarPorDisciplina(@RequestParam String name) {
        return disciplinaService.buscarPorNome(name);
    }

    @PostMapping
    public ResponseEntity<Disciplina> criarDisciplina(@RequestParam String name) {
        return ResponseEntity.ok(disciplinaService.criarDisciplina(name));
    }

    @GetMapping
    public ResponseEntity<List<Disciplina>> listarDisciplinas() {
        return ResponseEntity.ok(disciplinaService.listarDisciplinas());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Disciplina> editarDisciplina(@PathVariable Long id, @RequestParam String name) {
        return ResponseEntity.ok(disciplinaService.editarDisciplina(id, name));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirDisciplina(@PathVariable Long id) {
        disciplinaService.excluirDisciplina(id);
        return ResponseEntity.noContent().build();
    }
}
