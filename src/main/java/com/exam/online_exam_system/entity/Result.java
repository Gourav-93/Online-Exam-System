package com.exam.online_exam_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Double score;

    private String status;

    private Integer totalQuestions;
    
    private Integer attemptedQuestions;
    
    private Integer correctAnswers;
    
    private Integer wrongAnswers;

    private Integer blankAnswers;
    
    private Double percentage;

    private Boolean passed;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    @JsonIgnore
    private Exam exam;
}