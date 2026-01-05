package com.example.grade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.grade.entity.ScoreChangeLog;
import com.example.grade.entity.ScoreInfo;
import com.example.grade.mapper.ScoreChangeLogMapper;
import com.example.grade.mapper.ScoreInfoMapper;
import com.example.grade.model.request.ScoreChangeApproveRequest;
import com.example.grade.model.request.ScoreChangeRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class ScoreApprovalService extends ServiceImpl<ScoreChangeLogMapper, ScoreChangeLog> {

    private final ScoreInfoMapper scoreInfoMapper;

    public ScoreApprovalService(ScoreInfoMapper scoreInfoMapper) {
        this.scoreInfoMapper = scoreInfoMapper;
    }

    public Long submitChange(Long operatorId, ScoreChangeRequest request) {
        ScoreInfo score = scoreInfoMapper.selectById(request.getScoreId());
        Assert.notNull(score, "成绩不存在");
        ScoreChangeLog log = new ScoreChangeLog();
        log.setScoreId(score.getId());
        log.setStudentId(score.getStudentId());
        log.setExamId(score.getExamId());
        log.setSubjectId(score.getSubjectId());
        log.setOldScore(score.getScore());
        log.setNewScore(request.getNewScore());
        log.setStatus(0);
        log.setOperatorId(operatorId);
        log.setRemark(request.getRemark());
        this.save(log);
        return log.getId();
    }

    public void approve(Long approverId, ScoreChangeApproveRequest request) {
        ScoreChangeLog log = this.getById(request.getId());
        Assert.notNull(log, "审批记录不存在");
        if (log.getStatus() != 0) {
            throw new IllegalStateException("已处理");
        }
        log.setApproverId(approverId);
        log.setRemark(request.getRemark());
        log.setStatus(Boolean.TRUE.equals(request.getApprove()) ? 1 : 2);
        this.updateById(log);
        if (log.getStatus() == 1) {
            ScoreInfo score = scoreInfoMapper.selectById(log.getScoreId());
            if (score != null) {
                score.setScore(log.getNewScore());
                scoreInfoMapper.updateById(score);
            }
        }
    }

    public List<ScoreChangeLog> listPending() {
        return this.list(new LambdaQueryWrapper<ScoreChangeLog>().eq(ScoreChangeLog::getStatus, 0));
    }

    public List<ScoreChangeLog> listByStudent(Long studentId) {
        return this.list(new LambdaQueryWrapper<ScoreChangeLog>().eq(ScoreChangeLog::getStudentId, studentId));
    }
}
