package com.example.grade.model.response;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private Integer roleType;
    private Integer status;
}
