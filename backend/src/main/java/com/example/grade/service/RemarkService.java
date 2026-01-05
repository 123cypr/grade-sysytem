package com.example.grade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.grade.entity.ScoreRemark;
import com.example.grade.mapper.ScoreRemarkMapper;
import com.example.grade.model.request.RemarkRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RemarkService extends ServiceImpl<ScoreRemarkMapper, ScoreRemark> {

    public Long saveRemark(Long teacherId, RemarkRequest request) {
        ScoreRemark remark = new ScoreRemark();
        BeanUtils.copyProperties(request, remark);
        remark.setTeacherId(teacherId);
        this.save(remark);
        return remark.getId();
    }

    public List<ScoreRemark> listRemarks(Long studentId, Long examId) {
        LambdaQueryWrapper<ScoreRemark> wrapper = new LambdaQueryWrapper<>();
        if (studentId != null) wrapper.eq(ScoreRemark::getStudentId, studentId);
        if (examId != null) wrapper.eq(ScoreRemark::getExamId, examId);
        return this.list(wrapper);
    }
}
