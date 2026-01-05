package com.example.grade.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ScoreChangeRequest {
    @NotNull
    private Long scoreId;
    @NotNull
    private BigDecimal newScore;
    private String remark;
}
