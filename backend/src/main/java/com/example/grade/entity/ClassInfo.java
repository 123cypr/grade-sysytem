package com.example.grade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("class_info")
public class ClassInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer grade;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
}
