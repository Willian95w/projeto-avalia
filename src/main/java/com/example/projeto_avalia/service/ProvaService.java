package com.example.projeto_avalia.service;

import com.example.projeto_avalia.dto.ProvaRegisterDTO;
import com.example.projeto_avalia.exceptions.BadRequestException;
import com.example.projeto_avalia.exceptions.ResourceNotFoundException;
import com.example.projeto_avalia.model.Prova;
import com.example.projeto_avalia.model.Questao;
import com.example.projeto_avalia.model.User;
import com.example.projeto_avalia.model.UserRole;
import com.example.projeto_avalia.repository.ProfessorRepository;
import com.example.projeto_avalia.repository.ProvaRepository;
import com.example.projeto_avalia.repository.QuestaoRepository;
import com.example.projeto_avalia.repository.UserRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProvaService {

    private final ProvaRepository provaRepository;
    private final QuestaoRepository questaoRepository;
    private final UserRepository userRepository;
    private final ProfessorRepository professorRepository;

    private User getUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado."));
    }

    public byte[] criarEGerarProva(ProvaRegisterDTO dto) {
        User criador = getUsuarioAutenticado();

        User professorResponsavel = userRepository.findById(dto.professorResponsavelId())
                .orElseThrow(() -> new ResourceNotFoundException("Professor responsável não encontrado."));

        if (criador.getRole() != UserRole.COORDENADOR &&
                !professorResponsavel.getId().equals(criador.getId())) {
            throw new BadRequestException("Você só pode definir a si mesmo como professor responsável.");
        }

        List<Questao> questoes = questaoRepository.findAllById(dto.idsQuestoes());
        if (questoes.size() != dto.idsQuestoes().size()) {
            throw new BadRequestException("Uma ou mais questões não foram encontradas.");
        }

        if (criador.getRole() != UserRole.COORDENADOR) {
            boolean todasQuestoesSaoDoUsuario = questoes.stream()
                    .allMatch(q -> q.getCreatedBy().getId().equals(criador.getId()));

            if (!todasQuestoesSaoDoUsuario) {
                throw new BadRequestException("Você só pode criar provas com suas próprias questões.");
            }
        }

        Prova prova = Prova.builder()
                .titulo(dto.titulo().trim())
                .questoes(questoes)
                .dataProva(dto.dataProva())
                .professorResponsavel(professorResponsavel)
                .tipoAvaliacao(dto.tipoAvaliacao().trim())
                .duracao(dto.duracao())
                .peso(dto.peso())
                .createdBy(criador)
                .build();

        provaRepository.save(prova);

        return gerarPdf(prova);
    }

    private byte[] gerarPdf(Prova prova) {
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            Image logo = null;
            try {
                logo = Image.getInstance(new URL("https://bkpsitecpsnew.blob.core.windows.net/uploadsitecps/sites/1/2012/05/Logo-Fatec-1200x800-1.jpg"));
                logo.scaleToFit(80, 80);
            } catch (Exception e) {
            }

            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1f, 3f});
            headerTable.getDefaultCell().setBorder(Rectangle.BOX);
            headerTable.getDefaultCell().setPadding(10);
            headerTable.getDefaultCell().setBorderWidth(2);

            PdfPCell logoCell = new PdfPCell();
            logoCell.setBorder(Rectangle.BOX);
            logoCell.setBorderWidth(2);
            logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            logoCell.setPaddingTop(15);
            logoCell.setPaddingBottom(5);
            logoCell.setPaddingLeft(25);
            logoCell.setPaddingRight(10);

            if (logo != null) {
                logoCell.addElement(logo);
            } else {
                logoCell.addElement(new Paragraph("LOGO"));
            }

            headerTable.addCell(logoCell);

            PdfPCell titleCell = new PdfPCell();
            titleCell.setBorder(Rectangle.BOX);
            titleCell.setBorderWidth(2);
            titleCell.setPaddingTop(15);
            titleCell.setPaddingBottom(5);
            titleCell.setPaddingLeft(10);
            titleCell.setPaddingRight(10);

            Paragraph titulo = new Paragraph(prova.getTitulo(),
                    new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD));

            titulo.setAlignment(Element.ALIGN_CENTER);

            titleCell.addElement(titulo);
            headerTable.addCell(titleCell);

            document.add(headerTable);

            document.add(new Paragraph(" ")); // Espaço

            PdfPTable alunoTable = new PdfPTable(1);
            alunoTable.setWidthPercentage(100);
            alunoTable.getDefaultCell().setBorder(Rectangle.BOX);
            alunoTable.getDefaultCell().setBorderWidth(1.5f);
            alunoTable.getDefaultCell().setPadding(10);

            alunoTable.addCell("Nome do Aluno: _____________________________________________________________");

            document.add(alunoTable);
            document.add(new Paragraph(" "));

            PdfPTable infoTable = new PdfPTable(3);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{2f, 1f, 1f});
            infoTable.getDefaultCell().setBorder(Rectangle.BOX);
            infoTable.getDefaultCell().setBorderWidth(1.5f);
            infoTable.getDefaultCell().setPadding(8);

            String nomeProfessor = professorRepository.findByUserId(prova.getProfessorResponsavel().getId())
                    .map(p -> p.getName())
                    .orElse("Não informado");

            infoTable.addCell("Professor: " + nomeProfessor);
            infoTable.addCell("Data: " + prova.getDataProva().toLocalDate());
            infoTable.addCell("Peso: " + prova.getPeso());

            infoTable.addCell("Tipo de Avaliação: " + prova.getTipoAvaliacao());
            infoTable.addCell("Duração: " + prova.getDuracao() + " min");
            infoTable.addCell("Nota: ");

            document.add(infoTable);

            document.add(new Paragraph(" "));

            int numeroQuestao = 1;
            for (Questao q : prova.getQuestoes()) {
                document.add(new Paragraph(numeroQuestao + ". " + q.getTitle()));
                document.add(new Paragraph("   A) " + q.getAnswerA()));
                document.add(new Paragraph("   B) " + q.getAnswerB()));
                document.add(new Paragraph("   C) " + q.getAnswerC()));
                document.add(new Paragraph("   D) " + q.getAnswerD()));
                document.add(new Paragraph("   E) " + q.getAnswerE()));
                document.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------------"));
                numeroQuestao++;
            }

            document.close();
        } catch (Exception e) {
            throw new BadRequestException("Erro ao gerar o PDF: " + e.getMessage());
        }

        return baos.toByteArray();
    }
}