package com.sparta.springsecurity.auth.application.dto;

import lombok.Getter;

@Getter
public class LoginRequestDto {
    private String username;
    private String password;
}
