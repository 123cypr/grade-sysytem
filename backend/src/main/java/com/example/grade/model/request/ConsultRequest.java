package com.example.grade.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConsultRequest {
    @NotNull
    private Long studentId;
    private Long subjectId;
    private Long examId;
    @NotBlank
    private String question;
}
