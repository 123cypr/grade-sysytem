package com.example.grade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.grade.entity.ScoreInfo;
import com.example.grade.entity.SubjectInfo;
import com.example.grade.mapper.ScoreInfoMapper;
import com.example.grade.mapper.SubjectInfoMapper;
import com.example.grade.model.request.ScoreSubmitRequest;
import com.example.grade.model.response.ScoreResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ScoreService extends ServiceImpl<ScoreInfoMapper, ScoreInfo> {

    private final SubjectInfoMapper subjectInfoMapper;
    private final ObjectMapper objectMapper;

    public ScoreService(SubjectInfoMapper subjectInfoMapper, ObjectMapper objectMapper) {
        this.subjectInfoMapper = subjectInfoMapper;
        this.objectMapper = objectMapper;
    }

    public ScoreInfo submit(ScoreSubmitRequest request) {
        SubjectInfo subject = subjectInfoMapper.selectById(request.getSubjectId());
        if (subject == null || (subject.getDeleted() != null && subject.getDeleted() == 1)) {
            throw new IllegalArgumentException("学科不存在");
        }
        BigDecimal full = subject.getFullScore() != null ? subject.getFullScore() : BigDecimal.valueOf(100);

        BigDecimal total = request.getScore();
        if (!CollectionUtils.isEmpty(request.getSportsItems())) {
            BigDecimal sum = request.getSportsItems().values().stream()
                    .filter(v -> v != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            total = sum;
        }
        if (total == null) {
            throw new IllegalArgumentException("成绩或体育分项不能为空");
        }
        if (total.compareTo(BigDecimal.ZERO) < 0 || total.compareTo(full) > 0) {
            throw new IllegalArgumentException("成绩超出满分范围");
        }

        ScoreInfo existing = this.getOne(new LambdaQueryWrapper<ScoreInfo>()
                .eq(ScoreInfo::getStudentId, request.getStudentId())
                .eq(ScoreInfo::getExamId, request.getExamId())
                .eq(ScoreInfo::getSubjectId, request.getSubjectId()));

        if (existing == null) {
            existing = new ScoreInfo();
            existing.setStudentId(request.getStudentId());
            existing.setExamId(request.getExamId());
            existing.setSubjectId(request.getSubjectId());
        }
        existing.setScore(total);
        if (!CollectionUtils.isEmpty(request.getSportsItems())) {
            try {
                existing.setSportsItems(objectMapper.writeValueAsString(request.getSportsItems()));
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("体育分项格式错误");
            }
        } else {
            existing.setSportsItems(null);
        }
        this.saveOrUpdate(existing);
        return existing;
    }

    public List<ScoreResponse> listScores(Long studentId, Long examId, Long subjectId) {
        LambdaQueryWrapper<ScoreInfo> wrapper = new LambdaQueryWrapper<>();
        if (studentId != null) wrapper.eq(ScoreInfo::getStudentId, studentId);
        if (examId != null) wrapper.eq(ScoreInfo::getExamId, examId);
        if (subjectId != null) wrapper.eq(ScoreInfo::getSubjectId, subjectId);
        List<ScoreInfo> list = this.list(wrapper);
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private ScoreResponse toResponse(ScoreInfo info) {
        ScoreResponse resp = new ScoreResponse();
        BeanUtils.copyProperties(info, resp);
        if (info.getSportsItems() != null) {
            try {
                Map<String, BigDecimal> map = objectMapper.readValue(info.getSportsItems(), new TypeReference<>() {});
                resp.setSportsItems(map);
            } catch (Exception ignored) {
            }
        }
        return resp;
    }
}
