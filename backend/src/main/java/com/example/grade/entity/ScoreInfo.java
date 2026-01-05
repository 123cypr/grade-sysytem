package com.example.grade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("score_info")
public class ScoreInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Long examId;
    private Long subjectId;
    private BigDecimal score;
    private String sportsItems;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
}
