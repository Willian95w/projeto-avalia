package com.example.projeto_avalia.repository;

import com.example.projeto_avalia.model.Questao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestaoRepository extends JpaRepository<Questao, Long> {
    List<Questao> findAllByOrderByIdDesc();
    List<Questao> findByTitleContainingIgnoreCaseOrderByTitleAsc(String title);
    List<Questao> findBySubjectIdOrderByIdDesc(Long disciplinaId);
    List<Questao> findByCreatedByIdOrderByIdDesc(Long creatorId);
    List<Questao> findBySubjectIdAndCreatedByIdOrderByIdDesc(Long subjectId, Long createdById);
}
