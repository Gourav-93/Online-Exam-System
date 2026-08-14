package com.exam.online_exam_system.dto;

import lombok.Data;

@Data
public class ExamDTO {
    private Long id;
    private String title;
    private Integer duration;
    private Integer totalMarks;
    private Boolean isActive;
}
