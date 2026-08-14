package com.exam.online_exam_system.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exam.online_exam_system.entity.Exam;
import com.exam.online_exam_system.entity.ExamAttempt;
import com.exam.online_exam_system.entity.Question;
import com.exam.online_exam_system.entity.Result;
import com.exam.online_exam_system.repository.QuestionRepository;
import com.exam.online_exam_system.repository.ResultRepository;

@Service
public class ResultService {

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private QuestionRepository questionRepository;

    public Result calculateAndSaveResult(ExamAttempt attempt, Map<Long, String> answers) {
        Exam exam = attempt.getExam();
        
        List<Question> allQuestions = questionRepository.findByExamId(exam.getId());
        
        int totalQuestions = allQuestions.size();
        int attemptedQuestions = 0;
        int correctAnswers = 0;
        int wrongAnswers = 0;
        
        double totalScore = 0.0;
        double negativeMarking = exam.getNegativeMarking() != null ? exam.getNegativeMarking() : 0.0;
        
        for (Question question : allQuestions) {
            String selectedOption = answers != null ? answers.get(question.getId()) : null;
            
            if (selectedOption != null && !selectedOption.trim().isEmpty()) {
                attemptedQuestions++;
                
                if (selectedOption.equalsIgnoreCase(question.getCorrectAnswer())) {
                    correctAnswers++;
                    totalScore += question.getMarks() != null ? question.getMarks() : 1.0;
                } else {
                    wrongAnswers++;
                    totalScore -= negativeMarking;
                }
            }
        }
        
        int blankAnswers = totalQuestions - attemptedQuestions;
        double percentage = totalQuestions > 0 ? ((double) correctAnswers / totalQuestions) * 100 : 0.0;
        
        boolean passed = false;
        if (exam.getPassingMarks() != null) {
            passed = totalScore >= exam.getPassingMarks();
        } else {
            passed = percentage >= 40.0; // Default passing criteria
        }
        
        Result result = new Result();
        result.setUser(attempt.getUser());
        result.setExam(exam);
        result.setScore(totalScore);
        result.setTotalQuestions(totalQuestions);
        result.setAttemptedQuestions(attemptedQuestions);
        result.setCorrectAnswers(correctAnswers);
        result.setWrongAnswers(wrongAnswers);
        result.setBlankAnswers(blankAnswers);
        result.setPercentage(percentage);
        result.setPassed(passed);
        result.setStatus(passed ? "PASS" : "FAIL");
        
        return resultRepository.save(result);
    }

    public List<Result> getResultsByUserEmail(String email) {
        return resultRepository.findByUserEmail(email);
    }

    public List<Result> getAllResults() {
        return resultRepository.findAll();
    }

    public Result getResultById(Long id) {
        return resultRepository.findById(id).orElse(null);
    }

    private com.exam.online_exam_system.dto.ResultDTO mapToDTO(Result result) {
        com.exam.online_exam_system.dto.ResultDTO dto = new com.exam.online_exam_system.dto.ResultDTO();
        dto.setId(result.getId());
        dto.setStudentEmail(result.getUser() != null ? result.getUser().getEmail() : null);
        dto.setScore(result.getScore());
        dto.setStatus(result.getStatus());
        dto.setTotalQuestions(result.getTotalQuestions());
        dto.setAttemptedQuestions(result.getAttemptedQuestions());
        dto.setCorrectAnswers(result.getCorrectAnswers());
        dto.setWrongAnswers(result.getWrongAnswers());
        dto.setBlankAnswers(result.getBlankAnswers());
        dto.setPercentage(result.getPercentage());
        dto.setPassed(result.getPassed());
        // Map exam details if needed
        return dto;
    }
}
