package com.example.grade.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamTypeRequest {
    @NotBlank
    private String name;
    private LocalDateTime examTime;
    private Integer isRank;
    private Integer isPublic;
}
