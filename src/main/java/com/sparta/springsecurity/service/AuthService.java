package com.sparta.springsecurity.service;

import com.sparta.springsecurity.auth.JwtUtil;
import com.sparta.springsecurity.dto.LoginRequestDto;
import com.sparta.springsecurity.dto.LoginResponseDto;
import com.sparta.springsecurity.dto.SignupRequestDto;
import com.sparta.springsecurity.dto.SignupResponseDto;
import com.sparta.springsecurity.entity.User;
import com.sparta.springsecurity.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public SignupResponseDto signUp(SignupRequestDto requestDto) {
        // username 중복 확인
       Optional<User> userCheck = userRepository.findByUsername(requestDto.getUsername());
       if(userCheck.isPresent()){
           throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
       }

        // 비밀번호 암호화
//        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 사용자 등록
        User user = new User(requestDto, requestDto.getPassword());
        userRepository.save(user);

        return new SignupResponseDto(user);
    }

    public LoginResponseDto login(LoginRequestDto requestDto) {
        // 사용자 존재 확인
        User user = userRepository.findByUsername(requestDto.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 사용자입니다.")
        );

        // 비밀번호 확인
//        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
//            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
//        }

        // JWT 생성
        String token = jwtUtil.createToken(user.getUsername(), user.getRole());

        return new LoginResponseDto(token);
    }
}
