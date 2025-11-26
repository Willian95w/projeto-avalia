package com.example.projeto_avalia.repository;

import com.example.projeto_avalia.model.Prova;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProvaRepository extends JpaRepository<Prova, Long> {
    List<Prova> findAllByOrderByIdDesc();

    List<Prova> findByCreatedByIdOrderByIdDesc(Long creatorId);
}