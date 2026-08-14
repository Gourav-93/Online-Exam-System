package com.exam.online_exam_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.exam.online_exam_system.entity.Result;

public interface ResultRepository extends JpaRepository<Result, Long> {

    @EntityGraph(attributePaths = {"exam"})
    List<Result> findByUserEmail(String email);
}