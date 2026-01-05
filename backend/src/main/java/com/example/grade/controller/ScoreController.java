package com.example.grade.controller;

import com.example.grade.common.ApiResponse;
import com.example.grade.model.request.ScoreSubmitRequest;
import com.example.grade.model.response.ScoreResponse;
import com.example.grade.service.ScoreService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scores")
public class ScoreController {

    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('1','2')")
    public ApiResponse<Long> submit(@Valid @RequestBody ScoreSubmitRequest request) {
        return ApiResponse.success(scoreService.submit(request).getId());
    }

    @GetMapping
    public ApiResponse<List<ScoreResponse>> list(@RequestParam(required = false) Long studentId,
                                                 @RequestParam(required = false) Long examId,
                                                 @RequestParam(required = false) Long subjectId) {
        return ApiResponse.success(scoreService.listScores(studentId, examId, subjectId));
    }
}
