package com.exchange.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizRequest {
    @NotBlank
    private String title;
    
    @NotNull
    private String questionsJson;
    
    @NotNull
    private Integer passingScore;
    
    @NotBlank
    private String createdBy;
}
