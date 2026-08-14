package com.exam.online_exam_system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exam.online_exam_system.dto.AdminQuestionDTO;
import com.exam.online_exam_system.entity.Question;
import com.exam.online_exam_system.service.QuestionService;

@RestController
@RequestMapping("/admin")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @PostMapping("/question/{examId}")
    public AdminQuestionDTO addQuestion(
            @PathVariable Long examId,
            @RequestBody Question question){

        return questionService.addQuestion(examId, question);
    }

    @GetMapping("/questions/{examId}")
    public List<AdminQuestionDTO> getQuestionsByExam(@PathVariable Long examId){

        return questionService.getAdminQuestionsByExam(examId);
    }
}