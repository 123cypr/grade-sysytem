package com.example.grade.controller;

import com.example.grade.common.ApiResponse;
import com.example.grade.model.response.StatsResponse;
import com.example.grade.service.StatsService;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/score")
    public ApiResponse<StatsResponse> stats(@RequestParam(required = false) Long examId,
                                            @RequestParam(required = false) Long subjectId,
                                            @RequestParam @NotNull BigDecimal passLine,
                                            @RequestParam @NotNull BigDecimal excellentLine) {
        return ApiResponse.success(statsService.stats(examId, subjectId, passLine, excellentLine));
    }
}
