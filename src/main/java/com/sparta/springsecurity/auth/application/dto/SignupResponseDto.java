package com.sparta.springsecurity.auth.application.dto;

import com.sparta.springsecurity.auth.domain.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupResponseDto {
    private String username;
    private String nickname;
    private String authorities;

    public SignupResponseDto(User user) {
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.authorities = user.getRole().getAuthority();
    }
}
