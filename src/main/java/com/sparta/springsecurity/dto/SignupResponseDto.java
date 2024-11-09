package com.sparta.springsecurity.dto;

import com.sparta.springsecurity.entity.User;
import com.sparta.springsecurity.entity.UserRoleEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SignupResponseDto {
    private String username;
    private String password;
    private String authorities;

    public SignupResponseDto(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.authorities = user.getRole().getAuthority();
    }
}
