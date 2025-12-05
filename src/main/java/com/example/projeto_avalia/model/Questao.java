package com.example.projeto_avalia.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "ativo = true")
public class Questao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String answerA;

    @Column(nullable = false)
    private String answerB;

    @Column(nullable = false)
    private String answerC;

    @Column(nullable = false)
    private String answerD;

    @Column(nullable = false)
    private String answerE;

    @Column(nullable = false)
    private String correctAnswer;

    @ManyToOne
    @JoinColumn(name = "discipline_id", nullable = false)
    private Disciplina subject;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Boolean ativo = true;

}
