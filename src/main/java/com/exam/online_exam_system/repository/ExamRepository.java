package com.exam.online_exam_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exam.online_exam_system.entity.Exam;

public interface ExamRepository extends JpaRepository<Exam, Long> {
}