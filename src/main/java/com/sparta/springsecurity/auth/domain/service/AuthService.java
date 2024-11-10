package com.sparta.springsecurity.auth.domain.service;

import com.sparta.springsecurity.auth.domain.entity.UserRoleEnum;
import com.sparta.springsecurity.auth.domain.repository.UserRepository;
import com.sparta.springsecurity.auth.infrastructure.JwtUtil;
import com.sparta.springsecurity.auth.application.dto.*;
import com.sparta.springsecurity.auth.domain.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
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

    public LoginResponseDto login(LoginRequestDto requestDto) {
        // 사용자 존재 확인
        User user = userRepository.findByUsername(requestDto.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 사용자입니다.")
        );

        // 비밀번호 확인
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // JWT 생성
        String token = jwtUtil.createToken(user.getUsername(), user.getRole());

        return new LoginResponseDto(token);
    }

    @Transactional
    public String roleToAdmin(AdminRequestDto requestDto, User user) {
        if (!ADMIN_TOKEN.equals(requestDto.getAdminToken())) {
            throw new IllegalArgumentException("관리자 암호가 틀렸습니다.");
        }

        // repository에서 값 찾아와서 변경
        User user1 = userRepository.findByUsername(user.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
        );

        user1.updateRole(UserRoleEnum.ADMIN);

        return "사용자 역할이 ADMIN으로 변경되었습니다.";
    }
}
