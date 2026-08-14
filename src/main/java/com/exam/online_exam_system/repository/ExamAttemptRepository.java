package com.exam.online_exam_system.repository;

import com.exam.online_exam_system.entity.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {
    List<ExamAttempt> findByUserId(Long userId);
    List<ExamAttempt> findByExamId(Long examId);
}
