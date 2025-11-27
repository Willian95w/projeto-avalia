package com.example.projeto_avalia.controller;

import com.example.projeto_avalia.dto.QuestaoRegisterDTO;
import com.example.projeto_avalia.dto.QuestaoUpdateDTO;
import com.example.projeto_avalia.model.Questao;
import com.example.projeto_avalia.service.QuestaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/questoes")
@RequiredArgsConstructor
public class QuestaoController {

    private final QuestaoService questaoService;

    @PostMapping
    public ResponseEntity<Questao> criarQuestao(@RequestBody QuestaoRegisterDTO dto) {

        Questao novaQuestao = questaoService.criarQuestao(dto);
        return ResponseEntity.ok(novaQuestao);
    }

    @GetMapping
    public ResponseEntity<List<Questao>> listarQuestoes() {
        return ResponseEntity.ok(questaoService.listarQuestoes());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Questao> editarQuestao(@PathVariable Long id, @RequestBody QuestaoUpdateDTO dto) {
        Questao questao = questaoService.editarQuestao(id, dto);
        return ResponseEntity.ok(questao);
    }

    @PreAuthorize("hasRole('COORDENADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirQuestao(@PathVariable Long id) {
        questaoService.excluirQuestao(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Questao>> buscarComFiltros(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) List<Long> disciplinaIds,
            @RequestParam(required = false) List<Long> professorIds
    ) {
        return ResponseEntity.ok(questaoService.buscarComFiltros(title, disciplinaIds, professorIds));
    }
}
