package com.example.grade.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RemarkRequest {
    @NotNull
    private Long studentId;
    @NotNull
    private Long examId;
    @NotBlank
    private String remark;
}
