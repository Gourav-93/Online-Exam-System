package com.exam.online_exam_system.dto;

import lombok.Data;

@Data
public class AnswerRequest {

    private Long questionId;

    private String selectedAnswer;
}