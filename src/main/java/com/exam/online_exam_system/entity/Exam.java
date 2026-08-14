package com.exam.online_exam_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    // Time when the exam becomes available (null = always available)
    private LocalDateTime startTime;

    // Time when the exam stops being available (null = always available)
    private LocalDateTime endTime;

    // Duration in minutes
    private Integer duration;

    private Integer passingMarks;
    
    // e.g. 0.25 means 1/4th mark deducted for wrong answer
    private Double negativeMarking;

    // Total questions configured for this exam attempt
    private Integer totalQuestions;
    
    // Number of random questions to pick if pooling is used
    private Integer randomQuestionCount;

    private Boolean isActive = true;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExamAttempt> attempts;
}