package com.sparta.springsecurity.auth.infrastructure.filter;

import com.sparta.springsecurity.auth.domain.entity.User;
import com.sparta.springsecurity.auth.domain.repository.UserRepository;
import com.sparta.springsecurity.auth.infrastructure.JwtUtil;
import com.sparta.springsecurity.auth.infrastructure.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository userRepository;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService,
                                  UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {

        String tokenValue = jwtUtil.getJwtFromHeader(req);
        String refreshToken = req.getHeader(jwtUtil.REFRESH_HEADER);

        if (StringUtils.hasText(tokenValue)) {
            boolean isValid = false;
            try {
                isValid = jwtUtil.validateToken(tokenValue);
            } catch (ExpiredJwtException e){
                log.error("토큰 만료");

                // 로그인한 유저 정보
                String username = jwtUtil.getUserInfoFromToken(refreshToken).getSubject();
                User user = userRepository.findByUsername(username).orElseThrow(
                        () -> new IllegalArgumentException("유효하지 않은 유저입니다.")
                );

                // Refresh Token을 기반으로 access token과 refresh token을 재발급
                Map<String, String> tokens = jwtUtil.refresh(req, user);

                String newAccessToken = tokens.get(JwtUtil.AUTHORIZATION_HEADER);
                String newRefreshToken = tokens.get(JwtUtil.REFRESH_HEADER);

                // DB에 반영
                user.updateRefreshToken(newRefreshToken);
                userRepository.save(user);

                isValid = true;
                res.addHeader(jwtUtil.REFRESH_HEADER, newRefreshToken);
                res.addHeader(JwtUtil.AUTHORIZATION_HEADER, newAccessToken);
            }
            if (!isValid) {
                log.error("Token Error");
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return; // 필터 체인의 나머지 처리를 중단하고 응답을 반환
            }
            Claims info = jwtUtil.getUserInfoFromToken(tokenValue);

            try {
                setAuthentication(info.getSubject());
            } catch (Exception e) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태 코드를 설정
                res.getWriter().write("Authentication failed."); // 오류 메시지를 응답 본문에 작성
                return; // 필터 체인의 나머지 처리를 중단하고 응답을 반환
            }
        }

        filterChain.doFilter(req, res);
    }

    // 인증 처리
    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);

        log.info("Authenticated user: " + username);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}