package com.example.projeto_avalia.service;

import com.example.projeto_avalia.dto.QuestaoRegisterDTO;
import com.example.projeto_avalia.dto.QuestaoUpdateDTO;
import com.example.projeto_avalia.exceptions.BadRequestException;
import com.example.projeto_avalia.exceptions.ResourceNotFoundException;
import com.example.projeto_avalia.model.Disciplina;
import com.example.projeto_avalia.model.Questao;
import com.example.projeto_avalia.model.User;
import com.example.projeto_avalia.model.UserRole;
import com.example.projeto_avalia.repository.QuestaoRepository;
import com.example.projeto_avalia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestaoService {

    private final QuestaoRepository questaoRepository;
    private final DisciplinaService disciplinaService;
    private final UserRepository userRepository;

    private User getUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado."));
    }

    public Questao criarQuestao(QuestaoRegisterDTO dto) {

        User criador = getUsuarioAutenticado();
        Disciplina disciplina = disciplinaService.buscarPorId(dto.subjectId());

        if (!List.of("A","B","C","D","E").contains(dto.correctAnswer().toUpperCase())) {
            throw new BadRequestException("A resposta correta deve ser A, B, C, D ou E.");
        }

        Questao questao = Questao.builder()
                .title(dto.title().trim())
                .answerA(dto.answerA())
                .answerB(dto.answerB())
                .answerC(dto.answerC())
                .answerD(dto.answerD())
                .answerE(dto.answerE())
                .correctAnswer(dto.correctAnswer().toUpperCase())
                .subject(disciplina)
                .createdBy(criador)
                .build();

        return questaoRepository.save(questao);
    }

    public List<Questao> listarQuestoes() {
        User usuario = getUsuarioAutenticado();

        if (usuario.getRole() == UserRole.COORDENADOR) {
            return questaoRepository.findAllByOrderByIdDesc();
        }

        return questaoRepository.findByCreatedByIdOrderByIdDesc(usuario.getId());
    }

    public Questao editarQuestao(Long id, QuestaoUpdateDTO dto) {
        User usuario = getUsuarioAutenticado();

        Questao questao = questaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Questão não encontrada."));

        if (usuario.getRole() != UserRole.COORDENADOR &&
                !questao.getCreatedBy().getId().equals(usuario.getId())) {
            throw new BadRequestException("Você não tem permissão para editar esta questão.");
        }

        if (dto.title() != null && !dto.title().isBlank()) questao.setTitle(dto.title());
        if (dto.answerA() != null) questao.setAnswerA(dto.answerA());
        if (dto.answerB() != null) questao.setAnswerB(dto.answerB());
        if (dto.answerC() != null) questao.setAnswerC(dto.answerC());
        if (dto.answerD() != null) questao.setAnswerD(dto.answerD());
        if (dto.answerE() != null) questao.setAnswerE(dto.answerE());

        if (dto.correctAnswer() != null) {
            if (!List.of("A","B","C","D","E")
                    .contains(dto.correctAnswer().toUpperCase())) {
                throw new BadRequestException("A resposta correta deve ser A, B, C, D ou E.");
            }
            questao.setCorrectAnswer(dto.correctAnswer().toUpperCase());
        }

        if (dto.subjectId() != null) {
            Disciplina novaDisciplina = disciplinaService.buscarPorId(dto.subjectId());
            questao.setSubject(novaDisciplina);
        }

        return questaoRepository.save(questao);
    }

    public void excluirQuestao(Long id) {
        Questao questao = questaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Questão não encontrada."));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Usuário autenticado não encontrado."));

        if (usuario.getRole() != UserRole.COORDENADOR) {
            throw new BadRequestException("Apenas coordenadores podem excluir questões.");
        }

        questaoRepository.delete(questao);
    }

    public List<Questao> buscarPorTitulo(String title) {
        User usuario = getUsuarioAutenticado();

        List<Questao> resultados = questaoRepository
                .findByTitleContainingIgnoreCaseOrderByTitleAsc(title);

        if (usuario.getRole() == UserRole.COORDENADOR) return resultados;

        return resultados.stream()
                .filter(q -> q.getCreatedBy().getId().equals(usuario.getId()))
                .toList();
    }

    public List<Questao> filtrar(Long disciplinaId, Long professorId) {
        User usuario = getUsuarioAutenticado();

        if (usuario.getRole() != UserRole.COORDENADOR) {
            professorId = usuario.getId(); // professor só pode ver dele
        }

        if (disciplinaId != null && professorId != null) {
            return questaoRepository.findBySubjectIdAndCreatedByIdOrderByIdDesc(disciplinaId, professorId);
        }

        if (disciplinaId != null) {
            return questaoRepository.findBySubjectIdOrderByIdDesc(disciplinaId);
        }

        return questaoRepository.findByCreatedByIdOrderByIdDesc(professorId);
    }
}
