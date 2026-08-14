package com.exam.online_exam_system.service;

import com.exam.online_exam_system.dto.SubmitExamDTO;
import com.exam.online_exam_system.entity.AttemptStatus;
import com.exam.online_exam_system.entity.Exam;
import com.exam.online_exam_system.entity.ExamAttempt;
import com.exam.online_exam_system.entity.Result;
import com.exam.online_exam_system.entity.User;
import com.exam.online_exam_system.exception.ResourceNotFoundException;
import com.exam.online_exam_system.repository.ExamAttemptRepository;
import com.exam.online_exam_system.repository.ExamRepository;
import com.exam.online_exam_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AttemptService {

    @Autowired
    private ExamAttemptRepository examAttemptRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResultService resultService;

    public ExamAttempt startAttempt(Long userId, Long examId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        // Check if exam is active and within time bounds
        if (!exam.getIsActive()) {
            throw new IllegalStateException("Exam is not active");
        }

        LocalDateTime now = LocalDateTime.now();
        if (exam.getStartTime() != null && now.isBefore(exam.getStartTime())) {
            throw new IllegalStateException("Exam has not started yet");
        }
        if (exam.getEndTime() != null && now.isAfter(exam.getEndTime())) {
            throw new IllegalStateException("Exam has ended");
        }

        // Check for existing in-progress attempt
        // Ensure single attempt
        boolean hasExisting = examAttemptRepository.findByUserId(userId).stream()
                .anyMatch(a -> a.getExam().getId().equals(examId));
        if (hasExisting) {
            throw new IllegalStateException("You have already attempted or are currently attempting this exam");
        }

        ExamAttempt attempt = new ExamAttempt();
        attempt.setUser(user);
        attempt.setExam(exam);
        attempt.setStartTime(now);
        attempt.setStatus(AttemptStatus.IN_PROGRESS);
        attempt.setWarningCount(0);

        return examAttemptRepository.save(attempt);
    }

    @Transactional
    public Result submitAttempt(Long attemptId, SubmitExamDTO submitDTO, boolean autoSubmit) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found"));
        
        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new IllegalStateException("Attempt is already submitted");
        }

        LocalDateTime now = LocalDateTime.now();
        
        // Validate duration on backend (with 1 minute grace period)
        if (attempt.getExam().getDuration() != null) {
             LocalDateTime maxAllowedTime = attempt.getStartTime()
                    .plusMinutes(attempt.getExam().getDuration())
                    .plusMinutes(1); 
             if (now.isAfter(maxAllowedTime)) {
                 // Force auto-submit state if they submit way past deadline
                 autoSubmit = true;
             }
        }

        attempt.setSubmissionTime(now);
        attempt.setStatus(autoSubmit ? AttemptStatus.AUTO_SUBMITTED : AttemptStatus.SUBMITTED);
        examAttemptRepository.save(attempt);

        return resultService.calculateAndSaveResult(attempt, submitDTO.getAnswers());
    }

    public void incrementWarning(Long attemptId) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found"));
        
        if (attempt.getStatus() == AttemptStatus.IN_PROGRESS) {
            attempt.setWarningCount(attempt.getWarningCount() + 1);
            
            // Auto submit after 3 warnings
            if (attempt.getWarningCount() >= 3) {
                submitAttempt(attemptId, new SubmitExamDTO(attemptId, java.util.Collections.emptyMap()), true);
            } else {
                examAttemptRepository.save(attempt);
            }
        }
    }
}
