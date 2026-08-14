package com.exam.online_exam_system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exam.online_exam_system.dto.QuestionResponseDTO;
import com.exam.online_exam_system.dto.ResultDTO;
import com.exam.online_exam_system.dto.SubmitExamDTO;
import com.exam.online_exam_system.entity.ExamAttempt;
import com.exam.online_exam_system.entity.Result;
import com.exam.online_exam_system.service.AttemptService;
import com.exam.online_exam_system.service.QuestionService;
import com.exam.online_exam_system.service.ResultService;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ResultService resultService;
    
    @Autowired
    private AttemptService attemptService;

    @GetMapping("/start-exam/{examId}")
    public List<QuestionResponseDTO> getQuestions(@PathVariable Long examId){
        return questionService.getQuestionsForStudent(examId);
    }

    @Autowired
    private com.exam.online_exam_system.repository.UserRepository userRepository;

    @PostMapping("/start-exam")
    public ExamAttempt startExam(java.security.Principal principal, @RequestParam Long examId){
        com.exam.online_exam_system.entity.User user = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new com.exam.online_exam_system.exception.ResourceNotFoundException("User not found"));
        return attemptService.startAttempt(user.getId(), examId);
    }

    @PostMapping("/submit-exam/{attemptId}")
    public Result submitExam(@PathVariable Long attemptId, @RequestBody SubmitExamDTO request){
        return attemptService.submitAttempt(attemptId, request, false);
    }

    @GetMapping("/results/{email}")
    public List<Result> getStudentResults(@PathVariable String email) {
        return resultService.getResultsByUserEmail(email);
    }

    @GetMapping("/result/{id}")
    public Result getResultById(@PathVariable Long id) {
        return resultService.getResultById(id);
    }
}