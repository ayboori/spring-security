package com.sparta.springsecurity.auth.application.controller;

import com.sparta.springsecurity.auth.infrastructure.UserDetailsImpl;
import com.sparta.springsecurity.auth.application.dto.*;
import com.sparta.springsecurity.auth.domain.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // 회원가입
    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public SignupResponseDto signUp(@RequestBody SignupRequestDto requestDto){
        return authService.signUp(requestDto);
    }

    // 로그인
    @Operation(summary = "로그인")
    @GetMapping("/sign")
    public LoginResponseDto login(@RequestBody LoginRequestDto requestDto){
        return authService.login(requestDto);
    }

    // admin으로 역할 변경
    @Operation(summary = "admin으로 역할 변경", description = "로그인한 사용자의 role을 admin으로 변경")
    @PutMapping("/role-to-admin")
    public String roleToAdmin(@RequestBody AdminRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return authService.roleToAdmin(requestDto,userDetails.getUser());
    }
}
