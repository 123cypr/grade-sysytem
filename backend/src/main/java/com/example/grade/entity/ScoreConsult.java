package com.example.grade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("score_consult")
public class ScoreConsult {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Long subjectId;
    private Long examId;
    private String question;
    private String reply;
    private Integer status;
    private Long teacherId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
}
