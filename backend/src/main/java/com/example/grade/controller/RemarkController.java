package com.example.grade.controller;

import com.example.grade.common.ApiResponse;
import com.example.grade.entity.ScoreRemark;
import com.example.grade.model.request.RemarkRequest;
import com.example.grade.service.RemarkService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/remarks")
public class RemarkController {

    private final RemarkService remarkService;

    public RemarkController(RemarkService remarkService) {
        this.remarkService = remarkService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('1','2')")
    public ApiResponse<Long> add(Authentication authentication, @Valid @RequestBody RemarkRequest request) {
        Long teacherId = authentication == null ? null : ((com.example.grade.model.AuthPayload) authentication.getPrincipal()).getUserId();
        return ApiResponse.success(remarkService.saveRemark(teacherId, request));
    }

    @GetMapping
    public ApiResponse<List<ScoreRemark>> list(@RequestParam(required = false) Long studentId,
                                               @RequestParam(required = false) Long examId) {
        return ApiResponse.success(remarkService.listRemarks(studentId, examId));
    }
}
