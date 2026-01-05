package com.example.grade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("score_change_log")
public class ScoreChangeLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long scoreId;
    private Long studentId;
    private Long examId;
    private Long subjectId;
    private BigDecimal oldScore;
    private BigDecimal newScore;
    private Integer status;
    private Long operatorId;
    private Long approverId;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
}
