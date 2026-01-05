package com.example.grade.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.grade.entity.*;
import com.example.grade.mapper.*;
import com.example.grade.model.request.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class ConfigService {

    private final ClassInfoMapper classInfoMapper;
    private final SubjectInfoMapper subjectInfoMapper;
    private final ExamTypeMapper examTypeMapper;
    private final SportsItemMapper sportsItemMapper;

    public ConfigService(ClassInfoMapper classInfoMapper,
                         SubjectInfoMapper subjectInfoMapper,
                         ExamTypeMapper examTypeMapper,
                         SportsItemMapper sportsItemMapper) {
        this.classInfoMapper = classInfoMapper;
        this.subjectInfoMapper = subjectInfoMapper;
        this.examTypeMapper = examTypeMapper;
        this.sportsItemMapper = sportsItemMapper;
    }

    public Long saveClass(ConfigClassRequest req) {
        ClassInfo info = new ClassInfo();
        BeanUtils.copyProperties(req, info);
        classInfoMapper.insert(info);
        return info.getId();
    }

    public void updateClass(Long id, ConfigClassRequest req) {
        ClassInfo info = classInfoMapper.selectById(id);
        Assert.notNull(info, "班级不存在");
        BeanUtils.copyProperties(req, info);
        classInfoMapper.updateById(info);
    }

    public void deleteClass(Long id) {
        classInfoMapper.deleteById(id);
    }

    public List<ClassInfo> listClass() {
        return classInfoMapper.selectList(null);
    }

    public Long saveSubject(SubjectRequest req) {
        SubjectInfo info = new SubjectInfo();
        BeanUtils.copyProperties(req, info);
        subjectInfoMapper.insert(info);
        return info.getId();
    }

    public void updateSubject(Long id, SubjectRequest req) {
        SubjectInfo info = subjectInfoMapper.selectById(id);
        Assert.notNull(info, "学科不存在");
        BeanUtils.copyProperties(req, info);
        subjectInfoMapper.updateById(info);
    }

    public void deleteSubject(Long id) {
        subjectInfoMapper.deleteById(id);
    }

    public List<SubjectInfo> listSubject() {
        return subjectInfoMapper.selectList(null);
    }

    public Long saveExamType(ExamTypeRequest req) {
        ExamType info = new ExamType();
        BeanUtils.copyProperties(req, info);
        examTypeMapper.insert(info);
        return info.getId();
    }

    public void updateExamType(Long id, ExamTypeRequest req) {
        ExamType info = examTypeMapper.selectById(id);
        Assert.notNull(info, "考试类型不存在");
        BeanUtils.copyProperties(req, info);
        examTypeMapper.updateById(info);
    }

    public void deleteExamType(Long id) {
        examTypeMapper.deleteById(id);
    }

    public List<ExamType> listExamType() {
        return examTypeMapper.selectList(null);
    }

    public Long saveSportsItem(SportsItemRequest req) {
        ExamType examType = examTypeMapper.selectById(req.getExamTypeId());
        Assert.notNull(examType, "考试类型不存在");
        SportsItem info = new SportsItem();
        BeanUtils.copyProperties(req, info);
        sportsItemMapper.insert(info);
        return info.getId();
    }

    public void updateSportsItem(Long id, SportsItemRequest req) {
        SportsItem info = sportsItemMapper.selectById(id);
        Assert.notNull(info, "体育分项不存在");
        ExamType examType = examTypeMapper.selectById(req.getExamTypeId());
        Assert.notNull(examType, "考试类型不存在");
        BeanUtils.copyProperties(req, info);
        sportsItemMapper.updateById(info);
    }

    public void deleteSportsItem(Long id) {
        sportsItemMapper.deleteById(id);
    }

    public List<SportsItem> listSportsItem(Long examTypeId) {
        return sportsItemMapper.selectList(examTypeId == null ? null :
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SportsItem>()
                        .eq(SportsItem::getExamTypeId, examTypeId));
    }
}
