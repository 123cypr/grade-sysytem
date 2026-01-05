package com.example.grade.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SubjectRequest {
    @NotBlank
    private String name;
    @Min(1)
    private BigDecimal fullScore;
    private Integer isTotal;
}
