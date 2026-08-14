package com.exam.online_exam_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubmitExamDTO {
    private Long attemptId;
    
    // Maps Question ID to selected option (e.g. "A", "B", "C", "D")
    private Map<Long, String> answers;
}
