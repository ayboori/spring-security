package com.sparta.springsecurity.entity;

import com.sparta.springsecurity.dto.SignupRequestDto;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Entity
@Table(name = "p_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    public User(SignupRequestDto requestDto, String password) {
        this.username = requestDto.getUsername();
        this.nickname = requestDto.getNickname();
        this.password = password;
        this.role = UserRoleEnum.USER;
    }
}