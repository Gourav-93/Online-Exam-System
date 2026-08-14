package com.exam.online_exam_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponseDTO {
    private Long id;
    private String subject;
    private String topic;
    private String difficultyLevel;
    private Double marks;
    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    // Notice: correctAnswer is purposely omitted
}
