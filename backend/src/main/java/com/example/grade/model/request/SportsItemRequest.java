package com.example.grade.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SportsItemRequest {
    @NotNull
    private Long examTypeId;
    @NotBlank
    private String name;
    @Min(0)
    private BigDecimal fullScore;
}
