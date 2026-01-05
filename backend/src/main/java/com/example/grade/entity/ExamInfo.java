package com.example.grade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("exam_info")
public class ExamInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long examTypeId;
    private Long classId;
    private String name;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
}
