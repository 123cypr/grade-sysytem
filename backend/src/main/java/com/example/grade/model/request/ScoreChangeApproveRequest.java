package com.example.grade.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScoreChangeApproveRequest {
    @NotNull
    private Long id;
    @NotNull
    private Boolean approve;
    private String remark;
}
