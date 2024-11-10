package com.sparta.springsecurity;


import com.sparta.springsecurity.auth.domain.entity.UserRoleEnum;
import com.sparta.springsecurity.auth.infrastructure.JwtUtil;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.security.Key;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class JwtUtilTest {

    private JwtUtil jwtUtil;

    @Value("${jwt.secret.key}")
    private String secretKey;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        Key key = Keys.hmacShaKeyFor(bytes);

        jwtUtil.setKey(key);
    }

    // -- 토큰 생성 테스트
    @Test
    void testCreateAccessToken() {
        // Given
        String username = "testUser";
        UserRoleEnum role = UserRoleEnum.USER;

        // When
        String accessToken = jwtUtil.createAccessToken(username, role);

        // Then
        assertNotNull(accessToken);
        assertTrue(accessToken.startsWith("Bearer "));
    }

    @Test
    void testCreateRefreshToken() {
        // Given
        String username = "testUser";

        // When
        String refreshToken = jwtUtil.createRefreshToken(username);

        // Then
        assertNotNull(refreshToken);
        assertTrue(refreshToken.startsWith("Bearer "));
    }


    // -- 토큰 유효성 테스트
    @Test
    void testValidateToken() {
        // Given
        String username = "testUser";
        UserRoleEnum role = UserRoleEnum.USER;
        String accessToken = jwtUtil.createAccessToken(username, role);

        // When
        // prefix 제외하고 검증
        boolean isValid = jwtUtil.validateToken(accessToken.substring(7));

        // Then
        assertTrue(isValid);
    }

    @Test
    void testValidateInvalidToken() {
        // Given
        String invalidToken = "invalidToken";

        // When
        boolean isValid = jwtUtil.validateToken(invalidToken);

        // Then
        assertFalse(isValid);
    }
}
