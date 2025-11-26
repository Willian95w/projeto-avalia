package com.example.projeto_avalia.repository;

import com.example.projeto_avalia.model.Questao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestaoRepository extends JpaRepository<Questao, Long> {
    List<Questao> findAllByOrderByIdDesc();
    List<Questao> findByCreatedByIdOrderByIdDesc(Long creatorId);

    @Query("""
        SELECT q FROM Questao q
        WHERE (:title IS NULL OR :title = '' OR LOWER(q.title) LIKE LOWER(CONCAT('%', :title, '%')))
        AND (:disciplinas IS NULL OR :disciplinas IS EMPTY OR q.subject.id IN :disciplinas)
        AND (:professores IS NULL OR :professores IS EMPTY OR q.createdBy.id IN :professores)
        ORDER BY q.id DESC
    """)
    List<Questao> buscarComFiltros(
            @Param("title") String title,
            @Param("disciplinas") List<Long> disciplinaIds,
            @Param("professores") List<Long> professorIds
    );
}
