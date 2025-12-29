package com.exchange.service;

import com.exchange.model.entity.Quiz;
import com.exchange.model.entity.QuizAttempt;
import com.exchange.repository.QuizAttemptRepository;
import com.exchange.repository.QuizRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuizAttemptRepository attemptRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private QuizService quizService;

    private Quiz testQuiz;

    @BeforeEach
    void setUp() {
        testQuiz = Quiz.builder()
                .id("quiz-1")
                .title("Test Quiz")
                .createdBy("teacher-1")
                .passingScore(70)
                .questions("[{\"question\": \"Q1\"}]")
                .build();
    }

    @Test
    void testCreateQuiz() {
        when(quizRepository.save(any(Quiz.class))).thenReturn(testQuiz);

        Quiz created = quizService.createQuiz(
                "session-1",
                "teacher-1",
                "Test Quiz",
                "[{\"question\": \"Q1\"}]",
                70
        );

        assertNotNull(created);
        assertEquals("Test Quiz", created.getTitle());
        verify(quizRepository, times(1)).save(any(Quiz.class));
    }

    @Test
    void testSubmitQuizAttempt() {
        testQuiz.setPassingScore(70);
        when(quizRepository.findById("quiz-1")).thenReturn(Optional.of(testQuiz));
        when(attemptRepository.save(any(QuizAttempt.class))).thenAnswer(invocation -> invocation.getArgument(0));

        QuizAttempt attempt = quizService.submitQuizAttempt("quiz-1", "student-1", "[{\"answer\": \"A\"}]");

        assertNotNull(attempt);
        assertEquals("student-1", attempt.getUserId());
        verify(attemptRepository, times(1)).save(any(QuizAttempt.class));
    }
}
