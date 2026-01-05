package com.example.grade.controller;

import com.example.grade.common.ApiResponse;
import com.example.grade.entity.ScoreConsult;
import com.example.grade.model.request.ConsultReplyRequest;
import com.example.grade.model.request.ConsultRequest;
import com.example.grade.service.ConsultService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consults")
public class ConsultController {

    private final ConsultService consultService;

    public ConsultController(ConsultService consultService) {
        this.consultService = consultService;
    }

    @PostMapping
    public ApiResponse<Long> submit(@Valid @RequestBody ConsultRequest request) {
        return ApiResponse.success(consultService.submit(request));
    }

    @PostMapping("/reply")
    @PreAuthorize("hasAnyRole('1','2')")
    public ApiResponse<Void> reply(Authentication authentication, @Valid @RequestBody ConsultReplyRequest request) {
        Long teacherId = authentication == null ? null : ((com.example.grade.model.AuthPayload) authentication.getPrincipal()).getUserId();
        consultService.reply(teacherId, request);
        return ApiResponse.success(null);
    }

    @GetMapping("/student/{studentId}")
    public ApiResponse<List<ScoreConsult>> listByStudent(@PathVariable Long studentId) {
        return ApiResponse.success(consultService.listForStudent(studentId));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('1','2')")
    public ApiResponse<List<ScoreConsult>> pending() {
        return ApiResponse.success(consultService.listPending());
    }
}
