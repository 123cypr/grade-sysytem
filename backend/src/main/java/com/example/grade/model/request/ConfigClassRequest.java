package com.example.grade.model.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConfigClassRequest {
    @NotBlank
    private String name;
    @Min(1)
    @Max(3)
    private Integer grade;
}
