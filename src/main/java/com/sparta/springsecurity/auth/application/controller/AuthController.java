package com.sparta.springsecurity.auth.application.controller;

import com.sparta.springsecurity.auth.infrastructure.UserDetailsImpl;
import com.sparta.springsecurity.auth.application.dto.*;
import com.sparta.springsecurity.auth.domain.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    public SignupResponseDto signUp(@RequestBody SignupRequestDto requestDto){
        return authService.signUp(requestDto);
    }

    // 로그인
    @GetMapping("/sign")
    public LoginResponseDto login(@RequestBody LoginRequestDto requestDto){
        return authService.login(requestDto);
    }

    // admin으로 역할 변경
    @PutMapping("/role-to-admin")
    public String roleToAdmin(@RequestBody AdminRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return authService.roleToAdmin(requestDto,userDetails.getUser());
    }
}
