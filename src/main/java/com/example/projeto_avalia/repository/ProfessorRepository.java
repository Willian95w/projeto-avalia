package com.example.projeto_avalia.repository;

import com.example.projeto_avalia.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    List<Professor> findAllByOrderByIdDesc();
    List<Professor> findByNameContainingIgnoreCaseOrderByNameAsc(String name);
}
