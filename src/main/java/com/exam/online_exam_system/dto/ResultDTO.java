package com.exam.online_exam_system.dto;

import lombok.Data;

@Data
public class ResultDTO {
    private Long id;
    private String studentEmail;
    private Double score;
    private String status;
    private Integer totalQuestions;
    private Integer attemptedQuestions;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private Integer blankAnswers;
    private Double percentage;
    private Boolean passed;
    private ExamResponseDTO exam;
}
