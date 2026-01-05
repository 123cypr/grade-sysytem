package com.example.grade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("exam_type")
public class ExamType {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private LocalDateTime examTime;
    private Integer isRank;
    private Integer isPublic;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
}
