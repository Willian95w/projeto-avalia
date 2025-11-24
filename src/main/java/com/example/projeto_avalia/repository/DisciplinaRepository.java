package com.example.projeto_avalia.repository;

import com.example.projeto_avalia.model.Disciplina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {
    Optional<Disciplina> findByName(String name);
    List<Disciplina> findAllByOrderByIdDesc();
    List<Disciplina> findByNameContainingIgnoreCaseOrderByNameAsc(String name);
}