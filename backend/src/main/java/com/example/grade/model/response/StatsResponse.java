package com.example.grade.model.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StatsResponse {
    private Long examId;
    private Long subjectId;
    private BigDecimal average;
    private BigDecimal passRate;
    private BigDecimal excellentRate;
}
