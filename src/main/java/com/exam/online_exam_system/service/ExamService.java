package com.exam.online_exam_system.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exam.online_exam_system.dto.ExamResponseDTO;
import com.exam.online_exam_system.entity.Exam;
import com.exam.online_exam_system.exception.ResourceNotFoundException;
import com.exam.online_exam_system.repository.ExamRepository;

@Service
public class ExamService {

    @Autowired
    private ExamRepository examRepository;

    public ExamResponseDTO createExam(Exam exam) {
        if (exam.getIsActive() == null) {
            exam.setIsActive(true);
        }
        Exam savedExam = examRepository.save(exam);
        return mapToDTO(savedExam);
    }

    public List<ExamResponseDTO> getAllExams() {
        return examRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ExamResponseDTO getExamById(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam Not Found with id: " + id));
        return mapToDTO(exam);
    }

    public ExamResponseDTO updateExam(Long id, Exam examDetails) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam Not Found with id: " + id));
        
        exam.setTitle(examDetails.getTitle());
        exam.setDescription(examDetails.getDescription());
        exam.setDuration(examDetails.getDuration());
        exam.setStartTime(examDetails.getStartTime());
        exam.setEndTime(examDetails.getEndTime());
        exam.setPassingMarks(examDetails.getPassingMarks());
        exam.setNegativeMarking(examDetails.getNegativeMarking());
        exam.setTotalQuestions(examDetails.getTotalQuestions());
        exam.setRandomQuestionCount(examDetails.getRandomQuestionCount());

        if (examDetails.getIsActive() != null) {
            exam.setIsActive(examDetails.getIsActive());
        }
        Exam updatedExam = examRepository.save(exam);
        return mapToDTO(updatedExam);
    }

    public void deleteExam(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam Not Found with id: " + id));
        examRepository.delete(exam);
    }

    public ExamResponseDTO mapToDTO(Exam exam) {
        ExamResponseDTO dto = new ExamResponseDTO();
        dto.setId(exam.getId());
        dto.setTitle(exam.getTitle());
        dto.setDescription(exam.getDescription());
        dto.setDuration(exam.getDuration());
        dto.setStartTime(exam.getStartTime());
        dto.setEndTime(exam.getEndTime());
        dto.setPassingMarks(exam.getPassingMarks());
        dto.setNegativeMarking(exam.getNegativeMarking());
        dto.setTotalQuestions(exam.getTotalQuestions());
        dto.setIsActive(exam.getIsActive());
        return dto;
    }
}
