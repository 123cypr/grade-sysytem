package com.example.grade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.grade.entity.ScoreInfo;
import com.example.grade.mapper.ScoreInfoMapper;
import com.example.grade.model.response.StatsResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private final ScoreInfoMapper scoreInfoMapper;

    public StatsService(ScoreInfoMapper scoreInfoMapper) {
        this.scoreInfoMapper = scoreInfoMapper;
    }

    public StatsResponse stats(Long examId, Long subjectId, BigDecimal passLine, BigDecimal excellentLine) {
        LambdaQueryWrapper<ScoreInfo> wrapper = new LambdaQueryWrapper<>();
        if (examId != null) wrapper.eq(ScoreInfo::getExamId, examId);
        if (subjectId != null) wrapper.eq(ScoreInfo::getSubjectId, subjectId);
        List<ScoreInfo> scores = scoreInfoMapper.selectList(wrapper);
        StatsResponse resp = new StatsResponse();
        resp.setExamId(examId);
        resp.setSubjectId(subjectId);
        if (scores.isEmpty()) {
            resp.setAverage(BigDecimal.ZERO);
            resp.setPassRate(BigDecimal.ZERO);
            resp.setExcellentRate(BigDecimal.ZERO);
            return resp;
        }
        BigDecimal sum = scores.stream()
                .map(ScoreInfo::getScore)
                .filter(s -> s != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        resp.setAverage(sum.divide(BigDecimal.valueOf(scores.size()), 2, RoundingMode.HALF_UP));
        long passCount = scores.stream().filter(s -> s.getScore() != null && s.getScore().compareTo(passLine) >= 0).count();
        long excellentCount = scores.stream().filter(s -> s.getScore() != null && s.getScore().compareTo(excellentLine) >= 0).count();
        resp.setPassRate(BigDecimal.valueOf(passCount * 100.0 / scores.size()).setScale(2, RoundingMode.HALF_UP));
        resp.setExcellentRate(BigDecimal.valueOf(excellentCount * 100.0 / scores.size()).setScale(2, RoundingMode.HALF_UP));
        return resp;
    }
}
