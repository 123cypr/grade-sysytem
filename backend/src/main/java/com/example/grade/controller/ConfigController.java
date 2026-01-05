package com.example.grade.controller;

import com.example.grade.common.ApiResponse;
import com.example.grade.entity.ClassInfo;
import com.example.grade.entity.ExamType;
import com.example.grade.entity.SportsItem;
import com.example.grade.entity.SubjectInfo;
import com.example.grade.model.request.*;
import com.example.grade.service.ConfigService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    // Classes
    @PostMapping("/classes")
    @PreAuthorize("hasRole('1')")
    public ApiResponse<Long> createClass(@Valid @RequestBody ConfigClassRequest request) {
        return ApiResponse.success(configService.saveClass(request));
    }

    @PutMapping("/classes/{id}")
    @PreAuthorize("hasRole('1')")
    public ApiResponse<Void> updateClass(@PathVariable Long id, @Valid @RequestBody ConfigClassRequest request) {
        configService.updateClass(id, request);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/classes/{id}")
    @PreAuthorize("hasRole('1')")
    public ApiResponse<Void> deleteClass(@PathVariable Long id) {
        configService.deleteClass(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/classes")
    public ApiResponse<List<ClassInfo>> listClass() {
        return ApiResponse.success(configService.listClass());
    }

    // Subjects
    @PostMapping("/subjects")
    @PreAuthorize("hasRole('1')")
    public ApiResponse<Long> createSubject(@Valid @RequestBody SubjectRequest request) {
        return ApiResponse.success(configService.saveSubject(request));
    }

    @PutMapping("/subjects/{id}")
    @PreAuthorize("hasRole('1')")
    public ApiResponse<Void> updateSubject(@PathVariable Long id, @Valid @RequestBody SubjectRequest request) {
        configService.updateSubject(id, request);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/subjects/{id}")
    @PreAuthorize("hasRole('1')")
    public ApiResponse<Void> deleteSubject(@PathVariable Long id) {
        configService.deleteSubject(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/subjects")
    public ApiResponse<List<SubjectInfo>> listSubject() {
        return ApiResponse.success(configService.listSubject());
    }

    // Exam types
    @PostMapping("/exam-types")
    @PreAuthorize("hasRole('1')")
    public ApiResponse<Long> createExamType(@Valid @RequestBody ExamTypeRequest request) {
        return ApiResponse.success(configService.saveExamType(request));
    }

    @PutMapping("/exam-types/{id}")
    @PreAuthorize("hasRole('1')")
    public ApiResponse<Void> updateExamType(@PathVariable Long id, @Valid @RequestBody ExamTypeRequest request) {
        configService.updateExamType(id, request);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/exam-types/{id}")
    @PreAuthorize("hasRole('1')")
    public ApiResponse<Void> deleteExamType(@PathVariable Long id) {
        configService.deleteExamType(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/exam-types")
    public ApiResponse<List<ExamType>> listExamType() {
        return ApiResponse.success(configService.listExamType());
    }

    // Sports items
    @PostMapping("/sports-items")
    @PreAuthorize("hasRole('1')")
    public ApiResponse<Long> createSportsItem(@Valid @RequestBody SportsItemRequest request) {
        return ApiResponse.success(configService.saveSportsItem(request));
    }

    @PutMapping("/sports-items/{id}")
    @PreAuthorize("hasRole('1')")
    public ApiResponse<Void> updateSportsItem(@PathVariable Long id, @Valid @RequestBody SportsItemRequest request) {
        configService.updateSportsItem(id, request);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/sports-items/{id}")
    @PreAuthorize("hasRole('1')")
    public ApiResponse<Void> deleteSportsItem(@PathVariable Long id) {
        configService.deleteSportsItem(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/sports-items")
    public ApiResponse<List<SportsItem>> listSportsItems(@RequestParam(required = false) Long examTypeId) {
        return ApiResponse.success(configService.listSportsItem(examTypeId));
    }
}
