package com.example.grade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.grade.entity.ScoreConsult;
import com.example.grade.mapper.ScoreConsultMapper;
import com.example.grade.model.request.ConsultReplyRequest;
import com.example.grade.model.request.ConsultRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class ConsultService extends ServiceImpl<ScoreConsultMapper, ScoreConsult> {

    public Long submit(ConsultRequest request) {
        ScoreConsult consult = new ScoreConsult();
        BeanUtils.copyProperties(request, consult);
        consult.setStatus(0);
        this.save(consult);
        return consult.getId();
    }

    public void reply(Long teacherId, ConsultReplyRequest request) {
        ScoreConsult consult = this.getById(request.getId());
        Assert.notNull(consult, "咨询不存在");
        consult.setReply(request.getReply());
        consult.setStatus(1);
        consult.setTeacherId(teacherId);
        this.updateById(consult);
    }

    public List<ScoreConsult> listForStudent(Long studentId) {
        return this.list(new LambdaQueryWrapper<ScoreConsult>().eq(ScoreConsult::getStudentId, studentId));
    }

    public List<ScoreConsult> listPending() {
        return this.list(new LambdaQueryWrapper<ScoreConsult>().eq(ScoreConsult::getStatus, 0));
    }
}
