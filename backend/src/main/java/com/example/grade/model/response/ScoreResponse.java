package com.example.grade.model.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class ScoreResponse {
    private Long id;
    private Long studentId;
    private Long examId;
    private Long subjectId;
    private BigDecimal score;
    private Map<String, BigDecimal> sportsItems;
}
