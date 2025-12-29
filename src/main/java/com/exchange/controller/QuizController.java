package com.exchange.controller;

import com.exchange.model.dto.QuizAttemptRequest;
import com.exchange.model.dto.QuizRequest;
import com.exchange.model.entity.Quiz;
import com.exchange.model.entity.QuizAttempt;
import com.exchange.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quizzes")
@Slf4j
@Tag(name = "Quizzes", description = "Quiz and assessment endpoints")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping("/{sessionId}")
    @Operation(summary = "Create a quiz for a session")
    public ResponseEntity<Quiz> createQuiz(
            @PathVariable String sessionId,
            @Valid @RequestBody QuizRequest request) {
        Quiz quiz = quizService.createQuiz(
                sessionId,
                request.getCreatedBy(),
                request.getTitle(),
                request.getQuestionsJson(),
                request.getPassingScore()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(quiz);
    }

    @PostMapping("/{quizId}/attempt")
    @Operation(summary = "Submit quiz attempt")
    public ResponseEntity<QuizAttempt> submitAttempt(
            @PathVariable String quizId,
            @Valid @RequestBody QuizAttemptRequest request) {
        QuizAttempt attempt = quizService.submitQuizAttempt(quizId, request.getUserId(), request.getAnswersJson());
        return ResponseEntity.status(HttpStatus.CREATED).body(attempt);
    }

    @GetMapping("/{quizId}/results")
    @Operation(summary = "Get quiz results")
    public ResponseEntity<List<QuizAttempt>> getResults(@PathVariable String quizId) {
        List<QuizAttempt> results = quizService.getQuizResults(quizId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{quizId}/attempts")
    @Operation(summary = "Get user attempts for a quiz")
    public ResponseEntity<List<QuizAttempt>> getUserAttempts(
            @PathVariable String quizId,
            @RequestParam String userId) {
        List<QuizAttempt> attempts = quizService.getUserAttempts(quizId, userId);
        return ResponseEntity.ok(attempts);
    }
}