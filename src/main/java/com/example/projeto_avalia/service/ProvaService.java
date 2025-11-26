package com.example.projeto_avalia.service;

import com.example.projeto_avalia.dto.ProvaRegisterDTO;
import com.example.projeto_avalia.exceptions.BadRequestException;
import com.example.projeto_avalia.exceptions.ResourceNotFoundException;
import com.example.projeto_avalia.model.Disciplina;
import com.example.projeto_avalia.model.Prova;
import com.example.projeto_avalia.model.Questao;
import com.example.projeto_avalia.model.User;
import com.example.projeto_avalia.model.UserRole;
import com.example.projeto_avalia.repository.ProvaRepository;
import com.example.projeto_avalia.repository.QuestaoRepository;
import com.example.projeto_avalia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProvaService {

    private final ProvaRepository provaRepository;
    private final QuestaoRepository questaoRepository;
    private final UserRepository userRepository;
    private final DisciplinaService disciplinaService;

    private User getUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado."));
    }

    public Prova criarProva(ProvaRegisterDTO dto) {
        User criador = getUsuarioAutenticado();

        if (dto.titulo() == null || dto.titulo().trim().isEmpty()) {
            throw new BadRequestException("O título da prova é obrigatório.");
        }

        if (dto.disciplinaId() == null) {
            throw new BadRequestException("A disciplina é obrigatória.");
        }

        if (dto.dataProva() == null || dto.dataProva().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("A data da prova deve ser no futuro.");
        }

        if (dto.professorResponsavelId() == null) {
            throw new BadRequestException("O professor responsável é obrigatório.");
        }

        User professorResponsavel = userRepository.findById(dto.professorResponsavelId())
                .orElseThrow(() -> new ResourceNotFoundException("Professor responsável não encontrado."));

        // Validação: Professores só podem definir a si mesmos; coordenadores podem qualquer um
        if (criador.getRole() != UserRole.COORDENADOR &&
                !professorResponsavel.getId().equals(criador.getId())) {
            throw new BadRequestException("Você só pode definir a si mesmo como professor responsável.");
        }

        if (dto.tipoAvaliacao() == null || dto.tipoAvaliacao().trim().isEmpty()) {
            throw new BadRequestException("O tipo de avaliação é obrigatório.");
        }

        if (dto.duracao() == null || dto.duracao() <= 0) {
            throw new BadRequestException("A duração deve ser um número positivo em minutos.");
        }

        if (dto.peso() == null || dto.peso() <= 0) {
            throw new BadRequestException("O peso deve ser um número positivo.");
        }

        Disciplina disciplina = disciplinaService.buscarPorId(dto.disciplinaId());

        List<Questao> questoes = questaoRepository.findAllById(dto.idsQuestoes());
        if (questoes.size() != dto.idsQuestoes().size()) {
            throw new BadRequestException("Uma ou mais questões não foram encontradas.");
        }

        // Filtra questões por permissão (professor só pode usar suas próprias)
        if (criador.getRole() != UserRole.COORDENADOR) {
            questoes = questoes.stream()
                    .filter(q -> q.getCreatedBy().getId().equals(criador.getId()))
                    .collect(Collectors.toList());
        }

        if (questoes.isEmpty()) {
            throw new BadRequestException("Nenhuma questão válida encontrada para criar a prova.");
        }

        Prova prova = Prova.builder()
                .titulo(dto.titulo().trim())
                .questoes(questoes)
                .disciplina(disciplina)
                .dataProva(dto.dataProva())
                .professorResponsavel(professorResponsavel)
                .tipoAvaliacao(dto.tipoAvaliacao().trim())
                .duracao(dto.duracao())
                .peso(dto.peso())
                .createdBy(criador)
                .build();

        return provaRepository.save(prova);
    }

    public byte[] gerarPdfProva(Long id) {
        Prova prova = provaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prova não encontrada."));

        User usuario = getUsuarioAutenticado();

        // Verifica permissão: usuário pode gerar PDF de provas que criou ou se for coordenador
        if (usuario.getRole() != UserRole.COORDENADOR &&
                !prova.getCreatedBy().getId().equals(usuario.getId())) {
            throw new BadRequestException("Você não tem permissão para gerar PDF desta prova.");
        }

        // Gera o PDF
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            document.add(new Paragraph("Prova: " + prova.getTitulo()));
            document.add(new Paragraph("Disciplina: " + prova.getDisciplina().getName()));
            document.add(new Paragraph("Data: " + prova.getDataProva().toString()));
            document.add(new Paragraph("Professor Responsável: " + prova.getProfessorResponsavel().getRole().name()));
            document.add(new Paragraph("Tipo de Avaliação: " + prova.getTipoAvaliacao()));
            document.add(new Paragraph("Duração: " + prova.getDuracao() + " minutos"));
            document.add(new Paragraph("Peso: " + prova.getPeso()));
            document.add(new Paragraph(" "));

            int numeroQuestao = 1;
            for (Questao q : prova.getQuestoes()) {
                document.add(new Paragraph(numeroQuestao + ". " + q.getTitle()));
                document.add(new Paragraph("   A) " + q.getAnswerA()));
                document.add(new Paragraph("   B) " + q.getAnswerB()));
                document.add(new Paragraph("   C) " + q.getAnswerC()));
                document.add(new Paragraph("   D) " + q.getAnswerD()));
                document.add(new Paragraph("   E) " + q.getAnswerE()));
                document.add(new Paragraph(" "));
                numeroQuestao++;
            }

            document.close();
        } catch (Exception e) {
            throw new BadRequestException("Erro ao gerar o PDF: " + e.getMessage());
        }

        return baos.toByteArray();
    }
}