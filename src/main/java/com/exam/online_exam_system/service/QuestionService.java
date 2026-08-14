package com.exam.online_exam_system.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exam.online_exam_system.dto.AdminQuestionDTO;
import com.exam.online_exam_system.dto.QuestionResponseDTO;
import com.exam.online_exam_system.entity.Exam;
import com.exam.online_exam_system.entity.Question;
import com.exam.online_exam_system.exception.ResourceNotFoundException;
import com.exam.online_exam_system.repository.ExamRepository;
import com.exam.online_exam_system.repository.QuestionRepository;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ExamRepository examRepository;

    public AdminQuestionDTO addQuestion(Long examId, Question question) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam Not Found with id: " + examId));
        question.setExam(exam);
        Question savedQuestion = questionRepository.save(question);
        return mapToAdminDTO(savedQuestion);
    }

    public List<AdminQuestionDTO> getAdminQuestionsByExam(Long examId) {
        examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam Not Found with id: " + examId));
        return questionRepository.findByExamId(examId).stream()
                .map(this::mapToAdminDTO)
                .collect(Collectors.toList());
    }

    public List<QuestionResponseDTO> getQuestionsForStudent(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam Not Found with id: " + examId));

        
        List<Question> questions = questionRepository.findByExamId(examId);
        
        // Randomize questions
        Collections.shuffle(questions);
        
        // Pick configured amount of questions if randomQuestionCount is set
        if (exam.getRandomQuestionCount() != null && exam.getRandomQuestionCount() > 0 && questions.size() > exam.getRandomQuestionCount()) {
            questions = questions.subList(0, exam.getRandomQuestionCount());
        }
        
        return questions.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private QuestionResponseDTO mapToDTO(Question question) {
        QuestionResponseDTO dto = new QuestionResponseDTO();
        dto.setId(question.getId());
        dto.setSubject(question.getSubject());
        dto.setTopic(question.getTopic());
        dto.setDifficultyLevel(question.getDifficultyLevel());
        dto.setMarks(question.getMarks());
        dto.setQuestion(question.getQuestion());
        
        List<String> options = Arrays.asList(question.getOptionA(), question.getOptionB(), question.getOptionC(), question.getOptionD());
        Collections.shuffle(options);
        
        dto.setOptionA(options.get(0));
        dto.setOptionB(options.get(1));
        dto.setOptionC(options.get(2));
        dto.setOptionD(options.get(3));
        return dto;
    }

    private AdminQuestionDTO mapToAdminDTO(Question question) {
        AdminQuestionDTO dto = new AdminQuestionDTO();
        dto.setId(question.getId());
        dto.setQuestion(question.getQuestion());
        dto.setOptionA(question.getOptionA());
        dto.setOptionB(question.getOptionB());
        dto.setOptionC(question.getOptionC());
        dto.setOptionD(question.getOptionD());
        dto.setCorrectAnswer(question.getCorrectAnswer());
        // Could map subject, topic, difficultyLevel, marks here as well for Admin DTO
        return dto;
    }
}
