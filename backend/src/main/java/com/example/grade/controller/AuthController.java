package com.example.grade.controller;

import com.example.grade.common.ApiResponse;
import com.example.grade.entity.SysUser;
import com.example.grade.model.AuthPayload;
import com.example.grade.model.request.LoginRequest;
import com.example.grade.model.request.RegisterRequest;
import com.example.grade.model.response.UserResponse;
import com.example.grade.security.JwtUtil;
import com.example.grade.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        SysUser user = userService.register(request.getUsername(), request.getPassword(), request.getRoleType());
        UserResponse resp = new UserResponse();
        BeanUtils.copyProperties(user, resp);
        return ApiResponse.success(resp);
    }

    @PostMapping("/login")
    public ApiResponse<String> login(@Valid @RequestBody LoginRequest request) {
        SysUser user = userService.verify(request.getUsername(), request.getPassword());
        AuthPayload payload = new AuthPayload();
        payload.setUserId(user.getId());
        payload.setUsername(user.getUsername());
        payload.setRoleType(user.getRoleType());
        String token = jwtUtil.generate(payload);
        return ApiResponse.success(token);
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> profile(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).body(ApiResponse.failure(401, "未登录"));
        }
        AuthPayload payload = (AuthPayload) authentication.getPrincipal();
        SysUser user = userService.getById(payload.getUserId());
        if (user == null) {
            return ResponseEntity.status(404).body(ApiResponse.failure(404, "用户不存在"));
        }
        UserResponse resp = new UserResponse();
        BeanUtils.copyProperties(user, resp);
        return ResponseEntity.ok(ApiResponse.success(resp));
    }
}
