package com.example.grade.model;

import lombok.Data;

@Data
public class AuthPayload {
    private Long userId;
    private String username;
    private Integer roleType;
}
