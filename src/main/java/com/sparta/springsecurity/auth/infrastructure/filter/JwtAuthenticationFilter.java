package com.sparta.springsecurity.auth.infrastructure.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.springsecurity.auth.application.dto.LoginRequestDto;
import com.sparta.springsecurity.auth.application.dto.LoginResponseDto;
import com.sparta.springsecurity.auth.domain.entity.User;
import com.sparta.springsecurity.auth.domain.repository.UserRepository;
import com.sparta.springsecurity.auth.infrastructure.JwtUtil;
import com.sparta.springsecurity.auth.infrastructure.UserDetailsImpl;
import com.sparta.springsecurity.auth.domain.entity.UserRoleEnum;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/auth/sign");
        this.userRepository = userRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("로그인 요청");

        try {
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getUsername(),
                            requestDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        UserRoleEnum role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole();

        // JWT 토큰 생성
        String refreshToken = jwtUtil.createRefreshToken(username);
        String accessToken = jwtUtil.createAccessToken(username, role);

        // userRepository에서 사용자 정보 가져오기
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")
        );

        // 생성된 refreshToken을 user 객체에 업데이트
        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        // LoginResponseDto 생성
        LoginResponseDto loginResponseDto = new LoginResponseDto(refreshToken,accessToken);

        // 응답 헤더에 JWT 토큰 추가
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, accessToken);
        response.addHeader(JwtUtil.REFRESH_HEADER, refreshToken);

        // 응답 본문에 LoginResponseDto 추가
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getOutputStream(), loginResponseDto);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        log.info("로그인 실패");
        response.setStatus(401);
    }

}