package com.sparta.springsecurity.auth.application.dto;

import lombok.Getter;

@Getter
public class SignupRequestDto {
    private String username;
    private String password;
    private String nickname;
}
