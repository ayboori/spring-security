package com.sparta.springsecurity.auth.domain.service;

import com.sparta.springsecurity.auth.domain.entity.UserRoleEnum;
import com.sparta.springsecurity.auth.domain.repository.UserRepository;
import com.sparta.springsecurity.auth.infrastructure.JwtUtil;
import com.sparta.springsecurity.auth.application.dto.*;
import com.sparta.springsecurity.auth.domain.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // ADMIN_TOKEN
    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    public SignupResponseDto signUp(SignupRequestDto requestDto) {
        // username 중복 확인
       Optional<User> userCheck = userRepository.findByUsername(requestDto.getUsername());
       if(userCheck.isPresent()){
           throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
       }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 사용자 등록
        User user = new User(requestDto, encodedPassword);
        userRepository.save(user);

        return new SignupResponseDto(user);
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto requestDto) {
        // 사용자 존재 확인
        User user = userRepository.findByUsername(requestDto.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 사용자입니다.")
        );

        // 비밀번호 확인
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // JWT 생성 - access Token
        String accessToken = jwtUtil.createAccessToken(user.getUsername(), user.getRole());

        // Refresh Token 생성 / 갱신
        String refreshToken;
        Optional<String> existingRefreshToken = userRepository.findRefreshTokenByUsername(user.getUsername());

        if (existingRefreshToken.isPresent() && jwtUtil.validateToken(existingRefreshToken.get())) {
            // 기존 Refresh Token이 유효한 경우 그대로 사용
            refreshToken = existingRefreshToken.get();
        } else {
            // 기존 Refresh Token이 없거나 만료된 경우 새로 발급
            refreshToken = jwtUtil.createRefreshToken(user.getUsername());
            user.updateRefreshToken(refreshToken);
        }
        log.info("현재 사용자 이름 : {}", user.getUsername());

        return new LoginResponseDto(accessToken, refreshToken);
    }

    @Transactional
    public String roleToAdmin(AdminRequestDto requestDto, User user) {
        log.info("현재 역할: {}", user.getRole().getAuthority());  // 기존 역할 확인
        log.info("현재 사용자: {}", user.getUsername());

        if (!ADMIN_TOKEN.equals(requestDto.getAdminToken())) {
            throw new IllegalArgumentException("관리자 암호가 틀렸습니다.");
        }

        // repository에서 값 찾아와서 변경
        User user1 = userRepository.findByUsername(user.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
        );

        user1.updateRole(UserRoleEnum.ADMIN);

        log.info("변경된 역할: {}", user1.getRole().getAuthority());  // 기존 역할 확인

        return "사용자 역할이 ADMIN으로 변경되었습니다.";
    }
}
