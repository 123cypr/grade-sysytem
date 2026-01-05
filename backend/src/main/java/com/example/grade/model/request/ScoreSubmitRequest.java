package com.example.grade.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class ScoreSubmitRequest {
    @NotNull
    private Long studentId;
    @NotNull
    private Long examId;
    @NotNull
    private Long subjectId;
    @Min(0)
    private BigDecimal score;
    private Map<String, BigDecimal> sportsItems;
}
