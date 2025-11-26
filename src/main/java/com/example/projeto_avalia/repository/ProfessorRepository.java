package com.example.projeto_avalia.repository;

import com.example.projeto_avalia.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    Optional<Professor> findByUserId(Long userId);
    List<Professor> findAllByOrderByIdDesc();
    List<Professor> findByNameContainingIgnoreCaseOrderByNameAsc(String name);
}
