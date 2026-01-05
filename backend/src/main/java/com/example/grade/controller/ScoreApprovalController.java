package com.example.grade.controller;

import com.example.grade.common.ApiResponse;
import com.example.grade.entity.ScoreChangeLog;
import com.example.grade.model.request.ScoreChangeApproveRequest;
import com.example.grade.model.request.ScoreChangeRequest;
import com.example.grade.service.ScoreApprovalService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/score-change")
public class ScoreApprovalController {

    private final ScoreApprovalService scoreApprovalService;

    public ScoreApprovalController(ScoreApprovalService scoreApprovalService) {
        this.scoreApprovalService = scoreApprovalService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('1','2')")
    public ApiResponse<Long> submit(Authentication authentication, @Valid @RequestBody ScoreChangeRequest request) {
        Long operatorId = authentication == null ? null : ((com.example.grade.model.AuthPayload) authentication.getPrincipal()).getUserId();
        return ApiResponse.success(scoreApprovalService.submitChange(operatorId, request));
    }

    @PostMapping("/approve")
    @PreAuthorize("hasAnyRole('1','2')")
    public ApiResponse<Void> approve(Authentication authentication, @Valid @RequestBody ScoreChangeApproveRequest request) {
        Long approverId = authentication == null ? null : ((com.example.grade.model.AuthPayload) authentication.getPrincipal()).getUserId();
        scoreApprovalService.approve(approverId, request);
        return ApiResponse.success(null);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('1','2')")
    public ApiResponse<List<ScoreChangeLog>> pending() {
        return ApiResponse.success(scoreApprovalService.listPending());
    }

    @GetMapping("/student/{studentId}")
    public ApiResponse<List<ScoreChangeLog>> byStudent(@PathVariable Long studentId) {
        return ApiResponse.success(scoreApprovalService.listByStudent(studentId));
    }
}
