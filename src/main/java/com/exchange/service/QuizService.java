package com.exchange.service;

import com.exchange.model.entity.Quiz;
import com.exchange.model.entity.QuizAttempt;
import com.exchange.repository.QuizRepository;
import com.exchange.repository.QuizAttemptRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizAttemptRepository attemptRepository;
    private final ObjectMapper objectMapper;

    public QuizService(QuizRepository quizRepository,
                     QuizAttemptRepository attemptRepository,
                     ObjectMapper objectMapper) {
        this.quizRepository = quizRepository;
        this.attemptRepository = attemptRepository;
        this.objectMapper = objectMapper;
    }

    public Quiz createQuiz(String sessionId, String createdBy, String title,
                          String questionsJson, Integer passingScore) {
        Quiz quiz = Quiz.builder()
                .createdBy(createdBy)
                .title(title)
                .questions(questionsJson)
                .passingScore(passingScore)
                .build();

        Quiz saved = quizRepository.save(quiz);
        log.info("Quiz created: {} for session: {}", saved.getId(), sessionId);
        return saved;
    }

    public QuizAttempt submitQuizAttempt(String quizId, String userId, String answersJson) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        int score = calculateScore(quiz, answersJson);
        boolean passed = score >= quiz.getPassingScore();

        QuizAttempt attempt = QuizAttempt.builder()
                .quiz(quiz)
                .userId(userId)
                .answers(answersJson)
                .score(score)
                .passed(passed)
                .build();

        QuizAttempt saved = attemptRepository.save(attempt);
        log.info("Quiz attempt submitted - Quiz: {}, User: {}, Score: {}, Passed: {}",
                quizId, userId, score, passed);
        return saved;
    }

    public List<QuizAttempt> getQuizResults(String quizId) {
        return attemptRepository.findByQuizId(quizId);
    }

    public List<QuizAttempt> getUserAttempts(String quizId, String userId) {
        return attemptRepository.findByQuizIdAndUserId(quizId, userId);
    }

    private int calculateScore(Quiz quiz, String answersJson) {
        // Simplified scoring logic - in production, would compare with correct answers
        try {
            List<?> answers = objectMapper.readValue(answersJson, List.class);
            return (int) ((double) answers.size() / 10 * 100);
        } catch (Exception e) {
            log.error("Error calculating score", e);
            return 0;
        }
    }
}
