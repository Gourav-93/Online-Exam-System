package com.exam.online_exam_system.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exam.online_exam_system.dto.ExamResponseDTO;
import com.exam.online_exam_system.entity.Exam;
import com.exam.online_exam_system.service.ExamService;

@RestController
@RequestMapping("/admin")
public class ExamController {

    @Autowired
    private ExamService examService;

    @PostMapping("/exam")
    public ExamResponseDTO createExam(@RequestBody Exam exam){
        return examService.createExam(exam);
    }

    @GetMapping("/exams")
    public List<ExamResponseDTO> getAllExams(){
        return examService.getAllExams();
    }

    @PutMapping("/exam/{id}")
    public ExamResponseDTO updateExam(@PathVariable Long id, @RequestBody Exam examDetails){
        return examService.updateExam(id, examDetails);
    }

    @DeleteMapping("/exam/{id}")
    public ResponseEntity<Map<String, String>> deleteExam(@PathVariable Long id){
        examService.deleteExam(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Exam Deleted Successfully");
        return ResponseEntity.ok(response);
    }
}