package com.exam.online_exam_system.dto;

import java.util.List;

import lombok.Data;

@Data
public class SubmitExamRequest {

    private Long examId;

    private String studentEmail;

    private List<AnswerRequest> answers;
}